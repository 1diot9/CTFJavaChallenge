package org.apache.tomcat.util.modeler.modules;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/modeler/modules/MbeansDescriptorsDigesterSource.class */
public class MbeansDescriptorsDigesterSource extends ModelerSource {
    private Registry registry;
    private final List<ObjectName> mbeans = new ArrayList();
    private static final Log log = LogFactory.getLog((Class<?>) MbeansDescriptorsDigesterSource.class);
    private static final Object dLock = new Object();
    private static Digester digester = null;

    private static Digester createDigester() {
        Digester digester2 = new Digester();
        digester2.setNamespaceAware(false);
        digester2.setValidating(false);
        URL url = Registry.getRegistry(null, null).getClass().getResource("/org/apache/tomcat/util/modeler/mbeans-descriptors.dtd");
        if (url != null) {
            digester2.register("-//Apache Software Foundation//DTD Model MBeans Configuration File", url.toString());
        }
        digester2.addObjectCreate("mbeans-descriptors/mbean", "org.apache.tomcat.util.modeler.ManagedBean");
        digester2.addSetProperties("mbeans-descriptors/mbean");
        digester2.addSetNext("mbeans-descriptors/mbean", BeanUtil.PREFIX_ADDER, "java.lang.Object");
        digester2.addObjectCreate("mbeans-descriptors/mbean/attribute", "org.apache.tomcat.util.modeler.AttributeInfo");
        digester2.addSetProperties("mbeans-descriptors/mbean/attribute");
        digester2.addSetNext("mbeans-descriptors/mbean/attribute", "addAttribute", "org.apache.tomcat.util.modeler.AttributeInfo");
        digester2.addObjectCreate("mbeans-descriptors/mbean/notification", "org.apache.tomcat.util.modeler.NotificationInfo");
        digester2.addSetProperties("mbeans-descriptors/mbean/notification");
        digester2.addSetNext("mbeans-descriptors/mbean/notification", "addNotification", "org.apache.tomcat.util.modeler.NotificationInfo");
        digester2.addObjectCreate("mbeans-descriptors/mbean/notification/descriptor/field", "org.apache.tomcat.util.modeler.FieldInfo");
        digester2.addSetProperties("mbeans-descriptors/mbean/notification/descriptor/field");
        digester2.addSetNext("mbeans-descriptors/mbean/notification/descriptor/field", "addField", "org.apache.tomcat.util.modeler.FieldInfo");
        digester2.addCallMethod("mbeans-descriptors/mbean/notification/notification-type", "addNotifType", 0);
        digester2.addObjectCreate("mbeans-descriptors/mbean/operation", "org.apache.tomcat.util.modeler.OperationInfo");
        digester2.addSetProperties("mbeans-descriptors/mbean/operation");
        digester2.addSetNext("mbeans-descriptors/mbean/operation", "addOperation", "org.apache.tomcat.util.modeler.OperationInfo");
        digester2.addObjectCreate("mbeans-descriptors/mbean/operation/descriptor/field", "org.apache.tomcat.util.modeler.FieldInfo");
        digester2.addSetProperties("mbeans-descriptors/mbean/operation/descriptor/field");
        digester2.addSetNext("mbeans-descriptors/mbean/operation/descriptor/field", "addField", "org.apache.tomcat.util.modeler.FieldInfo");
        digester2.addObjectCreate("mbeans-descriptors/mbean/operation/parameter", "org.apache.tomcat.util.modeler.ParameterInfo");
        digester2.addSetProperties("mbeans-descriptors/mbean/operation/parameter");
        digester2.addSetNext("mbeans-descriptors/mbean/operation/parameter", "addParameter", "org.apache.tomcat.util.modeler.ParameterInfo");
        return digester2;
    }

    public void setRegistry(Registry reg) {
        this.registry = reg;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    @Override // org.apache.tomcat.util.modeler.modules.ModelerSource
    public List<ObjectName> loadDescriptors(Registry registry, String type, Object source) throws Exception {
        setRegistry(registry);
        setSource(source);
        execute();
        return this.mbeans;
    }

    public void execute() throws Exception {
        if (this.registry == null) {
            this.registry = Registry.getRegistry(null, null);
        }
        InputStream stream = (InputStream) this.source;
        List<ManagedBean> loadedMbeans = new ArrayList<>();
        synchronized (dLock) {
            if (digester == null) {
                digester = createDigester();
            }
            try {
                try {
                    digester.push(loadedMbeans);
                    digester.parse(stream);
                    digester.reset();
                } catch (Exception e) {
                    log.error(sm.getString("modules.digesterParseError"), e);
                    throw e;
                }
            } catch (Throwable th) {
                digester.reset();
                throw th;
            }
        }
        for (ManagedBean loadedMbean : loadedMbeans) {
            this.registry.addManagedBean(loadedMbean);
        }
    }
}
