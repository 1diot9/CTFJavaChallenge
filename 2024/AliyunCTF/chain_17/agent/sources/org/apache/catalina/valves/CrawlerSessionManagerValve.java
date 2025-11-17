package org.apache.catalina.valves;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/CrawlerSessionManagerValve.class */
public class CrawlerSessionManagerValve extends ValveBase {
    private static final Log log = LogFactory.getLog((Class<?>) CrawlerSessionManagerValve.class);
    private final Map<String, String> clientIdSessionId;
    private final Map<String, String> sessionIdClientId;
    private String crawlerUserAgents;
    private Pattern uaPattern;
    private String crawlerIps;
    private Pattern ipPattern;
    private int sessionInactiveInterval;
    private boolean isHostAware;
    private boolean isContextAware;

    public CrawlerSessionManagerValve() {
        super(true);
        this.clientIdSessionId = new ConcurrentHashMap();
        this.sessionIdClientId = new ConcurrentHashMap();
        this.crawlerUserAgents = ".*[bB]ot.*|.*Yahoo! Slurp.*|.*Feedfetcher-Google.*";
        this.uaPattern = null;
        this.crawlerIps = null;
        this.ipPattern = null;
        this.sessionInactiveInterval = 60;
        this.isHostAware = true;
        this.isContextAware = true;
    }

    public void setCrawlerUserAgents(String crawlerUserAgents) {
        this.crawlerUserAgents = crawlerUserAgents;
        if (crawlerUserAgents == null || crawlerUserAgents.length() == 0) {
            this.uaPattern = null;
        } else {
            this.uaPattern = Pattern.compile(crawlerUserAgents);
        }
    }

    public String getCrawlerUserAgents() {
        return this.crawlerUserAgents;
    }

    public void setCrawlerIps(String crawlerIps) {
        this.crawlerIps = crawlerIps;
        if (crawlerIps == null || crawlerIps.length() == 0) {
            this.ipPattern = null;
        } else {
            this.ipPattern = Pattern.compile(crawlerIps);
        }
    }

    public String getCrawlerIps() {
        return this.crawlerIps;
    }

    public void setSessionInactiveInterval(int sessionInactiveInterval) {
        this.sessionInactiveInterval = sessionInactiveInterval;
    }

    public int getSessionInactiveInterval() {
        return this.sessionInactiveInterval;
    }

    public Map<String, String> getClientIpSessionId() {
        return this.clientIdSessionId;
    }

    public boolean isHostAware() {
        return this.isHostAware;
    }

    public void setHostAware(boolean isHostAware) {
        this.isHostAware = isHostAware;
    }

    public boolean isContextAware() {
        return this.isContextAware;
    }

    public void setContextAware(boolean isContextAware) {
        this.isContextAware = isContextAware;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        super.initInternal();
        this.uaPattern = Pattern.compile(this.crawlerUserAgents);
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        boolean isBot = false;
        String sessionId = null;
        String clientIp = request.getRemoteAddr();
        String clientIdentifier = getClientIdentifier(request.getHost(), request.getContext(), clientIp);
        if (log.isDebugEnabled()) {
            log.debug(request.hashCode() + ": ClientIdentifier=" + clientIdentifier + ", RequestedSessionId=" + request.getRequestedSessionId());
        }
        if (request.getSession(false) == null) {
            Enumeration<String> uaHeaders = request.getHeaders("user-agent");
            String uaHeader = null;
            if (uaHeaders.hasMoreElements()) {
                uaHeader = uaHeaders.nextElement();
            }
            if (uaHeader != null && !uaHeaders.hasMoreElements()) {
                if (log.isDebugEnabled()) {
                    log.debug(request.hashCode() + ": UserAgent=" + uaHeader);
                }
                if (this.uaPattern.matcher(uaHeader).matches()) {
                    isBot = true;
                    if (log.isDebugEnabled()) {
                        log.debug(request.hashCode() + ": Bot found. UserAgent=" + uaHeader);
                    }
                }
            }
            if (this.ipPattern != null && this.ipPattern.matcher(clientIp).matches()) {
                isBot = true;
                if (log.isDebugEnabled()) {
                    log.debug(request.hashCode() + ": Bot found. IP=" + clientIp);
                }
            }
            if (isBot) {
                sessionId = this.clientIdSessionId.get(clientIdentifier);
                if (sessionId != null) {
                    request.setRequestedSessionId(sessionId);
                    if (log.isDebugEnabled()) {
                        log.debug(request.hashCode() + ": SessionID=" + sessionId);
                    }
                }
            }
        }
        getNext().invoke(request, response);
        if (isBot) {
            if (sessionId == null) {
                HttpSession s = request.getSession(false);
                if (s != null) {
                    this.clientIdSessionId.put(clientIdentifier, s.getId());
                    this.sessionIdClientId.put(s.getId(), clientIdentifier);
                    s.setAttribute(getClass().getName(), new CrawlerHttpSessionBindingListener(this.clientIdSessionId, clientIdentifier));
                    s.setMaxInactiveInterval(this.sessionInactiveInterval);
                    if (log.isDebugEnabled()) {
                        log.debug(request.hashCode() + ": New bot session. SessionID=" + s.getId());
                        return;
                    }
                    return;
                }
                return;
            }
            if (log.isDebugEnabled()) {
                log.debug(request.hashCode() + ": Bot session accessed. SessionID=" + sessionId);
            }
        }
    }

    private String getClientIdentifier(Host host, Context context, String clientIp) {
        StringBuilder result = new StringBuilder(clientIp);
        if (this.isHostAware) {
            result.append('-').append(host.getName());
        }
        if (this.isContextAware && context != null) {
            result.append(context.getName());
        }
        return result.toString();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/CrawlerSessionManagerValve$CrawlerHttpSessionBindingListener.class */
    private static class CrawlerHttpSessionBindingListener implements HttpSessionBindingListener, Serializable {
        private static final long serialVersionUID = 1;
        private final transient Map<String, String> clientIdSessionId;
        private final transient String clientIdentifier;

        private CrawlerHttpSessionBindingListener(Map<String, String> clientIdSessionId, String clientIdentifier) {
            this.clientIdSessionId = clientIdSessionId;
            this.clientIdentifier = clientIdentifier;
        }

        @Override // jakarta.servlet.http.HttpSessionBindingListener
        public void valueUnbound(HttpSessionBindingEvent event) {
            if (this.clientIdentifier != null && this.clientIdSessionId != null) {
                this.clientIdSessionId.remove(this.clientIdentifier, event.getSession().getId());
            }
        }
    }
}
