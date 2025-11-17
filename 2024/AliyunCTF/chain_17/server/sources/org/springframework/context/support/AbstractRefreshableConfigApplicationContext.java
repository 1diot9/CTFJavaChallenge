package org.springframework.context.support;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/AbstractRefreshableConfigApplicationContext.class */
public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext implements BeanNameAware, InitializingBean {

    @Nullable
    private String[] configLocations;
    private boolean setIdCalled;

    public AbstractRefreshableConfigApplicationContext() {
        this.setIdCalled = false;
    }

    public AbstractRefreshableConfigApplicationContext(@Nullable ApplicationContext parent) {
        super(parent);
        this.setIdCalled = false;
    }

    public void setConfigLocation(String location) {
        setConfigLocations(StringUtils.tokenizeToStringArray(location, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    public void setConfigLocations(@Nullable String... locations) {
        if (locations != null) {
            Assert.noNullElements(locations, "Config locations must not be null");
            this.configLocations = new String[locations.length];
            for (int i = 0; i < locations.length; i++) {
                this.configLocations[i] = resolvePath(locations[i]).trim();
            }
            return;
        }
        this.configLocations = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public String[] getConfigLocations() {
        return this.configLocations != null ? this.configLocations : getDefaultConfigLocations();
    }

    @Nullable
    protected String[] getDefaultConfigLocations() {
        return null;
    }

    protected String resolvePath(String path) {
        return getEnvironment().resolveRequiredPlaceholders(path);
    }

    @Override // org.springframework.context.support.AbstractApplicationContext, org.springframework.context.ConfigurableApplicationContext
    public void setId(String id) {
        super.setId(id);
        this.setIdCalled = true;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String name) {
        if (!this.setIdCalled) {
            super.setId(name);
            setDisplayName("ApplicationContext '" + name + "'");
        }
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (!isActive()) {
            refresh();
        }
    }
}
