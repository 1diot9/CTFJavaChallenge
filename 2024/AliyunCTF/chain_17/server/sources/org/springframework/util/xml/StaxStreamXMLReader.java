package org.springframework.util.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.AttributesImpl;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/xml/StaxStreamXMLReader.class */
class StaxStreamXMLReader extends AbstractStaxXMLReader {
    private static final String DEFAULT_XML_VERSION = "1.0";
    private final XMLStreamReader reader;
    private String xmlVersion = DEFAULT_XML_VERSION;

    @Nullable
    private String encoding;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StaxStreamXMLReader(XMLStreamReader reader) {
        int event = reader.getEventType();
        if (event != 7 && event != 1) {
            throw new IllegalStateException("XMLEventReader not at start of document or element");
        }
        this.reader = reader;
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x00be  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00d0 A[ADDED_TO_REGION, SYNTHETIC] */
    @Override // org.springframework.util.xml.AbstractStaxXMLReader
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void parseInternal() throws org.xml.sax.SAXException, javax.xml.stream.XMLStreamException {
        /*
            r3 = this;
            r0 = 0
            r4 = r0
            r0 = 0
            r5 = r0
            r0 = 0
            r6 = r0
            r0 = r3
            javax.xml.stream.XMLStreamReader r0 = r0.reader
            int r0 = r0.getEventType()
            r7 = r0
        L11:
            r0 = r7
            r1 = 7
            if (r0 == r1) goto L29
            r0 = r7
            r1 = 8
            if (r0 == r1) goto L29
            r0 = r4
            if (r0 != 0) goto L29
            r0 = r3
            r0.handleStartDocument()
            r0 = 1
            r4 = r0
        L29:
            r0 = r7
            switch(r0) {
                case 1: goto L68;
                case 2: goto L72;
                case 3: goto L80;
                case 4: goto L87;
                case 5: goto La0;
                case 6: goto L87;
                case 7: goto L8e;
                case 8: goto L97;
                case 9: goto Lae;
                case 10: goto Lb2;
                case 11: goto La7;
                case 12: goto L87;
                default: goto Lb2;
            }
        L68:
            int r6 = r6 + 1
            r0 = r3
            r0.handleStartElement()
            goto Lb2
        L72:
            int r6 = r6 + (-1)
            r0 = r6
            if (r0 < 0) goto Lb2
            r0 = r3
            r0.handleEndElement()
            goto Lb2
        L80:
            r0 = r3
            r0.handleProcessingInstruction()
            goto Lb2
        L87:
            r0 = r3
            r0.handleCharacters()
            goto Lb2
        L8e:
            r0 = r3
            r0.handleStartDocument()
            r0 = 1
            r4 = r0
            goto Lb2
        L97:
            r0 = r3
            r0.handleEndDocument()
            r0 = 1
            r5 = r0
            goto Lb2
        La0:
            r0 = r3
            r0.handleComment()
            goto Lb2
        La7:
            r0 = r3
            r0.handleDtd()
            goto Lb2
        Lae:
            r0 = r3
            r0.handleEntityReference()
        Lb2:
            r0 = r3
            javax.xml.stream.XMLStreamReader r0 = r0.reader
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto Ld0
            r0 = r6
            if (r0 < 0) goto Ld0
            r0 = r3
            javax.xml.stream.XMLStreamReader r0 = r0.reader
            int r0 = r0.next()
            r7 = r0
            goto L11
        Ld0:
            r0 = r5
            if (r0 != 0) goto Ld8
            r0 = r3
            r0.handleEndDocument()
        Ld8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.util.xml.StaxStreamXMLReader.parseInternal():void");
    }

    private void handleStartDocument() throws SAXException {
        if (7 == this.reader.getEventType()) {
            String xmlVersion = this.reader.getVersion();
            if (StringUtils.hasLength(xmlVersion)) {
                this.xmlVersion = xmlVersion;
            }
            this.encoding = this.reader.getCharacterEncodingScheme();
        }
        ContentHandler contentHandler = getContentHandler();
        if (contentHandler != null) {
            final Location location = this.reader.getLocation();
            contentHandler.setDocumentLocator(new Locator2() { // from class: org.springframework.util.xml.StaxStreamXMLReader.1
                @Override // org.xml.sax.Locator
                public int getColumnNumber() {
                    if (location != null) {
                        return location.getColumnNumber();
                    }
                    return -1;
                }

                @Override // org.xml.sax.Locator
                public int getLineNumber() {
                    if (location != null) {
                        return location.getLineNumber();
                    }
                    return -1;
                }

                @Override // org.xml.sax.Locator
                @Nullable
                public String getPublicId() {
                    if (location != null) {
                        return location.getPublicId();
                    }
                    return null;
                }

                @Override // org.xml.sax.Locator
                @Nullable
                public String getSystemId() {
                    if (location != null) {
                        return location.getSystemId();
                    }
                    return null;
                }

                @Override // org.xml.sax.ext.Locator2
                public String getXMLVersion() {
                    return StaxStreamXMLReader.this.xmlVersion;
                }

                @Override // org.xml.sax.ext.Locator2
                @Nullable
                public String getEncoding() {
                    return StaxStreamXMLReader.this.encoding;
                }
            });
            contentHandler.startDocument();
            if (this.reader.standaloneSet()) {
                setStandalone(this.reader.isStandalone());
            }
        }
    }

    private void handleStartElement() throws SAXException {
        if (getContentHandler() != null) {
            QName qName = this.reader.getName();
            if (hasNamespacesFeature()) {
                for (int i = 0; i < this.reader.getNamespaceCount(); i++) {
                    startPrefixMapping(this.reader.getNamespacePrefix(i), this.reader.getNamespaceURI(i));
                }
                for (int i2 = 0; i2 < this.reader.getAttributeCount(); i2++) {
                    String prefix = this.reader.getAttributePrefix(i2);
                    String namespace = this.reader.getAttributeNamespace(i2);
                    if (StringUtils.hasLength(namespace)) {
                        startPrefixMapping(prefix, namespace);
                    }
                }
                getContentHandler().startElement(qName.getNamespaceURI(), qName.getLocalPart(), toQualifiedName(qName), getAttributes());
                return;
            }
            getContentHandler().startElement("", "", toQualifiedName(qName), getAttributes());
        }
    }

    private void handleEndElement() throws SAXException {
        if (getContentHandler() != null) {
            QName qName = this.reader.getName();
            if (hasNamespacesFeature()) {
                getContentHandler().endElement(qName.getNamespaceURI(), qName.getLocalPart(), toQualifiedName(qName));
                for (int i = 0; i < this.reader.getNamespaceCount(); i++) {
                    String prefix = this.reader.getNamespacePrefix(i);
                    if (prefix == null) {
                        prefix = "";
                    }
                    endPrefixMapping(prefix);
                }
                return;
            }
            getContentHandler().endElement("", "", toQualifiedName(qName));
        }
    }

    private void handleCharacters() throws SAXException {
        if (12 == this.reader.getEventType() && getLexicalHandler() != null) {
            getLexicalHandler().startCDATA();
        }
        if (getContentHandler() != null) {
            getContentHandler().characters(this.reader.getTextCharacters(), this.reader.getTextStart(), this.reader.getTextLength());
        }
        if (12 == this.reader.getEventType() && getLexicalHandler() != null) {
            getLexicalHandler().endCDATA();
        }
    }

    private void handleComment() throws SAXException {
        if (getLexicalHandler() != null) {
            getLexicalHandler().comment(this.reader.getTextCharacters(), this.reader.getTextStart(), this.reader.getTextLength());
        }
    }

    private void handleDtd() throws SAXException {
        if (getLexicalHandler() != null) {
            Location location = this.reader.getLocation();
            getLexicalHandler().startDTD(null, location.getPublicId(), location.getSystemId());
        }
        if (getLexicalHandler() != null) {
            getLexicalHandler().endDTD();
        }
    }

    private void handleEntityReference() throws SAXException {
        if (getLexicalHandler() != null) {
            getLexicalHandler().startEntity(this.reader.getLocalName());
        }
        if (getLexicalHandler() != null) {
            getLexicalHandler().endEntity(this.reader.getLocalName());
        }
    }

    private void handleEndDocument() throws SAXException {
        if (getContentHandler() != null) {
            getContentHandler().endDocument();
        }
    }

    private void handleProcessingInstruction() throws SAXException {
        if (getContentHandler() != null) {
            getContentHandler().processingInstruction(this.reader.getPITarget(), this.reader.getPIData());
        }
    }

    private Attributes getAttributes() {
        String str;
        AttributesImpl attributes = new AttributesImpl();
        for (int i = 0; i < this.reader.getAttributeCount(); i++) {
            String namespace = this.reader.getAttributeNamespace(i);
            if (namespace == null || !hasNamespacesFeature()) {
                namespace = "";
            }
            String type = this.reader.getAttributeType(i);
            if (type == null) {
                type = "CDATA";
            }
            attributes.addAttribute(namespace, this.reader.getAttributeLocalName(i), toQualifiedName(this.reader.getAttributeName(i)), type, this.reader.getAttributeValue(i));
        }
        if (hasNamespacePrefixesFeature()) {
            for (int i2 = 0; i2 < this.reader.getNamespaceCount(); i2++) {
                String prefix = this.reader.getNamespacePrefix(i2);
                String namespaceUri = this.reader.getNamespaceURI(i2);
                if (StringUtils.hasLength(prefix)) {
                    str = "xmlns:" + prefix;
                } else {
                    str = "xmlns";
                }
                String qName = str;
                attributes.addAttribute("", "", qName, "CDATA", namespaceUri);
            }
        }
        return attributes;
    }
}
