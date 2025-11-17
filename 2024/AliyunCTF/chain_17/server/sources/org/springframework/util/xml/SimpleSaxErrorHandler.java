package org.springframework.util.xml;

import org.apache.commons.logging.Log;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/xml/SimpleSaxErrorHandler.class */
public class SimpleSaxErrorHandler implements ErrorHandler {
    private final Log logger;

    public SimpleSaxErrorHandler(Log logger) {
        this.logger = logger;
    }

    @Override // org.xml.sax.ErrorHandler
    public void warning(SAXParseException ex) throws SAXException {
        this.logger.warn("Ignored XML validation warning", ex);
    }

    @Override // org.xml.sax.ErrorHandler
    public void error(SAXParseException ex) throws SAXException {
        throw ex;
    }

    @Override // org.xml.sax.ErrorHandler
    public void fatalError(SAXParseException ex) throws SAXException {
        throw ex;
    }
}
