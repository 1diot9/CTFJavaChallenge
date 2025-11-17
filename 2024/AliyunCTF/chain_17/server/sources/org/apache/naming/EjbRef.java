package org.apache.naming;

import javax.naming.StringRefAddr;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/EjbRef.class */
public class EjbRef extends AbstractRef {
    private static final long serialVersionUID = 1;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.EjbFactory";
    public static final String TYPE = "type";
    public static final String REMOTE = "remote";
    public static final String LINK = "link";

    public EjbRef(String ejbType, String home, String remote, String link) {
        this(ejbType, home, remote, link, null, null);
    }

    public EjbRef(String ejbType, String home, String remote, String link, String factory, String factoryLocation) {
        super(home, factory, factoryLocation);
        if (ejbType != null) {
            StringRefAddr refAddr = new StringRefAddr("type", ejbType);
            add(refAddr);
        }
        if (remote != null) {
            StringRefAddr refAddr2 = new StringRefAddr(REMOTE, remote);
            add(refAddr2);
        }
        if (link != null) {
            StringRefAddr refAddr3 = new StringRefAddr(LINK, link);
            add(refAddr3);
        }
    }

    @Override // org.apache.naming.AbstractRef
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.EjbFactory";
    }
}
