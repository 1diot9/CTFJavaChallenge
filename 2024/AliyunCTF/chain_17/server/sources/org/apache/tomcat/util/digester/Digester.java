package org.apache.tomcat.util.digester;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.digester.DocumentProperties;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.AttributesImpl;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/digester/Digester.class */
public class Digester extends DefaultHandler2 {
    protected static IntrospectionUtils.PropertySource[] propertySources;
    private static boolean propertySourcesSet;
    protected static final StringManager sm = StringManager.getManager((Class<?>) Digester.class);
    private static final HashSet<String> generatedClasses;
    private static GeneratedCodeLoader generatedCodeLoader;
    protected IntrospectionUtils.PropertySource[] source;
    protected EntityResolver entityResolver;
    protected StringBuilder bodyText = new StringBuilder();
    protected ArrayStack<StringBuilder> bodyTexts = new ArrayStack<>();
    protected ArrayStack<List<Rule>> matches = new ArrayStack<>(10);
    protected ClassLoader classLoader = null;
    protected boolean configured = false;
    protected HashMap<String, String> entityValidator = new HashMap<>();
    protected ErrorHandler errorHandler = null;
    protected SAXParserFactory factory = null;
    protected Locator locator = null;
    protected String match = "";
    protected boolean namespaceAware = false;
    protected HashMap<String, ArrayStack<String>> namespaces = new HashMap<>();
    protected ArrayStack<Object> params = new ArrayStack<>();
    protected SAXParser parser = null;
    protected String publicId = null;
    protected XMLReader reader = null;
    protected Object root = null;
    protected Rules rules = null;
    protected ArrayStack<Object> stack = new ArrayStack<>();
    protected boolean useContextClassLoader = false;
    protected boolean validating = false;
    protected boolean rulesValidation = false;
    protected Map<Class<?>, List<String>> fakeAttributes = null;
    protected Log log = LogFactory.getLog((Class<?>) Digester.class);
    protected Log saxLog = LogFactory.getLog("org.apache.tomcat.util.digester.Digester.sax");
    protected StringBuilder code = null;
    protected ArrayList<Object> known = new ArrayList<>();

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/digester/Digester$GeneratedCodeLoader.class */
    public interface GeneratedCodeLoader {
        Object loadGeneratedCode(String str);
    }

    static {
        propertySourcesSet = false;
        String classNames = System.getProperty("org.apache.tomcat.util.digester.PROPERTY_SOURCE");
        ArrayList<IntrospectionUtils.PropertySource> sourcesList = new ArrayList<>();
        IntrospectionUtils.PropertySource[] sources = null;
        if (classNames != null) {
            StringTokenizer classNamesTokenizer = new StringTokenizer(classNames, ",");
            while (classNamesTokenizer.hasMoreTokens()) {
                String className = classNamesTokenizer.nextToken().trim();
                ClassLoader[] cls = {Digester.class.getClassLoader(), Thread.currentThread().getContextClassLoader()};
                for (ClassLoader cl : cls) {
                    try {
                        Class<?> clazz = Class.forName(className, true, cl);
                        sourcesList.add((IntrospectionUtils.PropertySource) clazz.getConstructor(new Class[0]).newInstance(new Object[0]));
                        break;
                    } catch (Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                        LogFactory.getLog((Class<?>) Digester.class).error(sm.getString("digester.propertySourceLoadError", className), t);
                    }
                }
            }
            sources = (IntrospectionUtils.PropertySource[]) sourcesList.toArray(new IntrospectionUtils.PropertySource[0]);
        }
        if (sources != null) {
            propertySources = sources;
            propertySourcesSet = true;
        }
        if (Boolean.getBoolean("org.apache.tomcat.util.digester.REPLACE_SYSTEM_PROPERTIES")) {
            replaceSystemProperties();
        }
        generatedClasses = new HashSet<>();
    }

    public static void setPropertySource(IntrospectionUtils.PropertySource propertySource) {
        if (!propertySourcesSet) {
            propertySources = new IntrospectionUtils.PropertySource[1];
            propertySources[0] = propertySource;
            propertySourcesSet = true;
        }
    }

