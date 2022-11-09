package com.midream.sheep.swcj.core.executetool.execute.jsoup.allocate.html;

import org.jsoup.nodes.Element;

public enum IsHtmlAllocateHandler {

    IS_HTML{
        @Override
        public String addAContent(Element content) {
            return content.html();
        }
    },
    NOT_HTML{
        @Override
        public String addAContent(Element content) {
            return content.text();
        }
    };

    public abstract String addAContent(Element content);
}
