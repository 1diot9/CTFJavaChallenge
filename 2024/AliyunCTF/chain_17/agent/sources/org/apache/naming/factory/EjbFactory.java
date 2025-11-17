package org.apache.naming.factory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.EjbRef;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/factory/EjbFactory.class */
public class EjbFactory extends FactoryBase {
    @Override // org.apache.naming.factory.FactoryBase
    protected boolean isReferenceTypeSupported(Object obj) {
        return obj instanceof EjbRef;
    }

    @Override // org.apache.naming.factory.FactoryBase
    protected ObjectFactory getDefaultFactory(Reference ref) throws NamingException {
        String javaxEjbFactoryClassName = System.getProperty("jakarta.ejb.Factory", Constants.OPENEJB_EJB_FACTORY);
        try {
            ObjectFactory factory = (ObjectFactory) Class.forName(javaxEjbFactoryClassName).getConstructor(new Class[0]).newInstance(new Object[0]);
            return factory;
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
            NamingException ex = new NamingException("Could not create resource factory instance");
            ex.initCause(th);
            throw ex;
        }
    }

    @Override // org.apache.naming.factory.FactoryBase
    protected Object getLinked(Reference ref) throws NamingException {
        RefAddr linkRefAddr = ref.get(EjbRef.LINK);
        if (linkRefAddr != null) {
            String ejbLink = linkRefAddr.getContent().toString();
            Object beanObj = new InitialContext().lookup(ejbLink);
            return beanObj;
        }
        return null;
    }
}
