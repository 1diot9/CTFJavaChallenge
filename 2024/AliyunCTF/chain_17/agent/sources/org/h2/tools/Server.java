package org.h2.tools;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import org.h2.api.ErrorCode;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.server.Service;
import org.h2.server.ShutdownHandler;
import org.h2.server.TcpServer;
import org.h2.server.pg.PgServer;
import org.h2.server.web.WebServer;
import org.h2.util.StringUtils;
import org.h2.util.Tool;
import org.h2.util.Utils;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/Server.class */
public class Server extends Tool implements Runnable, ShutdownHandler {
    private final Service service;
    private Server web;
    private Server tcp;
    private Server pg;
    private ShutdownHandler shutdownHandler;
    private boolean fromCommandLine;
    private boolean started;

    public Server() {
        this.service = null;
    }

    public Server(Service service, String... strArr) throws SQLException {
        verifyArgs(strArr);
        this.service = service;
        try {
            service.init(strArr);
        } catch (Exception e) {
            throw DbException.toSQLException(e);
        }
    }

    public static void main(String... strArr) throws SQLException {
        Server server = new Server();
        server.fromCommandLine = true;
        server.runTool(strArr);
    }

    private void verifyArgs(String... strArr) throws SQLException {
        int i = 0;
        while (strArr != null && i < strArr.length) {
            String str = strArr[i];
            if (str != null && !"-?".equals(str) && !"-help".equals(str)) {
                if (str.startsWith("-web")) {
                    if (!"-web".equals(str) && !"-webAllowOthers".equals(str)) {
                        if ("-webExternalNames".equals(str)) {
                            i++;
                        } else if (!"-webDaemon".equals(str)) {
                            if ("-webVirtualThreads".equals(str)) {
                                i++;
                            } else if (!"-webSSL".equals(str)) {
                                if ("-webPort".equals(str)) {
                                    i++;
                                } else if ("-webAdminPassword".equals(str)) {
                                    if (this.fromCommandLine) {
                                        throwUnsupportedOption(str);
                                    }
                                    i++;
                                } else {
                                    throwUnsupportedOption(str);
                                }
                            }
                        }
                    }
                } else if (!"-browser".equals(str)) {
                    if (str.startsWith("-tcp")) {
                        if (!"-tcp".equals(str) && !"-tcpAllowOthers".equals(str) && !"-tcpDaemon".equals(str)) {
                            if ("-tcpVirtualThreads".equals(str)) {
                                i++;
                            } else if (!"-tcpSSL".equals(str)) {
                                if ("-tcpPort".equals(str)) {
                                    i++;
                                } else if ("-tcpPassword".equals(str)) {
                                    i++;
                                } else if ("-tcpShutdown".equals(str)) {
                                    i++;
                                } else if (!"-tcpShutdownForce".equals(str)) {
                                    throwUnsupportedOption(str);
                                }
                            }
                        }
                    } else if (str.startsWith("-pg")) {
                        if (!"-pg".equals(str) && !"-pgAllowOthers".equals(str) && !"-pgDaemon".equals(str)) {
                            if ("-pgVirtualThreads".equals(str)) {
                                i++;
                            } else if ("-pgPort".equals(str)) {
                                i++;
                            } else {
                                throwUnsupportedOption(str);
                            }
                        }
                    } else if (str.startsWith("-ftp")) {
                        if ("-ftpPort".equals(str)) {
                            i++;
                        } else if ("-ftpDir".equals(str)) {
                            i++;
                        } else if ("-ftpRead".equals(str)) {
                            i++;
                        } else if ("-ftpWrite".equals(str)) {
                            i++;
                        } else if ("-ftpWritePassword".equals(str)) {
                            i++;
                        } else if (!"-ftpTask".equals(str)) {
                            throwUnsupportedOption(str);
                        }
                    } else if ("-properties".equals(str)) {
                        i++;
                    } else if (!"-trace".equals(str) && !"-ifExists".equals(str) && !"-ifNotExists".equals(str)) {
                        if ("-baseDir".equals(str)) {
                            i++;
                        } else if ("-key".equals(str)) {
                            i += 2;
                        } else if (!"-tool".equals(str)) {
                            throwUnsupportedOption(str);
                        }
                    }
                }
            }
            i++;
        }
    }

