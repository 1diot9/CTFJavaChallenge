package org.springframework.context.support;

import java.util.Set;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/ConversionServiceFactoryBean.class */
public class ConversionServiceFactoryBean implements FactoryBean<ConversionService>, InitializingBean {

    @Nullable
    private Set<?> converters;

    @Nullable
    private GenericConversionService conversionService;

    public void setConverters(Set<?> converters) {
        this.converters = converters;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        this.conversionService = createConversionService();
        ConversionServiceFactory.registerConverters(this.converters, this.conversionService);
    }

    protected GenericConversionService createConversionService() {
        return new DefaultConversionService();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public ConversionService getObject() {
        return this.conversionService;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<? extends ConversionService> getObjectType() {
        return GenericConversionService.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
