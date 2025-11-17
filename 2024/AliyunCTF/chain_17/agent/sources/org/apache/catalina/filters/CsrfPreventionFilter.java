package org.apache.catalina.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/CsrfPreventionFilter.class */
public class CsrfPreventionFilter extends CsrfPreventionFilterBase {
    private final Log log = LogFactory.getLog((Class<?>) CsrfPreventionFilter.class);
    private final Set<String> entryPoints = new HashSet();
    private int nonceCacheSize = 5;
    private String nonceRequestParameterName = "org.apache.catalina.filters.CSRF_NONCE";

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/CsrfPreventionFilter$NonceCache.class */
    public interface NonceCache<T> extends Serializable {
        void add(T t);

        boolean contains(T t);
    }

    public void setEntryPoints(String entryPoints) {
        String[] values = entryPoints.split(",");
        for (String value : values) {
            this.entryPoints.add(value.trim());
        }
    }

    public void setNonceCacheSize(int nonceCacheSize) {
        this.nonceCacheSize = nonceCacheSize;
    }

    public void setNonceRequestParameterName(String parameterName) {
        this.nonceRequestParameterName = parameterName;
    }

    @Override // org.apache.catalina.filters.CsrfPreventionFilterBase, org.apache.catalina.filters.FilterBase, jakarta.servlet.Filter
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        filterConfig.getServletContext().setAttribute(Constants.CSRF_NONCE_REQUEST_PARAM_NAME_KEY, this.nonceRequestParameterName);
    }

    @Override // jakarta.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ServletResponse wResponse = null;
        if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;
            HttpSession session = req.getSession(false);
            boolean skipNonceCheck = skipNonceCheck(req);
            NonceCache<String> nonceCache = null;
            if (!skipNonceCheck) {
                String previousNonce = req.getParameter(this.nonceRequestParameterName);
                if (previousNonce == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Rejecting request for " + getRequestedPath(req) + ", session " + (null == session ? "(none)" : session.getId()) + " with no CSRF nonce found in request");
                    }
                    res.sendError(getDenyStatus());
                    return;
                }
                nonceCache = getNonceCache(req, session);
                if (nonceCache == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Rejecting request for " + getRequestedPath(req) + ", session " + (null == session ? "(none)" : session.getId()) + " due to empty / missing nonce cache");
                    }
                    res.sendError(getDenyStatus());
                    return;
                } else if (!nonceCache.contains(previousNonce)) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Rejecting request for " + getRequestedPath(req) + ", session " + (null == session ? "(none)" : session.getId()) + " due to invalid nonce " + previousNonce);
                    }
                    res.sendError(getDenyStatus());
                    return;
                } else if (this.log.isTraceEnabled()) {
                    this.log.trace("Allowing request to " + getRequestedPath(req) + " with valid CSRF nonce " + previousNonce);
                }
            }
            if (!skipNonceGeneration(req)) {
                if (skipNonceCheck) {
                    nonceCache = getNonceCache(req, session);
                }
                if (nonceCache == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Creating new CSRF nonce cache with size=" + this.nonceCacheSize + " for session " + (null == session ? "(will create)" : session.getId()));
                    }
                    if (session == null) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug("Creating new session to store CSRF nonce cache");
                        }
                        session = req.getSession(true);
                    }
                    nonceCache = createNonceCache(req, session);
                }
                String newNonce = generateNonce(req);
                nonceCache.add(newNonce);
                request.setAttribute(Constants.CSRF_NONCE_REQUEST_ATTR_NAME, newNonce);
                wResponse = new CsrfResponseWrapper(res, this.nonceRequestParameterName, newNonce);
            }
        }
        chain.doFilter(request, wResponse == null ? response : wResponse);
    }

    protected boolean skipNonceCheck(HttpServletRequest request) {
        if (!"GET".equals(request.getMethod())) {
            return false;
        }
        String requestedPath = getRequestedPath(request);
        if (!this.entryPoints.contains(requestedPath)) {
            return false;
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace("Skipping CSRF nonce-check for GET request to entry point " + requestedPath);
            return true;
        }
        return true;
    }

    protected boolean skipNonceGeneration(HttpServletRequest request) {
        return false;
    }

    protected NonceCache<String> createNonceCache(HttpServletRequest request, HttpSession session) {
        NonceCache<String> nonceCache = new LruCache<>(this.nonceCacheSize);
        session.setAttribute("org.apache.catalina.filters.CSRF_NONCE", nonceCache);
        return nonceCache;
    }

    protected NonceCache<String> getNonceCache(HttpServletRequest request, HttpSession session) {
        if (session == null) {
            return null;
        }
        NonceCache<String> nonceCache = (NonceCache) session.getAttribute("org.apache.catalina.filters.CSRF_NONCE");
        return nonceCache;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/CsrfPreventionFilter$CsrfResponseWrapper.class */
    protected static class CsrfResponseWrapper extends HttpServletResponseWrapper {
        private final String nonceRequestParameterName;
        private final String nonce;

        public CsrfResponseWrapper(HttpServletResponse response, String nonceRequestParameterName, String nonce) {
            super(response);
            this.nonceRequestParameterName = nonceRequestParameterName;
            this.nonce = nonce;
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public String encodeRedirectURL(String url) {
            return addNonce(super.encodeRedirectURL(url));
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public String encodeURL(String url) {
            return addNonce(super.encodeURL(url));
        }

        private String addNonce(String url) {
            if (url == null || this.nonce == null) {
                return url;
            }
            String path = url;
            String query = "";
            String anchor = "";
            int pound = path.indexOf(35);
            if (pound >= 0) {
                anchor = path.substring(pound);
                path = path.substring(0, pound);
            }
            int question = path.indexOf(63);
            if (question >= 0) {
                query = path.substring(question);
                path = path.substring(0, question);
            }
            StringBuilder sb = new StringBuilder(path);
            if (query.length() > 0) {
                sb.append(query);
                sb.append('&');
            } else {
                sb.append('?');
            }
            sb.append(this.nonceRequestParameterName);
            sb.append('=');
            sb.append(this.nonce);
            sb.append(anchor);
            return sb.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/CsrfPreventionFilter$LruCache.class */
    public static class LruCache<T> implements NonceCache<T> {
        private static final long serialVersionUID = 1;
        private final Map<T, T> cache;

        public LruCache(final int cacheSize) {
            this.cache = new LinkedHashMap<T, T>() { // from class: org.apache.catalina.filters.CsrfPreventionFilter.LruCache.1
                private static final long serialVersionUID = 1;

                @Override // java.util.LinkedHashMap
                protected boolean removeEldestEntry(Map.Entry<T, T> eldest) {
                    if (size() > cacheSize) {
                        return true;
                    }
                    return false;
                }
            };
        }

        @Override // org.apache.catalina.filters.CsrfPreventionFilter.NonceCache
        public void add(T key) {
            synchronized (this.cache) {
                this.cache.put(key, null);
            }
        }

        @Override // org.apache.catalina.filters.CsrfPreventionFilter.NonceCache
        public boolean contains(T key) {
            boolean containsKey;
            synchronized (this.cache) {
                containsKey = this.cache.containsKey(key);
            }
            return containsKey;
        }
    }
}
