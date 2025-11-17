package org.apache.tomcat.util.digester;

import java.util.HashMap;
import org.apache.tomcat.util.IntrospectionUtils;
import org.xml.sax.Attributes;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/digester/SetPropertiesRule.class */
public class SetPropertiesRule extends Rule {
    protected final HashMap<String, String> excludes = null;

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/digester/SetPropertiesRule$Listener.class */
    public interface Listener {
        void endSetPropertiesRule();
    }

    public SetPropertiesRule() {
    }

    public SetPropertiesRule(String[] exclude) {
        for (String s : exclude) {
            if (s != null) {
                this.excludes.put(s, s);
            }
        }
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String theName, Attributes attributes) throws Exception {
        Object top = this.digester.peek();
        if (this.digester.log.isDebugEnabled()) {
            this.digester.log.debug("[SetPropertiesRule]{" + this.digester.match + "} Set " + top.getClass().getName() + " properties");
        }
        StringBuilder code = this.digester.getGeneratedCode();
        String variableName = null;
        if (code != null) {
            variableName = this.digester.toVariableName(top);
        }
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getLocalName(i);
            if (name.isEmpty()) {
                name = attributes.getQName(i);
            }
            String value = attributes.getValue(i);
            if (this.digester.log.isDebugEnabled()) {
                this.digester.log.debug("[SetPropertiesRule]{" + this.digester.match + "} Setting property '" + name + "' to '" + value + "'");
            }
            if (!this.digester.isFakeAttribute(top, name) && (this.excludes == null || !this.excludes.containsKey(name))) {
                StringBuilder actualMethod = null;
                if (code != null) {
                    actualMethod = new StringBuilder();
                }
                if (!IntrospectionUtils.setProperty(top, name, value, true, actualMethod)) {
                    if (this.digester.getRulesValidation() && !"optional".equals(name)) {
                        this.digester.log.warn(sm.getString("rule.noProperty", this.digester.match, name, value));
                    }
                } else if (code != null) {
                    code.append(variableName).append('.').append((CharSequence) actualMethod).append(';');
                    code.append(System.lineSeparator());
                }
            }
        }
        if (top instanceof Listener) {
            ((Listener) top).endSetPropertiesRule();
            if (code != null) {
                code.append("((org.apache.tomcat.util.digester.SetPropertiesRule.Listener) ");
                code.append(variableName).append(").endSetPropertiesRule();");
                code.append(System.lineSeparator());
            }
        }
    }

    public String toString() {
        return "SetPropertiesRule[]";
    }
}
