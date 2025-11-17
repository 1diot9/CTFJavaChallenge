package org.apache.catalina.mbeans;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import org.apache.tomcat.util.modeler.BaseModelMBean;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mbeans/BaseCatalinaMBean.class */
public abstract class BaseCatalinaMBean<T> extends BaseModelMBean {
    /* JADX INFO: Access modifiers changed from: protected */
    public T doGetManagedResource() throws MBeanException {
        try {
            return (T) getManagedResource();
        } catch (InstanceNotFoundException | RuntimeOperationsException | InvalidTargetObjectTypeException e) {
            throw new MBeanException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static Object newInstance(String type) throws MBeanException {
        try {
            return Class.forName(type).getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (ReflectiveOperationException e) {
            throw new MBeanException(e);
        }
    }
}
