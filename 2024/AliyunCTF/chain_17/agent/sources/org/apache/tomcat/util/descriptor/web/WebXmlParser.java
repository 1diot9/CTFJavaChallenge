package org.apache.tomcat.util.descriptor.web;

import java.io.IOException;
import java.net.URL;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.descriptor.DigesterFactory;
import org.apache.tomcat.util.descriptor.InputSourceUtil;
import org.apache.tomcat.util.descriptor.XmlErrorHandler;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/WebXmlParser.class */
public class WebXmlParser {
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE_NAME);
    private final Digester webDigester;
    private final Digester webFragmentDigester;
    private final WebRuleSet webFragmentRuleSet;
    private final Log log = LogFactory.getLog((Class<?>) WebXmlParser.class);
    private final WebRuleSet webRuleSet = new WebRuleSet(false);

    public WebXmlParser(boolean namespaceAware, boolean validation, boolean blockExternal) {
        this.webDigester = DigesterFactory.newDigester(validation, namespaceAware, this.webRuleSet, blockExternal);
        this.webDigester.getParser();
        this.webFragmentRuleSet = new WebRuleSet(true);
        this.webFragmentDigester = DigesterFactory.newDigester(validation, namespaceAware, this.webFragmentRuleSet, blockExternal);
        this.webFragmentDigester.getParser();
    }

    public boolean parseWebXml(URL url, WebXml dest, boolean fragment) throws IOException {
        if (url == null) {
            return true;
        }
        InputSource source = new InputSource(url.toExternalForm());
        source.setByteStream(url.openStream());
        return parseWebXml(source, dest, fragment);
    }

    public boolean parseWebXml(InputSource source, WebXml dest, boolean fragment) {
        Digester digester;
        WebRuleSet ruleSet;
        boolean ok = true;
        if (source == null) {
            return true;
        }
        XmlErrorHandler handler = new XmlErrorHandler();
        if (fragment) {
            digester = this.webFragmentDigester;
            ruleSet = this.webFragmentRuleSet;
        } else {
            digester = this.webDigester;
            ruleSet = this.webRuleSet;
        }
        digester.push(dest);
        digester.setErrorHandler(handler);
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("webXmlParser.applicationStart", source.getSystemId()));
        }
        try {
            try {
                try {
                    digester.parse(source);
                    if (handler.getWarnings().size() > 0 || handler.getErrors().size() > 0) {
                        ok = false;
                        handler.logFindings(this.log, source.getSystemId());
                    }
                    InputSourceUtil.close(source);
                    digester.reset();
                    ruleSet.recycle();
                } catch (Exception e) {
                    this.log.error(sm.getString("webXmlParser.applicationParse", source.getSystemId()), e);
                    ok = false;
                    InputSourceUtil.close(source);
                    digester.reset();
                    ruleSet.recycle();
                }
            } catch (SAXParseException e2) {
                this.log.error(sm.getString("webXmlParser.applicationParse", source.getSystemId()), e2);
                this.log.error(sm.getString("webXmlParser.applicationPosition", e2.getLineNumber(), e2.getColumnNumber()));
                ok = false;
                InputSourceUtil.close(source);
                digester.reset();
                ruleSet.recycle();
            }
            return ok;
        } catch (Throwable th) {
            InputSourceUtil.close(source);
            digester.reset();
            ruleSet.recycle();
            throw th;
        }
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.webDigester.setClassLoader(classLoader);
        this.webFragmentDigester.setClassLoader(classLoader);
    }
}
