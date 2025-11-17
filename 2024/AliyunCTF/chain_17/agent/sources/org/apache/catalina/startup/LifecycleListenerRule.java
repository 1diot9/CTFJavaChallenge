package org.apache.catalina.startup;

import org.apache.catalina.Container;
import org.apache.catalina.LifecycleListener;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/LifecycleListenerRule.class */
public class LifecycleListenerRule extends Rule {
    private final String attributeName;
    private final String listenerClass;

    public LifecycleListenerRule(String listenerClass, String attributeName) {
        this.listenerClass = listenerClass;
        this.attributeName = attributeName;
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        String configClass;
        String value;
        Container c = (Container) this.digester.peek();
        Container p = null;
        Object obj = this.digester.peek(1);
        if (obj instanceof Container) {
            p = (Container) obj;
        }
        String className = null;
        if (this.attributeName != null && (value = attributes.getValue(this.attributeName)) != null) {
            className = value;
        }
        if (p != null && className == null && (configClass = (String) IntrospectionUtils.getProperty(p, this.attributeName)) != null && configClass.length() > 0) {
            className = configClass;
        }
        if (className == null) {
            className = this.listenerClass;
        }
        Class<?> clazz = Class.forName(className);
        LifecycleListener listener = (LifecycleListener) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        c.addLifecycleListener(listener);
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(this.digester.toVariableName(c)).append(".addLifecycleListener(");
            code.append("new ").append(className).append("());").append(System.lineSeparator());
        }
    }
}
