package org.h2.tools;

import java.sql.SQLException;
import org.h2.server.ShutdownHandler;
import org.h2.util.JdbcUtils;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.Tool;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/Console.class */
public class Console extends Tool implements ShutdownHandler {
    Server web;
    private Server tcp;
    private Server pg;
    boolean isWindows;

    public static void main(String... strArr) throws SQLException {
        Console console;
        try {
            console = (Console) Utils.newInstance("org.h2.tools.GUIConsole", new Object[0]);
        } catch (Exception | NoClassDefFoundError e) {
            console = new Console();
        }
        console.runTool(strArr);
    }

    @Override // org.h2.util.Tool
    public void runTool(String... strArr) throws SQLException {
        String convertBytesToHex;
        this.isWindows = Utils.getProperty("os.name", "").startsWith("Windows");
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        boolean z5 = false;
        boolean z6 = true;
        boolean z7 = strArr != null && strArr.length > 0;
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        boolean z8 = false;
        boolean z9 = false;
        String str5 = "";
        String str6 = "";
        boolean z10 = false;
        boolean z11 = false;
        int i = 0;
        while (strArr != null && i < strArr.length) {
            String str7 = strArr[i];
            if (str7 != null) {
                if ("-?".equals(str7) || "-help".equals(str7)) {
                    showUsage();
                    return;
                }
                if ("-url".equals(str7)) {
                    z6 = false;
                    i++;
                    str2 = strArr[i];
                } else if ("-driver".equals(str7)) {
                    i++;
                    str = strArr[i];
                } else if ("-user".equals(str7)) {
                    i++;
                    str3 = strArr[i];
                } else if ("-password".equals(str7)) {
                    i++;
                    str4 = strArr[i];
                } else if (str7.startsWith("-web")) {
                    if ("-web".equals(str7)) {
                        z6 = false;
                        z3 = true;
                    } else if ("-webAllowOthers".equals(str7)) {
                        z11 = true;
                    } else if ("-webExternalNames".equals(str7)) {
                        i++;
                    } else if (!"-webDaemon".equals(str7)) {
                        if ("-webVirtualThreads".equals(str7)) {
                            i++;
                        } else if (!"-webSSL".equals(str7)) {
                            if ("-webPort".equals(str7)) {
                                i++;
                            } else {
                                showUsageAndThrowUnsupportedOption(str7);
                            }
                        }
                    }
                } else if ("-tool".equals(str7)) {
                    z6 = false;
                    z3 = true;
                    z4 = true;
                } else if ("-browser".equals(str7)) {
                    z6 = false;
                    z3 = true;
                    z5 = true;
                } else if (str7.startsWith("-tcp")) {
                    if ("-tcp".equals(str7)) {
                        z6 = false;
                        z = true;
                    } else if (!"-tcpAllowOthers".equals(str7) && !"-tcpDaemon".equals(str7)) {
                        if ("-tcpVirtualThreads".equals(str7)) {
                            i++;
                        } else if (!"-tcpSSL".equals(str7)) {
                            if ("-tcpPort".equals(str7)) {
                                i++;
                            } else if ("-tcpPassword".equals(str7)) {
                                i++;
                                str5 = strArr[i];
                            } else if ("-tcpShutdown".equals(str7)) {
                                z6 = false;
                                z8 = true;
                                i++;
                                str6 = strArr[i];
                            } else if ("-tcpShutdownForce".equals(str7)) {
                                z9 = true;
                            } else {
                                showUsageAndThrowUnsupportedOption(str7);
                            }
                        }
                    }
                } else if (str7.startsWith("-pg")) {
                    if ("-pg".equals(str7)) {
                        z6 = false;
                        z2 = true;
                    } else if (!"-pgAllowOthers".equals(str7) && !"-pgDaemon".equals(str7)) {
                        if ("-pgVirtualThreads".equals(str7)) {
                            i++;
                        } else if ("-pgPort".equals(str7)) {
                            i++;
                        } else {
                            showUsageAndThrowUnsupportedOption(str7);
                        }
                    }
                } else if ("-properties".equals(str7)) {
                    i++;
                } else if (!"-trace".equals(str7)) {
                    if ("-ifExists".equals(str7)) {
                        z10 = true;
                    } else if ("-baseDir".equals(str7)) {
                        i++;
                    } else {
                        showUsageAndThrowUnsupportedOption(str7);
                    }
                }
            }
            i++;
        }
        if (z6) {
            z3 = true;
            z4 = true;
            z5 = true;
            z = true;
            z2 = true;
        }
        if (z8) {
            this.out.println("Shutting down TCP Server at " + str6);
            Server.shutdownTcpServer(str6, str5, z9, false);
        }
        SQLException sQLException = null;
        boolean z12 = false;
        if (str2 != null) {
            Server.startWebServer(JdbcUtils.getConnection(str, str2, str3, str4));
        }
        if (z3) {
            if (z11) {
                convertBytesToHex = null;
            } else {
                try {
                    convertBytesToHex = StringUtils.convertBytesToHex(MathUtils.secureRandomBytes(32));
                } catch (SQLException e) {
                    printProblem(e, this.web);
                    sQLException = e;
                }
            }
            this.web = Server.createWebServer(strArr, convertBytesToHex, !z10);
            this.web.setShutdownHandler(this);
            this.web.start();
            if (z7) {
                this.out.println(this.web.getStatus());
            }
            z12 = true;
        }
        if (z4 && z12) {
            show();
        }
        if (z5 && this.web != null) {
            openBrowser(this.web.getURL());
        }
        if (z) {
            try {
                this.tcp = Server.createTcpServer(strArr);
                this.tcp.start();
                if (z7) {
                    this.out.println(this.tcp.getStatus());
                }
                this.tcp.setShutdownHandler(this);
            } catch (SQLException e2) {
                printProblem(e2, this.tcp);
                if (sQLException == null) {
                    sQLException = e2;
                }
            }
        }
        if (z2) {
            try {
                this.pg = Server.createPgServer(strArr);
                this.pg.start();
                if (z7) {
                    this.out.println(this.pg.getStatus());
                }
            } catch (SQLException e3) {
                printProblem(e3, this.pg);
                if (sQLException == null) {
                    sQLException = e3;
                }
            }
        }
        if (sQLException != null) {
            shutdown();
            throw sQLException;
        }
    }

    void show() {
    }

    private void printProblem(Exception exc, Server server) {
        if (server == null) {
            exc.printStackTrace();
        } else {
            this.out.println(server.getStatus());
            this.out.println("Root cause: " + exc.getMessage());
        }
    }

    @Override // org.h2.server.ShutdownHandler
    public void shutdown() {
        if (this.web != null && this.web.isRunning(false)) {
            this.web.stop();
            this.web = null;
        }
        if (this.tcp != null && this.tcp.isRunning(false)) {
            this.tcp.stop();
            this.tcp = null;
        }
        if (this.pg != null && this.pg.isRunning(false)) {
            this.pg.stop();
            this.pg = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void openBrowser(String str) {
        try {
            Server.openBrowser(str);
        } catch (Exception e) {
            this.out.println(e.getMessage());
        }
    }
}
