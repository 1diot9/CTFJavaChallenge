package org.apache.catalina.mbeans;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import org.apache.naming.ResourceRef;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.NamingResources;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mbeans/ContextResourceMBean.class */
public class ContextResourceMBean extends BaseCatalinaMBean<ContextResource> {
    private static final StringManager sm = StringManager.getManager((Class<?>) ContextResourceMBean.class);

    @Override // org.apache.tomcat.util.modeler.BaseModelMBean
    public Object getAttribute(String name) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("mBean.nullName")), sm.getString("mBean.nullName"));
        }
        ContextResource cr = doGetManagedResource();
        if (ResourceRef.AUTH.equals(name)) {
            return cr.getAuth();
        }
        if ("description".equals(name)) {
            return cr.getDescription();
        }
        if ("name".equals(name)) {
            return cr.getName();
        }
        if ("scope".equals(name)) {
            return cr.getScope();
        }
        if ("type".equals(name)) {
            return cr.getType();
        }
        String value = (String) cr.getProperty(name);
        if (value == null) {
            throw new AttributeNotFoundException(sm.getString("mBean.attributeNotFound", name));
        }
        return value;
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
        ContextResource cr = doGetManagedResource();
        if (ResourceRef.AUTH.equals(name)) {
            cr.setAuth((String) value);
        } else if ("description".equals(name)) {
            cr.setDescription((String) value);
        } else if ("name".equals(name)) {
            cr.setName((String) value);
        } else if ("scope".equals(name)) {
            cr.setScope((String) value);
        } else if ("type".equals(name)) {
            cr.setType((String) value);
        } else {
            cr.setProperty(name, value);
        }
        NamingResources nr = cr.getNamingResources();
        nr.removeResource(cr.getName());
        nr.addResource(cr);
    }
}
