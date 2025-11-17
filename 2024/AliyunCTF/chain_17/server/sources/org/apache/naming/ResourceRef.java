package org.apache.naming;

import javax.naming.StringRefAddr;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/ResourceRef.class */
public class ResourceRef extends AbstractRef {
    private static final long serialVersionUID = 1;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.ResourceFactory";
    public static final String DESCRIPTION = "description";
    public static final String SCOPE = "scope";
    public static final String AUTH = "auth";
    public static final String SINGLETON = "singleton";

    public ResourceRef(String resourceClass, String description, String scope, String auth, boolean singleton) {
        this(resourceClass, description, scope, auth, singleton, null, null);
    }

    public ResourceRef(String resourceClass, String description, String scope, String auth, boolean singleton, String factory, String factoryLocation) {
        super(resourceClass, factory, factoryLocation);
        if (description != null) {
            StringRefAddr refAddr = new StringRefAddr("description", description);
            add(refAddr);
        }
        if (scope != null) {
            StringRefAddr refAddr2 = new StringRefAddr("scope", scope);
            add(refAddr2);
        }
        if (auth != null) {
            StringRefAddr refAddr3 = new StringRefAddr(AUTH, auth);
            add(refAddr3);
        }
        StringRefAddr refAddr4 = new StringRefAddr("singleton", Boolean.toString(singleton));
        add(refAddr4);
    }

    @Override // org.apache.naming.AbstractRef
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.ResourceFactory";
    }
}
