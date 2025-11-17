package org.springframework.util.xml;

import org.springframework.lang.Nullable;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/xml/AbstractXMLReader.class */
abstract class AbstractXMLReader implements XMLReader {

    @Nullable
    private DTDHandler dtdHandler;

    @Nullable
    private ContentHandler contentHandler;

    @Nullable
    private EntityResolver entityResolver;

    @Nullable
    private ErrorHandler errorHandler;

    @Nullable
    private LexicalHandler lexicalHandler;

    @Override // org.xml.sax.XMLReader
    public void setContentHandler(@Nullable ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    @Override // org.xml.sax.XMLReader
    @Nullable
    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }

    @Override // org.xml.sax.XMLReader
    public void setDTDHandler(@Nullable DTDHandler dtdHandler) {
        this.dtdHandler = dtdHandler;
    }

    @Override // org.xml.sax.XMLReader
    @Nullable
    public DTDHandler getDTDHandler() {
        return this.dtdHandler;
    }

    @Override // org.xml.sax.XMLReader
    public void setEntityResolver(@Nullable EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    @Override // org.xml.sax.XMLReader
    @Nullable
    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }

    @Override // org.xml.sax.XMLReader
    public void setErrorHandler(@Nullable ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override // org.xml.sax.XMLReader
    @Nullable
    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public LexicalHandler getLexicalHandler() {
        return this.lexicalHandler;
    }

    @Override // org.xml.sax.XMLReader
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.startsWith("http://xml.org/sax/features/")) {
            return false;
        }
        throw new SAXNotRecognizedException(name);
    }

    @Override // org.xml.sax.XMLReader
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.startsWith("http://xml.org/sax/features/")) {
            if (value) {
                throw new SAXNotSupportedException(name);
            }
            return;
        }
        throw new SAXNotRecognizedException(name);
    }

    @Override // org.xml.sax.XMLReader
    @Nullable
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
            return this.lexicalHandler;
        }
        throw new SAXNotRecognizedException(name);
    }

    @Override // org.xml.sax.XMLReader
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
            this.lexicalHandler = (LexicalHandler) value;
            return;
        }
        throw new SAXNotRecognizedException(name);
    }
}
