package org.springframework.context.support;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/GenericXmlApplicationContext.class */
public class GenericXmlApplicationContext extends GenericApplicationContext {
    private final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);

    public GenericXmlApplicationContext() {
    }

    public GenericXmlApplicationContext(Resource... resources) {
        load(resources);
        refresh();
    }

    public GenericXmlApplicationContext(String... resourceLocations) {
        load(resourceLocations);
        refresh();
    }

    public GenericXmlApplicationContext(Class<?> relativeClass, String... resourceNames) {
        load(relativeClass, resourceNames);
        refresh();
    }

    public final XmlBeanDefinitionReader getReader() {
        return this.reader;
    }

    public void setValidating(boolean validating) {
        this.reader.setValidating(validating);
    }

    @Override // org.springframework.context.support.AbstractApplicationContext, org.springframework.context.ConfigurableApplicationContext
    public void setEnvironment(ConfigurableEnvironment environment) {
        super.setEnvironment(environment);
        this.reader.setEnvironment(getEnvironment());
    }

    public void load(Resource... resources) {
        this.reader.loadBeanDefinitions(resources);
    }

    public void load(String... resourceLocations) {
        this.reader.loadBeanDefinitions(resourceLocations);
    }

    public void load(Class<?> relativeClass, String... resourceNames) {
        Resource[] resources = new Resource[resourceNames.length];
        for (int i = 0; i < resourceNames.length; i++) {
            resources[i] = new ClassPathResource(resourceNames[i], relativeClass);
        }
        load(resources);
    }
}
