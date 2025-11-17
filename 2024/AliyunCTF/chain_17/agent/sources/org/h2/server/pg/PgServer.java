package org.h2.server.pg;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.server.Service;
import org.h2.util.NetUtils;
import org.h2.util.Tool;
import org.h2.util.Utils;
import org.h2.util.Utils10;
import org.h2.util.Utils21;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/server/pg/PgServer.class */
public class PgServer implements Service {
    public static final int DEFAULT_PORT = 5435;
    public static final int PG_TYPE_VARCHAR = 1043;
    public static final int PG_TYPE_BOOL = 16;
    public static final int PG_TYPE_BYTEA = 17;
    public static final int PG_TYPE_BPCHAR = 1042;
    public static final int PG_TYPE_INT8 = 20;
    public static final int PG_TYPE_INT2 = 21;
    public static final int PG_TYPE_INT4 = 23;
    public static final int PG_TYPE_TEXT = 25;
    public static final int PG_TYPE_FLOAT4 = 700;
    public static final int PG_TYPE_FLOAT8 = 701;
    public static final int PG_TYPE_UNKNOWN = 705;
    public static final int PG_TYPE_INT2_ARRAY = 1005;
    public static final int PG_TYPE_INT4_ARRAY = 1007;
    public static final int PG_TYPE_VARCHAR_ARRAY = 1015;
    public static final int PG_TYPE_DATE = 1082;
    public static final int PG_TYPE_TIME = 1083;
    public static final int PG_TYPE_TIMETZ = 1266;
    public static final int PG_TYPE_TIMESTAMP = 1114;
    public static final int PG_TYPE_TIMESTAMPTZ = 1184;
    public static final int PG_TYPE_NUMERIC = 1700;
    private boolean portIsSet;
    private boolean stop;
    private boolean trace;
    private ServerSocket serverSocket;
    private String baseDir;
    private boolean allowOthers;
    private boolean isDaemon;
    private boolean virtualThreads;
    private String key;
    private String keyDatabase;
    private final HashSet<Integer> typeSet = new HashSet<>();
    private int port = DEFAULT_PORT;
    private final Set<PgServerThread> running = Collections.synchronizedSet(new HashSet());
    private final AtomicInteger pid = new AtomicInteger();
    private boolean ifExists = true;

