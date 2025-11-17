package org.apache.catalina.startup;

import java.lang.reflect.Method;
import org.apache.catalina.Container;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/CopyParentClassLoaderRule.class */
public class CopyParentClassLoaderRule extends Rule {
    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug("Copying parent class loader");
        }
        Container child = (Container) this.digester.peek(0);
        Object parent = this.digester.peek(1);
        Method method = parent.getClass().getMethod("getParentClassLoader", new Class[0]);
        ClassLoader classLoader = (ClassLoader) method.invoke(parent, new Object[0]);
        child.setParentClassLoader(classLoader);
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(this.digester.toVariableName(child)).append(".setParentClassLoader(");
            code.append(this.digester.toVariableName(parent)).append(".getParentClassLoader());");
            code.append(System.lineSeparator());
        }
    }
}
