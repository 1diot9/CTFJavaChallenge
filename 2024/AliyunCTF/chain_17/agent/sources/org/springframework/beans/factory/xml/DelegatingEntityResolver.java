package org.springframework.beans.factory.xml;

import java.io.IOException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/xml/DelegatingEntityResolver.class */
public class DelegatingEntityResolver implements EntityResolver {
    public static final String DTD_SUFFIX = ".dtd";
    public static final String XSD_SUFFIX = ".xsd";
    private final EntityResolver dtdResolver;
    private final EntityResolver schemaResolver;

    public DelegatingEntityResolver(@Nullable ClassLoader classLoader) {
        this.dtdResolver = new BeansDtdResolver();
        this.schemaResolver = new PluggableSchemaResolver(classLoader);
    }

    public DelegatingEntityResolver(EntityResolver dtdResolver, EntityResolver schemaResolver) {
        Assert.notNull(dtdResolver, "'dtdResolver' is required");
        Assert.notNull(schemaResolver, "'schemaResolver' is required");
        this.dtdResolver = dtdResolver;
        this.schemaResolver = schemaResolver;
    }

    @Override // org.xml.sax.EntityResolver
    @Nullable
    public InputSource resolveEntity(@Nullable String publicId, @Nullable String systemId) throws SAXException, IOException {
        if (systemId != null) {
            if (systemId.endsWith(DTD_SUFFIX)) {
                return this.dtdResolver.resolveEntity(publicId, systemId);
            }
            if (systemId.endsWith(XSD_SUFFIX)) {
                return this.schemaResolver.resolveEntity(publicId, systemId);
            }
            return null;
        }
        return null;
    }

    public String toString() {
        return "EntityResolver delegating .xsd to " + this.schemaResolver + " and .dtd to " + this.dtdResolver;
    }
}