    @Override // org.h2.server.Service
    public void init(String... strArr) {
        this.port = DEFAULT_PORT;
        int i = 0;
        while (strArr != null && i < strArr.length) {
            String str = strArr[i];
            if (Tool.isOption(str, "-trace")) {
                this.trace = true;
            } else if (Tool.isOption(str, "-pgPort")) {
                i++;
                this.port = Integer.decode(strArr[i]).intValue();
                this.portIsSet = true;
            } else if (Tool.isOption(str, "-baseDir")) {
                i++;
                this.baseDir = strArr[i];
            } else if (Tool.isOption(str, "-pgAllowOthers")) {
                this.allowOthers = true;
            } else if (Tool.isOption(str, "-pgDaemon")) {
                this.isDaemon = true;
            } else if (Tool.isOption(str, "-pgVirtualThreads")) {
                i++;
                this.virtualThreads = Utils.parseBoolean(strArr[i], this.virtualThreads, true);
            } else if (Tool.isOption(str, "-ifExists")) {
                this.ifExists = true;
            } else if (Tool.isOption(str, "-ifNotExists")) {
                this.ifExists = false;
            } else if (Tool.isOption(str, "-key")) {
                int i2 = i + 1;
                this.key = strArr[i2];
                i = i2 + 1;
                this.keyDatabase = strArr[i];
            }
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getTrace() {
        return this.trace;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void trace(String str) {
        if (this.trace) {
            System.out.println(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void remove(PgServerThread pgServerThread) {
        this.running.remove(pgServerThread);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void traceError(Exception exc) {
        if (this.trace) {
            exc.printStackTrace();
        }
    }

    @Override // org.h2.server.Service
    public String getURL() {
        return "pg://" + NetUtils.getLocalAddress() + ":" + this.port;
    }

    @Override // org.h2.server.Service
    public int getPort() {
        return this.port;
    }

    private boolean allow(Socket socket) {
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
    public void start() {
        this.stop = false;
        try {
            this.serverSocket = NetUtils.createServerSocket(this.port, false);
        } catch (DbException e) {
            if (!this.portIsSet) {
                this.serverSocket = NetUtils.createServerSocket(0, false);
            } else {
                throw e;
            }
        }
        this.port = this.serverSocket.getLocalPort();
    }

    @Override // org.h2.server.Service
    public void listen() {
        Thread thread;
        String name = Thread.currentThread().getName();
        while (!this.stop) {
            try {
                Socket accept = this.serverSocket.accept();
                if (!allow(accept)) {
                    trace("Connection not allowed");
                    accept.close();
                } else {
                    Utils10.setTcpQuickack(accept, true);
                    PgServerThread pgServerThread = new PgServerThread(accept, this);
                    this.running.add(pgServerThread);
                    int incrementAndGet = this.pid.incrementAndGet();
                    pgServerThread.setProcessId(incrementAndGet);
                    if (this.virtualThreads) {
                        thread = Utils21.newVirtualThread(pgServerThread);
                    } else {
                        thread = new Thread(pgServerThread);
                        thread.setDaemon(this.isDaemon);
                    }
                    thread.setName(name + " thread-" + incrementAndGet);
                    pgServerThread.setThread(thread);
                    thread.start();
                }
            } catch (Exception e) {
                if (!this.stop) {
                    e.printStackTrace();
                    return;
                }
                return;
            }
        }
    }

    @Override // org.h2.server.Service
    public void stop() {
        if (!this.stop) {
            this.stop = true;
            if (this.serverSocket != null) {
                try {
                    this.serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.serverSocket = null;
            }
        }
        Iterator it = new ArrayList(this.running).iterator();
        while (it.hasNext()) {
            PgServerThread pgServerThread = (PgServerThread) it.next();
            pgServerThread.close();
            try {
                Thread thread = pgServerThread.getThread();
                if (thread != null) {
                    thread.join(100L);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    @Override // org.h2.server.Service
    public boolean isRunning(boolean z) {
        if (this.serverSocket == null) {
            return false;
        }
        try {
            NetUtils.createLoopbackSocket(this.serverSocket.getLocalPort(), false).close();
            return true;
        } catch (Exception e) {
            if (z) {
                traceError(e);
                return false;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PgServerThread getThread(int i) {
        Iterator it = new ArrayList(this.running).iterator();
        while (it.hasNext()) {
            PgServerThread pgServerThread = (PgServerThread) it.next();
            if (pgServerThread.getProcessId() == i) {
                return pgServerThread;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getBaseDir() {
        return this.baseDir;
    }

    @Override // org.h2.server.Service
    public boolean getAllowOthers() {
        return this.allowOthers;
    }

    @Override // org.h2.server.Service
    public String getType() {
        return "PG";
    }

    @Override // org.h2.server.Service
    public String getName() {
        return "H2 PG Server";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getIfExists() {
        return this.ifExists;
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0001. Please report as an issue. */
    public static String formatType(int i) {
        int i2;
        switch (i) {
            case 0:
                return "-";
            case 16:
                i2 = 8;
                return Value.getTypeName(i2);
            case 17:
                i2 = 6;
                return Value.getTypeName(i2);
            case 18:
                return "char";
            case 19:
                return "name";
            case 20:
                i2 = 12;
                return Value.getTypeName(i2);
            case 21:
                i2 = 10;
                return Value.getTypeName(i2);
            case 22:
                return "int2vector";
            case 23:
                i2 = 11;
                return Value.getTypeName(i2);
            case 24:
                return "regproc";
            case 25:
                i2 = 3;
                return Value.getTypeName(i2);
            case PG_TYPE_FLOAT4 /* 700 */:
                i2 = 14;
                return Value.getTypeName(i2);
            case PG_TYPE_FLOAT8 /* 701 */:
                i2 = 15;
                return Value.getTypeName(i2);
            case PG_TYPE_INT2_ARRAY /* 1005 */:
                return "smallint[]";
            case 1007:
                return "integer[]";
            case PG_TYPE_VARCHAR_ARRAY /* 1015 */:
                return "character varying[]";
            case PG_TYPE_BPCHAR /* 1042 */:
                i2 = 1;
                return Value.getTypeName(i2);
            case PG_TYPE_VARCHAR /* 1043 */:
                i2 = 2;
                return Value.getTypeName(i2);
            case PG_TYPE_DATE /* 1082 */:
                i2 = 17;
                return Value.getTypeName(i2);
            case PG_TYPE_TIME /* 1083 */:
                i2 = 18;
                return Value.getTypeName(i2);
            case PG_TYPE_TIMESTAMP /* 1114 */:
                i2 = 20;
                return Value.getTypeName(i2);
            case PG_TYPE_TIMESTAMPTZ /* 1184 */:
                i2 = 21;
                return Value.getTypeName(i2);
            case PG_TYPE_TIMETZ /* 1266 */:
                i2 = 19;
                return Value.getTypeName(i2);
            case PG_TYPE_NUMERIC /* 1700 */:
                i2 = 13;
                return Value.getTypeName(i2);
            case 2205:
                return "regclass";
            default:
                return "???";
        }
    }

    public static int convertType(TypeInfo typeInfo) {
        switch (typeInfo.getValueType()) {
            case 0:
            case 3:
                return 25;
            case 1:
                return PG_TYPE_BPCHAR;
            case 2:
                return PG_TYPE_VARCHAR;
            case 4:
            case 7:
            case 9:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            default:
                return PG_TYPE_UNKNOWN;
            case 5:
            case 6:
                return 17;
            case 8:
                return 16;
            case 10:
                return 21;
            case 11:
                return 23;
            case 12:
                return 20;
            case 13:
            case 16:
                return PG_TYPE_NUMERIC;
            case 14:
                return PG_TYPE_FLOAT4;
            case 15:
                return PG_TYPE_FLOAT8;
            case 17:
                return PG_TYPE_DATE;
            case 18:
                return PG_TYPE_TIME;
            case 19:
                return PG_TYPE_TIMETZ;
            case 20:
                return PG_TYPE_TIMESTAMP;
            case 21:
                return PG_TYPE_TIMESTAMPTZ;
            case 40:
                switch (((TypeInfo) typeInfo.getExtTypeInfo()).getValueType()) {
                    case 2:
                        return PG_TYPE_VARCHAR_ARRAY;
                    case 10:
                        return PG_TYPE_INT2_ARRAY;
                    case 11:
                        return 1007;
                    default:
                        return PG_TYPE_VARCHAR_ARRAY;
                }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HashSet<Integer> getTypeSet() {
        return this.typeSet;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkType(int i) {
        if (!this.typeSet.contains(Integer.valueOf(i))) {
            trace("Unsupported type: " + i);
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
