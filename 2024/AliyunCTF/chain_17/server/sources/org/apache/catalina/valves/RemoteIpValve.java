package org.apache.catalina.valves;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Enumeration;
import java.util.regex.Pattern;
import org.apache.catalina.AccessLog;
import org.apache.catalina.Globals;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.Host;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/RemoteIpValve.class */
public class RemoteIpValve extends ValveBase {
    private static final Pattern commaSeparatedValuesPattern = Pattern.compile("\\s*,\\s*");
    private static final Log log = LogFactory.getLog((Class<?>) RemoteIpValve.class);
    private String hostHeader;
    private boolean changeLocalName;
    private int httpServerPort;
    private int httpsServerPort;
    private String portHeader;
    private boolean changeLocalPort;
    private Pattern internalProxies;
    private String protocolHeader;
    private String protocolHeaderHttpsValue;
    private String proxiesHeader;
    private String remoteIpHeader;
    private boolean requestAttributesEnabled;
    private Pattern trustedProxies;

    protected static String[] commaDelimitedListToStringArray(String commaDelimitedStrings) {
        return (commaDelimitedStrings == null || commaDelimitedStrings.length() == 0) ? new String[0] : commaSeparatedValuesPattern.split(commaDelimitedStrings);
    }

    public RemoteIpValve() {
        super(true);
        this.hostHeader = null;
        this.changeLocalName = false;
        this.httpServerPort = 80;
        this.httpsServerPort = 443;
        this.portHeader = null;
        this.changeLocalPort = false;
        this.internalProxies = Pattern.compile("10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|192\\.168\\.\\d{1,3}\\.\\d{1,3}|169\\.254\\.\\d{1,3}\\.\\d{1,3}|127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|100\\.6[4-9]{1}\\.\\d{1,3}\\.\\d{1,3}|100\\.[7-9]{1}\\d{1}\\.\\d{1,3}\\.\\d{1,3}|100\\.1[0-1]{1}\\d{1}\\.\\d{1,3}\\.\\d{1,3}|100\\.12[0-7]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.1[6-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.2[0-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.3[0-1]{1}\\.\\d{1,3}\\.\\d{1,3}|0:0:0:0:0:0:0:1|::1");
        this.protocolHeader = "X-Forwarded-Proto";
        this.protocolHeaderHttpsValue = "https";
        this.proxiesHeader = "X-Forwarded-By";
        this.remoteIpHeader = "X-Forwarded-For";
        this.requestAttributesEnabled = true;
        this.trustedProxies = null;
    }

    public String getHostHeader() {
        return this.hostHeader;
    }

    public void setHostHeader(String hostHeader) {
        this.hostHeader = hostHeader;
    }

    public boolean isChangeLocalName() {
        return this.changeLocalName;
    }

    public void setChangeLocalName(boolean changeLocalName) {
        this.changeLocalName = changeLocalName;
    }

    public int getHttpServerPort() {
        return this.httpServerPort;
    }

    public int getHttpsServerPort() {
        return this.httpsServerPort;
    }

    public String getPortHeader() {
        return this.portHeader;
    }

    public void setPortHeader(String portHeader) {
        this.portHeader = portHeader;
    }

    public boolean isChangeLocalPort() {
        return this.changeLocalPort;
    }

    public void setChangeLocalPort(boolean changeLocalPort) {
        this.changeLocalPort = changeLocalPort;
    }

    public String getInternalProxies() {
        if (this.internalProxies == null) {
            return null;
        }
        return this.internalProxies.toString();
    }

