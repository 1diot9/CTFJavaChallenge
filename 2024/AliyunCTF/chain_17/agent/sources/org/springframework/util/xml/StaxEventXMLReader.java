package org.springframework.util.xml;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.NotationDeclaration;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.AttributesImpl;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/xml/StaxEventXMLReader.class */
class StaxEventXMLReader extends AbstractStaxXMLReader {
    private static final String DEFAULT_XML_VERSION = "1.0";
    private final XMLEventReader reader;
    private String xmlVersion = DEFAULT_XML_VERSION;

    @Nullable
    private String encoding;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StaxEventXMLReader(XMLEventReader reader) {
        try {
            XMLEvent event = reader.peek();
            if (event != null && !event.isStartDocument() && !event.isStartElement()) {
                throw new IllegalStateException("XMLEventReader not at start of document or element");
            }
            this.reader = reader;
        } catch (XMLStreamException ex) {
            throw new IllegalStateException("Could not read first element: " + ex.getMessage());
        }
    }

    @Override // org.springframework.util.xml.AbstractStaxXMLReader
    protected void parseInternal() throws SAXException, XMLStreamException {
        boolean documentStarted = false;
        boolean documentEnded = false;
        int elementDepth = 0;
        while (this.reader.hasNext() && elementDepth >= 0) {
            XMLEvent event = this.reader.nextEvent();
            if (!event.isStartDocument() && !event.isEndDocument() && !documentStarted) {
                handleStartDocument(event);
                documentStarted = true;
            }
            switch (event.getEventType()) {
                case 1:
                    elementDepth++;
                    handleStartElement(event.asStartElement());
                    break;
                case 2:
                    elementDepth--;
                    if (elementDepth < 0) {
                        break;
                    } else {
                        handleEndElement(event.asEndElement());
                        break;
                    }
                case 3:
                    handleProcessingInstruction((ProcessingInstruction) event);
                    break;
                case 4:
                case 6:
                case 12:
                    handleCharacters(event.asCharacters());
                    break;
                case 5:
                    handleComment((Comment) event);
                    break;
                case 7:
                    handleStartDocument(event);
                    documentStarted = true;
                    break;
                case 8:
                    handleEndDocument();
                    documentEnded = true;
                    break;
                case 9:
                    handleEntityReference((EntityReference) event);
                    break;
                case 11:
                    handleDtd((DTD) event);
                    break;
                case 14:
                    handleNotationDeclaration((NotationDeclaration) event);
                    break;
                case 15:
                    handleEntityDeclaration((EntityDeclaration) event);
                    break;
            }
        }
        if (documentStarted && !documentEnded) {
            handleEndDocument();
        }
    }

