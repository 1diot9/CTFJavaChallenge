package org.apache.tomcat.util.descriptor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/LocalResolver.class */
public class LocalResolver implements EntityResolver2 {
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE_NAME);
    private static final String[] JAVA_EE_NAMESPACES = {XmlIdentifiers.JAVAEE_1_4_NS, "http://java.sun.com/xml/ns/javaee", "http://xmlns.jcp.org/xml/ns/javaee", "https://jakarta.ee/xml/ns/jakartaee"};
    private final Map<String, String> publicIds;
    private final Map<String, String> systemIds;
    private final boolean blockExternal;

    public LocalResolver(Map<String, String> publicIds, Map<String, String> systemIds, boolean blockExternal) {
        this.publicIds = publicIds;
        this.systemIds = systemIds;
        this.blockExternal = blockExternal;
    }

    @Override // org.xml.sax.EntityResolver
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return resolveEntity(null, publicId, null, systemId);
    }

    @Override // org.xml.sax.ext.EntityResolver2
    public InputSource resolveEntity(String name, String publicId, String base, String systemId) throws SAXException, IOException {
        URI systemUri;
        String resolved = this.publicIds.get(publicId);
        if (resolved != null) {
            InputSource is = new InputSource(resolved);
            is.setPublicId(publicId);
            return is;
        }
        if (systemId == null) {
            throw new FileNotFoundException(sm.getString("localResolver.unresolvedEntity", name, publicId, null, base));
        }
        String resolved2 = this.systemIds.get(systemId);
        if (resolved2 != null) {
            InputSource is2 = new InputSource(resolved2);
            is2.setPublicId(publicId);
            return is2;
        }
        for (String javaEENamespace : JAVA_EE_NAMESPACES) {
            String javaEESystemId = javaEENamespace + "/" + systemId;
            String resolved3 = this.systemIds.get(javaEESystemId);
            if (resolved3 != null) {
                InputSource is3 = new InputSource(resolved3);
                is3.setPublicId(publicId);
                return is3;
            }
        }
        try {
            if (base == null) {
                systemUri = new URI(systemId);
            } else {
                URI baseUri = new URI(base);
                systemUri = UriUtil.resolve(baseUri, systemId);
            }
            URI systemUri2 = systemUri.normalize();
            if (systemUri2.isAbsolute()) {
                String resolved4 = this.systemIds.get(systemUri2.toString());
                if (resolved4 != null) {
                    InputSource is4 = new InputSource(resolved4);
                    is4.setPublicId(publicId);
                    return is4;
                }
                if (!this.blockExternal) {
                    InputSource is5 = new InputSource(systemUri2.toString());
                    is5.setPublicId(publicId);
                    return is5;
                }
            }
            throw new FileNotFoundException(sm.getString("localResolver.unresolvedEntity", name, publicId, systemId, base));
        } catch (URISyntaxException e) {
            if (this.blockExternal) {
                throw new MalformedURLException(e.getMessage());
            }
            return new InputSource(systemId);
        }
    }

    @Override // org.xml.sax.ext.EntityResolver2
    public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
        return null;
    }
}
