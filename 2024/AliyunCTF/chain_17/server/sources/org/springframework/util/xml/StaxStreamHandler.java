package org.springframework.util.xml;

import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/xml/StaxStreamHandler.class */
class StaxStreamHandler extends AbstractStaxHandler {
    private final XMLStreamWriter streamWriter;

    public StaxStreamHandler(XMLStreamWriter streamWriter) {
        this.streamWriter = streamWriter;
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void startDocumentInternal() throws XMLStreamException {
        this.streamWriter.writeStartDocument();
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void endDocumentInternal() throws XMLStreamException {
        this.streamWriter.writeEndDocument();
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void startElementInternal(QName name, Attributes attributes, Map<String, String> namespaceMapping) throws XMLStreamException {
        this.streamWriter.writeStartElement(name.getPrefix(), name.getLocalPart(), name.getNamespaceURI());
        for (Map.Entry<String, String> entry : namespaceMapping.entrySet()) {
            String prefix = entry.getKey();
            String namespaceUri = entry.getValue();
            this.streamWriter.writeNamespace(prefix, namespaceUri);
            if ("".equals(prefix)) {
                this.streamWriter.setDefaultNamespace(namespaceUri);
            } else {
                this.streamWriter.setPrefix(prefix, namespaceUri);
            }
        }
        for (int i = 0; i < attributes.getLength(); i++) {
            QName attrName = toQName(attributes.getURI(i), attributes.getQName(i));
            if (!isNamespaceDeclaration(attrName)) {
                this.streamWriter.writeAttribute(attrName.getPrefix(), attrName.getNamespaceURI(), attrName.getLocalPart(), attributes.getValue(i));
            }
        }
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void endElementInternal(QName name, Map<String, String> namespaceMapping) throws XMLStreamException {
        this.streamWriter.writeEndElement();
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void charactersInternal(String data) throws XMLStreamException {
        this.streamWriter.writeCharacters(data);
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void cDataInternal(String data) throws XMLStreamException {
        this.streamWriter.writeCData(data);
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void ignorableWhitespaceInternal(String data) throws XMLStreamException {
        this.streamWriter.writeCharacters(data);
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void processingInstructionInternal(String target, String data) throws XMLStreamException {
        this.streamWriter.writeProcessingInstruction(target, data);
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void dtdInternal(String dtd) throws XMLStreamException {
        this.streamWriter.writeDTD(dtd);
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void commentInternal(String comment) throws XMLStreamException {
        this.streamWriter.writeComment(comment);
    }

    @Override // org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator locator) {
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler, org.xml.sax.ext.LexicalHandler
    public void startEntity(String name) throws SAXException {
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler, org.xml.sax.ext.LexicalHandler
    public void endEntity(String name) throws SAXException {
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void skippedEntityInternal(String name) throws XMLStreamException {
    }
}
