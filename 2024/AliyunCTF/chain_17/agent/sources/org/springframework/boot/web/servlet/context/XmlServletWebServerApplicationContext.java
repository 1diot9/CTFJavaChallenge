package org.springframework.boot.web.servlet.context;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/context/XmlServletWebServerApplicationContext.class */
public class XmlServletWebServerApplicationContext extends ServletWebServerApplicationContext {
    private final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);

    public XmlServletWebServerApplicationContext() {
        this.reader.setEnvironment(getEnvironment());
    }

    public XmlServletWebServerApplicationContext(Resource... resources) {
        load(resources);
        refresh();
    }

    public XmlServletWebServerApplicationContext(String... resourceLocations) {
        load(resourceLocations);
        refresh();
    }

    public XmlServletWebServerApplicationContext(Class<?> relativeClass, String... resourceNames) {
        load(relativeClass, resourceNames);
        refresh();
    }

    public void setValidating(boolean validating) {
        this.reader.setValidating(validating);
    }

    @Override // org.springframework.context.support.AbstractApplicationContext, org.springframework.context.ConfigurableApplicationContext
    public void setEnvironment(ConfigurableEnvironment environment) {
        super.setEnvironment(environment);
        this.reader.setEnvironment(getEnvironment());
    }

    public final void load(Resource... resources) {
        this.reader.loadBeanDefinitions(resources);
    }

    public final void load(String... resourceLocations) {
        this.reader.loadBeanDefinitions(resourceLocations);
    }

    public final void load(Class<?> relativeClass, String... resourceNames) {
        Resource[] resources = new Resource[resourceNames.length];
        for (int i = 0; i < resourceNames.length; i++) {
            resources[i] = new ClassPathResource(resourceNames[i], relativeClass);
        }
        this.reader.loadBeanDefinitions(resources);
    }
}