    private void handleStartDocument(final XMLEvent event) throws SAXException {
        if (event.isStartDocument()) {
            StartDocument startDocument = (StartDocument) event;
            String xmlVersion = startDocument.getVersion();
            if (StringUtils.hasLength(xmlVersion)) {
                this.xmlVersion = xmlVersion;
            }
            if (startDocument.encodingSet()) {
                this.encoding = startDocument.getCharacterEncodingScheme();
            }
        }
        ContentHandler contentHandler = getContentHandler();
        if (contentHandler != null) {
            final Location location = event.getLocation();
            contentHandler.setDocumentLocator(new Locator2() { // from class: org.springframework.util.xml.StaxEventXMLReader.1
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
                    return StaxEventXMLReader.this.xmlVersion;
                }

                @Override // org.xml.sax.ext.Locator2
                @Nullable
                public String getEncoding() {
                    return StaxEventXMLReader.this.encoding;
                }
            });
            contentHandler.startDocument();
        }
    }

    private void handleStartElement(StartElement startElement) throws SAXException {
        if (getContentHandler() != null) {
            QName qName = startElement.getName();
            if (hasNamespacesFeature()) {
                Iterator i = startElement.getNamespaces();
                while (i.hasNext()) {
                    Namespace namespace = (Namespace) i.next();
                    startPrefixMapping(namespace.getPrefix(), namespace.getNamespaceURI());
                }
                Iterator i2 = startElement.getAttributes();
                while (i2.hasNext()) {
                    Attribute attribute = (Attribute) i2.next();
                    QName attributeName = attribute.getName();
                    startPrefixMapping(attributeName.getPrefix(), attributeName.getNamespaceURI());
                }
                getContentHandler().startElement(qName.getNamespaceURI(), qName.getLocalPart(), toQualifiedName(qName), getAttributes(startElement));
                return;
            }
            getContentHandler().startElement("", "", toQualifiedName(qName), getAttributes(startElement));
        }
    }

    private void handleCharacters(Characters characters) throws SAXException {
        char[] data = characters.getData().toCharArray();
        if (getContentHandler() != null && characters.isIgnorableWhiteSpace()) {
            getContentHandler().ignorableWhitespace(data, 0, data.length);
            return;
        }
        if (characters.isCData() && getLexicalHandler() != null) {
            getLexicalHandler().startCDATA();
        }
        if (getContentHandler() != null) {
            getContentHandler().characters(data, 0, data.length);
        }
        if (characters.isCData() && getLexicalHandler() != null) {
            getLexicalHandler().endCDATA();
        }
    }

    private void handleEndElement(EndElement endElement) throws SAXException {
        if (getContentHandler() != null) {
            QName qName = endElement.getName();
            if (hasNamespacesFeature()) {
                getContentHandler().endElement(qName.getNamespaceURI(), qName.getLocalPart(), toQualifiedName(qName));
                Iterator i = endElement.getNamespaces();
                while (i.hasNext()) {
                    Namespace namespace = (Namespace) i.next();
                    endPrefixMapping(namespace.getPrefix());
                }
                return;
            }
            getContentHandler().endElement("", "", toQualifiedName(qName));
        }
    }

    private void handleEndDocument() throws SAXException {
        if (getContentHandler() != null) {
            getContentHandler().endDocument();
        }
    }

    private void handleNotationDeclaration(NotationDeclaration declaration) throws SAXException {
        if (getDTDHandler() != null) {
            getDTDHandler().notationDecl(declaration.getName(), declaration.getPublicId(), declaration.getSystemId());
        }
    }

    private void handleEntityDeclaration(EntityDeclaration entityDeclaration) throws SAXException {
        if (getDTDHandler() != null) {
            getDTDHandler().unparsedEntityDecl(entityDeclaration.getName(), entityDeclaration.getPublicId(), entityDeclaration.getSystemId(), entityDeclaration.getNotationName());
        }
    }

    private void handleProcessingInstruction(ProcessingInstruction pi) throws SAXException {
        if (getContentHandler() != null) {
            getContentHandler().processingInstruction(pi.getTarget(), pi.getData());
        }
    }

    private void handleComment(Comment comment) throws SAXException {
        if (getLexicalHandler() != null) {
            char[] ch2 = comment.getText().toCharArray();
            getLexicalHandler().comment(ch2, 0, ch2.length);
        }
    }

    private void handleDtd(DTD dtd) throws SAXException {
        if (getLexicalHandler() != null) {
            Location location = dtd.getLocation();
            getLexicalHandler().startDTD(null, location.getPublicId(), location.getSystemId());
        }
        if (getLexicalHandler() != null) {
            getLexicalHandler().endDTD();
        }
    }

    private void handleEntityReference(EntityReference reference) throws SAXException {
        if (getLexicalHandler() != null) {
            getLexicalHandler().startEntity(reference.getName());
        }
        if (getLexicalHandler() != null) {
            getLexicalHandler().endEntity(reference.getName());
        }
    }

    private Attributes getAttributes(StartElement event) {
        String qName;
        AttributesImpl attributes = new AttributesImpl();
        Iterator i = event.getAttributes();
        while (i.hasNext()) {
            Attribute attribute = (Attribute) i.next();
            QName qName2 = attribute.getName();
            String namespace = qName2.getNamespaceURI();
            if (namespace == null || !hasNamespacesFeature()) {
                namespace = "";
            }
            String type = attribute.getDTDType();
            if (type == null) {
                type = "CDATA";
            }
            attributes.addAttribute(namespace, qName2.getLocalPart(), toQualifiedName(qName2), type, attribute.getValue());
        }
        if (hasNamespacePrefixesFeature()) {
            Iterator i2 = event.getNamespaces();
            while (i2.hasNext()) {
                Namespace namespace2 = (Namespace) i2.next();
                String prefix = namespace2.getPrefix();
                String namespaceUri = namespace2.getNamespaceURI();
                if (StringUtils.hasLength(prefix)) {
                    qName = "xmlns:" + prefix;
                } else {
                    qName = "xmlns";
                }
                attributes.addAttribute("", "", qName, "CDATA", namespaceUri);
            }
        }
        return attributes;
    }
}
