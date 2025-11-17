package org.jooq.impl;

import io.r2dbc.spi.ConnectionFactory;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.jooq.AlterDatabaseStep;
import org.jooq.AlterDomainStep;
import org.jooq.AlterIndexOnStep;
import org.jooq.AlterSchemaStep;
import org.jooq.AlterSequenceStep;
import org.jooq.AlterTableStep;
import org.jooq.AlterTypeStep;
import org.jooq.AlterViewStep;
import org.jooq.Attachable;
import org.jooq.Batch;
import org.jooq.BatchBindStep;
import org.jooq.BatchedCallable;
import org.jooq.BatchedRunnable;
import org.jooq.BindContext;
import org.jooq.Block;
import org.jooq.Catalog;
import org.jooq.CommentOnIsStep;
import org.jooq.CommonTableExpression;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.ConnectionCallable;
import org.jooq.ConnectionProvider;
import org.jooq.ConnectionRunnable;
import org.jooq.ContextTransactionalCallable;
import org.jooq.ContextTransactionalRunnable;
import org.jooq.CreateDatabaseFinalStep;
import org.jooq.CreateDomainAsStep;
import org.jooq.CreateIndexStep;
import org.jooq.CreateSchemaFinalStep;
import org.jooq.CreateSequenceFlagsStep;
import org.jooq.CreateTableElementListStep;
import org.jooq.CreateTypeStep;
import org.jooq.CreateViewAsStep;
import org.jooq.Cursor;
import org.jooq.DDLExportConfiguration;
import org.jooq.DDLFlag;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.DeleteQuery;
import org.jooq.DeleteUsingStep;
import org.jooq.Domain;
import org.jooq.DropDatabaseFinalStep;
import org.jooq.DropDomainCascadeStep;
import org.jooq.DropIndexOnStep;
import org.jooq.DropSchemaStep;
import org.jooq.DropSequenceFinalStep;
import org.jooq.DropTableStep;
import org.jooq.DropTypeStep;
import org.jooq.DropViewFinalStep;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Explain;
import org.jooq.Field;
import org.jooq.GrantOnStep;
import org.jooq.Index;
import org.jooq.InsertQuery;
import org.jooq.InsertSetStep;
import org.jooq.InsertValuesStep1;
import org.jooq.InsertValuesStep10;
import org.jooq.InsertValuesStep11;
import org.jooq.InsertValuesStep12;
import org.jooq.InsertValuesStep13;
import org.jooq.InsertValuesStep14;
import org.jooq.InsertValuesStep15;
import org.jooq.InsertValuesStep16;
import org.jooq.InsertValuesStep17;
import org.jooq.InsertValuesStep18;
import org.jooq.InsertValuesStep19;
import org.jooq.InsertValuesStep2;
import org.jooq.InsertValuesStep20;
import org.jooq.InsertValuesStep21;
import org.jooq.InsertValuesStep22;
import org.jooq.InsertValuesStep3;
import org.jooq.InsertValuesStep4;
import org.jooq.InsertValuesStep5;
import org.jooq.InsertValuesStep6;
import org.jooq.InsertValuesStep7;
import org.jooq.InsertValuesStep8;
import org.jooq.InsertValuesStep9;
import org.jooq.InsertValuesStepN;
import org.jooq.LoaderOptionsStep;
import org.jooq.MergeKeyStep1;
import org.jooq.MergeKeyStep10;
import org.jooq.MergeKeyStep11;
import org.jooq.MergeKeyStep12;
import org.jooq.MergeKeyStep13;
import org.jooq.MergeKeyStep14;
import org.jooq.MergeKeyStep15;
import org.jooq.MergeKeyStep16;
import org.jooq.MergeKeyStep17;
import org.jooq.MergeKeyStep18;
import org.jooq.MergeKeyStep19;
import org.jooq.MergeKeyStep2;
import org.jooq.MergeKeyStep20;
import org.jooq.MergeKeyStep21;
import org.jooq.MergeKeyStep22;
import org.jooq.MergeKeyStep3;
import org.jooq.MergeKeyStep4;
import org.jooq.MergeKeyStep5;
import org.jooq.MergeKeyStep6;
import org.jooq.MergeKeyStep7;
import org.jooq.MergeKeyStep8;
import org.jooq.MergeKeyStep9;
import org.jooq.MergeKeyStepN;
import org.jooq.MergeUsingStep;
import org.jooq.Meta;
import org.jooq.Migrations;
import org.jooq.Name;
import org.jooq.Param;
import org.jooq.Parser;
import org.jooq.Privilege;
import org.jooq.Publisher;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Record11;
import org.jooq.Record12;
import org.jooq.Record13;
import org.jooq.Record14;
import org.jooq.Record15;
import org.jooq.Record16;
import org.jooq.Record17;
import org.jooq.Record18;
import org.jooq.Record19;
import org.jooq.Record2;
import org.jooq.Record20;
import org.jooq.Record21;
import org.jooq.Record22;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Record9;
import org.jooq.RecordQualifier;
import org.jooq.Records;
import org.jooq.RenderContext;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Results;
import org.jooq.RevokeOnStep;
import org.jooq.RollbackToSavepointStep;
import org.jooq.RowCountQuery;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectQuery;
import org.jooq.SelectSelectStep;
import org.jooq.SelectWhereStep;
import org.jooq.Sequence;
import org.jooq.Source;
import org.jooq.Statement;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableLike;
import org.jooq.TableRecord;
import org.jooq.TransactionProvider;
import org.jooq.TransactionalCallable;
import org.jooq.TransactionalPublishable;
import org.jooq.TransactionalRunnable;
import org.jooq.TruncateIdentityStep;
import org.jooq.Type;
import org.jooq.UDT;
import org.jooq.UDTRecord;
import org.jooq.UpdatableRecord;
import org.jooq.UpdateQuery;
import org.jooq.UpdateSetFirstStep;
import org.jooq.WithAsStep;
import org.jooq.WithAsStep1;
import org.jooq.WithAsStep10;
import org.jooq.WithAsStep11;
import org.jooq.WithAsStep12;
import org.jooq.WithAsStep13;
import org.jooq.WithAsStep14;
import org.jooq.WithAsStep15;
import org.jooq.WithAsStep16;
import org.jooq.WithAsStep17;
import org.jooq.WithAsStep18;
import org.jooq.WithAsStep19;
import org.jooq.WithAsStep2;
import org.jooq.WithAsStep20;
import org.jooq.WithAsStep21;
import org.jooq.WithAsStep22;
import org.jooq.WithAsStep3;
import org.jooq.WithAsStep4;
import org.jooq.WithAsStep5;
import org.jooq.WithAsStep6;
import org.jooq.WithAsStep7;
import org.jooq.WithAsStep8;
import org.jooq.WithAsStep9;
import org.jooq.WithStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.exception.ConfigurationException;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DetachedException;
import org.jooq.exception.InvalidResultException;
import org.jooq.exception.SQLDialectNotSupportedException;
import org.jooq.impl.BatchCRUD;
import org.jooq.impl.R2DBC;
import org.jooq.tools.csv.CSVReader;
import org.jooq.tools.jdbc.BatchedConnection;
import org.jooq.tools.jdbc.MockCallable;
import org.jooq.tools.jdbc.MockConfiguration;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockRunnable;
import org.jooq.util.xml.jaxb.InformationSchema;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultDSLContext.class */
public class DefaultDSLContext extends AbstractScope implements DSLContext, Serializable {
    public DefaultDSLContext(SQLDialect dialect) {
        this(dialect, (Settings) null);
    }

    public DefaultDSLContext(SQLDialect dialect, Settings settings) {
        this(new DefaultConfiguration(new NoConnectionProvider(), dialect, settings));
    }

    public DefaultDSLContext(Connection connection, SQLDialect dialect) {
        this(connection, dialect, (Settings) null);
    }

    public DefaultDSLContext(Connection connection, SQLDialect dialect, Settings settings) {
        this(new DefaultConfiguration(new DefaultConnectionProvider(connection), dialect, settings));
    }

    public DefaultDSLContext(DataSource datasource, SQLDialect dialect) {
        this(datasource, dialect, (Settings) null);
    }

    public DefaultDSLContext(DataSource datasource, SQLDialect dialect, Settings settings) {
        this(new DefaultConfiguration(new DataSourceConnectionProvider(datasource), dialect, settings));
    }

    public DefaultDSLContext(ConnectionProvider connectionProvider, SQLDialect dialect) {
        this(connectionProvider, dialect, (Settings) null);
    }

    public DefaultDSLContext(ConnectionProvider connectionProvider, SQLDialect dialect, Settings settings) {
        this(new DefaultConfiguration(connectionProvider, dialect, settings));
    }

    public DefaultDSLContext(ConnectionFactory connectionFactory, SQLDialect dialect) {
        this(connectionFactory, dialect, (Settings) null);
    }

    public DefaultDSLContext(ConnectionFactory connectionFactory, SQLDialect dialect, Settings settings) {
        this(new DefaultConfiguration().set(connectionFactory).set(dialect).set(settings));
    }

    public DefaultDSLContext(Configuration configuration) {
        super(configuration, configuration == null ? null : configuration.data());
    }

