package org.h2.engine;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import org.h2.api.DatabaseEventListener;
import org.h2.api.ErrorCode;
import org.h2.api.JavaObjectSerializer;
import org.h2.command.CommandInterface;
import org.h2.command.CommandRemote;
import org.h2.engine.Mode;
import org.h2.engine.Session;
import org.h2.expression.ParameterInterface;
import org.h2.jdbc.JdbcException;
import org.h2.jdbc.meta.DatabaseMeta;
import org.h2.jdbc.meta.DatabaseMetaLegacy;
import org.h2.jdbc.meta.DatabaseMetaRemote;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.message.TraceSystem;
import org.h2.result.ResultInterface;
import org.h2.store.DataHandler;
import org.h2.store.FileStore;
import org.h2.store.LobStorageFrontend;
import org.h2.store.fs.FileUtils;
import org.h2.util.DateTimeUtils;
import org.h2.util.JdbcUtils;
import org.h2.util.MathUtils;
import org.h2.util.NetUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.SmallLRUCache;
import org.h2.util.StringUtils;
import org.h2.util.TempFileDeleter;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;
import org.h2.value.CompareMode;
import org.h2.value.Transfer;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueLob;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/SessionRemote.class */
public final class SessionRemote extends Session implements DataHandler {
    public static final int SESSION_PREPARE = 0;
    public static final int SESSION_CLOSE = 1;
    public static final int COMMAND_EXECUTE_QUERY = 2;
    public static final int COMMAND_EXECUTE_UPDATE = 3;
    public static final int COMMAND_CLOSE = 4;
    public static final int RESULT_FETCH_ROWS = 5;
    public static final int RESULT_RESET = 6;
    public static final int RESULT_CLOSE = 7;
    public static final int COMMAND_COMMIT = 8;
    public static final int CHANGE_ID = 9;
    public static final int COMMAND_GET_META_DATA = 10;
    public static final int SESSION_SET_ID = 12;
    public static final int SESSION_CANCEL_STATEMENT = 13;
    public static final int SESSION_CHECK_KEY = 14;
    public static final int SESSION_SET_AUTOCOMMIT = 15;
    public static final int SESSION_HAS_PENDING_TRANSACTION = 16;
    public static final int LOB_READ = 17;
    public static final int SESSION_PREPARE_READ_PARAMS2 = 18;
    public static final int GET_JDBC_META = 19;
    public static final int STATUS_ERROR = 0;
    public static final int STATUS_OK = 1;
    public static final int STATUS_CLOSED = 2;
    public static final int STATUS_OK_STATE_CHANGED = 3;
    private TraceSystem traceSystem;
    private Trace trace;
    private int nextId;
    private ConnectionInfo connectionInfo;
    private String databaseName;
    private String cipher;
    private byte[] fileEncryptionKey;
    private String sessionId;
    private int clientVersion;
    private boolean autoReconnect;
    private int lastReconnect;
    private Session embedded;
    private DatabaseEventListener eventListener;
    private LobStorageFrontend lobStorage;
    private boolean cluster;
    private TempFileDeleter tempFileDeleter;
    private JavaObjectSerializer javaObjectSerializer;
    private final boolean oldInformationSchema;
    private String currentSchemaName;
    private volatile Session.DynamicSettings dynamicSettings;
    private ArrayList<Transfer> transferList = Utils.newSmallArrayList();
    private boolean autoCommit = true;
    private final Object lobSyncObject = new Object();
    private final CompareMode compareMode = CompareMode.getInstance(null, 0);

    public SessionRemote(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
        this.oldInformationSchema = connectionInfo.getProperty("OLD_INFORMATION_SCHEMA", false);
    }

    @Override // org.h2.engine.Session
    public ArrayList<String> getClusterServers() {
        ArrayList<String> arrayList = new ArrayList<>();
        Iterator<Transfer> it = this.transferList.iterator();
        while (it.hasNext()) {
            Transfer next = it.next();
            arrayList.add(next.getSocket().getInetAddress().getHostAddress() + ":" + next.getSocket().getPort());
        }
        return arrayList;
    }

