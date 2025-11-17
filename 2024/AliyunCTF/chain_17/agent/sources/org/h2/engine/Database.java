package org.h2.engine;

import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.tomcat.util.buf.AbstractChunk;
import org.h2.api.DatabaseEventListener;
import org.h2.api.ErrorCode;
import org.h2.api.JavaObjectSerializer;
import org.h2.api.TableEngine;
import org.h2.command.Prepared;
import org.h2.command.ddl.CreateTableData;
import org.h2.command.dml.SetTypes;
import org.h2.constraint.Constraint;
import org.h2.engine.Mode;
import org.h2.index.Cursor;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.message.TraceSystem;
import org.h2.mode.DefaultNullOrdering;
import org.h2.mode.PgCatalogSchema;
import org.h2.mvstore.MVStoreException;
import org.h2.mvstore.db.LobStorageMap;
import org.h2.mvstore.db.Store;
import org.h2.result.Row;
import org.h2.result.RowFactory;
import org.h2.result.SearchRow;
import org.h2.schema.InformationSchema;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.schema.Sequence;
import org.h2.schema.TriggerObject;
import org.h2.security.auth.Authenticator;
import org.h2.store.DataHandler;
import org.h2.store.FileLock;
import org.h2.store.FileLockMethod;
import org.h2.store.FileStore;
import org.h2.store.InDoubtTransaction;
import org.h2.store.LobStorageInterface;
import org.h2.store.fs.FileUtils;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.table.TableLinkConnection;
import org.h2.table.TableSynonym;
import org.h2.table.TableType;
import org.h2.table.TableView;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.h2.util.JdbcUtils;
import org.h2.util.MathUtils;
import org.h2.util.NetUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.SmallLRUCache;
import org.h2.util.SourceCompiler;
import org.h2.util.StringUtils;
import org.h2.util.TempFileDeleter;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;
import org.h2.value.CaseInsensitiveConcurrentMap;
import org.h2.value.CaseInsensitiveMap;
import org.h2.value.CompareMode;
import org.h2.value.TypeInfo;
import org.h2.value.ValueInteger;
import org.h2.value.ValueTimestampTimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/Database.class */
public final class Database implements DataHandler, CastDataProvider {
    private static int initialPowerOffCount;
    private static final boolean ASSERT;
    private static final ThreadLocal<SessionLocal> META_LOCK_DEBUGGING;
    private static final ThreadLocal<Database> META_LOCK_DEBUGGING_DB;
    private static final ThreadLocal<Throwable> META_LOCK_DEBUGGING_STACK;
    private static final SessionLocal[] EMPTY_SESSION_ARRAY;
    private static final String SYSTEM_USER_NAME = "DBA";
    private final boolean persistent;
    private final String databaseName;
    private final String databaseShortName;
    private final String databaseURL;
    private final String cipher;
    private final byte[] filePasswordHash;
    private final Schema mainSchema;
    private final Schema infoSchema;
    private final Schema pgCatalogSchema;
    private int nextSessionId;
    private final User systemUser;
    private SessionLocal systemSession;
    private SessionLocal lobSession;
    private final Table meta;
    private final Index metaIdIndex;
    private FileLock lock;
    private volatile boolean starting;
    private final TraceSystem traceSystem;
    private final Trace trace;
    private final FileLockMethod fileLockMethod;
    private final Role publicRole;
    private CompareMode compareMode;
    private boolean readOnly;
    private DatabaseEventListener eventListener;
    private int lockMode;
    private int maxLengthInplaceLob;
    private volatile int closeDelay;
    private DelayedDatabaseCloser delayedCloser;
    private volatile boolean closing;
    private boolean ignoreCase;
    private boolean deleteFilesOnDisconnect;
    private final String cacheType;
    private Mode mode;
    private DefaultNullOrdering defaultNullOrdering;
    private SmallLRUCache<String, String[]> lobFileListCache;
    private final boolean closeAtVmShutdown;
    private final boolean autoServerMode;
    private final int autoServerPort;
    private Server server;
    private HashMap<TableLinkConnection, TableLinkConnection> linkConnections;
    private int compactMode;
    private SourceCompiler compiler;
    private final LobStorageInterface lobStorage;
    private final int pageSize;
    private final DbSettings dbSettings;
    private final Store store;
    private boolean allowBuiltinAliasOverride;
    private JavaObjectSerializer javaObjectSerializer;
    private String javaObjectSerializerName;
    private volatile boolean javaObjectSerializerInitialized;
    private volatile boolean queryStatistics;
    private QueryStatisticsData queryStatisticsData;
    private boolean ignoreCatalogs;
    private Authenticator authenticator;
    static final /* synthetic */ boolean $assertionsDisabled;
    private final ConcurrentHashMap<String, RightOwner> usersAndRoles = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Setting> settings = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Schema> schemas = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Right> rights = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Comment> comments = new ConcurrentHashMap<>();
    private final HashMap<String, TableEngine> tableEngines = new HashMap<>();
    private final Set<SessionLocal> userSessions = Collections.synchronizedSet(new HashSet());
    private final AtomicReference<SessionLocal> exclusiveSession = new AtomicReference<>();
    private final BitSet objectIds = new BitSet();
    private final Object lobSyncObject = new Object();
    private final AtomicInteger nextTempTableId = new AtomicInteger();
    private final AtomicLong modificationDataId = new AtomicLong();
    private final AtomicLong modificationMetaId = new AtomicLong();
    private final AtomicLong remoteSettingsId = new AtomicLong();
    private String cluster = Constants.CLUSTERING_DISABLED;
    private int maxMemoryRows = SysProperties.MAX_MEMORY_ROWS;
    private int allowLiterals = 2;
    private int powerOffCount = initialPowerOffCount;
    private boolean optimizeReuseResults = true;
    private boolean referentialIntegrity = true;
    private int maxOperationMemory = 100000;
    private final TempFileDeleter tempFileDeleter = TempFileDeleter.getInstance();
    private int defaultTableType = 0;
    private final AtomicReference<DbException> backgroundException = new AtomicReference<>();
    private int queryStatisticsMaxEntries = 100;
    private RowFactory rowFactory = RowFactory.getRowFactory();

    static {
        $assertionsDisabled = !Database.class.desiredAssertionStatus();
        EMPTY_SESSION_ARRAY = new SessionLocal[0];
        boolean z = false;
        if (!$assertionsDisabled) {
            z = true;
            if (1 == 0) {
                throw new AssertionError();
            }
        }
        ASSERT = z;
        if (z) {
            META_LOCK_DEBUGGING = new ThreadLocal<>();
            META_LOCK_DEBUGGING_DB = new ThreadLocal<>();
            META_LOCK_DEBUGGING_STACK = new ThreadLocal<>();
        } else {
            META_LOCK_DEBUGGING = null;
            META_LOCK_DEBUGGING_DB = null;
            META_LOCK_DEBUGGING_STACK = null;
        }
    }