    @Override // org.h2.util.Tool
    public void runTool(String... strArr) throws SQLException {
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        boolean z5 = false;
        boolean z6 = false;
        String str = "";
        String str2 = "";
        boolean z7 = true;
        int i = 0;
        while (strArr != null && i < strArr.length) {
            String str3 = strArr[i];
            if (str3 != null) {
                if ("-?".equals(str3) || "-help".equals(str3)) {
                    showUsage();
                    return;
                }
                if (str3.startsWith("-web")) {
                    if ("-web".equals(str3)) {
                        z7 = false;
                        z3 = true;
                    } else if (!"-webAllowOthers".equals(str3)) {
                        if ("-webExternalNames".equals(str3)) {
                            i++;
                        } else if (!"-webDaemon".equals(str3) && !"-webSSL".equals(str3)) {
                            if ("-webPort".equals(str3)) {
                                i++;
                            } else if ("-webAdminPassword".equals(str3)) {
                                if (this.fromCommandLine) {
                                    throwUnsupportedOption(str3);
                                }
                                i++;
                            } else {
                                showUsageAndThrowUnsupportedOption(str3);
                            }
                        }
                    }
                } else if ("-browser".equals(str3)) {
                    z7 = false;
                    z4 = true;
                } else if (str3.startsWith("-tcp")) {
                    if ("-tcp".equals(str3)) {
                        z7 = false;
                        z = true;
                    } else if (!"-tcpAllowOthers".equals(str3) && !"-tcpDaemon".equals(str3) && !"-tcpSSL".equals(str3)) {
                        if ("-tcpPort".equals(str3)) {
                            i++;
                        } else if ("-tcpPassword".equals(str3)) {
                            i++;
                            str = strArr[i];
                        } else if ("-tcpShutdown".equals(str3)) {
                            z7 = false;
                            z5 = true;
                            i++;
                            str2 = strArr[i];
                        } else if ("-tcpShutdownForce".equals(str3)) {
                            z6 = true;
                        } else {
                            showUsageAndThrowUnsupportedOption(str3);
                        }
                    }
                } else if (str3.startsWith("-pg")) {
                    if ("-pg".equals(str3)) {
                        z7 = false;
                        z2 = true;
                    } else if (!"-pgAllowOthers".equals(str3) && !"-pgDaemon".equals(str3)) {
                        if ("-pgPort".equals(str3)) {
                            i++;
                        } else {
                            showUsageAndThrowUnsupportedOption(str3);
                        }
                    }
                } else if ("-properties".equals(str3)) {
                    i++;
                } else if (!"-trace".equals(str3) && !"-ifExists".equals(str3) && !"-ifNotExists".equals(str3)) {
                    if ("-baseDir".equals(str3)) {
                        i++;
                    } else if ("-key".equals(str3)) {
                        i += 2;
                    } else {
                        showUsageAndThrowUnsupportedOption(str3);
                    }
                }
            }
            i++;
        }
        verifyArgs(strArr);
        if (z7) {
            z = true;
            z2 = true;
            z3 = true;
            z4 = true;
        }
        if (z5) {
            this.out.println("Shutting down TCP Server at " + str2);
            shutdownTcpServer(str2, str, z6, false);
        }
        if (z) {
            try {
                this.tcp = createTcpServer(strArr);
                this.tcp.start();
                this.out.println(this.tcp.getStatus());
                this.tcp.setShutdownHandler(this);
            } catch (SQLException e) {
                stopAll();
                throw e;
            }
        }
        if (z2) {
            this.pg = createPgServer(strArr);
            this.pg.start();
            this.out.println(this.pg.getStatus());
        }
        if (z3) {
            this.web = createWebServer(strArr);
            this.web.setShutdownHandler(this);
            SQLException sQLException = null;
            try {
                this.web.start();
            } catch (Exception e2) {
                sQLException = DbException.toSQLException(e2);
            }
            this.out.println(this.web.getStatus());
            if (z4) {
                try {
                    openBrowser(this.web.getURL());
                } catch (Exception e3) {
                    this.out.println(e3.getMessage());
                }
            }
            if (sQLException != null) {
                throw sQLException;
            }
        } else if (z4) {
            this.out.println("The browser can only start if a web server is started (-web)");
        }
    }