    private Transfer initTransfer(ConnectionInfo connectionInfo, String str, String str2) throws IOException {
        Transfer transfer = new Transfer(this, NetUtils.createSocket(str2, Constants.DEFAULT_TCP_PORT, connectionInfo.isSSL(), connectionInfo.getProperty("NETWORK_TIMEOUT", 0)));
        transfer.setSSL(connectionInfo.isSSL());
        transfer.init();
        transfer.writeInt(17);
        transfer.writeInt(20);
        transfer.writeString(str);
        transfer.writeString(connectionInfo.getOriginalURL());
        transfer.writeString(connectionInfo.getUserName());
        transfer.writeBytes(connectionInfo.getUserPasswordHash());
        transfer.writeBytes(connectionInfo.getFilePasswordHash());
        String[] keys = connectionInfo.getKeys();
        transfer.writeInt(keys.length);
        for (String str3 : keys) {
            transfer.writeString(str3).writeString(connectionInfo.getProperty(str3));
        }
        try {
            done(transfer);
            this.clientVersion = transfer.readInt();
            transfer.setVersion(this.clientVersion);
            if (connectionInfo.getFileEncryptionKey() != null) {
                transfer.writeBytes(connectionInfo.getFileEncryptionKey());
            }
            transfer.writeInt(12);
            transfer.writeString(this.sessionId);
            if (this.clientVersion >= 20) {
                TimeZoneProvider timeZone = connectionInfo.getTimeZone();
                if (timeZone == null) {
                    timeZone = DateTimeUtils.getTimeZone();
                }
                transfer.writeString(timeZone.getId());
            }
            done(transfer);
            this.autoCommit = transfer.readBoolean();
            return transfer;
        } catch (DbException e) {
            transfer.close();
            throw e;
        }
    }

    @Override // org.h2.engine.Session
    public boolean hasPendingTransaction() {
        int i = 0;
        for (int i2 = 0; i2 < this.transferList.size(); i2 = (i2 - 1) + 1) {
            Transfer transfer = this.transferList.get(i2);
            try {
                traceOperation("SESSION_HAS_PENDING_TRANSACTION", 0);
                transfer.writeInt(16);
                done(transfer);
                return transfer.readInt() != 0;
            } catch (IOException e) {
                i++;
                removeServer(e, i2, i);
            }
        }
        return true;
    }

    @Override // org.h2.engine.Session
    public void cancel() {
    }

    public void cancelStatement(int i) {
        Iterator<Transfer> it = this.transferList.iterator();
        while (it.hasNext()) {
            try {
                Transfer openNewConnection = it.next().openNewConnection();
                openNewConnection.init();
                openNewConnection.writeInt(this.clientVersion);
                openNewConnection.writeInt(this.clientVersion);
                openNewConnection.writeString(null);
                openNewConnection.writeString(null);
                openNewConnection.writeString(this.sessionId);
                openNewConnection.writeInt(13);
                openNewConnection.writeInt(i);
                openNewConnection.close();
            } catch (IOException e) {
                this.trace.debug(e, "could not cancel statement");
            }
        }
    }

    private void checkClusterDisableAutoCommit(String str) {
        if (this.autoCommit && this.transferList.size() > 1) {
            setAutoCommitSend(false);
            prepareCommand("SET CLUSTER " + str, Integer.MAX_VALUE).executeUpdate(null);
            this.autoCommit = true;
            this.cluster = true;
        }
    }

    public int getClientVersion() {
        return this.clientVersion;
    }

    @Override // org.h2.engine.Session
    public boolean getAutoCommit() {
        return this.autoCommit;
    }

    @Override // org.h2.engine.Session
    public void setAutoCommit(boolean z) {
        if (!this.cluster) {
            setAutoCommitSend(z);
        }
        this.autoCommit = z;
    }

    public void setAutoCommitFromServer(boolean z) {
        if (this.cluster) {
            if (z) {
                setAutoCommitSend(false);
                this.autoCommit = true;
                return;
            }
            return;
        }
        this.autoCommit = z;
    }

    private void setAutoCommitSend(boolean z) {
        lock();
        int i = 0;
        int i2 = 0;
        while (i < this.transferList.size()) {
            try {
                Transfer transfer = this.transferList.get(i);
                try {
                    traceOperation("SESSION_SET_AUTOCOMMIT", z ? 1 : 0);
                    transfer.writeInt(15).writeBoolean(z);
                    done(transfer);
                } catch (IOException e) {
                    int i3 = i;
                    i--;
                    i2++;
                    removeServer(e, i3, i2);
                }
                i++;
            } finally {
                unlock();
            }
        }
    }

    public void autoCommitIfCluster() {
        if (this.autoCommit && this.cluster) {
            int i = 0;
            int i2 = 0;
            while (i < this.transferList.size()) {
                Transfer transfer = this.transferList.get(i);
                try {
                    traceOperation("COMMAND_COMMIT", 0);
                    transfer.writeInt(8);
                    done(transfer);
                } catch (IOException e) {
                    int i3 = i;
                    i--;
                    i2++;
                    removeServer(e, i3, i2);
                }
                i++;
            }
        }
    }

    private String getFilePrefix(String str) {
        StringBuilder sb = new StringBuilder(str);
        sb.append('/');
        for (int i = 0; i < this.databaseName.length(); i++) {
            char charAt = this.databaseName.charAt(i);
            if (Character.isLetterOrDigit(charAt)) {
                sb.append(charAt);
            } else {
                sb.append('_');
            }
        }
        return sb.toString();
    }

