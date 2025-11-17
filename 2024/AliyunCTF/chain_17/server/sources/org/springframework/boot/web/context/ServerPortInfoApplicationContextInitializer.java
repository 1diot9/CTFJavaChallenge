package org.springframework.boot.web.context;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/context/ServerPortInfoApplicationContextInitializer.class */
public class ServerPortInfoApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, ApplicationListener<WebServerInitializedEvent> {
    private static final String PROPERTY_SOURCE_NAME = "server.ports";

    @Override // org.springframework.context.ApplicationContextInitializer
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.addApplicationListener(this);
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(WebServerInitializedEvent event) {
        String propertyName = "local." + getName(event.getApplicationContext()) + ".port";
        setPortProperty(event.getApplicationContext(), propertyName, event.getWebServer().getPort());
    }

    private String getName(WebServerApplicationContext context) {
        String name = context.getServerNamespace();
        return StringUtils.hasText(name) ? name : "server";
    }

    private void setPortProperty(ApplicationContext context, String propertyName, int port) {
        if (context instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) context;
            setPortProperty(configurableContext.getEnvironment(), propertyName, port);
        }
        if (context.getParent() != null) {
            setPortProperty(context.getParent(), propertyName, port);
        }
    }

    private void setPortProperty(ConfigurableEnvironment environment, String propertyName, int port) {
        MutablePropertySources sources = environment.getPropertySources();
        PropertySource<?> source = sources.get(PROPERTY_SOURCE_NAME);
        if (source == null) {
            source = new MapPropertySource(PROPERTY_SOURCE_NAME, new HashMap());
            sources.addFirst(source);
        }
        ((Map) source.getSource()).put(propertyName, Integer.valueOf(port));
    }
}
