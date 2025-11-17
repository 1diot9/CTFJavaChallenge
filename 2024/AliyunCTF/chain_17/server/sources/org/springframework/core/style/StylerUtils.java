package org.springframework.core.style;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/style/StylerUtils.class */
public abstract class StylerUtils {
    static final ValueStyler DEFAULT_VALUE_STYLER = new DefaultValueStyler();

    public static String style(Object value) {
        return DEFAULT_VALUE_STYLER.style(value);
    }
}
