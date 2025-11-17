package org.apache.catalina.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.catalina.util.NetMask;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.PropertyAccessor;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/RemoteCIDRFilter.class */
public final class RemoteCIDRFilter extends FilterBase {
    private static final String PLAIN_TEXT_MIME_TYPE = "text/plain";
    private final Log log = LogFactory.getLog((Class<?>) RemoteCIDRFilter.class);
    private final List<NetMask> allow = new ArrayList();
    private final List<NetMask> deny = new ArrayList();

    public String getAllow() {
        return this.allow.toString().replace(PropertyAccessor.PROPERTY_KEY_PREFIX, "").replace("]", "");
    }

    public void setAllow(String input) {
        List<String> messages = fillFromInput(input, this.allow);
        if (messages.isEmpty()) {
            return;
        }
        for (String message : messages) {
            this.log.error(message);
        }
        throw new IllegalArgumentException(sm.getString("remoteCidrFilter.invalid", "allow"));
    }

    public String getDeny() {
        return this.deny.toString().replace(PropertyAccessor.PROPERTY_KEY_PREFIX, "").replace("]", "");
    }

    public void setDeny(String input) {
        List<String> messages = fillFromInput(input, this.deny);
        if (messages.isEmpty()) {
            return;
        }
        for (String message : messages) {
            this.log.error(message);
        }
        throw new IllegalArgumentException(sm.getString("remoteCidrFilter.invalid", "deny"));
    }

    @Override // org.apache.catalina.filters.FilterBase
    protected boolean isConfigProblemFatal() {
        return true;
    }

    @Override // jakarta.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (isAllowed(request.getRemoteAddr())) {
            chain.doFilter(request, response);
        } else if (!(response instanceof HttpServletResponse)) {
            sendErrorWhenNotHttp(response);
        } else {
            ((HttpServletResponse) response).sendError(403);
        }
    }

    @Override // org.apache.catalina.filters.FilterBase
    public Log getLogger() {
        return this.log;
    }

    private boolean isAllowed(String property) {
        try {
            InetAddress addr = InetAddress.getByName(property);
            for (NetMask nm : this.deny) {
                if (nm.matches(addr)) {
                    return false;
                }
            }
            for (NetMask nm2 : this.allow) {
                if (nm2.matches(addr)) {
                    return true;
                }
            }
            if (!this.deny.isEmpty() && this.allow.isEmpty()) {
                return true;
            }
            return false;
        } catch (UnknownHostException e) {
            this.log.error(sm.getString("remoteCidrFilter.noRemoteIp"), e);
            return false;
        }
    }

    private void sendErrorWhenNotHttp(ServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType("text/plain");
        writer.write(sm.getString("http.403"));
        writer.flush();
    }

    private List<String> fillFromInput(String input, List<NetMask> target) {
        target.clear();
        if (input == null || input.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> messages = new ArrayList<>();
        for (String s : input.split("\\s*,\\s*")) {
            try {
                NetMask nm = new NetMask(s);
                target.add(nm);
            } catch (IllegalArgumentException e) {
                messages.add(s + ": " + e.getMessage());
            }
        }
        return Collections.unmodifiableList(messages);
    }
}
