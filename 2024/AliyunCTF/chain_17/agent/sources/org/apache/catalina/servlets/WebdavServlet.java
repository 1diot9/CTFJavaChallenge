package org.apache.catalina.servlets;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.text.StrPool;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.catalina.WebResource;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.util.DOMWriter;
import org.apache.catalina.util.XMLWriter;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.http.ConcurrentDateFormat;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.RequestUtil;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.tags.BindTag;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/servlets/WebdavServlet.class */
public class WebdavServlet extends DefaultServlet {
    private static final long serialVersionUID = 1;
    private static final String METHOD_PROPFIND = "PROPFIND";
    private static final String METHOD_PROPPATCH = "PROPPATCH";
    private static final String METHOD_MKCOL = "MKCOL";
    private static final String METHOD_COPY = "COPY";
    private static final String METHOD_MOVE = "MOVE";
    private static final String METHOD_LOCK = "LOCK";
    private static final String METHOD_UNLOCK = "UNLOCK";
    private static final int FIND_BY_PROPERTY = 0;
    private static final int FIND_ALL_PROP = 1;
    private static final int FIND_PROPERTY_NAMES = 2;
    private static final int LOCK_CREATION = 0;
    private static final int LOCK_REFRESH = 1;
    private static final int DEFAULT_TIMEOUT = 3600;
    private static final int MAX_TIMEOUT = 604800;
    protected static final String DEFAULT_NAMESPACE = "DAV:";
    protected static final ConcurrentDateFormat creationDateFormat = new ConcurrentDateFormat(DatePattern.UTC_PATTERN, Locale.US, TimeZone.getTimeZone("GMT"));
    private final Map<String, LockInfo> resourceLocks = new ConcurrentHashMap();
    private final Map<String, List<String>> lockNullResources = new ConcurrentHashMap();
    private final List<LockInfo> collectionLocks = Collections.synchronizedList(new ArrayList());
    private String secret = "catalina";
    private int maxDepth = 3;
    private boolean allowSpecialPaths = false;

    @Override // org.apache.catalina.servlets.DefaultServlet, jakarta.servlet.GenericServlet
    public void init() throws ServletException {
        super.init();
        if (getServletConfig().getInitParameter("secret") != null) {
            this.secret = getServletConfig().getInitParameter("secret");
        }
        if (getServletConfig().getInitParameter("maxDepth") != null) {
            this.maxDepth = Integer.parseInt(getServletConfig().getInitParameter("maxDepth"));
        }
        if (getServletConfig().getInitParameter("allowSpecialPaths") != null) {
            this.allowSpecialPaths = Boolean.parseBoolean(getServletConfig().getInitParameter("allowSpecialPaths"));
        }
    }

