package org.springframework.jndi.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ResolvableType;
import org.springframework.jndi.JndiLocatorSupport;
import org.springframework.jndi.TypeMismatchNamingException;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jndi/support/SimpleJndiBeanFactory.class */
public class SimpleJndiBeanFactory extends JndiLocatorSupport implements BeanFactory {
    private final Set<String> shareableResources = new HashSet();
    private final Map<String, Object> singletonObjects = new HashMap();
    private final Map<String, Class<?>> resourceTypes = new HashMap();

    public SimpleJndiBeanFactory() {
        setResourceRef(true);
    }

    public void addShareableResource(String shareableResource) {
        this.shareableResources.add(shareableResource);
    }

    public void setShareableResources(String... shareableResources) {
        Collections.addAll(this.shareableResources, shareableResources);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Object getBean(String name) throws BeansException {
        return getBean(name, Object.class);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(String str, Class<T> cls) throws BeansException {
        try {
            if (isSingleton(str)) {
                return (T) doGetSingleton(str, cls);
            }
            return (T) lookup(str, cls);
        } catch (NameNotFoundException e) {
            throw new NoSuchBeanDefinitionException(str, "not found in JNDI environment");
        } catch (TypeMismatchNamingException e2) {
            throw new BeanNotOfRequiredTypeException(str, e2.getRequiredType(), e2.getActualType());
        } catch (NamingException e3) {
            throw new BeanDefinitionStoreException("JNDI environment", str, "JNDI lookup failed", e3);
        }
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Object getBean(String name, @Nullable Object... args) throws BeansException {
        if (args != null) {
            throw new UnsupportedOperationException("SimpleJndiBeanFactory does not support explicit bean creation arguments");
        }
        return getBean(name);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(Class<T> cls) throws BeansException {
        return (T) getBean(cls.getSimpleName(), cls);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(Class<T> cls, @Nullable Object... objArr) throws BeansException {
        if (objArr != null) {
            throw new UnsupportedOperationException("SimpleJndiBeanFactory does not support explicit bean creation arguments");
        }
        return (T) getBean(cls);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> ObjectProvider<T> getBeanProvider(final Class<T> requiredType) {
        return new ObjectProvider<T>() { // from class: org.springframework.jndi.support.SimpleJndiBeanFactory.1
            @Override // org.springframework.beans.factory.ObjectFactory
            public T getObject() throws BeansException {
                return (T) SimpleJndiBeanFactory.this.getBean(requiredType);
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public T getObject(Object... objArr) throws BeansException {
                return (T) SimpleJndiBeanFactory.this.getBean(requiredType, objArr);
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            @Nullable
            public T getIfAvailable() throws BeansException {
                try {
                    return (T) SimpleJndiBeanFactory.this.getBean(requiredType);
                } catch (NoUniqueBeanDefinitionException e) {
                    throw e;
                } catch (NoSuchBeanDefinitionException e2) {
                    return null;
                }
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            @Nullable
            public T getIfUnique() throws BeansException {
                try {
                    return (T) SimpleJndiBeanFactory.this.getBean(requiredType);
                } catch (NoSuchBeanDefinitionException e) {
                    return null;
                }
            }
        };
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        throw new UnsupportedOperationException("SimpleJndiBeanFactory does not support resolution by ResolvableType");
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean containsBean(String name) {
        if (this.singletonObjects.containsKey(name) || this.resourceTypes.containsKey(name)) {
            return true;
        }
        try {
            doGetType(name);
            return true;
        } catch (NamingException e) {
            return false;
        }
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return this.shareableResources.contains(name);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return !this.shareableResources.contains(name);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        Class<?> type = getType(name);
        return type != null && typeToMatch.isAssignableFrom(type);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isTypeMatch(String name, @Nullable Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        Class<?> type = getType(name);
        return typeToMatch == null || (type != null && typeToMatch.isAssignableFrom(type));
    }

    @Override // org.springframework.beans.factory.BeanFactory
    @Nullable
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return getType(name, true);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    @Nullable
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        try {
            return doGetType(name);
        } catch (NamingException e) {
            return null;
        } catch (NameNotFoundException e2) {
            throw new NoSuchBeanDefinitionException(name, "not found in JNDI environment");
        }
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public String[] getAliases(String name) {
        return new String[0];
    }

    private <T> T doGetSingleton(String str, @Nullable Class<T> cls) throws NamingException {
        synchronized (this.singletonObjects) {
            T t = (T) this.singletonObjects.get(str);
            if (t != null) {
                if (cls != null && !cls.isInstance(t)) {
                    throw new TypeMismatchNamingException(convertJndiName(str), cls, t.getClass());
                }
                return t;
            }
            T t2 = (T) lookup(str, cls);
            this.singletonObjects.put(str, t2);
            return t2;
        }
    }

    private Class<?> doGetType(String name) throws NamingException {
        Class<?> cls;
        if (isSingleton(name)) {
            return doGetSingleton(name, null).getClass();
        }
        synchronized (this.resourceTypes) {
            Class<?> type = this.resourceTypes.get(name);
            if (type == null) {
                type = lookup(name, null).getClass();
                this.resourceTypes.put(name, type);
            }
            cls = type;
        }
        return cls;
    }
}
