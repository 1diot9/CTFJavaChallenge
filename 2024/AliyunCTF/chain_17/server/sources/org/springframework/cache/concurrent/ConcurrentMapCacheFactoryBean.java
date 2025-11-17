package org.springframework.cache.concurrent;

import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/concurrent/ConcurrentMapCacheFactoryBean.class */
public class ConcurrentMapCacheFactoryBean implements FactoryBean<ConcurrentMapCache>, BeanNameAware, InitializingBean {

    @Nullable
    private ConcurrentMap<Object, Object> store;

    @Nullable
    private ConcurrentMapCache cache;
    private String name = "";
    private boolean allowNullValues = true;

    public void setName(String name) {
        this.name = name;
    }

    public void setStore(ConcurrentMap<Object, Object> store) {
        this.store = store;
    }

    public void setAllowNullValues(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String beanName) {
        if (!StringUtils.hasLength(this.name)) {
            setName(beanName);
        }
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        this.cache = this.store != null ? new ConcurrentMapCache(this.name, this.store, this.allowNullValues) : new ConcurrentMapCache(this.name, this.allowNullValues);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public ConcurrentMapCache getObject() {
        return this.cache;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return ConcurrentMapCache.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
