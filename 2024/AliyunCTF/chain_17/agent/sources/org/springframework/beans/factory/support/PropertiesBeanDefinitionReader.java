package org.springframework.beans.factory.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.StringUtils;

@Deprecated
/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/PropertiesBeanDefinitionReader.class */
public class PropertiesBeanDefinitionReader extends AbstractBeanDefinitionReader {
    public static final String TRUE_VALUE = "true";
    public static final String SEPARATOR = ".";
    public static final String CLASS_KEY = "(class)";
    public static final String PARENT_KEY = "(parent)";
    public static final String SCOPE_KEY = "(scope)";
    public static final String SINGLETON_KEY = "(singleton)";
    public static final String ABSTRACT_KEY = "(abstract)";
    public static final String LAZY_INIT_KEY = "(lazy-init)";
    public static final String REF_SUFFIX = "(ref)";
    public static final String REF_PREFIX = "*";
    public static final String CONSTRUCTOR_ARG_PREFIX = "$";

    @Nullable
    private String defaultParentBean;
    private PropertiesPersister propertiesPersister;

    public PropertiesBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
        this.propertiesPersister = DefaultPropertiesPersister.INSTANCE;
    }

    public void setDefaultParentBean(@Nullable String defaultParentBean) {
        this.defaultParentBean = defaultParentBean;
    }

    @Nullable
    public String getDefaultParentBean() {
        return this.defaultParentBean;
    }

    public void setPropertiesPersister(@Nullable PropertiesPersister propertiesPersister) {
        this.propertiesPersister = propertiesPersister != null ? propertiesPersister : DefaultPropertiesPersister.INSTANCE;
    }

    public PropertiesPersister getPropertiesPersister() {
        return this.propertiesPersister;
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionReader
    public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
        return loadBeanDefinitions(new EncodedResource(resource), (String) null);
    }

    public int loadBeanDefinitions(Resource resource, @Nullable String prefix) throws BeanDefinitionStoreException {
        return loadBeanDefinitions(new EncodedResource(resource), prefix);
    }

    public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
        return loadBeanDefinitions(encodedResource, (String) null);
    }

    public int loadBeanDefinitions(EncodedResource encodedResource, @Nullable String prefix) throws BeanDefinitionStoreException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Loading properties bean definitions from " + encodedResource);
        }
        Properties props = new Properties();
        try {
            InputStream is = encodedResource.getResource().getInputStream();
            try {
                if (encodedResource.getEncoding() != null) {
                    getPropertiesPersister().load(props, new InputStreamReader(is, encodedResource.getEncoding()));
                } else {
                    getPropertiesPersister().load(props, is);
                }
                if (is != null) {
                    is.close();
                }
                int count = registerBeanDefinitions(props, prefix, encodedResource.getResource().getDescription());
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Loaded " + count + " bean definitions from " + encodedResource);
                }
                return count;
            } finally {
            }
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException("Could not parse properties from " + encodedResource.getResource(), ex);
        }
    }

    public int registerBeanDefinitions(ResourceBundle rb) throws BeanDefinitionStoreException {
        return registerBeanDefinitions(rb, (String) null);
    }

    public int registerBeanDefinitions(ResourceBundle rb, @Nullable String prefix) throws BeanDefinitionStoreException {
        Map<String, Object> map = new HashMap<>();
        Enumeration<String> keys = rb.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            map.put(key, rb.getObject(key));
        }
        return registerBeanDefinitions(map, prefix);
    }

    public int registerBeanDefinitions(Map<?, ?> map) throws BeansException {
        return registerBeanDefinitions(map, (String) null);
    }

    public int registerBeanDefinitions(Map<?, ?> map, @Nullable String prefix) throws BeansException {
        return registerBeanDefinitions(map, prefix, "Map " + map);
    }

    public int registerBeanDefinitions(Map<?, ?> map, @Nullable String prefix, String resourceDescription) throws BeansException {
        int sepIdx;
        if (prefix == null) {
            prefix = "";
        }
        int beanCount = 0;
        for (Object key : map.keySet()) {
            if (!(key instanceof String)) {
                throw new IllegalArgumentException("Illegal key [" + key + "]: only Strings allowed");
            }
            String keyString = (String) key;
            if (keyString.startsWith(prefix)) {
                String nameAndProperty = keyString.substring(prefix.length());
                int propKeyIdx = nameAndProperty.indexOf("[");
                if (propKeyIdx != -1) {
                    sepIdx = nameAndProperty.lastIndexOf(".", propKeyIdx);
                } else {
                    sepIdx = nameAndProperty.lastIndexOf(".");
                }
                if (sepIdx != -1) {
                    String beanName = nameAndProperty.substring(0, sepIdx);
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("Found bean name '" + beanName + "'");
                    }
                    if (!getRegistry().containsBeanDefinition(beanName)) {
                        registerBeanDefinition(beanName, map, prefix + beanName, resourceDescription);
                        beanCount++;
                    }
                } else if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Invalid bean name and property [" + nameAndProperty + "]");
                }
            }
        }
        return beanCount;
    }

    protected void registerBeanDefinition(String beanName, Map<?, ?> map, String prefix, String resourceDescription) throws BeansException {
        String className = null;
        String parent = null;
        String scope = "singleton";
        boolean isAbstract = false;
        boolean lazyInit = false;
        ConstructorArgumentValues cas = new ConstructorArgumentValues();
        MutablePropertyValues pvs = new MutablePropertyValues();
        String prefixWithSep = prefix + ".";
        int beginIndex = prefixWithSep.length();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = ((String) entry.getKey()).strip();
            if (key.startsWith(prefixWithSep)) {
                String property = key.substring(beginIndex);
                if (CLASS_KEY.equals(property)) {
                    className = ((String) entry.getValue()).strip();
                } else if (PARENT_KEY.equals(property)) {
                    parent = ((String) entry.getValue()).strip();
                } else if (ABSTRACT_KEY.equals(property)) {
                    isAbstract = "true".equals(((String) entry.getValue()).strip());
                } else if (SCOPE_KEY.equals(property)) {
                    scope = ((String) entry.getValue()).strip();
                } else if (SINGLETON_KEY.equals(property)) {
                    String val = ((String) entry.getValue()).strip();
                    scope = (!StringUtils.hasLength(val) || "true".equals(val)) ? "singleton" : "prototype";
                } else if (LAZY_INIT_KEY.equals(property)) {
                    lazyInit = "true".equals(((String) entry.getValue()).strip());
                } else if (property.startsWith(CONSTRUCTOR_ARG_PREFIX)) {
                    if (property.endsWith(REF_SUFFIX)) {
                        int index = Integer.parseInt(property, 1, property.length() - REF_SUFFIX.length(), 10);
                        cas.addIndexedArgumentValue(index, new RuntimeBeanReference(entry.getValue().toString()));
                    } else {
                        int index2 = Integer.parseInt(property, 1, property.length(), 10);
                        cas.addIndexedArgumentValue(index2, readValue(entry));
                    }
                } else if (property.endsWith(REF_SUFFIX)) {
                    String property2 = property.substring(0, property.length() - REF_SUFFIX.length());
                    String ref = ((String) entry.getValue()).strip();
                    pvs.add(property2, new RuntimeBeanReference(ref));
                } else {
                    pvs.add(property, readValue(entry));
                }
            }
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Registering bean definition for bean name '" + beanName + "' with " + pvs);
        }
        if (parent == null && className == null && !beanName.equals(this.defaultParentBean)) {
            parent = this.defaultParentBean;
        }
        try {
            AbstractBeanDefinition bd = BeanDefinitionReaderUtils.createBeanDefinition(parent, className, getBeanClassLoader());
            bd.setScope(scope);
            bd.setAbstract(isAbstract);
            bd.setLazyInit(lazyInit);
            bd.setConstructorArgumentValues(cas);
            bd.setPropertyValues(pvs);
            getRegistry().registerBeanDefinition(beanName, bd);
        } catch (ClassNotFoundException ex) {
            throw new CannotLoadBeanClassException(resourceDescription, beanName, className, ex);
        } catch (LinkageError err) {
            throw new CannotLoadBeanClassException(resourceDescription, beanName, className, err);
        }
    }

    private Object readValue(Map.Entry<?, ?> entry) {
        Object val = entry.getValue();
        if (val instanceof String) {
            String strVal = (String) val;
            if (strVal.startsWith("*")) {
                String targetName = strVal.substring(1);
                val = targetName.startsWith("*") ? targetName : new RuntimeBeanReference(targetName);
            }
        }
        return val;
    }
}
