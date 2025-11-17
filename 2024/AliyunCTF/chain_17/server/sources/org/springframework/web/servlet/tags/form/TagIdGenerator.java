package org.springframework.web.servlet.tags.form;

import jakarta.servlet.jsp.PageContext;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/form/TagIdGenerator.class */
abstract class TagIdGenerator {
    private static final String PAGE_CONTEXT_ATTRIBUTE_PREFIX = TagIdGenerator.class.getName() + ".";

    TagIdGenerator() {
    }

    public static String nextId(String name, PageContext pageContext) {
        String attributeName = PAGE_CONTEXT_ATTRIBUTE_PREFIX + name;
        Integer currentCount = (Integer) pageContext.getAttribute(attributeName);
        Integer currentCount2 = Integer.valueOf(currentCount != null ? currentCount.intValue() + 1 : 1);
        pageContext.setAttribute(attributeName, currentCount2);
        return name + currentCount2;
    }
}