    @Override // org.jooq.DSLContext
    public Schema map(Schema schema) {
        return Tools.getMappedSchema(this, schema);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Table<R> map(Table<R> table) {
        return Tools.getMappedTable(this, table);
    }

    @Override // org.jooq.DSLContext
    public Parser parser() {
        return new ParserImpl(configuration());
    }

    @Override // org.jooq.DSLContext
    public Connection parsingConnection() {
        return new ParsingConnection(configuration());
    }

    @Override // org.jooq.DSLContext
    public DataSource parsingDataSource() {
        return new ParsingDataSource(configuration());
    }

    @Override // org.jooq.DSLContext
    public ConnectionFactory parsingConnectionFactory() {
        return new ParsingConnectionFactory(configuration());
    }

    @Override // org.jooq.DSLContext
    public Connection diagnosticsConnection() {
        return new DiagnosticsConnection(configuration());
    }

    @Override // org.jooq.DSLContext
    public DataSource diagnosticsDataSource() {
        return new DiagnosticsDataSource(configuration());
    }

    @Override // org.jooq.DSLContext
    public Migrations migrations() {
        return new MigrationsImpl(configuration());
    }

    @Override // org.jooq.DSLContext
    public Meta meta() {
        return configuration().metaProvider().provide();
    }

    @Override // org.jooq.DSLContext
    public Meta meta(DatabaseMetaData meta) {
        return new MetaImpl(configuration(), meta);
    }

    @Override // org.jooq.DSLContext
    public Meta meta(Catalog... catalogs) {
        return CatalogMetaImpl.filterCatalogs(configuration(), catalogs);
    }

    @Override // org.jooq.DSLContext
    public Meta meta(Schema... schemas) {
        return CatalogMetaImpl.filterSchemas(configuration(), schemas);
    }

    @Override // org.jooq.DSLContext
    public Meta meta(Table<?>... tables) {
        return CatalogMetaImpl.filterTables(configuration(), tables);
    }

    @Override // org.jooq.DSLContext
    public Meta meta(InformationSchema schema) {
        return new InformationSchemaMetaImpl(configuration(), schema);
    }

    @Override // org.jooq.DSLContext
    public Meta meta(String... sources) {
        return new SourceMetaProvider(configuration(), (Source[]) Tools.map(sources, s -> {
            return Source.of(s);
        }, x$0 -> {
            return new Source[x$0];
        })).provide();
    }

    @Override // org.jooq.DSLContext
    public Meta meta(Source... sources) {
        return new SourceMetaProvider(configuration(), sources).provide();
    }

    @Override // org.jooq.DSLContext
    public Meta meta(Query... queries) {
        return new InterpreterMetaProvider(configuration(), queries).provide();
    }

    @Override // org.jooq.DSLContext
    public InformationSchema informationSchema(Catalog catalog) {
        return InformationSchemaExport.exportCatalogs(configuration(), Arrays.asList(catalog));
    }

    @Override // org.jooq.DSLContext
    public InformationSchema informationSchema(Catalog... catalogs) {
        return InformationSchemaExport.exportCatalogs(configuration(), Arrays.asList(catalogs));
    }

    @Override // org.jooq.DSLContext
    public InformationSchema informationSchema(Schema schema) {
        return InformationSchemaExport.exportSchemas(configuration(), Arrays.asList(schema));
    }

    @Override // org.jooq.DSLContext
    public InformationSchema informationSchema(Schema... schemas) {
        return InformationSchemaExport.exportSchemas(configuration(), Arrays.asList(schemas));
    }

    @Override // org.jooq.DSLContext
    public InformationSchema informationSchema(Table<?> table) {
        return InformationSchemaExport.exportTables(configuration(), Arrays.asList(table));
    }

    @Override // org.jooq.DSLContext
    public InformationSchema informationSchema(Table<?>... tables) {
        return InformationSchemaExport.exportTables(configuration(), Arrays.asList(tables));
    }

    @Override // org.jooq.DSLContext
    public Explain explain(Query query) {
        return ExplainQuery.explain(this, query);
    }

    @Override // org.jooq.DSLContext
    public <T> T transactionResult(ContextTransactionalCallable<T> contextTransactionalCallable) {
        TransactionProvider transactionProvider = configuration().transactionProvider();
        if (!(transactionProvider instanceof ThreadLocalTransactionProvider)) {
            throw new ConfigurationException("Cannot use ContextTransactionalCallable with TransactionProvider of type " + String.valueOf(transactionProvider.getClass()));
        }
        return (T) transactionResult0(c -> {
            return contextTransactionalCallable.run();
        }, ((ThreadLocalTransactionProvider) transactionProvider).configuration(configuration()), true);
    }

    @Override // org.jooq.DSLContext
    public <T> T transactionResult(TransactionalCallable<T> transactionalCallable) {
        return (T) transactionResult0(transactionalCallable, configuration(), false);
    }

    private static <T> T transactionResult0(TransactionalCallable<T> transactionalCallable, Configuration configuration, boolean z) {
        return (T) Tools.blocking(() -> {
            String str;
            DefaultTransactionContext ctx = new DefaultTransactionContext(configuration.derive());
            TransactionProvider provider = ctx.configuration().transactionProvider();
            TransactionListeners listeners = new TransactionListeners(ctx.configuration());
            boolean committed = false;
            try {
                try {
                    listeners.beginStart(ctx);
                    provider.begin(ctx);
                    listeners.beginEnd(ctx);
                    Object run = transactionalCallable.run(ctx.configuration());
                    try {
                        listeners.commitStart(ctx);
                        provider.commit(ctx);
                        committed = true;
                        listeners.commitEnd(ctx);
                        return run;
                    } catch (Throwable th) {
                        listeners.commitEnd(ctx);
                        throw th;
                    }
                } catch (Throwable th2) {
                    listeners.beginEnd(ctx);
                    throw th2;
                }
            } catch (Throwable cause) {
                if (!committed) {
                    if (cause instanceof Exception) {
                        Exception e = (Exception) cause;
                        ctx.cause(e);
                    } else {
                        ctx.causeThrowable(cause);
                    }
                    listeners.rollbackStart(ctx);
                    try {
                        provider.rollback(ctx);
                    } catch (Exception suppress) {
                        cause.addSuppressed(suppress);
                    }
                    listeners.rollbackEnd(ctx);
                }
                if (cause instanceof RuntimeException) {
                    RuntimeException e2 = (RuntimeException) cause;
                    throw e2;
                }
                if (cause instanceof Error) {
                    Error e3 = (Error) cause;
                    throw e3;
                }
                if (committed) {
                    str = "Exception after commit";
                } else {
                    str = "Rollback caused";
                }
                throw new DataAccessException(str, cause);
            }
        }, z).get();
    }

    @Override // org.jooq.DSLContext
    public void transaction(ContextTransactionalRunnable transactional) {
        transactionResult(() -> {
            transactional.run();
            return null;
        });
    }

    @Override // org.jooq.DSLContext
    public void transaction(TransactionalRunnable transactional) {
        transactionResult(c -> {
            transactional.run(c);
            return null;
        });
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Void> transactionAsync(TransactionalRunnable transactional) {
        return transactionAsync(Tools.configuration(configuration()).executorProvider().provide(), transactional);
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Void> transactionAsync(Executor executor, TransactionalRunnable transactional) {
        if (configuration().transactionProvider() instanceof ThreadLocalTransactionProvider) {
            throw new ConfigurationException("Cannot use TransactionalRunnable with ThreadLocalTransactionProvider");
        }
        return ExecutorProviderCompletionStage.of(CompletableFuture.supplyAsync(() -> {
            transaction(transactional);
            return null;
        }, executor), () -> {
            return executor;
        });
    }

    @Override // org.jooq.DSLContext
    public <T> CompletionStage<T> transactionResultAsync(TransactionalCallable<T> transactional) {
        return transactionResultAsync(Tools.configuration(configuration()).executorProvider().provide(), transactional);
    }

    @Override // org.jooq.DSLContext
    public <T> CompletionStage<T> transactionResultAsync(Executor executor, TransactionalCallable<T> transactional) {
        if (configuration().transactionProvider() instanceof ThreadLocalTransactionProvider) {
            throw new ConfigurationException("Cannot use TransactionalCallable with ThreadLocalTransactionProvider");
        }
        return ExecutorProviderCompletionStage.of(CompletableFuture.supplyAsync(() -> {
            return transactionResult(transactional);
        }, executor), () -> {
            return executor;
        });
    }

    @Override // org.jooq.DSLContext
    public <T> Publisher<T> transactionPublisher(TransactionalPublishable<T> transactional) {
        return subscriber -> {
            ConnectionFactory cf = configuration().connectionFactory();
            if (!(cf instanceof NoConnectionFactory)) {
                subscriber.onSubscribe(new R2DBC.TransactionSubscription(this, subscriber, transactional));
            } else {
                subscriber.onSubscribe(new R2DBC.BlockingTransactionSubscription(this, subscriber, transactional));
            }
        };
    }

    @Override // org.jooq.DSLContext
    public <T> T connectionResult(ConnectionCallable<T> connectionCallable) {
        if (data("org.jooq.workaround.issue13827") != null) {
            return (T) connectionResult0(connectionCallable);
        }
        Connection acquire = configuration().connectionProvider().acquire();
        try {
            if (acquire == null) {
                throw new DetachedException("No JDBC Connection provided by ConnectionProvider");
            }
            try {
                T run = connectionCallable.run(acquire);
                configuration().connectionProvider().release(acquire);
                return run;
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable th) {
                throw new DataAccessException("Error while running ConnectionCallable", th);
            }
        } catch (Throwable th2) {
            configuration().connectionProvider().release(acquire);
            throw th2;
        }
    }

    private final <T> T connectionResult0(ConnectionCallable<T> connectionCallable) {
        DefaultExecuteContext defaultExecuteContext = new DefaultExecuteContext(configuration());
        Connection connection = defaultExecuteContext.connection();
        if (connection == null) {
            throw new DetachedException("No JDBC Connection provided by ConnectionProvider");
        }
        return (T) DefaultExecuteContext.localExecuteContext(defaultExecuteContext, () -> {
            try {
                try {
                    try {
                        Object run = connectionCallable.run(connection);
                        Tools.safeClose(new DefaultExecuteListener(), defaultExecuteContext);
                        return run;
                    } catch (Throwable t) {
                        throw new DataAccessException("Error while running ConnectionCallable", t);
                    }
                } catch (Error | RuntimeException e) {
                    throw e;
                }
            } catch (Throwable th) {
                Tools.safeClose(new DefaultExecuteListener(), defaultExecuteContext);
                throw th;
            }
        });
    }

    @Override // org.jooq.DSLContext
    public void connection(ConnectionRunnable runnable) {
        connectionResult(connection -> {
            runnable.run(connection);
            return null;
        });
    }

    @Override // org.jooq.DSLContext
    public <T> T mockResult(MockDataProvider provider, MockCallable<T> mockable) {
        try {
            return mockable.run(new MockConfiguration(this.configuration, provider));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception cause) {
            throw new DataAccessException("Mock failed", cause);
        }
    }

    @Override // org.jooq.DSLContext
    public void mock(MockDataProvider provider, MockRunnable mockable) {
        mockResult(provider, c -> {
            mockable.run(c);
            return null;
        });
    }

    @Override // org.jooq.DSLContext
    public RenderContext renderContext() {
        return new DefaultRenderContext(configuration(), (ExecuteContext) null);
    }

    @Override // org.jooq.DSLContext
    public String render(QueryPart part) {
        return renderContext().visit(part).render();
    }

    @Override // org.jooq.DSLContext
    public String renderNamedParams(QueryPart part) {
        return renderContext().paramType(ParamType.NAMED).visit(part).render();
    }

    @Override // org.jooq.DSLContext
    public String renderNamedOrInlinedParams(QueryPart part) {
        return renderContext().paramType(ParamType.NAMED_OR_INLINED).visit(part).render();
    }

    @Override // org.jooq.DSLContext
    public String renderInlined(QueryPart part) {
        return renderContext().paramType(ParamType.INLINED).visit(part).render();
    }

    @Override // org.jooq.DSLContext
    public List<Object> extractBindValues(QueryPart part) {
        ParamCollector collector = new ParamCollector(configuration(), false);
        collector.visit(part);
        return Collections.unmodifiableList(Tools.map(collector.resultList, e -> {
            return ((Param) e.getValue()).getValue();
        }));
    }

    @Override // org.jooq.DSLContext
    public Map<String, Param<?>> extractParams(QueryPart part) {
        ParamCollector collector = new ParamCollector(configuration(), true);
        collector.visit(part);
        return Collections.unmodifiableMap(collector.resultFlat);
    }

    @Override // org.jooq.DSLContext
    public Param<?> extractParam(QueryPart part, String name) {
        return extractParams(part).get(name);
    }

    @Override // org.jooq.DSLContext
    public BindContext bindContext(PreparedStatement stmt) {
        return new DefaultBindContext(configuration(), null, stmt);
    }

    @Override // org.jooq.DSLContext
    public void attach(Attachable... attachables) {
        attach(Arrays.asList(attachables));
    }

    @Override // org.jooq.DSLContext
    public void attach(Collection<? extends Attachable> attachables) {
        for (Attachable attachable : attachables) {
            attachable.attach(configuration());
        }
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> LoaderOptionsStep<R> loadInto(Table<R> table) {
        return new LoaderImpl(configuration(), table);
    }

    @Override // org.jooq.DSLContext
    public Queries queries(Query... queries) {
        return queries(Arrays.asList(queries));
    }

    @Override // org.jooq.DSLContext
    public Queries queries(Collection<? extends Query> queries) {
        return new QueriesImpl(configuration(), queries);
    }

    @Override // org.jooq.DSLContext
    public Block begin(Statement... statements) {
        return begin(Arrays.asList(statements));
    }

    @Override // org.jooq.DSLContext
    public Block begin(Collection<? extends Statement> statements) {
        return new BlockImpl(configuration(), statements, true);
    }

    @Override // org.jooq.DSLContext
    public RowCountQuery query(SQL sql) {
        return new SQLQuery(configuration(), sql);
    }

    @Override // org.jooq.DSLContext
    public RowCountQuery query(String sql) {
        return query(sql, new Object[0]);
    }

    @Override // org.jooq.DSLContext
    public RowCountQuery query(String sql, Object... bindings) {
        return query(DSL.sql(sql, bindings));
    }

    @Override // org.jooq.DSLContext
    public RowCountQuery query(String sql, QueryPart... parts) {
        return query(sql, (Object[]) parts);
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetch(SQL sql) {
        return resultQuery(sql).fetch();
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetch(String sql) {
        return resultQuery(sql).fetch();
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetch(String sql, Object... bindings) {
        return resultQuery(sql, bindings).fetch();
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetch(String sql, QueryPart... parts) {
        return resultQuery(sql, parts).fetch();
    }

    @Override // org.jooq.DSLContext
    public Cursor<Record> fetchLazy(SQL sql) {
        return resultQuery(sql).fetchLazy();
    }

    @Override // org.jooq.DSLContext
    public Cursor<Record> fetchLazy(String sql) {
        return resultQuery(sql).fetchLazy();
    }

    @Override // org.jooq.DSLContext
    public Cursor<Record> fetchLazy(String sql, Object... bindings) {
        return resultQuery(sql, bindings).fetchLazy();
    }

    @Override // org.jooq.DSLContext
    public Cursor<Record> fetchLazy(String sql, QueryPart... parts) {
        return resultQuery(sql, parts).fetchLazy();
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Result<Record>> fetchAsync(SQL sql) {
        return resultQuery(sql).fetchAsync();
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Result<Record>> fetchAsync(String sql) {
        return resultQuery(sql).fetchAsync();
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Result<Record>> fetchAsync(String sql, Object... bindings) {
        return resultQuery(sql, bindings).fetchAsync();
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Result<Record>> fetchAsync(String sql, QueryPart... parts) {
        return resultQuery(sql, parts).fetchAsync();
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Result<Record>> fetchAsync(Executor executor, SQL sql) {
        return resultQuery(sql).fetchAsync(executor);
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Result<Record>> fetchAsync(Executor executor, String sql) {
        return resultQuery(sql).fetchAsync(executor);
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Result<Record>> fetchAsync(Executor executor, String sql, Object... bindings) {
        return resultQuery(sql, bindings).fetchAsync(executor);
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Result<Record>> fetchAsync(Executor executor, String sql, QueryPart... parts) {
        return resultQuery(sql, parts).fetchAsync(executor);
    }

    @Override // org.jooq.DSLContext
    public Stream<Record> fetchStream(SQL sql) {
        return resultQuery(sql).stream();
    }

    @Override // org.jooq.DSLContext
    public Stream<Record> fetchStream(String sql) {
        return resultQuery(sql).stream();
    }

    @Override // org.jooq.DSLContext
    public Stream<Record> fetchStream(String sql, Object... bindings) {
        return resultQuery(sql, bindings).stream();
    }

    @Override // org.jooq.DSLContext
    public Stream<Record> fetchStream(String sql, QueryPart... parts) {
        return resultQuery(sql, parts).stream();
    }

    @Override // org.jooq.DSLContext
    public Results fetchMany(SQL sql) {
        return resultQuery(sql).fetchMany();
    }

    @Override // org.jooq.DSLContext
    public Results fetchMany(String sql) {
        return resultQuery(sql).fetchMany();
    }

    @Override // org.jooq.DSLContext
    public Results fetchMany(String sql, Object... bindings) {
        return resultQuery(sql, bindings).fetchMany();
    }

    @Override // org.jooq.DSLContext
    public Results fetchMany(String sql, QueryPart... parts) {
        return resultQuery(sql, parts).fetchMany();
    }

    @Override // org.jooq.DSLContext
    public Record fetchOne(SQL sql) {
        return resultQuery(sql).fetchOne();
    }

    @Override // org.jooq.DSLContext
    public Record fetchOne(String sql) {
        return resultQuery(sql).fetchOne();
    }

    @Override // org.jooq.DSLContext
    public Record fetchOne(String sql, Object... bindings) {
        return resultQuery(sql, bindings).fetchOne();
    }

    @Override // org.jooq.DSLContext
    public Record fetchOne(String sql, QueryPart... parts) {
        return resultQuery(sql, parts).fetchOne();
    }

    @Override // org.jooq.DSLContext
    public Record fetchSingle(SQL sql) {
        return resultQuery(sql).fetchSingle();
    }

    @Override // org.jooq.DSLContext
    public Record fetchSingle(String sql) {
        return resultQuery(sql).fetchSingle();
    }

    @Override // org.jooq.DSLContext
    public Record fetchSingle(String sql, Object... bindings) {
        return resultQuery(sql, bindings).fetchSingle();
    }

    @Override // org.jooq.DSLContext
    public Record fetchSingle(String sql, QueryPart... parts) {
        return resultQuery(sql, parts).fetchSingle();
    }

    @Override // org.jooq.DSLContext
    public Optional<Record> fetchOptional(SQL sql) {
        return Optional.ofNullable(fetchOne(sql));
    }

    @Override // org.jooq.DSLContext
    public Optional<Record> fetchOptional(String sql) {
        return Optional.ofNullable(fetchOne(sql));
    }

    @Override // org.jooq.DSLContext
    public Optional<Record> fetchOptional(String sql, Object... bindings) {
        return Optional.ofNullable(fetchOne(sql, bindings));
    }

    @Override // org.jooq.DSLContext
    public Optional<Record> fetchOptional(String sql, QueryPart... parts) {
        return Optional.ofNullable(fetchOne(sql, parts));
    }

    @Override // org.jooq.DSLContext
    public Object fetchValue(SQL sql) {
        return fetchValue(resultQuery(sql));
    }

    @Override // org.jooq.DSLContext
    public Object fetchValue(String sql) {
        return fetchValue(resultQuery(sql));
    }

    @Override // org.jooq.DSLContext
    public Object fetchValue(String sql, Object... bindings) {
        return fetchValue(resultQuery(sql, bindings));
    }

    @Override // org.jooq.DSLContext
    public Object fetchValue(String sql, QueryPart... parts) {
        return fetchValue(resultQuery(sql, parts));
    }

    @Override // org.jooq.DSLContext
    public Optional<?> fetchOptionalValue(SQL sql) {
        return Optional.ofNullable(fetchValue(sql));
    }

    @Override // org.jooq.DSLContext
    public Optional<?> fetchOptionalValue(String sql) {
        return Optional.ofNullable(fetchValue(sql));
    }

    @Override // org.jooq.DSLContext
    public Optional<?> fetchOptionalValue(String sql, Object... bindings) {
        return Optional.ofNullable(fetchValue(sql, bindings));
    }

    @Override // org.jooq.DSLContext
    public Optional<?> fetchOptionalValue(String sql, QueryPart... parts) {
        return Optional.ofNullable(fetchValue(sql, parts));
    }

    @Override // org.jooq.DSLContext
    public List<?> fetchValues(SQL sql) {
        return fetchValues(resultQuery(sql));
    }

    @Override // org.jooq.DSLContext
    public List<?> fetchValues(String sql) {
        return fetchValues(resultQuery(sql));
    }

    @Override // org.jooq.DSLContext
    public List<?> fetchValues(String sql, Object... bindings) {
        return fetchValues(resultQuery(sql, bindings));
    }

    @Override // org.jooq.DSLContext
    public List<?> fetchValues(String sql, QueryPart... parts) {
        return fetchValues(resultQuery(sql, parts));
    }

    @Override // org.jooq.DSLContext
    public int execute(SQL sql) {
        return query(sql).execute();
    }

    @Override // org.jooq.DSLContext
    public int execute(String sql) {
        return query(sql).execute();
    }

    @Override // org.jooq.DSLContext
    public int execute(String sql, Object... bindings) {
        return query(sql, bindings).execute();
    }

    @Override // org.jooq.DSLContext
    public int execute(String sql, QueryPart... parts) {
        return query(sql, (Object[]) parts).execute();
    }

    @Override // org.jooq.DSLContext
    public ResultQuery<Record> resultQuery(SQL sql) {
        return new SQLResultQuery(configuration(), sql);
    }

    @Override // org.jooq.DSLContext
    public ResultQuery<Record> resultQuery(String sql) {
        return resultQuery(sql, new Object[0]);
    }

    @Override // org.jooq.DSLContext
    public ResultQuery<Record> resultQuery(String sql, Object... bindings) {
        return resultQuery(DSL.sql(sql, bindings));
    }

    @Override // org.jooq.DSLContext
    public ResultQuery<Record> resultQuery(String sql, QueryPart... parts) {
        return resultQuery(sql, (Object[]) parts);
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetch(ResultSet rs) {
        return fetchLazy(rs).fetch();
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetch(ResultSet rs, Field<?>... fields) {
        return fetchLazy(rs, fields).fetch();
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetch(ResultSet rs, DataType<?>... types) {
        return fetchLazy(rs, types).fetch();
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetch(ResultSet rs, Class<?>... types) {
        return fetchLazy(rs, types).fetch();
    }

    @Override // org.jooq.DSLContext
    public Record fetchOne(ResultSet rs) {
        return Tools.fetchOne(fetchLazy(rs));
    }

    @Override // org.jooq.DSLContext
    public Record fetchOne(ResultSet rs, Field<?>... fields) {
        return Tools.fetchOne(fetchLazy(rs, fields));
    }

    @Override // org.jooq.DSLContext
    public Record fetchOne(ResultSet rs, DataType<?>... types) {
        return Tools.fetchOne(fetchLazy(rs, types));
    }

    @Override // org.jooq.DSLContext
    public Record fetchOne(ResultSet rs, Class<?>... types) {
        return Tools.fetchOne(fetchLazy(rs, types));
    }

    @Override // org.jooq.DSLContext
    public Record fetchSingle(ResultSet rs) {
        return Tools.fetchSingle(fetchLazy(rs));
    }

    @Override // org.jooq.DSLContext
    public Record fetchSingle(ResultSet rs, Field<?>... fields) {
        return Tools.fetchSingle(fetchLazy(rs, fields));
    }

    @Override // org.jooq.DSLContext
    public Record fetchSingle(ResultSet rs, DataType<?>... types) {
        return Tools.fetchSingle(fetchLazy(rs, types));
    }

    @Override // org.jooq.DSLContext
    public Record fetchSingle(ResultSet rs, Class<?>... types) {
        return Tools.fetchSingle(fetchLazy(rs, types));
    }

    @Override // org.jooq.DSLContext
    public Optional<Record> fetchOptional(ResultSet rs) {
        return Optional.ofNullable(fetchOne(rs));
    }

    @Override // org.jooq.DSLContext
    public Optional<Record> fetchOptional(ResultSet rs, Field<?>... fields) {
        return Optional.ofNullable(fetchOne(rs, fields));
    }

    @Override // org.jooq.DSLContext
    public Optional<Record> fetchOptional(ResultSet rs, DataType<?>... types) {
        return Optional.ofNullable(fetchOne(rs, types));
    }

    @Override // org.jooq.DSLContext
    public Optional<Record> fetchOptional(ResultSet rs, Class<?>... types) {
        return Optional.ofNullable(fetchOne(rs, types));
    }

    @Override // org.jooq.DSLContext
    public Object fetchValue(ResultSet rs) {
        return value1((Record1) fetchOne(rs));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T> T fetchValue(ResultSet resultSet, Field<T> field) {
        return (T) value1((Record1) fetchOne(resultSet, (Field<?>[]) new Field[]{field}));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T> T fetchValue(ResultSet resultSet, DataType<T> dataType) {
        return (T) value1((Record1) fetchOne(resultSet, (DataType<?>[]) new DataType[]{dataType}));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T> T fetchValue(ResultSet resultSet, Class<T> cls) {
        return (T) value1((Record1) fetchOne(resultSet, (Class<?>[]) new Class[]{cls}));
    }

    @Override // org.jooq.DSLContext
    public Optional<?> fetchOptionalValue(ResultSet rs) {
        return Optional.ofNullable(fetchValue(rs));
    }

    @Override // org.jooq.DSLContext
    public <T> Optional<T> fetchOptionalValue(ResultSet rs, Field<T> field) {
        return Optional.ofNullable(fetchValue(rs, field));
    }

    @Override // org.jooq.DSLContext
    public <T> Optional<T> fetchOptionalValue(ResultSet rs, DataType<T> type) {
        return Optional.ofNullable(fetchValue(rs, type));
    }

    @Override // org.jooq.DSLContext
    public <T> Optional<T> fetchOptionalValue(ResultSet rs, Class<T> type) {
        return Optional.ofNullable(fetchValue(rs, type));
    }

    @Override // org.jooq.DSLContext
    public List<?> fetchValues(ResultSet rs) {
        return fetch(rs).getValues(0);
    }

    @Override // org.jooq.DSLContext
    public <T> List<T> fetchValues(ResultSet rs, Field<T> field) {
        return fetch(rs).getValues(field);
    }

    @Override // org.jooq.DSLContext
    public <T> List<T> fetchValues(ResultSet resultSet, DataType<T> dataType) {
        return (List<T>) fetch(resultSet).getValues(0, dataType.getType());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T> List<T> fetchValues(ResultSet resultSet, Class<T> cls) {
        return (List<T>) fetch(resultSet).getValues(0, cls);
    }

    @Override // org.jooq.DSLContext
    public Cursor<Record> fetchLazy(ResultSet rs) {
        try {
            return fetchLazy(rs, new MetaDataFieldProvider(configuration(), rs.getMetaData()).getFields());
        } catch (SQLException e) {
            throw new DataAccessException("Error while accessing ResultSet meta data", e);
        }
    }

    @Override // org.jooq.DSLContext
    public Cursor<Record> fetchLazy(ResultSet rs, Field<?>... fields) {
        ExecuteContext ctx = new DefaultExecuteContext(configuration());
        ExecuteListener listener = ExecuteListeners.getAndStart(ctx);
        ctx.resultSet(rs);
        return new CursorImpl(ctx, listener, fields, null, false, true);
    }

    @Override // org.jooq.DSLContext
    public Cursor<Record> fetchLazy(ResultSet rs, DataType<?>... types) {
        try {
            Field<?>[] fields = new Field[types.length];
            ResultSetMetaData meta = rs.getMetaData();
            int columns = meta.getColumnCount();
            for (int i = 0; i < types.length && i < columns; i++) {
                fields[i] = DSL.field(meta.getColumnLabel(i + 1), types[i]);
            }
            return fetchLazy(rs, fields);
        } catch (SQLException e) {
            throw new DataAccessException("Error while accessing ResultSet meta data", e);
        }
    }

    @Override // org.jooq.DSLContext
    public Cursor<Record> fetchLazy(ResultSet rs, Class<?>... types) {
        return fetchLazy(rs, Tools.dataTypes(types));
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Result<Record>> fetchAsync(ResultSet rs) {
        return fetchAsync(Tools.configuration(configuration()).executorProvider().provide(), rs);
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Result<Record>> fetchAsync(ResultSet rs, Field<?>... fields) {
        return fetchAsync(Tools.configuration(configuration()).executorProvider().provide(), rs, fields);
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Result<Record>> fetchAsync(ResultSet rs, DataType<?>... types) {
        return fetchAsync(Tools.configuration(configuration()).executorProvider().provide(), rs, types);
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Result<Record>> fetchAsync(ResultSet rs, Class<?>... types) {
        return fetchAsync(Tools.configuration(configuration()).executorProvider().provide(), rs, types);
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Result<Record>> fetchAsync(Executor executor, ResultSet rs) {
        return ExecutorProviderCompletionStage.of(CompletableFuture.supplyAsync(Tools.blocking(() -> {
            return fetch(rs);
        }), executor), () -> {
            return executor;
        });
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Result<Record>> fetchAsync(Executor executor, ResultSet rs, Field<?>... fields) {
        return ExecutorProviderCompletionStage.of(CompletableFuture.supplyAsync(Tools.blocking(() -> {
            return fetch(rs, (Field<?>[]) fields);
        }), executor), () -> {
            return executor;
        });
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Result<Record>> fetchAsync(Executor executor, ResultSet rs, DataType<?>... types) {
        return ExecutorProviderCompletionStage.of(CompletableFuture.supplyAsync(Tools.blocking(() -> {
            return fetch(rs, (DataType<?>[]) types);
        }), executor), () -> {
            return executor;
        });
    }

    @Override // org.jooq.DSLContext
    public CompletionStage<Result<Record>> fetchAsync(Executor executor, ResultSet rs, Class<?>... types) {
        return ExecutorProviderCompletionStage.of(CompletableFuture.supplyAsync(Tools.blocking(() -> {
            return fetch(rs, (Class<?>[]) types);
        }), executor), () -> {
            return executor;
        });
    }

    @Override // org.jooq.DSLContext
    public Stream<Record> fetchStream(ResultSet rs) {
        return fetchLazy(rs).stream();
    }

    @Override // org.jooq.DSLContext
    public Stream<Record> fetchStream(ResultSet rs, Field<?>... fields) {
        return fetchLazy(rs, fields).stream();
    }

    @Override // org.jooq.DSLContext
    public Stream<Record> fetchStream(ResultSet rs, DataType<?>... types) {
        return fetchLazy(rs, types).stream();
    }

    @Override // org.jooq.DSLContext
    public Stream<Record> fetchStream(ResultSet rs, Class<?>... types) {
        return fetchLazy(rs, types).stream();
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetchFromTXT(String string) {
        return fetchFromTXT(string, "{null}");
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetchFromTXT(String string, String nullLiteral) {
        return fetchFromStringData(Tools.parseTXT(string, nullLiteral));
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetchFromHTML(String string) {
        return fetchFromStringData(Tools.parseHTML(string));
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetchFromCSV(String string) {
        return fetchFromCSV(string, true, ',');
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetchFromCSV(String string, char delimiter) {
        return fetchFromCSV(string, true, delimiter);
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetchFromCSV(String string, boolean header) {
        return fetchFromCSV(string, header, ',');
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetchFromCSV(String string, boolean header, char delimiter) {
        CSVReader reader = new CSVReader(new StringReader(string), delimiter);
        try {
            try {
                List<String[]> list = reader.readAll();
                return fetchFromStringData(list, header);
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e2) {
            throw new DataAccessException("Could not read the CSV string", e2);
        }
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetchFromJSON(String string) {
        return new JSONReader(this, null, null, false).read(string);
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetchFromXML(String string) {
        return new XMLHandler(this, null, null).read(string);
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetchFromStringData(String[]... strings) {
        return fetchFromStringData(Tools.list(strings), true);
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetchFromStringData(List<String[]> strings) {
        return fetchFromStringData(strings, true);
    }

    @Override // org.jooq.DSLContext
    public Result<Record> fetchFromStringData(List<String[]> strings, boolean header) {
        Field<?>[] fieldArr;
        if (strings.size() == 0) {
            return new ResultImpl(configuration(), (Field<?>[]) new Field[0]);
        }
        String[] firstRow = strings.get(0);
        int firstRowIndex = header ? 1 : 0;
        if (header) {
            fieldArr = (Field[]) Tools.map(firstRow, s -> {
                return DSL.field(DSL.name(s), String.class);
            }, x$0 -> {
                return new Field[x$0];
            });
        } else {
            fieldArr = (Field[]) Tools.map(firstRow, (s2, i) -> {
                return DSL.field(DSL.name("COL" + (i + 1)), String.class);
            }, x$02 -> {
                return new Field[x$02];
            });
        }
        Field<?>[] fields = fieldArr;
        AbstractRow row = Tools.row0(fields);
        Result<Record> result = new ResultImpl<>(configuration(), row);
        if (strings.size() > firstRowIndex) {
            for (String[] values : strings.subList(firstRowIndex, strings.size())) {
                RecordImplN record = new RecordImplN((AbstractRow<?>) row);
                for (int i2 = 0; i2 < Math.min(values.length, fields.length); i2++) {
                    record.values[i2] = values[i2];
                    record.originals[i2] = values[i2];
                }
                result.add(record);
            }
        }
        return result;
    }

    @Override // org.jooq.DSLContext
    public WithAsStep with(String alias) {
        return new WithImpl(configuration(), false).with(alias);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep with(String alias, String... fieldAliases) {
        return new WithImpl(configuration(), false).with(alias, fieldAliases);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep with(String alias, Collection<String> fieldAliases) {
        return new WithImpl(configuration(), false).with(alias, fieldAliases);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep with(Name alias) {
        return new WithImpl(configuration(), false).with(alias);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep with(Name alias, Name... fieldAliases) {
        return new WithImpl(configuration(), false).with(alias, fieldAliases);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep with(Name alias, Collection<? extends Name> fieldAliases) {
        return new WithImpl(configuration(), false).with(alias, fieldAliases);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep with(String alias, java.util.function.Function<? super Field<?>, ? extends String> fieldNameFunction) {
        return new WithImpl(configuration(), false).with(alias, fieldNameFunction);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep with(String alias, BiFunction<? super Field<?>, ? super Integer, ? extends String> fieldNameFunction) {
        return new WithImpl(configuration(), false).with(alias, fieldNameFunction);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep1 with(String alias, String fieldAlias1) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep2 with(String alias, String fieldAlias1, String fieldAlias2) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep3 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep4 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep5 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep6 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep7 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep8 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep9 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep10 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep11 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep12 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep13 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep14 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep15 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep16 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep17 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep18 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep19 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep20 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep21 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20, String fieldAlias21) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep22 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20, String fieldAlias21, String fieldAlias22) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21, fieldAlias22);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep1 with(Name alias, Name fieldAlias1) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep2 with(Name alias, Name fieldAlias1, Name fieldAlias2) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep3 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep4 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep5 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep6 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep7 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep8 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep9 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep10 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep11 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep12 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep13 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep14 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep15 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep16 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep17 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep18 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep19 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep20 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep21 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20, Name fieldAlias21) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep22 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20, Name fieldAlias21, Name fieldAlias22) {
        return new WithImpl(configuration(), false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21, fieldAlias22);
    }

    @Override // org.jooq.DSLContext
    public WithStep with(CommonTableExpression<?>... tables) {
        return new WithImpl(configuration(), false).with(tables);
    }

    @Override // org.jooq.DSLContext
    public WithStep with(Collection<? extends CommonTableExpression<?>> tables) {
        return new WithImpl(configuration(), false).with(tables);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep withRecursive(String alias) {
        return new WithImpl(configuration(), true).with(alias);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep withRecursive(String alias, String... fieldAliases) {
        return new WithImpl(configuration(), true).with(alias, fieldAliases);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep withRecursive(String alias, Collection<String> fieldAliases) {
        return new WithImpl(configuration(), true).with(alias, fieldAliases);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep withRecursive(Name alias) {
        return new WithImpl(configuration(), true).with(alias);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep withRecursive(Name alias, Name... fieldAliases) {
        return new WithImpl(configuration(), true).with(alias, fieldAliases);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep withRecursive(Name alias, Collection<? extends Name> fieldAliases) {
        return new WithImpl(configuration(), true).with(alias, fieldAliases);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep withRecursive(String alias, java.util.function.Function<? super Field<?>, ? extends String> fieldNameFunction) {
        return new WithImpl(configuration(), true).with(alias, fieldNameFunction);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep withRecursive(String alias, BiFunction<? super Field<?>, ? super Integer, ? extends String> fieldNameFunction) {
        return new WithImpl(configuration(), true).with(alias, fieldNameFunction);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep1 withRecursive(String alias, String fieldAlias1) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep2 withRecursive(String alias, String fieldAlias1, String fieldAlias2) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep3 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep4 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep5 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep6 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep7 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep8 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep9 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep10 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep11 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep12 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep13 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep14 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep15 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep16 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep17 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep18 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep19 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep20 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep21 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20, String fieldAlias21) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep22 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20, String fieldAlias21, String fieldAlias22) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21, fieldAlias22);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep1 withRecursive(Name alias, Name fieldAlias1) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep2 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep3 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep4 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep5 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep6 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep7 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep8 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep9 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep10 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep11 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep12 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep13 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep14 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep15 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep16 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep17 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep18 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep19 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep20 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep21 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20, Name fieldAlias21) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21);
    }

    @Override // org.jooq.DSLContext
    public WithAsStep22 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20, Name fieldAlias21, Name fieldAlias22) {
        return new WithImpl(configuration(), true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21, fieldAlias22);
    }

    @Override // org.jooq.DSLContext
    public WithStep withRecursive(CommonTableExpression<?>... tables) {
        return new WithImpl(configuration(), true).with(tables);
    }

    @Override // org.jooq.DSLContext
    public WithStep withRecursive(Collection<? extends CommonTableExpression<?>> tables) {
        return new WithImpl(configuration(), true).with(tables);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> SelectWhereStep<R> selectFrom(TableLike<R> table) {
        return new SelectImpl(configuration(), null).from((TableLike<?>) table);
    }

    @Override // org.jooq.DSLContext
    public SelectWhereStep<Record> selectFrom(Name table) {
        return new SelectImpl(configuration(), null).from(table);
    }

    @Override // org.jooq.DSLContext
    public SelectWhereStep<Record> selectFrom(SQL sql) {
        return new SelectImpl(configuration(), null).from(sql);
    }

    @Override // org.jooq.DSLContext
    public SelectWhereStep<Record> selectFrom(String sql) {
        return new SelectImpl(configuration(), null).from(sql);
    }

    @Override // org.jooq.DSLContext
    public SelectWhereStep<Record> selectFrom(String sql, Object... bindings) {
        return new SelectImpl(configuration(), null).from(sql, bindings);
    }

    @Override // org.jooq.DSLContext
    public SelectWhereStep<Record> selectFrom(String sql, QueryPart... parts) {
        return new SelectImpl(configuration(), null).from(sql, parts);
    }

    @Override // org.jooq.DSLContext
    public SelectSelectStep<Record> select(Collection<? extends SelectFieldOrAsterisk> fields) {
        return new SelectImpl(configuration(), null).select(fields);
    }

    @Override // org.jooq.DSLContext
    public SelectSelectStep<Record> select(SelectFieldOrAsterisk... fields) {
        return new SelectImpl(configuration(), null).select(fields);
    }

    @Override // org.jooq.DSLContext
    public <T1> SelectSelectStep<Record1<T1>> select(SelectField<T1> selectField) {
        return (SelectSelectStep<Record1<T1>>) select(selectField);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2> SelectSelectStep<Record2<T1, T2>> select(SelectField<T1> selectField, SelectField<T2> selectField2) {
        return (SelectSelectStep<Record2<T1, T2>>) select(selectField, selectField2);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3> SelectSelectStep<Record3<T1, T2, T3>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3) {
        return (SelectSelectStep<Record3<T1, T2, T3>>) select(selectField, selectField2, selectField3);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4> SelectSelectStep<Record4<T1, T2, T3, T4>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4) {
        return (SelectSelectStep<Record4<T1, T2, T3, T4>>) select(selectField, selectField2, selectField3, selectField4);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5> SelectSelectStep<Record5<T1, T2, T3, T4, T5>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5) {
        return (SelectSelectStep<Record5<T1, T2, T3, T4, T5>>) select(selectField, selectField2, selectField3, selectField4, selectField5);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6> SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6) {
        return (SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7> SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7) {
        return (SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8> SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8) {
        return (SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9) {
        return (SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10) {
        return (SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11) {
        return (SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12) {
        return (SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13) {
        return (SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14) {
        return (SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15) {
        return (SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16) {
        return (SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17) {
        return (SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18) {
        return (SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19) {
        return (SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20) {
        return (SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21) {
        return (SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21, SelectField<T22> selectField22) {
        return (SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21, selectField22);
    }

    @Override // org.jooq.DSLContext
    public SelectSelectStep<Record> selectDistinct(Collection<? extends SelectFieldOrAsterisk> fields) {
        return new SelectImpl(configuration(), null, true).select(fields);
    }

    @Override // org.jooq.DSLContext
    public SelectSelectStep<Record> selectDistinct(SelectFieldOrAsterisk... fields) {
        return new SelectImpl(configuration(), null, true).select(fields);
    }

    @Override // org.jooq.DSLContext
    public <T1> SelectSelectStep<Record1<T1>> selectDistinct(SelectField<T1> selectField) {
        return (SelectSelectStep<Record1<T1>>) selectDistinct(selectField);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2> SelectSelectStep<Record2<T1, T2>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2) {
        return (SelectSelectStep<Record2<T1, T2>>) selectDistinct(selectField, selectField2);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3> SelectSelectStep<Record3<T1, T2, T3>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3) {
        return (SelectSelectStep<Record3<T1, T2, T3>>) selectDistinct(selectField, selectField2, selectField3);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4> SelectSelectStep<Record4<T1, T2, T3, T4>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4) {
        return (SelectSelectStep<Record4<T1, T2, T3, T4>>) selectDistinct(selectField, selectField2, selectField3, selectField4);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5> SelectSelectStep<Record5<T1, T2, T3, T4, T5>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5) {
        return (SelectSelectStep<Record5<T1, T2, T3, T4, T5>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6> SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6) {
        return (SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7> SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7) {
        return (SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8> SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8) {
        return (SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9) {
        return (SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10) {
        return (SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11) {
        return (SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12) {
        return (SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13) {
        return (SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14) {
        return (SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15) {
        return (SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16) {
        return (SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17) {
        return (SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18) {
        return (SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19) {
        return (SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20) {
        return (SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21) {
        return (SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21);
    }

    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21, SelectField<T22> selectField22) {
        return (SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21, selectField22);
    }

    @Override // org.jooq.DSLContext
    public SelectSelectStep<Record1<Integer>> selectZero() {
        return new SelectImpl(configuration(), null).select(DSL.zero().as("zero"));
    }

    @Override // org.jooq.DSLContext
    public SelectSelectStep<Record1<Integer>> selectOne() {
        return new SelectImpl(configuration(), null).select(DSL.one().as("one"));
    }

    @Override // org.jooq.DSLContext
    public SelectSelectStep<Record1<Integer>> selectCount() {
        return new SelectImpl(configuration(), null).select(DSL.count());
    }

    @Override // org.jooq.DSLContext
    public SelectQuery<Record> selectQuery() {
        return new SelectQueryImpl(configuration(), null);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> SelectQuery<R> selectQuery(TableLike<R> table) {
        return new SelectQueryImpl(configuration(), (WithImpl) null, table);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> InsertQuery<R> insertQuery(Table<R> into) {
        return new InsertQueryImpl(configuration(), null, into);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> InsertSetStep<R> insertInto(Table<R> into) {
        return new InsertImpl(configuration(), null, into, Collections.emptyList());
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1> InsertValuesStep1<R, T1> insertInto(Table<R> into, Field<T1> field1) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2> InsertValuesStep2<R, T1, T2> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3> InsertValuesStep3<R, T1, T2, T3> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4> InsertValuesStep4<R, T1, T2, T3, T4> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5> InsertValuesStep5<R, T1, T2, T3, T4, T5> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6> InsertValuesStep6<R, T1, T2, T3, T4, T5, T6> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7> InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6, field7));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8> InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9> InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> InsertValuesStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> InsertValuesStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> InsertValuesStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> InsertValuesStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> InsertValuesStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> InsertValuesStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> InsertValuesStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> InsertValuesStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> InsertValuesStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> InsertValuesStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> InsertValuesStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> InsertValuesStepN<R> insertInto(Table<R> into, Field<?>... fields) {
        return new InsertImpl(configuration(), null, into, Arrays.asList(fields));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> InsertValuesStepN<R> insertInto(Table<R> into, Collection<? extends Field<?>> fields) {
        return new InsertImpl(configuration(), null, into, fields);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> UpdateQuery<R> updateQuery(Table<R> table) {
        return new UpdateQueryImpl(configuration(), null, table);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> UpdateSetFirstStep<R> update(Table<R> table) {
        return new UpdateImpl(configuration(), null, table);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> MergeUsingStep<R> mergeInto(Table<R> table) {
        return new MergeImpl(configuration(), null, table);
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1> MergeKeyStep1<R, T1> mergeInto(Table<R> table, Field<T1> field1) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2> MergeKeyStep2<R, T1, T2> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3> MergeKeyStep3<R, T1, T2, T3> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4> MergeKeyStep4<R, T1, T2, T3, T4> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5> MergeKeyStep5<R, T1, T2, T3, T4, T5> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6> MergeKeyStep6<R, T1, T2, T3, T4, T5, T6> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7> MergeKeyStep7<R, T1, T2, T3, T4, T5, T6, T7> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6, field7));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8> MergeKeyStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9> MergeKeyStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> MergeKeyStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> MergeKeyStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> MergeKeyStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> MergeKeyStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> MergeKeyStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> MergeKeyStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> MergeKeyStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> MergeKeyStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> MergeKeyStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> MergeKeyStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> MergeKeyStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> MergeKeyStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21));
    }

    @Override // org.jooq.DSLContext
    @Deprecated(forRemoval = true, since = "3.14")
    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> MergeKeyStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return new MergeImpl(configuration(), null, table, Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> MergeKeyStepN<R> mergeInto(Table<R> table, Field<?>... fields) {
        return mergeInto(table, Arrays.asList(fields));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> MergeKeyStepN<R> mergeInto(Table<R> table, Collection<? extends Field<?>> fields) {
        return new MergeImpl(configuration(), null, table, fields);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> DeleteQuery<R> deleteQuery(Table<R> table) {
        return new DeleteQueryImpl(configuration(), null, table);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> DeleteUsingStep<R> delete(Table<R> table) {
        return deleteFrom(table);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> DeleteUsingStep<R> deleteFrom(Table<R> table) {
        return new DeleteImpl(configuration(), null, table);
    }

    @Override // org.jooq.DSLContext
    public void batched(BatchedRunnable runnable) {
        batchedResult(c -> {
            runnable.run(c);
            return null;
        });
    }

    @Override // org.jooq.DSLContext
    public <T> T batchedResult(BatchedCallable<T> batchedCallable) {
        return (T) connectionResult(connection -> {
            BatchedConnection bc = new BatchedConnection(connection, SettingsTools.getBatchSize(settings()));
            try {
                Configuration c = configuration().derive(bc);
                try {
                    try {
                        Object run = batchedCallable.run(c);
                        bc.close();
                        return run;
                    } catch (Error | RuntimeException e) {
                        throw e;
                    }
                } catch (Throwable t) {
                    throw new DataAccessException("Error while running BatchedCallable", t);
                }
            } catch (Throwable th) {
                try {
                    bc.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        });
    }

    @Override // org.jooq.DSLContext
    public Batch batch(Query... queries) {
        return new BatchMultiple(configuration(), queries);
    }

    @Override // org.jooq.DSLContext
    public Batch batch(Queries queries) {
        return batch(queries.queries());
    }

    @Override // org.jooq.DSLContext
    public Batch batch(String... queries) {
        return batch((Query[]) Tools.map(queries, q -> {
            return query(q);
        }, x$0 -> {
            return new Query[x$0];
        }));
    }

    @Override // org.jooq.DSLContext
    public Batch batch(Collection<? extends Query> queries) {
        return batch((Query[]) queries.toArray(Tools.EMPTY_QUERY));
    }

    @Override // org.jooq.DSLContext
    public BatchBindStep batch(Query query) {
        return new BatchSingle(configuration(), query);
    }

    @Override // org.jooq.DSLContext
    public BatchBindStep batch(String sql) {
        return batch(query(sql));
    }

    @Override // org.jooq.DSLContext
    public Batch batch(Query query, Object[]... bindings) {
        return batch(query).bind(bindings);
    }

    @Override // org.jooq.DSLContext
    public Batch batch(String sql, Object[]... bindings) {
        return batch(query(sql), bindings);
    }

    @Override // org.jooq.DSLContext
    public Batch batchStore(UpdatableRecord<?>... records) {
        return new BatchCRUD(configuration(), BatchCRUD.Action.STORE, records);
    }

    @Override // org.jooq.DSLContext
    public Batch batchStore(Collection<? extends UpdatableRecord<?>> records) {
        return batchStore((UpdatableRecord<?>[]) records.toArray(Tools.EMPTY_UPDATABLE_RECORD));
    }

    @Override // org.jooq.DSLContext
    public Batch batchInsert(TableRecord<?>... records) {
        return new BatchCRUD(configuration(), BatchCRUD.Action.INSERT, records);
    }

    @Override // org.jooq.DSLContext
    public Batch batchInsert(Collection<? extends TableRecord<?>> records) {
        return batchInsert((TableRecord<?>[]) records.toArray(Tools.EMPTY_TABLE_RECORD));
    }

    @Override // org.jooq.DSLContext
    public Batch batchUpdate(UpdatableRecord<?>... records) {
        return new BatchCRUD(configuration(), BatchCRUD.Action.UPDATE, records);
    }

    @Override // org.jooq.DSLContext
    public Batch batchUpdate(Collection<? extends UpdatableRecord<?>> records) {
        return batchUpdate((UpdatableRecord<?>[]) records.toArray(Tools.EMPTY_UPDATABLE_RECORD));
    }

    @Override // org.jooq.DSLContext
    public Batch batchMerge(UpdatableRecord<?>... records) {
        return new BatchCRUD(configuration(), BatchCRUD.Action.MERGE, records);
    }

    @Override // org.jooq.DSLContext
    public Batch batchMerge(Collection<? extends UpdatableRecord<?>> records) {
        return batchMerge((UpdatableRecord<?>[]) records.toArray(Tools.EMPTY_UPDATABLE_RECORD));
    }

    @Override // org.jooq.DSLContext
    public Batch batchDelete(UpdatableRecord<?>... records) {
        return new BatchCRUD(configuration(), BatchCRUD.Action.DELETE, records);
    }

    @Override // org.jooq.DSLContext
    public Batch batchDelete(Collection<? extends UpdatableRecord<?>> records) {
        return batchDelete((UpdatableRecord<?>[]) records.toArray(Tools.EMPTY_UPDATABLE_RECORD));
    }

    @Override // org.jooq.DSLContext
    public AlterDatabaseStep alterDatabase(String database) {
        return new AlterDatabaseImpl(configuration(), DSL.catalog(DSL.name(database)), false);
    }

    @Override // org.jooq.DSLContext
    public AlterDatabaseStep alterDatabase(Name database) {
        return new AlterDatabaseImpl(configuration(), DSL.catalog(database), false);
    }

    @Override // org.jooq.DSLContext
    public AlterDatabaseStep alterDatabase(Catalog database) {
        return new AlterDatabaseImpl(configuration(), database, false);
    }

    @Override // org.jooq.DSLContext
    public AlterDatabaseStep alterDatabaseIfExists(String database) {
        return new AlterDatabaseImpl(configuration(), DSL.catalog(DSL.name(database)), true);
    }

    @Override // org.jooq.DSLContext
    public AlterDatabaseStep alterDatabaseIfExists(Name database) {
        return new AlterDatabaseImpl(configuration(), DSL.catalog(database), true);
    }

    @Override // org.jooq.DSLContext
    public AlterDatabaseStep alterDatabaseIfExists(Catalog database) {
        return new AlterDatabaseImpl(configuration(), database, true);
    }

    @Override // org.jooq.DSLContext
    public <T> AlterDomainStep<T> alterDomain(String domain) {
        return new AlterDomainImpl(configuration(), DSL.domain(DSL.name(domain)), false);
    }

    @Override // org.jooq.DSLContext
    public <T> AlterDomainStep<T> alterDomain(Name domain) {
        return new AlterDomainImpl(configuration(), DSL.domain(domain), false);
    }

    @Override // org.jooq.DSLContext
    public <T> AlterDomainStep<T> alterDomain(Domain<T> domain) {
        return new AlterDomainImpl(configuration(), domain, false);
    }

    @Override // org.jooq.DSLContext
    public <T> AlterDomainStep<T> alterDomainIfExists(String domain) {
        return new AlterDomainImpl(configuration(), DSL.domain(DSL.name(domain)), true);
    }

    @Override // org.jooq.DSLContext
    public <T> AlterDomainStep<T> alterDomainIfExists(Name domain) {
        return new AlterDomainImpl(configuration(), DSL.domain(domain), true);
    }

    @Override // org.jooq.DSLContext
    public <T> AlterDomainStep<T> alterDomainIfExists(Domain<T> domain) {
        return new AlterDomainImpl(configuration(), domain, true);
    }

    @Override // org.jooq.DSLContext
    public AlterIndexOnStep alterIndex(String index) {
        return new AlterIndexImpl(configuration(), DSL.index(DSL.name(index)), false);
    }

    @Override // org.jooq.DSLContext
    public AlterIndexOnStep alterIndex(Name index) {
        return new AlterIndexImpl(configuration(), DSL.index(index), false);
    }

    @Override // org.jooq.DSLContext
    public AlterIndexOnStep alterIndex(Index index) {
        return new AlterIndexImpl(configuration(), index, false);
    }

    @Override // org.jooq.DSLContext
    public AlterIndexOnStep alterIndexIfExists(String index) {
        return new AlterIndexImpl(configuration(), DSL.index(DSL.name(index)), true);
    }

    @Override // org.jooq.DSLContext
    public AlterIndexOnStep alterIndexIfExists(Name index) {
        return new AlterIndexImpl(configuration(), DSL.index(index), true);
    }

    @Override // org.jooq.DSLContext
    public AlterIndexOnStep alterIndexIfExists(Index index) {
        return new AlterIndexImpl(configuration(), index, true);
    }

    @Override // org.jooq.DSLContext
    public AlterSchemaStep alterSchema(String schema) {
        return new AlterSchemaImpl(configuration(), DSL.schema(DSL.name(schema)), false);
    }

    @Override // org.jooq.DSLContext
    public AlterSchemaStep alterSchema(Name schema) {
        return new AlterSchemaImpl(configuration(), DSL.schema(schema), false);
    }

    @Override // org.jooq.DSLContext
    public AlterSchemaStep alterSchema(Schema schema) {
        return new AlterSchemaImpl(configuration(), schema, false);
    }

    @Override // org.jooq.DSLContext
    public AlterSchemaStep alterSchemaIfExists(String schema) {
        return new AlterSchemaImpl(configuration(), DSL.schema(DSL.name(schema)), true);
    }

    @Override // org.jooq.DSLContext
    public AlterSchemaStep alterSchemaIfExists(Name schema) {
        return new AlterSchemaImpl(configuration(), DSL.schema(schema), true);
    }

    @Override // org.jooq.DSLContext
    public AlterSchemaStep alterSchemaIfExists(Schema schema) {
        return new AlterSchemaImpl(configuration(), schema, true);
    }

    @Override // org.jooq.DSLContext
    public AlterSequenceStep<Number> alterSequence(String sequence) {
        return new AlterSequenceImpl(configuration(), DSL.sequence(DSL.name(sequence)), false);
    }

    @Override // org.jooq.DSLContext
    public AlterSequenceStep<Number> alterSequence(Name sequence) {
        return new AlterSequenceImpl(configuration(), DSL.sequence(sequence), false);
    }

    @Override // org.jooq.DSLContext
    public <T extends Number> AlterSequenceStep<T> alterSequence(Sequence<T> sequence) {
        return new AlterSequenceImpl(configuration(), sequence, false);
    }

    @Override // org.jooq.DSLContext
    public AlterSequenceStep<Number> alterSequenceIfExists(String sequence) {
        return new AlterSequenceImpl(configuration(), DSL.sequence(DSL.name(sequence)), true);
    }

    @Override // org.jooq.DSLContext
    public AlterSequenceStep<Number> alterSequenceIfExists(Name sequence) {
        return new AlterSequenceImpl(configuration(), DSL.sequence(sequence), true);
    }

    @Override // org.jooq.DSLContext
    public <T extends Number> AlterSequenceStep<T> alterSequenceIfExists(Sequence<T> sequence) {
        return new AlterSequenceImpl(configuration(), sequence, true);
    }

    @Override // org.jooq.DSLContext
    public AlterTypeStep alterType(String type) {
        return new AlterTypeImpl(configuration(), DSL.name(type), false);
    }

    @Override // org.jooq.DSLContext
    public AlterTypeStep alterType(Name type) {
        return new AlterTypeImpl(configuration(), type, false);
    }

    @Override // org.jooq.DSLContext
    public AlterTypeStep alterTypeIfExists(String type) {
        return new AlterTypeImpl(configuration(), DSL.name(type), true);
    }

    @Override // org.jooq.DSLContext
    public AlterTypeStep alterTypeIfExists(Name type) {
        return new AlterTypeImpl(configuration(), type, true);
    }

    @Override // org.jooq.DSLContext
    public AlterViewStep alterView(String view) {
        return new AlterViewImpl(configuration(), DSL.table(DSL.name(view)), null, false, false);
    }

    @Override // org.jooq.DSLContext
    public AlterViewStep alterView(Name view) {
        return new AlterViewImpl(configuration(), DSL.table(view), null, false, false);
    }

    @Override // org.jooq.DSLContext
    public AlterViewStep alterView(Table<?> view) {
        return new AlterViewImpl(configuration(), view, null, false, false);
    }

    @Override // org.jooq.DSLContext
    public AlterViewStep alterViewIfExists(String view) {
        return new AlterViewImpl(configuration(), DSL.table(DSL.name(view)), null, false, true);
    }

    @Override // org.jooq.DSLContext
    public AlterViewStep alterViewIfExists(Name view) {
        return new AlterViewImpl(configuration(), DSL.table(view), null, false, true);
    }

    @Override // org.jooq.DSLContext
    public AlterViewStep alterViewIfExists(Table<?> view) {
        return new AlterViewImpl(configuration(), view, null, false, true);
    }

    @Override // org.jooq.DSLContext
    public AlterViewStep alterMaterializedView(String view) {
        return new AlterViewImpl(configuration(), DSL.table(DSL.name(view)), null, true, false);
    }

    @Override // org.jooq.DSLContext
    public AlterViewStep alterMaterializedView(Name view) {
        return new AlterViewImpl(configuration(), DSL.table(view), null, true, false);
    }

    @Override // org.jooq.DSLContext
    public AlterViewStep alterMaterializedView(Table<?> view) {
        return new AlterViewImpl(configuration(), view, null, true, false);
    }

    @Override // org.jooq.DSLContext
    public AlterViewStep alterMaterializedViewIfExists(String view) {
        return new AlterViewImpl(configuration(), DSL.table(DSL.name(view)), null, true, true);
    }

    @Override // org.jooq.DSLContext
    public AlterViewStep alterMaterializedViewIfExists(Name view) {
        return new AlterViewImpl(configuration(), DSL.table(view), null, true, true);
    }

    @Override // org.jooq.DSLContext
    public AlterViewStep alterMaterializedViewIfExists(Table<?> view) {
        return new AlterViewImpl(configuration(), view, null, true, true);
    }

    @Override // org.jooq.DSLContext
    public AlterViewStep alterView(Table<?> view, Field<?>... fields) {
        return new AlterViewImpl(configuration(), view, Arrays.asList(fields), false, false);
    }

    @Override // org.jooq.DSLContext
    public AlterViewStep alterView(Table<?> view, Collection<? extends Field<?>> fields) {
        return new AlterViewImpl(configuration(), view, new QueryPartList(fields), false, false);
    }

    @Override // org.jooq.DSLContext
    public CommentOnIsStep commentOnTable(String table) {
        return new CommentOnImpl(configuration(), DSL.table(DSL.name(table)), false, false, null);
    }

    @Override // org.jooq.DSLContext
    public CommentOnIsStep commentOnTable(Name table) {
        return new CommentOnImpl(configuration(), DSL.table(table), false, false, null);
    }

    @Override // org.jooq.DSLContext
    public CommentOnIsStep commentOnTable(Table<?> table) {
        return new CommentOnImpl(configuration(), table, false, false, null);
    }

    @Override // org.jooq.DSLContext
    public CommentOnIsStep commentOnView(String view) {
        return new CommentOnImpl(configuration(), DSL.table(DSL.name(view)), true, false, null);
    }

    @Override // org.jooq.DSLContext
    public CommentOnIsStep commentOnView(Name view) {
        return new CommentOnImpl(configuration(), DSL.table(view), true, false, null);
    }

    @Override // org.jooq.DSLContext
    public CommentOnIsStep commentOnView(Table<?> view) {
        return new CommentOnImpl(configuration(), view, true, false, null);
    }

    @Override // org.jooq.DSLContext
    public CommentOnIsStep commentOnMaterializedView(String view) {
        return new CommentOnImpl(configuration(), DSL.table(DSL.name(view)), false, true, null);
    }

    @Override // org.jooq.DSLContext
    public CommentOnIsStep commentOnMaterializedView(Name view) {
        return new CommentOnImpl(configuration(), DSL.table(view), false, true, null);
    }

    @Override // org.jooq.DSLContext
    public CommentOnIsStep commentOnMaterializedView(Table<?> view) {
        return new CommentOnImpl(configuration(), view, false, true, null);
    }

    @Override // org.jooq.DSLContext
    public CommentOnIsStep commentOnColumn(String field) {
        return new CommentOnImpl(configuration(), null, false, false, DSL.field(DSL.name(field)));
    }

    @Override // org.jooq.DSLContext
    public CommentOnIsStep commentOnColumn(Name field) {
        return new CommentOnImpl(configuration(), null, false, false, DSL.field(field));
    }

    @Override // org.jooq.DSLContext
    public CommentOnIsStep commentOnColumn(Field<?> field) {
        return new CommentOnImpl(configuration(), null, false, false, field);
    }

    @Override // org.jooq.DSLContext
    public CreateDatabaseFinalStep createDatabase(String database) {
        return new CreateDatabaseImpl(configuration(), DSL.catalog(DSL.name(database)), false);
    }

    @Override // org.jooq.DSLContext
    public CreateDatabaseFinalStep createDatabase(Name database) {
        return new CreateDatabaseImpl(configuration(), DSL.catalog(database), false);
    }

    @Override // org.jooq.DSLContext
    public CreateDatabaseFinalStep createDatabase(Catalog database) {
        return new CreateDatabaseImpl(configuration(), database, false);
    }

    @Override // org.jooq.DSLContext
    public CreateDatabaseFinalStep createDatabaseIfNotExists(String database) {
        return new CreateDatabaseImpl(configuration(), DSL.catalog(DSL.name(database)), true);
    }

    @Override // org.jooq.DSLContext
    public CreateDatabaseFinalStep createDatabaseIfNotExists(Name database) {
        return new CreateDatabaseImpl(configuration(), DSL.catalog(database), true);
    }

    @Override // org.jooq.DSLContext
    public CreateDatabaseFinalStep createDatabaseIfNotExists(Catalog database) {
        return new CreateDatabaseImpl(configuration(), database, true);
    }

    @Override // org.jooq.DSLContext
    public CreateDomainAsStep createDomain(String domain) {
        return new CreateDomainImpl(configuration(), DSL.domain(DSL.name(domain)), false);
    }

    @Override // org.jooq.DSLContext
    public CreateDomainAsStep createDomain(Name domain) {
        return new CreateDomainImpl(configuration(), DSL.domain(domain), false);
    }

    @Override // org.jooq.DSLContext
    public CreateDomainAsStep createDomain(Domain<?> domain) {
        return new CreateDomainImpl(configuration(), domain, false);
    }

    @Override // org.jooq.DSLContext
    public CreateDomainAsStep createDomainIfNotExists(String domain) {
        return new CreateDomainImpl(configuration(), DSL.domain(DSL.name(domain)), true);
    }

    @Override // org.jooq.DSLContext
    public CreateDomainAsStep createDomainIfNotExists(Name domain) {
        return new CreateDomainImpl(configuration(), DSL.domain(domain), true);
    }

    @Override // org.jooq.DSLContext
    public CreateDomainAsStep createDomainIfNotExists(Domain<?> domain) {
        return new CreateDomainImpl(configuration(), domain, true);
    }

    @Override // org.jooq.DSLContext
    public CreateIndexStep createIndex(String index) {
        return new CreateIndexImpl(configuration(), false, DSL.index(DSL.name(index)), false);
    }

    @Override // org.jooq.DSLContext
    public CreateIndexStep createIndex(Name index) {
        return new CreateIndexImpl(configuration(), false, DSL.index(index), false);
    }

    @Override // org.jooq.DSLContext
    public CreateIndexStep createIndex(Index index) {
        return new CreateIndexImpl(configuration(), false, index, false);
    }

    @Override // org.jooq.DSLContext
    public CreateIndexStep createIndex() {
        return new CreateIndexImpl(configuration(), false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateIndexStep createIndexIfNotExists(String index) {
        return new CreateIndexImpl(configuration(), false, DSL.index(DSL.name(index)), true);
    }

    @Override // org.jooq.DSLContext
    public CreateIndexStep createIndexIfNotExists(Name index) {
        return new CreateIndexImpl(configuration(), false, DSL.index(index), true);
    }

    @Override // org.jooq.DSLContext
    public CreateIndexStep createIndexIfNotExists(Index index) {
        return new CreateIndexImpl(configuration(), false, index, true);
    }

    @Override // org.jooq.DSLContext
    public CreateIndexStep createIndexIfNotExists() {
        return new CreateIndexImpl(configuration(), false, true);
    }

    @Override // org.jooq.DSLContext
    public CreateIndexStep createUniqueIndex(String index) {
        return new CreateIndexImpl(configuration(), true, DSL.index(DSL.name(index)), false);
    }

    @Override // org.jooq.DSLContext
    public CreateIndexStep createUniqueIndex(Name index) {
        return new CreateIndexImpl(configuration(), true, DSL.index(index), false);
    }

    @Override // org.jooq.DSLContext
    public CreateIndexStep createUniqueIndex(Index index) {
        return new CreateIndexImpl(configuration(), true, index, false);
    }

    @Override // org.jooq.DSLContext
    public CreateIndexStep createUniqueIndex() {
        return new CreateIndexImpl(configuration(), true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateIndexStep createUniqueIndexIfNotExists(String index) {
        return new CreateIndexImpl(configuration(), true, DSL.index(DSL.name(index)), true);
    }

    @Override // org.jooq.DSLContext
    public CreateIndexStep createUniqueIndexIfNotExists(Name index) {
        return new CreateIndexImpl(configuration(), true, DSL.index(index), true);
    }

    @Override // org.jooq.DSLContext
    public CreateIndexStep createUniqueIndexIfNotExists(Index index) {
        return new CreateIndexImpl(configuration(), true, index, true);
    }

    @Override // org.jooq.DSLContext
    public CreateIndexStep createUniqueIndexIfNotExists() {
        return new CreateIndexImpl(configuration(), true, true);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createTable(String table) {
        return new CreateTableImpl(configuration(), DSL.table(DSL.name(table)), false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createTable(Name table) {
        return new CreateTableImpl(configuration(), DSL.table(table), false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createTable(Table<?> table) {
        return new CreateTableImpl(configuration(), table, false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createTableIfNotExists(String table) {
        return new CreateTableImpl(configuration(), DSL.table(DSL.name(table)), false, true);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createTableIfNotExists(Name table) {
        return new CreateTableImpl(configuration(), DSL.table(table), false, true);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createTableIfNotExists(Table<?> table) {
        return new CreateTableImpl(configuration(), table, false, true);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createTemporaryTable(String table) {
        return new CreateTableImpl(configuration(), DSL.table(DSL.name(table)), true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createTemporaryTable(Name table) {
        return new CreateTableImpl(configuration(), DSL.table(table), true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createTemporaryTable(Table<?> table) {
        return new CreateTableImpl(configuration(), table, true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createTemporaryTableIfNotExists(String table) {
        return new CreateTableImpl(configuration(), DSL.table(DSL.name(table)), true, true);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createTemporaryTableIfNotExists(Name table) {
        return new CreateTableImpl(configuration(), DSL.table(table), true, true);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createTemporaryTableIfNotExists(Table<?> table) {
        return new CreateTableImpl(configuration(), table, true, true);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createGlobalTemporaryTable(String table) {
        return new CreateTableImpl(configuration(), DSL.table(DSL.name(table)), true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createGlobalTemporaryTable(Name table) {
        return new CreateTableImpl(configuration(), DSL.table(table), true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createGlobalTemporaryTable(Table<?> table) {
        return new CreateTableImpl(configuration(), table, true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createGlobalTemporaryTableIfNotExists(String table) {
        return new CreateTableImpl(configuration(), DSL.table(DSL.name(table)), true, true);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createGlobalTemporaryTableIfNotExists(Name table) {
        return new CreateTableImpl(configuration(), DSL.table(table), true, true);
    }

    @Override // org.jooq.DSLContext
    public CreateTableElementListStep createGlobalTemporaryTableIfNotExists(Table<?> table) {
        return new CreateTableImpl(configuration(), table, true, true);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createView(String view, String... fields) {
        return new CreateViewImpl(configuration(), DSL.table(DSL.name(view)), Tools.fieldsByName(fields), false, false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createView(Name view, Name... fields) {
        return new CreateViewImpl(configuration(), DSL.table(view), Tools.fieldsByName(fields), false, false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createView(Table<?> view, Field<?>... fields) {
        return new CreateViewImpl(configuration(), view, Arrays.asList(fields), false, false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createView(String view, Collection<? extends String> fields) {
        return new CreateViewImpl(configuration(), DSL.table(DSL.name(view)), Tools.fieldsByName((String[]) fields.toArray(Tools.EMPTY_STRING)), false, false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createView(Name view, Collection<? extends Name> fields) {
        return new CreateViewImpl(configuration(), DSL.table(view), Tools.fieldsByName((Name[]) fields.toArray(Tools.EMPTY_NAME)), false, false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createView(Table<?> view, Collection<? extends Field<?>> fields) {
        return new CreateViewImpl(configuration(), view, new QueryPartList(fields), false, false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createViewIfNotExists(String view, String... fields) {
        return new CreateViewImpl(configuration(), DSL.table(DSL.name(view)), Tools.fieldsByName(fields), false, false, true);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createViewIfNotExists(Name view, Name... fields) {
        return new CreateViewImpl(configuration(), DSL.table(view), Tools.fieldsByName(fields), false, false, true);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createViewIfNotExists(Table<?> view, Field<?>... fields) {
        return new CreateViewImpl(configuration(), view, Arrays.asList(fields), false, false, true);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createViewIfNotExists(String view, Collection<? extends String> fields) {
        return new CreateViewImpl(configuration(), DSL.table(DSL.name(view)), Tools.fieldsByName((String[]) fields.toArray(Tools.EMPTY_STRING)), false, false, true);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createViewIfNotExists(Name view, Collection<? extends Name> fields) {
        return new CreateViewImpl(configuration(), DSL.table(view), Tools.fieldsByName((Name[]) fields.toArray(Tools.EMPTY_NAME)), false, false, true);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createViewIfNotExists(Table<?> view, Collection<? extends Field<?>> fields) {
        return new CreateViewImpl(configuration(), view, new QueryPartList(fields), false, false, true);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceView(String view, String... fields) {
        return new CreateViewImpl(configuration(), DSL.table(DSL.name(view)), Tools.fieldsByName(fields), true, false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceView(Name view, Name... fields) {
        return new CreateViewImpl(configuration(), DSL.table(view), Tools.fieldsByName(fields), true, false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceView(Table<?> view, Field<?>... fields) {
        return new CreateViewImpl(configuration(), view, Arrays.asList(fields), true, false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceView(String view, Collection<? extends String> fields) {
        return new CreateViewImpl(configuration(), DSL.table(DSL.name(view)), Tools.fieldsByName((String[]) fields.toArray(Tools.EMPTY_STRING)), true, false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceView(Name view, Collection<? extends Name> fields) {
        return new CreateViewImpl(configuration(), DSL.table(view), Tools.fieldsByName((Name[]) fields.toArray(Tools.EMPTY_NAME)), true, false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceView(Table<?> view, Collection<? extends Field<?>> fields) {
        return new CreateViewImpl(configuration(), view, new QueryPartList(fields), true, false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createMaterializedView(String view, String... fields) {
        return new CreateViewImpl(configuration(), DSL.table(DSL.name(view)), Tools.fieldsByName(fields), false, true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createMaterializedView(Name view, Name... fields) {
        return new CreateViewImpl(configuration(), DSL.table(view), Tools.fieldsByName(fields), false, true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createMaterializedView(Table<?> view, Field<?>... fields) {
        return new CreateViewImpl(configuration(), view, Arrays.asList(fields), false, true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createMaterializedView(String view, Collection<? extends String> fields) {
        return new CreateViewImpl(configuration(), DSL.table(DSL.name(view)), Tools.fieldsByName((String[]) fields.toArray(Tools.EMPTY_STRING)), false, true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createMaterializedView(Name view, Collection<? extends Name> fields) {
        return new CreateViewImpl(configuration(), DSL.table(view), Tools.fieldsByName((Name[]) fields.toArray(Tools.EMPTY_NAME)), false, true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createMaterializedView(Table<?> view, Collection<? extends Field<?>> fields) {
        return new CreateViewImpl(configuration(), view, new QueryPartList(fields), false, true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createMaterializedViewIfNotExists(String view, String... fields) {
        return new CreateViewImpl(configuration(), DSL.table(DSL.name(view)), Tools.fieldsByName(fields), false, true, true);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createMaterializedViewIfNotExists(Name view, Name... fields) {
        return new CreateViewImpl(configuration(), DSL.table(view), Tools.fieldsByName(fields), false, true, true);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createMaterializedViewIfNotExists(Table<?> view, Field<?>... fields) {
        return new CreateViewImpl(configuration(), view, Arrays.asList(fields), false, true, true);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createMaterializedViewIfNotExists(String view, Collection<? extends String> fields) {
        return new CreateViewImpl(configuration(), DSL.table(DSL.name(view)), Tools.fieldsByName((String[]) fields.toArray(Tools.EMPTY_STRING)), false, true, true);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createMaterializedViewIfNotExists(Name view, Collection<? extends Name> fields) {
        return new CreateViewImpl(configuration(), DSL.table(view), Tools.fieldsByName((Name[]) fields.toArray(Tools.EMPTY_NAME)), false, true, true);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createMaterializedViewIfNotExists(Table<?> view, Collection<? extends Field<?>> fields) {
        return new CreateViewImpl(configuration(), view, new QueryPartList(fields), false, true, true);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceMaterializedView(String view, String... fields) {
        return new CreateViewImpl(configuration(), DSL.table(DSL.name(view)), Tools.fieldsByName(fields), true, true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceMaterializedView(Name view, Name... fields) {
        return new CreateViewImpl(configuration(), DSL.table(view), Tools.fieldsByName(fields), true, true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceMaterializedView(Table<?> view, Field<?>... fields) {
        return new CreateViewImpl(configuration(), view, Arrays.asList(fields), true, true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceMaterializedView(String view, Collection<? extends String> fields) {
        return new CreateViewImpl(configuration(), DSL.table(DSL.name(view)), Tools.fieldsByName((String[]) fields.toArray(Tools.EMPTY_STRING)), true, true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceMaterializedView(Name view, Collection<? extends Name> fields) {
        return new CreateViewImpl(configuration(), DSL.table(view), Tools.fieldsByName((Name[]) fields.toArray(Tools.EMPTY_NAME)), true, true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceMaterializedView(Table<?> view, Collection<? extends Field<?>> fields) {
        return new CreateViewImpl(configuration(), view, new QueryPartList(fields), true, true, false);
    }

    @Override // org.jooq.DSLContext
    public CreateTypeStep createType(String type) {
        return new CreateTypeImpl(configuration(), DSL.type(DSL.name(type)), false);
    }

    @Override // org.jooq.DSLContext
    public CreateTypeStep createType(Name type) {
        return new CreateTypeImpl(configuration(), DSL.type(type), false);
    }

    @Override // org.jooq.DSLContext
    public CreateTypeStep createType(Type<?> type) {
        return new CreateTypeImpl(configuration(), type, false);
    }

    @Override // org.jooq.DSLContext
    public CreateTypeStep createTypeIfNotExists(String type) {
        return new CreateTypeImpl(configuration(), DSL.type(DSL.name(type)), true);
    }

    @Override // org.jooq.DSLContext
    public CreateTypeStep createTypeIfNotExists(Name type) {
        return new CreateTypeImpl(configuration(), DSL.type(type), true);
    }

    @Override // org.jooq.DSLContext
    public CreateTypeStep createTypeIfNotExists(Type<?> type) {
        return new CreateTypeImpl(configuration(), type, true);
    }

    @Override // org.jooq.DSLContext
    public CreateSchemaFinalStep createSchema(String schema) {
        return new CreateSchemaImpl(configuration(), DSL.schema(DSL.name(schema)), false);
    }

    @Override // org.jooq.DSLContext
    public CreateSchemaFinalStep createSchema(Name schema) {
        return new CreateSchemaImpl(configuration(), DSL.schema(schema), false);
    }

    @Override // org.jooq.DSLContext
    public CreateSchemaFinalStep createSchema(Schema schema) {
        return new CreateSchemaImpl(configuration(), schema, false);
    }

    @Override // org.jooq.DSLContext
    public CreateSchemaFinalStep createSchemaIfNotExists(String schema) {
        return new CreateSchemaImpl(configuration(), DSL.schema(DSL.name(schema)), true);
    }

    @Override // org.jooq.DSLContext
    public CreateSchemaFinalStep createSchemaIfNotExists(Name schema) {
        return new CreateSchemaImpl(configuration(), DSL.schema(schema), true);
    }

    @Override // org.jooq.DSLContext
    public CreateSchemaFinalStep createSchemaIfNotExists(Schema schema) {
        return new CreateSchemaImpl(configuration(), schema, true);
    }

    @Override // org.jooq.DSLContext
    public CreateSequenceFlagsStep createSequence(String sequence) {
        return new CreateSequenceImpl(configuration(), DSL.sequence(DSL.name(sequence)), false);
    }

    @Override // org.jooq.DSLContext
    public CreateSequenceFlagsStep createSequence(Name sequence) {
        return new CreateSequenceImpl(configuration(), DSL.sequence(sequence), false);
    }

    @Override // org.jooq.DSLContext
    public CreateSequenceFlagsStep createSequence(Sequence<?> sequence) {
        return new CreateSequenceImpl(configuration(), sequence, false);
    }

    @Override // org.jooq.DSLContext
    public CreateSequenceFlagsStep createSequenceIfNotExists(String sequence) {
        return new CreateSequenceImpl(configuration(), DSL.sequence(DSL.name(sequence)), true);
    }

    @Override // org.jooq.DSLContext
    public CreateSequenceFlagsStep createSequenceIfNotExists(Name sequence) {
        return new CreateSequenceImpl(configuration(), DSL.sequence(sequence), true);
    }

    @Override // org.jooq.DSLContext
    public CreateSequenceFlagsStep createSequenceIfNotExists(Sequence<?> sequence) {
        return new CreateSequenceImpl(configuration(), sequence, true);
    }

    @Override // org.jooq.DSLContext
    public DropDatabaseFinalStep dropDatabase(String database) {
        return new DropDatabaseImpl(configuration(), DSL.catalog(DSL.name(database)), false);
    }

    @Override // org.jooq.DSLContext
    public DropDatabaseFinalStep dropDatabase(Name database) {
        return new DropDatabaseImpl(configuration(), DSL.catalog(database), false);
    }

    @Override // org.jooq.DSLContext
    public DropDatabaseFinalStep dropDatabase(Catalog database) {
        return new DropDatabaseImpl(configuration(), database, false);
    }

    @Override // org.jooq.DSLContext
    public DropDatabaseFinalStep dropDatabaseIfExists(String database) {
        return new DropDatabaseImpl(configuration(), DSL.catalog(DSL.name(database)), true);
    }

    @Override // org.jooq.DSLContext
    public DropDatabaseFinalStep dropDatabaseIfExists(Name database) {
        return new DropDatabaseImpl(configuration(), DSL.catalog(database), true);
    }

    @Override // org.jooq.DSLContext
    public DropDatabaseFinalStep dropDatabaseIfExists(Catalog database) {
        return new DropDatabaseImpl(configuration(), database, true);
    }

    @Override // org.jooq.DSLContext
    public DropDomainCascadeStep dropDomain(String domain) {
        return new DropDomainImpl(configuration(), DSL.domain(DSL.name(domain)), false);
    }

    @Override // org.jooq.DSLContext
    public DropDomainCascadeStep dropDomain(Name domain) {
        return new DropDomainImpl(configuration(), DSL.domain(domain), false);
    }

    @Override // org.jooq.DSLContext
    public DropDomainCascadeStep dropDomain(Domain<?> domain) {
        return new DropDomainImpl(configuration(), domain, false);
    }

    @Override // org.jooq.DSLContext
    public DropDomainCascadeStep dropDomainIfExists(String domain) {
        return new DropDomainImpl(configuration(), DSL.domain(DSL.name(domain)), true);
    }

    @Override // org.jooq.DSLContext
    public DropDomainCascadeStep dropDomainIfExists(Name domain) {
        return new DropDomainImpl(configuration(), DSL.domain(domain), true);
    }

    @Override // org.jooq.DSLContext
    public DropDomainCascadeStep dropDomainIfExists(Domain<?> domain) {
        return new DropDomainImpl(configuration(), domain, true);
    }

    @Override // org.jooq.DSLContext
    public DropIndexOnStep dropIndex(String index) {
        return new DropIndexImpl(configuration(), DSL.index(DSL.name(index)), false);
    }

    @Override // org.jooq.DSLContext
    public DropIndexOnStep dropIndex(Name index) {
        return new DropIndexImpl(configuration(), DSL.index(index), false);
    }

    @Override // org.jooq.DSLContext
    public DropIndexOnStep dropIndex(Index index) {
        return new DropIndexImpl(configuration(), index, false);
    }

    @Override // org.jooq.DSLContext
    public DropIndexOnStep dropIndexIfExists(String index) {
        return new DropIndexImpl(configuration(), DSL.index(DSL.name(index)), true);
    }

    @Override // org.jooq.DSLContext
    public DropIndexOnStep dropIndexIfExists(Name index) {
        return new DropIndexImpl(configuration(), DSL.index(index), true);
    }

    @Override // org.jooq.DSLContext
    public DropIndexOnStep dropIndexIfExists(Index index) {
        return new DropIndexImpl(configuration(), index, true);
    }

    @Override // org.jooq.DSLContext
    public DropSchemaStep dropSchema(String schema) {
        return new DropSchemaImpl(configuration(), DSL.schema(DSL.name(schema)), false);
    }

    @Override // org.jooq.DSLContext
    public DropSchemaStep dropSchema(Name schema) {
        return new DropSchemaImpl(configuration(), DSL.schema(schema), false);
    }

    @Override // org.jooq.DSLContext
    public DropSchemaStep dropSchema(Schema schema) {
        return new DropSchemaImpl(configuration(), schema, false);
    }

    @Override // org.jooq.DSLContext
    public DropSchemaStep dropSchemaIfExists(String schema) {
        return new DropSchemaImpl(configuration(), DSL.schema(DSL.name(schema)), true);
    }

    @Override // org.jooq.DSLContext
    public DropSchemaStep dropSchemaIfExists(Name schema) {
        return new DropSchemaImpl(configuration(), DSL.schema(schema), true);
    }

    @Override // org.jooq.DSLContext
    public DropSchemaStep dropSchemaIfExists(Schema schema) {
        return new DropSchemaImpl(configuration(), schema, true);
    }

    @Override // org.jooq.DSLContext
    public DropSequenceFinalStep dropSequence(String sequence) {
        return new DropSequenceImpl(configuration(), DSL.sequence(DSL.name(sequence)), false);
    }

    @Override // org.jooq.DSLContext
    public DropSequenceFinalStep dropSequence(Name sequence) {
        return new DropSequenceImpl(configuration(), DSL.sequence(sequence), false);
    }

    @Override // org.jooq.DSLContext
    public DropSequenceFinalStep dropSequence(Sequence<?> sequence) {
        return new DropSequenceImpl(configuration(), sequence, false);
    }

    @Override // org.jooq.DSLContext
    public DropSequenceFinalStep dropSequenceIfExists(String sequence) {
        return new DropSequenceImpl(configuration(), DSL.sequence(DSL.name(sequence)), true);
    }

    @Override // org.jooq.DSLContext
    public DropSequenceFinalStep dropSequenceIfExists(Name sequence) {
        return new DropSequenceImpl(configuration(), DSL.sequence(sequence), true);
    }

    @Override // org.jooq.DSLContext
    public DropSequenceFinalStep dropSequenceIfExists(Sequence<?> sequence) {
        return new DropSequenceImpl(configuration(), sequence, true);
    }

    @Override // org.jooq.DSLContext
    public DropTableStep dropTable(String table) {
        return new DropTableImpl(configuration(), false, DSL.table(DSL.name(table)), false);
    }

    @Override // org.jooq.DSLContext
    public DropTableStep dropTable(Name table) {
        return new DropTableImpl(configuration(), false, DSL.table(table), false);
    }

    @Override // org.jooq.DSLContext
    public DropTableStep dropTable(Table<?> table) {
        return new DropTableImpl(configuration(), false, table, false);
    }

    @Override // org.jooq.DSLContext
    public DropTableStep dropTableIfExists(String table) {
        return new DropTableImpl(configuration(), false, DSL.table(DSL.name(table)), true);
    }

    @Override // org.jooq.DSLContext
    public DropTableStep dropTableIfExists(Name table) {
        return new DropTableImpl(configuration(), false, DSL.table(table), true);
    }

    @Override // org.jooq.DSLContext
    public DropTableStep dropTableIfExists(Table<?> table) {
        return new DropTableImpl(configuration(), false, table, true);
    }

    @Override // org.jooq.DSLContext
    public DropTableStep dropTemporaryTable(String table) {
        return new DropTableImpl(configuration(), true, DSL.table(DSL.name(table)), false);
    }

    @Override // org.jooq.DSLContext
    public DropTableStep dropTemporaryTable(Name table) {
        return new DropTableImpl(configuration(), true, DSL.table(table), false);
    }

    @Override // org.jooq.DSLContext
    public DropTableStep dropTemporaryTable(Table<?> table) {
        return new DropTableImpl(configuration(), true, table, false);
    }

    @Override // org.jooq.DSLContext
    public DropTableStep dropTemporaryTableIfExists(String table) {
        return new DropTableImpl(configuration(), true, DSL.table(DSL.name(table)), true);
    }

    @Override // org.jooq.DSLContext
    public DropTableStep dropTemporaryTableIfExists(Name table) {
        return new DropTableImpl(configuration(), true, DSL.table(table), true);
    }

    @Override // org.jooq.DSLContext
    public DropTableStep dropTemporaryTableIfExists(Table<?> table) {
        return new DropTableImpl(configuration(), true, table, true);
    }

    @Override // org.jooq.DSLContext
    public DropTypeStep dropType(String types) {
        return new DropTypeImpl(configuration(), Arrays.asList(DSL.type(types)), false);
    }

    @Override // org.jooq.DSLContext
    public DropTypeStep dropType(Name types) {
        return new DropTypeImpl(configuration(), Arrays.asList(DSL.type(types)), false);
    }

    @Override // org.jooq.DSLContext
    public DropTypeStep dropType(Type<?> types) {
        return new DropTypeImpl(configuration(), Arrays.asList(types), false);
    }

    @Override // org.jooq.DSLContext
    public DropTypeStep dropType(String... types) {
        return new DropTypeImpl(configuration(), Tools.map(types, e -> {
            return DSL.type(e);
        }), false);
    }

    @Override // org.jooq.DSLContext
    public DropTypeStep dropType(Name... types) {
        return new DropTypeImpl(configuration(), Tools.map(types, e -> {
            return DSL.type(e);
        }), false);
    }

    @Override // org.jooq.DSLContext
    public DropTypeStep dropType(Type<?>... types) {
        return new DropTypeImpl(configuration(), Arrays.asList(types), false);
    }

    @Override // org.jooq.DSLContext
    public DropTypeStep dropType(Collection<? extends Type<?>> types) {
        return new DropTypeImpl(configuration(), new QueryPartList(types), false);
    }

    @Override // org.jooq.DSLContext
    public DropTypeStep dropTypeIfExists(String types) {
        return new DropTypeImpl(configuration(), Arrays.asList(DSL.type(types)), true);
    }

    @Override // org.jooq.DSLContext
    public DropTypeStep dropTypeIfExists(Name types) {
        return new DropTypeImpl(configuration(), Arrays.asList(DSL.type(types)), true);
    }

    @Override // org.jooq.DSLContext
    public DropTypeStep dropTypeIfExists(Type<?> types) {
        return new DropTypeImpl(configuration(), Arrays.asList(types), true);
    }

    @Override // org.jooq.DSLContext
    public DropTypeStep dropTypeIfExists(String... types) {
        return new DropTypeImpl(configuration(), Tools.map(types, e -> {
            return DSL.type(e);
        }), true);
    }

    @Override // org.jooq.DSLContext
    public DropTypeStep dropTypeIfExists(Name... types) {
        return new DropTypeImpl(configuration(), Tools.map(types, e -> {
            return DSL.type(e);
        }), true);
    }

    @Override // org.jooq.DSLContext
    public DropTypeStep dropTypeIfExists(Type<?>... types) {
        return new DropTypeImpl(configuration(), Arrays.asList(types), true);
    }

    @Override // org.jooq.DSLContext
    public DropTypeStep dropTypeIfExists(Collection<? extends Type<?>> types) {
        return new DropTypeImpl(configuration(), new QueryPartList(types), true);
    }

    @Override // org.jooq.DSLContext
    public DropViewFinalStep dropView(String view) {
        return new DropViewImpl(configuration(), DSL.table(DSL.name(view)), false, false);
    }

    @Override // org.jooq.DSLContext
    public DropViewFinalStep dropView(Name view) {
        return new DropViewImpl(configuration(), DSL.table(view), false, false);
    }

    @Override // org.jooq.DSLContext
    public DropViewFinalStep dropView(Table<?> view) {
        return new DropViewImpl(configuration(), view, false, false);
    }

    @Override // org.jooq.DSLContext
    public DropViewFinalStep dropViewIfExists(String view) {
        return new DropViewImpl(configuration(), DSL.table(DSL.name(view)), false, true);
    }

    @Override // org.jooq.DSLContext
    public DropViewFinalStep dropViewIfExists(Name view) {
        return new DropViewImpl(configuration(), DSL.table(view), false, true);
    }

    @Override // org.jooq.DSLContext
    public DropViewFinalStep dropViewIfExists(Table<?> view) {
        return new DropViewImpl(configuration(), view, false, true);
    }

    @Override // org.jooq.DSLContext
    public DropViewFinalStep dropMaterializedView(String view) {
        return new DropViewImpl(configuration(), DSL.table(DSL.name(view)), true, false);
    }

    @Override // org.jooq.DSLContext
    public DropViewFinalStep dropMaterializedView(Name view) {
        return new DropViewImpl(configuration(), DSL.table(view), true, false);
    }

    @Override // org.jooq.DSLContext
    public DropViewFinalStep dropMaterializedView(Table<?> view) {
        return new DropViewImpl(configuration(), view, true, false);
    }

    @Override // org.jooq.DSLContext
    public DropViewFinalStep dropMaterializedViewIfExists(String view) {
        return new DropViewImpl(configuration(), DSL.table(DSL.name(view)), true, true);
    }

    @Override // org.jooq.DSLContext
    public DropViewFinalStep dropMaterializedViewIfExists(Name view) {
        return new DropViewImpl(configuration(), DSL.table(view), true, true);
    }

    @Override // org.jooq.DSLContext
    public DropViewFinalStep dropMaterializedViewIfExists(Table<?> view) {
        return new DropViewImpl(configuration(), view, true, true);
    }

    @Override // org.jooq.DSLContext
    public GrantOnStep grant(Privilege privileges) {
        return new GrantImpl(configuration(), Arrays.asList(privileges));
    }

    @Override // org.jooq.DSLContext
    public GrantOnStep grant(Privilege... privileges) {
        return new GrantImpl(configuration(), Arrays.asList(privileges));
    }

    @Override // org.jooq.DSLContext
    public GrantOnStep grant(Collection<? extends Privilege> privileges) {
        return new GrantImpl(configuration(), new QueryPartList(privileges));
    }

    @Override // org.jooq.DSLContext
    public RevokeOnStep revoke(Privilege privileges) {
        return new RevokeImpl(configuration(), Arrays.asList(privileges), false);
    }

    @Override // org.jooq.DSLContext
    public RevokeOnStep revoke(Privilege... privileges) {
        return new RevokeImpl(configuration(), Arrays.asList(privileges), false);
    }

    @Override // org.jooq.DSLContext
    public RevokeOnStep revoke(Collection<? extends Privilege> privileges) {
        return new RevokeImpl(configuration(), new QueryPartList(privileges), false);
    }

    @Override // org.jooq.DSLContext
    public RevokeOnStep revokeGrantOptionFor(Privilege privileges) {
        return new RevokeImpl(configuration(), Arrays.asList(privileges), true);
    }

    @Override // org.jooq.DSLContext
    public RevokeOnStep revokeGrantOptionFor(Privilege... privileges) {
        return new RevokeImpl(configuration(), Arrays.asList(privileges), true);
    }

    @Override // org.jooq.DSLContext
    public RevokeOnStep revokeGrantOptionFor(Collection<? extends Privilege> privileges) {
        return new RevokeImpl(configuration(), new QueryPartList(privileges), true);
    }

    @Override // org.jooq.DSLContext
    public RowCountQuery set(String name, Param<?> value) {
        return new SetCommand(configuration(), DSL.name(name), value, false);
    }

    @Override // org.jooq.DSLContext
    public RowCountQuery set(Name name, Param<?> value) {
        return new SetCommand(configuration(), name, value, false);
    }

    @Override // org.jooq.DSLContext
    public RowCountQuery setLocal(String name, Param<?> value) {
        return new SetCommand(configuration(), DSL.name(name), value, true);
    }

    @Override // org.jooq.DSLContext
    public RowCountQuery setLocal(Name name, Param<?> value) {
        return new SetCommand(configuration(), name, value, true);
    }

    @Override // org.jooq.DSLContext
    public RowCountQuery setCatalog(String catalog) {
        return new SetCatalog(configuration(), DSL.catalog(DSL.name(catalog)));
    }

    @Override // org.jooq.DSLContext
    public RowCountQuery setCatalog(Name catalog) {
        return new SetCatalog(configuration(), DSL.catalog(catalog));
    }

    @Override // org.jooq.DSLContext
    public RowCountQuery setCatalog(Catalog catalog) {
        return new SetCatalog(configuration(), catalog);
    }

    @Override // org.jooq.DSLContext
    public RowCountQuery setSchema(String schema) {
        return new SetSchema(configuration(), DSL.schema(DSL.name(schema)));
    }

    @Override // org.jooq.DSLContext
    public RowCountQuery setSchema(Name schema) {
        return new SetSchema(configuration(), DSL.schema(schema));
    }

    @Override // org.jooq.DSLContext
    public RowCountQuery setSchema(Schema schema) {
        return new SetSchema(configuration(), schema);
    }

    @Override // org.jooq.DSLContext
    public TruncateIdentityStep<Record> truncate(String table) {
        return new TruncateImpl(configuration(), Arrays.asList(DSL.table(table)));
    }

    @Override // org.jooq.DSLContext
    public TruncateIdentityStep<Record> truncate(Name table) {
        return new TruncateImpl(configuration(), Arrays.asList(DSL.table(table)));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> TruncateIdentityStep<R> truncate(Table<R> table) {
        return new TruncateImpl(configuration(), Arrays.asList(table));
    }

    @Override // org.jooq.DSLContext
    public TruncateIdentityStep<Record> truncate(String... table) {
        return new TruncateImpl(configuration(), Tools.map(table, e -> {
            return DSL.table(e);
        }));
    }

    @Override // org.jooq.DSLContext
    public TruncateIdentityStep<Record> truncate(Name... table) {
        return new TruncateImpl(configuration(), Tools.map(table, e -> {
            return DSL.table(e);
        }));
    }

    @Override // org.jooq.DSLContext
    public TruncateIdentityStep<Record> truncate(Table<?>... table) {
        return new TruncateImpl(configuration(), Arrays.asList(table));
    }

    @Override // org.jooq.DSLContext
    public TruncateIdentityStep<Record> truncate(Collection<? extends Table<?>> table) {
        return new TruncateImpl(configuration(), new QueryPartList(table));
    }

    @Override // org.jooq.DSLContext
    public TruncateIdentityStep<Record> truncateTable(String table) {
        return truncate(table);
    }

    @Override // org.jooq.DSLContext
    public TruncateIdentityStep<Record> truncateTable(Name table) {
        return truncate(table);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> TruncateIdentityStep<R> truncateTable(Table<R> table) {
        return truncate(table);
    }

    @Override // org.jooq.DSLContext
    public TruncateIdentityStep<Record> truncateTable(String... table) {
        return truncate(table);
    }

    @Override // org.jooq.DSLContext
    public TruncateIdentityStep<Record> truncateTable(Name... table) {
        return truncate(table);
    }

    @Override // org.jooq.DSLContext
    public TruncateIdentityStep<Record> truncateTable(Table<?>... table) {
        return truncate(table);
    }

    @Override // org.jooq.DSLContext
    public TruncateIdentityStep<Record> truncateTable(Collection<? extends Table<?>> table) {
        return truncate(table);
    }

    @Override // org.jooq.DSLContext
    public Query startTransaction() {
        return new StartTransaction(configuration());
    }

    @Override // org.jooq.DSLContext
    public Query savepoint(String name) {
        return new Savepoint(configuration(), DSL.name(name));
    }

    @Override // org.jooq.DSLContext
    public Query savepoint(Name name) {
        return new Savepoint(configuration(), name);
    }

    @Override // org.jooq.DSLContext
    public Query releaseSavepoint(String name) {
        return new ReleaseSavepoint(configuration(), DSL.name(name));
    }

    @Override // org.jooq.DSLContext
    public Query releaseSavepoint(Name name) {
        return new ReleaseSavepoint(configuration(), name);
    }

    @Override // org.jooq.DSLContext
    public Query commit() {
        return new Commit(configuration());
    }

    @Override // org.jooq.DSLContext
    public RollbackToSavepointStep rollback() {
        return new Rollback(configuration());
    }

    @Override // org.jooq.DSLContext
    public Queries ddl(Catalog catalog) {
        return ddl(catalog, new DDLExportConfiguration());
    }

    @Override // org.jooq.DSLContext
    public Queries ddl(Catalog catalog, DDLFlag... flags) {
        return ddl(catalog, new DDLExportConfiguration().flags(flags));
    }

    @Override // org.jooq.DSLContext
    public Queries ddl(Catalog catalog, DDLExportConfiguration exportConfiguration) {
        return meta(catalog).ddl(exportConfiguration);
    }

    @Override // org.jooq.DSLContext
    public Queries ddl(Schema schema) {
        return ddl(schema, new DDLExportConfiguration());
    }

    @Override // org.jooq.DSLContext
    public Queries ddl(Schema schema, DDLFlag... flags) {
        return ddl(schema, new DDLExportConfiguration().flags(flags));
    }

    @Override // org.jooq.DSLContext
    public Queries ddl(Schema schema, DDLExportConfiguration exportConfiguration) {
        return meta(schema).ddl(exportConfiguration);
    }

    @Override // org.jooq.DSLContext
    public Queries ddl(Table<?> table) {
        return ddl(table);
    }

    @Override // org.jooq.DSLContext
    public Queries ddl(Table<?> table, DDLFlag... flags) {
        return ddl(new Table[]{table}, flags);
    }

    @Override // org.jooq.DSLContext
    public Queries ddl(Table<?> table, DDLExportConfiguration exportConfiguration) {
        return ddl(new Table[]{table}, exportConfiguration);
    }

    @Override // org.jooq.DSLContext
    public Queries ddl(Table... tables) {
        return ddl(tables, new DDLExportConfiguration());
    }

    @Override // org.jooq.DSLContext
    public Queries ddl(Table[] tables, DDLFlag... flags) {
        return ddl(tables, new DDLExportConfiguration().flags(flags));
    }

    @Override // org.jooq.DSLContext
    public Queries ddl(Table[] tables, DDLExportConfiguration exportConfiguration) {
        return meta((Table<?>[]) tables).ddl(exportConfiguration);
    }

    @Override // org.jooq.DSLContext
    public Queries ddl(Collection<? extends Table<?>> tables) {
        return ddl((Table[]) tables.toArray(Tools.EMPTY_TABLE));
    }

    @Override // org.jooq.DSLContext
    public Queries ddl(Collection<? extends Table<?>> tables, DDLFlag... flags) {
        return ddl((Table[]) tables.toArray(Tools.EMPTY_TABLE), flags);
    }

    @Override // org.jooq.DSLContext
    public Queries ddl(Collection<? extends Table<?>> tables, DDLExportConfiguration exportConfiguration) {
        return ddl((Table[]) tables.toArray(Tools.EMPTY_TABLE), exportConfiguration);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createView(String view, java.util.function.Function<? super Field<?>, ? extends String> fieldNameFunction) {
        return createView(DSL.table(DSL.name(view)), (f, i) -> {
            return DSL.field(DSL.name((String) fieldNameFunction.apply(f)));
        });
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createView(String view, BiFunction<? super Field<?>, ? super Integer, ? extends String> fieldNameFunction) {
        return createView(DSL.table(DSL.name(view)), (f, i) -> {
            return DSL.field(DSL.name((String) fieldNameFunction.apply(f, i)));
        });
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createView(Name view, java.util.function.Function<? super Field<?>, ? extends Name> fieldNameFunction) {
        return createView(DSL.table(view), (f, i) -> {
            return DSL.field((Name) fieldNameFunction.apply(f));
        });
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createView(Name view, BiFunction<? super Field<?>, ? super Integer, ? extends Name> fieldNameFunction) {
        return createView(DSL.table(view), (f, i) -> {
            return DSL.field((Name) fieldNameFunction.apply(f, i));
        });
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createView(Table<?> view, java.util.function.Function<? super Field<?>, ? extends Field<?>> fieldNameFunction) {
        return createView(view, (f, i) -> {
            return (Field) fieldNameFunction.apply(f);
        });
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createView(Table<?> view, BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> fieldNameFunction) {
        return new CreateViewImplWithFieldNameFunctionImpl(configuration(), view, fieldNameFunction, false, false);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceView(String view, java.util.function.Function<? super Field<?>, ? extends String> fieldNameFunction) {
        return createOrReplaceView(DSL.table(DSL.name(view)), (f, i) -> {
            return DSL.field(DSL.name((String) fieldNameFunction.apply(f)));
        });
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceView(String view, BiFunction<? super Field<?>, ? super Integer, ? extends String> fieldNameFunction) {
        return createOrReplaceView(DSL.table(DSL.name(view)), (f, i) -> {
            return DSL.field(DSL.name((String) fieldNameFunction.apply(f, i)));
        });
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceView(Name view, java.util.function.Function<? super Field<?>, ? extends Name> fieldNameFunction) {
        return createOrReplaceView(DSL.table(view), (f, i) -> {
            return DSL.field((Name) fieldNameFunction.apply(f));
        });
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceView(Name view, BiFunction<? super Field<?>, ? super Integer, ? extends Name> fieldNameFunction) {
        return createOrReplaceView(DSL.table(view), (f, i) -> {
            return DSL.field((Name) fieldNameFunction.apply(f, i));
        });
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceView(Table<?> view, java.util.function.Function<? super Field<?>, ? extends Field<?>> fieldNameFunction) {
        return createOrReplaceView(view, (f, i) -> {
            return (Field) fieldNameFunction.apply(f);
        });
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createOrReplaceView(Table<?> view, BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> fieldNameFunction) {
        return new CreateViewImplWithFieldNameFunctionImpl(configuration(), view, fieldNameFunction, false, true);
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createViewIfNotExists(String view, java.util.function.Function<? super Field<?>, ? extends String> fieldNameFunction) {
        return createViewIfNotExists(DSL.table(DSL.name(view)), (f, i) -> {
            return DSL.field(DSL.name((String) fieldNameFunction.apply(f)));
        });
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createViewIfNotExists(String view, BiFunction<? super Field<?>, ? super Integer, ? extends String> fieldNameFunction) {
        return createViewIfNotExists(DSL.table(DSL.name(view)), (f, i) -> {
            return DSL.field(DSL.name((String) fieldNameFunction.apply(f, i)));
        });
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createViewIfNotExists(Name view, java.util.function.Function<? super Field<?>, ? extends Name> fieldNameFunction) {
        return createViewIfNotExists(DSL.table(view), (f, i) -> {
            return DSL.field((Name) fieldNameFunction.apply(f));
        });
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createViewIfNotExists(Name view, BiFunction<? super Field<?>, ? super Integer, ? extends Name> fieldNameFunction) {
        return createViewIfNotExists(DSL.table(view), (f, i) -> {
            return DSL.field((Name) fieldNameFunction.apply(f, i));
        });
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createViewIfNotExists(Table<?> view, java.util.function.Function<? super Field<?>, ? extends Field<?>> fieldNameFunction) {
        return createViewIfNotExists(view, (f, i) -> {
            return (Field) fieldNameFunction.apply(f);
        });
    }

    @Override // org.jooq.DSLContext
    public CreateViewAsStep<Record> createViewIfNotExists(Table<?> view, BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> fieldNameFunction) {
        return new CreateViewImplWithFieldNameFunctionImpl(configuration(), view, fieldNameFunction, true, false);
    }

    @Override // org.jooq.DSLContext
    public AlterTableStep alterTable(String table) {
        return alterTable(DSL.name(table));
    }

    @Override // org.jooq.DSLContext
    public AlterTableStep alterTable(Name table) {
        return alterTable(DSL.table(table));
    }

    @Override // org.jooq.DSLContext
    public AlterTableStep alterTable(Table<?> table) {
        return new AlterTableImpl(configuration(), table);
    }

    @Override // org.jooq.DSLContext
    public AlterTableStep alterTableIfExists(String table) {
        return alterTableIfExists(DSL.name(table));
    }

    @Override // org.jooq.DSLContext
    public AlterTableStep alterTableIfExists(Name table) {
        return alterTableIfExists(DSL.table(table));
    }

    @Override // org.jooq.DSLContext
    public AlterTableStep alterTableIfExists(Table<?> table) {
        return new AlterTableImpl(configuration(), table, true);
    }

    @Override // org.jooq.DSLContext
    public BigInteger lastID() {
        switch (family()) {
            case DERBY:
                return (BigInteger) fetchValue(DSL.field("identity_val_local()", BigInteger.class));
            case H2:
            case HSQLDB:
                return (BigInteger) fetchValue(DSL.field("identity()", BigInteger.class));
            case CUBRID:
            case MARIADB:
            case MYSQL:
                return (BigInteger) fetchValue(DSL.field("last_insert_id()", BigInteger.class));
            case SQLITE:
                return (BigInteger) fetchValue(DSL.field("last_insert_rowid()", BigInteger.class));
            case POSTGRES:
            case YUGABYTEDB:
                return (BigInteger) fetchValue(DSL.field("lastval()", BigInteger.class));
            default:
                throw new SQLDialectNotSupportedException("identity functionality not supported by " + String.valueOf(configuration().dialect()));
        }
    }

    @Override // org.jooq.DSLContext
    public BigInteger nextval(String sequence) {
        return nextval(DSL.name(sequence));
    }

    @Override // org.jooq.DSLContext
    public BigInteger nextval(Name sequence) {
        return (BigInteger) nextval(DSL.sequence(sequence));
    }

    @Override // org.jooq.DSLContext
    public <T extends Number> T nextval(Sequence<T> sequence) {
        return (T) fetchValue(sequence.nextval());
    }

    @Override // org.jooq.DSLContext
    public <T extends Number> List<T> nextvals(Sequence<T> sequence, int size) {
        return fetchValues(sequence.nextvals(size));
    }

    @Override // org.jooq.DSLContext
    public BigInteger currval(String sequence) {
        return currval(DSL.name(sequence));
    }

    @Override // org.jooq.DSLContext
    public BigInteger currval(Name sequence) {
        return (BigInteger) currval(DSL.sequence(sequence));
    }

    @Override // org.jooq.DSLContext
    public <T extends Number> T currval(Sequence<T> sequence) {
        return (T) fetchValue(sequence.currval());
    }

    @Override // org.jooq.DSLContext
    public Record newRecord(Field<?>... fields) {
        return Tools.newRecord(false, RecordImplN.class, Tools.row0(fields), configuration()).operate(null);
    }

    @Override // org.jooq.DSLContext
    public Record newRecord(Collection<? extends Field<?>> fields) {
        return newRecord((Field<?>[]) fields.toArray(Tools.EMPTY_FIELD));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1> Record1<T1> newRecord(Field<T1> field1) {
        return (Record1) newRecord((Field<?>[]) new Field[]{field1});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2> Record2<T1, T2> newRecord(Field<T1> field1, Field<T2> field2) {
        return (Record2) newRecord((Field<?>[]) new Field[]{field1, field2});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3> Record3<T1, T2, T3> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return (Record3) newRecord((Field<?>[]) new Field[]{field1, field2, field3});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4> Record4<T1, T2, T3, T4> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return (Record4) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5> Record5<T1, T2, T3, T4, T5> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return (Record5) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6> Record6<T1, T2, T3, T4, T5, T6> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return (Record6) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7> Record7<T1, T2, T3, T4, T5, T6, T7> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return (Record7) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8> Record8<T1, T2, T3, T4, T5, T6, T7, T8> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return (Record8) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return (Record9) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return (Record10) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return (Record11) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return (Record12) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return (Record13) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return (Record14) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return (Record15) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return (Record16) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return (Record17) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return (Record18) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return (Record19) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return (Record20) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return (Record21) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return (Record22) newRecord((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22});
    }

    @Override // org.jooq.DSLContext
    public <R extends UDTRecord<R>> R newRecord(UDT<R> type) {
        return (R) Tools.newRecord(false, (RecordQualifier) type, configuration()).operate(null);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R newRecord(Table<R> table) {
        return (R) Tools.newRecord(false, (RecordQualifier) table, configuration()).operate(null);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R newRecord(Table<R> table, Object obj) {
        return (R) Tools.newRecord(false, (RecordQualifier) table, configuration()).operate(record -> {
            record.from(obj);
            return record;
        });
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Result<R> newResult(Table<R> table) {
        return new ResultImpl(configuration(), (AbstractRow) table.fieldsRow());
    }

    @Override // org.jooq.DSLContext
    public Result<Record> newResult(Field<?>... fields) {
        return new ResultImpl(configuration(), fields);
    }

    @Override // org.jooq.DSLContext
    public Result<Record> newResult(Collection<? extends Field<?>> fields) {
        return new ResultImpl(configuration(), fields);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1> Result<Record1<T1>> newResult(Field<T1> field) {
        return (Result<Record1<T1>>) newResult((Field<?>[]) new Field[]{field});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2> Result<Record2<T1, T2>> newResult(Field<T1> field, Field<T2> field2) {
        return (Result<Record2<T1, T2>>) newResult((Field<?>[]) new Field[]{field, field2});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3> Result<Record3<T1, T2, T3>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3) {
        return (Result<Record3<T1, T2, T3>>) newResult((Field<?>[]) new Field[]{field, field2, field3});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4> Result<Record4<T1, T2, T3, T4>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return (Result<Record4<T1, T2, T3, T4>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5> Result<Record5<T1, T2, T3, T4, T5>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return (Result<Record5<T1, T2, T3, T4, T5>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6> Result<Record6<T1, T2, T3, T4, T5, T6>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return (Result<Record6<T1, T2, T3, T4, T5, T6>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7> Result<Record7<T1, T2, T3, T4, T5, T6, T7>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return (Result<Record7<T1, T2, T3, T4, T5, T6, T7>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8> Result<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return (Result<Record8<T1, T2, T3, T4, T5, T6, T7, T8>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> Result<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return (Result<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Result<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return (Result<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Result<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return (Result<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Result<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return (Result<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Result<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return (Result<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Result<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return (Result<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Result<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return (Result<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Result<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return (Result<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Result<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return (Result<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Result<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return (Result<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Result<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return (Result<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Result<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return (Result<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Result<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return (Result<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Result<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return (Result<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>) newResult((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22});
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Result<R> fetch(ResultQuery<R> query) {
        Configuration configuration = configuration();
        Objects.requireNonNull(query);
        return (Result) Tools.attach(query, configuration, query::fetch);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Cursor<R> fetchLazy(ResultQuery<R> query) {
        Configuration configuration = configuration();
        Objects.requireNonNull(query);
        return (Cursor) Tools.attach(query, configuration, query::fetchLazy);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> CompletionStage<Result<R>> fetchAsync(ResultQuery<R> query) {
        return fetchAsync(Tools.configuration(configuration()).executorProvider().provide(), query);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Executor executor, ResultQuery<R> query) {
        return ExecutorProviderCompletionStage.of(CompletableFuture.supplyAsync(Tools.blocking(() -> {
            return fetch(query);
        })), () -> {
            return executor;
        });
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Stream<R> fetchStream(ResultQuery<R> query) {
        Configuration configuration = configuration();
        Objects.requireNonNull(query);
        return (Stream) Tools.attach(query, configuration, query::stream);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Results fetchMany(ResultQuery<R> query) {
        Configuration configuration = configuration();
        Objects.requireNonNull(query);
        return (Results) Tools.attach(query, configuration, query::fetchMany);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchOne(ResultQuery<R> query) {
        Configuration configuration = configuration();
        Objects.requireNonNull(query);
        return (R) Tools.attach(query, configuration, query::fetchOne);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(ResultQuery<R> query) {
        Configuration configuration = configuration();
        Objects.requireNonNull(query);
        return (R) Tools.attach(query, configuration, query::fetchSingle);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Optional<R> fetchOptional(ResultQuery<R> query) {
        return Optional.ofNullable(fetchOne(query));
    }

    @Override // org.jooq.DSLContext
    public <T> T fetchValue(Table<? extends Record1<T>> table) {
        return (T) fetchValue(selectFrom(table));
    }

    @Override // org.jooq.DSLContext
    public <T, R extends Record1<T>> T fetchValue(ResultQuery<R> resultQuery) {
        return (T) Tools.attach(resultQuery, configuration(), () -> {
            return value1((Record1) fetchOne(resultQuery));
        });
    }

    @Override // org.jooq.DSLContext
    public <T> T fetchValue(TableField<?, T> tableField) {
        return (T) fetchValue(tableField, DSL.noCondition());
    }

    @Override // org.jooq.DSLContext
    public <T> T fetchValue(TableField<?, T> tableField, Condition condition) {
        return (T) fetchValue(select(tableField).from(tableField.getTable()).where(condition));
    }

    @Override // org.jooq.DSLContext
    public <T> T fetchValue(SelectField<T> selectField) {
        if (selectField instanceof TableField) {
            return (T) fetchValue((TableField) selectField);
        }
        if (selectField instanceof Table) {
            return (T) fetchValue(select(selectField).from((Table) selectField));
        }
        return (T) fetchValue(select(selectField));
    }

    @Override // org.jooq.DSLContext
    public <T, R extends Record1<T>> Optional<T> fetchOptionalValue(ResultQuery<R> query) {
        return Optional.ofNullable(fetchValue(query));
    }

    @Override // org.jooq.DSLContext
    public <T> Optional<T> fetchOptionalValue(TableField<?, T> field) {
        return Optional.ofNullable(fetchValue((TableField) field));
    }

    @Override // org.jooq.DSLContext
    public <T> Optional<T> fetchOptionalValue(TableField<?, T> field, Condition condition) {
        return Optional.ofNullable(fetchValue(field, condition));
    }

    @Override // org.jooq.DSLContext
    public <T> List<T> fetchValues(Table<? extends Record1<T>> table) {
        return fetchValues(selectFrom(table));
    }

    @Override // org.jooq.DSLContext
    public <T, R extends Record1<T>> List<T> fetchValues(ResultQuery<R> resultQuery) {
        return (List<T>) fetch(resultQuery).getValues(0);
    }

    @Override // org.jooq.DSLContext
    public <T> List<T> fetchValues(TableField<?, T> field) {
        return fetchValues(field, DSL.noCondition());
    }

    @Override // org.jooq.DSLContext
    public <T> List<T> fetchValues(TableField<?, T> field, Condition condition) {
        return fetchValues(select(field).from(field.getTable()).where(condition));
    }

    @Override // org.jooq.DSLContext
    public <K, V> Map<K, V> fetchMap(ResultQuery<? extends Record2<K, V>> query) {
        return (Map) Tools.attach(query, configuration(), () -> {
            return (Map) query.collect(Records.intoMap());
        });
    }

    @Override // org.jooq.DSLContext
    public <K, V> Map<K, List<V>> fetchGroups(ResultQuery<? extends Record2<K, V>> query) {
        return (Map) Tools.attach(query, configuration(), () -> {
            return (Map) query.collect(Records.intoGroups());
        });
    }

    private final <T, R extends Record1<T>> T value1(R r) {
        if (r == null) {
            return null;
        }
        if (r.size() != 1) {
            throw new InvalidResultException("Record contains more than one value : " + String.valueOf(r));
        }
        return (T) r.value1();
    }

    @Override // org.jooq.DSLContext
    public <R extends TableRecord<R>> Result<R> fetchByExample(R r) {
        return (Result<R>) selectFrom(r.getTable()).where(DSL.condition(r)).fetch();
    }

    @Override // org.jooq.DSLContext
    public int fetchCount(Select<?> query) {
        return ((Integer) ((Record1) new FetchCount(configuration(), query).fetchOne()).value1()).intValue();
    }

    @Override // org.jooq.DSLContext
    public int fetchCount(Table<?> table) {
        return fetchCount(table, DSL.noCondition());
    }

    @Override // org.jooq.DSLContext
    public int fetchCount(Table<?> table, Condition condition) {
        return ((Integer) selectCount().from(table).where(condition).fetchOne(0, Integer.TYPE)).intValue();
    }

    @Override // org.jooq.DSLContext
    public int fetchCount(Table<?> table, Condition... conditions) {
        return fetchCount(table, DSL.and(conditions));
    }

    @Override // org.jooq.DSLContext
    public int fetchCount(Table<?> table, Collection<? extends Condition> conditions) {
        return fetchCount(table, DSL.and(conditions));
    }

    @Override // org.jooq.DSLContext
    public boolean fetchExists(Select<?> query) {
        return ((Boolean) fetchValue(DSL.exists(query))).booleanValue();
    }

    @Override // org.jooq.DSLContext
    public boolean fetchExists(Table<?> table) {
        return fetchExists(table, DSL.noCondition());
    }

    @Override // org.jooq.DSLContext
    public boolean fetchExists(Table<?> table, Condition condition) {
        return fetchExists(selectOne().from(table).where(condition));
    }

    @Override // org.jooq.DSLContext
    public boolean fetchExists(Table<?> table, Condition... conditions) {
        return fetchExists(table, DSL.and(conditions));
    }

    @Override // org.jooq.DSLContext
    public boolean fetchExists(Table<?> table, Collection<? extends Condition> conditions) {
        return fetchExists(table, DSL.and(conditions));
    }

    @Override // org.jooq.DSLContext
    public int execute(Query query) {
        Configuration configuration = configuration();
        Objects.requireNonNull(query);
        return ((Integer) Tools.attach(query, configuration, query::execute)).intValue();
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Result<R> fetch(Table<R> table) {
        return fetch(table, DSL.noCondition());
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Result<R> fetch(Table<R> table, Condition condition) {
        return selectFrom(table).where(condition).fetch();
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Result<R> fetch(Table<R> table, Condition... conditions) {
        return fetch(table, DSL.and(conditions));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Result<R> fetch(Table<R> table, Collection<? extends Condition> conditions) {
        return fetch(table, DSL.and(conditions));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchOne(Table<R> table) {
        return (R) fetchOne(table, DSL.noCondition());
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchOne(Table<R> table, Condition condition) {
        return (R) Tools.fetchOne(fetchLazy(table, condition));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchOne(Table<R> table, Condition... conditionArr) {
        return (R) fetchOne(table, DSL.and(conditionArr));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchOne(Table<R> table, Collection<? extends Condition> collection) {
        return (R) fetchOne(table, DSL.and(collection));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table) {
        return (R) fetchSingle((Table) table, DSL.noCondition());
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition) {
        return (R) Tools.fetchSingle(fetchLazy(table, condition));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition... conditionArr) {
        return (R) fetchSingle((Table) table, DSL.and(conditionArr));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Collection<? extends Condition> collection) {
        return (R) fetchSingle((Table) table, DSL.and(collection));
    }

    @Override // org.jooq.DSLContext
    public Record fetchSingle(SelectField<?>... fields) throws DataAccessException {
        return fetchSingle(Arrays.asList(fields));
    }

    @Override // org.jooq.DSLContext
    public Record fetchSingle(Collection<? extends SelectField<?>> fields) throws DataAccessException {
        return fetchSingle(DSL.select(fields));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1> Record1<T1> fetchSingle(SelectField<T1> field1) {
        return (Record1) fetchSingle((SelectField<?>[]) new SelectField[]{field1});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2> Record2<T1, T2> fetchSingle(SelectField<T1> field1, SelectField<T2> field2) {
        return (Record2) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3> Record3<T1, T2, T3> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3) {
        return (Record3) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4> Record4<T1, T2, T3, T4> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4) {
        return (Record4) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5> Record5<T1, T2, T3, T4, T5> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5) {
        return (Record5) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6> Record6<T1, T2, T3, T4, T5, T6> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6) {
        return (Record6) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7> Record7<T1, T2, T3, T4, T5, T6, T7> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7) {
        return (Record7) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8> Record8<T1, T2, T3, T4, T5, T6, T7, T8> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8) {
        return (Record8) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9) {
        return (Record9) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10) {
        return (Record10) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11) {
        return (Record11) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12) {
        return (Record12) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13) {
        return (Record13) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14) {
        return (Record14) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15) {
        return (Record15) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16) {
        return (Record16) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17) {
        return (Record17) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18) {
        return (Record18) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19) {
        return (Record19) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20) {
        return (Record20) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20, SelectField<T21> field21) {
        return (Record21) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DSLContext
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20, SelectField<T21> field21, SelectField<T22> field22) {
        return (Record22) fetchSingle((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22});
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2) {
        return (R) fetchSingle(table, condition, condition2);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3) {
        return (R) fetchSingle(table, condition, condition2, condition3);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6, condition7);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6, condition7, condition8);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6, condition7, condition8, condition9);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6, condition7, condition8, condition9, condition10);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6, condition7, condition8, condition9, condition10, condition11);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6, condition7, condition8, condition9, condition10, condition11, condition12);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6, condition7, condition8, condition9, condition10, condition11, condition12, condition13);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6, condition7, condition8, condition9, condition10, condition11, condition12, condition13, condition14);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14, Condition condition15) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6, condition7, condition8, condition9, condition10, condition11, condition12, condition13, condition14, condition15);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14, Condition condition15, Condition condition16) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6, condition7, condition8, condition9, condition10, condition11, condition12, condition13, condition14, condition15, condition16);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14, Condition condition15, Condition condition16, Condition condition17) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6, condition7, condition8, condition9, condition10, condition11, condition12, condition13, condition14, condition15, condition16, condition17);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14, Condition condition15, Condition condition16, Condition condition17, Condition condition18) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6, condition7, condition8, condition9, condition10, condition11, condition12, condition13, condition14, condition15, condition16, condition17, condition18);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14, Condition condition15, Condition condition16, Condition condition17, Condition condition18, Condition condition19) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6, condition7, condition8, condition9, condition10, condition11, condition12, condition13, condition14, condition15, condition16, condition17, condition18, condition19);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14, Condition condition15, Condition condition16, Condition condition17, Condition condition18, Condition condition19, Condition condition20) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6, condition7, condition8, condition9, condition10, condition11, condition12, condition13, condition14, condition15, condition16, condition17, condition18, condition19, condition20);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14, Condition condition15, Condition condition16, Condition condition17, Condition condition18, Condition condition19, Condition condition20, Condition condition21) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6, condition7, condition8, condition9, condition10, condition11, condition12, condition13, condition14, condition15, condition16, condition17, condition18, condition19, condition20, condition21);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14, Condition condition15, Condition condition16, Condition condition17, Condition condition18, Condition condition19, Condition condition20, Condition condition21, Condition condition22) {
        return (R) fetchSingle(table, condition, condition2, condition3, condition4, condition5, condition6, condition7, condition8, condition9, condition10, condition11, condition12, condition13, condition14, condition15, condition16, condition17, condition18, condition19, condition20, condition21, condition22);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Optional<R> fetchOptional(Table<R> table) {
        return fetchOptional(table, DSL.noCondition());
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Optional<R> fetchOptional(Table<R> table, Condition condition) {
        return Optional.ofNullable(fetchOne(table, condition));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Optional<R> fetchOptional(Table<R> table, Condition... conditions) {
        return fetchOptional(table, DSL.and(conditions));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Optional<R> fetchOptional(Table<R> table, Collection<? extends Condition> conditions) {
        return fetchOptional(table, DSL.and(conditions));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchAny(Table<R> table) {
        return (R) fetchAny(table, DSL.noCondition());
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchAny(Table<R> table, Condition condition) {
        return selectFrom(table).where(condition).limit((Number) 1).fetchOne();
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchAny(Table<R> table, Condition... conditionArr) {
        return (R) fetchAny(table, DSL.and(conditionArr));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> R fetchAny(Table<R> table, Collection<? extends Condition> collection) {
        return (R) fetchAny(table, DSL.and(collection));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Cursor<R> fetchLazy(Table<R> table) {
        return fetchLazy(table, DSL.noCondition());
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Cursor<R> fetchLazy(Table<R> table, Condition condition) {
        return selectFrom(table).where(condition).fetchLazy();
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Cursor<R> fetchLazy(Table<R> table, Condition... conditions) {
        return fetchLazy(table, DSL.and(conditions));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Cursor<R> fetchLazy(Table<R> table, Collection<? extends Condition> conditions) {
        return fetchLazy(table, DSL.and(conditions));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Table<R> table) {
        return fetchAsync(table, DSL.noCondition());
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Table<R> table, Condition condition) {
        return selectFrom(table).where(condition).fetchAsync();
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Table<R> table, Condition... conditions) {
        return fetchAsync(table, DSL.and(conditions));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Table<R> table, Collection<? extends Condition> conditions) {
        return fetchAsync(table, DSL.and(conditions));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Executor executor, Table<R> table) {
        return fetchAsync(executor, table, DSL.noCondition());
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Executor executor, Table<R> table, Condition condition) {
        return selectFrom(table).where(condition).fetchAsync(executor);
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Executor executor, Table<R> table, Condition... conditions) {
        return fetchAsync(executor, table, DSL.and(conditions));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Executor executor, Table<R> table, Collection<? extends Condition> conditions) {
        return fetchAsync(executor, table, DSL.and(conditions));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Stream<R> fetchStream(Table<R> table) {
        return fetchStream(table, DSL.noCondition());
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Stream<R> fetchStream(Table<R> table, Condition condition) {
        return selectFrom(table).where(condition).stream();
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Stream<R> fetchStream(Table<R> table, Condition... conditions) {
        return fetchStream(table, DSL.and(conditions));
    }

    @Override // org.jooq.DSLContext
    public <R extends Record> Stream<R> fetchStream(Table<R> table, Collection<? extends Condition> conditions) {
        return fetchStream(table, DSL.and(conditions));
    }

    @Override // org.jooq.DSLContext
    public int executeInsert(TableRecord<?> record) {
        InsertQuery insert = insertQuery(record.getTable());
        insert.setRecord(record);
        return insert.execute();
    }

    @Override // org.jooq.DSLContext
    public int executeUpdate(UpdatableRecord<?> record) {
        UpdateQuery update = updateQuery(record.getTable());
        Tools.addConditions(update, record, record.getTable().getPrimaryKey().getFieldsArray());
        update.setRecord(record);
        return update.execute();
    }

    @Override // org.jooq.DSLContext
    public int executeUpdate(TableRecord<?> record, Condition condition) {
        UpdateQuery update = updateQuery(record.getTable());
        update.addConditions(condition);
        update.setRecord(record);
        return update.execute();
    }

    @Override // org.jooq.DSLContext
    public int executeDelete(UpdatableRecord<?> record) {
        DeleteQuery delete = deleteQuery(record.getTable());
        Tools.addConditions(delete, record, record.getTable().getPrimaryKey().getFieldsArray());
        return delete.execute();
    }

    @Override // org.jooq.DSLContext
    public int executeDelete(TableRecord<?> record, Condition condition) {
        DeleteQuery delete = deleteQuery(record.getTable());
        delete.addConditions(condition);
        return delete.execute();
    }

    static {
        try {
            Class.forName(SQLDataType.class.getName());
        } catch (Exception e) {
        }
    }

    public String toString() {
        return configuration().toString();
    }
}
