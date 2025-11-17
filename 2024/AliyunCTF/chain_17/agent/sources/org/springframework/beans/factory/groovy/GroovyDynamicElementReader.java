package org.springframework.beans.factory.groovy;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.Writable;
import groovy.xml.StreamingMarkupBuilder;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.lang.Nullable;
import org.w3c.dom.Element;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/groovy/GroovyDynamicElementReader.class */
class GroovyDynamicElementReader extends GroovyObjectSupport {
    private final String rootNamespace;
    private final Map<String, String> xmlNamespaces;
    private final BeanDefinitionParserDelegate delegate;
    private final GroovyBeanDefinitionWrapper beanDefinition;
    protected final boolean decorating;
    private boolean callAfterInvocation = true;

    public GroovyDynamicElementReader(String namespace, Map<String, String> namespaceMap, BeanDefinitionParserDelegate delegate, GroovyBeanDefinitionWrapper beanDefinition, boolean decorating) {
        this.rootNamespace = namespace;
        this.xmlNamespaces = namespaceMap;
        this.delegate = delegate;
        this.beanDefinition = beanDefinition;
        this.decorating = decorating;
    }

    @Nullable
    public Object invokeMethod(final String name, Object obj) {
        final Object[] args = (Object[]) obj;
        if (name.equals("doCall")) {
            Closure<Object> callable = (Closure) args[0];
            callable.setResolveStrategy(1);
            callable.setDelegate(this);
            Object result = callable.call();
            if (this.callAfterInvocation) {
                afterInvocation();
                this.callAfterInvocation = false;
            }
            return result;
        }
        final StreamingMarkupBuilder builder = new StreamingMarkupBuilder();
        final String myNamespace = this.rootNamespace;
        final Map<String, String> myNamespaces = this.xmlNamespaces;
        Closure<Object> callable2 = new Closure<Object>(this) { // from class: org.springframework.beans.factory.groovy.GroovyDynamicElementReader.1
            public Object call(Object... arguments) {
                ((GroovyObject) getProperty("mkp")).invokeMethod("declareNamespace", new Object[]{myNamespaces});
                int len = args.length;
                if (len > 0) {
                    Object obj2 = args[len - 1];
                    if (obj2 instanceof Closure) {
                        Closure<?> callable3 = (Closure) obj2;
                        callable3.setResolveStrategy(1);
                        callable3.setDelegate(builder);
                    }
                }
                return ((GroovyObject) ((GroovyObject) getDelegate()).getProperty(myNamespace)).invokeMethod(name, args);
            }
        };
        callable2.setResolveStrategy(1);
        callable2.setDelegate(builder);
        Writable writable = (Writable) builder.bind(callable2);
        StringWriter sw = new StringWriter();
        try {
            writable.writeTo(sw);
            Element element = this.delegate.getReaderContext().readDocumentFromString(sw.toString()).getDocumentElement();
            this.delegate.initDefaults(element);
            if (this.decorating) {
                BeanDefinitionHolder holder = this.beanDefinition.getBeanDefinitionHolder();
                this.beanDefinition.setBeanDefinitionHolder(this.delegate.decorateIfRequired(element, holder, null));
            } else {
                BeanDefinition beanDefinition = this.delegate.parseCustomElement(element);
                if (beanDefinition != null) {
                    this.beanDefinition.setBeanDefinition((AbstractBeanDefinition) beanDefinition);
                }
            }
            if (this.callAfterInvocation) {
                afterInvocation();
                this.callAfterInvocation = false;
            }
            return element;
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected void afterInvocation() {
    }
}
