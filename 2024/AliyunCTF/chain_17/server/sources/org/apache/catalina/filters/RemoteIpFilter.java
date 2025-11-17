package org.apache.catalina.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestWrapper;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.PushBuilder;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.catalina.AccessLog;
import org.apache.catalina.Globals;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.util.RequestUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.parser.Host;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/RemoteIpFilter.class */
public class RemoteIpFilter extends GenericFilter {
    private static final long serialVersionUID = 1;
    protected static final String HTTP_SERVER_PORT_PARAMETER = "httpServerPort";
    protected static final String HTTPS_SERVER_PORT_PARAMETER = "httpsServerPort";
    protected static final String INTERNAL_PROXIES_PARAMETER = "internalProxies";
    protected static final String PROTOCOL_HEADER_PARAMETER = "protocolHeader";
    protected static final String PROTOCOL_HEADER_HTTPS_VALUE_PARAMETER = "protocolHeaderHttpsValue";
    protected static final String HOST_HEADER_PARAMETER = "hostHeader";
    protected static final String PORT_HEADER_PARAMETER = "portHeader";
    protected static final String CHANGE_LOCAL_NAME_PARAMETER = "changeLocalName";
    protected static final String CHANGE_LOCAL_PORT_PARAMETER = "changeLocalPort";
    protected static final String PROXIES_HEADER_PARAMETER = "proxiesHeader";
    protected static final String REMOTE_IP_HEADER_PARAMETER = "remoteIpHeader";
    protected static final String TRUSTED_PROXIES_PARAMETER = "trustedProxies";
    protected static final String ENABLE_LOOKUPS_PARAMETER = "enableLookups";
    private boolean enableLookups;
    private static final Pattern commaSeparatedValuesPattern = Pattern.compile("\\s*,\\s*");
    protected static final StringManager sm = StringManager.getManager((Class<?>) RemoteIpFilter.class);
    private transient Log log = LogFactory.getLog((Class<?>) RemoteIpFilter.class);
    private int httpServerPort = 80;
    private int httpsServerPort = 443;
    private Pattern internalProxies = Pattern.compile("10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|192\\.168\\.\\d{1,3}\\.\\d{1,3}|169\\.254\\.\\d{1,3}\\.\\d{1,3}|127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|100\\.6[4-9]{1}\\.\\d{1,3}\\.\\d{1,3}|100\\.[7-9]{1}\\d{1}\\.\\d{1,3}\\.\\d{1,3}|100\\.1[0-1]{1}\\d{1}\\.\\d{1,3}\\.\\d{1,3}|100\\.12[0-7]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.1[6-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.2[0-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.3[0-1]{1}\\.\\d{1,3}\\.\\d{1,3}|0:0:0:0:0:0:0:1|::1");
    private String protocolHeader = "X-Forwarded-Proto";
    private String protocolHeaderHttpsValue = "https";
    private String hostHeader = null;
    private boolean changeLocalName = false;
    private String portHeader = null;
    private boolean changeLocalPort = false;
    private String proxiesHeader = "X-Forwarded-By";
    private String remoteIpHeader = "X-Forwarded-For";
    private boolean requestAttributesEnabled = true;
    private Pattern trustedProxies = null;

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/RemoteIpFilter$XForwardedRequest.class */
    public static class XForwardedRequest extends HttpServletRequestWrapper {
        protected final Map<String, List<String>> headers;
        protected String localName;
        protected int localPort;
        protected String remoteAddr;
        protected String remoteHost;
        protected String scheme;
        protected boolean secure;
        protected String serverName;
        protected int serverPort;

        public XForwardedRequest(HttpServletRequest request) {
            super(request);
            this.localName = request.getLocalName();
            this.localPort = request.getLocalPort();
            this.remoteAddr = request.getRemoteAddr();
            this.remoteHost = request.getRemoteHost();
            this.scheme = request.getScheme();
            this.secure = request.isSecure();
            this.serverName = request.getServerName();
            this.serverPort = request.getServerPort();
            this.headers = new HashMap();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                this.headers.put(header, Collections.list(request.getHeaders(header)));
            }
        }

        @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
        public long getDateHeader(String name) {
            String value = getHeader(name);
            if (value == null) {
                return -1L;
            }
            long date = FastHttpDateFormat.parseDate(value);
            if (date == -1) {
                throw new IllegalArgumentException(value);
            }
            return date;
        }

