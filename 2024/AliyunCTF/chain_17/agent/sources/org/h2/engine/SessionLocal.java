package org.h2.engine;

import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.h2.api.ErrorCode;
import org.h2.api.JavaObjectSerializer;
import org.h2.command.Command;
import org.h2.command.CommandInterface;
import org.h2.command.Parser;
import org.h2.command.ParserBase;
import org.h2.command.Prepared;
import org.h2.command.ddl.Analyze;
import org.h2.command.query.Query;
import org.h2.constraint.Constraint;
import org.h2.engine.Session;
import org.h2.index.Index;
import org.h2.index.QueryExpressionIndex;
import org.h2.jdbc.JdbcConnection;
import org.h2.jdbc.meta.DatabaseMeta;
import org.h2.jdbc.meta.DatabaseMetaLocal;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.message.TraceSystem;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.db.MVIndex;
import org.h2.mvstore.db.MVTable;
import org.h2.mvstore.db.Store;
import org.h2.mvstore.tx.Transaction;
import org.h2.mvstore.tx.TransactionStore;
import org.h2.result.Row;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;
import org.h2.store.DataHandler;
import org.h2.store.InDoubtTransaction;
import org.h2.table.Table;
import org.h2.util.DateTimeUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.SmallLRUCache;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;
import org.h2.value.CompareMode;
import org.h2.value.Value;
import org.h2.value.ValueLob;
import org.h2.value.ValueNull;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueVarchar;
import org.h2.value.VersionedValue;
import org.h2.value.lob.LobData;
import org.h2.value.lob.LobDataDatabase;
import org.h2.value.lob.LobDataInMemory;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/SessionLocal.class */
public final class SessionLocal extends Session implements TransactionStore.RollbackListener {
    private static final String SYSTEM_IDENTIFIER_PREFIX = "_";
    private static int nextSerialId;
    private static final ThreadLocal<Session> THREAD_LOCAL_SESSION = new ThreadLocal<>();
    private final int serialId;
    private Database database;
    private User user;
    private final int id;
    private NetworkConnectionInfo networkConnectionInfo;
    private final ArrayList<Table> locks;
    private boolean autoCommit;
    private Random random;
    private int lockTimeout;
    private HashMap<SequenceAndPrepared, RowNumberAndValue> nextValueFor;
    private WeakHashMap<Sequence, Value> currentValueFor;
    private Value lastIdentity;
    private HashMap<String, Savepoint> savepoints;
    private HashMap<String, Table> localTempTables;
    private HashMap<String, Index> localTempTableIndexes;
    private HashMap<String, Constraint> localTempTableConstraints;
    private int throttleMs;
    private long lastThrottleNs;
    private Command currentCommand;
    private boolean allowLiterals;
    private String currentSchemaName;
    private String[] schemaSearchPath;
    private Trace trace;
    private HashMap<String, ValueLob> removeLobMap;
    private int systemIdentifier;
    private HashMap<String, Procedure> procedures;
    private boolean autoCommitAtTransactionEnd;
    private String currentTransactionName;
    private volatile long cancelAtNs;
    private final ValueTimestampTimeZone sessionStart;
    private Instant commandStartOrEnd;
    private ValueTimestampTimeZone currentTimestamp;
    private HashMap<String, Value> variables;
    private int queryTimeout;
    private boolean commitOrRollbackDisabled;
    private Table waitForLock;
    private Thread waitForLockThread;
    private int modificationId;
    private int objectId;
    private final int queryCacheSize;
    private SmallLRUCache<String, Command> queryCache;
    private long modificationMetaID;
    private int createViewLevel;
    private volatile SmallLRUCache<Object, QueryExpressionIndex> viewIndexCache;
    private HashMap<Object, QueryExpressionIndex> derivedTableIndexCache;
    private boolean lazyQueryExecution;
    private BitSet nonKeywords;
    private TimeZoneProvider timeZone;
    private HashSet<Table> tablesToAnalyze;
    private LinkedList<TimeoutValue> temporaryResultLobs;
    private ArrayList<ValueLob> temporaryLobs;
    private Transaction transaction;
    private final AtomicReference<State> state;
    private long startStatement;
    private IsolationLevel isolationLevel;
    private long snapshotDataModificationId;
    private BitSet idsToRelease;
    private boolean truncateLargeLength;
    private boolean variableBinary;
    private boolean oldInformationSchema;
    private boolean quirksMode;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/SessionLocal$Savepoint.class */
    public static class Savepoint {
        int logIndex;
        long transactionSavepoint;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/SessionLocal$State.class */
    public enum State {
        INIT,
        RUNNING,
        BLOCKED,
        SLEEP,
        THROTTLED,
        SUSPENDED,
        CLOSED
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/SessionLocal$SequenceAndPrepared.class */
    public static final class SequenceAndPrepared {
        private final Sequence sequence;
        private final Prepared prepared;

        SequenceAndPrepared(Sequence sequence, Prepared prepared) {
            this.sequence = sequence;
            this.prepared = prepared;
        }

        public int hashCode() {
            return (31 * (31 + this.prepared.hashCode())) + this.sequence.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != SequenceAndPrepared.class) {
                return false;
            }
            SequenceAndPrepared sequenceAndPrepared = (SequenceAndPrepared) obj;
            return this.sequence == sequenceAndPrepared.sequence && this.prepared == sequenceAndPrepared.prepared;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/SessionLocal$RowNumberAndValue.class */
    public static final class RowNumberAndValue {
        long rowNumber;
        Value nextValue;

