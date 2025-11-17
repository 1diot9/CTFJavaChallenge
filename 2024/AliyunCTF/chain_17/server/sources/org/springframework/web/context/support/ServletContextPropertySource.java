package org.springframework.web.context.support;

import jakarta.servlet.ServletContext;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/ServletContextPropertySource.class */
public class ServletContextPropertySource extends EnumerablePropertySource<ServletContext> {
    public ServletContextPropertySource(String name, ServletContext servletContext) {
        super(name, servletContext);
    }

    @Override // org.springframework.core.env.EnumerablePropertySource
    public String[] getPropertyNames() {
        return StringUtils.toStringArray(((ServletContext) this.source).getInitParameterNames());
    }

    @Override // org.springframework.core.env.PropertySource
    @Nullable
    public String getProperty(String name) {
        return ((ServletContext) this.source).getInitParameter(name);
    }
}
