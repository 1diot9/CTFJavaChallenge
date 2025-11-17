package org.springframework.util.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.springframework.lang.Nullable;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/xml/AbstractStaxHandler.class */
abstract class AbstractStaxHandler implements ContentHandler, LexicalHandler {
    private final List<Map<String, String>> namespaceMappings = new ArrayList();
    private boolean inCData;

    protected abstract void startDocumentInternal() throws XMLStreamException;

    protected abstract void endDocumentInternal() throws XMLStreamException;

    protected abstract void startElementInternal(QName name, Attributes attributes, Map<String, String> namespaceMapping) throws XMLStreamException;

    protected abstract void endElementInternal(QName name, Map<String, String> namespaceMapping) throws XMLStreamException;

    protected abstract void charactersInternal(String data) throws XMLStreamException;

    protected abstract void cDataInternal(String data) throws XMLStreamException;

    protected abstract void ignorableWhitespaceInternal(String data) throws XMLStreamException;

    protected abstract void processingInstructionInternal(String target, String data) throws XMLStreamException;

    protected abstract void skippedEntityInternal(String name) throws XMLStreamException;

    protected abstract void dtdInternal(String dtd) throws XMLStreamException;

    protected abstract void commentInternal(String comment) throws XMLStreamException;

    @Override // org.xml.sax.ContentHandler
    public final void startDocument() throws SAXException {
        removeAllNamespaceMappings();
        newNamespaceMapping();
        try {
            startDocumentInternal();
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle startDocument: " + ex.getMessage(), ex);
        }
    }

    @Override // org.xml.sax.ContentHandler
    public final void endDocument() throws SAXException {
        removeAllNamespaceMappings();
        try {
            endDocumentInternal();
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle endDocument: " + ex.getMessage(), ex);
        }
    }

    @Override // org.xml.sax.ContentHandler
    public final void startPrefixMapping(String prefix, String uri) {
        currentNamespaceMapping().put(prefix, uri);
    }

    @Override // org.xml.sax.ContentHandler
    public final void endPrefixMapping(String prefix) {
    }

    @Override // org.xml.sax.ContentHandler
    public final void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        try {
            startElementInternal(toQName(uri, qName), atts, currentNamespaceMapping());
            newNamespaceMapping();
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle startElement: " + ex.getMessage(), ex);
        }
    }

    @Override // org.xml.sax.ContentHandler
    public final void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            endElementInternal(toQName(uri, qName), currentNamespaceMapping());
            removeNamespaceMapping();
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle endElement: " + ex.getMessage(), ex);
        }
    }

    @Override // org.xml.sax.ContentHandler
    public final void characters(char[] ch2, int start, int length) throws SAXException {
        try {
            String data = new String(ch2, start, length);
            if (!this.inCData) {
                charactersInternal(data);
            } else {
                cDataInternal(data);
            }
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle characters: " + ex.getMessage(), ex);
        }
    }

    @Override // org.xml.sax.ContentHandler
    public final void ignorableWhitespace(char[] ch2, int start, int length) throws SAXException {
        try {
            ignorableWhitespaceInternal(new String(ch2, start, length));
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle ignorableWhitespace:" + ex.getMessage(), ex);
        }
    }

    @Override // org.xml.sax.ContentHandler
    public final void processingInstruction(String target, String data) throws SAXException {
        try {
            processingInstructionInternal(target, data);
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle processingInstruction: " + ex.getMessage(), ex);
        }
    }

    @Override // org.xml.sax.ContentHandler
    public final void skippedEntity(String name) throws SAXException {
        try {
            skippedEntityInternal(name);
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle skippedEntity: " + ex.getMessage(), ex);
        }
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public final void startDTD(String name, @Nullable String publicId, String systemId) throws SAXException {
        try {
            StringBuilder builder = new StringBuilder("<!DOCTYPE ");
            builder.append(name);
            if (publicId != null) {
                builder.append(" PUBLIC \"");
                builder.append(publicId);
                builder.append("\" \"");
            } else {
                builder.append(" SYSTEM \"");
            }
            builder.append(systemId);
            builder.append("\">");
            dtdInternal(builder.toString());
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle startDTD: " + ex.getMessage(), ex);
        }
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public final void endDTD() throws SAXException {
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public final void startCDATA() throws SAXException {
        this.inCData = true;
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public final void endCDATA() throws SAXException {
        this.inCData = false;
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public final void comment(char[] ch2, int start, int length) throws SAXException {
        try {
            commentInternal(new String(ch2, start, length));
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle comment: " + ex.getMessage(), ex);
        }
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void startEntity(String name) throws SAXException {
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void endEntity(String name) throws SAXException {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public QName toQName(String namespaceUri, String qualifiedName) {
        int idx = qualifiedName.indexOf(58);
        if (idx == -1) {
            return new QName(namespaceUri, qualifiedName);
        }
        String prefix = qualifiedName.substring(0, idx);
        String localPart = qualifiedName.substring(idx + 1);
        return new QName(namespaceUri, localPart, prefix);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isNamespaceDeclaration(QName qName) {
        String prefix = qName.getPrefix();
        String localPart = qName.getLocalPart();
        return ("xmlns".equals(localPart) && prefix.isEmpty()) || ("xmlns".equals(prefix) && !localPart.isEmpty());
    }

    private Map<String, String> currentNamespaceMapping() {
        return this.namespaceMappings.get(this.namespaceMappings.size() - 1);
    }

    private void newNamespaceMapping() {
        this.namespaceMappings.add(new HashMap());
    }

    private void removeNamespaceMapping() {
        this.namespaceMappings.remove(this.namespaceMappings.size() - 1);
    }

    private void removeAllNamespaceMappings() {
        this.namespaceMappings.clear();
    }
}
