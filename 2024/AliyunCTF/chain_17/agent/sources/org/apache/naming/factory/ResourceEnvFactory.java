package org.apache.naming.factory;

import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.ResourceEnvRef;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/factory/ResourceEnvFactory.class */
public class ResourceEnvFactory extends FactoryBase {
    @Override // org.apache.naming.factory.FactoryBase
    protected boolean isReferenceTypeSupported(Object obj) {
        return obj instanceof ResourceEnvRef;
    }

    @Override // org.apache.naming.factory.FactoryBase
    protected ObjectFactory getDefaultFactory(Reference ref) {
        return null;
    }

    @Override // org.apache.naming.factory.FactoryBase
    protected Object getLinked(Reference ref) {
        return null;
    }
}
