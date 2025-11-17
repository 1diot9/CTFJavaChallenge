package org.apache.catalina.mbeans;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.descriptor.web.NamingResources;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mbeans/ContextResourceLinkMBean.class */
public class ContextResourceLinkMBean extends BaseCatalinaMBean<ContextResourceLink> {
    private static final StringManager sm = StringManager.getManager((Class<?>) ContextResourceLinkMBean.class);

    @Override // org.apache.tomcat.util.modeler.BaseModelMBean
    public Object getAttribute(String name) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("mBean.nullName")), sm.getString("mBean.nullName"));
        }
        ContextResourceLink cl = doGetManagedResource();
        if ("global".equals(name)) {
            return cl.getGlobal();
        }
        if ("description".equals(name)) {
            return cl.getDescription();
        }
        if ("name".equals(name)) {
            return cl.getName();
        }
        if ("type".equals(name)) {
            return cl.getType();
        }
        String value = (String) cl.getProperty(name);
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
        ContextResourceLink crl = doGetManagedResource();
        if ("global".equals(name)) {
            crl.setGlobal((String) value);
        } else if ("description".equals(name)) {
            crl.setDescription((String) value);
        } else if ("name".equals(name)) {
            crl.setName((String) value);
        } else if ("type".equals(name)) {
            crl.setType((String) value);
        } else {
            crl.setProperty(name, value);
        }
        NamingResources nr = crl.getNamingResources();
        nr.removeResourceLink(crl.getName());
        nr.addResourceLink(crl);
    }
}
