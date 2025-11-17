package org.apache.catalina.mbeans;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mbeans/ConnectorMBean.class */
public class ConnectorMBean extends ClassNameMBean<Connector> {
    private static final StringManager sm = StringManager.getManager((Class<?>) ConnectorMBean.class);

    @Override // org.apache.tomcat.util.modeler.BaseModelMBean
    public Object getAttribute(String name) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("mBean.nullName")), sm.getString("mBean.nullName"));
        }
        Connector connector = doGetManagedResource();
        return IntrospectionUtils.getProperty(connector, name);
    }

    @Override // org.apache.tomcat.util.modeler.BaseModelMBean
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (attribute == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("mBean.nullAttribute")), sm.getString("mBean.nullAttribute"));
        }
        String name = attribute.getName();
        Object value = attribute.getValue();
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("mBean.nullName")), sm.getString("mBean.nullName"));
        }
        Connector connector = doGetManagedResource();
        IntrospectionUtils.setProperty(connector, name, String.valueOf(value));
    }
}
