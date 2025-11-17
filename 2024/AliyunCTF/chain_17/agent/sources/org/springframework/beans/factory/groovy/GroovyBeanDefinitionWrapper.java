package org.springframework.beans.factory.groovy;

import groovy.lang.GroovyObjectSupport;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/groovy/GroovyBeanDefinitionWrapper.class */
class GroovyBeanDefinitionWrapper extends GroovyObjectSupport {
    private static final String PARENT = "parent";
    private static final String AUTOWIRE = "autowire";
    private static final String SINGLETON = "singleton";

    @Nullable
    private String beanName;

    @Nullable
    private final Class<?> clazz;

    @Nullable
    private final Collection<?> constructorArgs;

    @Nullable
    private AbstractBeanDefinition definition;

    @Nullable
    private BeanWrapper definitionWrapper;

    @Nullable
    private String parentName;
    private static final String CONSTRUCTOR_ARGS = "constructorArgs";
    private static final String FACTORY_BEAN = "factoryBean";
    private static final String FACTORY_METHOD = "factoryMethod";
    private static final String INIT_METHOD = "initMethod";
    private static final String DESTROY_METHOD = "destroyMethod";
    private static final Set<String> dynamicProperties = Set.of("parent", "autowire", CONSTRUCTOR_ARGS, FACTORY_BEAN, FACTORY_METHOD, INIT_METHOD, DESTROY_METHOD, "singleton");

    /* JADX INFO: Access modifiers changed from: package-private */
    public GroovyBeanDefinitionWrapper(String beanName) {
        this(beanName, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GroovyBeanDefinitionWrapper(@Nullable String beanName, @Nullable Class<?> clazz) {
        this(beanName, clazz, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GroovyBeanDefinitionWrapper(@Nullable String beanName, Class<?> clazz, @Nullable Collection<?> constructorArgs) {
        this.beanName = beanName;
        this.clazz = clazz;
        this.constructorArgs = constructorArgs;
    }

    @Nullable
    public String getBeanName() {
        return this.beanName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBeanDefinition(AbstractBeanDefinition definition) {
        this.definition = definition;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractBeanDefinition getBeanDefinition() {
        if (this.definition == null) {
            this.definition = createBeanDefinition();
        }
        return this.definition;
    }

    protected AbstractBeanDefinition createBeanDefinition() {
        AbstractBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(this.clazz);
        if (!CollectionUtils.isEmpty(this.constructorArgs)) {
            ConstructorArgumentValues cav = new ConstructorArgumentValues();
            for (Object constructorArg : this.constructorArgs) {
                cav.addGenericArgumentValue(constructorArg);
            }
            bd.setConstructorArgumentValues(cav);
        }
        if (this.parentName != null) {
            bd.setParentName(this.parentName);
        }
        this.definitionWrapper = new BeanWrapperImpl(bd);
        return bd;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBeanDefinitionHolder(BeanDefinitionHolder holder) {
        this.definition = (AbstractBeanDefinition) holder.getBeanDefinition();
        this.beanName = holder.getBeanName();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BeanDefinitionHolder getBeanDefinitionHolder() {
        return new BeanDefinitionHolder(getBeanDefinition(), getBeanName());
    }

    void setParent(Object obj) {
        Assert.notNull(obj, "Parent bean cannot be set to a null runtime bean reference.");
        if (obj instanceof String) {
            String name = (String) obj;
            this.parentName = name;
        } else if (obj instanceof RuntimeBeanReference) {
            RuntimeBeanReference runtimeBeanReference = (RuntimeBeanReference) obj;
            this.parentName = runtimeBeanReference.getBeanName();
        } else if (obj instanceof GroovyBeanDefinitionWrapper) {
            GroovyBeanDefinitionWrapper wrapper = (GroovyBeanDefinitionWrapper) obj;
            this.parentName = wrapper.getBeanName();
        }
        getBeanDefinition().setParentName(this.parentName);
        getBeanDefinition().setAbstract(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GroovyBeanDefinitionWrapper addProperty(String propertyName, Object propertyValue) {
        if (propertyValue instanceof GroovyBeanDefinitionWrapper) {
            GroovyBeanDefinitionWrapper wrapper = (GroovyBeanDefinitionWrapper) propertyValue;
            propertyValue = wrapper.getBeanDefinition();
        }
        getBeanDefinition().getPropertyValues().add(propertyName, propertyValue);
        return this;
    }

    public Object getProperty(String property) {
        Assert.state(this.definitionWrapper != null, "BeanDefinition wrapper not initialized");
        if (this.definitionWrapper.isReadableProperty(property)) {
            return this.definitionWrapper.getPropertyValue(property);
        }
        if (dynamicProperties.contains(property)) {
            return null;
        }
        return super.getProperty(property);
    }

    public void setProperty(String property, Object newValue) {
        if ("parent".equals(property)) {
            setParent(newValue);
            return;
        }
        AbstractBeanDefinition bd = getBeanDefinition();
        Assert.state(this.definitionWrapper != null, "BeanDefinition wrapper not initialized");
        if ("autowire".equals(property)) {
            if (BeanDefinitionParserDelegate.AUTOWIRE_BY_NAME_VALUE.equals(newValue)) {
                bd.setAutowireMode(1);
                return;
            }
            if (BeanDefinitionParserDelegate.AUTOWIRE_BY_TYPE_VALUE.equals(newValue)) {
                bd.setAutowireMode(2);
                return;
            } else if (BeanDefinitionParserDelegate.AUTOWIRE_CONSTRUCTOR_VALUE.equals(newValue)) {
                bd.setAutowireMode(3);
                return;
            } else {
                if (Boolean.TRUE.equals(newValue)) {
                    bd.setAutowireMode(1);
                    return;
                }
                return;
            }
        }
        if (CONSTRUCTOR_ARGS.equals(property) && (newValue instanceof List)) {
            List<?> args = (List) newValue;
            ConstructorArgumentValues cav = new ConstructorArgumentValues();
            for (Object arg : args) {
                cav.addGenericArgumentValue(arg);
            }
            bd.setConstructorArgumentValues(cav);
            return;
        }
        if (FACTORY_BEAN.equals(property)) {
            if (newValue != null) {
                bd.setFactoryBeanName(newValue.toString());
                return;
            }
            return;
        }
        if (FACTORY_METHOD.equals(property)) {
            if (newValue != null) {
                bd.setFactoryMethodName(newValue.toString());
                return;
            }
            return;
        }
        if (INIT_METHOD.equals(property)) {
            if (newValue != null) {
                bd.setInitMethodName(newValue.toString());
            }
        } else if (DESTROY_METHOD.equals(property)) {
            if (newValue != null) {
                bd.setDestroyMethodName(newValue.toString());
            }
        } else if ("singleton".equals(property)) {
            bd.setScope(Boolean.TRUE.equals(newValue) ? "singleton" : "prototype");
        } else if (this.definitionWrapper.isWritableProperty(property)) {
            this.definitionWrapper.setPropertyValue(property, newValue);
        } else {
            super.setProperty(property, newValue);
        }
    }
}