        RowNumberAndValue(long j, Value value) {
            this.rowNumber = j;
            this.nextValue = value;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Session getThreadLocalSession() {
        Session session = THREAD_LOCAL_SESSION.get();
        if (session == null) {
            THREAD_LOCAL_SESSION.remove();
        }
        return session;
    }

    public SessionLocal(Database database, User user, int i) {
        int i2 = nextSerialId;
        nextSerialId = i2 + 1;
        this.serialId = i2;
        this.locks = Utils.newSmallArrayList();
        this.autoCommit = true;
        this.lastIdentity = ValueNull.INSTANCE;
        this.modificationMetaID = -1L;
        this.state = new AtomicReference<>(State.INIT);
        this.startStatement = -1L;
        this.isolationLevel = IsolationLevel.READ_COMMITTED;
        this.database = database;
        this.queryTimeout = database.getSettings().maxQueryTimeout;
        this.queryCacheSize = database.getSettings().queryCacheSize;
        this.user = user;
        this.id = i;
        this.lockTimeout = database.getLockTimeout();
        Schema mainSchema = database.getMainSchema();
        this.currentSchemaName = mainSchema != null ? mainSchema.getName() : database.sysIdentifier("PUBLIC");
        this.timeZone = DateTimeUtils.getTimeZone();
        TimeZoneProvider timeZoneProvider = this.timeZone;
        Instant now = Instant.now();
        this.commandStartOrEnd = now;
        this.sessionStart = DateTimeUtils.currentTimestamp(timeZoneProvider, now);
    }

    public void setLazyQueryExecution(boolean z) {
        this.lazyQueryExecution = z;
    }

    public boolean isLazyQueryExecution() {
        return this.lazyQueryExecution;
    }

    public void setParsingCreateView(boolean z) {
        this.createViewLevel += z ? 1 : -1;
    }

    public boolean isParsingCreateView() {
        return this.createViewLevel != 0;
    }

    @Override // org.h2.engine.Session
    public ArrayList<String> getClusterServers() {
        return new ArrayList<>();
    }

    public boolean setCommitOrRollbackDisabled(boolean z) {
        boolean z2 = this.commitOrRollbackDisabled;
        this.commitOrRollbackDisabled = z;
        return z2;
    }

    private void initVariables() {
        if (this.variables == null) {
            this.variables = newStringsMap();
        }
    }

    public void setVariable(String str, Value value) {
        Value put;
        initVariables();
        this.modificationId++;
        if (value == ValueNull.INSTANCE) {
            put = this.variables.remove(str);
        } else {
            if (value instanceof ValueLob) {
                value = ((ValueLob) value).copy(getDatabase(), -1);
            }
            put = this.variables.put(str, value);
        }
        if (put instanceof ValueLob) {
            ((ValueLob) put).remove();
        }
    }

    public Value getVariable(String str) {
        initVariables();
        Value value = this.variables.get(str);
        return value == null ? ValueNull.INSTANCE : value;
    }

    public String[] getVariableNames() {
        if (this.variables == null) {
            return new String[0];
        }
        return (String[]) this.variables.keySet().toArray(new String[0]);
    }

    public Table findLocalTempTable(String str) {
        if (this.localTempTables == null) {
            return null;
        }
        return this.localTempTables.get(str);
    }

    public List<Table> getLocalTempTables() {
        if (this.localTempTables == null) {
            return Collections.emptyList();
        }
        return new ArrayList(this.localTempTables.values());
    }

    public void addLocalTempTable(Table table) {
        if (this.localTempTables == null) {
            this.localTempTables = newStringsMap();
        }
        if (this.localTempTables.putIfAbsent(table.getName(), table) != null) {
            StringBuilder sb = new StringBuilder();
            table.getSQL(sb, 3).append(" AS ");
            ParserBase.quoteIdentifier(table.getName(), 3);
            throw DbException.get(ErrorCode.TABLE_OR_VIEW_ALREADY_EXISTS_1, sb.toString());
        }
        this.modificationId++;
    }

    public void removeLocalTempTable(Table table) {
        if (this.localTempTables != null && this.localTempTables.remove(table.getName()) != null) {
            this.modificationId++;
            Database database = this.database;
            if (database != null) {
                synchronized (database) {
                    table.removeChildrenAndResources(this);
                }
            }
        }
    }

    public Index findLocalTempTableIndex(String str) {
        if (this.localTempTableIndexes == null) {
            return null;
        }
        return this.localTempTableIndexes.get(str);
    }

    public HashMap<String, Index> getLocalTempTableIndexes() {
        if (this.localTempTableIndexes == null) {
            return new HashMap<>();
        }
        return this.localTempTableIndexes;
    }

    public void addLocalTempTableIndex(Index index) {
        if (this.localTempTableIndexes == null) {
            this.localTempTableIndexes = newStringsMap();
        }
        if (this.localTempTableIndexes.putIfAbsent(index.getName(), index) != null) {
            throw DbException.get(ErrorCode.INDEX_ALREADY_EXISTS_1, index.getTraceSQL());
        }
    }

    public void removeLocalTempTableIndex(Index index) {
        if (this.localTempTableIndexes != null) {
            this.localTempTableIndexes.remove(index.getName());
            synchronized (this.database) {
                index.removeChildrenAndResources(this);
            }
        }
    }

    public Constraint findLocalTempTableConstraint(String str) {
        if (this.localTempTableConstraints == null) {
            return null;
        }
        return this.localTempTableConstraints.get(str);
    }

    public HashMap<String, Constraint> getLocalTempTableConstraints() {
        if (this.localTempTableConstraints == null) {
            return new HashMap<>();
        }
        return this.localTempTableConstraints;
    }

    public void addLocalTempTableConstraint(Constraint constraint) {
        if (this.localTempTableConstraints == null) {
            this.localTempTableConstraints = newStringsMap();
        }
        if (this.localTempTableConstraints.putIfAbsent(constraint.getName(), constraint) != null) {
            throw DbException.get(ErrorCode.CONSTRAINT_ALREADY_EXISTS_1, constraint.getTraceSQL());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeLocalTempTableConstraint(Constraint constraint) {
        if (this.localTempTableConstraints != null) {
            this.localTempTableConstraints.remove(constraint.getName());
            synchronized (this.database) {
                constraint.removeChildrenAndResources(this);
            }
        }
    }

    @Override // org.h2.engine.Session
    public boolean getAutoCommit() {
        return this.autoCommit;
    }

    public User getUser() {
        return this.user;
    }

    @Override // org.h2.engine.Session
    public void setAutoCommit(boolean z) {
        this.autoCommit = z;
    }

    public int getLockTimeout() {
        return this.lockTimeout;
    }

    public void setLockTimeout(int i) {
        this.lockTimeout = i;
        if (hasTransaction()) {
            this.transaction.setTimeoutMillis(i);
        }
    }

    @Override // org.h2.engine.Session
    public CommandInterface prepareCommand(String str, int i) {
        lock();
        try {
            Command prepareLocal = prepareLocal(str);
            unlock();
            return prepareLocal;
        } catch (Throwable th) {
            unlock();
            throw th;
        }
    }

    public Prepared prepare(String str) {
        return prepare(str, false, false);
    }

    public Prepared prepare(String str, boolean z, boolean z2) {
        Parser parser = new Parser(this);
        parser.setRightsChecked(z);
        parser.setLiteralsChecked(z2);
        return parser.prepare(str);
    }

    public Query prepareQueryExpression(String str) {
        Parser parser = new Parser(this);
        parser.setRightsChecked(true);
        parser.setLiteralsChecked(true);
        return parser.prepareQueryExpression(str);
    }

    public Command prepareLocal(String str) {
        if (isClosed()) {
            throw DbException.get(ErrorCode.CONNECTION_BROKEN_1, "session closed");
        }
        if (this.queryCacheSize > 0) {
            if (this.queryCache == null) {
                this.queryCache = SmallLRUCache.newInstance(this.queryCacheSize);
                this.modificationMetaID = getDatabase().getModificationMetaId();
            } else {
                long modificationMetaId = getDatabase().getModificationMetaId();
                if (modificationMetaId != this.modificationMetaID) {
                    this.queryCache.clear();
                    this.modificationMetaID = modificationMetaId;
                }
                Command command = this.queryCache.get(str);
                if (command != null && command.canReuse()) {
                    command.reuse();
                    return command;
                }
            }
        }
        try {
            Command prepareCommand = new Parser(this).prepareCommand(str);
            this.derivedTableIndexCache = null;
            if (this.queryCache != null && prepareCommand.isCacheable()) {
                this.queryCache.put(str, prepareCommand);
            }
            return prepareCommand;
        } catch (Throwable th) {
            this.derivedTableIndexCache = null;
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void scheduleDatabaseObjectIdForRelease(int i) {
        if (this.idsToRelease == null) {
            this.idsToRelease = new BitSet();
        }
        this.idsToRelease.set(i);
    }

    public Database getDatabase() {
        if (this.database == null) {
            throw DbException.get(ErrorCode.DATABASE_IS_CLOSED);
        }
        return this.database;
    }

    public void commit(boolean z) {
        beforeCommitOrRollback();
        if (hasTransaction()) {
            try {
                markUsedTablesAsUpdated();
                this.transaction.commit();
                removeTemporaryLobs(true);
                endTransaction();
                if (!z) {
                    cleanTempTables(false);
                    if (this.autoCommitAtTransactionEnd) {
                        this.autoCommit = true;
                        this.autoCommitAtTransactionEnd = false;
                    }
                }
                analyzeTables();
            } finally {
                this.transaction = null;
            }
        }
    }

    private void markUsedTablesAsUpdated() {
        if (!this.locks.isEmpty()) {
            Iterator<Table> it = this.locks.iterator();
            while (it.hasNext()) {
                Table next = it.next();
                if (next instanceof MVTable) {
                    ((MVTable) next).commit();
                }
            }
        }
    }

    private void analyzeTables() {
        if (this.tablesToAnalyze != null && isLockedByCurrentThread()) {
            HashSet<Table> hashSet = this.tablesToAnalyze;
            this.tablesToAnalyze = null;
            int i = getDatabase().getSettings().analyzeSample / 10;
            Iterator<Table> it = hashSet.iterator();
            while (it.hasNext()) {
                Analyze.analyzeTable(this, it.next(), i, false);
            }
            getDatabase().unlockMeta(this);
            commit(true);
        }
    }

    private void removeTemporaryLobs(boolean z) {
        if (this.temporaryLobs != null) {
            Iterator<ValueLob> it = this.temporaryLobs.iterator();
            while (it.hasNext()) {
                ValueLob next = it.next();
                if (!next.isLinkedToTable()) {
                    next.remove();
                }
            }
            this.temporaryLobs.clear();
        }
        if (this.temporaryResultLobs != null && !this.temporaryResultLobs.isEmpty()) {
            long nanoTime = System.nanoTime() - (getDatabase().getSettings().lobTimeout * 1000000);
            while (!this.temporaryResultLobs.isEmpty()) {
                TimeoutValue first = this.temporaryResultLobs.getFirst();
                if (!z || first.created - nanoTime < 0) {
                    ValueLob valueLob = this.temporaryResultLobs.removeFirst().value;
                    if (!valueLob.isLinkedToTable()) {
                        valueLob.remove();
                    }
                } else {
                    return;
                }
            }
        }
    }

    private void beforeCommitOrRollback() {
        if (this.commitOrRollbackDisabled && !this.locks.isEmpty()) {
            throw DbException.get(ErrorCode.COMMIT_ROLLBACK_NOT_ALLOWED);
        }
        this.currentTransactionName = null;
        this.currentTimestamp = null;
        getDatabase().throwLastBackgroundException();
    }

    private void endTransaction() {
        if (this.removeLobMap != null && !this.removeLobMap.isEmpty()) {
            Iterator<ValueLob> it = this.removeLobMap.values().iterator();
            while (it.hasNext()) {
                it.next().remove();
            }
            this.removeLobMap = null;
        }
        unlockAll();
        if (this.idsToRelease != null) {
            getDatabase().releaseDatabaseObjectIds(this.idsToRelease);
            this.idsToRelease = null;
        }
        if (hasTransaction() && !this.transaction.allowNonRepeatableRead()) {
            this.snapshotDataModificationId = getDatabase().getNextModificationDataId();
        }
    }

    public long getSnapshotDataModificationId() {
        return this.snapshotDataModificationId;
    }

    public void rollback() {
        beforeCommitOrRollback();
        if (hasTransaction()) {
            rollbackTo(null);
        }
        this.idsToRelease = null;
        cleanTempTables(false);
        if (this.autoCommitAtTransactionEnd) {
            this.autoCommit = true;
            this.autoCommitAtTransactionEnd = false;
        }
        endTransaction();
    }

    public void rollbackTo(Savepoint savepoint) {
        int i = savepoint == null ? 0 : savepoint.logIndex;
        if (hasTransaction()) {
            markUsedTablesAsUpdated();
            if (savepoint == null) {
                this.transaction.rollback();
                this.transaction = null;
            } else {
                this.transaction.rollbackToSavepoint(savepoint.transactionSavepoint);
            }
        }
        if (this.savepoints != null) {
            for (String str : (String[]) this.savepoints.keySet().toArray(new String[0])) {
                if (this.savepoints.get(str).logIndex > i) {
                    this.savepoints.remove(str);
                }
            }
        }
        if (this.queryCache != null) {
            this.queryCache.clear();
        }
    }

    @Override // org.h2.engine.Session
    public boolean hasPendingTransaction() {
        return hasTransaction() && this.transaction.hasChanges() && this.transaction.getStatus() != 2;
    }

    public Savepoint setSavepoint() {
        Savepoint savepoint = new Savepoint();
        savepoint.transactionSavepoint = getStatementSavepoint();
        return savepoint;
    }

    public int getId() {
        return this.id;
    }

    @Override // org.h2.engine.Session
    public void cancel() {
        this.cancelAtNs = Utils.currentNanoTime();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void suspend() {
        cancel();
        if (transitionToState(State.SUSPENDED, false) == State.SLEEP) {
            close();
        }
    }

    @Override // org.h2.engine.Session, java.lang.AutoCloseable
    public void close() {
        if (this.state.getAndSet(State.CLOSED) != State.CLOSED) {
            try {
                if (this.queryCache != null) {
                    this.queryCache.clear();
                }
                this.database.throwLastBackgroundException();
                this.database.checkPowerOff();
                if (hasPreparedTransaction()) {
                    this.removeLobMap = null;
                    endTransaction();
                } else {
                    rollback();
                    removeTemporaryLobs(false);
                    cleanTempTables(true);
                    commit(true);
                }
                this.database.unlockMeta(this);
            } finally {
                this.database.removeSession(this);
                this.database = null;
                this.user = null;
            }
        }
    }

    public void registerTableAsLocked(Table table) {
        if (SysProperties.CHECK && this.locks.contains(table)) {
            throw DbException.getInternalError(table.toString());
        }
        this.locks.add(table);
    }

    public void registerTableAsUpdated(Table table) {
        if (!this.locks.contains(table)) {
            this.locks.add(table);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unlock(Table table) {
        this.locks.remove(table);
    }

    private boolean hasTransaction() {
        return this.transaction != null;
    }

    private void unlockAll() {
        if (!this.locks.isEmpty()) {
            for (Table table : (Table[]) this.locks.toArray(new Table[0])) {
                if (table != null) {
                    table.unlock(this);
                }
            }
            this.locks.clear();
        }
        Database.unlockMetaDebug(this);
        this.savepoints = null;
        this.sessionStateChanged = true;
    }

    private void cleanTempTables(boolean z) {
        if (this.localTempTables != null && !this.localTempTables.isEmpty()) {
            Iterator<Table> it = this.localTempTables.values().iterator();
            while (it.hasNext()) {
                Table next = it.next();
                if (z || next.getOnCommitDrop()) {
                    this.modificationId++;
                    next.setModified();
                    it.remove();
                    this.database.lockMeta(this);
                    next.removeChildrenAndResources(this);
                    if (z) {
                        this.database.throwLastBackgroundException();
                    }
                } else if (next.getOnCommitTruncate()) {
                    next.truncate(this);
                }
            }
        }
    }

    public Random getRandom() {
        if (this.random == null) {
            this.random = new Random();
        }
        return this.random;
    }

    @Override // org.h2.engine.Session
    public Trace getTrace() {
        if (this.trace != null && !isClosed()) {
            return this.trace;
        }
        String str = "jdbc[" + this.id + "]";
        Database database = this.database;
        if (isClosed() || database == null) {
            return new TraceSystem(null).getTrace(str);
        }
        this.trace = database.getTraceSystem().getTrace(str);
        return this.trace;
    }

    public Value getNextValueFor(Sequence sequence, Prepared prepared) {
        Value next;
        Mode mode = getMode();
        if (mode.nextValueReturnsDifferentValues || prepared == null) {
            next = sequence.getNext(this);
        } else {
            if (this.nextValueFor == null) {
                this.nextValueFor = new HashMap<>();
            }
            SequenceAndPrepared sequenceAndPrepared = new SequenceAndPrepared(sequence, prepared);
            RowNumberAndValue rowNumberAndValue = this.nextValueFor.get(sequenceAndPrepared);
            long currentRowNumber = prepared.getCurrentRowNumber();
            if (rowNumberAndValue == null) {
                next = sequence.getNext(this);
                this.nextValueFor.put(sequenceAndPrepared, new RowNumberAndValue(currentRowNumber, next));
            } else if (rowNumberAndValue.rowNumber == currentRowNumber) {
                next = rowNumberAndValue.nextValue;
            } else {
                Value next2 = sequence.getNext(this);
                next = next2;
                rowNumberAndValue.nextValue = next2;
                rowNumberAndValue.rowNumber = currentRowNumber;
            }
        }
        WeakHashMap<Sequence, Value> weakHashMap = this.currentValueFor;
        if (weakHashMap == null) {
            WeakHashMap<Sequence, Value> weakHashMap2 = new WeakHashMap<>();
            weakHashMap = weakHashMap2;
            this.currentValueFor = weakHashMap2;
        }
        weakHashMap.put(sequence, next);
        if (mode.takeGeneratedSequenceValue) {
            this.lastIdentity = next;
        }
        return next;
    }

    public Value getCurrentValueFor(Sequence sequence) {
        Value value;
        WeakHashMap<Sequence, Value> weakHashMap = this.currentValueFor;
        if (weakHashMap != null && (value = weakHashMap.get(sequence)) != null) {
            return value;
        }
        throw DbException.get(ErrorCode.CURRENT_SEQUENCE_VALUE_IS_NOT_DEFINED_IN_SESSION_1, sequence.getTraceSQL());
    }

    public void setLastIdentity(Value value) {
        this.lastIdentity = value;
    }

    public Value getLastIdentity() {
        return this.lastIdentity;
    }

    public boolean containsUncommitted() {
        return this.transaction != null && this.transaction.hasChanges();
    }

    public void addSavepoint(String str) {
        if (this.savepoints == null) {
            this.savepoints = newStringsMap();
        }
        this.savepoints.put(str, setSavepoint());
    }

    public void rollbackToSavepoint(String str) {
        Savepoint savepoint;
        beforeCommitOrRollback();
        if (this.savepoints == null || (savepoint = this.savepoints.get(str)) == null) {
            throw DbException.get(ErrorCode.SAVEPOINT_IS_INVALID_1, str);
        }
        rollbackTo(savepoint);
    }

    public void prepareCommit(String str) {
        if (hasPendingTransaction()) {
            getDatabase().prepareCommit(this, str);
        }
        this.currentTransactionName = str;
    }

    public boolean hasPreparedTransaction() {
        return this.currentTransactionName != null;
    }

    public void setPreparedTransaction(String str, boolean z) {
        if (hasPreparedTransaction() && this.currentTransactionName.equals(str)) {
            if (z) {
                commit(false);
                return;
            } else {
                rollback();
                return;
            }
        }
        ArrayList<InDoubtTransaction> inDoubtTransactions = getDatabase().getInDoubtTransactions();
        int i = z ? 1 : 2;
        boolean z2 = false;
        Iterator<InDoubtTransaction> it = inDoubtTransactions.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            InDoubtTransaction next = it.next();
            if (next.getTransactionName().equals(str)) {
                next.setState(i);
                z2 = true;
                break;
            }
        }
        if (!z2) {
            throw DbException.get(ErrorCode.TRANSACTION_NOT_FOUND_1, str);
        }
    }

    @Override // org.h2.engine.Session
    public boolean isClosed() {
        return this.state.get() == State.CLOSED;
    }

    public boolean isOpen() {
        State state = this.state.get();
        checkSuspended(state);
        return state != State.CLOSED;
    }

    public void setThrottle(int i) {
        this.throttleMs = i;
    }

    public void throttle() {
        if (this.throttleMs == 0) {
            return;
        }
        long nanoTime = System.nanoTime();
        if (this.lastThrottleNs != 0 && nanoTime - this.lastThrottleNs < 50000000) {
            return;
        }
        this.lastThrottleNs = Utils.nanoTimePlusMillis(nanoTime, this.throttleMs);
        State transitionToState = transitionToState(State.THROTTLED, false);
        try {
            Thread.sleep(this.throttleMs);
            transitionToState(transitionToState, false);
        } catch (InterruptedException e) {
            transitionToState(transitionToState, false);
        } catch (Throwable th) {
            transitionToState(transitionToState, false);
            throw th;
        }
    }

    private void setCurrentCommand(Command command) {
        transitionToState(command == null ? State.SLEEP : State.RUNNING, true);
        if (isOpen()) {
            this.currentCommand = command;
            this.commandStartOrEnd = Instant.now();
            if (command != null) {
                if (this.queryTimeout > 0) {
                    this.cancelAtNs = Utils.currentNanoTimePlusMillis(this.queryTimeout);
                }
            } else {
                if (this.currentTimestamp != null && !getMode().dateTimeValueWithinTransaction) {
                    this.currentTimestamp = null;
                }
                if (this.nextValueFor != null) {
                    this.nextValueFor.clear();
                }
            }
        }
    }

    private State transitionToState(State state, boolean z) {
        State state2;
        do {
            state2 = this.state.get();
            if (state2 == State.CLOSED || (z && !checkSuspended(state2))) {
                break;
            }
        } while (!this.state.compareAndSet(state2, state));
        return state2;
    }

    private boolean checkSuspended(State state) {
        if (state == State.SUSPENDED) {
            close();
            throw DbException.get(ErrorCode.DATABASE_IS_IN_EXCLUSIVE_MODE);
        }
        return true;
    }

    public void checkCanceled() {
        throttle();
        long j = this.cancelAtNs;
        if (j != 0 && System.nanoTime() - j >= 0) {
            this.cancelAtNs = 0L;
            throw DbException.get(ErrorCode.STATEMENT_WAS_CANCELED);
        }
    }

    public long getCancel() {
        return this.cancelAtNs;
    }

    public Command getCurrentCommand() {
        return this.currentCommand;
    }

    public ValueTimestampTimeZone getCommandStartOrEnd() {
        return DateTimeUtils.currentTimestamp(this.timeZone, this.commandStartOrEnd);
    }

    public boolean getAllowLiterals() {
        return this.allowLiterals;
    }

    public void setAllowLiterals(boolean z) {
        this.allowLiterals = z;
    }

    public void setCurrentSchema(Schema schema) {
        this.modificationId++;
        if (this.queryCache != null) {
            this.queryCache.clear();
        }
        this.currentSchemaName = schema.getName();
    }

    @Override // org.h2.engine.Session
    public String getCurrentSchemaName() {
        return this.currentSchemaName;
    }

    @Override // org.h2.engine.Session
    public void setCurrentSchemaName(String str) {
        setCurrentSchema(getDatabase().getSchema(str));
    }

    public JdbcConnection createConnection(boolean z) {
        String str;
        if (z) {
            str = Constants.CONN_URL_COLUMNLIST;
        } else {
            str = Constants.CONN_URL_INTERNAL;
        }
        return new JdbcConnection(this, getUser().getName(), str);
    }

    @Override // org.h2.engine.Session
    public DataHandler getDataHandler() {
        return getDatabase();
    }

    public void removeAtCommit(ValueLob valueLob) {
        if (valueLob.isLinkedToTable()) {
            if (this.removeLobMap == null) {
                this.removeLobMap = new HashMap<>();
            }
            this.removeLobMap.put(valueLob.toString(), valueLob);
        }
    }

    public void removeAtCommitStop(ValueLob valueLob) {
        if (valueLob.isLinkedToTable() && this.removeLobMap != null) {
            this.removeLobMap.remove(valueLob.toString());
        }
    }

    public String getNextSystemIdentifier(String str) {
        String sb;
        do {
            StringBuilder append = new StringBuilder().append("_");
            int i = this.systemIdentifier;
            this.systemIdentifier = i + 1;
            sb = append.append(i).toString();
        } while (str.contains(sb));
        return sb;
    }

    public void addProcedure(Procedure procedure) {
        if (this.procedures == null) {
            this.procedures = newStringsMap();
        }
        this.procedures.put(procedure.getName(), procedure);
    }

    public void removeProcedure(String str) {
        if (this.procedures != null) {
            this.procedures.remove(str);
        }
    }

    public Procedure getProcedure(String str) {
        if (this.procedures == null) {
            return null;
        }
        return this.procedures.get(str);
    }

    public void setSchemaSearchPath(String[] strArr) {
        this.modificationId++;
        this.schemaSearchPath = strArr;
    }

    public String[] getSchemaSearchPath() {
        return this.schemaSearchPath;
    }

    public int hashCode() {
        return this.serialId;
    }

    public String toString() {
        return "#" + this.serialId + " (user: " + (this.user == null ? "<null>" : this.user.getName()) + ", " + this.state.get() + ")";
    }

    public void begin() {
        this.autoCommitAtTransactionEnd = true;
        this.autoCommit = false;
    }

    public ValueTimestampTimeZone getSessionStart() {
        return this.sessionStart;
    }

    public Set<Table> getLocks() {
        if (getDatabase().getLockMode() == 0 || this.locks.isEmpty()) {
            return Collections.emptySet();
        }
        Object[] array = this.locks.toArray();
        switch (array.length) {
            case 0:
                break;
            case 1:
                Object obj = array[0];
                if (obj != null) {
                    return Collections.singleton((Table) obj);
                }
                break;
            default:
                HashSet hashSet = new HashSet();
                for (Object obj2 : array) {
                    if (obj2 != null) {
                        hashSet.add((Table) obj2);
                    }
                }
                return hashSet;
        }
        return Collections.emptySet();
    }

    public void waitIfExclusiveModeEnabled() {
        SessionLocal exclusiveSession;
        transitionToState(State.RUNNING, true);
        if (getDatabase().getLobSession() == this) {
            return;
        }
        while (isOpen() && (exclusiveSession = getDatabase().getExclusiveSession()) != null && exclusiveSession != this && !exclusiveSession.isLockedByCurrentThread()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
            }
        }
    }

    public Map<Object, QueryExpressionIndex> getViewIndexCache(boolean z) {
        if (z) {
            if (this.derivedTableIndexCache == null) {
                this.derivedTableIndexCache = new HashMap<>();
            }
            return this.derivedTableIndexCache;
        }
        SmallLRUCache<Object, QueryExpressionIndex> smallLRUCache = this.viewIndexCache;
        if (smallLRUCache == null) {
            SmallLRUCache<Object, QueryExpressionIndex> newInstance = SmallLRUCache.newInstance(64);
            smallLRUCache = newInstance;
            this.viewIndexCache = newInstance;
        }
        return smallLRUCache;
    }

    public void setQueryTimeout(int i) {
        int i2 = getDatabase().getSettings().maxQueryTimeout;
        if (i2 != 0 && (i2 < i || i == 0)) {
            i = i2;
        }
        this.queryTimeout = i;
        this.cancelAtNs = 0L;
    }

    public int getQueryTimeout() {
        return this.queryTimeout;
    }

    public void setWaitForLock(Table table, Thread thread) {
        this.waitForLock = table;
        this.waitForLockThread = thread;
    }

    public Table getWaitForLock() {
        return this.waitForLock;
    }

    public Thread getWaitForLockThread() {
        return this.waitForLockThread;
    }

    public int getModificationId() {
        return this.modificationId;
    }

    public Value getTransactionId() {
        if (this.transaction == null || !this.transaction.hasChanges()) {
            return ValueNull.INSTANCE;
        }
        return ValueVarchar.get(Long.toString(this.transaction.getSequenceNum()));
    }

    public int nextObjectId() {
        int i = this.objectId;
        this.objectId = i + 1;
        return i;
    }

    public Transaction getTransaction() {
        if (this.transaction == null) {
            Store store = getDatabase().getStore();
            if (store.getMvStore().isClosed()) {
                Throwable backgroundException = getDatabase().getBackgroundException();
                getDatabase().shutdownImmediately();
                throw DbException.get(ErrorCode.DATABASE_IS_CLOSED, backgroundException, new String[0]);
            }
            this.transaction = store.getTransactionStore().begin(this, this.lockTimeout, this.id, this.isolationLevel);
            this.startStatement = -1L;
        }
        return this.transaction;
    }

    private long getStatementSavepoint() {
        if (this.startStatement == -1) {
            this.startStatement = getTransaction().setSavepoint();
        }
        return this.startStatement;
    }

    public void startStatementWithinTransaction(Command command) {
        Transaction transaction = getTransaction();
        if (transaction != null) {
            HashSet<MVMap<Object, VersionedValue<Object>>> hashSet = new HashSet<>();
            if (command != null) {
                Set<DbObject> dependencies = command.getDependencies();
                switch (transaction.getIsolationLevel()) {
                    case SNAPSHOT:
                    case SERIALIZABLE:
                        if (!transaction.hasStatementDependencies()) {
                            Iterator<Schema> it = getDatabase().getAllSchemasNoMeta().iterator();
                            while (it.hasNext()) {
                                for (Table table : it.next().getAllTablesAndViews(null)) {
                                    if (table instanceof MVTable) {
                                        addTableToDependencies((MVTable) table, hashSet);
                                    }
                                }
                            }
                            break;
                        }
                    case READ_COMMITTED:
                    case READ_UNCOMMITTED:
                        for (DbObject dbObject : dependencies) {
                            if (dbObject instanceof MVTable) {
                                addTableToDependencies((MVTable) dbObject, hashSet);
                            }
                        }
                        break;
                    case REPEATABLE_READ:
                        HashSet hashSet2 = new HashSet();
                        for (DbObject dbObject2 : dependencies) {
                            if (dbObject2 instanceof MVTable) {
                                addTableToDependencies((MVTable) dbObject2, hashSet, hashSet2);
                            }
                        }
                        break;
                }
            }
            transaction.markStatementStart(hashSet);
        }
        this.startStatement = -1L;
        if (command != null) {
            setCurrentCommand(command);
        }
    }

    private static void addTableToDependencies(MVTable mVTable, HashSet<MVMap<Object, VersionedValue<Object>>> hashSet) {
        Iterator<Index> it = mVTable.getIndexes().iterator();
        while (it.hasNext()) {
            Index next = it.next();
            if (next instanceof MVIndex) {
                hashSet.add(((MVIndex) next).getMVMap());
            }
        }
    }

    private static void addTableToDependencies(MVTable mVTable, HashSet<MVMap<Object, VersionedValue<Object>>> hashSet, HashSet<MVTable> hashSet2) {
        if (!hashSet2.add(mVTable)) {
            return;
        }
        addTableToDependencies(mVTable, hashSet);
        ArrayList<Constraint> constraints = mVTable.getConstraints();
        if (constraints != null) {
            Iterator<Constraint> it = constraints.iterator();
            while (it.hasNext()) {
                Table table = it.next().getTable();
                if (table != mVTable && (table instanceof MVTable)) {
                    addTableToDependencies((MVTable) table, hashSet, hashSet2);
                }
            }
        }
    }

    public void endStatement() {
        setCurrentCommand(null);
        if (hasTransaction()) {
            this.transaction.markStatementEnd();
        }
        this.startStatement = -1L;
    }

    public void clearViewIndexCache() {
        this.viewIndexCache = null;
    }

    @Override // org.h2.engine.Session
    public ValueLob addTemporaryLob(ValueLob valueLob) {
        LobData lobData = valueLob.getLobData();
        if (lobData instanceof LobDataInMemory) {
            return valueLob;
        }
        int tableId = ((LobDataDatabase) lobData).getTableId();
        if (tableId == -3 || tableId == -2) {
            if (this.temporaryResultLobs == null) {
                this.temporaryResultLobs = new LinkedList<>();
            }
            this.temporaryResultLobs.add(new TimeoutValue(valueLob));
        } else {
            if (this.temporaryLobs == null) {
                this.temporaryLobs = new ArrayList<>();
            }
            this.temporaryLobs.add(valueLob);
        }
        return valueLob;
    }

    @Override // org.h2.engine.Session
    public boolean isRemote() {
        return false;
    }

    public void markTableForAnalyze(Table table) {
        if (this.tablesToAnalyze == null) {
            this.tablesToAnalyze = new HashSet<>();
        }
        this.tablesToAnalyze.add(table);
    }

    public State getState() {
        return getBlockingSessionId() != 0 ? State.BLOCKED : this.state.get();
    }

    public int getBlockingSessionId() {
        if (this.transaction == null) {
            return 0;
        }
        return this.transaction.getBlockerId();
    }

    @Override // org.h2.mvstore.tx.TransactionStore.RollbackListener
    public void onRollback(MVMap<Object, VersionedValue<Object>> mVMap, Object obj, VersionedValue<Object> versionedValue, VersionedValue<Object> versionedValue2) {
        MVTable table = getDatabase().getStore().getTable(mVMap.getName());
        if (table != null) {
            Row row = versionedValue == null ? null : (Row) versionedValue.getCurrentValue();
            Row row2 = versionedValue2 == null ? null : (Row) versionedValue2.getCurrentValue();
            table.fireAfterRow(this, row, row2, true);
            if (table.getContainsLargeObject()) {
                if (row != null) {
                    int columnCount = row.getColumnCount();
                    for (int i = 0; i < columnCount; i++) {
                        Value value = row.getValue(i);
                        if (value instanceof ValueLob) {
                            removeAtCommit((ValueLob) value);
                        }
                    }
                }
                if (row2 != null) {
                    int columnCount2 = row2.getColumnCount();
                    for (int i2 = 0; i2 < columnCount2; i2++) {
                        Value value2 = row2.getValue(i2);
                        if (value2 instanceof ValueLob) {
                            removeAtCommitStop((ValueLob) value2);
                        }
                    }
                }
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/SessionLocal$TimeoutValue.class */
    public static class TimeoutValue {
        final long created = System.nanoTime();
        final ValueLob value;

        TimeoutValue(ValueLob valueLob) {
            this.value = valueLob;
        }
    }

    public NetworkConnectionInfo getNetworkConnectionInfo() {
        return this.networkConnectionInfo;
    }

    @Override // org.h2.engine.Session
    public void setNetworkConnectionInfo(NetworkConnectionInfo networkConnectionInfo) {
        this.networkConnectionInfo = networkConnectionInfo;
    }

    @Override // org.h2.engine.CastDataProvider
    public ValueTimestampTimeZone currentTimestamp() {
        ValueTimestampTimeZone valueTimestampTimeZone = this.currentTimestamp;
        if (valueTimestampTimeZone == null) {
            ValueTimestampTimeZone currentTimestamp = DateTimeUtils.currentTimestamp(this.timeZone, this.commandStartOrEnd);
            valueTimestampTimeZone = currentTimestamp;
            this.currentTimestamp = currentTimestamp;
        }
        return valueTimestampTimeZone;
    }

    @Override // org.h2.engine.CastDataProvider
    public Mode getMode() {
        return getDatabase().getMode();
    }

    @Override // org.h2.engine.CastDataProvider
    public JavaObjectSerializer getJavaObjectSerializer() {
        return getDatabase().getJavaObjectSerializer();
    }

    @Override // org.h2.engine.Session
    public IsolationLevel getIsolationLevel() {
        return this.isolationLevel;
    }

    @Override // org.h2.engine.Session
    public void setIsolationLevel(IsolationLevel isolationLevel) {
        commit(false);
        this.isolationLevel = isolationLevel;
    }

    public BitSet getNonKeywords() {
        return this.nonKeywords;
    }

    public void setNonKeywords(BitSet bitSet) {
        this.nonKeywords = bitSet;
    }

    @Override // org.h2.engine.Session
    public Session.StaticSettings getStaticSettings() {
        Session.StaticSettings staticSettings = this.staticSettings;
        if (staticSettings == null) {
            DbSettings settings = getDatabase().getSettings();
            Session.StaticSettings staticSettings2 = new Session.StaticSettings(settings.databaseToUpper, settings.databaseToLower, settings.caseInsensitiveIdentifiers);
            staticSettings = staticSettings2;
            this.staticSettings = staticSettings2;
        }
        return staticSettings;
    }

    @Override // org.h2.engine.Session
    public Session.DynamicSettings getDynamicSettings() {
        return new Session.DynamicSettings(getMode(), this.timeZone);
    }

    @Override // org.h2.engine.CastDataProvider
    public TimeZoneProvider currentTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(TimeZoneProvider timeZoneProvider) {
        if (!timeZoneProvider.equals(this.timeZone)) {
            this.timeZone = timeZoneProvider;
            ValueTimestampTimeZone valueTimestampTimeZone = this.currentTimestamp;
            if (valueTimestampTimeZone != null) {
                long dateValue = valueTimestampTimeZone.getDateValue();
                long timeNanos = valueTimestampTimeZone.getTimeNanos();
                int timeZoneOffsetSeconds = valueTimestampTimeZone.getTimeZoneOffsetSeconds();
                this.currentTimestamp = DateTimeUtils.timestampTimeZoneAtOffset(dateValue, timeNanos, timeZoneOffsetSeconds, timeZoneProvider.getTimeZoneOffsetUTC(DateTimeUtils.getEpochSeconds(dateValue, timeNanos, timeZoneOffsetSeconds)));
            }
            this.modificationId++;
        }
    }

    public boolean areEqual(Value value, Value value2) {
        return value.compareTo(value2, this, getCompareMode()) == 0;
    }

    public int compare(Value value, Value value2) {
        return value.compareTo(value2, this, getCompareMode());
    }

    public int compareWithNull(Value value, Value value2, boolean z) {
        return value.compareWithNull(value2, z, this, getCompareMode());
    }

    public int compareTypeSafe(Value value, Value value2) {
        return value.compareTypeSafe(value2, getCompareMode(), this);
    }

    public void setTruncateLargeLength(boolean z) {
        this.truncateLargeLength = z;
    }

    public boolean isTruncateLargeLength() {
        return this.truncateLargeLength;
    }

    public void setVariableBinary(boolean z) {
        this.variableBinary = z;
    }

    public boolean isVariableBinary() {
        return this.variableBinary;
    }

    public void setOldInformationSchema(boolean z) {
        this.oldInformationSchema = z;
    }

    @Override // org.h2.engine.Session
    public boolean isOldInformationSchema() {
        return this.oldInformationSchema;
    }

    @Override // org.h2.engine.Session
    public DatabaseMeta getDatabaseMeta() {
        return new DatabaseMetaLocal(this);
    }

    @Override // org.h2.engine.CastDataProvider
    public boolean zeroBasedEnums() {
        return getDatabase().zeroBasedEnums();
    }

    public void setQuirksMode(boolean z) {
        this.quirksMode = z;
    }

    public boolean isQuirksMode() {
        return this.quirksMode || getDatabase().isStarting();
    }

    @Override // org.h2.engine.Session
    public Session setThreadLocalSession() {
        Session session = THREAD_LOCAL_SESSION.get();
        THREAD_LOCAL_SESSION.set(this);
        return session;
    }

    @Override // org.h2.engine.Session
    public void resetThreadLocalSession(Session session) {
        if (session == null) {
            THREAD_LOCAL_SESSION.remove();
        } else {
            THREAD_LOCAL_SESSION.set(session);
        }
    }

    private CompareMode getCompareMode() {
        return getDatabase().getCompareMode();
    }

    private <T> HashMap<String, T> newStringsMap() {
        return getDatabase().newStringMap();
    }
}
