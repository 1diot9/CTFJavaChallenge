package org.springframework.http.converter.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/Jackson2ObjectMapperFactoryBean.class */
public class Jackson2ObjectMapperFactoryBean implements FactoryBean<ObjectMapper>, BeanClassLoaderAware, ApplicationContextAware, InitializingBean {
    private final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

    @Nullable
    private ObjectMapper objectMapper;

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setCreateXmlMapper(boolean createXmlMapper) {
        this.builder.createXmlMapper(createXmlMapper);
    }

    public void setFactory(JsonFactory factory) {
        this.builder.factory(factory);
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.builder.dateFormat(dateFormat);
    }

    public void setSimpleDateFormat(String format) {
        this.builder.simpleDateFormat(format);
    }

    public void setLocale(Locale locale) {
        this.builder.locale(locale);
    }

    public void setTimeZone(TimeZone timeZone) {
        this.builder.timeZone(timeZone);
    }

    public void setAnnotationIntrospector(AnnotationIntrospector annotationIntrospector) {
        this.builder.annotationIntrospector(annotationIntrospector);
    }

    public void setPropertyNamingStrategy(PropertyNamingStrategy propertyNamingStrategy) {
        this.builder.propertyNamingStrategy(propertyNamingStrategy);
    }

    public void setDefaultTyping(TypeResolverBuilder<?> typeResolverBuilder) {
        this.builder.defaultTyping(typeResolverBuilder);
    }

    public void setSerializationInclusion(JsonInclude.Include serializationInclusion) {
        this.builder.serializationInclusion(serializationInclusion);
    }

    public void setFilters(FilterProvider filters) {
        this.builder.filters(filters);
    }

    public void setMixIns(Map<Class<?>, Class<?>> mixIns) {
        this.builder.mixIns(mixIns);
    }

    public void setSerializers(JsonSerializer<?>... serializers) {
        this.builder.serializers(serializers);
    }

    public void setSerializersByType(Map<Class<?>, JsonSerializer<?>> serializers) {
        this.builder.serializersByType(serializers);
    }

    public void setDeserializers(JsonDeserializer<?>... deserializers) {
        this.builder.deserializers(deserializers);
    }

    public void setDeserializersByType(Map<Class<?>, JsonDeserializer<?>> deserializers) {
        this.builder.deserializersByType(deserializers);
    }

    public void setAutoDetectFields(boolean autoDetectFields) {
        this.builder.autoDetectFields(autoDetectFields);
    }

    public void setAutoDetectGettersSetters(boolean autoDetectGettersSetters) {
        this.builder.autoDetectGettersSetters(autoDetectGettersSetters);
    }

    public void setDefaultViewInclusion(boolean defaultViewInclusion) {
        this.builder.defaultViewInclusion(defaultViewInclusion);
    }

    public void setFailOnUnknownProperties(boolean failOnUnknownProperties) {
        this.builder.failOnUnknownProperties(failOnUnknownProperties);
    }

    public void setFailOnEmptyBeans(boolean failOnEmptyBeans) {
        this.builder.failOnEmptyBeans(failOnEmptyBeans);
    }

    public void setIndentOutput(boolean indentOutput) {
        this.builder.indentOutput(indentOutput);
    }

    public void setDefaultUseWrapper(boolean defaultUseWrapper) {
        this.builder.defaultUseWrapper(defaultUseWrapper);
    }

    public void setFeaturesToEnable(Object... featuresToEnable) {
        this.builder.featuresToEnable(featuresToEnable);
    }

    public void setFeaturesToDisable(Object... featuresToDisable) {
        this.builder.featuresToDisable(featuresToDisable);
    }

    public void setModules(List<Module> modules) {
        this.builder.modules(modules);
    }

    @SafeVarargs
    public final void setModulesToInstall(Class<? extends Module>... modules) {
        this.builder.modulesToInstall(modules);
    }

    public void setFindModulesViaServiceLoader(boolean findModules) {
        this.builder.findModulesViaServiceLoader(findModules);
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.builder.moduleClassLoader(beanClassLoader);
    }

    public void setHandlerInstantiator(HandlerInstantiator handlerInstantiator) {
        this.builder.handlerInstantiator(handlerInstantiator);
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.builder.applicationContext(applicationContext);
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (this.objectMapper != null) {
            this.builder.configure(this.objectMapper);
        } else {
            this.objectMapper = this.builder.build();
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public ObjectMapper getObject() {
        return this.objectMapper;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        if (this.objectMapper != null) {
            return this.objectMapper.getClass();
        }
        return null;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
