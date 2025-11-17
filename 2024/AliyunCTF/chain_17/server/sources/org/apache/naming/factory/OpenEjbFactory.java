package org.apache.naming.factory;

import ch.qos.logback.core.testUtil.CoreTestConstants;
import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.EjbRef;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/factory/OpenEjbFactory.class */
public class OpenEjbFactory implements ObjectFactory {
    protected static final String DEFAULT_OPENEJB_FACTORY = "org.openejb.client.LocalInitialContextFactory";

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        Object beanObj = null;
        if (obj instanceof EjbRef) {
            Reference ref = (Reference) obj;
            String factory = DEFAULT_OPENEJB_FACTORY;
            RefAddr factoryRefAddr = ref.get("openejb.factory");
            if (factoryRefAddr != null) {
                factory = factoryRefAddr.getContent().toString();
            }
            Properties env = new Properties();
            env.put(CoreTestConstants.JAVA_NAMING_FACTORY_INITIAL, factory);
            RefAddr linkRefAddr = ref.get("openejb.link");
            if (linkRefAddr != null) {
                String ejbLink = linkRefAddr.getContent().toString();
                beanObj = new InitialContext(env).lookup(ejbLink);
            }
        }
        return beanObj;
    }
}
