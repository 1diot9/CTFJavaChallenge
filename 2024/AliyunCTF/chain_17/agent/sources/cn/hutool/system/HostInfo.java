package cn.hutool.system;

import cn.hutool.core.net.NetUtil;
import java.io.Serializable;
import java.net.InetAddress;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/system/HostInfo.class */
public class HostInfo implements Serializable {
    private static final long serialVersionUID = 1;
    private final String HOST_NAME;
    private final String HOST_ADDRESS;

    public HostInfo() {
        InetAddress localhost = NetUtil.getLocalhost();
        if (null != localhost) {
            this.HOST_NAME = localhost.getHostName();
            this.HOST_ADDRESS = localhost.getHostAddress();
        } else {
            this.HOST_NAME = null;
            this.HOST_ADDRESS = null;
        }
    }

    public final String getName() {
        return this.HOST_NAME;
    }

    public final String getAddress() {
        return this.HOST_ADDRESS;
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder();
        SystemUtil.append(builder, "Host Name:    ", getName());
        SystemUtil.append(builder, "Host Address: ", getAddress());
        return builder.toString();
    }
}
