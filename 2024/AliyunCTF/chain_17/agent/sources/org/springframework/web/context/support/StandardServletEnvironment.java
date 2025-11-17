package org.springframework.web.context.support;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.jndi.JndiPropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ConfigurableWebEnvironment;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/StandardServletEnvironment.class */
public class StandardServletEnvironment extends StandardEnvironment implements ConfigurableWebEnvironment {
    public static final String SERVLET_CONTEXT_PROPERTY_SOURCE_NAME = "servletContextInitParams";
    public static final String SERVLET_CONFIG_PROPERTY_SOURCE_NAME = "servletConfigInitParams";
    public static final String JNDI_PROPERTY_SOURCE_NAME = "jndiProperties";
    private static final boolean jndiPresent = ClassUtils.isPresent("javax.naming.InitialContext", StandardServletEnvironment.class.getClassLoader());

    public StandardServletEnvironment() {
    }

    protected StandardServletEnvironment(MutablePropertySources propertySources) {
        super(propertySources);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.core.env.StandardEnvironment, org.springframework.core.env.AbstractEnvironment
    public void customizePropertySources(MutablePropertySources propertySources) {
        propertySources.addLast(new PropertySource.StubPropertySource(SERVLET_CONFIG_PROPERTY_SOURCE_NAME));
        propertySources.addLast(new PropertySource.StubPropertySource(SERVLET_CONTEXT_PROPERTY_SOURCE_NAME));
        if (jndiPresent && JndiLocatorDelegate.isDefaultJndiEnvironmentAvailable()) {
            propertySources.addLast(new JndiPropertySource(JNDI_PROPERTY_SOURCE_NAME));
        }
        super.customizePropertySources(propertySources);
    }

    @Override // org.springframework.web.context.ConfigurableWebEnvironment
    public void initPropertySources(@Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
        WebApplicationContextUtils.initServletPropertySources(getPropertySources(), servletContext, servletConfig);
    }
}
