package org.apache.naming.factory;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/factory/FactoryBase.class */
public abstract class FactoryBase implements ObjectFactory {
    private static final StringManager sm = StringManager.getManager((Class<?>) FactoryBase.class);

    protected abstract boolean isReferenceTypeSupported(Object obj);

    protected abstract ObjectFactory getDefaultFactory(Reference reference) throws NamingException;

    protected abstract Object getLinked(Reference reference) throws NamingException;

    public final Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        ObjectFactory factory;
        Class<?> factoryClass;
        if (isReferenceTypeSupported(obj)) {
            Reference ref = (Reference) obj;
            Object linked = getLinked(ref);
            if (linked != null) {
                return linked;
            }
            RefAddr factoryRefAddr = ref.get(Constants.FACTORY);
            if (factoryRefAddr != null) {
                String factoryClassName = factoryRefAddr.getContent().toString();
                ClassLoader tcl = Thread.currentThread().getContextClassLoader();
                try {
                    if (tcl != null) {
                        factoryClass = tcl.loadClass(factoryClassName);
                    } else {
                        factoryClass = Class.forName(factoryClassName);
                    }
                    try {
                        factory = (ObjectFactory) factoryClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                    } catch (Throwable th) {
                        if (th instanceof NamingException) {
                            throw th;
                        }
                        if (th instanceof ThreadDeath) {
                            throw ((ThreadDeath) th);
                        }
                        if (th instanceof VirtualMachineError) {
                            throw ((VirtualMachineError) th);
                        }
                        NamingException ex = new NamingException(sm.getString("factoryBase.factoryCreationError"));
                        ex.initCause(th);
                        throw ex;
                    }
                } catch (ClassNotFoundException e) {
                    NamingException ex2 = new NamingException(sm.getString("factoryBase.factoryClassError"));
                    ex2.initCause(e);
                    throw ex2;
                }
            } else {
                factory = getDefaultFactory(ref);
            }
            if (factory != null) {
                return factory.getObjectInstance(obj, name, nameCtx, environment);
            }
            throw new NamingException(sm.getString("factoryBase.instanceCreationError"));
        }
        return null;
    }
}
