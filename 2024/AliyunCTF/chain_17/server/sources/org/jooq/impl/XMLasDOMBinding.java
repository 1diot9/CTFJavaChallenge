package org.jooq.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jooq.Converter;
import org.jooq.XML;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLasDOMBinding.class */
public class XMLasDOMBinding extends AbstractXMLBinding<Node> {
    private static final Converter<XML, Node> CONVERTER = Converter.ofNullable(XML.class, Node.class, t -> {
        return fromString(t.data());
    }, u -> {
        return XML.xml(toString(u));
    });

    @Override // org.jooq.Binding
    public final Converter<XML, Node> converter() {
        return CONVERTER;
    }

    static final String toString(Node node) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", CustomBooleanEditor.VALUE_YES);
            Source source = new DOMSource(node);
            Result target = new StreamResult(out);
            transformer.transform(source, target);
            return out.toString("UTF-8");
        } catch (Exception e) {
            return "[ ERROR IN toString() : " + e.getMessage() + " ]";
        }
    }

    public static Document fromString(String name) {
        Document document = builder().newDocument();
        DocumentFragment fragment = createContent(document, name);
        if (fragment != null) {
            document.appendChild(fragment);
        } else {
            document.appendChild(document.createElement(name));
        }
        return document;
    }

    public static DocumentBuilder builder() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            } catch (ParserConfigurationException e) {
            }
            try {
                factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            } catch (ParserConfigurationException e2) {
            }
            try {
                factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            } catch (ParserConfigurationException e3) {
            }
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder;
        } catch (Exception e4) {
            throw new RuntimeException(e4);
        }
    }

    static final DocumentFragment createContent(Document doc, String text) {
        if (text != null && text.contains("<")) {
            DocumentBuilder builder = builder();
            try {
                String text2 = text.trim();
                if (text2.startsWith("<?xml")) {
                    Document parsed = builder.parse(new InputSource(new StringReader(text2)));
                    DocumentFragment fragment = parsed.createDocumentFragment();
                    fragment.appendChild(parsed.getDocumentElement());
                    return (DocumentFragment) doc.importNode(fragment, true);
                }
                String wrapped = "<dummy>" + text2 + "</dummy>";
                Document parsed2 = builder.parse(new InputSource(new StringReader(wrapped)));
                DocumentFragment fragment2 = parsed2.createDocumentFragment();
                NodeList children = parsed2.getDocumentElement().getChildNodes();
                while (children.getLength() > 0) {
                    fragment2.appendChild(children.item(0));
                }
                return (DocumentFragment) doc.importNode(fragment2, true);
            } catch (IOException | SAXException e) {
                return null;
            }
        }
        return null;
    }
}
