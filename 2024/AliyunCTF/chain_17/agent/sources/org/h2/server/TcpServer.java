package org.h2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.h2.api.ErrorCode;
import org.h2.engine.Constants;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;
import org.h2.util.MathUtils;
import org.h2.util.NetUtils;
import org.h2.util.StringUtils;
import org.h2.util.Tool;
import org.h2.util.Utils;
import org.h2.util.Utils10;
import org.h2.util.Utils21;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/server/TcpServer.class */
public class TcpServer implements Service {
    private static final int SHUTDOWN_NORMAL = 0;
    private static final int SHUTDOWN_FORCE = 1;
    private static final String MANAGEMENT_DB_PREFIX = "management_db_";
    private static final ConcurrentHashMap<Integer, TcpServer> SERVERS = new ConcurrentHashMap<>();
    private int port;
    private boolean portIsSet;
    private boolean trace;
    private boolean ssl;
    private boolean stop;
    private ShutdownHandler shutdownHandler;
    private ServerSocket serverSocket;
    private String baseDir;
    private boolean allowOthers;
    private boolean isDaemon;
    private boolean virtualThreads;
    private JdbcConnection managementDb;
    private PreparedStatement managementDbAdd;
    private PreparedStatement managementDbRemove;
    private Thread listenerThread;
    private int nextThreadId;
    private String key;
    private String keyDatabase;
    private final Set<TcpServerThread> running = Collections.synchronizedSet(new HashSet());
    private boolean ifExists = true;
    private String managementPassword = "";

    public static String getManagementDbName(int i) {
        return "mem:management_db_" + i;
    }