    public Database(ConnectionInfo connectionInfo, String str) {
        String str2;
        this.mode = Mode.getRegular();
        this.defaultNullOrdering = DefaultNullOrdering.LOW;
        if (ASSERT) {
            META_LOCK_DEBUGGING.set(null);
            META_LOCK_DEBUGGING_DB.set(null);
            META_LOCK_DEBUGGING_STACK.set(null);
        }
        String name = connectionInfo.getName();
        this.dbSettings = connectionInfo.getDbSettings();
        this.compareMode = CompareMode.getInstance(null, 0);
        this.persistent = connectionInfo.isPersistent();
        this.filePasswordHash = connectionInfo.getFilePasswordHash();
        this.databaseName = name;
        this.databaseShortName = parseDatabaseShortName();
        this.maxLengthInplaceLob = this.persistent ? 256 : AbstractChunk.ARRAY_MAX_SIZE;
        this.cipher = str;
        this.autoServerMode = connectionInfo.getProperty("AUTO_SERVER", false);
        this.autoServerPort = connectionInfo.getProperty("AUTO_SERVER_PORT", 0);
        this.pageSize = connectionInfo.getProperty("PAGE_SIZE", 4096);
        if (str != null && this.pageSize % 4096 != 0) {
            throw DbException.getUnsupportedException("CIPHER && PAGE_SIZE=" + this.pageSize);
        }
        if ("r".equals(StringUtils.toLowerEnglish(connectionInfo.getProperty("ACCESS_MODE_DATA", "rw")))) {
            this.readOnly = true;
        }
        String property = connectionInfo.getProperty("FILE_LOCK", (String) null);
        this.fileLockMethod = property != null ? FileLock.getFileLockMethod(property) : this.autoServerMode ? FileLockMethod.FILE : FileLockMethod.FS;
        this.databaseURL = connectionInfo.getURL();
        String removeProperty = connectionInfo.removeProperty("DATABASE_EVENT_LISTENER", (String) null);
        if (removeProperty != null) {
            setEventListenerClass(StringUtils.trim(removeProperty, true, true, '\''));
        }
        String removeProperty2 = connectionInfo.removeProperty("MODE", (String) null);
        if (removeProperty2 != null) {
            this.mode = Mode.getInstance(removeProperty2);
            if (this.mode == null) {
                throw DbException.get(ErrorCode.UNKNOWN_MODE_1, removeProperty2);
            }
        }
        String removeProperty3 = connectionInfo.removeProperty("DEFAULT_NULL_ORDERING", (String) null);
        if (removeProperty3 != null) {
            try {
                this.defaultNullOrdering = DefaultNullOrdering.valueOf(StringUtils.toUpperEnglish(removeProperty3));
            } catch (RuntimeException e) {
                throw DbException.getInvalidValueException("DEFAULT_NULL_ORDERING", removeProperty3);
            }
        }
        String property2 = connectionInfo.getProperty("JAVA_OBJECT_SERIALIZER", (String) null);
        if (property2 != null) {
            this.javaObjectSerializerName = StringUtils.trim(property2, true, true, '\'');
        }
        this.allowBuiltinAliasOverride = connectionInfo.getProperty("BUILTIN_ALIAS_OVERRIDE", false);
        if (this.autoServerMode && (this.readOnly || !this.persistent || this.fileLockMethod == FileLockMethod.NO || this.fileLockMethod == FileLockMethod.FS)) {
            throw DbException.getUnsupportedException("AUTO_SERVER=TRUE && (readOnly || inMemory || FILE_LOCK=NO || FILE_LOCK=FS)");
        }
        this.closeAtVmShutdown = connectionInfo.getProperty("DB_CLOSE_ON_EXIT", this.persistent);
        if (this.autoServerMode && !this.closeAtVmShutdown) {
            throw DbException.getUnsupportedException("AUTO_SERVER=TRUE && DB_CLOSE_ON_EXIT=FALSE");
        }
        int intProperty = connectionInfo.getIntProperty(9, 1);
        int intProperty2 = connectionInfo.getIntProperty(8, 0);
        this.cacheType = StringUtils.toUpperEnglish(connectionInfo.removeProperty("CACHE_TYPE", Constants.CACHE_TYPE_DEFAULT));
        this.ignoreCatalogs = connectionInfo.getProperty("IGNORE_CATALOGS", this.dbSettings.ignoreCatalogs);
        this.lockMode = connectionInfo.getProperty("LOCK_MODE", 3);
        if (this.persistent) {
            if (this.readOnly) {
                if (intProperty >= 3) {
                    str2 = Utils.getProperty("java.io.tmpdir", ".") + "/h2_" + System.currentTimeMillis() + Constants.SUFFIX_TRACE_FILE;
                } else {
                    str2 = null;
                }
            } else {
                str2 = name + Constants.SUFFIX_TRACE_FILE;
            }
        } else {
            str2 = null;
        }
        this.traceSystem = new TraceSystem(str2);
        this.traceSystem.setLevelFile(intProperty);
        this.traceSystem.setLevelSystemOut(intProperty2);
        this.trace = this.traceSystem.getTrace(2);
        this.trace.info("opening {0} (build {1})", name, 224);
        try {
            if (this.persistent) {
                String str3 = name + Constants.SUFFIX_LOCK_FILE;
                if (this.readOnly) {
                    if (FileUtils.exists(str3)) {
                        throw DbException.get(ErrorCode.DATABASE_ALREADY_OPEN_1, "Lock file exists: " + str3);
                    }
                } else if (this.fileLockMethod != FileLockMethod.NO && this.fileLockMethod != FileLockMethod.FS) {
                    this.lock = new FileLock(this.traceSystem, str3, 1000);
                    this.lock.lock(this.fileLockMethod);
                    if (this.autoServerMode) {
                        startServer(this.lock.getUniqueId());
                    }
                }
                deleteOldTempFiles();
            }
            this.starting = true;
            if (this.dbSettings.mvStore) {
                this.store = new Store(this, connectionInfo.getFileEncryptionKey());
                this.starting = false;
                this.systemUser = new User(this, 0, SYSTEM_USER_NAME, true);
                this.systemUser.setAdmin(true);
                this.mainSchema = new Schema(this, 0, sysIdentifier("PUBLIC"), this.systemUser, true);
                this.infoSchema = new InformationSchema(this, this.systemUser);
                this.schemas.put(this.mainSchema.getName(), this.mainSchema);
                this.schemas.put(this.infoSchema.getName(), this.infoSchema);
                if (this.mode.getEnum() == Mode.ModeEnum.PostgreSQL) {
                    this.pgCatalogSchema = new PgCatalogSchema(this, this.systemUser);
                    this.schemas.put(this.pgCatalogSchema.getName(), this.pgCatalogSchema);
                } else {
                    this.pgCatalogSchema = null;
                }
                this.publicRole = new Role(this, 0, sysIdentifier("PUBLIC"), true);
                this.usersAndRoles.put(this.publicRole.getName(), this.publicRole);
                this.systemSession = createSession(this.systemUser);
                this.lobSession = createSession(this.systemUser);
                Set<String> keySet = this.dbSettings.getSettings().keySet();
                this.store.getTransactionStore().init(this.lobSession);
                keySet.removeIf(str4 -> {
                    return str4.startsWith("PAGE_STORE_");
                });
                CreateTableData createSysTableData = createSysTableData();
                this.starting = true;
                this.meta = this.mainSchema.createTable(createSysTableData);
                this.metaIdIndex = this.meta.addIndex(this.systemSession, "SYS_ID", 0, IndexColumn.wrap(new Column[]{createSysTableData.columns.get(0)}), 1, IndexType.createPrimaryKey(false, false), true, null);
                this.systemSession.commit(true);
                this.objectIds.set(0);
                executeMeta();
                this.systemSession.commit(true);
                this.store.getTransactionStore().endLeftoverTransactions();
                this.store.removeTemporaryMaps(this.objectIds);
                recompileInvalidViews();
                this.starting = false;
                if (!this.readOnly) {
                    String typeName = SetTypes.getTypeName(28);
                    if (this.settings.get(typeName) == null) {
                        Setting setting = new Setting(this, allocateObjectId(), typeName);
                        setting.setIntValue(224);
                        lockMeta(this.systemSession);
                        addDatabaseObject(this.systemSession, setting);
                    }
                }
                this.lobStorage = new LobStorageMap(this);
                this.lobSession.commit(true);
                this.systemSession.commit(true);
                this.trace.info("opened {0}", name);
                if (this.persistent) {
                    setWriteDelay(connectionInfo.getProperty("WRITE_DELAY", 500));
                }
                if (this.closeAtVmShutdown || this.persistent) {
                    OnExitDatabaseCloser.register(this);
                }
                return;
            }
            throw new UnsupportedOperationException();
        } catch (Throwable th) {
            try {
                if (th instanceof OutOfMemoryError) {
                    th.fillInStackTrace();
                }
                if (th instanceof DbException) {
                    if (((DbException) th).getErrorCode() == 90020) {
                        stopServer();
                    } else {
                        this.trace.error(th, "opening {0}", name);
                    }
                }
                this.traceSystem.close();
                closeOpenFilesAndUnlock();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw DbException.convert(th);
        }
    }

    public int getLockTimeout() {
        Setting findSetting = findSetting(SetTypes.getTypeName(5));
        if (findSetting == null) {
            return 2000;
        }
        return findSetting.getIntValue();
    }

    public RowFactory getRowFactory() {
        return this.rowFactory;
    }

    public void setRowFactory(RowFactory rowFactory) {
        this.rowFactory = rowFactory;
    }

    public static void setInitialPowerOffCount(int i) {
        initialPowerOffCount = i;
    }

    public void setPowerOffCount(int i) {
        if (this.powerOffCount == -1) {
            return;
        }
        this.powerOffCount = i;
    }

    public Store getStore() {
        return this.store;
    }

    public long getModificationDataId() {
        return this.modificationDataId.get();
    }

    public long getNextModificationDataId() {
        return this.modificationDataId.incrementAndGet();
    }

    public long getModificationMetaId() {
        return this.modificationMetaId.get();
    }

    public long getNextModificationMetaId() {
        this.modificationDataId.incrementAndGet();
        return this.modificationMetaId.incrementAndGet() - 1;
    }

    public long getRemoteSettingsId() {
        return this.remoteSettingsId.get();
    }

    public long getNextRemoteSettingsId() {
        return this.remoteSettingsId.incrementAndGet();
    }

    public int getPowerOffCount() {
        return this.powerOffCount;
    }

    @Override // org.h2.store.DataHandler
    public void checkPowerOff() {
        if (this.powerOffCount != 0) {
            checkPowerOff2();
        }
    }

    private void checkPowerOff2() {
        if (this.powerOffCount > 1) {
            this.powerOffCount--;
            return;
        }
        if (this.powerOffCount != -1) {
            try {
                this.powerOffCount = -1;
                this.store.closeImmediately();
                if (this.lock != null) {
                    stopServer();
                    this.lock.unlock();
                    this.lock = null;
                }
                if (this.traceSystem != null) {
                    this.traceSystem.close();
                }
            } catch (DbException e) {
                DbException.traceThrowable(e);
            }
        }
        Engine.close(this.databaseName);
        throw DbException.get(ErrorCode.DATABASE_IS_CLOSED);
    }

    public Trace getTrace(int i) {
        return this.traceSystem.getTrace(i);
    }

    @Override // org.h2.store.DataHandler
    public FileStore openFile(String str, String str2, boolean z) {
        if (z && !FileUtils.exists(str)) {
            throw DbException.get(ErrorCode.FILE_NOT_FOUND_1, str);
        }
        FileStore open = FileStore.open(this, str, str2, this.cipher, this.filePasswordHash);
        try {
            open.init();
            return open;
        } catch (DbException e) {
            open.closeSilently();
            throw e;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean validateFilePasswordHash(String str, byte[] bArr) {
        if (!Objects.equals(str, this.cipher)) {
            return false;
        }
        return Utils.compareSecure(bArr, this.filePasswordHash);
    }

    private String parseDatabaseShortName() {
        String lowerEnglish;
        String str = this.databaseName;
        int length = str.length();
        int i = length;
        while (true) {
            i--;
            if (i >= 0) {
                switch (str.charAt(i)) {
                    case '/':
                    case ':':
                    case '\\':
                        break;
                }
            }
        }
        int i2 = i + 1;
        String substring = i2 == length ? "UNNAMED" : str.substring(i2);
        if (this.dbSettings.databaseToUpper) {
            lowerEnglish = StringUtils.toUpperEnglish(substring);
        } else {
            lowerEnglish = this.dbSettings.databaseToLower ? StringUtils.toLowerEnglish(substring) : substring;
        }
        return StringUtils.truncateString(lowerEnglish, 256);
    }

    private CreateTableData createSysTableData() {
        CreateTableData createTableData = new CreateTableData();
        ArrayList<Column> arrayList = createTableData.columns;
        Column column = new Column("ID", TypeInfo.TYPE_INTEGER);
        column.setNullable(false);
        arrayList.add(column);
        arrayList.add(new Column("HEAD", TypeInfo.TYPE_INTEGER));
        arrayList.add(new Column("TYPE", TypeInfo.TYPE_INTEGER));
        arrayList.add(new Column("SQL", TypeInfo.TYPE_VARCHAR));
        createTableData.tableName = "SYS";
        createTableData.id = 0;
        createTableData.temporary = false;
        createTableData.persistData = this.persistent;
        createTableData.persistIndexes = this.persistent;
        createTableData.isHidden = true;
        createTableData.session = this.systemSession;
        return createTableData;
    }

    private void executeMeta() {
        Cursor find = this.metaIdIndex.find(this.systemSession, null, null);
        ArrayList<MetaRecord> arrayList = new ArrayList<>();
        ArrayList arrayList2 = new ArrayList();
        ArrayList<MetaRecord> arrayList3 = new ArrayList<>();
        ArrayList arrayList4 = new ArrayList();
        ArrayList<MetaRecord> arrayList5 = new ArrayList<>();
        while (find.next()) {
            MetaRecord metaRecord = new MetaRecord(find.get());
            this.objectIds.set(metaRecord.getId());
            switch (metaRecord.getObjectType()) {
                case 0:
                case 1:
                case 3:
                case 11:
                    arrayList3.add(metaRecord);
                    break;
                case 2:
                case 6:
                case 9:
                case 10:
                    arrayList.add(metaRecord);
                    break;
                case 4:
                case 7:
                case 8:
                default:
                    arrayList5.add(metaRecord);
                    break;
                case 5:
                    arrayList4.add(metaRecord);
                    break;
                case 12:
                    arrayList2.add(metaRecord);
                    break;
            }
        }
        SessionLocal sessionLocal = this.systemSession;
        sessionLocal.lock();
        try {
            executeMeta(arrayList);
            int size = arrayList2.size();
            if (size > 0) {
                int i = 0;
                while (true) {
                    DbException dbException = null;
                    for (int i2 = 0; i2 < size; i2++) {
                        MetaRecord metaRecord2 = (MetaRecord) arrayList2.get(i2);
                        try {
                            metaRecord2.prepareAndExecute(this, sessionLocal, this.eventListener);
                        } catch (DbException e) {
                            if (dbException == null) {
                                dbException = e;
                            }
                            int i3 = i;
                            i++;
                            arrayList2.set(i3, metaRecord2);
                        }
                    }
                    if (dbException != null) {
                        if (size != i) {
                            size = i;
                        } else {
                            throw dbException;
                        }
                    }
                }
            }
            executeMeta(arrayList3);
            int size2 = arrayList4.size();
            if (size2 > 0) {
                ArrayList arrayList6 = new ArrayList(size2);
                for (int i4 = 0; i4 < size2; i4++) {
                    Prepared prepare = ((MetaRecord) arrayList4.get(i4)).prepare(this, sessionLocal, this.eventListener);
                    if (prepare != null) {
                        arrayList6.add(prepare);
                    }
                }
                arrayList6.sort(MetaRecord.CONSTRAINTS_COMPARATOR);
                Iterator it = arrayList6.iterator();
                while (it.hasNext()) {
                    Prepared prepared = (Prepared) it.next();
                    MetaRecord.execute(this, prepared, this.eventListener, prepared.getSQL());
                }
            }
            executeMeta(arrayList5);
            sessionLocal.unlock();
        } catch (Throwable th) {
            sessionLocal.unlock();
            throw th;
        }
    }

    private void executeMeta(ArrayList<MetaRecord> arrayList) {
        if (!arrayList.isEmpty()) {
            arrayList.sort(null);
            Iterator<MetaRecord> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().prepareAndExecute(this, this.systemSession, this.eventListener);
            }
        }
    }

    private void startServer(String str) {
        try {
            this.server = Server.createTcpServer("-tcpPort", Integer.toString(this.autoServerPort), "-tcpAllowOthers", "-tcpDaemon", "-key", str, this.databaseName);
            this.server.start();
            String localAddress = NetUtils.getLocalAddress();
            this.lock.setProperty("server", localAddress + ":" + this.server.getPort());
            this.lock.setProperty("hostName", NetUtils.getHostName(localAddress));
            this.lock.save();
        } catch (SQLException e) {
            throw DbException.convert(e);
        }
    }

    private void stopServer() {
        if (this.server != null) {
            Server server = this.server;
            this.server = null;
            server.stop();
        }
    }

    private void recompileInvalidViews() {
        boolean z;
        do {
            z = false;
            Iterator<Schema> it = this.schemas.values().iterator();
            while (it.hasNext()) {
                for (Table table : it.next().getAllTablesAndViews(null)) {
                    if (table instanceof TableView) {
                        TableView tableView = (TableView) table;
                        if (tableView.isInvalid()) {
                            tableView.recompile(this.systemSession, true, false);
                            if (!tableView.isInvalid()) {
                                z = true;
                            }
                        }
                    }
                }
            }
        } while (z);
        TableView.clearIndexCaches(this);
    }

    private void addMeta(SessionLocal sessionLocal, DbObject dbObject) {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        int id = dbObject.getId();
        if (id > 0 && !dbObject.isTemporary() && !isReadOnly()) {
            Row templateRow = this.meta.getTemplateRow();
            MetaRecord.populateRowFromDBObject(dbObject, templateRow);
            if (!$assertionsDisabled && !this.objectIds.get(id)) {
                throw new AssertionError();
            }
            if (SysProperties.CHECK) {
                verifyMetaLocked(sessionLocal);
            }
            Cursor find = this.metaIdIndex.find(sessionLocal, templateRow, templateRow);
            if (!find.next()) {
                this.meta.addRow(sessionLocal, templateRow);
                return;
            }
            if (!$assertionsDisabled && !this.starting) {
                throw new AssertionError();
            }
            Row row = find.get();
            MetaRecord metaRecord = new MetaRecord(row);
            if (!$assertionsDisabled && metaRecord.getId() != dbObject.getId()) {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && metaRecord.getObjectType() != dbObject.getType()) {
                throw new AssertionError();
            }
            if (!metaRecord.getSQL().equals(dbObject.getCreateSQLForMeta())) {
                this.meta.updateRow(sessionLocal, row, templateRow);
            }
        }
    }

    public void verifyMetaLocked(SessionLocal sessionLocal) {
        if (this.lockMode != 0 && this.meta != null && !this.meta.isLockedExclusivelyBy(sessionLocal)) {
            throw DbException.getInternalError();
        }
    }

    public boolean lockMeta(SessionLocal sessionLocal) {
        if (this.meta == null) {
            return true;
        }
        if (ASSERT) {
            lockMetaAssertion(sessionLocal);
        }
        return this.meta.lock(sessionLocal, 2);
    }

    private void lockMetaAssertion(SessionLocal sessionLocal) {
        if (META_LOCK_DEBUGGING_DB.get() != null && META_LOCK_DEBUGGING_DB.get() != this) {
            SessionLocal sessionLocal2 = META_LOCK_DEBUGGING.get();
            if (sessionLocal2 == null) {
                META_LOCK_DEBUGGING.set(sessionLocal);
                META_LOCK_DEBUGGING_DB.set(this);
                META_LOCK_DEBUGGING_STACK.set(new Throwable("Last meta lock granted in this stack trace, this is debug information for following IllegalStateException"));
            } else if (sessionLocal2 != sessionLocal) {
                META_LOCK_DEBUGGING_STACK.get().printStackTrace();
                throw new IllegalStateException("meta currently locked by " + sessionLocal2 + ", sessionid=" + sessionLocal2.getId() + " and trying to be locked by different session, " + sessionLocal + ", sessionid=" + sessionLocal.getId() + " on same thread");
            }
        }
    }

    public void unlockMeta(SessionLocal sessionLocal) {
        if (this.meta != null) {
            unlockMetaDebug(sessionLocal);
            this.meta.unlock(sessionLocal);
            sessionLocal.unlock(this.meta);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void unlockMetaDebug(SessionLocal sessionLocal) {
        if (ASSERT && META_LOCK_DEBUGGING.get() == sessionLocal) {
            META_LOCK_DEBUGGING.set(null);
            META_LOCK_DEBUGGING_DB.set(null);
            META_LOCK_DEBUGGING_STACK.set(null);
        }
    }

    public void removeMeta(SessionLocal sessionLocal, int i) {
        if (i > 0 && !this.starting) {
            SearchRow createRow = this.meta.getRowFactory().createRow();
            createRow.setValue(0, ValueInteger.get(i));
            boolean lockMeta = lockMeta(sessionLocal);
            try {
                Cursor find = this.metaIdIndex.find(sessionLocal, createRow, createRow);
                if (find.next()) {
                    this.meta.removeRow(sessionLocal, find.get());
                    if (SysProperties.CHECK) {
                        checkMetaFree(sessionLocal, i);
                    }
                }
                sessionLocal.scheduleDatabaseObjectIdForRelease(i);
            } finally {
                if (!lockMeta) {
                    unlockMeta(sessionLocal);
                }
            }
        }
    }

    public void releaseDatabaseObjectIds(BitSet bitSet) {
        synchronized (this.objectIds) {
            this.objectIds.andNot(bitSet);
        }
    }

    private ConcurrentHashMap<String, DbObject> getMap(int i) {
        AbstractMap abstractMap;
        switch (i) {
            case 2:
            case 7:
                abstractMap = this.usersAndRoles;
                break;
            case 3:
            case 4:
            case 5:
            case 9:
            case 11:
            case 12:
            default:
                throw DbException.getInternalError("type=" + i);
            case 6:
                abstractMap = this.settings;
                break;
            case 8:
                abstractMap = this.rights;
                break;
            case 10:
                abstractMap = this.schemas;
                break;
            case 13:
                abstractMap = this.comments;
                break;
        }
        return (ConcurrentHashMap) abstractMap;
    }

    public void addSchemaObject(SessionLocal sessionLocal, SchemaObject schemaObject) {
        if (schemaObject.getId() > 0 && !this.starting) {
            checkWritingAllowed();
        }
        lockMeta(sessionLocal);
        synchronized (this) {
            schemaObject.getSchema().add(schemaObject);
            addMeta(sessionLocal, schemaObject);
        }
    }

    public synchronized void addDatabaseObject(SessionLocal sessionLocal, DbObject dbObject) {
        if (dbObject.getId() > 0 && !this.starting) {
            checkWritingAllowed();
        }
        ConcurrentHashMap<String, DbObject> map = getMap(dbObject.getType());
        if (dbObject.getType() == 2) {
            User user = (User) dbObject;
            if (user.isAdmin() && this.systemUser.getName().equals(SYSTEM_USER_NAME)) {
                this.systemUser.rename(user.getName());
            }
        }
        String name = dbObject.getName();
        if (SysProperties.CHECK && map.get(name) != null) {
            throw DbException.getInternalError("object already exists");
        }
        lockMeta(sessionLocal);
        addMeta(sessionLocal, dbObject);
        map.put(name, dbObject);
    }

    public Comment findComment(DbObject dbObject) {
        if (dbObject.getType() == 13) {
            return null;
        }
        return this.comments.get(Comment.getKey(dbObject));
    }

    public Role findRole(String str) {
        RightOwner findUserOrRole = findUserOrRole(str);
        if (findUserOrRole instanceof Role) {
            return (Role) findUserOrRole;
        }
        return null;
    }

    public Schema findSchema(String str) {
        if (str == null) {
            return null;
        }
        return this.schemas.get(str);
    }

    public Setting findSetting(String str) {
        return this.settings.get(str);
    }

    public User findUser(String str) {
        RightOwner findUserOrRole = findUserOrRole(str);
        if (findUserOrRole instanceof User) {
            return (User) findUserOrRole;
        }
        return null;
    }

    public User getUser(String str) {
        User findUser = findUser(str);
        if (findUser == null) {
            throw DbException.get(ErrorCode.USER_NOT_FOUND_1, str);
        }
        return findUser;
    }

    public RightOwner findUserOrRole(String str) {
        return this.usersAndRoles.get(StringUtils.toUpperEnglish(str));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized SessionLocal createSession(User user, NetworkConnectionInfo networkConnectionInfo) {
        if (this.closing) {
            return null;
        }
        if (this.exclusiveSession.get() != null) {
            throw DbException.get(ErrorCode.DATABASE_IS_IN_EXCLUSIVE_MODE);
        }
        SessionLocal createSession = createSession(user);
        createSession.setNetworkConnectionInfo(networkConnectionInfo);
        this.userSessions.add(createSession);
        this.trace.info("connecting session #{0} to {1}", Integer.valueOf(createSession.getId()), this.databaseName);
        if (this.delayedCloser != null) {
            this.delayedCloser.reset();
            this.delayedCloser = null;
        }
        return createSession;
    }

    private SessionLocal createSession(User user) {
        int i = this.nextSessionId + 1;
        this.nextSessionId = i;
        return new SessionLocal(this, user, i);
    }

    public synchronized void removeSession(SessionLocal sessionLocal) {
        if (sessionLocal != null) {
            this.exclusiveSession.compareAndSet(sessionLocal, null);
            if (this.userSessions.remove(sessionLocal)) {
                this.trace.info("disconnecting session #{0}", Integer.valueOf(sessionLocal.getId()));
            }
        }
        if (isUserSession(sessionLocal)) {
            if (this.userSessions.isEmpty()) {
                if (this.closeDelay == 0) {
                    close();
                } else if (this.closeDelay < 0) {
                    return;
                } else {
                    this.delayedCloser = new DelayedDatabaseCloser(this, this.closeDelay * 1000);
                }
            }
            if (sessionLocal != null) {
                this.trace.info("disconnected session #{0}", Integer.valueOf(sessionLocal.getId()));
            }
        }
    }

    boolean isUserSession(SessionLocal sessionLocal) {
        return (sessionLocal == this.systemSession || sessionLocal == this.lobSession) ? false : true;
    }

    private synchronized void closeAllSessionsExcept(SessionLocal sessionLocal) {
        SessionLocal[] sessionLocalArr = (SessionLocal[]) this.userSessions.toArray(EMPTY_SESSION_ARRAY);
        boolean z = true;
        for (SessionLocal sessionLocal2 : sessionLocalArr) {
            if (sessionLocal2 != sessionLocal) {
                sessionLocal2.suspend();
                z = false;
            }
        }
        if (z) {
            return;
        }
        int lockTimeout = getLockTimeout();
        long max = Math.max(lockTimeout / 10, 1);
        long j = lockTimeout * 2000000;
        long nanoTime = System.nanoTime();
        do {
            boolean z2 = true;
            int length = sessionLocalArr.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                SessionLocal sessionLocal3 = sessionLocalArr[i];
                if (sessionLocal3 == sessionLocal || sessionLocal3.isClosed()) {
                    i++;
                } else {
                    z2 = false;
                    break;
                }
            }
            if (z2) {
                return;
            } else {
                try {
                    wait(max);
                } catch (InterruptedException e) {
                }
            }
        } while (System.nanoTime() - nanoTime <= j);
        for (SessionLocal sessionLocal4 : sessionLocalArr) {
            if (sessionLocal4 != sessionLocal && !sessionLocal4.isClosed()) {
                try {
                    sessionLocal4.close();
                } catch (Throwable th) {
                    this.trace.error(th, "disconnecting session #{0}", Integer.valueOf(sessionLocal4.getId()));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void close() {
        close(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onShutdown() {
        if (this.closeAtVmShutdown) {
            close(true);
        } else if (this.persistent) {
            checkpoint();
        }
    }

    private void close(boolean z) {
        DbException andSet = this.backgroundException.getAndSet(null);
        try {
            closeImpl(z);
            if (andSet != null) {
                throw DbException.get(andSet.getErrorCode(), andSet, andSet.getMessage());
            }
        } catch (Throwable th) {
            if (andSet != null) {
                th.addSuppressed(andSet);
            }
            throw th;
        }
    }

    private void closeImpl(boolean z) {
        synchronized (this) {
            if (this.closing || !(z || this.userSessions.isEmpty())) {
                return;
            }
            this.closing = true;
            stopServer();
            if (!this.userSessions.isEmpty()) {
                if (!$assertionsDisabled && !z) {
                    throw new AssertionError();
                }
                this.trace.info("closing {0} from shutdown hook", this.databaseName);
                closeAllSessionsExcept(null);
            }
            this.trace.info("closing {0}", this.databaseName);
            if (this.eventListener != null) {
                this.closing = false;
                DatabaseEventListener databaseEventListener = this.eventListener;
                this.eventListener = null;
                databaseEventListener.closingDatabase();
                this.closing = true;
                if (!this.userSessions.isEmpty()) {
                    this.trace.info("event listener {0} left connection open", databaseEventListener.getClass().getName());
                    closeAllSessionsExcept(null);
                }
            }
            try {
                try {
                    if (this.systemSession != null) {
                        if (this.powerOffCount != -1) {
                            Iterator<Schema> it = this.schemas.values().iterator();
                            while (it.hasNext()) {
                                for (Table table : it.next().getAllTablesAndViews(null)) {
                                    if (table.isGlobalTemporary()) {
                                        removeSchemaObject(this.systemSession, table);
                                    } else {
                                        table.close(this.systemSession);
                                    }
                                }
                            }
                            Iterator<Schema> it2 = this.schemas.values().iterator();
                            while (it2.hasNext()) {
                                Iterator<Sequence> it3 = it2.next().getAllSequences().iterator();
                                while (it3.hasNext()) {
                                    it3.next().close();
                                }
                            }
                        }
                        Iterator<Schema> it4 = this.schemas.values().iterator();
                        while (it4.hasNext()) {
                            Iterator<TriggerObject> it5 = it4.next().getAllTriggers().iterator();
                            while (it5.hasNext()) {
                                try {
                                    it5.next().close();
                                } catch (SQLException e) {
                                    this.trace.error(e, org.apache.coyote.http11.Constants.CLOSE);
                                }
                            }
                        }
                        if (this.powerOffCount != -1) {
                            this.meta.close(this.systemSession);
                            this.systemSession.commit(true);
                        }
                        if (this.lobSession != null) {
                            this.lobSession.close();
                            this.lobSession = null;
                        }
                        this.systemSession.close();
                        this.systemSession = null;
                    }
                    this.tempFileDeleter.deleteAll();
                    closeOpenFilesAndUnlock();
                } finally {
                    Engine.close(this.databaseName);
                }
            } catch (DbException | MVStoreException e2) {
                this.trace.error(e2, org.apache.coyote.http11.Constants.CLOSE);
            }
            this.trace.info("closed");
            this.traceSystem.close();
            OnExitDatabaseCloser.unregister(this);
            if (this.deleteFilesOnDisconnect && this.persistent) {
                this.deleteFilesOnDisconnect = false;
                try {
                    DeleteDbFiles.execute(FileUtils.getParent(this.databaseName), FileUtils.getName(this.databaseName), true);
                } catch (Exception e3) {
                }
            }
        }
    }

    private synchronized void closeOpenFilesAndUnlock() {
        try {
            if (this.lobStorage != null) {
                this.lobStorage.close();
            }
            if (this.store != null && !this.store.getMvStore().isClosed()) {
                if (this.compactMode == 81) {
                    this.store.closeImmediately();
                } else {
                    this.store.close((this.compactMode == 82 || this.compactMode == 84 || this.dbSettings.defragAlways) ? -1 : this.dbSettings.maxCompactTime);
                }
                if (this.persistent && (this.lock != null || this.fileLockMethod == FileLockMethod.NO || this.fileLockMethod == FileLockMethod.FS)) {
                    deleteOldTempFiles();
                }
            }
        } finally {
            if (this.lock != null) {
                this.lock.unlock();
                this.lock = null;
            }
        }
    }

    private synchronized void closeFiles() {
        try {
            this.store.closeImmediately();
        } catch (DbException e) {
            this.trace.error(e, org.apache.coyote.http11.Constants.CLOSE);
        }
    }

    private void checkMetaFree(SessionLocal sessionLocal, int i) {
        SearchRow createRow = this.meta.getRowFactory().createRow();
        createRow.setValue(0, ValueInteger.get(i));
        if (this.metaIdIndex.find(sessionLocal, createRow, createRow).next()) {
            throw DbException.getInternalError();
        }
    }

    public int allocateObjectId() {
        int nextClearBit;
        synchronized (this.objectIds) {
            nextClearBit = this.objectIds.nextClearBit(0);
            this.objectIds.set(nextClearBit);
        }
        return nextClearBit;
    }

    public User getSystemUser() {
        return this.systemUser;
    }

    public Schema getMainSchema() {
        return this.mainSchema;
    }

    public ArrayList<Comment> getAllComments() {
        return new ArrayList<>(this.comments.values());
    }

    public int getAllowLiterals() {
        if (this.starting) {
            return 2;
        }
        return this.allowLiterals;
    }

    public ArrayList<Right> getAllRights() {
        return new ArrayList<>(this.rights.values());
    }

    public ArrayList<Table> getAllTablesAndViews() {
        ArrayList<Table> arrayList = new ArrayList<>();
        Iterator<Schema> it = this.schemas.values().iterator();
        while (it.hasNext()) {
            arrayList.addAll(it.next().getAllTablesAndViews(null));
        }
        return arrayList;
    }

    public ArrayList<TableSynonym> getAllSynonyms() {
        ArrayList<TableSynonym> arrayList = new ArrayList<>();
        Iterator<Schema> it = this.schemas.values().iterator();
        while (it.hasNext()) {
            arrayList.addAll(it.next().getAllSynonyms());
        }
        return arrayList;
    }

    public Collection<Schema> getAllSchemas() {
        return this.schemas.values();
    }

    public Collection<Schema> getAllSchemasNoMeta() {
        return this.schemas.values();
    }

    public Collection<Setting> getAllSettings() {
        return this.settings.values();
    }

    public Collection<RightOwner> getAllUsersAndRoles() {
        return this.usersAndRoles.values();
    }

    public String getCacheType() {
        return this.cacheType;
    }

    public String getCluster() {
        return this.cluster;
    }

    @Override // org.h2.store.DataHandler
    public CompareMode getCompareMode() {
        return this.compareMode;
    }

    @Override // org.h2.store.DataHandler
    public String getDatabasePath() {
        if (this.persistent) {
            return FileUtils.toRealPath(this.databaseName);
        }
        return null;
    }

    public String getShortName() {
        return this.databaseShortName;
    }

    public String getName() {
        return this.databaseName;
    }

    public SessionLocal[] getSessions(boolean z) {
        ArrayList arrayList;
        synchronized (this) {
            arrayList = new ArrayList(this.userSessions);
        }
        if (z) {
            SessionLocal sessionLocal = this.systemSession;
            if (sessionLocal != null) {
                arrayList.add(sessionLocal);
            }
            SessionLocal sessionLocal2 = this.lobSession;
            if (sessionLocal2 != null) {
                arrayList.add(sessionLocal2);
            }
        }
        return (SessionLocal[]) arrayList.toArray(new SessionLocal[0]);
    }

    public void updateMeta(SessionLocal sessionLocal, DbObject dbObject) {
        int id = dbObject.getId();
        if (id > 0) {
            if (!this.starting && !dbObject.isTemporary()) {
                Row templateRow = this.meta.getTemplateRow();
                MetaRecord.populateRowFromDBObject(dbObject, templateRow);
                Row row = this.metaIdIndex.getRow(sessionLocal, id);
                if (row != null) {
                    this.meta.updateRow(sessionLocal, row, templateRow);
                }
            }
            synchronized (this.objectIds) {
                this.objectIds.set(id);
            }
        }
    }

    public synchronized void renameSchemaObject(SessionLocal sessionLocal, SchemaObject schemaObject, String str) {
        checkWritingAllowed();
        schemaObject.getSchema().rename(schemaObject, str);
        updateMetaAndFirstLevelChildren(sessionLocal, schemaObject);
    }

    private synchronized void updateMetaAndFirstLevelChildren(SessionLocal sessionLocal, DbObject dbObject) {
        ArrayList<DbObject> children = dbObject.getChildren();
        Comment findComment = findComment(dbObject);
        if (findComment != null) {
            throw DbException.getInternalError(findComment.toString());
        }
        updateMeta(sessionLocal, dbObject);
        if (children != null) {
            Iterator<DbObject> it = children.iterator();
            while (it.hasNext()) {
                DbObject next = it.next();
                if (next.getCreateSQL() != null) {
                    updateMeta(sessionLocal, next);
                }
            }
        }
    }

    public synchronized void renameDatabaseObject(SessionLocal sessionLocal, DbObject dbObject, String str) {
        checkWritingAllowed();
        ConcurrentHashMap<String, DbObject> map = getMap(dbObject.getType());
        if (SysProperties.CHECK) {
            if (!map.containsKey(dbObject.getName())) {
                throw DbException.getInternalError("not found: " + dbObject.getName());
            }
            if (dbObject.getName().equals(str) || map.containsKey(str)) {
                throw DbException.getInternalError("object already exists: " + str);
            }
        }
        dbObject.checkRename();
        map.remove(dbObject.getName());
        dbObject.rename(str);
        map.put(str, dbObject);
        updateMetaAndFirstLevelChildren(sessionLocal, dbObject);
    }

    private void deleteOldTempFiles() {
        for (String str : FileUtils.newDirectoryStream(FileUtils.getParent(this.databaseName))) {
            if (str.endsWith(Constants.SUFFIX_TEMP_FILE) && str.startsWith(this.databaseName)) {
                FileUtils.tryDelete(str);
            }
        }
    }

    public Schema getSchema(String str) {
        Schema findSchema = findSchema(str);
        if (findSchema == null) {
            throw DbException.get(ErrorCode.SCHEMA_NOT_FOUND_1, str);
        }
        return findSchema;
    }

    public synchronized void removeDatabaseObject(SessionLocal sessionLocal, DbObject dbObject) {
        checkWritingAllowed();
        String name = dbObject.getName();
        ConcurrentHashMap<String, DbObject> map = getMap(dbObject.getType());
        if (SysProperties.CHECK && !map.containsKey(name)) {
            throw DbException.getInternalError("not found: " + name);
        }
        Comment findComment = findComment(dbObject);
        lockMeta(sessionLocal);
        if (findComment != null) {
            removeDatabaseObject(sessionLocal, findComment);
        }
        int id = dbObject.getId();
        dbObject.removeChildrenAndResources(sessionLocal);
        map.remove(name);
        removeMeta(sessionLocal, id);
    }

    public Table getDependentTable(SchemaObject schemaObject, Table table) {
        switch (schemaObject.getType()) {
            case 1:
            case 2:
            case 4:
            case 5:
            case 8:
            case 13:
                return null;
            case 3:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
            case 12:
            default:
                HashSet<DbObject> hashSet = new HashSet<>();
                Iterator<Schema> it = this.schemas.values().iterator();
                while (it.hasNext()) {
                    for (Table table2 : it.next().getAllTablesAndViews(null)) {
                        if (table != table2 && TableType.VIEW != table2.getTableType()) {
                            hashSet.clear();
                            table2.addDependencies(hashSet);
                            if (hashSet.contains(schemaObject)) {
                                return table2;
                            }
                        }
                    }
                }
                return null;
        }
    }

    public void removeSchemaObject(SessionLocal sessionLocal, SchemaObject schemaObject) {
        int type = schemaObject.getType();
        if (type == 0) {
            Table table = (Table) schemaObject;
            if (table.isTemporary() && !table.isGlobalTemporary()) {
                sessionLocal.removeLocalTempTable(table);
                return;
            }
        } else if (type == 1) {
            Index index = (Index) schemaObject;
            Table table2 = index.getTable();
            if (table2.isTemporary() && !table2.isGlobalTemporary()) {
                sessionLocal.removeLocalTempTableIndex(index);
                return;
            }
        } else if (type == 5) {
            Constraint constraint = (Constraint) schemaObject;
            if (constraint.getConstraintType() != Constraint.Type.DOMAIN) {
                Table table3 = constraint.getTable();
                if (table3.isTemporary() && !table3.isGlobalTemporary()) {
                    sessionLocal.removeLocalTempTableConstraint(constraint);
                    return;
                }
            }
        }
        checkWritingAllowed();
        lockMeta(sessionLocal);
        synchronized (this) {
            Comment findComment = findComment(schemaObject);
            if (findComment != null) {
                removeDatabaseObject(sessionLocal, findComment);
            }
            schemaObject.getSchema().remove(schemaObject);
            int id = schemaObject.getId();
            if (!this.starting) {
                Table dependentTable = getDependentTable(schemaObject, null);
                if (dependentTable != null) {
                    schemaObject.getSchema().add(schemaObject);
                    throw DbException.get(ErrorCode.CANNOT_DROP_2, schemaObject.getTraceSQL(), dependentTable.getTraceSQL());
                }
                schemaObject.removeChildrenAndResources(sessionLocal);
            }
            removeMeta(sessionLocal, id);
        }
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    public TraceSystem getTraceSystem() {
        return this.traceSystem;
    }

    public synchronized void setCacheSize(int i) {
        if (this.starting) {
            i = Math.min(i, MathUtils.convertLongToInt(Utils.getMemoryMax()) / 2);
        }
        this.store.setCacheSize(Math.max(1, i));
    }

    public synchronized void setMasterUser(User user) {
        lockMeta(this.systemSession);
        addDatabaseObject(this.systemSession, user);
        this.systemSession.commit(true);
    }

    public Role getPublicRole() {
        return this.publicRole;
    }

    public String getTempTableName(String str, SessionLocal sessionLocal) {
        String str2;
        if (str.length() > 227) {
            str = str.substring(0, 227);
        }
        do {
            str2 = str + "_COPY_" + sessionLocal.getId() + '_' + this.nextTempTableId.getAndIncrement();
        } while (this.mainSchema.findTableOrView(sessionLocal, str2) != null);
        return str2;
    }

    public void setCompareMode(CompareMode compareMode) {
        this.compareMode = compareMode;
    }

    public void setCluster(String str) {
        this.cluster = str;
    }

    @Override // org.h2.store.DataHandler
    public void checkWritingAllowed() {
        if (this.readOnly) {
            throw DbException.get(ErrorCode.DATABASE_IS_READ_ONLY);
        }
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public int getWriteDelay() {
        return this.store.getMvStore().getAutoCommitDelay();
    }

    public void setWriteDelay(int i) {
        this.store.getMvStore().setAutoCommitDelay(i < 0 ? 0 : i);
    }

    public int getRetentionTime() {
        return this.store.getMvStore().getRetentionTime();
    }

    public void setRetentionTime(int i) {
        this.store.getMvStore().setRetentionTime(i);
    }

    public void setAllowBuiltinAliasOverride(boolean z) {
        this.allowBuiltinAliasOverride = z;
    }

    public boolean isAllowBuiltinAliasOverride() {
        return this.allowBuiltinAliasOverride;
    }

    public ArrayList<InDoubtTransaction> getInDoubtTransactions() {
        return this.store.getInDoubtTransactions();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void prepareCommit(SessionLocal sessionLocal, String str) {
        if (!this.readOnly) {
            this.store.prepareCommit(sessionLocal, str);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void throwLastBackgroundException() {
        DbException andSet = this.backgroundException.getAndSet(null);
        if (andSet != null) {
            throw DbException.get(andSet.getErrorCode(), andSet, andSet.getMessage());
        }
    }

    public void setBackgroundException(DbException dbException) {
        TraceSystem traceSystem;
        if (this.backgroundException.compareAndSet(null, dbException) && (traceSystem = getTraceSystem()) != null) {
            traceSystem.getTrace(2).error(dbException, "flush");
        }
    }

    public Throwable getBackgroundException() {
        MVStoreException panicException = this.store.getMvStore().getPanicException();
        if (panicException != null) {
            return panicException;
        }
        return this.backgroundException.getAndSet(null);
    }

    public synchronized void flush() {
        if (!this.readOnly) {
            try {
                this.store.flush();
            } catch (RuntimeException e) {
                this.backgroundException.compareAndSet(null, DbException.convert(e));
                throw e;
            }
        }
    }

    public void setEventListener(DatabaseEventListener databaseEventListener) {
        this.eventListener = databaseEventListener;
    }

    public void setEventListenerClass(String str) {
        if (str == null || str.isEmpty()) {
            this.eventListener = null;
            return;
        }
        try {
            this.eventListener = (DatabaseEventListener) JdbcUtils.loadUserClass(str).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            String str2 = this.databaseURL;
            if (this.cipher != null) {
                str2 = str2 + ";CIPHER=" + this.cipher;
            }
            this.eventListener.init(str2);
        } catch (Throwable th) {
            throw DbException.get(ErrorCode.ERROR_SETTING_DATABASE_EVENT_LISTENER_2, th, str, th.toString());
        }
    }

    public void setProgress(int i, String str, long j, long j2) {
        if (this.eventListener != null) {
            try {
                this.eventListener.setProgress(i, str, j, j2);
            } catch (Exception e) {
            }
        }
    }

    public void exceptionThrown(SQLException sQLException, String str) {
        if (this.eventListener != null) {
            try {
                this.eventListener.exceptionThrown(sQLException, str);
            } catch (Exception e) {
            }
        }
    }

    public synchronized void sync() {
        if (this.readOnly) {
            return;
        }
        this.store.sync();
    }

    public int getMaxMemoryRows() {
        return this.maxMemoryRows;
    }

    public void setMaxMemoryRows(int i) {
        this.maxMemoryRows = i;
    }

    public void setLockMode(int i) {
        switch (i) {
            case 0:
            case 3:
                break;
            case 1:
            case 2:
                i = 3;
                break;
            default:
                throw DbException.getInvalidValueException("lock mode", Integer.valueOf(i));
        }
        this.lockMode = i;
    }

    public int getLockMode() {
        return this.lockMode;
    }

    public void setCloseDelay(int i) {
        this.closeDelay = i;
    }

    public SessionLocal getSystemSession() {
        return this.systemSession;
    }

    public boolean isClosing() {
        return this.closing;
    }

    public void setMaxLengthInplaceLob(int i) {
        this.maxLengthInplaceLob = i;
    }

    @Override // org.h2.store.DataHandler
    public int getMaxLengthInplaceLob() {
        return this.maxLengthInplaceLob;
    }

    public void setIgnoreCase(boolean z) {
        this.ignoreCase = z;
    }

    public boolean getIgnoreCase() {
        if (this.starting) {
            return false;
        }
        return this.ignoreCase;
    }

    public void setIgnoreCatalogs(boolean z) {
        this.ignoreCatalogs = z;
    }

    public boolean getIgnoreCatalogs() {
        return this.ignoreCatalogs;
    }

    public synchronized void setDeleteFilesOnDisconnect(boolean z) {
        this.deleteFilesOnDisconnect = z;
    }

    public void setAllowLiterals(int i) {
        this.allowLiterals = i;
    }

    public boolean getOptimizeReuseResults() {
        return this.optimizeReuseResults;
    }

    public void setOptimizeReuseResults(boolean z) {
        this.optimizeReuseResults = z;
    }

    @Override // org.h2.store.DataHandler
    public Object getLobSyncObject() {
        return this.lobSyncObject;
    }

    public int getSessionCount() {
        return this.userSessions.size();
    }

    public void setReferentialIntegrity(boolean z) {
        this.referentialIntegrity = z;
    }

    public boolean getReferentialIntegrity() {
        return this.referentialIntegrity;
    }

    public void setQueryStatistics(boolean z) {
        this.queryStatistics = z;
        synchronized (this) {
            if (!z) {
                this.queryStatisticsData = null;
            }
        }
    }

    public boolean getQueryStatistics() {
        return this.queryStatistics;
    }

    public void setQueryStatisticsMaxEntries(int i) {
        this.queryStatisticsMaxEntries = i;
        if (this.queryStatisticsData != null) {
            synchronized (this) {
                if (this.queryStatisticsData != null) {
                    this.queryStatisticsData.setMaxQueryEntries(this.queryStatisticsMaxEntries);
                }
            }
        }
    }

    public QueryStatisticsData getQueryStatisticsData() {
        if (!this.queryStatistics) {
            return null;
        }
        if (this.queryStatisticsData == null) {
            synchronized (this) {
                if (this.queryStatisticsData == null) {
                    this.queryStatisticsData = new QueryStatisticsData(this.queryStatisticsMaxEntries);
                }
            }
        }
        return this.queryStatisticsData;
    }

    public boolean isStarting() {
        return this.starting;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void opened() {
        if (this.eventListener != null) {
            this.eventListener.opened();
        }
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        getNextRemoteSettingsId();
    }

    @Override // org.h2.engine.CastDataProvider
    public Mode getMode() {
        return this.mode;
    }

    public void setDefaultNullOrdering(DefaultNullOrdering defaultNullOrdering) {
        this.defaultNullOrdering = defaultNullOrdering;
    }

    public DefaultNullOrdering getDefaultNullOrdering() {
        return this.defaultNullOrdering;
    }

    public void setMaxOperationMemory(int i) {
        this.maxOperationMemory = i;
    }

    public int getMaxOperationMemory() {
        return this.maxOperationMemory;
    }

    public SessionLocal getExclusiveSession() {
        return this.exclusiveSession.get();
    }

    public boolean setExclusiveSession(SessionLocal sessionLocal, boolean z) {
        if (this.exclusiveSession.get() != sessionLocal && !this.exclusiveSession.compareAndSet(null, sessionLocal)) {
            return false;
        }
        if (z) {
            closeAllSessionsExcept(sessionLocal);
            return true;
        }
        return true;
    }

    public boolean unsetExclusiveSession(SessionLocal sessionLocal) {
        return this.exclusiveSession.get() == null || this.exclusiveSession.compareAndSet(sessionLocal, null);
    }

    @Override // org.h2.store.DataHandler
    public SmallLRUCache<String, String[]> getLobFileListCache() {
        if (this.lobFileListCache == null) {
            this.lobFileListCache = SmallLRUCache.newInstance(128);
        }
        return this.lobFileListCache;
    }

    public boolean isSysTableLocked() {
        return this.meta == null || this.meta.isLockedExclusively();
    }

    public boolean isSysTableLockedBy(SessionLocal sessionLocal) {
        return this.meta == null || this.meta.isLockedExclusivelyBy(sessionLocal);
    }

    public TableLinkConnection getLinkConnection(String str, String str2, String str3, String str4) {
        if (this.linkConnections == null) {
            this.linkConnections = new HashMap<>();
        }
        return TableLinkConnection.open(this.linkConnections, str, str2, str3, str4, this.dbSettings.shareLinkedConnections);
    }

    public String toString() {
        return this.databaseShortName + ":" + super.toString();
    }

    public void shutdownImmediately() {
        this.closing = true;
        setPowerOffCount(1);
        try {
            checkPowerOff();
        } catch (DbException e) {
        }
        closeFiles();
        this.powerOffCount = 0;
    }

    @Override // org.h2.store.DataHandler
    public TempFileDeleter getTempFileDeleter() {
        return this.tempFileDeleter;
    }

    public Table getFirstUserTable() {
        for (Schema schema : this.schemas.values()) {
            for (Table table : schema.getAllTablesAndViews(null)) {
                if (table.getCreateSQL() != null && !table.isHidden() && (schema.getId() != -1 || !table.getName().equalsIgnoreCase("LOB_BLOCKS"))) {
                    return table;
                }
            }
        }
        return null;
    }

    public void checkpoint() {
        if (this.persistent) {
            this.store.flush();
        }
        getTempFileDeleter().deleteUnused();
    }

    public void setReadOnly(boolean z) {
        this.readOnly = z;
    }

    public void setCompactMode(int i) {
        this.compactMode = i;
    }

    public SourceCompiler getCompiler() {
        if (this.compiler == null) {
            this.compiler = new SourceCompiler();
        }
        return this.compiler;
    }

    @Override // org.h2.store.DataHandler
    public LobStorageInterface getLobStorage() {
        return this.lobStorage;
    }

    public SessionLocal getLobSession() {
        return this.lobSession;
    }

    public int getDefaultTableType() {
        return this.defaultTableType;
    }

    public void setDefaultTableType(int i) {
        this.defaultTableType = i;
    }

    public DbSettings getSettings() {
        return this.dbSettings;
    }

    public <V> HashMap<String, V> newStringMap() {
        return this.dbSettings.caseInsensitiveIdentifiers ? new CaseInsensitiveMap() : new HashMap<>();
    }

    public <V> HashMap<String, V> newStringMap(int i) {
        return this.dbSettings.caseInsensitiveIdentifiers ? new CaseInsensitiveMap(i) : new HashMap<>(i);
    }

    public <V> ConcurrentHashMap<String, V> newConcurrentStringMap() {
        return this.dbSettings.caseInsensitiveIdentifiers ? new CaseInsensitiveConcurrentMap() : new ConcurrentHashMap<>();
    }

    public boolean equalsIdentifiers(String str, String str2) {
        return str.equals(str2) || (this.dbSettings.caseInsensitiveIdentifiers && str.equalsIgnoreCase(str2));
    }

    public String sysIdentifier(String str) {
        if ($assertionsDisabled || isUpperSysIdentifier(str)) {
            return this.dbSettings.databaseToLower ? StringUtils.toLowerEnglish(str) : str;
        }
        throw new AssertionError();
    }

    private static boolean isUpperSysIdentifier(String str) {
        char charAt;
        int length = str.length();
        if (length == 0 || (charAt = str.charAt(0)) < 'A' || charAt > 'Z') {
            return false;
        }
        int i = length - 1;
        for (int i2 = 1; i2 < i; i2++) {
            char charAt2 = str.charAt(i2);
            if ((charAt2 < 'A' || charAt2 > 'Z') && charAt2 != '_') {
                return false;
            }
        }
        if (i > 0) {
            char charAt3 = str.charAt(i);
            if (charAt3 < 'A' || charAt3 > 'Z') {
                return false;
            }
            return true;
        }
        return true;
    }

    @Override // org.h2.store.DataHandler
    public int readLob(long j, byte[] bArr, long j2, byte[] bArr2, int i, int i2) {
        throw DbException.getInternalError();
    }

    public int getPageSize() {
        return this.pageSize;
    }

    @Override // org.h2.engine.CastDataProvider
    public JavaObjectSerializer getJavaObjectSerializer() {
        initJavaObjectSerializer();
        return this.javaObjectSerializer;
    }

    private void initJavaObjectSerializer() {
        if (this.javaObjectSerializerInitialized) {
            return;
        }
        synchronized (this) {
            if (this.javaObjectSerializerInitialized) {
                return;
            }
            String str = this.javaObjectSerializerName;
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
            this.javaObjectSerializerInitialized = true;
        }
    }

    public void setJavaObjectSerializerName(String str) {
        synchronized (this) {
            this.javaObjectSerializerInitialized = false;
            this.javaObjectSerializerName = str;
            getNextRemoteSettingsId();
        }
    }

    public TableEngine getTableEngine(String str) {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        TableEngine tableEngine = this.tableEngines.get(str);
        if (tableEngine == null) {
            try {
                tableEngine = (TableEngine) JdbcUtils.loadUserClass(str).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                this.tableEngines.put(str, tableEngine);
            } catch (Exception e) {
                throw DbException.convert(e);
            }
        }
        return tableEngine;
    }

    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    public void setAuthenticator(Authenticator authenticator) {
        if (authenticator != null) {
            authenticator.init(this);
        }
        this.authenticator = authenticator;
    }

    @Override // org.h2.engine.CastDataProvider
    public ValueTimestampTimeZone currentTimestamp() {
        Session threadLocalSession = SessionLocal.getThreadLocalSession();
        if (threadLocalSession != null) {
            return threadLocalSession.currentTimestamp();
        }
        throw DbException.getUnsupportedException("Unsafe comparison or cast");
    }

    @Override // org.h2.engine.CastDataProvider
    public TimeZoneProvider currentTimeZone() {
        Session threadLocalSession = SessionLocal.getThreadLocalSession();
        if (threadLocalSession != null) {
            return threadLocalSession.currentTimeZone();
        }
        throw DbException.getUnsupportedException("Unsafe comparison or cast");
    }

    @Override // org.h2.engine.CastDataProvider
    public boolean zeroBasedEnums() {
        return this.dbSettings.zeroBasedEnums;
    }
}
