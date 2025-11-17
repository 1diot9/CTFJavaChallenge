package org.apache.catalina.util;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.catalina.Globals;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.LifecycleException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/LifecycleMBeanBase.class */
public abstract class LifecycleMBeanBase extends LifecycleBase implements JmxEnabled {
    private static final Log log = LogFactory.getLog((Class<?>) LifecycleMBeanBase.class);
    private static final StringManager sm = StringManager.getManager("org.apache.catalina.util");
    private String domain = null;
    private ObjectName oname = null;

    protected abstract String getDomainInternal();

    protected abstract String getObjectNameKeyProperties();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        if (this.oname == null) {
            this.oname = register(this, getObjectNameKeyProperties());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleBase
    public void destroyInternal() throws LifecycleException {
        unregister(this.oname);
    }

    @Override // org.apache.catalina.JmxEnabled
    public final void setDomain(String domain) {
        this.domain = domain;
    }

    @Override // org.apache.catalina.JmxEnabled
    public final String getDomain() {
        if (this.domain == null) {
            this.domain = getDomainInternal();
        }
        if (this.domain == null) {
            this.domain = Globals.DEFAULT_MBEAN_DOMAIN;
        }
        return this.domain;
    }

    @Override // org.apache.catalina.JmxEnabled
    public final ObjectName getObjectName() {
        return this.oname;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ObjectName register(Object obj, String objectNameKeyProperties) {
        StringBuilder name = new StringBuilder(getDomain());
        name.append(':');
        name.append(objectNameKeyProperties);
        ObjectName on = null;
        try {
            on = new ObjectName(name.toString());
            Registry.getRegistry(null, null).registerComponent(obj, on, (String) null);
        } catch (Exception e) {
            log.warn(sm.getString("lifecycleMBeanBase.registerFail", obj, name), e);
        }
        return on;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void unregister(String objectNameKeyProperties) {
        Registry.getRegistry(null, null).unregisterComponent(getDomain() + ':' + objectNameKeyProperties);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void unregister(ObjectName on) {
        Registry.getRegistry(null, null).unregisterComponent(on);
    }

    public final void postDeregister() {
    }

    public final void postRegister(Boolean registrationDone) {
    }

    public final void preDeregister() throws Exception {
    }

    public final ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        this.oname = name;
        this.domain = name.getDomain().intern();
        return this.oname;
    }
}
