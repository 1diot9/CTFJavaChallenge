package org.apache.naming.java;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.ContextBindings;
import org.apache.naming.NamingContext;
import org.apache.naming.SelectorContext;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/java/javaURLContextFactory.class */
public class javaURLContextFactory implements ObjectFactory, InitialContextFactory {
    public static final String MAIN = "initialContext";
    protected static volatile Context initialContext = null;

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws NamingException {
        if (ContextBindings.isThreadBound() || ContextBindings.isClassLoaderBound()) {
            return new SelectorContext(environment);
        }
        return null;
    }

    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
        if (ContextBindings.isThreadBound() || ContextBindings.isClassLoaderBound()) {
            return new SelectorContext(environment, true);
        }
        if (initialContext == null) {
            synchronized (javaURLContextFactory.class) {
                if (initialContext == null) {
                    initialContext = new NamingContext(environment, MAIN);
                }
            }
        }
        return initialContext;
    }
}
