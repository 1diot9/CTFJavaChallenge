package org.apache.catalina.valves;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.NetMask;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.PropertyAccessor;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/RemoteCIDRValve.class */
public final class RemoteCIDRValve extends RequestFilterValve {
    private static final Log log = LogFactory.getLog((Class<?>) RemoteCIDRValve.class);
    private final List<NetMask> allow = new ArrayList();
    private final List<NetMask> deny = new ArrayList();

    @Override // org.apache.catalina.valves.RequestFilterValve
    public String getAllow() {
        return this.allow.toString().replace(PropertyAccessor.PROPERTY_KEY_PREFIX, "").replace("]", "");
    }

    @Override // org.apache.catalina.valves.RequestFilterValve
    public void setAllow(String input) {
        List<String> messages = fillFromInput(input, this.allow);
        if (messages.isEmpty()) {
            return;
        }
        this.allowValid = false;
        for (String message : messages) {
            log.error(message);
        }
        throw new IllegalArgumentException(sm.getString("remoteCidrValve.invalid", "allow"));
    }

    @Override // org.apache.catalina.valves.RequestFilterValve
    public String getDeny() {
        return this.deny.toString().replace(PropertyAccessor.PROPERTY_KEY_PREFIX, "").replace("]", "");
    }

    @Override // org.apache.catalina.valves.RequestFilterValve
    public void setDeny(String input) {
        List<String> messages = fillFromInput(input, this.deny);
        if (messages.isEmpty()) {
            return;
        }
        this.denyValid = false;
        for (String message : messages) {
            log.error(message);
        }
        throw new IllegalArgumentException(sm.getString("remoteCidrValve.invalid", "deny"));
    }

    @Override // org.apache.catalina.valves.RequestFilterValve, org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String property;
        if (getUsePeerAddress()) {
            property = request.getPeerAddr();
        } else {
            property = request.getRequest().getRemoteAddr();
        }
        if (getAddConnectorPort()) {
            property = property + ";" + request.getConnector().getPortWithOffset();
        }
        process(property, request, response);
    }

    @Override // org.apache.catalina.valves.RequestFilterValve
    public boolean isAllowed(String property) {
        String nonPortPart;
        int port;
        int portIdx = property.indexOf(59);
        if (portIdx == -1) {
            if (getAddConnectorPort()) {
                log.error(sm.getString("remoteCidrValve.noPort"));
                return false;
            }
            port = -1;
            nonPortPart = property;
        } else {
            if (!getAddConnectorPort()) {
                log.error(sm.getString("remoteCidrValve.unexpectedPort"));
                return false;
            }
            nonPortPart = property.substring(0, portIdx);
            try {
                port = Integer.parseInt(property.substring(portIdx + 1));
            } catch (NumberFormatException e) {
                log.error(sm.getString("remoteCidrValve.noPort"), e);
                return false;
            }
        }
        try {
            InetAddress addr = InetAddress.getByName(nonPortPart);
            for (NetMask nm : this.deny) {
                if (getAddConnectorPort()) {
                    if (nm.matches(addr, port)) {
                        return false;
                    }
                } else if (nm.matches(addr)) {
                    return false;
                }
            }
            for (NetMask nm2 : this.allow) {
                if (getAddConnectorPort()) {
                    if (nm2.matches(addr, port)) {
                        return true;
                    }
                } else if (nm2.matches(addr)) {
                    return true;
                }
            }
            if (!this.deny.isEmpty() && this.allow.isEmpty()) {
                return true;
            }
            return false;
        } catch (UnknownHostException e2) {
            log.error(sm.getString("remoteCidrValve.noRemoteIp"), e2);
            return false;
        }
    }

    @Override // org.apache.catalina.valves.RequestFilterValve
    protected Log getLog() {
        return log;
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
