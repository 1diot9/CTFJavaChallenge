package org.springframework.beans.factory.config;

import java.beans.PropertyEditor;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/ConfigurableBeanFactory.class */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {
    public static final String SCOPE_SINGLETON = "singleton";
    public static final String SCOPE_PROTOTYPE = "prototype";

    void setParentBeanFactory(BeanFactory parentBeanFactory) throws IllegalStateException;

    void setBeanClassLoader(@Nullable ClassLoader beanClassLoader);

    @Nullable
    ClassLoader getBeanClassLoader();

    void setTempClassLoader(@Nullable ClassLoader tempClassLoader);

    @Nullable
    ClassLoader getTempClassLoader();

    void setCacheBeanMetadata(boolean cacheBeanMetadata);

    boolean isCacheBeanMetadata();

    void setBeanExpressionResolver(@Nullable BeanExpressionResolver resolver);

    @Nullable
    BeanExpressionResolver getBeanExpressionResolver();

    void setConversionService(@Nullable ConversionService conversionService);

    @Nullable
    ConversionService getConversionService();

    void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);

    void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass);

    void copyRegisteredEditorsTo(PropertyEditorRegistry registry);

    void setTypeConverter(TypeConverter typeConverter);

    TypeConverter getTypeConverter();

    void addEmbeddedValueResolver(StringValueResolver valueResolver);

    boolean hasEmbeddedValueResolver();

    @Nullable
    String resolveEmbeddedValue(String value);

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    int getBeanPostProcessorCount();

    void registerScope(String scopeName, Scope scope);

    String[] getRegisteredScopeNames();

    @Nullable
    Scope getRegisteredScope(String scopeName);

    void setApplicationStartup(ApplicationStartup applicationStartup);

    ApplicationStartup getApplicationStartup();

    void copyConfigurationFrom(ConfigurableBeanFactory otherFactory);

    void registerAlias(String beanName, String alias) throws BeanDefinitionStoreException;

    void resolveAliases(StringValueResolver valueResolver);

    BeanDefinition getMergedBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException;

    void setCurrentlyInCreation(String beanName, boolean inCreation);

    boolean isCurrentlyInCreation(String beanName);

    void registerDependentBean(String beanName, String dependentBeanName);

    String[] getDependentBeans(String beanName);

    String[] getDependenciesForBean(String beanName);

    void destroyBean(String beanName, Object beanInstance);

    void destroyScopedBean(String beanName);

    void destroySingletons();
}