    public static void setPropertySource(IntrospectionUtils.PropertySource[] propertySources2) {
        if (!propertySourcesSet) {
            propertySources = propertySources2;
            propertySourcesSet = true;
        }
    }

    public static void addGeneratedClass(String className) {
        generatedClasses.add(className);
    }

    public static String[] getGeneratedClasses() {
        return (String[]) generatedClasses.toArray(new String[0]);
    }

    public static boolean isGeneratedCodeLoaderSet() {
        return generatedCodeLoader != null;
    }

    public static void setGeneratedCodeLoader(GeneratedCodeLoader generatedCodeLoader2) {
        if (generatedCodeLoader == null) {
            generatedCodeLoader = generatedCodeLoader2;
        }
    }

    public static Object loadGeneratedClass(String className) {
        if (generatedCodeLoader != null) {
            return generatedCodeLoader.loadGeneratedCode(className);
        }
        return null;
    }

    public Digester() {
        propertySourcesSet = true;
        ArrayList<IntrospectionUtils.PropertySource> sourcesList = new ArrayList<>();
        boolean systemPropertySourceFound = false;
        if (propertySources != null) {
            for (IntrospectionUtils.PropertySource source : propertySources) {
                if (source instanceof SystemPropertySource) {
                    systemPropertySourceFound = true;
                }
                sourcesList.add(source);
            }
        }
        if (!systemPropertySourceFound) {
            sourcesList.add(new SystemPropertySource());
        }
        this.source = (IntrospectionUtils.PropertySource[]) sourcesList.toArray(new IntrospectionUtils.PropertySource[0]);
    }

    public static void replaceSystemProperties() {
        Log log = LogFactory.getLog((Class<?>) Digester.class);
        if (propertySources != null) {
            Properties properties = System.getProperties();
            Set<String> names = properties.stringPropertyNames();
            for (String name : names) {
                String value = System.getProperty(name);
                if (value != null) {
                    try {
                        String newValue = IntrospectionUtils.replaceProperties(value, null, propertySources, null);
                        if (!value.equals(newValue)) {
                            System.setProperty(name, newValue);
                        }
                    } catch (Exception e) {
                        log.warn(sm.getString("digester.failedToUpdateSystemProperty", name, value), e);
                    }
                }
            }
        }
    }

    public void startGeneratingCode() {
        this.code = new StringBuilder();
    }

    public void endGeneratingCode() {
        this.code = null;
        this.known.clear();
    }

    public StringBuilder getGeneratedCode() {
        return this.code;
    }

    public void setKnown(Object object) {
        this.known.add(object);
    }