    public String getProtocolHeader() {
        return this.protocolHeader;
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

    public String getTrustedProxies() {
        if (this.trustedProxies == null) {
            return null;
        }
        return this.trustedProxies.toString();
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String protocolHeaderValue;
        String originalRemoteAddr = request.getRemoteAddr();
        String originalRemoteHost = request.getRemoteHost();
        String originalScheme = request.getScheme();
        boolean originalSecure = request.isSecure();
        String originalServerName = request.getServerName();
        String originalLocalName = isChangeLocalName() ? request.getLocalName() : null;
        int originalServerPort = request.getServerPort();
        int originalLocalPort = request.getLocalPort();
        String originalProxiesHeader = request.getHeader(this.proxiesHeader);
        String originalRemoteIpHeader = request.getHeader(this.remoteIpHeader);
        boolean isInternal = this.internalProxies != null && this.internalProxies.matcher(originalRemoteAddr).matches();
        if (isInternal || (this.trustedProxies != null && this.trustedProxies.matcher(originalRemoteAddr).matches())) {
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
                proxiesHeaderValue.addFirst(originalRemoteAddr);
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
            Deque<String> newRemoteIpHeaderValue = new ArrayDeque<>();
            while (idx >= 0) {
                newRemoteIpHeaderValue.addFirst(remoteIpHeaderValue[idx]);
                idx--;
            }
            if (remoteIp != null) {
                request.setRemoteAddr(remoteIp);
                if (request.getConnector().getEnableLookups()) {
                    try {
                        InetAddress inetAddress = InetAddress.getByName(remoteIp);
                        request.setRemoteHost(inetAddress.getCanonicalHostName());
                    } catch (UnknownHostException e2) {
                        log.debug(sm.getString("remoteIpValve.invalidRemoteAddress", remoteIp), e2);
                        request.setRemoteHost(remoteIp);
                    }
                } else {
                    request.setRemoteHost(remoteIp);
                }
                if (proxiesHeaderValue.size() == 0) {
                    request.getCoyoteRequest().getMimeHeaders().removeHeader(this.proxiesHeader);
                } else {
                    String commaDelimitedListOfProxies = StringUtils.join(proxiesHeaderValue);
                    request.getCoyoteRequest().getMimeHeaders().setValue(this.proxiesHeader).setString(commaDelimitedListOfProxies);
                }
                if (newRemoteIpHeaderValue.size() == 0) {
                    request.getCoyoteRequest().getMimeHeaders().removeHeader(this.remoteIpHeader);
                } else {
                    String commaDelimitedRemoteIpHeaderValue = StringUtils.join(newRemoteIpHeaderValue);
                    request.getCoyoteRequest().getMimeHeaders().setValue(this.remoteIpHeader).setString(commaDelimitedRemoteIpHeaderValue);
                }
            }
            if (this.protocolHeader != null && (protocolHeaderValue = request.getHeader(this.protocolHeader)) != null) {
                if (isForwardedProtoHeaderValueSecure(protocolHeaderValue)) {
                    request.setSecure(true);
                    request.getCoyoteRequest().scheme().setString("https");
                    setPorts(request, this.httpsServerPort);
                } else {
                    request.setSecure(false);
                    request.getCoyoteRequest().scheme().setString("http");
                    setPorts(request, this.httpServerPort);
                }
            }
            if (this.hostHeader != null) {
                String hostHeaderValue = request.getHeader(this.hostHeader);
                if (hostHeaderValue != null) {
                    try {
                        int portIndex = Host.parse(hostHeaderValue);
                        if (portIndex > -1) {
                            log.debug(sm.getString("remoteIpValve.invalidHostWithPort", hostHeaderValue, this.hostHeader));
                            hostHeaderValue = hostHeaderValue.substring(0, portIndex);
                        }
                        request.getCoyoteRequest().serverName().setString(hostHeaderValue);
                        if (isChangeLocalName()) {
                            request.getCoyoteRequest().localName().setString(hostHeaderValue);
                        }
                    } catch (IllegalArgumentException e3) {
                        log.debug(sm.getString("remoteIpValve.invalidHostHeader", hostHeaderValue, this.hostHeader));
                    }
                }
            }
            request.setAttribute(Globals.REQUEST_FORWARDED_ATTRIBUTE, Boolean.TRUE);
            if (log.isDebugEnabled()) {
                log.debug("Incoming request " + request.getRequestURI() + " with originalRemoteAddr [" + originalRemoteAddr + "], originalRemoteHost=[" + originalRemoteHost + "], originalSecure=[" + originalSecure + "], originalScheme=[" + originalScheme + "], originalServerName=[" + originalServerName + "], originalServerPort=[" + originalServerPort + "] will be seen as newRemoteAddr=[" + request.getRemoteAddr() + "], newRemoteHost=[" + request.getRemoteHost() + "], newSecure=[" + request.isSecure() + "], newScheme=[" + request.getScheme() + "], newServerName=[" + request.getServerName() + "], newServerPort=[" + request.getServerPort() + "]");
            }
        } else if (log.isDebugEnabled()) {
            log.debug("Skip RemoteIpValve for request " + request.getRequestURI() + " with originalRemoteAddr '" + request.getRemoteAddr() + "'");
        }
        if (this.requestAttributesEnabled) {
            request.setAttribute(AccessLog.REMOTE_ADDR_ATTRIBUTE, request.getRemoteAddr());
            request.setAttribute("org.apache.tomcat.remoteAddr", request.getRemoteAddr());
            request.setAttribute(AccessLog.REMOTE_HOST_ATTRIBUTE, request.getRemoteHost());
            request.setAttribute(AccessLog.PROTOCOL_ATTRIBUTE, request.getProtocol());
            request.setAttribute(AccessLog.SERVER_NAME_ATTRIBUTE, request.getServerName());
            request.setAttribute(AccessLog.SERVER_PORT_ATTRIBUTE, Integer.valueOf(request.getServerPort()));
        }
        try {
            getNext().invoke(request, response);
            if (!request.isAsync()) {
                request.setRemoteAddr(originalRemoteAddr);
                request.setRemoteHost(originalRemoteHost);
                request.setSecure(originalSecure);
                request.getCoyoteRequest().scheme().setString(originalScheme);
                request.getCoyoteRequest().serverName().setString(originalServerName);
                if (isChangeLocalName()) {
                    request.getCoyoteRequest().localName().setString(originalLocalName);
                }
                request.setServerPort(originalServerPort);
                request.setLocalPort(originalLocalPort);
                MimeHeaders headers = request.getCoyoteRequest().getMimeHeaders();
                if (originalProxiesHeader == null || originalProxiesHeader.length() == 0) {
                    headers.removeHeader(this.proxiesHeader);
                } else {
                    headers.setValue(this.proxiesHeader).setString(originalProxiesHeader);
                }
                if (originalRemoteIpHeader == null || originalRemoteIpHeader.length() == 0) {
                    headers.removeHeader(this.remoteIpHeader);
                } else {
                    headers.setValue(this.remoteIpHeader).setString(originalRemoteIpHeader);
                }
            }
        } catch (Throwable th) {
            if (!request.isAsync()) {
                request.setRemoteAddr(originalRemoteAddr);
                request.setRemoteHost(originalRemoteHost);
                request.setSecure(originalSecure);
                request.getCoyoteRequest().scheme().setString(originalScheme);
                request.getCoyoteRequest().serverName().setString(originalServerName);
                if (isChangeLocalName()) {
                    request.getCoyoteRequest().localName().setString(originalLocalName);
                }
                request.setServerPort(originalServerPort);
                request.setLocalPort(originalLocalPort);
                MimeHeaders headers2 = request.getCoyoteRequest().getMimeHeaders();
                if (originalProxiesHeader == null || originalProxiesHeader.length() == 0) {
                    headers2.removeHeader(this.proxiesHeader);
                } else {
                    headers2.setValue(this.proxiesHeader).setString(originalProxiesHeader);
                }
                if (originalRemoteIpHeader == null || originalRemoteIpHeader.length() == 0) {
                    headers2.removeHeader(this.remoteIpHeader);
                } else {
                    headers2.setValue(this.remoteIpHeader).setString(originalRemoteIpHeader);
                }
            }
            throw th;
        }
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

    private void setPorts(Request request, int defaultPort) {
        String portHeaderValue;
        int port = defaultPort;
        if (this.portHeader != null && (portHeaderValue = request.getHeader(this.portHeader)) != null) {
            try {
                port = Integer.parseInt(portHeaderValue);
            } catch (NumberFormatException nfe) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("remoteIpValve.invalidPortHeader", portHeaderValue, this.portHeader), nfe);
                }
            }
        }
        request.setServerPort(port);
        if (this.changeLocalPort) {
            request.setLocalPort(port);
        }
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
}
