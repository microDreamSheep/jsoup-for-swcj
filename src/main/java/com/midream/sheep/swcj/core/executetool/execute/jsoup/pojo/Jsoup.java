package com.midream.sheep.swcj.core.executetool.execute.jsoup.pojo;

import java.util.Arrays;

/**
 * @author midreamsheep
 */
public class Jsoup {
    @Override
    public String toString() {
        return "Jsoup{" +
                "pas=" + Arrays.toString(pas) +
                ", name='" + name + '\'' +
                '}';
    }

    private Pa[] pas;
    private String name;

    public String getName() {
        return name;
    }

    public Jsoup setName(String name) {
        this.name = name;
        return this;
    }

    public Pa[] getPas() {
        return pas;
    }

    public Jsoup setPas(Pa[] pas) {
        this.pas = pas;
        return this;
    }
}
