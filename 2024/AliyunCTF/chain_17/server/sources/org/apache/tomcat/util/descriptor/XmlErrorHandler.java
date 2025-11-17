package org.apache.tomcat.util.descriptor;

import java.util.ArrayList;
import java.util.List;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/XmlErrorHandler.class */
public class XmlErrorHandler implements ErrorHandler {
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE_NAME);
    private final List<SAXParseException> errors = new ArrayList();
    private final List<SAXParseException> warnings = new ArrayList();

    @Override // org.xml.sax.ErrorHandler
    public void error(SAXParseException exception) throws SAXException {
        this.errors.add(exception);
    }

    @Override // org.xml.sax.ErrorHandler
    public void fatalError(SAXParseException exception) throws SAXException {
        throw exception;
    }

    @Override // org.xml.sax.ErrorHandler
    public void warning(SAXParseException exception) throws SAXException {
        this.warnings.add(exception);
    }

    public List<SAXParseException> getErrors() {
        return this.errors;
    }

    public List<SAXParseException> getWarnings() {
        return this.warnings;
    }

    public void logFindings(Log log, String source) {
        for (SAXParseException e : getWarnings()) {
            log.warn(sm.getString("xmlErrorHandler.warning", e.getMessage(), source));
        }
        for (SAXParseException e2 : getErrors()) {
            log.warn(sm.getString("xmlErrorHandler.error", e2.getMessage(), source));
        }
    }
}
