package org.springframework.util.xml;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/xml/AbstractStaxXMLReader.class */
abstract class AbstractStaxXMLReader extends AbstractXMLReader {
    private static final String NAMESPACES_FEATURE_NAME = "http://xml.org/sax/features/namespaces";
    private static final String NAMESPACE_PREFIXES_FEATURE_NAME = "http://xml.org/sax/features/namespace-prefixes";
    private static final String IS_STANDALONE_FEATURE_NAME = "http://xml.org/sax/features/is-standalone";

    @Nullable
    private Boolean isStandalone;
    private boolean namespacesFeature = true;
    private boolean namespacePrefixesFeature = false;
    private final Map<String, String> namespaces = new LinkedHashMap();

    protected abstract void parseInternal() throws SAXException, XMLStreamException;

    @Override // org.springframework.util.xml.AbstractXMLReader, org.xml.sax.XMLReader
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        boolean z = -1;
        switch (name.hashCode()) {
            case -1847794159:
                if (name.equals(NAMESPACES_FEATURE_NAME)) {
                    z = false;
                    break;
                }
                break;
            case 1832771215:
                if (name.equals(IS_STANDALONE_FEATURE_NAME)) {
                    z = 2;
                    break;
                }
                break;
            case 1922977163:
                if (name.equals(NAMESPACE_PREFIXES_FEATURE_NAME)) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                return this.namespacesFeature;
            case true:
                return this.namespacePrefixesFeature;
            case true:
                if (this.isStandalone != null) {
                    return this.isStandalone.booleanValue();
                }
                throw new SAXNotSupportedException("startDocument() callback not completed yet");
            default:
                return super.getFeature(name);
        }
    }

    @Override // org.springframework.util.xml.AbstractXMLReader, org.xml.sax.XMLReader
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (NAMESPACES_FEATURE_NAME.equals(name)) {
            this.namespacesFeature = value;
        } else if (NAMESPACE_PREFIXES_FEATURE_NAME.equals(name)) {
            this.namespacePrefixesFeature = value;
        } else {
            super.setFeature(name, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setStandalone(boolean standalone) {
        this.isStandalone = Boolean.valueOf(standalone);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasNamespacesFeature() {
        return this.namespacesFeature;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasNamespacePrefixesFeature() {
        return this.namespacePrefixesFeature;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String toQualifiedName(QName qName) {
        String prefix = qName.getPrefix();
        if (!StringUtils.hasLength(prefix)) {
            return qName.getLocalPart();
        }
        return prefix + ":" + qName.getLocalPart();
    }

    @Override // org.xml.sax.XMLReader
    public final void parse(InputSource ignored) throws SAXException {
        parse();
    }

    @Override // org.xml.sax.XMLReader
    public final void parse(String ignored) throws SAXException {
        parse();
    }

    private void parse() throws SAXException {
        try {
            parseInternal();
        } catch (XMLStreamException ex) {
            Locator locator = null;
            if (ex.getLocation() != null) {
                locator = new StaxLocator(ex.getLocation());
            }
            SAXParseException saxException = new SAXParseException(ex.getMessage(), locator, ex);
            if (getErrorHandler() != null) {
                getErrorHandler().fatalError(saxException);
                return;
            }
            throw saxException;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void startPrefixMapping(@Nullable String prefix, String namespace) throws SAXException {
        if (getContentHandler() != null && StringUtils.hasLength(namespace)) {
            if (prefix == null) {
                prefix = "";
            }
            if (!namespace.equals(this.namespaces.get(prefix))) {
                getContentHandler().startPrefixMapping(prefix, namespace);
                this.namespaces.put(prefix, namespace);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void endPrefixMapping(String prefix) throws SAXException {
        if (getContentHandler() != null && this.namespaces.containsKey(prefix)) {
            getContentHandler().endPrefixMapping(prefix);
            this.namespaces.remove(prefix);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/xml/AbstractStaxXMLReader$StaxLocator.class */
    public static class StaxLocator implements Locator {
        private final Location location;

        public StaxLocator(Location location) {
            this.location = location;
        }

        @Override // org.xml.sax.Locator
        public String getPublicId() {
            return this.location.getPublicId();
        }

        @Override // org.xml.sax.Locator
        public String getSystemId() {
            return this.location.getSystemId();
        }

        @Override // org.xml.sax.Locator
        public int getLineNumber() {
            return this.location.getLineNumber();
        }

        @Override // org.xml.sax.Locator
        public int getColumnNumber() {
            return this.location.getColumnNumber();
        }
    }
}
