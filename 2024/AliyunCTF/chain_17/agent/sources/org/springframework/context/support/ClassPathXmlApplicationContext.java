package org.springframework.context.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/ClassPathXmlApplicationContext.class */
public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {

    @Nullable
    private Resource[] configResources;

    public ClassPathXmlApplicationContext() {
    }

    public ClassPathXmlApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
        this(new String[]{configLocation}, true, (ApplicationContext) null);
    }

    public ClassPathXmlApplicationContext(String... configLocations) throws BeansException {
        this(configLocations, true, (ApplicationContext) null);
    }

    public ClassPathXmlApplicationContext(String[] configLocations, @Nullable ApplicationContext parent) throws BeansException {
        this(configLocations, true, parent);
    }

    public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
        this(configLocations, refresh, (ApplicationContext) null);
    }

    public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh, @Nullable ApplicationContext parent) throws BeansException {
        super(parent);
        setConfigLocations(configLocations);
        if (refresh) {
            refresh();
        }
    }

    public ClassPathXmlApplicationContext(String path, Class<?> clazz) throws BeansException {
        this(new String[]{path}, clazz);
    }

    public ClassPathXmlApplicationContext(String[] paths, Class<?> clazz) throws BeansException {
        this(paths, clazz, (ApplicationContext) null);
    }

    public ClassPathXmlApplicationContext(String[] paths, Class<?> clazz, @Nullable ApplicationContext parent) throws BeansException {
        super(parent);
        Assert.notNull(paths, "Path array must not be null");
        Assert.notNull(clazz, "Class argument must not be null");
        this.configResources = new Resource[paths.length];
        for (int i = 0; i < paths.length; i++) {
            this.configResources[i] = new ClassPathResource(paths[i], clazz);
        }
        refresh();
    }

    @Override // org.springframework.context.support.AbstractXmlApplicationContext
    @Nullable
    protected Resource[] getConfigResources() {
        return this.configResources;
    }
}
