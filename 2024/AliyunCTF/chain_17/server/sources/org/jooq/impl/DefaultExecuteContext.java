package org.jooq.impl;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.ConverterContext;
import org.jooq.DDLQuery;
import org.jooq.DSLContext;
import org.jooq.Delete;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteType;
import org.jooq.Insert;
import org.jooq.Merge;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Routine;
import org.jooq.SQLDialect;
import org.jooq.Scope;
import org.jooq.Update;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.jdbc.JDBCUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultExecuteContext.class */
public class DefaultExecuteContext implements ExecuteContext {
    private final ConverterContext converterContext;
    private final Instant creationTime;
    private final Configuration originalConfiguration;
    private final Configuration derivedConfiguration;
    private final Map<Object, Object> data;
    private Query query;
    private final Routine<?> routine;
    private String sql;
    private int skipUpdateCounts;
    private final ExecuteContext.BatchMode batchMode;
    private Query[] batchQueries;
    private String[] batchSQL;
    private int[] batchRows;
    ConnectionProvider connectionProvider;
    private Connection connection;
    private Connection wrappedConnection;
    private PreparedStatement statement;
    private int statementExecutionCount;
    private ResultSet resultSet;
    private Record record;
    private Result<?> result;
    int recordLevel;
    int resultLevel;
    private int rows;
    private RuntimeException exception;
    private SQLException sqlException;
    private SQLWarning sqlWarning;
    private String[] serverOutput;
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) DefaultExecuteContext.class);
    private static final JooqLogger logVersionSupport = JooqLogger.getLogger(DefaultExecuteContext.class, "logVersionSupport", 1);
    private static final JooqLogger logDefaultDialect = JooqLogger.getLogger(DefaultExecuteContext.class, "logDefaultDialect", 1);
    private static final ThreadLocal<List<AutoCloseable>> RESOURCES = new ThreadLocal<>();
    private static final ThreadLocal<ExecuteContext> LOCAL_EXECUTE_CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Connection> LOCAL_CONNECTION = new ThreadLocal<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void clean() {
        List<AutoCloseable> resources = RESOURCES.get();
        if (resources != null) {
            for (AutoCloseable resource : resources) {
                JDBCUtils.safeClose(resource);
            }
            RESOURCES.remove();
        }
        LOCAL_CONNECTION.remove();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void register(Blob blob) {
        Objects.requireNonNull(blob);
        register(blob::free);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void register(Clob clob) {
        Objects.requireNonNull(clob);
        register(clob::free);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void register(SQLXML xml) {
        Objects.requireNonNull(xml);
        register(xml::free);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void register(java.sql.Array array) {
        Objects.requireNonNull(array);
        register(array::free);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void register(AutoCloseable closeable) {
        List<AutoCloseable> list = RESOURCES.get();
        if (list == null) {
            list = new ArrayList();
            RESOURCES.set(list);
        }
        list.add(closeable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final ExecuteContext localExecuteContext() {
        return LOCAL_EXECUTE_CONTEXT.get();
    }

    static final <E extends Exception> void localExecuteContext(ExecuteContext ctx, ThrowingRunnable<E> runnable) throws Exception {
        localExecuteContext(ctx, () -> {
            runnable.run();
            return null;
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, E extends Exception> T localExecuteContext(ExecuteContext ctx, ThrowingSupplier<T, E> supplier) throws Exception {
        ExecuteContext old = localExecuteContext();
        try {
            LOCAL_EXECUTE_CONTEXT.set(ctx);
            T t = supplier.get();
            LOCAL_EXECUTE_CONTEXT.set(old);
            return t;
        } catch (Throwable th) {
            LOCAL_EXECUTE_CONTEXT.set(old);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Connection localConnection() {
        return LOCAL_CONNECTION.get();
    }

    static final Connection localTargetConnection(Scope scope) {
        Connection result = localConnection();
        log.info("Could not unwrap native Connection type. Consider implementing an org.jooq.UnwrapperProvider");
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultExecuteContext(Configuration configuration) {
        this(configuration, ExecuteContext.BatchMode.NONE, null, null, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultExecuteContext(Configuration configuration, ExecuteContext.BatchMode batchMode, Query[] batchQueries) {
        this(configuration, batchMode, null, batchQueries, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultExecuteContext(Configuration configuration, Query query) {
        this(configuration, ExecuteContext.BatchMode.NONE, query, null, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultExecuteContext(Configuration configuration, Routine<?> routine) {
        this(configuration, ExecuteContext.BatchMode.NONE, null, null, routine);
    }

    private DefaultExecuteContext(Configuration configuration, ExecuteContext.BatchMode batchMode, Query query, Query[] batchQueries, Routine<?> routine) {
        this.rows = -1;
        this.creationTime = configuration.clock().instant();
        this.connectionProvider = configuration.connectionProvider();
        this.originalConfiguration = configuration;
        this.derivedConfiguration = configuration.derive(new ExecuteContextConnectionProvider());
        this.data = new DataMap();
        this.batchMode = batchMode;
        this.query = query;
        this.routine = routine;
        this.converterContext = new DefaultConverterContext(this.derivedConfiguration, this.data);
        batchQueries0(batchQueries);
        clean();
    }

    @Override // org.jooq.ExecuteContext
    public final ConverterContext converterContext() {
        return this.converterContext;
    }

    @Override // org.jooq.Scope
    public final Instant creationTime() {
        return this.creationTime;
    }

    @Override // org.jooq.Scope
    public final Map<Object, Object> data() {
        return this.data;
    }

    @Override // org.jooq.Scope
    public final Object data(Object key) {
        return this.data.get(key);
    }

    @Override // org.jooq.Scope
    public final Object data(Object key, Object value) {
        return this.data.put(key, value);
    }

    @Override // org.jooq.ExecuteContext
    public final ExecuteType type() {
        if (this.routine != null) {
            return ExecuteType.ROUTINE;
        }
        if (this.batchMode != ExecuteContext.BatchMode.NONE) {
            return ExecuteType.BATCH;
        }
        if (this.query != null) {
            if (this.query instanceof ResultQuery) {
                return ExecuteType.READ;
            }
            if ((this.query instanceof Insert) || (this.query instanceof Update) || (this.query instanceof Delete) || (this.query instanceof Merge)) {
                return ExecuteType.WRITE;
            }
            if (this.query instanceof DDLQuery) {
                return ExecuteType.DDL;
            }
            String s = this.query.getSQL().toLowerCase(SettingsTools.renderLocale(configuration().settings()));
            if (s.matches("^(with\\b.*?\\bselect|select|explain)\\b.*?")) {
                return ExecuteType.READ;
            }
            if (s.matches("^(insert|update|delete|merge|replace|upsert|lock)\\b.*?")) {
                return ExecuteType.WRITE;
            }
            if (s.matches("^(create|alter|drop|truncate|grant|revoke|analyze|comment|flashback|enable|disable)\\b.*?")) {
                return ExecuteType.DDL;
            }
            if (s.matches("^\\s*\\{\\s*(\\?\\s*=\\s*)call.*?")) {
                return ExecuteType.ROUTINE;
            }
            if (s.matches("^(call|begin|declare)\\b.*?")) {
                return ExecuteType.ROUTINE;
            }
        } else if (this.resultSet != null) {
            return ExecuteType.READ;
        }
        return ExecuteType.OTHER;
    }

    @Override // org.jooq.ExecuteContext
    public final Query query() {
        return this.query;
    }

    @Override // org.jooq.ExecuteContext
    public final ExecuteContext.BatchMode batchMode() {
        return this.batchMode;
    }

    @Override // org.jooq.ExecuteContext
    public final Query[] batchQueries() {
        if (this.batchMode != ExecuteContext.BatchMode.NONE) {
            return this.batchQueries;
        }
        if (query() != null) {
            return new Query[]{query()};
        }
        return Tools.EMPTY_QUERY;
    }

    private final void batchQueries0(Query... newQueries) {
        if (newQueries != null) {
            this.batchQueries = (Query[]) newQueries.clone();
            this.batchSQL = new String[newQueries.length];
            this.batchRows = new int[newQueries.length];
            Arrays.fill(this.batchRows, -1);
            return;
        }
        this.batchQueries = null;
        this.batchSQL = null;
        this.batchRows = null;
    }

    @Override // org.jooq.ExecuteContext
    public final Routine<?> routine() {
        return this.routine;
    }

    @Override // org.jooq.ExecuteContext
    public final void sql(String s) {
        this.sql = s;
        if (this.batchSQL != null && this.batchSQL.length == 1) {
            this.batchSQL[0] = s;
        }
    }

    @Override // org.jooq.ExecuteContext
    public final String sql() {
        return this.sql;
    }

    @Override // org.jooq.ExecuteContext
    public final int skipUpdateCounts() {
        return this.skipUpdateCounts;
    }

    @Override // org.jooq.ExecuteContext
    public void skipUpdateCounts(int skip) {
        this.skipUpdateCounts = skip;
    }

    @Override // org.jooq.ExecuteContext
    public final String[] batchSQL() {
        if (this.batchMode != ExecuteContext.BatchMode.NONE) {
            return this.batchSQL;
        }
        if (this.routine != null || query() != null) {
            return new String[]{this.sql};
        }
        return Tools.EMPTY_STRING;
    }

    @Override // org.jooq.ExecuteContext
    public final void statement(PreparedStatement s) {
        this.statement = s;
    }

    @Override // org.jooq.ExecuteContext
    public final PreparedStatement statement() {
        return this.statement;
    }

    @Override // org.jooq.ExecuteContext
    public final int statementExecutionCount() {
        return this.statementExecutionCount;
    }

    @Override // org.jooq.ExecuteContext
    public final void resultSet(ResultSet rs) {
        this.resultSet = rs;
    }

    @Override // org.jooq.ExecuteContext
    public final ResultSet resultSet() {
        return this.resultSet;
    }

    @Override // org.jooq.Scope
    public final Configuration configuration() {
        return this.derivedConfiguration;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Configuration originalConfiguration() {
        return this.originalConfiguration;
    }

    @Override // org.jooq.Scope
    public final DSLContext dsl() {
        return configuration().dsl();
    }

    @Override // org.jooq.Scope
    public final Settings settings() {
        return Tools.settings(configuration());
    }

    @Override // org.jooq.Scope
    public final SQLDialect dialect() {
        return Tools.configuration(configuration()).dialect();
    }

    @Override // org.jooq.Scope
    public final SQLDialect family() {
        return dialect().family();
    }

    @Override // org.jooq.ExecuteContext
    public final void connectionProvider(ConnectionProvider provider) {
        this.connectionProvider = provider;
    }

    @Override // org.jooq.ExecuteContext
    public final Connection connection() {
        if (this.wrappedConnection == null && this.connectionProvider != null) {
            connection(this.connectionProvider, this.connectionProvider.acquire());
        }
        return this.wrappedConnection;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void connection(ConnectionProvider provider, Connection c) {
        if (c != null) {
            if (dialect().isVersioned() && logVersionSupport.isWarnEnabled()) {
                String productVersion = null;
                try {
                    int majorVersion = c.getMetaData().getDatabaseMajorVersion();
                    int minorVersion = c.getMetaData().getDatabaseMinorVersion();
                    productVersion = c.getMetaData().getDatabaseProductVersion();
                    logVersionSupport(majorVersion, minorVersion, productVersion);
                } catch (SQLException e) {
                    logVersionSupport.info("Version", "Database version cannot be read: " + e.getMessage());
                } catch (Exception e2) {
                    logVersionSupport.info("Version", "Cannot obtain database version for " + String.valueOf(dialect()) + ": " + productVersion + ". (" + String.valueOf(e2.getClass()) + ": " + e2.getMessage() + "). Please consider reporting this here: https://jooq.org/bug");
                }
            }
            LOCAL_CONNECTION.set(c);
            this.connection = c;
            this.wrappedConnection = wrap(provider, c);
        }
    }

    private final void logVersionSupport(int majorVersion, int minorVersion, String productVersion) {
        if (!dialect().supportsDatabaseVersion(majorVersion, minorVersion, productVersion)) {
            logVersionSupport.warn("Version mismatch", "Database version is older than what dialect " + String.valueOf(dialect()) + " supports: " + productVersion + ". Consider https://www.jooq.org/download/support-matrix to see what jOOQ version and edition supports which RDBMS versions.");
        } else {
            logVersionSupport.info("Version", "Database version is supported by dialect " + String.valueOf(dialect()) + ": " + productVersion);
        }
    }

    private final Connection wrap(ConnectionProvider provider, Connection c) {
        return wrap0(new SettingsEnabledConnection(new ProviderEnabledConnection(provider, c), this.derivedConfiguration.settings(), this));
    }

    private final Connection wrap0(Connection c) {
        if (this.derivedConfiguration.settings().getDiagnosticsConnection() == org.jooq.conf.DiagnosticsConnection.ON) {
            return new DiagnosticsConnection(this.derivedConfiguration, c);
        }
        return c;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void incrementStatementExecutionCount() {
        this.statementExecutionCount++;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final DefaultExecuteContext withStatementExecutionCount(int count) {
        this.statementExecutionCount = count;
        return this;
    }

    @Override // org.jooq.ExecuteContext
    public final void record(Record r) {
        this.record = r;
    }

    @Override // org.jooq.ExecuteContext
    public final Record record() {
        return this.record;
    }

    @Override // org.jooq.ExecuteContext
    public final int recordLevel() {
        return this.recordLevel;
    }

    @Override // org.jooq.ExecuteContext
    public final int rows() {
        return this.rows;
    }

    @Override // org.jooq.ExecuteContext
    public final void rows(int r) {
        this.rows = r;
        if (this.batchRows != null && this.batchRows.length == 1) {
            this.batchRows[0] = r;
        }
    }

    @Override // org.jooq.ExecuteContext
    public final int[] batchRows() {
        if (this.batchMode != ExecuteContext.BatchMode.NONE) {
            return this.batchRows;
        }
        if (this.routine != null || query() != null) {
            return new int[]{this.rows};
        }
        return Tools.EMPTY_INT;
    }

    @Override // org.jooq.ExecuteContext
    public final void result(Result<?> r) {
        this.result = r;
    }

    @Override // org.jooq.ExecuteContext
    public final Result<?> result() {
        return this.result;
    }

    @Override // org.jooq.ExecuteContext
    public final int resultLevel() {
        return this.resultLevel;
    }

    @Override // org.jooq.ExecuteContext
    public final RuntimeException exception() {
        return this.exception;
    }

    @Override // org.jooq.ExecuteContext
    public final void exception(RuntimeException e) {
        StackTraceElement[] oldStack;
        this.exception = Tools.translate(sql(), e);
        if (Boolean.TRUE.equals(settings().isDebugInfoOnStackTrace()) && (oldStack = this.exception.getStackTrace()) != null) {
            StackTraceElement[] newStack = new StackTraceElement[oldStack.length + 1];
            System.arraycopy(oldStack, 0, newStack, 1, oldStack.length);
            newStack[0] = new StackTraceElement("org.jooq_3.19.3." + String.valueOf(dialect()), "debug", null, -1);
            this.exception.setStackTrace(newStack);
        }
    }

    @Override // org.jooq.ExecuteContext
    public final SQLException sqlException() {
        return this.sqlException;
    }

    @Override // org.jooq.ExecuteContext
    public final void sqlException(SQLException e) {
        this.sqlException = e;
        exception(Tools.translate(sql(), e));
        if (family() == SQLDialect.DEFAULT && logDefaultDialect.isWarnEnabled()) {
            logDefaultDialect.warn("Unsupported dialect", "An exception was thrown when executing a query with unsupported dialect: SQLDialect.DEFAULT.\n\nThis is usually due to one of 2 reasons:\n- The dialect was configured by accident (e.g. through a wrong Spring Boot configuration).\n  In this case, the solution is to configure the correct dialect, e.g. SQLDialect.POSTGRES\n- SQLDialect.DEFAULT is used as a \"close enough\" approximation of an unsupported dialect.\n  Please beware that SQLDialect.DEFAULT is used mainly for DEBUG logging SQL strings, e.g.\n  when calling Query.toString(). It does not guarantee stability of generated SQL, i.e.\n  future versions of jOOQ may produce different SQL strings, which may break assumptions\n  about your unsupported dialect.\n  Please visit https://github.com/jOOQ/jOOQ/discussions/14059 for new dialect support.\n");
        }
    }

    @Override // org.jooq.ExecuteContext
    public final SQLWarning sqlWarning() {
        return this.sqlWarning;
    }

    @Override // org.jooq.ExecuteContext
    public final void sqlWarning(SQLWarning e) {
        this.sqlWarning = e;
    }

    @Override // org.jooq.ExecuteContext
    public final String[] serverOutput() {
        return this.serverOutput == null ? Tools.EMPTY_STRING : this.serverOutput;
    }

    @Override // org.jooq.ExecuteContext
    public final void serverOutput(String[] output) {
        this.serverOutput = output;
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultExecuteContext$ExecuteContextConnectionProvider.class */
    final class ExecuteContextConnectionProvider implements ConnectionProvider {
        ExecuteContextConnectionProvider() {
        }

        @Override // org.jooq.ConnectionProvider
        @NotNull
        public final Connection acquire() {
            if (DefaultExecuteContext.this.connection == null) {
                DefaultExecuteContext.this.connection();
            }
            return DefaultExecuteContext.this.wrap(this, DefaultExecuteContext.this.connection);
        }

        @Override // org.jooq.ConnectionProvider
        public final void release(Connection c) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void transformQueries(ExecuteListener listener) {
        if (!Boolean.TRUE.equals(settings().isTransformPatterns()) || configuration().requireCommercial(() -> {
            return "SQL transformations are a commercial only feature. Please consider upgrading to the jOOQ Professional Edition or jOOQ Enterprise Edition.";
        })) {
        }
    }
}
