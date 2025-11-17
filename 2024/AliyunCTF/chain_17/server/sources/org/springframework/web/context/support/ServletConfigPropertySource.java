package org.springframework.web.context.support;

import jakarta.servlet.ServletConfig;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/ServletConfigPropertySource.class */
public class ServletConfigPropertySource extends EnumerablePropertySource<ServletConfig> {
    public ServletConfigPropertySource(String name, ServletConfig servletConfig) {
        super(name, servletConfig);
    }

    @Override // org.springframework.core.env.EnumerablePropertySource
    public String[] getPropertyNames() {
        return StringUtils.toStringArray(((ServletConfig) this.source).getInitParameterNames());
    }

    @Override // org.springframework.core.env.PropertySource
    @Nullable
    public String getProperty(String name) {
        return ((ServletConfig) this.source).getInitParameter(name);
    }
}
