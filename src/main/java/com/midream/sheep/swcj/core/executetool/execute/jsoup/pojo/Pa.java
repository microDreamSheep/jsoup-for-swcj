package com.midream.sheep.swcj.core.executetool.execute.jsoup.pojo;

/**
 * @author midreamsheep
 */
public class Pa {
    private String not;
    private int allstep;
    private int step;
    private String element;
    private String value;

    public String getNot() {
        return not;
    }

    public Pa setNot(String not) {
        this.not = not;
        return this;
    }

    public int getAllstep() {
        return allstep;
    }

    public Pa setAllstep(int allstep) {
        this.allstep = allstep;
        return this;
    }

    public int getStep() {
        return step;
    }

    public Pa setStep(int step) {
        this.step = step;
        return this;
    }

    public String getElement() {
        return element;
    }

    public Pa setElement(String element) {
        this.element = element;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Pa setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return "Pa{" +
                "not='" + not + '\'' +
                ", allstep=" + allstep +
                ", step=" + step +
                ", element='" + element + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
