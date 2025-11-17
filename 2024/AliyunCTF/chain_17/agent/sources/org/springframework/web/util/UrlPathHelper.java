package org.springframework.web.util;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.MappingMatch;
import java.net.URLDecoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/UrlPathHelper.class */
public class UrlPathHelper {
    private static final String WEBSPHERE_URI_ATTRIBUTE = "com.ibm.websphere.servlet.uri_non_decoded";

    @Nullable
    static volatile Boolean websphereComplianceFlag;
    private boolean alwaysUseFullPath = false;
    private boolean urlDecode = true;
    private boolean removeSemicolonContent = true;
    private String defaultEncoding = "ISO-8859-1";
    private boolean readOnly = false;
    public static final UrlPathHelper rawPathInstance;
    public static final String PATH_ATTRIBUTE = UrlPathHelper.class.getName() + ".PATH";
    private static final Log logger = LogFactory.getLog((Class<?>) UrlPathHelper.class);
    public static final UrlPathHelper defaultInstance = new UrlPathHelper();

    static {
        defaultInstance.setReadOnly();
        rawPathInstance = new UrlPathHelper() { // from class: org.springframework.web.util.UrlPathHelper.1
            @Override // org.springframework.web.util.UrlPathHelper
            public String removeSemicolonContent(String requestUri) {
                return requestUri;
            }
        };
        rawPathInstance.setAlwaysUseFullPath(true);
        rawPathInstance.setUrlDecode(false);
        rawPathInstance.setRemoveSemicolonContent(false);
        rawPathInstance.setReadOnly();
    }

    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
        checkReadOnly();
        this.alwaysUseFullPath = alwaysUseFullPath;
    }

    public void setUrlDecode(boolean urlDecode) {
        checkReadOnly();
        this.urlDecode = urlDecode;
    }

    public boolean isUrlDecode() {
        return this.urlDecode;
    }

    public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
        checkReadOnly();
        this.removeSemicolonContent = removeSemicolonContent;
    }

    public boolean shouldRemoveSemicolonContent() {
        return this.removeSemicolonContent;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        checkReadOnly();
        this.defaultEncoding = defaultEncoding;
    }

    protected String getDefaultEncoding() {
        return this.defaultEncoding;
    }

    private void setReadOnly() {
        this.readOnly = true;
    }

    private void checkReadOnly() {
        Assert.isTrue(!this.readOnly, "This instance cannot be modified");
    }

    public String resolveAndCacheLookupPath(HttpServletRequest request) {
        String lookupPath = getLookupPathForRequest(request);
        request.setAttribute(PATH_ATTRIBUTE, lookupPath);
        return lookupPath;
    }

    public static String getResolvedLookupPath(ServletRequest request) {
        String lookupPath = (String) request.getAttribute(PATH_ATTRIBUTE);
        Assert.notNull(lookupPath, (Supplier<String>) () -> {
            return "Expected lookupPath in request attribute \"" + PATH_ATTRIBUTE + "\".";
        });
        return lookupPath;
    }

    @Deprecated
    public String getLookupPathForRequest(HttpServletRequest request, @Nullable String name) {
        String result = null;
        if (name != null) {
            result = (String) request.getAttribute(name);
        }
        return result != null ? result : getLookupPathForRequest(request);
    }

    public String getLookupPathForRequest(HttpServletRequest request) {
        String pathWithinApp = getPathWithinApplication(request);
        if (this.alwaysUseFullPath || ignoreServletPath(request)) {
            return pathWithinApp;
        }
        String rest = getPathWithinServletMapping(request, pathWithinApp);
        if (StringUtils.hasLength(rest)) {
            return rest;
        }
        return pathWithinApp;
    }

    private boolean ignoreServletPath(HttpServletRequest request) {
        HttpServletMapping mapping = (HttpServletMapping) request.getAttribute(RequestDispatcher.INCLUDE_MAPPING);
        HttpServletMapping mapping2 = mapping == null ? request.getHttpServletMapping() : mapping;
        MappingMatch match = mapping2.getMappingMatch();
        return match != null && (!match.equals(MappingMatch.PATH) || mapping2.getPattern().equals("/*"));
    }

    public String getPathWithinServletMapping(HttpServletRequest request) {
        return getPathWithinServletMapping(request, getPathWithinApplication(request));
    }

    protected String getPathWithinServletMapping(HttpServletRequest request, String pathWithinApp) {
        String path;
        String servletPath = getServletPath(request);
        String sanitizedPathWithinApp = getSanitizedPath(pathWithinApp);
        if (servletPath.contains(sanitizedPathWithinApp)) {
            path = getRemainingPath(sanitizedPathWithinApp, servletPath, false);
        } else {
            path = getRemainingPath(pathWithinApp, servletPath, false);
        }
        if (path != null) {
            return path;
        }
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            return pathInfo;
        }
        if (!this.urlDecode) {
            String path2 = getRemainingPath(decodeInternal(request, pathWithinApp), servletPath, false);
            if (path2 != null) {
                return pathWithinApp;
            }
        }
        return servletPath;
    }

    public String getPathWithinApplication(HttpServletRequest request) {
        String contextPath = getContextPath(request);
        String requestUri = getRequestUri(request);
        String path = getRemainingPath(requestUri, contextPath, true);
        if (path != null) {
            return StringUtils.hasText(path) ? path : "/";
        }
        return requestUri;
    }

    @Nullable
    private String getRemainingPath(String requestUri, String mapping, boolean ignoreCase) {
        int index1 = 0;
        int index2 = 0;
        while (index1 < requestUri.length() && index2 < mapping.length()) {
            char c1 = requestUri.charAt(index1);
            char c2 = mapping.charAt(index2);
            if (c1 == ';') {
                index1 = requestUri.indexOf(47, index1);
                if (index1 == -1) {
                    return null;
                }
                c1 = requestUri.charAt(index1);
            }
            if (c1 == c2 || (ignoreCase && Character.toLowerCase(c1) == Character.toLowerCase(c2))) {
                index1++;
                index2++;
            } else {
                return null;
            }
        }
        if (index2 != mapping.length()) {
            return null;
        }
        if (index1 == requestUri.length()) {
            return "";
        }
        if (requestUri.charAt(index1) == ';') {
            index1 = requestUri.indexOf(47, index1);
        }
        return index1 != -1 ? requestUri.substring(index1) : "";
    }

    private static String getSanitizedPath(final String path) {
        int start = path.indexOf("//");
        if (start == -1) {
            return path;
        }
        char[] content = path.toCharArray();
        int slowIndex = start;
        for (int fastIndex = start + 1; fastIndex < content.length; fastIndex++) {
            if (content[fastIndex] != '/' || content[slowIndex] != '/') {
                slowIndex++;
                content[slowIndex] = content[fastIndex];
            }
        }
        return new String(content, 0, slowIndex + 1);
    }

    public String getRequestUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute("jakarta.servlet.include.request_uri");
        if (uri == null) {
            uri = request.getRequestURI();
        }
        return decodeAndCleanUriString(request, uri);
    }

    public String getContextPath(HttpServletRequest request) {
        String contextPath = (String) request.getAttribute("jakarta.servlet.include.context_path");
        if (contextPath == null) {
            contextPath = request.getContextPath();
        }
        if (StringUtils.matchesCharacter(contextPath, '/')) {
            contextPath = "";
        }
        return decodeRequestString(request, contextPath);
    }

    public String getServletPath(HttpServletRequest request) {
        String servletPath = (String) request.getAttribute("jakarta.servlet.include.servlet_path");
        if (servletPath == null) {
            servletPath = request.getServletPath();
        }
        if (servletPath.length() > 1 && servletPath.endsWith("/") && shouldRemoveTrailingServletPathSlash(request)) {
            servletPath = servletPath.substring(0, servletPath.length() - 1);
        }
        return servletPath;
    }

    public String getOriginatingRequestUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute(WEBSPHERE_URI_ATTRIBUTE);
        if (uri == null) {
            uri = (String) request.getAttribute("jakarta.servlet.forward.request_uri");
            if (uri == null) {
                uri = request.getRequestURI();
            }
        }
        return decodeAndCleanUriString(request, uri);
    }

    public String getOriginatingContextPath(HttpServletRequest request) {
        String contextPath = (String) request.getAttribute("jakarta.servlet.forward.context_path");
        if (contextPath == null) {
            contextPath = request.getContextPath();
        }
        return decodeRequestString(request, contextPath);
    }

    public String getOriginatingServletPath(HttpServletRequest request) {
        String servletPath = (String) request.getAttribute("jakarta.servlet.forward.servlet_path");
        if (servletPath == null) {
            servletPath = request.getServletPath();
        }
        return servletPath;
    }

    public String getOriginatingQueryString(HttpServletRequest request) {
        if (request.getAttribute("jakarta.servlet.forward.request_uri") != null || request.getAttribute("jakarta.servlet.error.request_uri") != null) {
            return (String) request.getAttribute("jakarta.servlet.forward.query_string");
        }
        return request.getQueryString();
    }

    private String decodeAndCleanUriString(HttpServletRequest request, String uri) {
        return getSanitizedPath(decodeRequestString(request, removeSemicolonContent(uri)));
    }

    public String decodeRequestString(HttpServletRequest request, String source) {
        if (this.urlDecode) {
            return decodeInternal(request, source);
        }
        return source;
    }

    private String decodeInternal(HttpServletRequest request, String source) {
        String enc = determineEncoding(request);
        try {
            return UriUtils.decode(source, enc);
        } catch (UnsupportedCharsetException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not decode request string [" + source + "] with encoding '" + enc + "': falling back to platform default encoding; exception message: " + ex.getMessage());
            }
            return URLDecoder.decode(source);
        }
    }

    protected String determineEncoding(HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (enc == null) {
            enc = getDefaultEncoding();
        }
        return enc;
    }

    public String removeSemicolonContent(String requestUri) {
        return this.removeSemicolonContent ? removeSemicolonContentInternal(requestUri) : removeJsessionid(requestUri);
    }

    private static String removeSemicolonContentInternal(String requestUri) {
        int semicolonIndex = requestUri.indexOf(59);
        if (semicolonIndex == -1) {
            return requestUri;
        }
        StringBuilder sb = new StringBuilder(requestUri);
        while (semicolonIndex != -1) {
            int slashIndex = sb.indexOf("/", semicolonIndex + 1);
            if (slashIndex == -1) {
                return sb.substring(0, semicolonIndex);
            }
            sb.delete(semicolonIndex, slashIndex);
            semicolonIndex = sb.indexOf(";", semicolonIndex);
        }
        return sb.toString();
    }

    private String removeJsessionid(String requestUri) {
        int index = requestUri.toLowerCase().indexOf(";jsessionid=");
        if (index == -1) {
            return requestUri;
        }
        String start = requestUri.substring(0, index);
        for (int i = index + ";jsessionid=".length(); i < requestUri.length(); i++) {
            char c = requestUri.charAt(i);
            if (c == ';' || c == '/') {
                return start + requestUri.substring(i);
            }
        }
        return start;
    }

    public Map<String, String> decodePathVariables(HttpServletRequest request, Map<String, String> vars) {
        if (this.urlDecode) {
            return vars;
        }
        Map<String, String> decodedVars = CollectionUtils.newLinkedHashMap(vars.size());
        vars.forEach((key, value) -> {
            decodedVars.put(key, decodeInternal(request, value));
        });
        return decodedVars;
    }

    public MultiValueMap<String, String> decodeMatrixVariables(HttpServletRequest request, MultiValueMap<String, String> vars) {
        if (this.urlDecode) {
            return vars;
        }
        MultiValueMap<String, String> decodedVars = new LinkedMultiValueMap<>(vars.size());
        vars.forEach((key, values) -> {
            Iterator it = values.iterator();
            while (it.hasNext()) {
                String value = (String) it.next();
                decodedVars.add(key, decodeInternal(request, value));
            }
        });
        return decodedVars;
    }

    private boolean shouldRemoveTrailingServletPathSlash(HttpServletRequest request) {
        if (request.getAttribute(WEBSPHERE_URI_ATTRIBUTE) == null) {
            return false;
        }
        Boolean flagToUse = websphereComplianceFlag;
        if (flagToUse == null) {
            ClassLoader classLoader = UrlPathHelper.class.getClassLoader();
            boolean flag = false;
            try {
                Class<?> cl = classLoader.loadClass("com.ibm.ws.webcontainer.WebContainer");
                Properties prop = (Properties) cl.getMethod("getWebContainerProperties", new Class[0]).invoke(null, new Object[0]);
                flag = Boolean.parseBoolean(prop.getProperty("com.ibm.ws.webcontainer.removetrailingservletpathslash"));
            } catch (Throwable ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not introspect WebSphere web container properties: " + ex);
                }
            }
            flagToUse = Boolean.valueOf(flag);
            websphereComplianceFlag = Boolean.valueOf(flag);
        }
        return !flagToUse.booleanValue();
    }
}
