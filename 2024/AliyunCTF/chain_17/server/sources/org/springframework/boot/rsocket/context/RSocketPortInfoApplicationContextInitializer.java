package org.springframework.boot.rsocket.context;

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

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/rsocket/context/RSocketPortInfoApplicationContextInitializer.class */
public class RSocketPortInfoApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override // org.springframework.context.ApplicationContextInitializer
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.addApplicationListener(new Listener(applicationContext));
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/rsocket/context/RSocketPortInfoApplicationContextInitializer$Listener.class */
    private static class Listener implements ApplicationListener<RSocketServerInitializedEvent> {
        private static final String PROPERTY_NAME = "local.rsocket.server.port";
        private static final String PROPERTY_SOURCE_NAME = "server.ports";
        private final ConfigurableApplicationContext applicationContext;

        Listener(ConfigurableApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override // org.springframework.context.ApplicationListener
        public void onApplicationEvent(RSocketServerInitializedEvent event) {
            if (event.getServer().address() != null) {
                setPortProperty(this.applicationContext, event.getServer().address().getPort());
            }
        }

        private void setPortProperty(ApplicationContext context, int port) {
            if (context instanceof ConfigurableApplicationContext) {
                ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) context;
                setPortProperty(configurableContext.getEnvironment(), port);
            }
            if (context.getParent() != null) {
                setPortProperty(context.getParent(), port);
            }
        }

        private void setPortProperty(ConfigurableEnvironment environment, int port) {
            MutablePropertySources sources = environment.getPropertySources();
            PropertySource<?> source = sources.get(PROPERTY_SOURCE_NAME);
            if (source == null) {
                source = new MapPropertySource(PROPERTY_SOURCE_NAME, new HashMap());
                sources.addFirst(source);
            }
            setPortProperty(port, source);
        }

        private void setPortProperty(int port, PropertySource<?> source) {
            ((Map) source.getSource()).put(PROPERTY_NAME, Integer.valueOf(port));
        }
    }
}
