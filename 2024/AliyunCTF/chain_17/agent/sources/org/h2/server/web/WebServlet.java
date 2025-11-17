package org.h2.server.web;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.h2.util.NetworkConnectionInfo;
import org.springframework.http.HttpHeaders;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/server/web/WebServlet.class */
public class WebServlet extends HttpServlet {
    private static final long serialVersionUID = 1;
    private transient WebServer server;

    public void init() {
        ServletConfig servletConfig = getServletConfig();
        Enumeration initParameterNames = servletConfig.getInitParameterNames();
        ArrayList arrayList = new ArrayList();
        while (initParameterNames.hasMoreElements()) {
            String obj = initParameterNames.nextElement().toString();
            String initParameter = servletConfig.getInitParameter(obj);
            if (!obj.startsWith("-")) {
                obj = "-" + obj;
            }
            arrayList.add(obj);
            if (initParameter.length() > 0) {
                arrayList.add(initParameter);
            }
        }
        String[] strArr = (String[]) arrayList.toArray(new String[0]);
        this.server = new WebServer();
        this.server.setAllowChunked(false);
        this.server.init(strArr);
    }

    public void destroy() {
        this.server.stop();
    }

    private boolean allow(HttpServletRequest httpServletRequest) {
        if (this.server.getAllowOthers()) {
            return true;
        }
        try {
            return InetAddress.getByName(httpServletRequest.getRemoteAddr()).isLoopbackAddress();
        } catch (NoClassDefFoundError | UnknownHostException e) {
            return false;
        }
    }

    private String getAllowedFile(HttpServletRequest httpServletRequest, String str) {
        if (!allow(httpServletRequest)) {
            return "notAllowed.jsp";
        }
        if (str.length() == 0) {
            return "index.do";
        }
        return str;
    }

    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        httpServletRequest.setCharacterEncoding("utf-8");
        String pathInfo = httpServletRequest.getPathInfo();
        if (pathInfo == null) {
            httpServletResponse.sendRedirect(httpServletRequest.getRequestURI() + "/");
            return;
        }
        if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);
        }
        String allowedFile = getAllowedFile(httpServletRequest, pathInfo);
        Properties properties = new Properties();
        Enumeration attributeNames = httpServletRequest.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String obj = attributeNames.nextElement().toString();
            properties.put(obj, httpServletRequest.getAttribute(obj).toString());
        }
        Enumeration parameterNames = httpServletRequest.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String obj2 = parameterNames.nextElement().toString();
            properties.put(obj2, httpServletRequest.getParameter(obj2));
        }
        WebSession webSession = null;
        String property = properties.getProperty("jsessionid");
        if (property != null) {
            webSession = this.server.getSession(property);
        }
        WebApp webApp = new WebApp(this.server);
        webApp.setSession(webSession, properties);
        String header = httpServletRequest.getHeader("if-modified-since");
        String scheme = httpServletRequest.getScheme();
        StringBuilder append = new StringBuilder(scheme).append("://").append(httpServletRequest.getServerName());
        int serverPort = httpServletRequest.getServerPort();
        if ((serverPort != 80 || !scheme.equals("http")) && (serverPort != 443 || !scheme.equals("https"))) {
            append.append(':').append(serverPort);
        }
        String processRequest = webApp.processRequest(allowedFile, new NetworkConnectionInfo(append.append(httpServletRequest.getContextPath()).toString(), httpServletRequest.getRemoteAddr(), httpServletRequest.getRemotePort()));
        WebSession session = webApp.getSession();
        String mimeType = webApp.getMimeType();
        boolean cache = webApp.getCache();
        if (cache && this.server.getStartDateTime().equals(header)) {
            httpServletResponse.setStatus(304);
            return;
        }
        byte[] file = this.server.getFile(processRequest);
        if (file == null) {
            httpServletResponse.sendError(404);
            file = ("File not found: " + processRequest).getBytes(StandardCharsets.UTF_8);
        } else {
            if (session != null && processRequest.endsWith(".jsp")) {
                file = PageParser.parse(new String(file, StandardCharsets.UTF_8), session.map).getBytes(StandardCharsets.UTF_8);
            }
            httpServletResponse.setContentType(mimeType);
            if (!cache) {
                httpServletResponse.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
            } else {
                httpServletResponse.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=10");
                httpServletResponse.setHeader(HttpHeaders.LAST_MODIFIED, this.server.getStartDateTime());
            }
        }
        if (file != null) {
            httpServletResponse.getOutputStream().write(file);
        }
    }

    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        doGet(httpServletRequest, httpServletResponse);
    }
}