    public Session connectEmbeddedOrServer(boolean z) {
        String sql;
        ConnectionInfo connectionInfo = this.connectionInfo;
        if (connectionInfo.isRemote()) {
            connectServer(connectionInfo);
            return this;
        }
        boolean property = connectionInfo.getProperty("AUTO_SERVER", false);
        ConnectionInfo connectionInfo2 = null;
        if (property) {
            try {
                connectionInfo2 = connectionInfo.m1459clone();
                this.connectionInfo = connectionInfo.m1459clone();
            } catch (Exception e) {
                DbException convert = DbException.convert(e);
                if (convert.getErrorCode() == 90020 && property && (sql = ((JdbcException) convert.getSQLException()).getSQL()) != null) {
                    connectionInfo2.setServerKey(sql);
                    connectionInfo2.removeProperty("OPEN_NEW", (String) null);
                    connectServer(connectionInfo2);
                    return this;
                }
                throw convert;
            }
        }
        if (z) {
            connectionInfo.setProperty("OPEN_NEW", "true");
        }
        return Engine.createSession(connectionInfo);
    }

    private void connectServer(ConnectionInfo connectionInfo) {
        String property;
        String name = connectionInfo.getName();
        if (name.startsWith("//")) {
            name = name.substring("//".length());
        }
        int indexOf = name.indexOf(47);
        if (indexOf < 0) {
            throw connectionInfo.getFormatException();
        }
        this.databaseName = name.substring(indexOf + 1);
        String substring = name.substring(0, indexOf);
        this.traceSystem = new TraceSystem(null);
        String property2 = connectionInfo.getProperty(9, (String) null);
        if (property2 != null) {
            int parseInt = Integer.parseInt(property2);
            String filePrefix = getFilePrefix(SysProperties.CLIENT_TRACE_DIRECTORY);
            try {
                this.traceSystem.setLevelFile(parseInt);
                if (parseInt > 0 && parseInt < 4) {
                    this.traceSystem.setFileName(FileUtils.createTempFile(filePrefix, Constants.SUFFIX_TRACE_FILE, false));
                }
            } catch (IOException e) {
                throw DbException.convertIOException(e, filePrefix);
            }
        }
        String property3 = connectionInfo.getProperty(8, (String) null);
        if (property3 != null) {
            this.traceSystem.setLevelSystemOut(Integer.parseInt(property3));
        }
        this.trace = this.traceSystem.getTrace(6);
        String str = null;
        if (substring.indexOf(44) >= 0) {
            str = StringUtils.quoteStringSQL(substring);
            connectionInfo.setProperty("CLUSTER", Constants.CLUSTERING_ENABLED);
        }
        this.autoReconnect = connectionInfo.getProperty("AUTO_RECONNECT", false);
        boolean property4 = connectionInfo.getProperty("AUTO_SERVER", false);
        if (property4 && str != null) {
            throw DbException.getUnsupportedException("autoServer && serverList != null");
        }
        this.autoReconnect |= property4;
        if (this.autoReconnect && (property = connectionInfo.getProperty("DATABASE_EVENT_LISTENER")) != null) {
            try {
                this.eventListener = (DatabaseEventListener) JdbcUtils.loadUserClass(StringUtils.trim(property, true, true, '\'')).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Throwable th) {
                throw DbException.convert(th);
            }
        }
        this.cipher = connectionInfo.getProperty("CIPHER");
        if (this.cipher != null) {
            this.fileEncryptionKey = MathUtils.secureRandomBytes(32);
        }
        String[] arraySplit = StringUtils.arraySplit(substring, ',', true);
        int length = arraySplit.length;
        this.transferList.clear();
        this.sessionId = StringUtils.convertBytesToHex(MathUtils.secureRandomBytes(32));
        boolean z = false;
        try {
            for (String str2 : arraySplit) {
                try {
                    this.transferList.add(initTransfer(connectionInfo, this.databaseName, str2));
                } catch (IOException e2) {
                    if (length == 1) {
                        throw DbException.get(ErrorCode.CONNECTION_BROKEN_1, e2, e2 + ": " + str2);
                    }
                    z = true;
                }
            }
            checkClosed();
            if (z) {
                switchOffCluster();
            }
            checkClusterDisableAutoCommit(str);
            getDynamicSettings();
        } catch (DbException e3) {
            this.traceSystem.close();
            throw e3;
        }
    }

    private void switchOffCluster() {
        prepareCommand("SET CLUSTER ''", Integer.MAX_VALUE).executeUpdate(null);
    }

