
import com.midream.sheep.swcj.Exception.ConfigException;
import com.midream.sheep.swcj.Exception.EmptyMatchMethodException;
import com.midream.sheep.swcj.Exception.InterfaceIllegal;
import com.midream.sheep.swcj.core.factory.SWCJXmlFactory;
import com.midream.sheep.swcj.core.factory.xmlfactory.CoreXmlFactory;
import org.xml.sax.SAXException;
import test.image;
import test.pojo;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author midreamsheep
 */
public class Test {
    public static void main(String[] args) throws ConfigException, IOException, ParserConfigurationException, SAXException, EmptyMatchMethodException, InterfaceIllegal {
        SWCJXmlFactory swcjXmlFactory = new CoreXmlFactory();
        swcjXmlFactory.parse(new File(Objects.requireNonNull(Test.class.getClassLoader().getResource("")).getPath() + "/test.xml"));
        long start1 = System.currentTimeMillis();
        pojo html = (pojo) swcjXmlFactory.getWebSpiderById("downloader");
        long end1 = System.currentTimeMillis();
        System.out.println("编译用时"+(end1 - start1));
        image[] it = html.gethtml("9");
        for (image s : it) {
            System.out.println(s);
        }
        System.out.println(it.length);
        swcjXmlFactory.invokeSpecialMethod();
    }
}
