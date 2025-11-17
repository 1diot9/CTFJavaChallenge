package org.springframework.util.xml;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/xml/DomContentHandler.class */
class DomContentHandler implements ContentHandler {
    private final Document document;
    private final List<Element> elements = new ArrayList();
    private final Node node;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DomContentHandler(Node node) {
        this.node = node;
        if (node instanceof Document) {
            Document doc = (Document) node;
            this.document = doc;
        } else {
            this.document = node.getOwnerDocument();
        }
    }

    private Node getParent() {
        if (!this.elements.isEmpty()) {
            return this.elements.get(this.elements.size() - 1);
        }
        return this.node;
    }

    @Override // org.xml.sax.ContentHandler
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        Node parent = getParent();
        Element element = this.document.createElementNS(uri, qName);
        for (int i = 0; i < attributes.getLength(); i++) {
            String attrUri = attributes.getURI(i);
            String attrQname = attributes.getQName(i);
            String value = attributes.getValue(i);
            if (!attrQname.startsWith("xmlns")) {
                element.setAttributeNS(attrUri, attrQname, value);
            }
        }
        this.elements.add((Element) parent.appendChild(element));
    }

    @Override // org.xml.sax.ContentHandler
    public void endElement(String uri, String localName, String qName) {
        this.elements.remove(this.elements.size() - 1);
    }

    @Override // org.xml.sax.ContentHandler
    public void characters(char[] ch2, int start, int length) {
        String data = new String(ch2, start, length);
        Node parent = getParent();
        Node lastChild = parent.getLastChild();
        if (lastChild != null && lastChild.getNodeType() == 3) {
            ((Text) lastChild).appendData(data);
        } else {
            Text text = this.document.createTextNode(data);
            parent.appendChild(text);
        }
    }

    @Override // org.xml.sax.ContentHandler
    public void processingInstruction(String target, String data) {
        Node parent = getParent();
        ProcessingInstruction pi = this.document.createProcessingInstruction(target, data);
        parent.appendChild(pi);
    }

    @Override // org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator locator) {
    }

    @Override // org.xml.sax.ContentHandler
    public void startDocument() {
    }

    @Override // org.xml.sax.ContentHandler
    public void endDocument() {
    }

    @Override // org.xml.sax.ContentHandler
    public void startPrefixMapping(String prefix, String uri) {
    }

    @Override // org.xml.sax.ContentHandler
    public void endPrefixMapping(String prefix) {
    }

    @Override // org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] ch2, int start, int length) {
    }

    @Override // org.xml.sax.ContentHandler
    public void skippedEntity(String name) {
    }
}