    public void removeServer(IOException iOException, int i, int i2) {
        this.trace.debug(iOException, "removing server because of exception");
        this.transferList.remove(i);
        if (this.transferList.isEmpty() && autoReconnect(i2)) {
            return;
        }
        checkClosed();
        switchOffCluster();
    }

    @Override // org.h2.engine.Session
    public CommandInterface prepareCommand(String str, int i) {
        lock();
        try {
            checkClosed();
            CommandRemote commandRemote = new CommandRemote(this, this.transferList, str, i);
            unlock();
            return commandRemote;
        } catch (Throwable th) {
            unlock();
            throw th;
        }
    }

    private boolean autoReconnect(int i) {
        if (!isClosed() || !this.autoReconnect) {
            return false;
        }
        if ((!this.cluster && !this.autoCommit) || i > SysProperties.MAX_RECONNECT) {
            return false;
        }
        this.lastReconnect++;
        while (true) {
            try {
                this.embedded = connectEmbeddedOrServer(false);
                if (this.embedded == this) {
                    this.embedded = null;
                } else {
                    connectEmbeddedOrServer(true);
                }
                recreateSessionState();
                if (this.eventListener != null) {
                    this.eventListener.setProgress(4, this.databaseName, i, SysProperties.MAX_RECONNECT);
                    return true;
                }
                return true;
            } catch (DbException e) {
                if (e.getErrorCode() != 90135) {
                    throw e;
                }
                try {
                    Thread.sleep(500L);
                } catch (Exception e2) {
                }
            }
        }
    }

    public void checkClosed() {
        if (isClosed()) {
            throw DbException.get(ErrorCode.CONNECTION_BROKEN_1, "session closed");
        }
    }

    @Override // org.h2.engine.Session, java.lang.AutoCloseable
    public void close() {
        RuntimeException runtimeException = null;
        if (this.transferList != null) {
            lock();
            try {
                Iterator<Transfer> it = this.transferList.iterator();
                while (it.hasNext()) {
                    Transfer next = it.next();
                    try {
                        traceOperation("SESSION_CLOSE", 0);
                        next.writeInt(1);
                        done(next);
                        next.close();
                    } catch (RuntimeException e) {
                        this.trace.error(e, org.apache.coyote.http11.Constants.CLOSE);
                        runtimeException = e;
                    } catch (Exception e2) {
                        this.trace.error(e2, org.apache.coyote.http11.Constants.CLOSE);
                    }
                }
                this.transferList = null;
            } finally {
                unlock();
            }
        }
        this.traceSystem.close();
        if (this.embedded != null) {
            this.embedded.close();
            this.embedded = null;
        }
        if (runtimeException != null) {
            throw runtimeException;
        }
    }

    @Override // org.h2.engine.Session
    public Trace getTrace() {
        return this.traceSystem.getTrace(6);
    }

    public int getNextId() {
        int i = this.nextId;
        this.nextId = i + 1;
        return i;
    }

    public int getCurrentId() {
        return this.nextId;
    }

    public void done(Transfer transfer) throws IOException {
        transfer.flush();
        int readInt = transfer.readInt();
        switch (readInt) {
            case 0:
                throw readException(transfer);
            case 1:
                return;
            case 2:
                this.transferList = null;
                return;
            case 3:
                this.sessionStateChanged = true;
                this.currentSchemaName = null;
                this.dynamicSettings = null;
                return;
            default:
                throw DbException.get(ErrorCode.CONNECTION_BROKEN_1, "unexpected status " + readInt);
        }
    }

    public static DbException readException(Transfer transfer) throws IOException {
        String readString = transfer.readString();
        String readString2 = transfer.readString();
        String readString3 = transfer.readString();
        int readInt = transfer.readInt();
        SQLException jdbcSQLException = DbException.getJdbcSQLException(readString2, readString3, readString, readInt, null, transfer.readString());
        if (readInt == 90067) {
            throw new IOException(jdbcSQLException.toString(), jdbcSQLException);
        }
        return DbException.convert(jdbcSQLException);
    }

    public boolean isClustered() {
        return this.cluster;
    }

    @Override // org.h2.engine.Session
    public boolean isClosed() {
        return this.transferList == null || this.transferList.isEmpty();
    }

    public void traceOperation(String str, int i) {
        if (this.trace.isDebugEnabled()) {
            this.trace.debug("{0} {1}", str, Integer.valueOf(i));
        }
    }

    @Override // org.h2.store.DataHandler
    public void checkPowerOff() {
    }

    @Override // org.h2.store.DataHandler
    public void checkWritingAllowed() {
    }

    @Override // org.h2.store.DataHandler
    public String getDatabasePath() {
        return "";
    }

    @Override // org.h2.store.DataHandler
    public int getMaxLengthInplaceLob() {
        return SysProperties.LOB_CLIENT_MAX_SIZE_MEMORY;
    }

