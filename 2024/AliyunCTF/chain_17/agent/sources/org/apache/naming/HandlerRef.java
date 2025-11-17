package org.apache.naming;

import javax.naming.StringRefAddr;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/HandlerRef.class */
public class HandlerRef extends AbstractRef {
    private static final long serialVersionUID = 1;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.HandlerFactory";
    public static final String HANDLER_NAME = "handlername";
    public static final String HANDLER_CLASS = "handlerclass";
    public static final String HANDLER_LOCALPART = "handlerlocalpart";
    public static final String HANDLER_NAMESPACE = "handlernamespace";
    public static final String HANDLER_PARAMNAME = "handlerparamname";
    public static final String HANDLER_PARAMVALUE = "handlerparamvalue";
    public static final String HANDLER_SOAPROLE = "handlersoaprole";
    public static final String HANDLER_PORTNAME = "handlerportname";

    public HandlerRef(String refname, String handlerClass) {
        this(refname, handlerClass, null, null);
    }

    public HandlerRef(String refname, String handlerClass, String factory, String factoryLocation) {
        super(refname, factory, factoryLocation);
        if (refname != null) {
            StringRefAddr refAddr = new StringRefAddr(HANDLER_NAME, refname);
            add(refAddr);
        }
        if (handlerClass != null) {
            StringRefAddr refAddr2 = new StringRefAddr(HANDLER_CLASS, handlerClass);
            add(refAddr2);
        }
    }

    @Override // org.apache.naming.AbstractRef
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.HandlerFactory";
    }
}
