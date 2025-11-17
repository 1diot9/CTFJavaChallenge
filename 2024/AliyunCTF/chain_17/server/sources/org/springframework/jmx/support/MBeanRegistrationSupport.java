package org.springframework.jmx.support;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/support/MBeanRegistrationSupport.class */
public class MBeanRegistrationSupport {

    @Nullable
    protected MBeanServer server;
    protected final Log logger = LogFactory.getLog(getClass());
    private final Set<ObjectName> registeredBeans = new LinkedHashSet();
    private RegistrationPolicy registrationPolicy = RegistrationPolicy.FAIL_ON_EXISTING;

    public void setServer(@Nullable MBeanServer server) {
        this.server = server;
    }

    @Nullable
    public final MBeanServer getServer() {
        return this.server;
    }

    public void setRegistrationPolicy(RegistrationPolicy registrationPolicy) {
        Assert.notNull(registrationPolicy, "RegistrationPolicy must not be null");
        this.registrationPolicy = registrationPolicy;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doRegister(Object mbean, ObjectName objectName) throws JMException {
        ObjectName actualObjectName;
        Assert.state(this.server != null, "No MBeanServer set");
        synchronized (this.registeredBeans) {
            ObjectInstance registeredBean = null;
            try {
                registeredBean = this.server.registerMBean(mbean, objectName);
            } catch (InstanceAlreadyExistsException ex) {
                if (this.registrationPolicy == RegistrationPolicy.IGNORE_EXISTING) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Ignoring existing MBean at [" + objectName + "]");
                    }
                } else if (this.registrationPolicy == RegistrationPolicy.REPLACE_EXISTING) {
                    try {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Replacing existing MBean at [" + objectName + "]");
                        }
                        this.server.unregisterMBean(objectName);
                        registeredBean = this.server.registerMBean(mbean, objectName);
                    } catch (InstanceNotFoundException e) {
                        if (this.logger.isInfoEnabled()) {
                            this.logger.info("Unable to replace existing MBean at [" + objectName + "]", e);
                        }
                        throw ex;
                    }
                } else {
                    throw ex;
                }
            }
            actualObjectName = registeredBean != null ? registeredBean.getObjectName() : null;
            if (actualObjectName == null) {
                actualObjectName = objectName;
            }
            this.registeredBeans.add(actualObjectName);
        }
        onRegister(actualObjectName, mbean);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void unregisterBeans() {
        Set<ObjectName> snapshot;
        synchronized (this.registeredBeans) {
            snapshot = new LinkedHashSet<>(this.registeredBeans);
        }
        if (!snapshot.isEmpty()) {
            this.logger.debug("Unregistering JMX-exposed beans");
            for (ObjectName objectName : snapshot) {
                doUnregister(objectName);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doUnregister(ObjectName objectName) {
        Assert.state(this.server != null, "No MBeanServer set");
        boolean actuallyUnregistered = false;
        synchronized (this.registeredBeans) {
            if (this.registeredBeans.remove(objectName)) {
                try {
                    if (this.server.isRegistered(objectName)) {
                        this.server.unregisterMBean(objectName);
                        actuallyUnregistered = true;
                    } else if (this.logger.isInfoEnabled()) {
                        this.logger.info("Could not unregister MBean [" + objectName + "] as said MBean is not registered (perhaps already unregistered by an external process)");
                    }
                } catch (JMException e) {
                    if (this.logger.isInfoEnabled()) {
                        this.logger.info("Could not unregister MBean [" + objectName + "]", e);
                    }
                }
            }
        }
        if (actuallyUnregistered) {
            onUnregister(objectName);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ObjectName[] getRegisteredObjectNames() {
        ObjectName[] objectNameArr;
        synchronized (this.registeredBeans) {
            objectNameArr = (ObjectName[]) this.registeredBeans.toArray(new ObjectName[0]);
        }
        return objectNameArr;
    }

    protected void onRegister(ObjectName objectName, Object mbean) {
        onRegister(objectName);
    }

    protected void onRegister(ObjectName objectName) {
    }

    protected void onUnregister(ObjectName objectName) {
    }
}