    @Override // org.h2.store.DataHandler
    public FileStore openFile(String str, String str2, boolean z) {
        FileStore open;
        if (z && !FileUtils.exists(str)) {
            throw DbException.get(ErrorCode.FILE_NOT_FOUND_1, str);
        }
        if (this.cipher == null) {
            open = FileStore.open(this, str, str2);
        } else {
            open = FileStore.open(this, str, str2, this.cipher, this.fileEncryptionKey, 0);
        }
        open.setCheckedWriting(false);
        try {
            open.init();
            return open;
        } catch (DbException e) {
            open.closeSilently();
            throw e;
        }
    }

    @Override // org.h2.engine.Session
    public DataHandler getDataHandler() {
        return this;
    }

    @Override // org.h2.store.DataHandler
    public Object getLobSyncObject() {
        return this.lobSyncObject;
    }

    @Override // org.h2.store.DataHandler
    public SmallLRUCache<String, String[]> getLobFileListCache() {
        return null;
    }

    public int getLastReconnect() {
        return this.lastReconnect;
    }

    @Override // org.h2.store.DataHandler
    public TempFileDeleter getTempFileDeleter() {
        if (this.tempFileDeleter == null) {
            this.tempFileDeleter = TempFileDeleter.getInstance();
        }
        return this.tempFileDeleter;
    }

    @Override // org.h2.store.DataHandler
    public LobStorageFrontend getLobStorage() {
        if (this.lobStorage == null) {
            this.lobStorage = new LobStorageFrontend(this);
        }
        return this.lobStorage;
    }

    @Override // org.h2.store.DataHandler
    public int readLob(long j, byte[] bArr, long j2, byte[] bArr2, int i, int i2) {
        lock();
        try {
            checkClosed();
            int i3 = 0;
            for (int i4 = 0; i4 < this.transferList.size(); i4 = (i4 - 1) + 1) {
                Transfer transfer = this.transferList.get(i4);
                try {
                    traceOperation("LOB_READ", (int) j);
                    transfer.writeInt(17);
                    transfer.writeLong(j);
                    transfer.writeBytes(bArr);
                    transfer.writeLong(j2);
                    transfer.writeInt(i2);
                    done(transfer);
                    i2 = transfer.readInt();
                    if (i2 <= 0) {
                        return i2;
                    }
                    transfer.readBytes(bArr2, i, i2);
                    unlock();
                    return i2;
                } catch (IOException e) {
                    i3++;
                    removeServer(e, i4, i3);
                }
            }
            unlock();
            return 1;
        } finally {
            unlock();
        }
    }

    @Override // org.h2.engine.CastDataProvider
    public JavaObjectSerializer getJavaObjectSerializer() {
        if (this.dynamicSettings == null) {
            getDynamicSettings();
        }
        return this.javaObjectSerializer;
    }

    @Override // org.h2.engine.Session
    public ValueLob addTemporaryLob(ValueLob valueLob) {
        return valueLob;
    }

    @Override // org.h2.store.DataHandler
    public CompareMode getCompareMode() {
        return this.compareMode;
    }

    @Override // org.h2.engine.Session
    public boolean isRemote() {
        return true;
    }

    /* JADX WARN: Failed to calculate best type for var: r7v0 ??
    java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r8v0 ??
    java.lang.NullPointerException
     */
    /* JADX WARN: Multi-variable type inference failed. Error: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.RegisterArg.getSVar()" because the return value of "jadx.core.dex.nodes.InsnNode.getResult()" is null
    	at jadx.core.dex.visitors.typeinference.AbstractTypeConstraint.collectRelatedVars(AbstractTypeConstraint.java:31)
    	at jadx.core.dex.visitors.typeinference.AbstractTypeConstraint.<init>(AbstractTypeConstraint.java:19)
    	at jadx.core.dex.visitors.typeinference.TypeSearch$1.<init>(TypeSearch.java:376)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.makeMoveConstraint(TypeSearch.java:376)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.makeConstraint(TypeSearch.java:361)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.collectConstraints(TypeSearch.java:341)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:60)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.runMultiVariableSearch(FixTypesVisitor.java:116)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Not initialized variable reg: 7, insn: 0x00cc: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r7 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:57:0x00cc */
    /* JADX WARN: Not initialized variable reg: 8, insn: 0x00d0: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r8 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:59:0x00d0 */
    /* JADX WARN: Type inference failed for: r7v0, types: [org.h2.command.CommandInterface] */
    /* JADX WARN: Type inference failed for: r8v0, types: [java.lang.Throwable] */
    @Override // org.h2.engine.Session
    public String getCurrentSchemaName() {
        String str = this.currentSchemaName;
        if (str == null) {
            lock();
            try {
                try {
                    CommandInterface prepareCommand = prepareCommand("CALL SCHEMA()", 1);
                    Throwable th = null;
                    ResultInterface executeQuery = prepareCommand.executeQuery(1L, false);
                    Throwable th2 = null;
                    try {
                        executeQuery.next();
                        String string = executeQuery.currentRow()[0].getString();
                        str = string;
                        this.currentSchemaName = string;
                        if (executeQuery != null) {
                            if (0 != 0) {
                                try {
                                    executeQuery.close();
                                } catch (Throwable th3) {
                                    th2.addSuppressed(th3);
                                }
                            } else {
                                executeQuery.close();
                            }
                        }
                        if (prepareCommand != null) {
                            if (0 != 0) {
                                try {
                                    prepareCommand.close();
                                } catch (Throwable th4) {
                                    th.addSuppressed(th4);
                                }
                            } else {
                                prepareCommand.close();
                            }
                        }
                    } catch (Throwable th5) {
                        if (executeQuery != null) {
                            if (0 != 0) {
                                try {
                                    executeQuery.close();
                                } catch (Throwable th6) {
                                    th2.addSuppressed(th6);
                                }
                            } else {
                                executeQuery.close();
                            }
                        }
                        throw th5;
                    }
                } finally {
                }
            } finally {
                unlock();
            }
        }
        return str;
    }

