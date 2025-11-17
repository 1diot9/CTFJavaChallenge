package org.apache.naming;

import javax.naming.StringRefAddr;
import org.apache.naming.factory.Constants;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/LookupRef.class */
public class LookupRef extends AbstractRef {
    private static final long serialVersionUID = 1;
    public static final String LOOKUP_NAME = "lookup-name";

    public LookupRef(String resourceType, String lookupName) {
        this(resourceType, null, null, lookupName);
    }

    public LookupRef(String resourceType, String factory, String factoryLocation, String lookupName) {
        super(resourceType, factory, factoryLocation);
        if (lookupName != null && !lookupName.equals("")) {
            add(new StringRefAddr(LOOKUP_NAME, lookupName));
        }
    }

    @Override // org.apache.naming.AbstractRef
    protected String getDefaultFactoryClassName() {
        return Constants.DEFAULT_LOOKUP_JNDI_FACTORY;
    }
}