        @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
        public String getHeader(String name) {
            Map.Entry<String, List<String>> header = getHeaderEntry(name);
            if (header == null || header.getValue() == null || header.getValue().isEmpty()) {
                return null;
            }
            return header.getValue().get(0);
        }

        protected Map.Entry<String, List<String>> getHeaderEntry(String name) {
            for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(name)) {
                    return entry;
                }
            }
            return null;
        }

        @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
        public Enumeration<String> getHeaderNames() {
            return Collections.enumeration(this.headers.keySet());
        }

        @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
        public Enumeration<String> getHeaders(String name) {
            Map.Entry<String, List<String>> header = getHeaderEntry(name);
            if (header == null || header.getValue() == null) {
                return Collections.enumeration(Collections.emptyList());
            }
            return Collections.enumeration(header.getValue());
        }

        @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
        public int getIntHeader(String name) {
            String value = getHeader(name);
            if (value == null) {
                return -1;
            }
            return Integer.parseInt(value);
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        public String getLocalName() {
            return this.localName;
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        public int getLocalPort() {
            return this.localPort;
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        public String getRemoteAddr() {
            return this.remoteAddr;
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        public String getRemoteHost() {
            return this.remoteHost;
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        public String getScheme() {
            return this.scheme;
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        public String getServerName() {
            return this.serverName;
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        public int getServerPort() {
            return this.serverPort;
        }

        public void removeHeader(String name) {
            Map.Entry<String, List<String>> header = getHeaderEntry(name);
            if (header != null) {
                this.headers.remove(header.getKey());
            }
        }

        public void setHeader(String name, String value) {
            List<String> values = Collections.singletonList(value);
            Map.Entry<String, List<String>> header = getHeaderEntry(name);
            if (header == null) {
                this.headers.put(name, values);
            } else {
                header.setValue(values);
            }
        }

        public void setLocalName(String localName) {
            this.localName = localName;
        }

        public void setLocalPort(int localPort) {
            this.localPort = localPort;
        }

        public void setRemoteAddr(String remoteAddr) {
            this.remoteAddr = remoteAddr;
        }

        public void setRemoteHost(String remoteHost) {
            this.remoteHost = remoteHost;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }

        public void setSecure(boolean secure) {
            super.getRequest().setAttribute(Globals.REMOTE_IP_FILTER_SECURE, Boolean.valueOf(secure));
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }

        public void setServerPort(int serverPort) {
            this.serverPort = serverPort;
        }

        @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
        public StringBuffer getRequestURL() {
            return RequestUtil.getRequestURL(this);
        }

        @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
        public PushBuilder newPushBuilder() {
            ServletRequest current;
            ServletRequest request = getRequest();
            while (true) {
                current = request;
                if (!(current instanceof ServletRequestWrapper)) {
                    break;
                }
                request = ((ServletRequestWrapper) current).getRequest();
            }
            if (current instanceof RequestFacade) {
                return ((RequestFacade) current).newPushBuilder(this);
            }
            return null;
        }
    }

    protected static String[] commaDelimitedListToStringArray(String commaDelimitedStrings) {
        return (commaDelimitedStrings == null || commaDelimitedStrings.length() == 0) ? new String[0] : commaSeparatedValuesPattern.split(commaDelimitedStrings);
    }

    @Deprecated
    protected static String listToCommaDelimitedString(List<String> stringList) {
        if (stringList == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        Iterator<String> it = stringList.iterator();
        while (it.hasNext()) {
            Object element = it.next();
            if (element != null) {
                result.append(element);
                if (it.hasNext()) {
                    result.append(", ");
                }
            }
        }
        return result.toString();
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String protocolHeaderValue;
        boolean isInternal = this.internalProxies != null && this.internalProxies.matcher(request.getRemoteAddr()).matches();
        if (isInternal || (this.trustedProxies != null && this.trustedProxies.matcher(request.getRemoteAddr()).matches())) {
            String remoteIp = null;
            Deque<String> proxiesHeaderValue = new ArrayDeque<>();
            StringBuilder concatRemoteIpHeaderValue = new StringBuilder();
            Enumeration<String> e = request.getHeaders(this.remoteIpHeader);
            while (e.hasMoreElements()) {
                if (concatRemoteIpHeaderValue.length() > 0) {
                    concatRemoteIpHeaderValue.append(", ");
                }
                concatRemoteIpHeaderValue.append(e.nextElement());
            }
            String[] remoteIpHeaderValue = commaDelimitedListToStringArray(concatRemoteIpHeaderValue.toString());
            if (!isInternal) {
                proxiesHeaderValue.addFirst(request.getRemoteAddr());
            }
            int idx = remoteIpHeaderValue.length - 1;
            while (idx >= 0) {
                String currentRemoteIp = remoteIpHeaderValue[idx];
                remoteIp = currentRemoteIp;
                if (this.internalProxies == null || !this.internalProxies.matcher(currentRemoteIp).matches()) {
                    if (this.trustedProxies != null && this.trustedProxies.matcher(currentRemoteIp).matches()) {
                        proxiesHeaderValue.addFirst(currentRemoteIp);
                    } else {
                        idx--;
                        break;
                    }
                }
                idx--;
            }
            LinkedList<String> newRemoteIpHeaderValue = new LinkedList<>();
            while (idx >= 0) {
                newRemoteIpHeaderValue.addFirst(remoteIpHeaderValue[idx]);
                idx--;
            }
            XForwardedRequest xRequest = new XForwardedRequest(request);
            if (remoteIp != null) {
                xRequest.setRemoteAddr(remoteIp);
                if (getEnableLookups()) {
                    try {
                        InetAddress inetAddress = InetAddress.getByName(remoteIp);
                        xRequest.setRemoteHost(inetAddress.getCanonicalHostName());
                    } catch (UnknownHostException e2) {
                        this.log.debug(sm.getString("remoteIpFilter.invalidRemoteAddress", remoteIp), e2);
                        xRequest.setRemoteHost(remoteIp);
                    }
                } else {
                    xRequest.setRemoteHost(remoteIp);
                }
                if (proxiesHeaderValue.size() == 0) {
                    xRequest.removeHeader(this.proxiesHeader);
                } else {
                    String commaDelimitedListOfProxies = StringUtils.join(proxiesHeaderValue);
                    xRequest.setHeader(this.proxiesHeader, commaDelimitedListOfProxies);
                }
                if (newRemoteIpHeaderValue.size() == 0) {
                    xRequest.removeHeader(this.remoteIpHeader);
                } else {
                    String commaDelimitedRemoteIpHeaderValue = StringUtils.join(newRemoteIpHeaderValue);
                    xRequest.setHeader(this.remoteIpHeader, commaDelimitedRemoteIpHeaderValue);
                }
            }
            if (this.protocolHeader != null && (protocolHeaderValue = request.getHeader(this.protocolHeader)) != null) {
                if (isForwardedProtoHeaderValueSecure(protocolHeaderValue)) {
                    xRequest.setSecure(true);
                    xRequest.setScheme("https");
                    setPorts(xRequest, this.httpsServerPort);
                } else {
                    xRequest.setSecure(false);
                    xRequest.setScheme("http");
                    setPorts(xRequest, this.httpServerPort);
                }
            }
            if (this.hostHeader != null) {
                String hostHeaderValue = request.getHeader(this.hostHeader);
                if (hostHeaderValue != null) {
                    try {
                        int portIndex = Host.parse(hostHeaderValue);
                        if (portIndex > -1) {
                            this.log.debug(sm.getString("remoteIpFilter.invalidHostWithPort", hostHeaderValue, this.hostHeader));
                            hostHeaderValue = hostHeaderValue.substring(0, portIndex);
                        }
                        xRequest.setServerName(hostHeaderValue);
                        if (isChangeLocalName()) {
                            xRequest.setLocalName(hostHeaderValue);
                        }
                    } catch (IllegalArgumentException e3) {
                        this.log.debug(sm.getString("remoteIpFilter.invalidHostHeader", hostHeaderValue, this.hostHeader));
                    }
                }
            }
            request.setAttribute(Globals.REQUEST_FORWARDED_ATTRIBUTE, Boolean.TRUE);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Incoming request " + request.getRequestURI() + " with originalRemoteAddr [" + request.getRemoteAddr() + "], originalRemoteHost=[" + request.getRemoteHost() + "], originalSecure=[" + request.isSecure() + "], originalScheme=[" + request.getScheme() + "], originalServerName=[" + request.getServerName() + "], originalServerPort=[" + request.getServerPort() + "] will be seen as newRemoteAddr=[" + xRequest.getRemoteAddr() + "], newRemoteHost=[" + xRequest.getRemoteHost() + "], newSecure=[" + xRequest.isSecure() + "], newScheme=[" + xRequest.getScheme() + "], newServerName=[" + xRequest.getServerName() + "], newServerPort=[" + xRequest.getServerPort() + "]");
            }
            if (this.requestAttributesEnabled) {
                request.setAttribute(AccessLog.REMOTE_ADDR_ATTRIBUTE, xRequest.getRemoteAddr());
                request.setAttribute("org.apache.tomcat.remoteAddr", xRequest.getRemoteAddr());
                request.setAttribute(AccessLog.REMOTE_HOST_ATTRIBUTE, xRequest.getRemoteHost());
                request.setAttribute(AccessLog.PROTOCOL_ATTRIBUTE, xRequest.getProtocol());
                request.setAttribute(AccessLog.SERVER_NAME_ATTRIBUTE, xRequest.getServerName());
                request.setAttribute(AccessLog.SERVER_PORT_ATTRIBUTE, Integer.valueOf(xRequest.getServerPort()));
            }
            chain.doFilter(xRequest, response);
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Skip RemoteIpFilter for request " + request.getRequestURI() + " with originalRemoteAddr '" + request.getRemoteAddr() + "'");
        }
        chain.doFilter(request, response);
    }

    private boolean isForwardedProtoHeaderValueSecure(String protocolHeaderValue) {
        if (!protocolHeaderValue.contains(",")) {
            return this.protocolHeaderHttpsValue.equalsIgnoreCase(protocolHeaderValue);
        }
        String[] forwardedProtocols = commaDelimitedListToStringArray(protocolHeaderValue);
        if (forwardedProtocols.length == 0) {
            return false;
        }
        for (String forwardedProtocol : forwardedProtocols) {
            if (!this.protocolHeaderHttpsValue.equalsIgnoreCase(forwardedProtocol)) {
                return false;
            }
        }
        return true;
    }

    private void setPorts(XForwardedRequest xrequest, int defaultPort) {
        String portHeaderValue;
        int port = defaultPort;
        if (getPortHeader() != null && (portHeaderValue = xrequest.getHeader(getPortHeader())) != null) {
            try {
                port = Integer.parseInt(portHeaderValue);
            } catch (NumberFormatException e) {
                this.log.debug("Invalid port value [" + portHeaderValue + "] provided in header [" + getPortHeader() + "]");
            }
        }
        xrequest.setServerPort(port);
        if (isChangeLocalPort()) {
            xrequest.setLocalPort(port);
        }
    }

    @Override // jakarta.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
            doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    public boolean isChangeLocalName() {
        return this.changeLocalName;
    }

    public boolean isChangeLocalPort() {
        return this.changeLocalPort;
    }

    public int getHttpsServerPort() {
        return this.httpsServerPort;
    }

    public Pattern getInternalProxies() {
        return this.internalProxies;
    }

    public String getProtocolHeader() {
        return this.protocolHeader;
    }

    public String getPortHeader() {
        return this.portHeader;
    }

    public String getProtocolHeaderHttpsValue() {
        return this.protocolHeaderHttpsValue;
    }

    public String getProxiesHeader() {
        return this.proxiesHeader;
    }

    public String getRemoteIpHeader() {
        return this.remoteIpHeader;
    }

    public boolean getRequestAttributesEnabled() {
        return this.requestAttributesEnabled;
    }

    public Pattern getTrustedProxies() {
        return this.trustedProxies;
    }

    public boolean getEnableLookups() {
        return this.enableLookups;
    }

    @Override // jakarta.servlet.GenericFilter
    public void init() throws ServletException {
        if (getInitParameter(INTERNAL_PROXIES_PARAMETER) != null) {
            setInternalProxies(getInitParameter(INTERNAL_PROXIES_PARAMETER));
        }
        if (getInitParameter(PROTOCOL_HEADER_PARAMETER) != null) {
            setProtocolHeader(getInitParameter(PROTOCOL_HEADER_PARAMETER));
        }
        if (getInitParameter(PROTOCOL_HEADER_HTTPS_VALUE_PARAMETER) != null) {
            setProtocolHeaderHttpsValue(getInitParameter(PROTOCOL_HEADER_HTTPS_VALUE_PARAMETER));
        }
        if (getInitParameter(HOST_HEADER_PARAMETER) != null) {
            setHostHeader(getInitParameter(HOST_HEADER_PARAMETER));
        }
        if (getInitParameter(PORT_HEADER_PARAMETER) != null) {
            setPortHeader(getInitParameter(PORT_HEADER_PARAMETER));
        }
        if (getInitParameter(CHANGE_LOCAL_NAME_PARAMETER) != null) {
            setChangeLocalName(Boolean.parseBoolean(getInitParameter(CHANGE_LOCAL_NAME_PARAMETER)));
        }
        if (getInitParameter(CHANGE_LOCAL_PORT_PARAMETER) != null) {
            setChangeLocalPort(Boolean.parseBoolean(getInitParameter(CHANGE_LOCAL_PORT_PARAMETER)));
        }
        if (getInitParameter(PROXIES_HEADER_PARAMETER) != null) {
            setProxiesHeader(getInitParameter(PROXIES_HEADER_PARAMETER));
        }
        if (getInitParameter(REMOTE_IP_HEADER_PARAMETER) != null) {
            setRemoteIpHeader(getInitParameter(REMOTE_IP_HEADER_PARAMETER));
        }
        if (getInitParameter(TRUSTED_PROXIES_PARAMETER) != null) {
            setTrustedProxies(getInitParameter(TRUSTED_PROXIES_PARAMETER));
        }
        if (getInitParameter(HTTP_SERVER_PORT_PARAMETER) != null) {
            try {
                setHttpServerPort(Integer.parseInt(getInitParameter(HTTP_SERVER_PORT_PARAMETER)));
            } catch (NumberFormatException e) {
                throw new NumberFormatException(sm.getString("remoteIpFilter.invalidNumber", HTTP_SERVER_PORT_PARAMETER, e.getLocalizedMessage()));
            }
        }
        if (getInitParameter(HTTPS_SERVER_PORT_PARAMETER) != null) {
            try {
                setHttpsServerPort(Integer.parseInt(getInitParameter(HTTPS_SERVER_PORT_PARAMETER)));
            } catch (NumberFormatException e2) {
                throw new NumberFormatException(sm.getString("remoteIpFilter.invalidNumber", HTTPS_SERVER_PORT_PARAMETER, e2.getLocalizedMessage()));
            }
        }
        if (getInitParameter(ENABLE_LOOKUPS_PARAMETER) != null) {
            setEnableLookups(Boolean.parseBoolean(getInitParameter(ENABLE_LOOKUPS_PARAMETER)));
        }
    }

    public void setChangeLocalName(boolean changeLocalName) {
        this.changeLocalName = changeLocalName;
    }

    public void setChangeLocalPort(boolean changeLocalPort) {
        this.changeLocalPort = changeLocalPort;
    }

    public void setHttpServerPort(int httpServerPort) {
        this.httpServerPort = httpServerPort;
    }

    public void setHttpsServerPort(int httpsServerPort) {
        this.httpsServerPort = httpsServerPort;
    }

    public void setInternalProxies(String internalProxies) {
        if (internalProxies == null || internalProxies.length() == 0) {
            this.internalProxies = null;
        } else {
            this.internalProxies = Pattern.compile(internalProxies);
        }
    }

    public void setHostHeader(String hostHeader) {
        this.hostHeader = hostHeader;
    }

    public void setPortHeader(String portHeader) {
        this.portHeader = portHeader;
    }

    public void setProtocolHeader(String protocolHeader) {
        this.protocolHeader = protocolHeader;
    }

    public void setProtocolHeaderHttpsValue(String protocolHeaderHttpsValue) {
        this.protocolHeaderHttpsValue = protocolHeaderHttpsValue;
    }

    public void setProxiesHeader(String proxiesHeader) {
        this.proxiesHeader = proxiesHeader;
    }

    public void setRemoteIpHeader(String remoteIpHeader) {
        this.remoteIpHeader = remoteIpHeader;
    }

    public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
        this.requestAttributesEnabled = requestAttributesEnabled;
    }

    public void setTrustedProxies(String trustedProxies) {
        if (trustedProxies == null || trustedProxies.length() == 0) {
            this.trustedProxies = null;
        } else {
            this.trustedProxies = Pattern.compile(trustedProxies);
        }
    }

    public void setEnableLookups(boolean enableLookups) {
        this.enableLookups = enableLookups;
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.log = LogFactory.getLog((Class<?>) RemoteIpFilter.class);
    }
}