    @Override // org.h2.engine.Session
    public void setCurrentSchemaName(String str) {
        lock();
        try {
            this.currentSchemaName = null;
            CommandInterface prepareCommand = prepareCommand(StringUtils.quoteIdentifier(new StringBuilder("SET SCHEMA "), str).toString(), 0);
            Throwable th = null;
            try {
                try {
                    prepareCommand.executeUpdate(null);
                    this.currentSchemaName = str;
                    if (prepareCommand != null) {
                        if (0 != 0) {
                            try {
                                prepareCommand.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            prepareCommand.close();
                        }
                    }
                } finally {
                }
            } finally {
            }
        } finally {
            unlock();
        }
    }

    @Override // org.h2.engine.Session
    public void setNetworkConnectionInfo(NetworkConnectionInfo networkConnectionInfo) {
    }

    @Override // org.h2.engine.Session
    public IsolationLevel getIsolationLevel() {
        ResultInterface executeQuery;
        if (this.clientVersion >= 19) {
            CommandInterface prepareCommand = prepareCommand(!isOldInformationSchema() ? "SELECT ISOLATION_LEVEL FROM INFORMATION_SCHEMA.SESSIONS WHERE SESSION_ID = SESSION_ID()" : "SELECT ISOLATION_LEVEL FROM INFORMATION_SCHEMA.SESSIONS WHERE ID = SESSION_ID()", 1);
            Throwable th = null;
            try {
                executeQuery = prepareCommand.executeQuery(1L, false);
                Throwable th2 = null;
                try {
                    try {
                        executeQuery.next();
                        IsolationLevel fromSql = IsolationLevel.fromSql(executeQuery.currentRow()[0].getString());
                        if (executeQuery != null) {
                            if (0 != 0) {
                                try {
                                    executeQuery.close();
                                } catch (Throwable th3) {
                                    th2.addSuppressed(th3);
                                }
                            } else {
                                executeQuery.close();
                            }
                        }
                        return fromSql;
                    } finally {
                    }
                } finally {
                }
            } finally {
                if (prepareCommand != null) {
                    if (0 != 0) {
                        try {
                            prepareCommand.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        prepareCommand.close();
                    }
                }
            }
        }
        CommandInterface prepareCommand2 = prepareCommand("CALL LOCK_MODE()", 1);
        Throwable th5 = null;
        try {
            executeQuery = prepareCommand2.executeQuery(1L, false);
            Throwable th6 = null;
            try {
                try {
                    executeQuery.next();
                    IsolationLevel fromLockMode = IsolationLevel.fromLockMode(executeQuery.currentRow()[0].getInt());
                    if (executeQuery != null) {
                        if (0 != 0) {
                            try {
                                executeQuery.close();
                            } catch (Throwable th7) {
                                th6.addSuppressed(th7);
                            }
                        } else {
                            executeQuery.close();
                        }
                    }
                    return fromLockMode;
                } finally {
                }
            } finally {
            }
        } finally {
            if (prepareCommand2 != null) {
                if (0 != 0) {
                    try {
                        prepareCommand2.close();
                    } catch (Throwable th8) {
                        th5.addSuppressed(th8);
                    }
                } else {
                    prepareCommand2.close();
                }
            }
        }
    }

    @Override // org.h2.engine.Session
    public void setIsolationLevel(IsolationLevel isolationLevel) {
        CommandInterface prepareCommand;
        if (this.clientVersion >= 19) {
            prepareCommand = prepareCommand("SET SESSION CHARACTERISTICS AS TRANSACTION ISOLATION LEVEL " + isolationLevel.getSQL(), 0);
            Throwable th = null;
            try {
                try {
                    prepareCommand.executeUpdate(null);
                    if (prepareCommand != null) {
                        if (0 != 0) {
                            try {
                                prepareCommand.close();
                                return;
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                                return;
                            }
                        }
                        prepareCommand.close();
                        return;
                    }
                    return;
                } finally {
                }
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        }
        prepareCommand = prepareCommand("SET LOCK_MODE ?", 0);
        Throwable th4 = null;
        try {
            try {
                prepareCommand.getParameters().get(0).setValue(ValueInteger.get(isolationLevel.getLockMode()), false);
                prepareCommand.executeUpdate(null);
                if (prepareCommand != null) {
                    if (0 != 0) {
                        try {
                            prepareCommand.close();
                            return;
                        } catch (Throwable th5) {
                            th4.addSuppressed(th5);
                            return;
                        }
                    }
                    prepareCommand.close();
                }
            } finally {
            }
        } catch (Throwable th6) {
            th4 = th6;
            throw th6;
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:11:0x009b. Please report as an issue. */
    @Override // org.h2.engine.Session
    public Session.StaticSettings getStaticSettings() {
        Session.StaticSettings staticSettings = this.staticSettings;
        if (staticSettings == null) {
            boolean z = true;
            boolean z2 = false;
            boolean z3 = false;
            CommandInterface settingsCommand = getSettingsCommand(" IN (?, ?, ?)");
            Throwable th = null;
            try {
                ArrayList<? extends ParameterInterface> parameters = settingsCommand.getParameters();
                parameters.get(0).setValue(ValueVarchar.get("DATABASE_TO_UPPER"), false);
                parameters.get(1).setValue(ValueVarchar.get("DATABASE_TO_LOWER"), false);
                parameters.get(2).setValue(ValueVarchar.get("CASE_INSENSITIVE_IDENTIFIERS"), false);
                ResultInterface executeQuery = settingsCommand.executeQuery(0L, false);
                Throwable th2 = null;
                while (executeQuery.next()) {
                    try {
                        try {
                            Value[] currentRow = executeQuery.currentRow();
                            String string = currentRow[1].getString();
                            String string2 = currentRow[0].getString();
                            boolean z4 = -1;
                            switch (string2.hashCode()) {
                                case -601444243:
                                    if (string2.equals("CASE_INSENSITIVE_IDENTIFIERS")) {
                                        z4 = 2;
                                        break;
                                    }
                                    break;
                                case 756040289:
                                    if (string2.equals("DATABASE_TO_LOWER")) {
                                        z4 = true;
                                        break;
                                    }
                                    break;
                                case 764375042:
                                    if (string2.equals("DATABASE_TO_UPPER")) {
                                        z4 = false;
                                        break;
                                    }
                                    break;
                            }
                            switch (z4) {
                                case false:
                                    z = Boolean.valueOf(string).booleanValue();
                                    break;
                                case true:
                                    z2 = Boolean.valueOf(string).booleanValue();
                                    break;
                                case true:
                                    z3 = Boolean.valueOf(string).booleanValue();
                                    break;
                            }
                        } catch (Throwable th3) {
                            if (executeQuery != null) {
                                if (th2 != null) {
                                    try {
                                        executeQuery.close();
                                    } catch (Throwable th4) {
                                        th2.addSuppressed(th4);
                                    }
                                } else {
                                    executeQuery.close();
                                }
                            }
                            throw th3;
                        }
                    } finally {
                    }
                }
                if (executeQuery != null) {
                    if (0 != 0) {
                        try {
                            executeQuery.close();
                        } catch (Throwable th5) {
                            th2.addSuppressed(th5);
                        }
                    } else {
                        executeQuery.close();
                    }
                }
                if (this.clientVersion < 18) {
                    z3 = !z;
                }
                Session.StaticSettings staticSettings2 = new Session.StaticSettings(z, z2, z3);
                staticSettings = staticSettings2;
                this.staticSettings = staticSettings2;
            } finally {
                if (settingsCommand != null) {
                    if (0 != 0) {
                        try {
                            settingsCommand.close();
                        } catch (Throwable th6) {
                            th.addSuppressed(th6);
                        }
                    } else {
                        settingsCommand.close();
                    }
                }
            }
        }
        return staticSettings;
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:11:0x00a5. Please report as an issue. */
    @Override // org.h2.engine.Session
    public Session.DynamicSettings getDynamicSettings() {
        Session.DynamicSettings dynamicSettings = this.dynamicSettings;
        if (dynamicSettings == null) {
            String name = Mode.ModeEnum.REGULAR.name();
            TimeZoneProvider timeZone = DateTimeUtils.getTimeZone();
            String str = null;
            CommandInterface settingsCommand = getSettingsCommand(" IN (?, ?, ?)");
            Throwable th = null;
            try {
                ArrayList<? extends ParameterInterface> parameters = settingsCommand.getParameters();
                parameters.get(0).setValue(ValueVarchar.get("MODE"), false);
                parameters.get(1).setValue(ValueVarchar.get("TIME ZONE"), false);
                parameters.get(2).setValue(ValueVarchar.get("JAVA_OBJECT_SERIALIZER"), false);
                ResultInterface executeQuery = settingsCommand.executeQuery(0L, false);
                Throwable th2 = null;
                while (executeQuery.next()) {
                    try {
                        try {
                            Value[] currentRow = executeQuery.currentRow();
                            String string = currentRow[1].getString();
                            String string2 = currentRow[0].getString();
                            boolean z = -1;
                            switch (string2.hashCode()) {
                                case -1500153569:
                                    if (string2.equals("TIME ZONE")) {
                                        z = true;
                                        break;
                                    }
                                    break;
                                case 2372003:
                                    if (string2.equals("MODE")) {
                                        z = false;
                                        break;
                                    }
                                    break;
                                case 590707125:
                                    if (string2.equals("JAVA_OBJECT_SERIALIZER")) {
                                        z = 2;
                                        break;
                                    }
                                    break;
                            }
                            switch (z) {
                                case false:
                                    name = string;
                                    break;
                                case true:
                                    timeZone = TimeZoneProvider.ofId(string);
                                    break;
                                case true:
                                    str = string;
                                    break;
                            }
                        } catch (Throwable th3) {
                            if (executeQuery != null) {
                                if (th2 != null) {
                                    try {
                                        executeQuery.close();
                                    } catch (Throwable th4) {
                                        th2.addSuppressed(th4);
                                    }
                                } else {
                                    executeQuery.close();
                                }
                            }
                            throw th3;
                        }
                    } finally {
                    }
                }
                if (executeQuery != null) {
                    if (0 != 0) {
                        try {
                            executeQuery.close();
                        } catch (Throwable th5) {
                            th2.addSuppressed(th5);
                        }
                    } else {
                        executeQuery.close();
                    }
                }
                Mode mode = Mode.getInstance(name);
                if (mode == null) {
                    mode = Mode.getRegular();
                }
                Session.DynamicSettings dynamicSettings2 = new Session.DynamicSettings(mode, timeZone);
                dynamicSettings = dynamicSettings2;
                this.dynamicSettings = dynamicSettings2;
                if (str != null) {
                    String trim = str.trim();
                    if (!trim.isEmpty() && !trim.equals("null")) {
                        try {
                            this.javaObjectSerializer = (JavaObjectSerializer) JdbcUtils.loadUserClass(trim).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                        } catch (Exception e) {
                            throw DbException.convert(e);
                        }
                    }
                }
                this.javaObjectSerializer = null;
            } finally {
                if (settingsCommand != null) {
                    if (0 != 0) {
                        try {
                            settingsCommand.close();
                        } catch (Throwable th6) {
                            th.addSuppressed(th6);
                        }
                    } else {
                        settingsCommand.close();
                    }
                }
            }
        }
        return dynamicSettings;
    }

    private CommandInterface getSettingsCommand(String str) {
        return prepareCommand((!isOldInformationSchema() ? "SELECT SETTING_NAME, SETTING_VALUE FROM INFORMATION_SCHEMA.SETTINGS WHERE SETTING_NAME" : "SELECT NAME, `VALUE` FROM INFORMATION_SCHEMA.SETTINGS WHERE NAME") + str, Integer.MAX_VALUE);
    }

    @Override // org.h2.engine.CastDataProvider
    public ValueTimestampTimeZone currentTimestamp() {
        return DateTimeUtils.currentTimestamp(getDynamicSettings().timeZone);
    }

    @Override // org.h2.engine.CastDataProvider
    public TimeZoneProvider currentTimeZone() {
        return getDynamicSettings().timeZone;
    }

    @Override // org.h2.engine.CastDataProvider
    public Mode getMode() {
        return getDynamicSettings().mode;
    }

    @Override // org.h2.engine.Session
    public DatabaseMeta getDatabaseMeta() {
        return this.clientVersion >= 20 ? new DatabaseMetaRemote(this, this.transferList) : new DatabaseMetaLegacy(this);
    }

    @Override // org.h2.engine.Session
    public boolean isOldInformationSchema() {
        return this.oldInformationSchema || this.clientVersion < 20;
    }

    @Override // org.h2.engine.CastDataProvider
    public boolean zeroBasedEnums() {
        return false;
    }
}
