package com.midream.sheep.swcj.core.executetool.execute.jsoup;

import com.midream.sheep.api.clazz.ClazzBuilder;
import com.midream.sheep.swcj.Exception.ConfigException;
import com.midream.sheep.swcj.core.executetool.SWCJExecute;
import com.midream.sheep.swcj.core.executetool.execute.jsoup.allocate.html.IsHtmlAllocateHandler;
import com.midream.sheep.swcj.core.executetool.execute.jsoup.pojo.Jsoup;
import com.midream.sheep.swcj.core.executetool.execute.jsoup.pojo.Pa;
import com.midream.sheep.swcj.pojo.ExecuteValue;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * @author midreamsheep
 */
public class SWCJJsoup<T> implements SWCJExecute<T> {
    @Override
    @SuppressWarnings("unchecked")
    public List<T> execute(ExecuteValue executeValue, String... args) throws Exception {
        //获取节点对象
        NodeList d = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(
                        new InputSource(new StringReader(args[0].replace("&gt;",">").trim()))
                )
                .getElementsByTagName("jsoup");
        Map<String, List<String>> map = executeCorn(
                getConnection(executeValue),
                Parse.parse(d),
                executeValue.isHtml()
        );
        return (List<T>)(
                executeValue.getClassNameReturn().equals("java.lang.String[]")?
                        map.get("string")
                        :
                        buildByMap(map,executeValue.getClassNameReturn().replace("[]","")));
    }
    private List<?> buildByMap(Map<String, List<String>> map, String className) {
        ClazzBuilder clazzBuilder = new ClazzBuilder();
        clazzBuilder.setClass(className);
        return clazzBuilder.buildByMap(map);
    }
    private Map<String,List<String>> executeCorn(Document document,Jsoup[] parse,boolean isHtml){
        Map<String,List<String>> values = new LinkedHashMap<>();
        Arrays.stream(parse).forEach(js->executeJsoup(values,js,isHtml,document));
        return values;
    }
    private void executeJsoup(Map<String,List<String>> values,Jsoup js,Boolean isHtml,Element document){
        //是否html分派器
        IsHtmlAllocateHandler isHtmlAllocateHandler = isHtml?IsHtmlAllocateHandler.IS_HTML:IsHtmlAllocateHandler.NOT_HTML;
        List<String> list = new LinkedList<>();
        values.put("".equals(js.getName()) ? "string" : js.getName(), list);
        Elements elements = null;
        for (int pointer = 0; pointer < js.getPas().length; pointer++) {
            Pa pa = js.getPas()[pointer];
            if (pointer != 0) {
                Elements childElements = new Elements();
                for (Element element : elements) {
                    childElements.addAll(executePa(pa,element.select(pa.getValue())));
                }
                elements = childElements;
            } else {
                elements = executePa(pa, document.select(pa.getValue()));
            }
            if (pointer != js.getPas().length - 1) {
                continue;
            }
            for (Element element : elements) {
                String in = js.getPas()[js.getPas().length - 1].getElement();
                if (!"".equals(in)) {
                    list.add(element.attr(in));
                    continue;
                }
                list.add(isHtmlAllocateHandler.addAContent(element));
            }
        }
    }
    private Elements executePa(Pa p,Elements select){
        Elements elements = new Elements();
        for(int i = p.getStep();i<select.size();i+=(p.getAllstep()+1)){
            Element element = select.get(i);
            for (String s : p.getNot().split(",")) {
                if(element.text().equals(s)){
                    continue;
                }
                elements.add(element);
            }
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

        switch (executeValue.getType()) {
            case GET:
                return connection.get();
            case POST:
                return connection.post();
        }
        return connection.get();
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
