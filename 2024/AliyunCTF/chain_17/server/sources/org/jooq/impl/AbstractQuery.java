package org.jooq.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import org.jooq.CloseableQuery;
import org.jooq.Configuration;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteType;
import org.jooq.Param;
import org.jooq.Record;
import org.jooq.RenderContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.QueryPoolable;
import org.jooq.conf.SettingsTools;
import org.jooq.conf.ThrowExceptions;
import org.jooq.exception.ControlFlowSignal;
import org.jooq.exception.DetachedException;
import org.jooq.impl.DefaultRenderContext;
import org.jooq.impl.DefaultUnwrapperProvider;
import org.jooq.impl.Tools;
import org.jooq.tools.Ints;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.jdbc.BatchedPreparedStatement;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractQuery.class */
abstract class AbstractQuery<R extends Record> extends AbstractAttachableQueryPart implements CloseableQuery {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) AbstractQuery.class);
    private static final Set<SQLDialect> SET_AUTOCOMMIT_ON_START_TRANSACTION = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.HSQLDB);
    private int timeout;
    private QueryPoolable poolable;
    private boolean keepStatement;
    transient PreparedStatement statement;
    transient int statementExecutionCount;
    transient DefaultRenderContext.Rendered rendered;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractQuery(Configuration configuration) {
        super(configuration);
        this.poolable = QueryPoolable.DEFAULT;
    }

    final void toSQLSemiColon(RenderContext ctx) {
    }

    @Override // org.jooq.CloseableQuery, org.jooq.Query
    public CloseableQuery bind(String param, Object value) {
        Integer index = Ints.tryParse(param);
        if (index != null) {
            return bind(index.intValue(), value);
        }
        ParamCollector collector = new ParamCollector(configuration(), true);
        collector.visit(this);
        List<Param<?>> params = collector.result.get(param);
        if (params == null || params.size() == 0) {
            throw new IllegalArgumentException("No such parameter : " + param);
        }
        for (Param<?> p : params) {
            ((AbstractParamX) p).setConverted0(value);
            closeIfNecessary(p);
        }
        return this;
    }

    @Override // org.jooq.CloseableQuery, org.jooq.Query
    public CloseableQuery bind(int index, Object value) {
        Param<?>[] params = (Param[]) getParams().values().toArray(Tools.EMPTY_PARAM);
        if (index < 1 || index > params.length) {
            throw new IllegalArgumentException("Index out of range for Query parameters : " + index);
        }
        AbstractParamX<?> param = (AbstractParamX) params[index - 1];
        param.setConverted0(value);
        closeIfNecessary(param);
        return this;
    }

    private final void closeIfNecessary(Param<?> param) {
        if (keepStatement() && this.statement != null) {
            if (param.isInline()) {
                close();
            } else if (SettingsTools.getParamType(configuration().settings()) == ParamType.INLINED) {
                close();
            }
        }
    }

    @Override // org.jooq.CloseableQuery, org.jooq.Query
    public CloseableQuery poolable(boolean p) {
        this.poolable = p ? QueryPoolable.TRUE : QueryPoolable.FALSE;
        return this;
    }

    @Override // org.jooq.CloseableQuery, org.jooq.Query
    public CloseableQuery queryTimeout(int t) {
        this.timeout = t;
        return this;
    }

    @Override // org.jooq.CloseableQuery, org.jooq.Query
    public CloseableQuery keepStatement(boolean k) {
        this.keepStatement = k;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean keepStatement() {
        return this.keepStatement;
    }

    @Override // org.jooq.CloseableQuery, java.lang.AutoCloseable
    public final void close() {
        if (this.statement != null) {
            try {
                this.statement.close();
                this.statement = null;
            } catch (SQLException e) {
                throw Tools.translate(this.rendered.sql, e);
            }
        }
    }

    @Override // org.jooq.Query
    public final void cancel() {
        if (this.statement != null) {
            try {
                this.statement.cancel();
            } catch (SQLException e) {
                throw Tools.translate(this.rendered.sql, e);
            }
        }
    }

    @Override // org.jooq.Query
    public final int execute() {
        if (isExecutable()) {
            Configuration c = configurationOrDefault();
            DefaultExecuteContext defaultExecuteContext = new DefaultExecuteContext(c, this);
            ExecuteListener executeListener = ExecuteListeners.get(defaultExecuteContext);
            try {
                try {
                    try {
                        executeListener.start(defaultExecuteContext);
                        if (keepStatement() && this.statement != null) {
                            defaultExecuteContext.sql(this.rendered.sql);
                            defaultExecuteContext.statement(this.statement);
                            defaultExecuteContext.connection(c.connectionProvider(), this.statement.getConnection());
                            int i = this.statementExecutionCount + 1;
                            this.statementExecutionCount = i;
                            defaultExecuteContext.withStatementExecutionCount(i);
                        } else {
                            defaultExecuteContext.transformQueries(executeListener);
                            executeListener.renderStart(defaultExecuteContext);
                            this.rendered = getSQL0(defaultExecuteContext);
                            defaultExecuteContext.sql(this.rendered.sql);
                            executeListener.renderEnd(defaultExecuteContext);
                            this.rendered.sql = defaultExecuteContext.sql();
                            connection(defaultExecuteContext);
                            if ((this instanceof StartTransaction) && SET_AUTOCOMMIT_ON_START_TRANSACTION.contains(defaultExecuteContext.dialect())) {
                                defaultExecuteContext.connection().setAutoCommit(false);
                            }
                            executeListener.prepareStart(defaultExecuteContext);
                            prepare(defaultExecuteContext);
                            executeListener.prepareEnd(defaultExecuteContext);
                            this.statement = defaultExecuteContext.statement();
                        }
                        int t = SettingsTools.getQueryTimeout(this.timeout, defaultExecuteContext.settings());
                        if (t != 0) {
                            defaultExecuteContext.statement().setQueryTimeout(t);
                        }
                        QueryPoolable p = SettingsTools.getQueryPoolable(this.poolable, defaultExecuteContext.settings());
                        if (p == QueryPoolable.TRUE) {
                            defaultExecuteContext.statement().setPoolable(true);
                        } else if (p == QueryPoolable.FALSE) {
                            defaultExecuteContext.statement().setPoolable(false);
                        }
                        if (SettingsTools.executePreparedStatements(c.settings()) && !Boolean.TRUE.equals(defaultExecuteContext.data(Tools.BooleanDataKey.DATA_FORCE_STATIC_STATEMENT))) {
                            executeListener.bindStart(defaultExecuteContext);
                            if (this.rendered.bindValues != null) {
                                new DefaultBindContext(c, defaultExecuteContext, defaultExecuteContext.statement()).visit(this.rendered.bindValues);
                            }
                            executeListener.bindEnd(defaultExecuteContext);
                        }
                        int result = execute(defaultExecuteContext, executeListener);
                        if (!keepResultSet() || defaultExecuteContext.exception() != null) {
                            Tools.safeClose(executeListener, defaultExecuteContext, keepStatement());
                        }
                        if (!keepStatement()) {
                            this.statement = null;
                            this.rendered = null;
                        }
                        return result;
                    } catch (ControlFlowSignal e) {
                        throw e;
                    } catch (RuntimeException e2) {
                        defaultExecuteContext.exception(e2);
                        executeListener.exception(defaultExecuteContext);
                        throw defaultExecuteContext.exception();
                    }
                } catch (SQLException e3) {
                    defaultExecuteContext.sqlException(e3);
                    executeListener.exception(defaultExecuteContext);
                    throw defaultExecuteContext.exception();
                }
            } catch (Throwable th) {
                if (!keepResultSet() || defaultExecuteContext.exception() != null) {
                    Tools.safeClose(executeListener, defaultExecuteContext, keepStatement());
                }
                if (!keepStatement()) {
                    this.statement = null;
                    this.rendered = null;
                }
                throw th;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Query is not executable", this);
            return 0;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Connection connection(DefaultExecuteContext ctx) {
        Connection result = ctx.connection();
        if (result == null) {
            if (ctx.configuration().connectionFactory() instanceof NoConnectionFactory) {
                throw new DetachedException("Cannot execute query. No JDBC Connection configured");
            }
            throw new DetachedException("Attempt to execute a blocking method (e.g. Query.execute() or ResultQuery.fetch()) when only an R2BDC ConnectionFactory was configured. jOOQ's RowCountQuery and ResultQuery extend Publisher, which allows for reactive streams implementations to subscribe to the results of a jOOQ query. Simply embed your query in the stream, e.g. using Flux.from(query). See also: https://www.jooq.org/doc/latest/manual/sql-execution/fetching/reactive-fetching/");
        }
        return result;
    }

    @Override // org.jooq.Query
    public final CompletionStage<Integer> executeAsync() {
        return executeAsync(Tools.configuration(this).executorProvider().provide());
    }

    @Override // org.jooq.Query
    public final CompletionStage<Integer> executeAsync(Executor executor) {
        return ExecutorProviderCompletionStage.of(CompletableFuture.supplyAsync(Tools.blocking(this::execute), executor), () -> {
            return executor;
        });
    }

    protected boolean keepResultSet() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void prepare(ExecuteContext ctx) throws SQLException {
        if (ctx.statement() == null) {
            ctx.statement(ctx.connection().prepareStatement(ctx.sql()));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final PreparedStatement executeImmediate(PreparedStatement s) throws SQLException {
        if (DefaultUnwrapperProvider.DefaultUnwrapper.isWrapperFor(s, BatchedPreparedStatement.class)) {
            ((BatchedPreparedStatement) s.unwrap(BatchedPreparedStatement.class)).setExecuteImmediate(true);
        }
        return s;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int execute(ExecuteContext ctx, ExecuteListener listener) throws SQLException {
        int result = 0;
        PreparedStatement stmt = ctx.statement();
        try {
            listener.executeStart(ctx);
            if (!stmt.execute()) {
                result = stmt.getUpdateCount();
                ctx.rows(result);
            }
            listener.executeEnd(ctx);
            return result;
        } catch (SQLException e) {
            Tools.consumeExceptions(ctx.configuration(), stmt, e);
            if (ctx.settings().getThrowExceptions() != ThrowExceptions.THROW_NONE) {
                throw e;
            }
            return stmt.getUpdateCount();
        }
    }

    @Override // org.jooq.Query
    public boolean isExecutable() {
        return true;
    }

    private static final DefaultRenderContext.Rendered getSQL0(DefaultExecuteContext ctx) {
        DefaultRenderContext.Rendered result;
        Configuration c = ctx.originalConfiguration();
        if (ctx.type() == ExecuteType.DDL) {
            ctx.data(Tools.BooleanDataKey.DATA_FORCE_STATIC_STATEMENT, true);
            DefaultRenderContext render = new DefaultRenderContext(c, ctx);
            result = new DefaultRenderContext.Rendered(render.paramType(ParamType.INLINED).visit(ctx.query()).render(), null, render.skipUpdateCounts());
        } else if (SettingsTools.executePreparedStatements(c.settings())) {
            try {
                DefaultRenderContext render2 = new DefaultRenderContext(c, ctx);
                render2.data(Tools.BooleanDataKey.DATA_COUNT_BIND_VALUES, true);
                result = new DefaultRenderContext.Rendered(render2.visit(ctx.query()).render(), render2.bindValues(), render2.skipUpdateCounts());
            } catch (DefaultRenderContext.ForceInlineSignal e) {
                ctx.data(Tools.BooleanDataKey.DATA_FORCE_STATIC_STATEMENT, true);
                DefaultRenderContext render3 = new DefaultRenderContext(c, ctx);
                result = new DefaultRenderContext.Rendered(render3.paramType(ParamType.INLINED).visit(ctx.query()).render(), null, render3.skipUpdateCounts());
            }
        } else {
            DefaultRenderContext render4 = new DefaultRenderContext(c, ctx);
            result = new DefaultRenderContext.Rendered(render4.paramType(ParamType.INLINED).visit(ctx.query()).render(), null, render4.skipUpdateCounts());
        }
        return result;
    }
}