    private void initManagementDb() throws SQLException {
        if (this.managementPassword.isEmpty()) {
            this.managementPassword = StringUtils.convertBytesToHex(MathUtils.secureRandomBytes(32));
        }
        JdbcConnection jdbcConnection = new JdbcConnection(Constants.START_URL + getManagementDbName(this.port), null, "", this.managementPassword, false);
        this.managementDb = jdbcConnection;
        Statement createStatement = jdbcConnection.createStatement();
        Throwable th = null;
        try {
            try {
                createStatement.execute("CREATE ALIAS IF NOT EXISTS STOP_SERVER FOR '" + TcpServer.class.getName() + ".stopServer'");
                createStatement.execute("CREATE TABLE IF NOT EXISTS SESSIONS(ID INT PRIMARY KEY, URL VARCHAR, `USER` VARCHAR, CONNECTED TIMESTAMP(9) WITH TIME ZONE)");
                this.managementDbAdd = jdbcConnection.prepareStatement("INSERT INTO SESSIONS VALUES(?, ?, ?, CURRENT_TIMESTAMP(9))");
                this.managementDbRemove = jdbcConnection.prepareStatement("DELETE FROM SESSIONS WHERE ID=?");
                if (createStatement != null) {
                    if (0 != 0) {
                        try {
                            createStatement.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        createStatement.close();
                    }
                }
                SERVERS.put(Integer.valueOf(this.port), this);
            } finally {
            }
        } catch (Throwable th3) {
            if (createStatement != null) {
                if (th != null) {
                    try {
                        createStatement.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    createStatement.close();
                }
            }
            throw th3;
        }
    }

    void shutdown() {
        if (this.shutdownHandler != null) {
            this.shutdownHandler.shutdown();
        }
    }

    public void setShutdownHandler(ShutdownHandler shutdownHandler) {
        this.shutdownHandler = shutdownHandler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void addConnection(int i, String str, String str2) {
        try {
            this.managementDbAdd.setInt(1, i);
            this.managementDbAdd.setString(2, str);
            this.managementDbAdd.setString(3, str2);
            this.managementDbAdd.execute();
        } catch (SQLException e) {
            DbException.traceThrowable(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void removeConnection(int i) {
        try {
            this.managementDbRemove.setInt(1, i);
            this.managementDbRemove.execute();
        } catch (SQLException e) {
            DbException.traceThrowable(e);
        }
    }

    private synchronized void stopManagementDb() {
        if (this.managementDb != null) {
            try {
                this.managementDb.close();
            } catch (SQLException e) {
                DbException.traceThrowable(e);
            }
            this.managementDb = null;
        }
    }

    @Override // org.h2.server.Service
    public void init(String... strArr) {
        this.port = Constants.DEFAULT_TCP_PORT;
        int i = 0;
        while (strArr != null && i < strArr.length) {
            String str = strArr[i];
            if (Tool.isOption(str, "-trace")) {
                this.trace = true;
            } else if (Tool.isOption(str, "-tcpSSL")) {
                this.ssl = true;
            } else if (Tool.isOption(str, "-tcpPort")) {
                i++;
                this.port = Integer.decode(strArr[i]).intValue();
                this.portIsSet = true;
            } else if (Tool.isOption(str, "-tcpPassword")) {
                i++;
                this.managementPassword = strArr[i];
            } else if (Tool.isOption(str, "-baseDir")) {
                i++;
                this.baseDir = strArr[i];
            } else if (Tool.isOption(str, "-key")) {
                int i2 = i + 1;
                this.key = strArr[i2];
                i = i2 + 1;
                this.keyDatabase = strArr[i];
            } else if (Tool.isOption(str, "-tcpAllowOthers")) {
                this.allowOthers = true;
            } else if (Tool.isOption(str, "-tcpDaemon")) {
                this.isDaemon = true;
            } else if (Tool.isOption(str, "-tcpVirtualThreads")) {
                i++;
                this.virtualThreads = Utils.parseBoolean(strArr[i], this.virtualThreads, true);
            } else if (Tool.isOption(str, "-ifExists")) {
                this.ifExists = true;
            } else if (Tool.isOption(str, "-ifNotExists")) {
                this.ifExists = false;
            }
            i++;
        }
    }

    @Override // org.h2.server.Service
    public String getURL() {
        return (this.ssl ? "ssl" : "tcp") + "://" + NetUtils.getLocalAddress() + ":" + this.port;
    }

    @Override // org.h2.server.Service
    public int getPort() {
        return this.port;
    }

    public boolean getSSL() {
        return this.ssl;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean allow(Socket socket) {
        if (this.allowOthers) {
            return true;
        }
        try {
            return NetUtils.isLocalAddress(socket);
        } catch (UnknownHostException e) {
            traceError(e);
            return false;
        }
    }

    @Override // org.h2.server.Service
    public synchronized void start() throws SQLException {
        this.stop = false;
        try {
            this.serverSocket = NetUtils.createServerSocket(this.port, this.ssl);
        } catch (DbException e) {
            if (!this.portIsSet) {
                this.serverSocket = NetUtils.createServerSocket(0, this.ssl);
            } else {
                throw e;
            }
        }
        this.port = this.serverSocket.getLocalPort();
        initManagementDb();
    }

    @Override // org.h2.server.Service
    public void listen() {
        Thread thread;
        this.listenerThread = Thread.currentThread();
        String name = this.listenerThread.getName();
        while (!this.stop) {
            try {
                Socket accept = this.serverSocket.accept();
                Utils10.setTcpQuickack(accept, true);
                int i = this.nextThreadId;
                this.nextThreadId = i + 1;
                TcpServerThread tcpServerThread = new TcpServerThread(accept, this, i);
                this.running.add(tcpServerThread);
                if (this.virtualThreads) {
                    thread = Utils21.newVirtualThread(tcpServerThread);
                } else {
                    thread = new Thread(tcpServerThread);
                    thread.setDaemon(this.isDaemon);
                }
                thread.setName(name + " thread-" + i);
                tcpServerThread.setThread(thread);
                thread.start();
            } catch (Exception e) {
                if (!this.stop) {
                    DbException.traceThrowable(e);
                }
            }
        }
        this.serverSocket = NetUtils.closeSilently(this.serverSocket);
        stopManagementDb();
    }

    @Override // org.h2.server.Service
    public synchronized boolean isRunning(boolean z) {
        if (this.serverSocket == null) {
            return false;
        }
        try {
            NetUtils.createLoopbackSocket(this.port, this.ssl).close();
            return true;
        } catch (Exception e) {
            if (z) {
                traceError(e);
                return false;
            }
            return false;
        }
    }

    @Override // org.h2.server.Service
    public void stop() {
        SERVERS.remove(Integer.valueOf(this.port));
        if (!this.stop) {
            stopManagementDb();
            this.stop = true;
            if (this.serverSocket != null) {
                try {
                    this.serverSocket.close();
                } catch (IOException e) {
                    DbException.traceThrowable(e);
                } catch (NullPointerException e2) {
                }
                this.serverSocket = null;
            }
            if (this.listenerThread != null) {
                try {
                    this.listenerThread.join(1000L);
                } catch (InterruptedException e3) {
                    DbException.traceThrowable(e3);
                }
            }
        }
        Iterator it = new ArrayList(this.running).iterator();
        while (it.hasNext()) {
            TcpServerThread tcpServerThread = (TcpServerThread) it.next();
            if (tcpServerThread != null) {
                tcpServerThread.close();
                try {
                    tcpServerThread.getThread().join(100L);
                } catch (Exception e4) {
                    DbException.traceThrowable(e4);
                }
            }
        }
    }

    public static void stopServer(int i, String str, int i2) {
        if (i == 0) {
            for (Integer num : (Integer[]) SERVERS.keySet().toArray(new Integer[0])) {
                int intValue = num.intValue();
                if (intValue != 0) {
                    stopServer(intValue, str, i2);
                }
            }
            return;
        }
        TcpServer tcpServer = SERVERS.get(Integer.valueOf(i));
        if (tcpServer == null || !tcpServer.managementPassword.equals(str)) {
            return;
        }
        if (i2 == 0) {
            tcpServer.stopManagementDb();
            tcpServer.stop = true;
            try {
                NetUtils.createLoopbackSocket(i, false).close();
            } catch (Exception e) {
            }
        } else if (i2 == 1) {
            tcpServer.stop();
        }
        tcpServer.shutdown();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void remove(TcpServerThread tcpServerThread) {
        this.running.remove(tcpServerThread);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getBaseDir() {
        return this.baseDir;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void trace(String str) {
        if (this.trace) {
            System.out.println(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void traceError(Throwable th) {
        if (this.trace) {
            th.printStackTrace();
        }
    }

    @Override // org.h2.server.Service
    public boolean getAllowOthers() {
        return this.allowOthers;
    }

    @Override // org.h2.server.Service
    public String getType() {
        return "TCP";
    }

    @Override // org.h2.server.Service
    public String getName() {
        return "H2 TCP Server";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getIfExists() {
        return this.ifExists;
    }

    /* JADX WARN: Code restructure failed: missing block: B:34:0x0135, code lost:            return;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static synchronized void shutdown(java.lang.String r8, java.lang.String r9, boolean r10, boolean r11) throws java.sql.SQLException {
        /*
            Method dump skipped, instructions count: 310
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.server.TcpServer.shutdown(java.lang.String, java.lang.String, boolean, boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cancelStatement(String str, int i) {
        Iterator it = new ArrayList(this.running).iterator();
        while (it.hasNext()) {
            TcpServerThread tcpServerThread = (TcpServerThread) it.next();
            if (tcpServerThread != null) {
                tcpServerThread.cancelStatement(str, i);
            }
        }
    }

    public String checkKeyAndGetDatabaseName(String str) {
        if (this.key == null) {
            return str;
        }
        if (this.key.equals(str)) {
            return this.keyDatabase;
        }
        throw DbException.get(ErrorCode.WRONG_USER_OR_PASSWORD);
    }

    @Override // org.h2.server.Service
    public boolean isDaemon() {
        return this.isDaemon;
    }
}
