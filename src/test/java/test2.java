

import com.midream.sheep.api.clazz.ClazzBuilder;
import com.midream.sheep.api.clazz.filed.fileds.StringHandler;
import test.image;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class test2 {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, IOException {
        ClazzBuilder clazzBuilder = new ClazzBuilder();
        clazzBuilder.setClass(image.class);
        clazzBuilder.addFiled("name",new StringHandler("01"));
        clazzBuilder.addFiled("name",new StringHandler("02"));
        clazzBuilder.addFiled("name",new StringHandler("03"));
        clazzBuilder.addFiled("url",new StringHandler("http://www.baidu.com01"));
        clazzBuilder.addFiled("url",new StringHandler("http://www.baidu.com02"));
        clazzBuilder.addFiled("url",new StringHandler("http://www.baidu.com03"));
        List<Object> objects = clazzBuilder.buildObjects();
        objects.forEach(System.out::println);
    }
}
