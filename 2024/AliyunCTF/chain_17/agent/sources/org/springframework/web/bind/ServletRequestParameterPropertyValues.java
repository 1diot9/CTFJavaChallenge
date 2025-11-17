package org.springframework.web.bind;

import jakarta.servlet.ServletRequest;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.web.util.WebUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/ServletRequestParameterPropertyValues.class */
public class ServletRequestParameterPropertyValues extends MutablePropertyValues {
    public static final String DEFAULT_PREFIX_SEPARATOR = "_";

    public ServletRequestParameterPropertyValues(ServletRequest request) {
        this(request, null, null);
    }

    public ServletRequestParameterPropertyValues(ServletRequest request, @Nullable String prefix) {
        this(request, prefix, "_");
    }

    public ServletRequestParameterPropertyValues(ServletRequest request, @Nullable String prefix, @Nullable String prefixSeparator) {
        super(WebUtils.getParametersStartingWith(request, prefix != null ? prefix + prefixSeparator : null));
    }
}
