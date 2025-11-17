package org.apache.catalina.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/NetMaskSet.class */
public class NetMaskSet {
    private final Set<NetMask> netmasks = new HashSet();

    public boolean contains(InetAddress inetAddress) {
        for (NetMask nm : this.netmasks) {
            if (nm.matches(inetAddress)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(String ipAddress) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(ipAddress);
        return contains(inetAddress);
    }

    public boolean add(NetMask netmask) {
        return this.netmasks.add(netmask);
    }

    public boolean add(String input) {
        NetMask netmask = new NetMask(input);
        return this.netmasks.add(netmask);
    }

    public void clear() {
        this.netmasks.clear();
    }

    public boolean isEmpty() {
        return this.netmasks.isEmpty();
    }

    public List<String> addAll(String input) {
        if (input == null || input.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> errMessages = new ArrayList<>();
        for (String s : input.split("\\s*,\\s*")) {
            try {
                add(s);
            } catch (IllegalArgumentException e) {
                errMessages.add(s + ": " + e.getMessage());
            }
        }
        return Collections.unmodifiableList(errMessages);
    }

    public String toString() {
        String result = this.netmasks.toString();
        if (result.startsWith("[")) {
            result = result.substring(1);
        }
        if (result.endsWith("]")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