    public static void shutdownTcpServer(String str, String str2, boolean z, boolean z2) throws SQLException {
        TcpServer.shutdown(str, str2, z, z2);
    }

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        if (!this.started) {
            sb.append("Not started");
        } else if (isRunning(false)) {
            sb.append(this.service.getType()).append(" server running at ").append(this.service.getURL()).append(" (");
            if (this.service.getAllowOthers()) {
                sb.append("others can connect");
            } else {
                sb.append("only local connections");
            }
            sb.append(')');
        } else {
            sb.append("The ").append(this.service.getType()).append(" server could not be started. Possible cause: another server is already running at ").append(this.service.getURL());
        }
        return sb.toString();
    }

    public static Server createWebServer(String... strArr) throws SQLException {
        return createWebServer(strArr, null, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Server createWebServer(String[] strArr, String str, boolean z) throws SQLException {
        WebServer webServer = new WebServer();
        webServer.setKey(str);
        webServer.setAllowSecureCreation(z);
        Server server = new Server(webServer, strArr);
        webServer.setShutdownHandler(server);
        return server;
    }

    public static Server createTcpServer(String... strArr) throws SQLException {
        TcpServer tcpServer = new TcpServer();
        Server server = new Server(tcpServer, strArr);
        tcpServer.setShutdownHandler(server);
        return server;
    }

    public static Server createPgServer(String... strArr) throws SQLException {
        return new Server(new PgServer(), strArr);
    }

    public Server start() throws SQLException {
        try {
            this.started = true;
            this.service.start();
            String url = this.service.getURL();
            int indexOf = url.indexOf(63);
            if (indexOf >= 0) {
                url = url.substring(0, indexOf);
            }
            String str = this.service.getName() + " (" + url + ')';
            Thread thread = new Thread(this, str);
            thread.setDaemon(this.service.isDaemon());
            thread.start();
            for (int i = 1; i < 64; i += i) {
                wait(i);
                if (isRunning(false)) {
                    return this;
                }
            }
            if (isRunning(true)) {
                return this;
            }
            throw DbException.get(ErrorCode.EXCEPTION_OPENING_PORT_2, str, "timeout; please check your network configuration, specially the file /etc/hosts");
        } catch (DbException e) {
            throw DbException.toSQLException(e);
        }
    }

    private static void wait(int i) {
        try {
            Thread.sleep(i * i);
        } catch (InterruptedException e) {
        }
    }

    private void stopAll() {
        Server server = this.web;
        if (server != null && server.isRunning(false)) {
            server.stop();
            this.web = null;
        }
        Server server2 = this.tcp;
        if (server2 != null && server2.isRunning(false)) {
            server2.stop();
            this.tcp = null;
        }
        Server server3 = this.pg;
        if (server3 != null && server3.isRunning(false)) {
            server3.stop();
            this.pg = null;
        }
    }

    public boolean isRunning(boolean z) {
        return this.service.isRunning(z);
    }

    public void stop() {
        this.started = false;
        if (this.service != null) {
            this.service.stop();
        }
    }

    public String getURL() {
        return this.service.getURL();
    }

    public int getPort() {
        return this.service.getPort();
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.service.listen();
        } catch (Exception e) {
            DbException.traceThrowable(e);
        }
    }

    public void setShutdownHandler(ShutdownHandler shutdownHandler) {
        this.shutdownHandler = shutdownHandler;
    }

    @Override // org.h2.server.ShutdownHandler
    public void shutdown() {
        if (this.shutdownHandler != null) {
            this.shutdownHandler.shutdown();
        } else {
            stopAll();
        }
    }

    public Service getService() {
        return this.service;
    }

    public static void openBrowser(String str) throws Exception {
        Class<?> cls;
        Boolean bool;
        URI uri;
        try {
            String lowerEnglish = StringUtils.toLowerEnglish(Utils.getProperty("os.name", "linux"));
            Runtime runtime = Runtime.getRuntime();
            String property = Utils.getProperty(SysProperties.H2_BROWSER, (String) null);
            if (property == null) {
                try {
                    property = System.getenv("BROWSER");
                } catch (SecurityException e) {
                }
            }
            if (property != null) {
                if (property.startsWith("call:")) {
                    Utils.callStaticMethod(property.substring("call:".length()), str);
                    return;
                }
                if (!property.contains("%url")) {
                    if (lowerEnglish.contains("windows")) {
                        runtime.exec(new String[]{"cmd.exe", "/C", property, str});
                        return;
                    } else {
                        runtime.exec(new String[]{property, str});
                        return;
                    }
                }
                String[] arraySplit = StringUtils.arraySplit(property, ',', false);
                for (int i = 0; i < arraySplit.length; i++) {
                    arraySplit[i] = StringUtils.replaceAll(arraySplit[i], "%url", str);
                }
                runtime.exec(arraySplit);
                return;
            }
            try {
                cls = Class.forName("java.awt.Desktop");
                bool = (Boolean) cls.getMethod("isDesktopSupported", new Class[0]).invoke(null, new Object[0]);
                uri = new URI(str);
            } catch (Exception e2) {
            }
            if (bool.booleanValue()) {
                cls.getMethod("browse", URI.class).invoke(cls.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]), uri);
                return;
            }
            if (lowerEnglish.contains("windows")) {
                runtime.exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", str});
            } else if (lowerEnglish.contains("mac") || lowerEnglish.contains("darwin")) {
                Runtime.getRuntime().exec(new String[]{"open", str});
            } else {
                boolean z = false;
                for (String str2 : new String[]{"xdg-open", "chromium", "google-chrome", "firefox", "mozilla-firefox", "mozilla", "konqueror", "netscape", "opera", "midori"}) {
                    try {
                        runtime.exec(new String[]{str2, str});
                        z = true;
                        break;
                    } catch (Exception e3) {
                    }
                }
                if (!z) {
                    throw new Exception("Browser detection failed, and java property 'h2.browser' and environment variable BROWSER are not set to a browser executable.");
                }
            }
        } catch (Exception e4) {
            throw new Exception("Failed to start a browser to open the URL " + str + ": " + e4.getMessage());
        }
    }

    public static void startWebServer(Connection connection) throws SQLException {
        startWebServer(connection, false);
    }

    public static void startWebServer(Connection connection, boolean z) throws SQLException {
        String[] strArr;
        WebServer webServer = new WebServer();
        if (z) {
            strArr = new String[]{"-webPort", CustomBooleanEditor.VALUE_0, "-properties", "null"};
        } else {
            strArr = new String[]{"-webPort", CustomBooleanEditor.VALUE_0};
        }
        Server server = new Server(webServer, strArr);
        server.start();
        Server server2 = new Server();
        server2.web = server;
        webServer.setShutdownHandler(server2);
        try {
            openBrowser(webServer.addSession(connection));
            while (!webServer.isStopped()) {
                Thread.sleep(1000L);
            }
        } catch (Exception e) {
        }
    }
}
