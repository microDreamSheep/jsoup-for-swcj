package com.midream.sheep.swcj.core.executetool.execute.jsoup;

import com.midream.sheep.swcj.Exception.ConfigException;
import com.midream.sheep.swcj.core.executetool.SWCJExecute;
import com.midream.sheep.swcj.core.executetool.execute.jsoup.pojo.Jsoup;
import com.midream.sheep.swcj.core.executetool.execute.jsoup.pojo.Pa;
import com.midream.sheep.swcj.pojo.ExecuteValue;
import com.midream.sheep.swcj.util.function.StringUtil;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author midreamsheep
 */
public class SWCJJsoup<T> implements SWCJExecute<T> {
    @Override
    public List<T> execute(ExecuteValue executeValue, String... args) throws Exception {
        //获取节点对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        NodeList d = builder.parse(new InputSource(new StringReader(args[0].replace("&gt;",">").trim()))).getElementsByTagName("jsoup");
        Jsoup[] parse = Parse.parse(d);
        Document document = getConnection(executeValue);
        Map<String, List<String>> map = executeCorn(document, parse, executeValue.isHtml());
        if(executeValue.getClassNameReturn().equals("java.lang.String[]")){
            @SuppressWarnings("unchecked")
            List<T> list = (List<T>) map.get("string");
            return list;
        }
        Class<?> aClass = Class.forName(executeValue.getClassNameReturn().replace("[]",""));
        List<Integer> list = new LinkedList<>();
        Arrays.stream(parse).forEach(jsoup->list.add(map.get(jsoup.getName()).size()));

        int max = Collections.min(Arrays.asList(list.toArray(new Integer[]{})));

        List<T> listw = new LinkedList<>();
        for (int i = 0;i< max;i++) {
            Object o = aClass.getDeclaredConstructor().newInstance();
            for (Jsoup jsoup : parse) {
                String name = jsoup.getName();
                Method repay1 = aClass.getMethod("set" + StringUtil.StringToUpperCase(name), String.class);
                List<String> list1 = map.get(name);
                repay1.invoke(o, list1.size() > i ? map.get(name).get(i) : "");
            }
            @SuppressWarnings("unchecked")
            T t = (T) o;
            listw.add(t);
        }
        return listw;
    }
    private Map<String,List<String>> executeCorn(Document document,Jsoup[] parse,boolean isHtml){
        Map<String,List<String>> values = new LinkedHashMap<>();
        Arrays.stream(parse).forEach(js->executeJsoup(values,js,isHtml,document));
        return values;
    }
    private void executeJsoup(Map<String,List<String>> values,Jsoup js,Boolean isHtml,Element document){
        List<String> list = new LinkedList<>();
        values.put("".equals(js.getName()) ? "string" : js.getName(), list);
        Elements elements = null;
        for (int a = 0; a < js.getPas().length; a++) {
            Pa pa = js.getPas()[a];
            if (a != 0) {
                Elements elements1 = new Elements();
                elements.forEach(element ->elements1.addAll(executePa(pa,element.select(pa.getValue()))));
                elements = elements1;
            } else {
                elements = executePa(pa, document.select(pa.getValue()));
            }
            if (a != js.getPas().length - 1) {
                continue;
            }
            for (Element element : elements) {
                String in = js.getPas()[js.getPas().length - 1].getElement();
                if (!"".equals(in)) {
                    list.add(element.attr(in));
                    continue;
                }
                if (isHtml) {
                    list.add(element.html());
                } else {
                    list.add(element.text());
                }
            }
        }
    }
    private Elements executePa(Pa p,Elements select){
        Elements elements = new Elements();
        for(int i = p.getStep();i<select.size();i+=(p.getAllstep()+1)){
            Element element = select.get(i);
            Arrays.stream(p.getNot().split(",")).filter(s->!element.text().equals(s)).forEach(e-> elements.add(element));
        }
        return elements;
    }
    /**
     * @param executeValue necessary data
     * */
    private Document getConnection(ExecuteValue executeValue) throws IOException {
        Connection connection = org.jsoup.Jsoup.connect(executeValue.getUrl()).userAgent(executeValue.getUserAge()).
                cookies(executeValue.getValues()).ignoreContentType(true).data(executeValue.getValues()).
                timeout(Integer.parseInt(executeValue.getTimeout()));
        return switch (executeValue.getType()) {
            case GET -> connection.get();
            case POST -> connection.post();
        };
    }
    /**
     * Static inner classes parse XML
     * */
    public static class Parse {
        public static Jsoup[] parse(NodeList jsoup) throws ConfigException {
            //判断第一个子节点是否是jsoup
            String s = jsoup.item(0).getNodeName();
            if(s.equals("#text")){
                s = jsoup.item(1).getNodeName();
            }
            Jsoup[] jsoups;
            if("jsoup".equals(s)) {
                jsoups = parseJsoup(jsoup);
            }else if("pa".equals(s)){
                jsoups = new Jsoup[]{new Jsoup().setPas(parsePa(jsoup))};
            }else {
                throw new ConfigException("你的配置文件有问题");
            }
            return jsoups;
        }
        private static Jsoup[] parseJsoup(NodeList jsoup){
            List<Jsoup> list = new LinkedList<>();
            for (int i = 0; i < jsoup.getLength(); i++) {
                Node item = jsoup.item(i);
                if(item.getNodeName().equals("#text")){
                    continue;
                }
                list.add(new Jsoup().setName(item.getAttributes().getNamedItem("name").getNodeValue()).setPas(parsePa(item.getChildNodes())));
            }
            return list.toArray(new Jsoup[]{});
        }
        private static Pa[] parsePa(NodeList pa){
            List<Pa> list = new LinkedList<>();
            for (int i = 1; i < pa.getLength(); i+=2) {
                Node item = pa.item(i);
                NamedNodeMap nodeMap = item.getAttributes();
                list.add(new Pa()
                        .setAllstep(Integer.parseInt(nodeMap.getNamedItem("allStep").getNodeValue()))
                        .setNot(nodeMap.getNamedItem("not").getNodeValue())
                        .setStep(Integer.parseInt(nodeMap.getNamedItem("step").getNodeValue()))
                        .setElement(nodeMap.getNamedItem("element").getNodeValue())
                        .setValue(item.getTextContent().trim())
                );
            }
            return list.toArray(new Pa[]{});
        }
    }
}
