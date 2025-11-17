package org.h2.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/NetworkConnectionInfo.class */
public final class NetworkConnectionInfo {
    private final String server;
    private final byte[] clientAddr;
    private final int clientPort;
    private final String clientInfo;

    public NetworkConnectionInfo(String str, String str2, int i) throws UnknownHostException {
        this(str, InetAddress.getByName(str2).getAddress(), i, null);
    }

    public NetworkConnectionInfo(String str, byte[] bArr, int i, String str2) {
        this.server = str;
        this.clientAddr = bArr;
        this.clientPort = i;
        this.clientInfo = str2;
    }

    public String getServer() {
        return this.server;
    }

    public byte[] getClientAddr() {
        return this.clientAddr;
    }

    public int getClientPort() {
        return this.clientPort;
    }

    public String getClientInfo() {
        return this.clientInfo;
    }

    public String getClient() {
        return NetUtils.ipToShortForm(new StringBuilder(), this.clientAddr, true).append(':').append(this.clientPort).toString();
    }
}