    protected DocumentBuilder getDocumentBuilder() throws ServletException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setExpandEntityReferences(false);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver(new WebdavResolver(getServletContext()));
            return documentBuilder;
        } catch (ParserConfigurationException e) {
            throw new ServletException(sm.getString("webdavservlet.jaxpfailed"));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.servlets.DefaultServlet, jakarta.servlet.http.HttpServlet
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = getRelativePath(req);
        if (req.getDispatcherType() == DispatcherType.ERROR) {
            doGet(req, resp);
            return;
        }
        if (isSpecialPath(path)) {
            resp.sendError(404);
            return;
        }
        String method = req.getMethod();
        if (this.debug > 0) {
            log("[" + method + "] " + path);
        }
        if (method.equals(METHOD_PROPFIND)) {
            doPropfind(req, resp);
            return;
        }
        if (method.equals(METHOD_PROPPATCH)) {
            doProppatch(req, resp);
            return;
        }
        if (method.equals(METHOD_MKCOL)) {
            doMkcol(req, resp);
            return;
        }
        if (method.equals(METHOD_COPY)) {
            doCopy(req, resp);
            return;
        }
        if (method.equals(METHOD_MOVE)) {
            doMove(req, resp);
            return;
        }
        if (method.equals(METHOD_LOCK)) {
            doLock(req, resp);
        } else if (method.equals(METHOD_UNLOCK)) {
            doUnlock(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    private boolean isSpecialPath(String path) {
        return !this.allowSpecialPaths && (path.toUpperCase(Locale.ENGLISH).startsWith("/WEB-INF") || path.toUpperCase(Locale.ENGLISH).startsWith("/META-INF"));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.servlets.DefaultServlet
    public boolean checkIfHeaders(HttpServletRequest request, HttpServletResponse response, WebResource resource) throws IOException {
        if (!super.checkIfHeaders(request, response, resource)) {
            return false;
        }
        return true;
    }

    @Override // org.apache.catalina.servlets.DefaultServlet
    protected String getRelativePath(HttpServletRequest request) {
        return getRelativePath(request, false);
    }

    @Override // org.apache.catalina.servlets.DefaultServlet
    protected String getRelativePath(HttpServletRequest request, boolean allowEmptyPath) {
        String pathInfo;
        if (request.getAttribute("jakarta.servlet.include.request_uri") != null) {
            pathInfo = (String) request.getAttribute("jakarta.servlet.include.path_info");
        } else {
            pathInfo = request.getPathInfo();
        }
        StringBuilder result = new StringBuilder();
        if (pathInfo != null) {
            result.append(pathInfo);
        }
        if (result.length() == 0) {
            result.append('/');
        }
        return result.toString();
    }

    @Override // org.apache.catalina.servlets.DefaultServlet
    protected String getPathPrefix(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        if (request.getServletPath() != null) {
            contextPath = contextPath + request.getServletPath();
        }
        return contextPath;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.servlets.DefaultServlet, jakarta.servlet.http.HttpServlet
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("DAV", "1,2");
        resp.addHeader(HttpHeaders.ALLOW, determineMethodsAllowed(req));
        resp.addHeader("MS-Author-Via", "DAV");
    }

    protected void doPropfind(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int slash;
        String propertyName;
        if (!this.listings) {
            sendNotAllowed(req, resp);
            return;
        }
        String path = getRelativePath(req);
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        List<String> properties = null;
        int depth = this.maxDepth;
        int type = 1;
        String depthStr = req.getHeader("Depth");
        if (depthStr == null) {
            depth = this.maxDepth;
        } else if (depthStr.equals(CustomBooleanEditor.VALUE_0)) {
            depth = 0;
        } else if (depthStr.equals(CustomBooleanEditor.VALUE_1)) {
            depth = 1;
        } else if (depthStr.equals("infinity")) {
            depth = this.maxDepth;
        }
        Node propNode = null;
        if (req.getContentLengthLong() > 0) {
            DocumentBuilder documentBuilder = getDocumentBuilder();
            try {
                Document document = documentBuilder.parse(new InputSource(req.getInputStream()));
                Element rootElement = document.getDocumentElement();
                NodeList childList = rootElement.getChildNodes();
                for (int i = 0; i < childList.getLength(); i++) {
                    Node currentNode = childList.item(i);
                    switch (currentNode.getNodeType()) {
                        case 1:
                            if (currentNode.getNodeName().endsWith(BeanDefinitionParserDelegate.PROP_ELEMENT)) {
                                type = 0;
                                propNode = currentNode;
                            }
                            if (currentNode.getNodeName().endsWith("propname")) {
                                type = 2;
                            }
                            if (currentNode.getNodeName().endsWith("allprop")) {
                                type = 1;
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            break;
                    }
                }
            } catch (IOException | SAXException e) {
                resp.sendError(400);
                return;
            }
        }
        if (type == 0) {
            properties = new ArrayList<>();
            NodeList childList2 = propNode.getChildNodes();
            for (int i2 = 0; i2 < childList2.getLength(); i2++) {
                Node currentNode2 = childList2.item(i2);
                switch (currentNode2.getNodeType()) {
                    case 1:
                        String nodeName = currentNode2.getNodeName();
                        if (nodeName.indexOf(58) != -1) {
                            propertyName = nodeName.substring(nodeName.indexOf(58) + 1);
                        } else {
                            propertyName = nodeName;
                        }
                        properties.add(propertyName);
                        break;
                }
            }
        }
        WebResource resource = this.resources.getResource(path);
        if (!resource.exists() && (slash = path.lastIndexOf(47)) != -1) {
            String parentPath = path.substring(0, slash);
            List<String> currentLockNullResources = this.lockNullResources.get(parentPath);
            if (currentLockNullResources != null) {
                for (String lockNullPath : currentLockNullResources) {
                    if (lockNullPath.equals(path)) {
                        resp.setStatus(WebdavStatus.SC_MULTI_STATUS);
                        resp.setContentType("text/xml; charset=UTF-8");
                        XMLWriter generatedXML = new XMLWriter(resp.getWriter());
                        generatedXML.writeXMLHeader();
                        generatedXML.writeElement("D", DEFAULT_NAMESPACE, "multistatus", 0);
                        parseLockNullProperties(req, generatedXML, lockNullPath, type, properties);
                        generatedXML.writeElement("D", "multistatus", 1);
                        generatedXML.sendData();
                        return;
                    }
                }
            }
        }
        if (!resource.exists()) {
            resp.sendError(404);
            return;
        }
        resp.setStatus(WebdavStatus.SC_MULTI_STATUS);
        resp.setContentType("text/xml; charset=UTF-8");
        XMLWriter generatedXML2 = new XMLWriter(resp.getWriter());
        generatedXML2.writeXMLHeader();
        generatedXML2.writeElement("D", DEFAULT_NAMESPACE, "multistatus", 0);
        if (depth == 0) {
            parseProperties(req, generatedXML2, path, type, properties);
        } else {
            Deque<String> stack = new ArrayDeque<>();
            stack.addFirst(path);
            Deque<String> stackBelow = new ArrayDeque<>();
            while (!stack.isEmpty() && depth >= 0) {
                String currentPath = stack.remove();
                parseProperties(req, generatedXML2, currentPath, type, properties);
                if (this.resources.getResource(currentPath).isDirectory() && depth > 0) {
                    String[] entries = this.resources.list(currentPath);
                    for (String entry : entries) {
                        String newPath = currentPath;
                        if (!newPath.endsWith("/")) {
                            newPath = newPath + "/";
                        }
                        stackBelow.addFirst(newPath + entry);
                    }
                    String lockPath = currentPath;
                    if (lockPath.endsWith("/")) {
                        lockPath = lockPath.substring(0, lockPath.length() - 1);
                    }
                    List<String> currentLockNullResources2 = this.lockNullResources.get(lockPath);
                    if (currentLockNullResources2 != null) {
                        Iterator<String> it = currentLockNullResources2.iterator();
                        while (it.hasNext()) {
                            parseLockNullProperties(req, generatedXML2, it.next(), type, properties);
                        }
                    }
                }
                if (stack.isEmpty()) {
                    depth--;
                    stack = stackBelow;
                    stackBelow = new ArrayDeque<>();
                }
                generatedXML2.sendData();
            }
        }
        generatedXML2.writeElement("D", "multistatus", 1);
        generatedXML2.sendData();
    }

    protected void doProppatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (this.readOnly) {
            resp.sendError(403);
        } else if (isLocked(req)) {
            resp.sendError(WebdavStatus.SC_LOCKED);
        } else {
            resp.sendError(501);
        }
    }

    protected void doMkcol(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = getRelativePath(req);
        WebResource resource = this.resources.getResource(path);
        if (resource.exists()) {
            sendNotAllowed(req, resp);
            return;
        }
        if (this.readOnly) {
            resp.sendError(403);
            return;
        }
        if (isLocked(req)) {
            resp.sendError(WebdavStatus.SC_LOCKED);
            return;
        }
        if (req.getContentLengthLong() > 0) {
            DocumentBuilder documentBuilder = getDocumentBuilder();
            try {
                documentBuilder.parse(new InputSource(req.getInputStream()));
                resp.sendError(501);
                return;
            } catch (SAXException e) {
                resp.sendError(415);
                return;
            }
        }
        if (this.resources.mkdir(path)) {
            resp.setStatus(201);
            this.lockNullResources.remove(path);
        } else {
            resp.sendError(409);
        }
    }

    @Override // org.apache.catalina.servlets.DefaultServlet, jakarta.servlet.http.HttpServlet
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (this.readOnly) {
            sendNotAllowed(req, resp);
        } else if (isLocked(req)) {
            resp.sendError(WebdavStatus.SC_LOCKED);
        } else {
            deleteResource(req, resp);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.servlets.DefaultServlet, jakarta.servlet.http.HttpServlet
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (isLocked(req)) {
            resp.sendError(WebdavStatus.SC_LOCKED);
            return;
        }
        String path = getRelativePath(req);
        WebResource resource = this.resources.getResource(path);
        if (resource.isDirectory()) {
            sendNotAllowed(req, resp);
        } else {
            super.doPut(req, resp);
            this.lockNullResources.remove(path);
        }
    }

    protected void doCopy(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (this.readOnly) {
            resp.sendError(403);
        } else {
            copyResource(req, resp);
        }
    }

    protected void doMove(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (this.readOnly) {
            resp.sendError(403);
            return;
        }
        if (isLocked(req)) {
            resp.sendError(WebdavStatus.SC_LOCKED);
            return;
        }
        String path = getRelativePath(req);
        if (copyResource(req, resp)) {
            deleteResource(path, req, resp, false);
        }
    }

    /* JADX WARN: Type inference failed for: r0v91, types: [byte[], byte[][]] */
    protected void doLock(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (this.readOnly) {
            resp.sendError(403);
            return;
        }
        if (isLocked(req)) {
            resp.sendError(WebdavStatus.SC_LOCKED);
            return;
        }
        LockInfo lock = new LockInfo(this.maxDepth);
        String depthStr = req.getHeader("Depth");
        if (depthStr == null) {
            lock.depth = this.maxDepth;
        } else if (depthStr.equals(CustomBooleanEditor.VALUE_0)) {
            lock.depth = 0;
        } else {
            lock.depth = this.maxDepth;
        }
        int lockDuration = DEFAULT_TIMEOUT;
        String lockDurationStr = req.getHeader("Timeout");
        if (lockDurationStr != null) {
            int commaPos = lockDurationStr.indexOf(44);
            if (commaPos != -1) {
                lockDurationStr = lockDurationStr.substring(0, commaPos);
            }
            if (lockDurationStr.startsWith("Second-")) {
                lockDuration = Integer.parseInt(lockDurationStr.substring(7));
            } else if (lockDurationStr.equalsIgnoreCase("infinity")) {
                lockDuration = MAX_TIMEOUT;
            } else {
                try {
                    lockDuration = Integer.parseInt(lockDurationStr);
                } catch (NumberFormatException e) {
                    lockDuration = MAX_TIMEOUT;
                }
            }
            if (lockDuration == 0) {
                lockDuration = DEFAULT_TIMEOUT;
            }
            if (lockDuration > MAX_TIMEOUT) {
                lockDuration = MAX_TIMEOUT;
            }
        }
        lock.expiresAt = System.currentTimeMillis() + (lockDuration * 1000);
        int lockRequestType = 0;
        Node lockInfoNode = null;
        DocumentBuilder documentBuilder = getDocumentBuilder();
        try {
            Document document = documentBuilder.parse(new InputSource(req.getInputStream()));
            Node rootElement = document.getDocumentElement();
            lockInfoNode = rootElement;
        } catch (IOException | SAXException e2) {
            lockRequestType = 1;
        }
        if (lockInfoNode != null) {
            NodeList childList = lockInfoNode.getChildNodes();
            Node lockScopeNode = null;
            Node lockTypeNode = null;
            Node lockOwnerNode = null;
            for (int i = 0; i < childList.getLength(); i++) {
                Node currentNode = childList.item(i);
                switch (currentNode.getNodeType()) {
                    case 1:
                        String nodeName = currentNode.getNodeName();
                        if (nodeName.endsWith("lockscope")) {
                            lockScopeNode = currentNode;
                        }
                        if (nodeName.endsWith("locktype")) {
                            lockTypeNode = currentNode;
                        }
                        if (nodeName.endsWith("owner")) {
                            lockOwnerNode = currentNode;
                            break;
                        } else {
                            break;
                        }
                }
            }
            if (lockScopeNode != null) {
                NodeList childList2 = lockScopeNode.getChildNodes();
                for (int i2 = 0; i2 < childList2.getLength(); i2++) {
                    Node currentNode2 = childList2.item(i2);
                    switch (currentNode2.getNodeType()) {
                        case 1:
                            String tempScope = currentNode2.getNodeName();
                            if (tempScope.indexOf(58) != -1) {
                                lock.scope = tempScope.substring(tempScope.indexOf(58) + 1);
                                break;
                            } else {
                                lock.scope = tempScope;
                                break;
                            }
                    }
                }
                if (lock.scope == null) {
                    resp.setStatus(400);
                }
            } else {
                resp.setStatus(400);
            }
            if (lockTypeNode != null) {
                NodeList childList3 = lockTypeNode.getChildNodes();
                for (int i3 = 0; i3 < childList3.getLength(); i3++) {
                    Node currentNode3 = childList3.item(i3);
                    switch (currentNode3.getNodeType()) {
                        case 1:
                            String tempType = currentNode3.getNodeName();
                            if (tempType.indexOf(58) != -1) {
                                lock.type = tempType.substring(tempType.indexOf(58) + 1);
                                break;
                            } else {
                                lock.type = tempType;
                                break;
                            }
                    }
                }
                if (lock.type == null) {
                    resp.setStatus(400);
                }
            } else {
                resp.setStatus(400);
            }
            if (lockOwnerNode != null) {
                NodeList childList4 = lockOwnerNode.getChildNodes();
                for (int i4 = 0; i4 < childList4.getLength(); i4++) {
                    Node currentNode4 = childList4.item(i4);
                    switch (currentNode4.getNodeType()) {
                        case 1:
                            StringWriter strWriter = new StringWriter();
                            DOMWriter domWriter = new DOMWriter(strWriter);
                            domWriter.print(currentNode4);
                            lock.owner += strWriter.toString();
                            break;
                        case 3:
                            lock.owner += currentNode4.getNodeValue();
                            break;
                    }
                }
                if (lock.owner == null) {
                    resp.setStatus(400);
                }
            } else {
                lock.owner = "";
            }
        }
        String path = getRelativePath(req);
        lock.path = path;
        WebResource resource = this.resources.getResource(path);
        if (lockRequestType == 0) {
            String servletPath = req.getServletPath();
            String str = lock.type;
            String str2 = lock.scope;
            Principal userPrincipal = req.getUserPrincipal();
            int i5 = lock.depth;
            String str3 = lock.owner;
            List<String> list = lock.tokens;
            long j = lock.expiresAt;
            long currentTimeMillis = System.currentTimeMillis();
            String str4 = this.secret;
            String lockTokenStr = servletPath + "-" + str + "-" + str2 + "-" + userPrincipal + "-" + i5 + "-" + str3 + "-" + list + "-" + j + "-" + servletPath + "-" + currentTimeMillis;
            String lockToken = HexUtils.toHexString(ConcurrentMessageDigest.digestMD5(new byte[]{lockTokenStr.getBytes(StandardCharsets.ISO_8859_1)}));
            if (resource.isDirectory() && lock.depth == this.maxDepth) {
                List<String> lockPaths = new ArrayList<>();
                Iterator<LockInfo> collectionLocksIterator = this.collectionLocks.iterator();
                while (collectionLocksIterator.hasNext()) {
                    LockInfo currentLock = collectionLocksIterator.next();
                    if (currentLock.hasExpired()) {
                        collectionLocksIterator.remove();
                    } else if (currentLock.path.startsWith(lock.path) && (currentLock.isExclusive() || lock.isExclusive())) {
                        lockPaths.add(currentLock.path);
                    }
                }
                for (LockInfo currentLock2 : this.resourceLocks.values()) {
                    if (currentLock2.hasExpired()) {
                        this.resourceLocks.remove(currentLock2.path);
                    } else if (currentLock2.path.startsWith(lock.path) && (currentLock2.isExclusive() || lock.isExclusive())) {
                        lockPaths.add(currentLock2.path);
                    }
                }
                if (!lockPaths.isEmpty()) {
                    resp.setStatus(409);
                    XMLWriter generatedXML = new XMLWriter();
                    generatedXML.writeXMLHeader();
                    generatedXML.writeElement("D", DEFAULT_NAMESPACE, "multistatus", 0);
                    for (String lockPath : lockPaths) {
                        generatedXML.writeElement("D", "response", 0);
                        generatedXML.writeElement("D", "href", 0);
                        generatedXML.writeText(lockPath);
                        generatedXML.writeElement("D", "href", 1);
                        generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 0);
                        generatedXML.writeText("HTTP/1.1 423 ");
                        generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 1);
                        generatedXML.writeElement("D", "response", 1);
                    }
                    generatedXML.writeElement("D", "multistatus", 1);
                    Writer writer = resp.getWriter();
                    writer.write(generatedXML.toString());
                    writer.close();
                    return;
                }
                boolean addLock = true;
                for (LockInfo currentLock3 : this.collectionLocks) {
                    if (currentLock3.path.equals(lock.path)) {
                        if (currentLock3.isExclusive()) {
                            resp.sendError(WebdavStatus.SC_LOCKED);
                            return;
                        } else if (lock.isExclusive()) {
                            resp.sendError(WebdavStatus.SC_LOCKED);
                            return;
                        } else {
                            currentLock3.tokens.add(lockToken);
                            lock = currentLock3;
                            addLock = false;
                        }
                    }
                }
                if (addLock) {
                    lock.tokens.add(lockToken);
                    this.collectionLocks.add(lock);
                }
            } else {
                LockInfo presentLock = this.resourceLocks.get(lock.path);
                if (presentLock != null) {
                    if (presentLock.isExclusive() || lock.isExclusive()) {
                        resp.sendError(412);
                        return;
                    } else {
                        presentLock.tokens.add(lockToken);
                        lock = presentLock;
                    }
                } else {
                    lock.tokens.add(lockToken);
                    this.resourceLocks.put(lock.path, lock);
                    if (!resource.exists()) {
                        int slash = lock.path.lastIndexOf(47);
                        String parentPath = lock.path.substring(0, slash);
                        this.lockNullResources.computeIfAbsent(parentPath, k -> {
                            return new ArrayList();
                        }).add(lock.path);
                    }
                    resp.addHeader("Lock-Token", "<opaquelocktoken:" + lockToken + ">");
                }
            }
        }
        if (lockRequestType == 1) {
            String ifHeader = req.getHeader("If");
            if (ifHeader == null) {
                ifHeader = "";
            }
            LockInfo toRenew = this.resourceLocks.get(path);
            if (toRenew != null) {
                for (String token : toRenew.tokens) {
                    if (ifHeader.contains(token)) {
                        toRenew.expiresAt = lock.expiresAt;
                        lock = toRenew;
                    }
                }
            }
            for (LockInfo collecionLock : this.collectionLocks) {
                if (path.equals(collecionLock.path)) {
                    for (String token2 : collecionLock.tokens) {
                        if (ifHeader.contains(token2)) {
                            collecionLock.expiresAt = lock.expiresAt;
                            lock = collecionLock;
                        }
                    }
                }
            }
        }
        XMLWriter generatedXML2 = new XMLWriter();
        generatedXML2.writeXMLHeader();
        generatedXML2.writeElement("D", DEFAULT_NAMESPACE, BeanDefinitionParserDelegate.PROP_ELEMENT, 0);
        generatedXML2.writeElement("D", "lockdiscovery", 0);
        lock.toXML(generatedXML2);
        generatedXML2.writeElement("D", "lockdiscovery", 1);
        generatedXML2.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 1);
        resp.setStatus(200);
        resp.setContentType("text/xml; charset=UTF-8");
        Writer writer2 = resp.getWriter();
        writer2.write(generatedXML2.toString());
        writer2.close();
    }

    protected void doUnlock(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (this.readOnly) {
            resp.sendError(403);
            return;
        }
        if (isLocked(req)) {
            resp.sendError(WebdavStatus.SC_LOCKED);
            return;
        }
        String path = getRelativePath(req);
        String lockTokenHeader = req.getHeader("Lock-Token");
        if (lockTokenHeader == null) {
            lockTokenHeader = "";
        }
        LockInfo lock = this.resourceLocks.get(path);
        if (lock != null) {
            Iterator<String> tokenList = lock.tokens.iterator();
            while (tokenList.hasNext()) {
                String token = tokenList.next();
                if (lockTokenHeader.contains(token)) {
                    tokenList.remove();
                }
            }
            if (lock.tokens.isEmpty()) {
                this.resourceLocks.remove(path);
                this.lockNullResources.remove(path);
            }
        }
        Iterator<LockInfo> collectionLocksList = this.collectionLocks.iterator();
        while (collectionLocksList.hasNext()) {
            LockInfo lock2 = collectionLocksList.next();
            if (path.equals(lock2.path)) {
                Iterator<String> tokenList2 = lock2.tokens.iterator();
                while (true) {
                    if (!tokenList2.hasNext()) {
                        break;
                    }
                    String token2 = tokenList2.next();
                    if (lockTokenHeader.contains(token2)) {
                        tokenList2.remove();
                        break;
                    }
                }
                if (lock2.tokens.isEmpty()) {
                    collectionLocksList.remove();
                    this.lockNullResources.remove(path);
                }
            }
        }
        resp.setStatus(204);
    }

    private boolean isLocked(HttpServletRequest req) {
        String path = getRelativePath(req);
        String ifHeader = req.getHeader("If");
        if (ifHeader == null) {
            ifHeader = "";
        }
        String lockTokenHeader = req.getHeader("Lock-Token");
        if (lockTokenHeader == null) {
            lockTokenHeader = "";
        }
        return isLocked(path, ifHeader + lockTokenHeader);
    }

    private boolean isLocked(String path, String ifHeader) {
        LockInfo lock = this.resourceLocks.get(path);
        if (lock != null && lock.hasExpired()) {
            this.resourceLocks.remove(path);
        } else if (lock != null) {
            boolean tokenMatch = false;
            Iterator<String> it = lock.tokens.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                String token = it.next();
                if (ifHeader.contains(token)) {
                    tokenMatch = true;
                    break;
                }
            }
            if (!tokenMatch) {
                return true;
            }
        }
        Iterator<LockInfo> collectionLockList = this.collectionLocks.iterator();
        while (collectionLockList.hasNext()) {
            LockInfo lock2 = collectionLockList.next();
            if (lock2.hasExpired()) {
                collectionLockList.remove();
            } else if (path.startsWith(lock2.path)) {
                boolean tokenMatch2 = false;
                Iterator<String> it2 = lock2.tokens.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    String token2 = it2.next();
                    if (ifHeader.contains(token2)) {
                        tokenMatch2 = true;
                        break;
                    }
                }
                if (!tokenMatch2) {
                    return true;
                }
            } else {
                continue;
            }
        }
        return false;
    }

    private boolean copyResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = getRelativePath(req);
        WebResource source = this.resources.getResource(path);
        if (!source.exists()) {
            resp.sendError(404);
            return false;
        }
        String destinationHeader = req.getHeader("Destination");
        if (destinationHeader == null || destinationHeader.isEmpty()) {
            resp.sendError(400);
            return false;
        }
        try {
            URI destinationUri = new URI(destinationHeader);
            String destinationPath = destinationUri.getPath();
            if (!destinationPath.equals(RequestUtil.normalize(destinationPath))) {
                resp.sendError(400);
                return false;
            }
            if (destinationUri.isAbsolute()) {
                if (!req.getScheme().equals(destinationUri.getScheme()) || !req.getServerName().equals(destinationUri.getHost())) {
                    resp.sendError(403);
                    return false;
                }
                if (req.getServerPort() != destinationUri.getPort() && (destinationUri.getPort() != -1 || ((!"http".equals(req.getScheme()) || req.getServerPort() != 80) && (!"https".equals(req.getScheme()) || req.getServerPort() != 443)))) {
                    resp.sendError(403);
                    return false;
                }
            }
            String reqContextPath = req.getContextPath();
            if (!destinationPath.startsWith(reqContextPath + "/")) {
                resp.sendError(403);
                return false;
            }
            String destinationPath2 = destinationPath.substring(reqContextPath.length() + req.getServletPath().length());
            if (this.debug > 0) {
                log("Dest path :" + destinationPath2);
            }
            if (isSpecialPath(destinationPath2)) {
                resp.sendError(403);
                return false;
            }
            if (destinationPath2.equals(path)) {
                resp.sendError(403);
                return false;
            }
            if ((destinationPath2.startsWith(path) && destinationPath2.charAt(path.length()) == '/') || (path.startsWith(destinationPath2) && path.charAt(destinationPath2.length()) == '/')) {
                resp.sendError(403);
                return false;
            }
            boolean overwrite = true;
            String overwriteHeader = req.getHeader("Overwrite");
            if (overwriteHeader != null) {
                if (overwriteHeader.equalsIgnoreCase("T")) {
                    overwrite = true;
                } else {
                    overwrite = false;
                }
            }
            WebResource destination = this.resources.getResource(destinationPath2);
            if (overwrite) {
                if (destination.exists()) {
                    if (!deleteResource(destinationPath2, req, resp, true)) {
                        return false;
                    }
                } else {
                    resp.setStatus(201);
                }
            } else if (destination.exists()) {
                resp.sendError(412);
                return false;
            }
            Map<String, Integer> errorList = new HashMap<>();
            boolean result = copyResource(errorList, path, destinationPath2);
            if (!result || !errorList.isEmpty()) {
                if (errorList.size() == 1) {
                    resp.sendError(errorList.values().iterator().next().intValue());
                    return false;
                }
                sendReport(req, resp, errorList);
                return false;
            }
            if (destination.exists()) {
                resp.setStatus(204);
            } else {
                resp.setStatus(201);
            }
            this.lockNullResources.remove(destinationPath2);
            return true;
        } catch (URISyntaxException e) {
            resp.sendError(400);
            return false;
        }
    }

    private boolean copyResource(Map<String, Integer> errorList, String source, String dest) {
        int lastSlash;
        if (this.debug > 1) {
            log("Copy: " + source + " To: " + dest);
        }
        WebResource sourceResource = this.resources.getResource(source);
        if (sourceResource.isDirectory()) {
            if (!this.resources.mkdir(dest) && !this.resources.getResource(dest).isDirectory()) {
                errorList.put(dest, 409);
                return false;
            }
            String[] entries = this.resources.list(source);
            for (String entry : entries) {
                String childDest = dest;
                if (!childDest.equals("/")) {
                    childDest = childDest + "/";
                }
                String childDest2 = childDest + entry;
                String childSrc = source;
                if (!childSrc.equals("/")) {
                    childSrc = childSrc + "/";
                }
                copyResource(errorList, childSrc + entry, childDest2);
            }
            return true;
        }
        if (sourceResource.isFile()) {
            WebResource destResource = this.resources.getResource(dest);
            if (!destResource.exists() && !destResource.getWebappPath().endsWith("/") && (lastSlash = destResource.getWebappPath().lastIndexOf(47)) > 0) {
                String parent = destResource.getWebappPath().substring(0, lastSlash);
                WebResource parentResource = this.resources.getResource(parent);
                if (!parentResource.isDirectory()) {
                    errorList.put(source, 409);
                    return false;
                }
            }
            if (!destResource.exists() && dest.endsWith("/") && dest.length() > 1) {
                dest = dest.substring(0, dest.length() - 1);
            }
            try {
                InputStream is = sourceResource.getInputStream();
                try {
                    if (!this.resources.write(dest, is, false)) {
                        errorList.put(source, 500);
                        if (is != null) {
                            is.close();
                        }
                        return false;
                    }
                    if (is != null) {
                        is.close();
                    }
                    return true;
                } finally {
                }
            } catch (IOException e) {
                log(sm.getString("webdavservlet.inputstreamclosefail", source), e);
                return true;
            }
        }
        errorList.put(source, 500);
        return false;
    }

    private boolean deleteResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = getRelativePath(req);
        return deleteResource(path, req, resp, true);
    }

    private boolean deleteResource(String path, HttpServletRequest req, HttpServletResponse resp, boolean setStatus) throws IOException {
        String ifHeader = req.getHeader("If");
        if (ifHeader == null) {
            ifHeader = "";
        }
        String lockTokenHeader = req.getHeader("Lock-Token");
        if (lockTokenHeader == null) {
            lockTokenHeader = "";
        }
        if (isLocked(path, ifHeader + lockTokenHeader)) {
            resp.sendError(WebdavStatus.SC_LOCKED);
            return false;
        }
        WebResource resource = this.resources.getResource(path);
        if (!resource.exists()) {
            resp.sendError(404);
            return false;
        }
        if (!resource.isDirectory()) {
            if (!resource.delete()) {
                resp.sendError(500);
                return false;
            }
        } else {
            Map<String, Integer> errorList = new HashMap<>();
            deleteCollection(req, path, errorList);
            if (!resource.delete()) {
                errorList.put(path, 500);
            }
            if (!errorList.isEmpty()) {
                sendReport(req, resp, errorList);
                return false;
            }
        }
        if (setStatus) {
            resp.setStatus(204);
            return true;
        }
        return true;
    }

    private void deleteCollection(HttpServletRequest req, String path, Map<String, Integer> errorList) {
        if (this.debug > 1) {
            log("Delete:" + path);
        }
        if (isSpecialPath(path)) {
            errorList.put(path, 403);
            return;
        }
        String ifHeader = req.getHeader("If");
        if (ifHeader == null) {
            ifHeader = "";
        }
        String lockTokenHeader = req.getHeader("Lock-Token");
        if (lockTokenHeader == null) {
            lockTokenHeader = "";
        }
        String[] entries = this.resources.list(path);
        for (String entry : entries) {
            String childName = path;
            if (!childName.equals("/")) {
                childName = childName + "/";
            }
            String childName2 = childName + entry;
            if (isLocked(childName2, ifHeader + lockTokenHeader)) {
                errorList.put(childName2, Integer.valueOf(WebdavStatus.SC_LOCKED));
            } else {
                WebResource childResource = this.resources.getResource(childName2);
                if (childResource.isDirectory()) {
                    deleteCollection(req, childName2, errorList);
                }
                if (!childResource.delete() && !childResource.isDirectory()) {
                    errorList.put(childName2, 500);
                }
            }
        }
    }

    private void sendReport(HttpServletRequest req, HttpServletResponse resp, Map<String, Integer> errorList) throws IOException {
        resp.setStatus(WebdavStatus.SC_MULTI_STATUS);
        XMLWriter generatedXML = new XMLWriter();
        generatedXML.writeXMLHeader();
        generatedXML.writeElement("D", DEFAULT_NAMESPACE, "multistatus", 0);
        for (Map.Entry<String, Integer> errorEntry : errorList.entrySet()) {
            String errorPath = errorEntry.getKey();
            int errorCode = errorEntry.getValue().intValue();
            generatedXML.writeElement("D", "response", 0);
            generatedXML.writeElement("D", "href", 0);
            generatedXML.writeText(getServletContext().getContextPath() + errorPath);
            generatedXML.writeElement("D", "href", 1);
            generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 0);
            generatedXML.writeText("HTTP/1.1 " + errorCode + " ");
            generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 1);
            generatedXML.writeElement("D", "response", 1);
        }
        generatedXML.writeElement("D", "multistatus", 1);
        Writer writer = resp.getWriter();
        writer.write(generatedXML.toString());
        writer.close();
    }

    private void parseProperties(HttpServletRequest req, XMLWriter generatedXML, String path, int type, List<String> properties) {
        String href;
        if (isSpecialPath(path)) {
            return;
        }
        WebResource resource = this.resources.getResource(path);
        if (!resource.exists()) {
            return;
        }
        String href2 = req.getContextPath() + req.getServletPath();
        if (href2.endsWith("/") && path.startsWith("/")) {
            href = href2 + path.substring(1);
        } else {
            href = href2 + path;
        }
        if (resource.isDirectory() && !href.endsWith("/")) {
            href = href + "/";
        }
        String rewrittenUrl = rewriteUrl(href);
        generatePropFindResponse(generatedXML, rewrittenUrl, path, type, properties, resource.isFile(), false, resource.getCreation(), resource.getLastModified(), resource.getContentLength(), getServletContext().getMimeType(resource.getName()), generateETag(resource));
    }

    private void parseLockNullProperties(HttpServletRequest req, XMLWriter generatedXML, String path, int type, List<String> properties) {
        LockInfo lock;
        if (isSpecialPath(path) || (lock = this.resourceLocks.get(path)) == null) {
            return;
        }
        String absoluteUri = req.getRequestURI();
        String relativePath = getRelativePath(req);
        String toAppend = path.substring(relativePath.length());
        if (!toAppend.startsWith("/")) {
            toAppend = "/" + toAppend;
        }
        String rewrittenUrl = rewriteUrl(RequestUtil.normalize(absoluteUri + toAppend));
        generatePropFindResponse(generatedXML, rewrittenUrl, path, type, properties, true, true, lock.creationDate.getTime(), lock.creationDate.getTime(), 0L, "", "");
    }

    private void generatePropFindResponse(XMLWriter generatedXML, String rewrittenUrl, String path, int propFindType, List<String> properties, boolean isFile, boolean isLockNull, long created, long lastModified, long contentLength, String contentType, String eTag) {
        generatedXML.writeElement("D", "response", 0);
        generatedXML.writeElement("D", "href", 0);
        generatedXML.writeText(rewrittenUrl);
        generatedXML.writeElement("D", "href", 1);
        String resourceName = path;
        int lastSlash = path.lastIndexOf(47);
        if (lastSlash != -1) {
            resourceName = resourceName.substring(lastSlash + 1);
        }
        switch (propFindType) {
            case 0:
                List<String> propertiesNotFound = new ArrayList<>();
                generatedXML.writeElement("D", "propstat", 0);
                generatedXML.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 0);
                for (String property : properties) {
                    if (property.equals("creationdate")) {
                        generatedXML.writeProperty("D", "creationdate", getISOCreationDate(created));
                    } else if (property.equals("displayname")) {
                        generatedXML.writeElement("D", "displayname", 0);
                        generatedXML.writeData(resourceName);
                        generatedXML.writeElement("D", "displayname", 1);
                    } else if (property.equals("getcontentlanguage")) {
                        if (isFile) {
                            generatedXML.writeElement("D", "getcontentlanguage", 2);
                        } else {
                            propertiesNotFound.add(property);
                        }
                    } else if (property.equals("getcontentlength")) {
                        if (isFile) {
                            generatedXML.writeProperty("D", "getcontentlength", Long.toString(contentLength));
                        } else {
                            propertiesNotFound.add(property);
                        }
                    } else if (property.equals("getcontenttype")) {
                        if (isFile) {
                            generatedXML.writeProperty("D", "getcontenttype", contentType);
                        } else {
                            propertiesNotFound.add(property);
                        }
                    } else if (property.equals("getetag")) {
                        if (isFile) {
                            generatedXML.writeProperty("D", "getetag", eTag);
                        } else {
                            propertiesNotFound.add(property);
                        }
                    } else if (property.equals("getlastmodified")) {
                        if (isFile) {
                            generatedXML.writeProperty("D", "getlastmodified", FastHttpDateFormat.formatDate(lastModified));
                        } else {
                            propertiesNotFound.add(property);
                        }
                    } else if (property.equals("resourcetype")) {
                        if (isFile) {
                            if (isLockNull) {
                                generatedXML.writeElement("D", "resourcetype", 0);
                                generatedXML.writeElement("D", "lock-null", 2);
                                generatedXML.writeElement("D", "resourcetype", 1);
                            } else {
                                generatedXML.writeElement("D", "resourcetype", 2);
                            }
                        } else {
                            generatedXML.writeElement("D", "resourcetype", 0);
                            generatedXML.writeElement("D", "collection", 2);
                            generatedXML.writeElement("D", "resourcetype", 1);
                        }
                    } else if (property.equals("source")) {
                        generatedXML.writeProperty("D", "source", "");
                    } else if (property.equals("supportedlock")) {
                        generatedXML.writeElement("D", "supportedlock", 0);
                        generatedXML.writeRaw("<D:lockentry><D:lockscope><D:exclusive/></D:lockscope><D:locktype><D:write/></D:locktype></D:lockentry><D:lockentry><D:lockscope><D:shared/></D:lockscope><D:locktype><D:write/></D:locktype></D:lockentry>");
                        generatedXML.writeElement("D", "supportedlock", 1);
                    } else if (property.equals("lockdiscovery")) {
                        if (!generateLockDiscovery(path, generatedXML)) {
                            propertiesNotFound.add(property);
                        }
                    } else {
                        propertiesNotFound.add(property);
                    }
                }
                generatedXML.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 1);
                generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 0);
                generatedXML.writeText("HTTP/1.1 200 ");
                generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 1);
                generatedXML.writeElement("D", "propstat", 1);
                if (!propertiesNotFound.isEmpty()) {
                    generatedXML.writeElement("D", "propstat", 0);
                    generatedXML.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 0);
                    for (String propertyNotFound : propertiesNotFound) {
                        generatedXML.writeElement("D", propertyNotFound, 2);
                    }
                    generatedXML.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 1);
                    generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 0);
                    generatedXML.writeText("HTTP/1.1 404 ");
                    generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 1);
                    generatedXML.writeElement("D", "propstat", 1);
                    break;
                }
                break;
            case 1:
                generatedXML.writeElement("D", "propstat", 0);
                generatedXML.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 0);
                generatedXML.writeProperty("D", "creationdate", getISOCreationDate(created));
                generatedXML.writeElement("D", "displayname", 0);
                generatedXML.writeData(resourceName);
                generatedXML.writeElement("D", "displayname", 1);
                if (isFile) {
                    generatedXML.writeProperty("D", "getlastmodified", FastHttpDateFormat.formatDate(lastModified));
                    generatedXML.writeProperty("D", "getcontentlength", Long.toString(contentLength));
                    if (contentType != null) {
                        generatedXML.writeProperty("D", "getcontenttype", contentType);
                    }
                    generatedXML.writeProperty("D", "getetag", eTag);
                    if (isLockNull) {
                        generatedXML.writeElement("D", "resourcetype", 0);
                        generatedXML.writeElement("D", "lock-null", 2);
                        generatedXML.writeElement("D", "resourcetype", 1);
                    } else {
                        generatedXML.writeElement("D", "resourcetype", 2);
                    }
                } else {
                    generatedXML.writeProperty("D", "getlastmodified", FastHttpDateFormat.formatDate(lastModified));
                    generatedXML.writeElement("D", "resourcetype", 0);
                    generatedXML.writeElement("D", "collection", 2);
                    generatedXML.writeElement("D", "resourcetype", 1);
                }
                generatedXML.writeProperty("D", "source", "");
                generatedXML.writeElement("D", "supportedlock", 0);
                generatedXML.writeRaw("<D:lockentry><D:lockscope><D:exclusive/></D:lockscope><D:locktype><D:write/></D:locktype></D:lockentry><D:lockentry><D:lockscope><D:shared/></D:lockscope><D:locktype><D:write/></D:locktype></D:lockentry>");
                generatedXML.writeElement("D", "supportedlock", 1);
                generateLockDiscovery(path, generatedXML);
                generatedXML.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 1);
                generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 0);
                generatedXML.writeText("HTTP/1.1 200 ");
                generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 1);
                generatedXML.writeElement("D", "propstat", 1);
                break;
            case 2:
                generatedXML.writeElement("D", "propstat", 0);
                generatedXML.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 0);
                generatedXML.writeElement("D", "creationdate", 2);
                generatedXML.writeElement("D", "displayname", 2);
                if (isFile) {
                    generatedXML.writeElement("D", "getcontentlanguage", 2);
                    generatedXML.writeElement("D", "getcontentlength", 2);
                    generatedXML.writeElement("D", "getcontenttype", 2);
                    generatedXML.writeElement("D", "getetag", 2);
                    generatedXML.writeElement("D", "getlastmodified", 2);
                }
                generatedXML.writeElement("D", "resourcetype", 2);
                generatedXML.writeElement("D", "source", 2);
                generatedXML.writeElement("D", "lockdiscovery", 2);
                generatedXML.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 1);
                generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 0);
                generatedXML.writeText("HTTP/1.1 200 ");
                generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 1);
                generatedXML.writeElement("D", "propstat", 1);
                break;
        }
        generatedXML.writeElement("D", "response", 1);
    }

    private boolean generateLockDiscovery(String path, XMLWriter generatedXML) {
        LockInfo resourceLock = this.resourceLocks.get(path);
        boolean wroteStart = false;
        if (resourceLock != null) {
            wroteStart = true;
            generatedXML.writeElement("D", "lockdiscovery", 0);
            resourceLock.toXML(generatedXML);
        }
        for (LockInfo currentLock : this.collectionLocks) {
            if (path.startsWith(currentLock.path)) {
                if (!wroteStart) {
                    wroteStart = true;
                    generatedXML.writeElement("D", "lockdiscovery", 0);
                }
                currentLock.toXML(generatedXML);
            }
        }
        if (wroteStart) {
            generatedXML.writeElement("D", "lockdiscovery", 1);
            return true;
        }
        return false;
    }

    private String getISOCreationDate(long creationDate) {
        return creationDateFormat.format(new Date(creationDate));
    }

    @Override // org.apache.catalina.servlets.DefaultServlet
    protected String determineMethodsAllowed(HttpServletRequest req) {
        WebResource resource = this.resources.getResource(getRelativePath(req));
        StringBuilder methodsAllowed = new StringBuilder("OPTIONS, GET, POST, HEAD");
        if (!this.readOnly) {
            methodsAllowed.append(", DELETE");
            if (!resource.isDirectory()) {
                methodsAllowed.append(", PUT");
            }
        }
        if ((req instanceof RequestFacade) && ((RequestFacade) req).getAllowTrace()) {
            methodsAllowed.append(", TRACE");
        }
        methodsAllowed.append(", LOCK, UNLOCK, PROPPATCH, COPY, MOVE");
        if (this.listings) {
            methodsAllowed.append(", PROPFIND");
        }
        if (!resource.exists()) {
            methodsAllowed.append(", MKCOL");
        }
        return methodsAllowed.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/servlets/WebdavServlet$LockInfo.class */
    public static class LockInfo implements Serializable {
        private static final long serialVersionUID = 1;
        private final int maxDepth;
        String path = "/";
        String type = "write";
        String scope = "exclusive";
        int depth = 0;
        String owner = "";
        List<String> tokens = Collections.synchronizedList(new ArrayList());
        long expiresAt = 0;
        Date creationDate = new Date();

        LockInfo(int maxDepth) {
            this.maxDepth = maxDepth;
        }

        public String toString() {
            StringBuilder result = new StringBuilder("Type:");
            result.append(this.type);
            result.append("\nScope:");
            result.append(this.scope);
            result.append("\nDepth:");
            result.append(this.depth);
            result.append("\nOwner:");
            result.append(this.owner);
            result.append("\nExpiration:");
            result.append(FastHttpDateFormat.formatDate(this.expiresAt));
            for (String token : this.tokens) {
                result.append("\nToken:");
                result.append(token);
            }
            result.append(StrPool.LF);
            return result.toString();
        }

        public boolean hasExpired() {
            return System.currentTimeMillis() > this.expiresAt;
        }

        public boolean isExclusive() {
            return this.scope.equals("exclusive");
        }

        public void toXML(XMLWriter generatedXML) {
            generatedXML.writeElement("D", "activelock", 0);
            generatedXML.writeElement("D", "locktype", 0);
            generatedXML.writeElement("D", this.type, 2);
            generatedXML.writeElement("D", "locktype", 1);
            generatedXML.writeElement("D", "lockscope", 0);
            generatedXML.writeElement("D", this.scope, 2);
            generatedXML.writeElement("D", "lockscope", 1);
            generatedXML.writeElement("D", "depth", 0);
            if (this.depth == this.maxDepth) {
                generatedXML.writeText("Infinity");
            } else {
                generatedXML.writeText(CustomBooleanEditor.VALUE_0);
            }
            generatedXML.writeElement("D", "depth", 1);
            generatedXML.writeElement("D", "owner", 0);
            generatedXML.writeText(this.owner);
            generatedXML.writeElement("D", "owner", 1);
            generatedXML.writeElement("D", "timeout", 0);
            long timeout = (this.expiresAt - System.currentTimeMillis()) / 1000;
            generatedXML.writeText("Second-" + timeout);
            generatedXML.writeElement("D", "timeout", 1);
            generatedXML.writeElement("D", "locktoken", 0);
            for (String token : this.tokens) {
                generatedXML.writeElement("D", "href", 0);
                generatedXML.writeText("opaquelocktoken:" + token);
                generatedXML.writeElement("D", "href", 1);
            }
            generatedXML.writeElement("D", "locktoken", 1);
            generatedXML.writeElement("D", "activelock", 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/servlets/WebdavServlet$WebdavResolver.class */
    public static class WebdavResolver implements EntityResolver {
        private ServletContext context;

        WebdavResolver(ServletContext theContext) {
            this.context = theContext;
        }

        @Override // org.xml.sax.EntityResolver
        public InputSource resolveEntity(String publicId, String systemId) {
            this.context.log(DefaultServlet.sm.getString("webdavservlet.externalEntityIgnored", publicId, systemId));
            return new InputSource(new StringReader("Ignored external entity"));
        }
    }
}
