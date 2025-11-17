package org.apache.naming;

import javax.naming.StringRefAddr;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/ResourceLinkRef.class */
public class ResourceLinkRef extends AbstractRef {
    private static final long serialVersionUID = 1;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.ResourceLinkFactory";
    public static final String GLOBALNAME = "globalName";

    public ResourceLinkRef(String resourceClass, String globalName, String factory, String factoryLocation) {
        super(resourceClass, factory, factoryLocation);
        if (globalName != null) {
            StringRefAddr refAddr = new StringRefAddr(GLOBALNAME, globalName);
            add(refAddr);
        }
    }

    @Override // org.apache.naming.AbstractRef
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.ResourceLinkFactory";
    }
}
