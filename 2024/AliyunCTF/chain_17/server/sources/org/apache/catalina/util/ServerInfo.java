package org.apache.catalina.util;

import java.io.InputStream;
import java.util.Properties;
import org.apache.tomcat.util.ExceptionUtils;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/ServerInfo.class */
public class ServerInfo {
    private static final String serverInfo;
    private static final String serverBuilt;
    private static final String serverNumber;

    static {
        String info = null;
        String built = null;
        String number = null;
        Properties props = new Properties();
        try {
            InputStream is = ServerInfo.class.getResourceAsStream("/org/apache/catalina/util/ServerInfo.properties");
            try {
                props.load(is);
                info = props.getProperty("server.info");
                built = props.getProperty("server.built");
                number = props.getProperty("server.number");
                if (is != null) {
                    is.close();
                }
            } finally {
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
        if (info == null || info.equals("Apache Tomcat/@VERSION@")) {
            info = "Apache Tomcat/10.1.x-dev";
        }
        if (built == null || built.equals("@VERSION_BUILT@")) {
            built = "unknown";
        }
        if (number == null || number.equals("@VERSION_NUMBER@")) {
            number = "10.1.x";
        }
        serverInfo = info;
        serverBuilt = built;
        serverNumber = number;
    }

    public static String getServerInfo() {
        return serverInfo;
    }

    public static String getServerBuilt() {
        return serverBuilt;
    }

    public static String getServerNumber() {
        return serverNumber;
    }

    public static void main(String[] args) {
        System.out.println("Server version: " + getServerInfo());
        System.out.println("Server built:   " + getServerBuilt());
        System.out.println("Server number:  " + getServerNumber());
        System.out.println("OS Name:        " + System.getProperty("os.name"));
        System.out.println("OS Version:     " + System.getProperty("os.version"));
        System.out.println("Architecture:   " + System.getProperty("os.arch"));
        System.out.println("JVM Version:    " + System.getProperty("java.runtime.version"));
        System.out.println("JVM Vendor:     " + System.getProperty("java.vm.vendor"));
    }
}
