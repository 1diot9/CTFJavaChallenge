package org.apache.naming;

import java.util.Enumeration;
import javax.naming.RefAddr;
import javax.naming.Reference;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/AbstractRef.class */
public abstract class AbstractRef extends Reference {
    private static final long serialVersionUID = 1;

    protected abstract String getDefaultFactoryClassName();

    public AbstractRef(String className) {
        super(className);
    }

    public AbstractRef(String className, String factory, String factoryLocation) {
        super(className, factory, factoryLocation);
    }

    public final String getFactoryClassName() {
        String factory = super.getFactoryClassName();
        if (factory != null) {
            return factory;
        }
        if (System.getProperty("java.naming.factory.object") != null) {
            return null;
        }
        return getDefaultFactoryClassName();
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("[className=");
        sb.append(getClassName());
        sb.append(",factoryClassLocation=");
        sb.append(getFactoryClassLocation());
        sb.append(",factoryClassName=");
        sb.append(getFactoryClassName());
        Enumeration<RefAddr> refAddrs = getAll();
        while (refAddrs.hasMoreElements()) {
            RefAddr refAddr = refAddrs.nextElement();
            sb.append(",{type=");
            sb.append(refAddr.getType());
            sb.append(",content=");
            sb.append(refAddr.getContent());
            sb.append('}');
        }
        sb.append(']');
        return sb.toString();
    }
}
