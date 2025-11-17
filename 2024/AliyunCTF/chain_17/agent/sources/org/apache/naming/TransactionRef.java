package org.apache.naming;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/TransactionRef.class */
public class TransactionRef extends AbstractRef {
    private static final long serialVersionUID = 1;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.TransactionFactory";

    public TransactionRef() {
        this(null, null);
    }

    public TransactionRef(String factory, String factoryLocation) {
        super("jakarta.transaction.UserTransaction", factory, factoryLocation);
    }

    @Override // org.apache.naming.AbstractRef
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.TransactionFactory";
    }
}
