package org.apache.tomcat.util.net.openssl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/OpenSSLConf.class */
public class OpenSSLConf implements Serializable {
    private static final long serialVersionUID = 1;
    private final List<OpenSSLConfCmd> commands = new ArrayList();

    public void addCmd(OpenSSLConfCmd cmd) {
        this.commands.add(cmd);
    }

    public List<OpenSSLConfCmd> getCommands() {
        return this.commands;
    }
}
