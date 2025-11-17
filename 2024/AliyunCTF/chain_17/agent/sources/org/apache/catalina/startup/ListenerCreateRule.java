package org.apache.catalina.startup;

import java.util.HashMap;
import java.util.Set;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.digester.ObjectCreateRule;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/ListenerCreateRule.class */
public class ListenerCreateRule extends ObjectCreateRule {
    private static final Log log = LogFactory.getLog((Class<?>) ListenerCreateRule.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) ListenerCreateRule.class);

    public ListenerCreateRule(String className, String attributeName) {
        super(className, attributeName);
    }

    @Override // org.apache.tomcat.util.digester.ObjectCreateRule, org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if ("true".equals(attributes.getValue("optional"))) {
            try {
                super.begin(namespace, name, attributes);
                return;
            } catch (Exception e) {
                String className = getRealClassName(attributes);
                if (log.isDebugEnabled()) {
                    log.info(sm.getString("listener.createFailed", className), e);
                } else {
                    log.info(sm.getString("listener.createFailed", className));
                }
                Object instance = new OptionalListener(className);
                this.digester.push(instance);
                StringBuilder code = this.digester.getGeneratedCode();
                if (code != null) {
                    code.append(OptionalListener.class.getName().replace('$', '.')).append(' ');
                    code.append(this.digester.toVariableName(instance)).append(" = new ");
                    code.append(OptionalListener.class.getName().replace('$', '.')).append("(\"").append(className).append("\");");
                    code.append(System.lineSeparator());
                    return;
                }
                return;
            }
        }
        super.begin(namespace, name, attributes);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/ListenerCreateRule$OptionalListener.class */
    public static class OptionalListener implements LifecycleListener {
        protected final String className;
        protected final HashMap<String, String> properties = new HashMap<>();

        public OptionalListener(String className) {
            this.className = className;
        }

        public String getClassName() {
            return this.className;
        }

        @Override // org.apache.catalina.LifecycleListener
        public void lifecycleEvent(LifecycleEvent event) {
        }

        public Set<String> getProperties() {
            return this.properties.keySet();
        }

        public Object getProperty(String name) {
            return this.properties.get(name);
        }

        public boolean setProperty(String name, String value) {
            this.properties.put(name, value);
            return true;
        }
    }
}
