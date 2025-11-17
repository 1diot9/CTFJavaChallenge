package org.springframework.beans.factory.groovy;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovyShell;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.beans.factory.parsing.Location;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/groovy/GroovyBeanDefinitionReader.class */
public class GroovyBeanDefinitionReader extends AbstractBeanDefinitionReader implements GroovyObject {
    private final XmlBeanDefinitionReader standardXmlBeanDefinitionReader;
    private final XmlBeanDefinitionReader groovyDslXmlBeanDefinitionReader;
    private final Map<String, String> namespaces;
    private final Map<String, DeferredProperty> deferredProperties;
    private MetaClass metaClass;

    @Nullable
    private Binding binding;

    @Nullable
    private GroovyBeanDefinitionWrapper currentBeanDefinition;

    public GroovyBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
        this.namespaces = new HashMap();
        this.deferredProperties = new HashMap();
        this.metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(getClass());
        this.standardXmlBeanDefinitionReader = new XmlBeanDefinitionReader(registry);
        this.groovyDslXmlBeanDefinitionReader = new XmlBeanDefinitionReader(registry);
        this.groovyDslXmlBeanDefinitionReader.setValidating(false);
    }

    public GroovyBeanDefinitionReader(XmlBeanDefinitionReader xmlBeanDefinitionReader) {
        super(xmlBeanDefinitionReader.getRegistry());
        this.namespaces = new HashMap();
        this.deferredProperties = new HashMap();
        this.metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(getClass());
        this.standardXmlBeanDefinitionReader = new XmlBeanDefinitionReader(xmlBeanDefinitionReader.getRegistry());
        this.groovyDslXmlBeanDefinitionReader = xmlBeanDefinitionReader;
    }

    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    public MetaClass getMetaClass() {
        return this.metaClass;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    @Nullable
    public Binding getBinding() {
        return this.binding;
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionReader
    public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
        return loadBeanDefinitions(new EncodedResource(resource));
    }

    public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
        String filename = encodedResource.getResource().getFilename();
        if (StringUtils.endsWithIgnoreCase(filename, XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX)) {
            return this.standardXmlBeanDefinitionReader.loadBeanDefinitions(encodedResource);
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Loading Groovy bean definitions from " + encodedResource);
        }
        Closure<Object> beans = new Closure<Object>(this) { // from class: org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader.1
            public Object call(Object... args) {
                GroovyBeanDefinitionReader.this.invokeBeanDefiningClosure((Closure) args[0]);
                return null;
            }
        };
        Binding binding = new Binding() { // from class: org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader.2
            public void setVariable(String name, Object value) {
                if (GroovyBeanDefinitionReader.this.currentBeanDefinition != null) {
                    GroovyBeanDefinitionReader.this.applyPropertyToBeanDefinition(name, value);
                } else {
                    super.setVariable(name, value);
                }
            }
        };
        binding.setVariable(DefaultBeanDefinitionDocumentReader.NESTED_BEANS_ELEMENT, beans);
        int countBefore = getRegistry().getBeanDefinitionCount();
        try {
            GroovyShell shell = new GroovyShell(getBeanClassLoader(), binding);
            shell.evaluate(encodedResource.getReader(), DefaultBeanDefinitionDocumentReader.NESTED_BEANS_ELEMENT);
            int count = getRegistry().getBeanDefinitionCount() - countBefore;
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Loaded " + count + " bean definitions from " + encodedResource);
            }
            return count;
        } catch (Throwable ex) {
            throw new BeanDefinitionParsingException(new Problem("Error evaluating Groovy script: " + ex.getMessage(), new Location(encodedResource.getResource()), null, ex));
        }
    }

    public GroovyBeanDefinitionReader beans(Closure<?> closure) {
        return invokeBeanDefiningClosure(closure);
    }

    public GenericBeanDefinition bean(Class<?> type) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(type);
        return beanDefinition;
    }

    public AbstractBeanDefinition bean(Class<?> type, Object... args) {
        GroovyBeanDefinitionWrapper current = this.currentBeanDefinition;
        try {
            Closure<?> callable = null;
            Collection<Object> constructorArgs = null;
            if (!ObjectUtils.isEmpty(args)) {
                int index = args.length;
                Object lastArg = args[index - 1];
                if (lastArg instanceof Closure) {
                    Closure<?> closure = (Closure) lastArg;
                    callable = closure;
                    index--;
                }
                constructorArgs = resolveConstructorArguments(args, 0, index);
            }
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(null, type, constructorArgs);
            if (callable != null) {
                callable.call(this.currentBeanDefinition);
            }
            AbstractBeanDefinition beanDefinition = this.currentBeanDefinition.getBeanDefinition();
            this.currentBeanDefinition = current;
            return beanDefinition;
        } catch (Throwable th) {
            this.currentBeanDefinition = current;
            throw th;
        }
    }

    public void xmlns(Map<String, String> definition) {
        if (!definition.isEmpty()) {
            for (Map.Entry<String, String> entry : definition.entrySet()) {
                String namespace = entry.getKey();
                String uri = entry.getValue();
                if (uri == null) {
                    throw new IllegalArgumentException("Namespace definition must supply a non-null URI");
                }
                NamespaceHandler namespaceHandler = this.groovyDslXmlBeanDefinitionReader.getNamespaceHandlerResolver().resolve(uri);
                if (namespaceHandler == null) {
                    throw new BeanDefinitionParsingException(new Problem("No namespace handler found for URI: " + uri, new Location(new DescriptiveResource("Groovy"))));
                }
                this.namespaces.put(namespace, uri);
            }
        }
    }

    public void importBeans(String resourcePattern) throws IOException {
        loadBeanDefinitions(resourcePattern);
    }

    public Object invokeMethod(String name, Object arg) {
        String refName;
        Object[] args = (Object[]) arg;
        if (DefaultBeanDefinitionDocumentReader.NESTED_BEANS_ELEMENT.equals(name) && args.length == 1) {
            Object obj = args[0];
            if (obj instanceof Closure) {
                Closure<?> closure = (Closure) obj;
                return beans(closure);
            }
        }
        if ("ref".equals(name)) {
            if (args[0] == null) {
                throw new IllegalArgumentException("Argument to ref() is not a valid bean or was not found");
            }
            Object obj2 = args[0];
            if (obj2 instanceof RuntimeBeanReference) {
                RuntimeBeanReference runtimeBeanReference = (RuntimeBeanReference) obj2;
                refName = runtimeBeanReference.getBeanName();
            } else {
                refName = args[0].toString();
            }
            boolean parentRef = false;
            if (args.length > 1) {
                Object obj3 = args[1];
                if (obj3 instanceof Boolean) {
                    Boolean bool = (Boolean) obj3;
                    parentRef = bool.booleanValue();
                }
            }
            return new RuntimeBeanReference(refName, parentRef);
        }
        if (this.namespaces.containsKey(name) && args.length > 0 && (args[0] instanceof Closure)) {
            GroovyDynamicElementReader reader = createDynamicElementReader(name);
            reader.invokeMethod("doCall", args);
        } else {
            if (args.length > 0 && (args[0] instanceof Closure)) {
                return invokeBeanDefiningMethod(name, args);
            }
            if (args.length > 0 && ((args[0] instanceof Class) || (args[0] instanceof RuntimeBeanReference) || (args[0] instanceof Map))) {
                return invokeBeanDefiningMethod(name, args);
            }
            if (args.length > 1 && (args[args.length - 1] instanceof Closure)) {
                return invokeBeanDefiningMethod(name, args);
            }
        }
        MetaClass mc = DefaultGroovyMethods.getMetaClass(getRegistry());
        if (!mc.respondsTo(getRegistry(), name, args).isEmpty()) {
            return mc.invokeMethod(getRegistry(), name, args);
        }
        return this;
    }

    private boolean addDeferredProperty(String property, Object newValue) {
        if ((newValue instanceof List) || (newValue instanceof Map)) {
            this.deferredProperties.put(this.currentBeanDefinition.getBeanName() + "." + property, new DeferredProperty(this.currentBeanDefinition, property, newValue));
            return true;
        }
        return false;
    }

    private void finalizeDeferredProperties() {
        for (DeferredProperty dp : this.deferredProperties.values()) {
            Object obj = dp.value;
            if (obj instanceof List) {
                List<?> list = (List) obj;
                dp.value = manageListIfNecessary(list);
            } else {
                Object obj2 = dp.value;
                if (obj2 instanceof Map) {
                    Map<?, ?> map = (Map) obj2;
                    dp.value = manageMapIfNecessary(map);
                }
            }
            dp.apply();
        }
        this.deferredProperties.clear();
    }

    protected GroovyBeanDefinitionReader invokeBeanDefiningClosure(Closure<?> callable) {
        callable.setDelegate(this);
        callable.call();
        finalizeDeferredProperties();
        return this;
    }

    private GroovyBeanDefinitionWrapper invokeBeanDefiningMethod(String beanName, Object[] args) {
        boolean hasClosureArgument = args[args.length - 1] instanceof Closure;
        Object obj = args[0];
        if (obj instanceof Class) {
            Class<?> beanClass = (Class) obj;
            if (hasClosureArgument) {
                if (args.length - 1 != 1) {
                    this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, beanClass, resolveConstructorArguments(args, 1, args.length - 1));
                } else {
                    this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, beanClass);
                }
            } else {
                this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, beanClass, resolveConstructorArguments(args, 1, args.length));
            }
        } else {
            Object obj2 = args[0];
            if (obj2 instanceof RuntimeBeanReference) {
                RuntimeBeanReference runtimeBeanReference = (RuntimeBeanReference) obj2;
                this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
                this.currentBeanDefinition.getBeanDefinition().setFactoryBeanName(runtimeBeanReference.getBeanName());
            } else {
                Object obj3 = args[0];
                if (obj3 instanceof Map) {
                    Map<?, ?> namedArgs = (Map) obj3;
                    if (args.length > 1) {
                        Object obj4 = args[1];
                        if (obj4 instanceof Class) {
                            Class<?> clazz = (Class) obj4;
                            List<Object> constructorArgs = resolveConstructorArguments(args, 2, hasClosureArgument ? args.length - 1 : args.length);
                            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, clazz, constructorArgs);
                            for (Map.Entry<?, ?> entity : namedArgs.entrySet()) {
                                String propName = (String) entity.getKey();
                                setProperty(propName, entity.getValue());
                            }
                        }
                    }
                    this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
                    Map.Entry<?, ?> factoryBeanEntry = namedArgs.entrySet().iterator().next();
                    int constructorArgsTest = hasClosureArgument ? 2 : 1;
                    if (args.length > constructorArgsTest) {
                        int endOfConstructArgs = hasClosureArgument ? args.length - 1 : args.length;
                        this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, null, resolveConstructorArguments(args, 1, endOfConstructArgs));
                    } else {
                        this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
                    }
                    this.currentBeanDefinition.getBeanDefinition().setFactoryBeanName(factoryBeanEntry.getKey().toString());
                    this.currentBeanDefinition.getBeanDefinition().setFactoryMethodName(factoryBeanEntry.getValue().toString());
                } else if (args[0] instanceof Closure) {
                    this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
                    this.currentBeanDefinition.getBeanDefinition().setAbstract(true);
                } else {
                    List<Object> constructorArgs2 = resolveConstructorArguments(args, 0, hasClosureArgument ? args.length - 1 : args.length);
                    this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, null, constructorArgs2);
                }
            }
        }
        if (hasClosureArgument) {
            Closure<?> callable = (Closure) args[args.length - 1];
            callable.setDelegate(this);
            callable.setResolveStrategy(1);
            callable.call(this.currentBeanDefinition);
        }
        GroovyBeanDefinitionWrapper beanDefinition = this.currentBeanDefinition;
        this.currentBeanDefinition = null;
        beanDefinition.getBeanDefinition().setAttribute(GroovyBeanDefinitionWrapper.class.getName(), beanDefinition);
        getRegistry().registerBeanDefinition(beanName, beanDefinition.getBeanDefinition());
        return beanDefinition;
    }

    protected List<Object> resolveConstructorArguments(Object[] args, int start, int end) {
        Object[] constructorArgs = Arrays.copyOfRange(args, start, end);
        for (int i = 0; i < constructorArgs.length; i++) {
            if (constructorArgs[i] instanceof GString) {
                constructorArgs[i] = constructorArgs[i].toString();
            } else {
                Object obj = constructorArgs[i];
                if (obj instanceof List) {
                    List<?> list = (List) obj;
                    constructorArgs[i] = manageListIfNecessary(list);
                } else {
                    Object obj2 = constructorArgs[i];
                    if (obj2 instanceof Map) {
                        Map<?, ?> map = (Map) obj2;
                        constructorArgs[i] = manageMapIfNecessary(map);
                    }
                }
            }
        }
        return List.of(constructorArgs);
    }

    private Object manageMapIfNecessary(Map<?, ?> map) {
        boolean containsRuntimeRefs = false;
        Iterator<?> it = map.values().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Object element = it.next();
            if (element instanceof RuntimeBeanReference) {
                containsRuntimeRefs = true;
                break;
            }
        }
        if (containsRuntimeRefs) {
            Map<Object, Object> managedMap = new ManagedMap<>();
            managedMap.putAll(map);
            return managedMap;
        }
        return map;
    }

    private Object manageListIfNecessary(List<?> list) {
        boolean containsRuntimeRefs = false;
        Iterator<?> it = list.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Object element = it.next();
            if (element instanceof RuntimeBeanReference) {
                containsRuntimeRefs = true;
                break;
            }
        }
        if (containsRuntimeRefs) {
            List<Object> managedList = new ManagedList<>();
            managedList.addAll(list);
            return managedList;
        }
        return list;
    }

    public void setProperty(String name, Object value) {
        if (this.currentBeanDefinition != null) {
            applyPropertyToBeanDefinition(name, value);
        }
    }

    protected void applyPropertyToBeanDefinition(String name, Object value) {
        if (value instanceof GString) {
            value = value.toString();
        }
        if (addDeferredProperty(name, value)) {
            return;
        }
        if (value instanceof Closure) {
            Closure<?> callable = (Closure) value;
            GroovyBeanDefinitionWrapper current = this.currentBeanDefinition;
            try {
                Class<?> parameterType = callable.getParameterTypes()[0];
                if (Object.class == parameterType) {
                    this.currentBeanDefinition = new GroovyBeanDefinitionWrapper("");
                    callable.call(this.currentBeanDefinition);
                } else {
                    this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(null, parameterType);
                    callable.call((Object) null);
                }
                value = this.currentBeanDefinition.getBeanDefinition();
                this.currentBeanDefinition = current;
            } catch (Throwable th) {
                this.currentBeanDefinition = current;
                throw th;
            }
        }
        this.currentBeanDefinition.addProperty(name, value);
    }

    public Object getProperty(String name) {
        Binding binding = getBinding();
        if (binding != null && binding.hasVariable(name)) {
            return binding.getVariable(name);
        }
        if (this.namespaces.containsKey(name)) {
            return createDynamicElementReader(name);
        }
        if (getRegistry().containsBeanDefinition(name)) {
            GroovyBeanDefinitionWrapper beanDefinition = (GroovyBeanDefinitionWrapper) getRegistry().getBeanDefinition(name).getAttribute(GroovyBeanDefinitionWrapper.class.getName());
            if (beanDefinition != null) {
                return new GroovyRuntimeBeanReference(name, beanDefinition, false);
            }
            return new RuntimeBeanReference(name, false);
        }
        if (this.currentBeanDefinition != null) {
            MutablePropertyValues pvs = this.currentBeanDefinition.getBeanDefinition().getPropertyValues();
            if (pvs.contains(name)) {
                return pvs.get(name);
            }
            DeferredProperty dp = this.deferredProperties.get(this.currentBeanDefinition.getBeanName() + name);
            if (dp != null) {
                return dp.value;
            }
            return getMetaClass().getProperty(this, name);
        }
        return getMetaClass().getProperty(this, name);
    }

    private GroovyDynamicElementReader createDynamicElementReader(String namespace) {
        XmlReaderContext readerContext = this.groovyDslXmlBeanDefinitionReader.createReaderContext(new DescriptiveResource("Groovy"));
        BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext);
        boolean decorating = this.currentBeanDefinition != null;
        if (!decorating) {
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(namespace);
        }
        return new GroovyDynamicElementReader(namespace, this.namespaces, delegate, this.currentBeanDefinition, decorating) { // from class: org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader.3
            @Override // org.springframework.beans.factory.groovy.GroovyDynamicElementReader
            protected void afterInvocation() {
                if (!this.decorating) {
                    GroovyBeanDefinitionReader.this.currentBeanDefinition = null;
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/groovy/GroovyBeanDefinitionReader$DeferredProperty.class */
    public static class DeferredProperty {
        private final GroovyBeanDefinitionWrapper beanDefinition;
        private final String name;
        public Object value;

        public DeferredProperty(GroovyBeanDefinitionWrapper beanDefinition, String name, Object value) {
            this.beanDefinition = beanDefinition;
            this.name = name;
            this.value = value;
        }

        public void apply() {
            this.beanDefinition.addProperty(this.name, this.value);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/groovy/GroovyBeanDefinitionReader$GroovyRuntimeBeanReference.class */
    public class GroovyRuntimeBeanReference extends RuntimeBeanReference implements GroovyObject {
        private final GroovyBeanDefinitionWrapper beanDefinition;
        private MetaClass metaClass;

        public GroovyRuntimeBeanReference(String beanName, GroovyBeanDefinitionWrapper beanDefinition, boolean toParent) {
            super(beanName, toParent);
            this.beanDefinition = beanDefinition;
            this.metaClass = InvokerHelper.getMetaClass(this);
        }

        public MetaClass getMetaClass() {
            return this.metaClass;
        }

        public Object getProperty(String property) {
            if (property.equals("beanName")) {
                return getBeanName();
            }
            if (property.equals("source")) {
                return getSource();
            }
            if (this.beanDefinition != null) {
                return new GroovyPropertyValue(property, this.beanDefinition.getBeanDefinition().getPropertyValues().get(property));
            }
            return this.metaClass.getProperty(this, property);
        }

        public Object invokeMethod(String name, Object args) {
            return this.metaClass.invokeMethod(this, name, args);
        }

        public void setMetaClass(MetaClass metaClass) {
            this.metaClass = metaClass;
        }

        public void setProperty(String property, Object newValue) {
            if (!GroovyBeanDefinitionReader.this.addDeferredProperty(property, newValue)) {
                this.beanDefinition.getBeanDefinition().getPropertyValues().add(property, newValue);
            }
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/groovy/GroovyBeanDefinitionReader$GroovyRuntimeBeanReference$GroovyPropertyValue.class */
        private class GroovyPropertyValue extends GroovyObjectSupport {
            private final String propertyName;
            private final Object propertyValue;

            public GroovyPropertyValue(String propertyName, Object propertyValue) {
                this.propertyName = propertyName;
                this.propertyValue = propertyValue;
            }

            public void leftShift(Object value) {
                InvokerHelper.invokeMethod(this.propertyValue, "leftShift", value);
                updateDeferredProperties(value);
            }

            public boolean add(Object value) {
                boolean retVal = ((Boolean) InvokerHelper.invokeMethod(this.propertyValue, BeanUtil.PREFIX_ADDER, value)).booleanValue();
                updateDeferredProperties(value);
                return retVal;
            }

            public boolean addAll(Collection<?> values) {
                boolean retVal = ((Boolean) InvokerHelper.invokeMethod(this.propertyValue, "addAll", values)).booleanValue();
                for (Object value : values) {
                    updateDeferredProperties(value);
                }
                return retVal;
            }

            public Object invokeMethod(String name, Object args) {
                return InvokerHelper.invokeMethod(this.propertyValue, name, args);
            }

            public Object getProperty(String name) {
                return InvokerHelper.getProperty(this.propertyValue, name);
            }

            public void setProperty(String name, Object value) {
                InvokerHelper.setProperty(this.propertyValue, name, value);
            }

            private void updateDeferredProperties(Object value) {
                if (value instanceof RuntimeBeanReference) {
                    GroovyBeanDefinitionReader.this.deferredProperties.put(GroovyRuntimeBeanReference.this.beanDefinition.getBeanName(), new DeferredProperty(GroovyRuntimeBeanReference.this.beanDefinition, this.propertyName, this.propertyValue));
                }
            }
        }
    }
}
