package ch.qos.logback.core.joran.event;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.ContextAwareImpl;
import ch.qos.logback.core.status.Status;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/event/SaxEventRecorder.class */
public class SaxEventRecorder extends DefaultHandler implements ContextAware {
    final ContextAwareImpl contextAwareImpl;
    final ElementPath elementPath;
    List<SaxEvent> saxEventList;
    Locator locator;

    public SaxEventRecorder(Context context) {
        this(context, new ElementPath());
    }

    public SaxEventRecorder(Context context, ElementPath elementPath) {
        this.saxEventList = new ArrayList();
        this.contextAwareImpl = new ContextAwareImpl(context, this);
        this.elementPath = elementPath;
    }

    public final void recordEvents(InputStream inputStream) throws JoranException {
        recordEvents(new InputSource(inputStream));
    }

    public void recordEvents(InputSource inputSource) throws JoranException {
        SAXParser saxParser = buildSaxParser();
        try {
            saxParser.parse(inputSource, this);
        } catch (IOException ie) {
            handleError("I/O error occurred while parsing xml file", ie);
            throw new IllegalStateException("This point can never be reached");
        } catch (SAXException se) {
            throw new JoranException("Problem parsing XML document. See previously reported errors.", se);
        } catch (Exception ex) {
            handleError("Unexpected exception while parsing XML document.", ex);
            throw new IllegalStateException("This point can never be reached");
        }
    }

    private void handleError(String errMsg, Throwable t) throws JoranException {
        addError(errMsg, t);
        throw new JoranException(errMsg, t);
    }

    private SAXParser buildSaxParser() throws JoranException {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(false);
            spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            spf.setNamespaceAware(true);
            return spf.newSAXParser();
        } catch (ParserConfigurationException pce) {
            addError("Error during SAX paser configuration. See https://logback.qos.ch/codes.html#saxParserConfiguration", pce);
            throw new JoranException("Error during SAX paser configuration. See https://logback.qos.ch/codes.html#saxParserConfiguration", pce);
        } catch (SAXException pce2) {
            addError("Error during parser creation or parser configuration", pce2);
            throw new JoranException("Error during parser creation or parser configuration", pce2);
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startDocument() {
    }

    public Locator getLocator() {
        return this.locator;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator l) {
        this.locator = l;
    }

    protected boolean shouldIgnoreForElementPath(String tagName) {
        return false;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        String tagName = getTagName(localName, qName);
        if (!shouldIgnoreForElementPath(tagName)) {
            this.elementPath.push(tagName);
        }
        ElementPath current = this.elementPath.duplicate();
        this.saxEventList.add(new StartEvent(current, namespaceURI, localName, qName, atts, getLocator()));
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void characters(char[] ch2, int start, int length) {
        String bodyStr = new String(ch2, start, length);
        SaxEvent lastEvent = getLastEvent();
        if (lastEvent instanceof BodyEvent) {
            BodyEvent be = (BodyEvent) lastEvent;
            be.append(bodyStr);
        } else if (!isSpaceOnly(bodyStr)) {
            this.saxEventList.add(new BodyEvent(bodyStr, getLocator()));
        }
    }

    boolean isSpaceOnly(String bodyStr) {
        String bodyTrimmed = bodyStr.trim();
        return bodyTrimmed.length() == 0;
    }

    SaxEvent getLastEvent() {
        if (this.saxEventList.isEmpty()) {
            return null;
        }
        int size = this.saxEventList.size();
        return this.saxEventList.get(size - 1);
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endElement(String namespaceURI, String localName, String qName) {
        this.saxEventList.add(new EndEvent(namespaceURI, localName, qName, getLocator()));
        String tagName = getTagName(localName, qName);
        if (!shouldIgnoreForElementPath(tagName)) {
            this.elementPath.pop();
        }
    }

    String getTagName(String localName, String qName) {
        String tagName = localName;
        if (tagName == null || tagName.length() < 1) {
            tagName = qName;
        }
        return tagName;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ErrorHandler
    public void error(SAXParseException spe) throws SAXException {
        addError("XML_PARSING - Parsing error on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber());
        addError(spe.toString());
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ErrorHandler
    public void fatalError(SAXParseException spe) throws SAXException {
        addError("XML_PARSING - Parsing fatal error on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber());
        addError(spe.toString());
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ErrorHandler
    public void warning(SAXParseException spe) throws SAXException {
        addWarn("XML_PARSING - Parsing warning on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber(), spe);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addError(String msg) {
        this.contextAwareImpl.addError(msg);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addError(String msg, Throwable ex) {
        this.contextAwareImpl.addError(msg, ex);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addInfo(String msg) {
        this.contextAwareImpl.addInfo(msg);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addInfo(String msg, Throwable ex) {
        this.contextAwareImpl.addInfo(msg, ex);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addStatus(Status status) {
        this.contextAwareImpl.addStatus(status);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addWarn(String msg) {
        this.contextAwareImpl.addWarn(msg);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addWarn(String msg, Throwable ex) {
        this.contextAwareImpl.addWarn(msg, ex);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public Context getContext() {
        return this.contextAwareImpl.getContext();
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void setContext(Context context) {
        this.contextAwareImpl.setContext(context);
    }

    public List<SaxEvent> getSaxEventList() {
        return this.saxEventList;
    }
}