    public String toVariableName(Object object) {
        boolean found = false;
        int pos = 0;
        if (this.known.size() > 0) {
            int i = this.known.size() - 1;
            while (true) {
                if (i < 0) {
                    break;
                }
                if (this.known.get(i) != object) {
                    i--;
                } else {
                    pos = i;
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            pos = this.known.size();
            this.known.add(object);
        }
        return "tc_" + object.getClass().getSimpleName() + "_" + String.valueOf(pos);
    }

    public String findNamespaceURI(String prefix) {
        ArrayStack<String> stack = this.namespaces.get(prefix);
        if (stack == null) {
            return null;
        }
        try {
            return stack.peek();
        } catch (EmptyStackException e) {
            return null;
        }
    }

    public ClassLoader getClassLoader() {
        ClassLoader classLoader;
        if (this.classLoader != null) {
            return this.classLoader;
        }
        if (this.useContextClassLoader && (classLoader = Thread.currentThread().getContextClassLoader()) != null) {
            return classLoader;
        }
        return getClass().getClassLoader();
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public int getCount() {
        return this.stack.size();
    }

    public String getCurrentElementName() {
        String elementName = this.match;
        int lastSlash = elementName.lastIndexOf(47);
        if (lastSlash >= 0) {
            elementName = elementName.substring(lastSlash + 1);
        }
        return elementName;
    }

    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public SAXParserFactory getFactory() throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException {
        if (this.factory == null) {
            this.factory = SAXParserFactory.newInstance();
            this.factory.setNamespaceAware(this.namespaceAware);
            if (this.namespaceAware) {
                this.factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            }
            this.factory.setValidating(this.validating);
            if (this.validating) {
                this.factory.setFeature("http://xml.org/sax/features/validation", true);
                this.factory.setFeature("http://apache.org/xml/features/validation/schema", true);
            }
        }
        return this.factory;
    }

    public void setFeature(String feature, boolean value) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        getFactory().setFeature(feature, value);
    }

    public Log getLogger() {
        return this.log;
    }

    public void setLogger(Log log) {
        this.log = log;
    }

    public Log getSAXLogger() {
        return this.saxLog;
    }

    public void setSAXLogger(Log saxLog) {
        this.saxLog = saxLog;
    }

    public String getMatch() {
        return this.match;
    }

    public boolean getNamespaceAware() {
        return this.namespaceAware;
    }

    public void setNamespaceAware(boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public SAXParser getParser() {
        if (this.parser != null) {
            return this.parser;
        }
        try {
            this.parser = getFactory().newSAXParser();
            return this.parser;
        } catch (Exception e) {
            this.log.error(sm.getString("digester.createParserError"), e);
            return null;
        }
    }

    public Object getProperty(String property) throws SAXNotRecognizedException, SAXNotSupportedException {
        return getParser().getProperty(property);
    }

    public Rules getRules() {
        if (this.rules == null) {
            this.rules = new RulesBase();
            this.rules.setDigester(this);
        }
        return this.rules;
    }

    public void setRules(Rules rules) {
        this.rules = rules;
        this.rules.setDigester(this);
    }

    public boolean getUseContextClassLoader() {
        return this.useContextClassLoader;
    }

    public void setUseContextClassLoader(boolean use) {
        this.useContextClassLoader = use;
    }

    public boolean getValidating() {
        return this.validating;
    }

    public void setValidating(boolean validating) {
        this.validating = validating;
    }

    public boolean getRulesValidation() {
        return this.rulesValidation;
    }

    public void setRulesValidation(boolean rulesValidation) {
        this.rulesValidation = rulesValidation;
    }

    public Map<Class<?>, List<String>> getFakeAttributes() {
        return this.fakeAttributes;
    }

    public boolean isFakeAttribute(Object object, String name) {
        if (this.fakeAttributes == null) {
            return false;
        }
        List<String> result = this.fakeAttributes.get(object.getClass());
        if (result == null) {
            result = this.fakeAttributes.get(Object.class);
        }
        if (result == null) {
            return false;
        }
        return result.contains(name);
    }

    public void setFakeAttributes(Map<Class<?>, List<String>> fakeAttributes) {
        this.fakeAttributes = fakeAttributes;
    }

    public XMLReader getXMLReader() throws SAXException {
        EntityResolver entityResolver;
        if (this.reader == null) {
            this.reader = getParser().getXMLReader();
        }
        this.reader.setDTDHandler(this);
        this.reader.setContentHandler(this);
        EntityResolver entityResolver2 = getEntityResolver();
        if (entityResolver2 == null) {
            entityResolver2 = this;
        }
        if (entityResolver2 instanceof EntityResolver2) {
            entityResolver = new EntityResolver2Wrapper((EntityResolver2) entityResolver2, this.source, this.classLoader);
        } else {
            entityResolver = new EntityResolverWrapper(entityResolver2, this.source, this.classLoader);
        }
        this.reader.setEntityResolver(entityResolver);
        this.reader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
        this.reader.setErrorHandler(this);
        return this.reader;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void characters(char[] buffer, int start, int length) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("characters(" + new String(buffer, start, length) + ")");
        }
        this.bodyText.append(buffer, start, length);
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endDocument() throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            if (getCount() > 1) {
                this.saxLog.debug("endDocument():  " + getCount() + " elements left");
            } else {
                this.saxLog.debug("endDocument()");
            }
        }
        while (getCount() > 1) {
            pop();
        }
        for (Rule rule : getRules().rules()) {
            try {
                rule.finish();
            } catch (Error e) {
                this.log.error(sm.getString("digester.error.finish"), e);
                throw e;
            } catch (Exception e2) {
                this.log.error(sm.getString("digester.error.finish"), e2);
                throw createSAXException(e2);
            }
        }
        clear();
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        boolean debug = this.log.isDebugEnabled();
        if (debug) {
            if (this.saxLog.isDebugEnabled()) {
                this.saxLog.debug("endElement(" + namespaceURI + "," + localName + "," + qName + ")");
            }
            this.log.debug("  match='" + this.match + "'");
            this.log.debug("  bodyText='" + this.bodyText + "'");
        }
        this.bodyText = updateBodyText(this.bodyText);
        String name = localName;
        if (name == null || name.length() < 1) {
            name = qName;
        }
        List<Rule> rules = this.matches.pop();
        if (rules != null && rules.size() > 0) {
            String bodyText = this.bodyText.toString().intern();
            for (Rule value : rules) {
                if (debug) {
                    try {
                        this.log.debug("  Fire body() for " + value);
                    } catch (Error e) {
                        this.log.error(sm.getString("digester.error.body"), e);
                        throw e;
                    } catch (Exception e2) {
                        this.log.error(sm.getString("digester.error.body"), e2);
                        throw createSAXException(e2);
                    }
                }
                value.body(namespaceURI, name, bodyText);
            }
        } else {
            if (debug) {
                this.log.debug(sm.getString("digester.noRulesFound", this.match));
            }
            if (this.rulesValidation) {
                this.log.warn(sm.getString("digester.noRulesFound", this.match));
            }
        }
        this.bodyText = this.bodyTexts.pop();
        if (rules != null) {
            for (int i = 0; i < rules.size(); i++) {
                int j = (rules.size() - i) - 1;
                try {
                    Rule rule = rules.get(j);
                    if (debug) {
                        this.log.debug("  Fire end() for " + rule);
                    }
                    rule.end(namespaceURI, name);
                } catch (Error e3) {
                    this.log.error(sm.getString("digester.error.end"), e3);
                    throw e3;
                } catch (Exception e4) {
                    this.log.error(sm.getString("digester.error.end"), e4);
                    throw createSAXException(e4);
                }
            }
        }
        int slash = this.match.lastIndexOf(47);
        if (slash >= 0) {
            this.match = this.match.substring(0, slash);
        } else {
            this.match = "";
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endPrefixMapping(String prefix) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("endPrefixMapping(" + prefix + ")");
        }
        ArrayStack<String> stack = this.namespaces.get(prefix);
        if (stack == null) {
            return;
        }
        try {
            stack.pop();
            if (stack.empty()) {
                this.namespaces.remove(prefix);
            }
        } catch (EmptyStackException e) {
            throw createSAXException(sm.getString("digester.emptyStackError"));
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] buffer, int start, int len) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("ignorableWhitespace(" + new String(buffer, start, len) + ")");
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void processingInstruction(String target, String data) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("processingInstruction('" + target + "','" + data + "')");
        }
    }

    public Locator getDocumentLocator() {
        return this.locator;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator locator) {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("setDocumentLocator(" + locator + ")");
        }
        this.locator = locator;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void skippedEntity(String name) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("skippedEntity(" + name + ")");
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startDocument() throws SAXException {
        String enc;
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("startDocument()");
        }
        if ((this.locator instanceof Locator2) && (this.root instanceof DocumentProperties.Charset) && (enc = ((Locator2) this.locator).getEncoding()) != null) {
            try {
                ((DocumentProperties.Charset) this.root).setCharset(B2CConverter.getCharset(enc));
            } catch (UnsupportedEncodingException e) {
                this.log.warn(sm.getString("digester.encodingInvalid", enc), e);
            }
        }
        configure();
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String namespaceURI, String localName, String qName, Attributes list) throws SAXException {
        boolean debug = this.log.isDebugEnabled();
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("startElement(" + namespaceURI + "," + localName + "," + qName + ")");
        }
        Attributes list2 = updateAttributes(list);
        this.bodyTexts.push(this.bodyText);
        this.bodyText = new StringBuilder();
        String name = localName;
        if (name == null || name.length() < 1) {
            name = qName;
        }
        StringBuilder sb = new StringBuilder(this.match);
        if (this.match.length() > 0) {
            sb.append('/');
        }
        sb.append(name);
        this.match = sb.toString();
        if (debug) {
            this.log.debug("  New match='" + this.match + "'");
        }
        List<Rule> rules = getRules().match(namespaceURI, this.match);
        this.matches.push(rules);
        if (rules != null && rules.size() > 0) {
            for (Rule value : rules) {
                if (debug) {
                    try {
                        this.log.debug("  Fire begin() for " + value);
                    } catch (Error e) {
                        this.log.error(sm.getString("digester.error.begin"), e);
                        throw e;
                    } catch (Exception e2) {
                        this.log.error(sm.getString("digester.error.begin"), e2);
                        throw createSAXException(e2);
                    }
                }
                value.begin(namespaceURI, name, list2);
            }
            return;
        }
        if (debug) {
            this.log.debug(sm.getString("digester.noRulesFound", this.match));
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startPrefixMapping(String prefix, String namespaceURI) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("startPrefixMapping(" + prefix + "," + namespaceURI + ")");
        }
        ArrayStack<String> stack = this.namespaces.get(prefix);
        if (stack == null) {
            stack = new ArrayStack<>();
            this.namespaces.put(prefix, stack);
        }
        stack.push(namespaceURI);
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.DTDHandler
    public void notationDecl(String name, String publicId, String systemId) {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("notationDecl(" + name + "," + publicId + "," + systemId + ")");
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.DTDHandler
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notation) {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("unparsedEntityDecl(" + name + "," + publicId + "," + systemId + "," + notation + ")");
        }
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }

    @Override // org.xml.sax.ext.DefaultHandler2, org.xml.sax.ext.EntityResolver2
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("resolveEntity('" + publicId + "', '" + systemId + "', '" + baseURI + "')");
        }
        String entityURL = null;
        if (publicId != null) {
            entityURL = this.entityValidator.get(publicId);
        }
        if (entityURL == null) {
            if (systemId == null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug(" Cannot resolve entity: '" + publicId + "'");
                    return null;
                }
                return null;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug(" Trying to resolve using system ID '" + systemId + "'");
            }
            entityURL = systemId;
            if (baseURI != null) {
                try {
                    URI uri = new URI(systemId);
                    if (!uri.isAbsolute()) {
                        entityURL = new URI(baseURI).resolve(uri).toString();
                    }
                } catch (URISyntaxException e) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Invalid URI '" + baseURI + "' or '" + systemId + "'");
                    }
                }
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug(" Resolving to alternate DTD '" + entityURL + "'");
        }
        try {
            return new InputSource(entityURL);
        } catch (Exception e2) {
            throw createSAXException(e2);
        }
    }

    @Override // org.xml.sax.ext.DefaultHandler2, org.xml.sax.ext.LexicalHandler
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        setPublicId(publicId);
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ErrorHandler
    public void error(SAXParseException exception) throws SAXException {
        this.log.error(sm.getString("digester.parseError", Integer.valueOf(exception.getLineNumber()), Integer.valueOf(exception.getColumnNumber())), exception);
        if (this.errorHandler != null) {
            this.errorHandler.error(exception);
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ErrorHandler
    public void fatalError(SAXParseException exception) throws SAXException {
        this.log.error(sm.getString("digester.parseErrorFatal", Integer.valueOf(exception.getLineNumber()), Integer.valueOf(exception.getColumnNumber())), exception);
        if (this.errorHandler != null) {
            this.errorHandler.fatalError(exception);
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ErrorHandler
    public void warning(SAXParseException exception) throws SAXException {
        this.log.error(sm.getString("digester.parseWarning", Integer.valueOf(exception.getLineNumber()), Integer.valueOf(exception.getColumnNumber()), exception));
        if (this.errorHandler != null) {
            this.errorHandler.warning(exception);
        }
    }

    public Object parse(File file) throws IOException, SAXException {
        configure();
        InputSource input = new InputSource(new FileInputStream(file));
        input.setSystemId("file://" + file.getAbsolutePath());
        getXMLReader().parse(input);
        return this.root;
    }

    public Object parse(InputSource input) throws IOException, SAXException {
        configure();
        getXMLReader().parse(input);
        return this.root;
    }

    public Object parse(InputStream input) throws IOException, SAXException {
        configure();
        InputSource is = new InputSource(input);
        getXMLReader().parse(is);
        return this.root;
    }

    public void register(String publicId, String entityURL) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("register('" + publicId + "', '" + entityURL + "'");
        }
        this.entityValidator.put(publicId, entityURL);
    }

    public void addRule(String pattern, Rule rule) {
        rule.setDigester(this);
        getRules().add(pattern, rule);
    }

    public void addRuleSet(RuleSet ruleSet) {
        ruleSet.addRuleInstances(this);
    }

    public void addCallMethod(String pattern, String methodName) {
        addRule(pattern, new CallMethodRule(methodName));
    }

    public void addCallMethod(String pattern, String methodName, int paramCount) {
        addRule(pattern, new CallMethodRule(methodName, paramCount));
    }

    public void addCallParam(String pattern, int paramIndex) {
        addRule(pattern, new CallParamRule(paramIndex));
    }

    public void addFactoryCreate(String pattern, ObjectCreationFactory creationFactory, boolean ignoreCreateExceptions) {
        creationFactory.setDigester(this);
        addRule(pattern, new FactoryCreateRule(creationFactory, ignoreCreateExceptions));
    }

    public void addObjectCreate(String pattern, String className) {
        addRule(pattern, new ObjectCreateRule(className));
    }

    public void addObjectCreate(String pattern, String className, String attributeName) {
        addRule(pattern, new ObjectCreateRule(className, attributeName));
    }

    public void addSetNext(String pattern, String methodName, String paramType) {
        addRule(pattern, new SetNextRule(methodName, paramType));
    }

    public void addSetProperties(String pattern) {
        addRule(pattern, new SetPropertiesRule());
    }

    public void addSetProperties(String pattern, String[] excludes) {
        addRule(pattern, new SetPropertiesRule(excludes));
    }

    public void clear() {
        this.match = "";
        this.bodyTexts.clear();
        this.params.clear();
        this.publicId = null;
        this.stack.clear();
        this.log = null;
        this.saxLog = null;
        this.configured = false;
    }

    public void reset() {
        this.root = null;
        setErrorHandler(null);
        clear();
    }

    public Object peek() {
        try {
            return this.stack.peek();
        } catch (EmptyStackException e) {
            this.log.warn(sm.getString("digester.emptyStack"));
            return null;
        }
    }

    public Object peek(int n) {
        try {
            return this.stack.peek(n);
        } catch (EmptyStackException e) {
            this.log.warn(sm.getString("digester.emptyStack"));
            return null;
        }
    }

    public Object pop() {
        try {
            return this.stack.pop();
        } catch (EmptyStackException e) {
            this.log.warn(sm.getString("digester.emptyStack"));
            return null;
        }
    }

    public void push(Object object) {
        if (this.stack.size() == 0) {
            this.root = object;
        }
        this.stack.push(object);
    }

    public Object getRoot() {
        return this.root;
    }

    protected void configure() {
        if (this.configured) {
            return;
        }
        this.log = LogFactory.getLog("org.apache.tomcat.util.digester.Digester");
        this.saxLog = LogFactory.getLog("org.apache.tomcat.util.digester.Digester.sax");
        this.configured = true;
    }

    public Object peekParams() {
        try {
            return this.params.peek();
        } catch (EmptyStackException e) {
            this.log.warn(sm.getString("digester.emptyStack"));
            return null;
        }
    }

    public Object popParams() {
        try {
            if (this.log.isTraceEnabled()) {
                this.log.trace("Popping params");
            }
            return this.params.pop();
        } catch (EmptyStackException e) {
            this.log.warn(sm.getString("digester.emptyStack"));
            return null;
        }
    }

    public void pushParams(Object object) {
        if (this.log.isTraceEnabled()) {
            this.log.trace("Pushing params");
        }
        this.params.push(object);
    }

    public SAXException createSAXException(String message, Exception e) {
        if (e != null && (e instanceof InvocationTargetException)) {
            Throwable t = e.getCause();
            if (t instanceof ThreadDeath) {
                throw ((ThreadDeath) t);
            }
            if (t instanceof VirtualMachineError) {
                throw ((VirtualMachineError) t);
            }
            if (t instanceof Exception) {
                e = (Exception) t;
            }
        }
        if (this.locator != null) {
            String error = sm.getString("digester.errorLocation", Integer.valueOf(this.locator.getLineNumber()), Integer.valueOf(this.locator.getColumnNumber()), message);
            if (e != null) {
                return new SAXParseException(error, this.locator, e);
            }
            return new SAXParseException(error, this.locator);
        }
        this.log.error(sm.getString("digester.noLocator"));
        if (e != null) {
            return new SAXException(message, e);
        }
        return new SAXException(message);
    }

    public SAXException createSAXException(Exception e) {
        if (e instanceof InvocationTargetException) {
            Throwable t = e.getCause();
            if (t instanceof ThreadDeath) {
                throw ((ThreadDeath) t);
            }
            if (t instanceof VirtualMachineError) {
                throw ((VirtualMachineError) t);
            }
            if (t instanceof Exception) {
                e = (Exception) t;
            }
        }
        return createSAXException(e.getMessage(), e);
    }

    public SAXException createSAXException(String message) {
        return createSAXException(message, null);
    }

    private Attributes updateAttributes(Attributes list) {
        if (list.getLength() == 0) {
            return list;
        }
        AttributesImpl newAttrs = new AttributesImpl(list);
        int nAttributes = newAttrs.getLength();
        for (int i = 0; i < nAttributes; i++) {
            String value = newAttrs.getValue(i);
            try {
                newAttrs.setValue(i, IntrospectionUtils.replaceProperties(value, null, this.source, getClassLoader()).intern());
            } catch (Exception e) {
                this.log.warn(sm.getString("digester.failedToUpdateAttributes", newAttrs.getLocalName(i), value), e);
            }
        }
        return newAttrs;
    }

    private StringBuilder updateBodyText(StringBuilder bodyText) {
        String in = bodyText.toString();
        try {
            String out = IntrospectionUtils.replaceProperties(in, null, this.source, getClassLoader());
            if (out == in) {
                return bodyText;
            }
            return new StringBuilder(out);
        } catch (Exception e) {
            return bodyText;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/digester/Digester$EntityResolverWrapper.class */
    public static class EntityResolverWrapper implements EntityResolver {
        private final EntityResolver entityResolver;
        private final IntrospectionUtils.PropertySource[] source;
        private final ClassLoader classLoader;

        EntityResolverWrapper(EntityResolver entityResolver, IntrospectionUtils.PropertySource[] source, ClassLoader classLoader) {
            this.entityResolver = entityResolver;
            this.source = source;
            this.classLoader = classLoader;
        }

        @Override // org.xml.sax.EntityResolver
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return this.entityResolver.resolveEntity(replace(publicId), replace(systemId));
        }

        protected String replace(String input) {
            try {
                return IntrospectionUtils.replaceProperties(input, null, this.source, this.classLoader);
            } catch (Exception e) {
                return input;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/digester/Digester$EntityResolver2Wrapper.class */
    public static class EntityResolver2Wrapper extends EntityResolverWrapper implements EntityResolver2 {
        private final EntityResolver2 entityResolver2;

        EntityResolver2Wrapper(EntityResolver2 entityResolver, IntrospectionUtils.PropertySource[] source, ClassLoader classLoader) {
            super(entityResolver, source, classLoader);
            this.entityResolver2 = entityResolver;
        }

        @Override // org.xml.sax.ext.EntityResolver2
        public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
            return this.entityResolver2.getExternalSubset(replace(name), replace(baseURI));
        }

        @Override // org.xml.sax.ext.EntityResolver2
        public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
            return this.entityResolver2.resolveEntity(replace(name), replace(publicId), replace(baseURI), replace(systemId));
        }
    }
}
