package org.jooq.impl;

import io.r2dbc.spi.ConnectionFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import javax.sql.DataSource;
import org.jooq.CacheProvider;
import org.jooq.CharsetProvider;
import org.jooq.CommitProvider;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.ConverterProvider;
import org.jooq.DSLContext;
import org.jooq.DiagnosticsListener;
import org.jooq.DiagnosticsListenerProvider;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteListenerProvider;
import org.jooq.ExecutorProvider;
import org.jooq.FormattingProvider;
import org.jooq.MetaProvider;
import org.jooq.MigrationListenerProvider;
import org.jooq.Record;
import org.jooq.RecordListener;
import org.jooq.RecordListenerProvider;
import org.jooq.RecordMapper;
import org.jooq.RecordMapperProvider;
import org.jooq.RecordType;
import org.jooq.RecordUnmapper;
import org.jooq.RecordUnmapperProvider;
import org.jooq.SQLDialect;
import org.jooq.SchemaMapping;
import org.jooq.TransactionListener;
import org.jooq.TransactionListenerProvider;
import org.jooq.TransactionProvider;
import org.jooq.Unwrapper;
import org.jooq.UnwrapperProvider;
import org.jooq.VisitListener;
import org.jooq.VisitListenerProvider;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.exception.ConfigurationException;
import org.jooq.impl.DefaultConnectionFactory;
import org.jooq.impl.DefaultExecuteContext;
import org.jooq.impl.ThreadLocalTransactionProvider;
import org.jooq.impl.Tools;
import org.jooq.migrations.xml.jaxb.MigrationsType;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultConfiguration.class */
public class DefaultConfiguration extends AbstractConfiguration {
    private SQLDialect dialect;
    private Settings settings;
    private Clock clock;
    private transient ConnectionProvider connectionProvider;
    private transient ConnectionProvider interpreterConnectionProvider;
    private transient ConnectionProvider systemConnectionProvider;
    private transient ConnectionFactory connectionFactory;
    private transient MetaProvider metaProvider;
    private transient CommitProvider commitProvider;
    private transient ExecutorProvider executorProvider;
    private transient CacheProvider cacheProvider;
    private transient TransactionProvider transactionProvider;
    private transient RecordMapperProvider recordMapperProvider;
    private transient RecordUnmapperProvider recordUnmapperProvider;
    private transient RecordListenerProvider[] recordListenerProviders;
    private transient ExecuteListenerProvider[] executeListenerProviders;
    private transient MigrationListenerProvider[] migrationListenerProviders;
    private transient VisitListenerProvider[] visitListenerProviders;
    private transient TransactionListenerProvider[] transactionListenerProviders;
    private transient DiagnosticsListenerProvider[] diagnosticsListenerProviders;
    private transient UnwrapperProvider unwrapperProvider;
    private transient CharsetProvider charsetProvider;
    private transient ConverterProvider converterProvider;
    private transient FormattingProvider formattingProvider;
    private transient ConcurrentHashMap<Object, Object> data;
    private SchemaMapping mapping;
    private static final String END_OF_MAP_MARKER = "EOM";

    public DefaultConfiguration() {
        this(SQLDialect.DEFAULT);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultConfiguration(SQLDialect dialect) {
        this(null, dialect, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultConfiguration(ConnectionProvider connectionProvider, SQLDialect dialect, Settings settings) {
        this(connectionProvider, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, dialect, settings, null);
    }

    DefaultConfiguration(DefaultConfiguration configuration) {
        this(configuration.connectionProvider, configuration.interpreterConnectionProvider, configuration.systemConnectionProvider, configuration.connectionFactory, configuration.metaProvider, configuration.commitProvider, configuration.executorProvider, configuration.cacheProvider, configuration.transactionProvider, configuration.recordMapperProvider, configuration.recordUnmapperProvider, configuration.recordListenerProviders, configuration.executeListenerProviders, configuration.migrationListenerProviders, configuration.visitListenerProviders, configuration.transactionListenerProviders, configuration.diagnosticsListenerProviders, configuration.unwrapperProvider, configuration.charsetProvider, configuration.converterProvider, configuration.formattingProvider, configuration.clock, configuration.dialect, configuration.settings, configuration.data);
    }

    DefaultConfiguration(ConnectionProvider connectionProvider, ConnectionProvider interpreterConnectionProvider, ConnectionProvider systemConnectionProvider, ConnectionFactory connectionFactory, MetaProvider metaProvider, CommitProvider commitProvider, ExecutorProvider executorProvider, CacheProvider cacheProvider, TransactionProvider transactionProvider, RecordMapperProvider recordMapperProvider, RecordUnmapperProvider recordUnmapperProvider, RecordListenerProvider[] recordListenerProviders, ExecuteListenerProvider[] executeListenerProviders, MigrationListenerProvider[] migrationListenerProviders, VisitListenerProvider[] visitListenerProviders, TransactionListenerProvider[] transactionListenerProviders, DiagnosticsListenerProvider[] diagnosticsListenerProviders, UnwrapperProvider unwrapperProvider, CharsetProvider charsetProvider, ConverterProvider converterProvider, FormattingProvider formattingProvider, Clock clock, SQLDialect dialect, Settings settings, Map<Object, Object> data) {
        ConcurrentHashMap<Object, Object> concurrentHashMap;
        set(connectionProvider);
        setInterpreterConnectionProvider(interpreterConnectionProvider);
        setSystemConnectionProvider(systemConnectionProvider);
        set(connectionFactory);
        set(metaProvider);
        set(commitProvider);
        set(executorProvider);
        set(cacheProvider);
        set(transactionProvider);
        set(recordMapperProvider);
        set(recordUnmapperProvider);
        set(recordListenerProviders);
        set(executeListenerProviders);
        set(migrationListenerProviders);
        set(visitListenerProviders);
        set(transactionListenerProviders);
        set(diagnosticsListenerProviders);
        set(unwrapperProvider);
        set(charsetProvider);
        set(converterProvider);
        set(formattingProvider);
        set(clock);
        set(dialect);
        set(settings);
        if (data != null) {
            concurrentHashMap = new ConcurrentHashMap<>((Map<? extends Object, ? extends Object>) data);
        } else {
            concurrentHashMap = new ConcurrentHashMap<>();
        }
        this.data = concurrentHashMap;
    }

    @Override // org.jooq.Configuration
    public final DSLContext dsl() {
        return DSL.using(this);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive() {
        return new DefaultConfiguration(this);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(Connection newConnection) {
        return derive(new DefaultConnectionProvider(newConnection));
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(DataSource newDataSource) {
        return derive(new DataSourceConnectionProvider(newDataSource));
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(ConnectionFactory newConnectionFactory) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, newConnectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(ConnectionProvider newConnectionProvider) {
        return new DefaultConfiguration(newConnectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(MetaProvider newMetaProvider) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, newMetaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(CommitProvider newCommitProvider) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, newCommitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(Executor newExecutor) {
        return derive(new ExecutorWrapper(newExecutor));
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(ExecutorProvider newExecutorProvider) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, newExecutorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(CacheProvider newCacheProvider) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, newCacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(TransactionProvider newTransactionProvider) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, newTransactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(RecordMapper<?, ?> newRecordMapper) {
        return derive(new RecordMapperWrapper(newRecordMapper));
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(RecordMapperProvider newRecordMapperProvider) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, newRecordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(RecordUnmapper<?, ?> newRecordUnmapper) {
        return derive(new RecordUnmapperWrapper(newRecordUnmapper));
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(RecordUnmapperProvider newRecordUnmapperProvider) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, newRecordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(RecordListenerProvider... newRecordListenerProviders) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, newRecordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(ExecuteListenerProvider... newExecuteListenerProviders) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, newExecuteListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(MigrationListenerProvider... newMigrationListenerProviders) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, newMigrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(VisitListenerProvider... newVisitListenerProviders) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, newVisitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(TransactionListenerProvider... newTransactionListenerProviders) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, newTransactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(DiagnosticsListenerProvider... newDiagnosticsListenerProviders) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, newDiagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(Unwrapper newUnwrapper) {
        return derive(new UnwrapperWrapper(newUnwrapper));
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(UnwrapperProvider newUnwrapperProvider) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, newUnwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(CharsetProvider newCharsetProvider) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, newCharsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(ConverterProvider newConverterProvider) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, newConverterProvider, this.formattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(FormattingProvider newFormattingProvider) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, newFormattingProvider, this.clock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(Clock newClock) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, newClock, this.dialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(SQLDialect newDialect) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, newDialect, this.settings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(Settings newSettings) {
        return new DefaultConfiguration(this.connectionProvider, this.interpreterConnectionProvider, this.systemConnectionProvider, this.connectionFactory, this.metaProvider, this.commitProvider, this.executorProvider, this.cacheProvider, this.transactionProvider, this.recordMapperProvider, this.recordUnmapperProvider, this.recordListenerProviders, this.executeListenerProviders, this.migrationListenerProviders, this.visitListenerProviders, this.transactionListenerProviders, this.diagnosticsListenerProviders, this.unwrapperProvider, this.charsetProvider, this.converterProvider, this.formattingProvider, this.clock, this.dialect, newSettings, this.data);
    }

    @Override // org.jooq.Configuration
    public final Configuration deriveSettings(java.util.function.Function<? super Settings, ? extends Settings> newSettings) {
        return derive(newSettings.apply(SettingsTools.clone(this.settings)));
    }

    @Override // org.jooq.Configuration
    public final Configuration set(Connection newConnection) {
        return set(new DefaultConnectionProvider(newConnection));
    }

    @Override // org.jooq.Configuration
    public final Configuration set(DataSource newDataSource) {
        return set(new DataSourceConnectionProvider(newDataSource));
    }

    @Override // org.jooq.Configuration
    public final Configuration set(ConnectionProvider newConnectionProvider) {
        if (newConnectionProvider != null) {
            if ((this.transactionProvider instanceof ThreadLocalTransactionProvider) && !(newConnectionProvider instanceof ThreadLocalTransactionProvider.ThreadLocalConnectionProvider)) {
                throw new ConfigurationException("Cannot specify custom ConnectionProvider when Configuration contains a ThreadLocalTransactionProvider");
            }
            this.connectionProvider = newConnectionProvider;
        } else {
            this.connectionProvider = new NoConnectionProvider();
        }
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(ConnectionFactory newConnectionFactory) {
        this.connectionFactory = newConnectionFactory;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(MetaProvider newMetaProvider) {
        this.metaProvider = newMetaProvider;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(CommitProvider newCommitProvider) {
        this.commitProvider = newCommitProvider;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(Executor newExecutor) {
        return set(new ExecutorWrapper(newExecutor));
    }

    @Override // org.jooq.Configuration
    public final Configuration set(ExecutorProvider newExecutorProvider) {
        this.executorProvider = newExecutorProvider;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(CacheProvider newCacheProvider) {
        this.cacheProvider = newCacheProvider;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(TransactionProvider newTransactionProvider) {
        if (newTransactionProvider != null && !(this.connectionProvider instanceof DefaultExecuteContext.ExecuteContextConnectionProvider)) {
            this.transactionProvider = newTransactionProvider;
            if (newTransactionProvider instanceof ThreadLocalTransactionProvider) {
                ThreadLocalTransactionProvider t = (ThreadLocalTransactionProvider) newTransactionProvider;
                this.connectionProvider = t.localConnectionProvider;
            }
        } else {
            this.transactionProvider = new NoTransactionProvider();
        }
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(RecordMapper<?, ?> newRecordMapper) {
        return set(new RecordMapperWrapper(newRecordMapper));
    }

    @Override // org.jooq.Configuration
    public final Configuration set(RecordMapperProvider newRecordMapperProvider) {
        this.recordMapperProvider = newRecordMapperProvider;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(RecordUnmapper<?, ?> newRecordUnmapper) {
        return set(new RecordUnmapperWrapper(newRecordUnmapper));
    }

    @Override // org.jooq.Configuration
    public final Configuration set(RecordUnmapperProvider newRecordUnmapperProvider) {
        this.recordUnmapperProvider = newRecordUnmapperProvider;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(RecordListenerProvider... newRecordListenerProviders) {
        RecordListenerProvider[] recordListenerProviderArr;
        if (newRecordListenerProviders != null) {
            recordListenerProviderArr = newRecordListenerProviders;
        } else {
            recordListenerProviderArr = new RecordListenerProvider[0];
        }
        this.recordListenerProviders = recordListenerProviderArr;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(ExecuteListenerProvider... newExecuteListenerProviders) {
        ExecuteListenerProvider[] executeListenerProviderArr;
        if (newExecuteListenerProviders != null) {
            executeListenerProviderArr = newExecuteListenerProviders;
        } else {
            executeListenerProviderArr = new ExecuteListenerProvider[0];
        }
        this.executeListenerProviders = executeListenerProviderArr;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(MigrationListenerProvider... newMigrationListenerProviders) {
        MigrationListenerProvider[] migrationListenerProviderArr;
        if (newMigrationListenerProviders != null) {
            migrationListenerProviderArr = newMigrationListenerProviders;
        } else {
            migrationListenerProviderArr = new MigrationListenerProvider[0];
        }
        this.migrationListenerProviders = migrationListenerProviderArr;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(VisitListenerProvider... newVisitListenerProviders) {
        VisitListenerProvider[] visitListenerProviderArr;
        if (newVisitListenerProviders != null) {
            visitListenerProviderArr = newVisitListenerProviders;
        } else {
            visitListenerProviderArr = new VisitListenerProvider[0];
        }
        this.visitListenerProviders = visitListenerProviderArr;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(TransactionListenerProvider... newTransactionListenerProviders) {
        TransactionListenerProvider[] transactionListenerProviderArr;
        if (newTransactionListenerProviders != null) {
            transactionListenerProviderArr = newTransactionListenerProviders;
        } else {
            transactionListenerProviderArr = new TransactionListenerProvider[0];
        }
        this.transactionListenerProviders = transactionListenerProviderArr;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(DiagnosticsListenerProvider... newDiagnosticsListenerProviders) {
        DiagnosticsListenerProvider[] diagnosticsListenerProviderArr;
        if (newDiagnosticsListenerProviders != null) {
            diagnosticsListenerProviderArr = newDiagnosticsListenerProviders;
        } else {
            diagnosticsListenerProviderArr = new DiagnosticsListenerProvider[0];
        }
        this.diagnosticsListenerProviders = diagnosticsListenerProviderArr;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(Unwrapper newUnwrapper) {
        if (newUnwrapper != null) {
            return set(new UnwrapperWrapper(newUnwrapper));
        }
        return set((UnwrapperProvider) null);
    }

    @Override // org.jooq.Configuration
    public final Configuration set(UnwrapperProvider newUnwrapperProvider) {
        this.unwrapperProvider = newUnwrapperProvider;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(CharsetProvider newCharsetProvider) {
        this.charsetProvider = newCharsetProvider;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(ConverterProvider newConverterProvider) {
        this.converterProvider = newConverterProvider;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(FormattingProvider newFormattingProvider) {
        this.formattingProvider = newFormattingProvider;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(Clock newClock) {
        this.clock = newClock == null ? Clock.systemUTC() : newClock;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(SQLDialect newDialect) {
        this.dialect = newDialect == null ? SQLDialect.DEFAULT : newDialect;
        return this;
    }

    @Override // org.jooq.Configuration
    public final Configuration set(Settings newSettings) {
        Settings defaultSettings;
        if (newSettings != null) {
            defaultSettings = SettingsTools.clone(newSettings);
        } else {
            defaultSettings = SettingsTools.defaultSettings();
        }
        this.settings = defaultSettings;
        this.mapping = new SchemaMapping(this);
        return this;
    }

    public final void setConnection(Connection newConnection) {
        set(newConnection);
    }

    public final void setDataSource(DataSource newDataSource) {
        set(newDataSource);
    }

    public final void setConnectionFactory(ConnectionFactory newConnectionFactory) {
        set(newConnectionFactory);
    }

    public final void setConnectionProvider(ConnectionProvider newConnectionProvider) {
        set(newConnectionProvider);
    }

    public final void setInterpreterConnectionProvider(ConnectionProvider newInterpreterConnectionProvider) {
        this.interpreterConnectionProvider = newInterpreterConnectionProvider;
    }

    public final void setSystemConnectionProvider(ConnectionProvider newSystemConnectionProvider) {
        this.systemConnectionProvider = newSystemConnectionProvider;
    }

    public final void setMetaProvider(MetaProvider newMetaProvider) {
        set(newMetaProvider);
    }

    public final void setVersionProvider(CommitProvider newCommitProvider) {
        set(newCommitProvider);
    }

    public final void setExecutor(Executor newExecutor) {
        set(newExecutor);
    }

    public final void setExecutorProvider(ExecutorProvider newExecutorProvider) {
        set(newExecutorProvider);
    }

    public final void setCacheProvider(CacheProvider newCacheProvider) {
        set(newCacheProvider);
    }

    public final void setTransactionProvider(TransactionProvider newTransactionProvider) {
        set(newTransactionProvider);
    }

    public final void setRecordMapper(RecordMapper<?, ?> newRecordMapper) {
        set(newRecordMapper);
    }

    public final void setRecordMapperProvider(RecordMapperProvider newRecordMapperProvider) {
        set(newRecordMapperProvider);
    }

    public final void setRecordUnmapper(RecordUnmapper<?, ?> newRecordUnmapper) {
        set(newRecordUnmapper);
    }

    public final void setRecordUnmapperProvider(RecordUnmapperProvider newRecordUnmapperProvider) {
        set(newRecordUnmapperProvider);
    }

    public final void setRecordListener(RecordListener... newRecordListeners) {
        set(newRecordListeners);
    }

    public final void setRecordListenerProvider(RecordListenerProvider... newRecordListenerProviders) {
        set(newRecordListenerProviders);
    }

    public final void setExecuteListener(ExecuteListener... newExecuteListeners) {
        set(newExecuteListeners);
    }

    public final void setExecuteListenerProvider(ExecuteListenerProvider... newExecuteListenerProviders) {
        set(newExecuteListenerProviders);
    }

    public final void setVisitListener(VisitListener... newVisitListeners) {
        set(newVisitListeners);
    }

    public final void setVisitListenerProvider(VisitListenerProvider... newVisitListenerProviders) {
        set(newVisitListenerProviders);
    }

    public final void setTransactionListener(TransactionListener... newTransactionListeners) {
        set(newTransactionListeners);
    }

    public final void setTransactionListenerProvider(TransactionListenerProvider... newTransactionListenerProviders) {
        set(newTransactionListenerProviders);
    }

    public final void setDiagnosticsListener(DiagnosticsListener... newDiagnosticsListener) {
        set(newDiagnosticsListener);
    }

    public final void setDiagnosticsListenerProvider(DiagnosticsListenerProvider... newDiagnosticsListenerProviders) {
        set(newDiagnosticsListenerProviders);
    }

    public final void setUnwrapper(Unwrapper newUnwrapper) {
        set(newUnwrapper);
    }

    public final void setUnwrapperProvider(UnwrapperProvider newUnwrapperProvider) {
        set(newUnwrapperProvider);
    }

    public final void setClock(Clock newClock) {
        set(newClock);
    }

    public final void setSQLDialect(SQLDialect newDialect) {
        set(newDialect);
    }

    public final void setSettings(Settings newSettings) {
        set(newSettings);
    }

    @Override // org.jooq.Configuration
    public final ConnectionProvider connectionProvider() {
        ConnectionProvider connectionProvider;
        TransactionProvider tp = transactionProvider();
        if (tp instanceof ThreadLocalTransactionProvider) {
            ThreadLocalTransactionProvider t = (ThreadLocalTransactionProvider) tp;
            connectionProvider = t.localConnectionProvider;
        } else {
            connectionProvider = (ConnectionProvider) data(Tools.SimpleDataKey.DATA_DEFAULT_TRANSACTION_PROVIDER_CONNECTION);
        }
        ConnectionProvider transactional = connectionProvider;
        if (transactional != null) {
            return transactional;
        }
        if (this.connectionProvider != null) {
            return this.connectionProvider;
        }
        return new NoConnectionProvider();
    }

    @Override // org.jooq.Configuration
    public final ConnectionProvider interpreterConnectionProvider() {
        if (this.interpreterConnectionProvider != null) {
            return this.interpreterConnectionProvider;
        }
        return new DefaultInterpreterConnectionProvider(this);
    }

    @Override // org.jooq.Configuration
    public final ConnectionProvider systemConnectionProvider() {
        if (this.systemConnectionProvider != null) {
            return this.systemConnectionProvider;
        }
        return connectionProvider();
    }

    @Override // org.jooq.Configuration
    public final ConnectionFactory connectionFactory() {
        if (this.connectionFactory != null) {
            return this.connectionFactory;
        }
        return new NoConnectionFactory();
    }

    @Override // org.jooq.Configuration
    public final MetaProvider metaProvider() {
        if (this.metaProvider != null) {
            return this.metaProvider;
        }
        return new DefaultMetaProvider(this);
    }

    @Override // org.jooq.Configuration
    public final CommitProvider commitProvider() {
        if (this.commitProvider != null) {
            return this.commitProvider;
        }
        return new DefaultCommitProvider(this, new MigrationsType());
    }

    @Override // org.jooq.Configuration
    public final ExecutorProvider executorProvider() {
        if (this.executorProvider != null) {
            return this.executorProvider;
        }
        return new DefaultExecutorProvider();
    }

    @Override // org.jooq.Configuration
    public final CacheProvider cacheProvider() {
        if (this.cacheProvider != null) {
            return this.cacheProvider;
        }
        return new DefaultCacheProvider();
    }

    @Override // org.jooq.Configuration
    public final TransactionProvider transactionProvider() {
        if (this.transactionProvider == null || (this.transactionProvider instanceof NoTransactionProvider)) {
            return new DefaultTransactionProvider(this.connectionProvider);
        }
        return this.transactionProvider;
    }

    @Override // org.jooq.Configuration
    public final RecordMapperProvider recordMapperProvider() {
        if (this.recordMapperProvider != null) {
            return this.recordMapperProvider;
        }
        return new DefaultRecordMapperProvider(this);
    }

    @Override // org.jooq.Configuration
    public final RecordUnmapperProvider recordUnmapperProvider() {
        if (this.recordUnmapperProvider != null) {
            return this.recordUnmapperProvider;
        }
        return new DefaultRecordUnmapperProvider(this);
    }

    @Override // org.jooq.Configuration
    public final RecordListenerProvider[] recordListenerProviders() {
        return this.recordListenerProviders;
    }

    @Override // org.jooq.Configuration
    public final ExecuteListenerProvider[] executeListenerProviders() {
        return this.executeListenerProviders;
    }

    @Override // org.jooq.Configuration
    public final MigrationListenerProvider[] migrationListenerProviders() {
        return this.migrationListenerProviders;
    }

    @Override // org.jooq.Configuration
    public final VisitListenerProvider[] visitListenerProviders() {
        return this.visitListenerProviders;
    }

    @Override // org.jooq.Configuration
    public final TransactionListenerProvider[] transactionListenerProviders() {
        return this.transactionListenerProviders;
    }

    @Override // org.jooq.Configuration
    public final DiagnosticsListenerProvider[] diagnosticsListenerProviders() {
        return this.diagnosticsListenerProviders;
    }

    @Override // org.jooq.Configuration
    public final UnwrapperProvider unwrapperProvider() {
        if (this.unwrapperProvider != null) {
            return this.unwrapperProvider;
        }
        return new DefaultUnwrapperProvider();
    }

    @Override // org.jooq.Configuration
    public final CharsetProvider charsetProvider() {
        if (this.charsetProvider != null) {
            return this.charsetProvider;
        }
        return new DefaultCharsetProvider();
    }

    @Override // org.jooq.Configuration
    public final ConverterProvider converterProvider() {
        if (this.converterProvider != null) {
            return this.converterProvider;
        }
        return new DefaultConverterProvider();
    }

    @Override // org.jooq.Configuration
    public final FormattingProvider formattingProvider() {
        if (this.formattingProvider != null) {
            return this.formattingProvider;
        }
        return new DefaultFormattingProvider();
    }

    @Override // org.jooq.Configuration
    public final Clock clock() {
        return this.clock;
    }

    @Override // org.jooq.Configuration
    public final SQLDialect dialect() {
        return this.dialect;
    }

    @Override // org.jooq.Configuration
    public final SQLDialect family() {
        return this.dialect.family();
    }

    @Override // org.jooq.Configuration
    public final Settings settings() {
        return this.settings;
    }

    @Override // org.jooq.Configuration
    public final ConcurrentHashMap<Object, Object> data() {
        return this.data;
    }

    @Override // org.jooq.Configuration
    public final Object data(Object key) {
        return this.data.get(key);
    }

    @Override // org.jooq.Configuration
    public final Object data(Object key, Object value) {
        return this.data.put(key, value);
    }

    @Override // org.jooq.Configuration
    @Deprecated
    public final SchemaMapping schemaMapping() {
        if (this.mapping == null) {
            this.mapping = new SchemaMapping(this);
        }
        return this.mapping;
    }

    public String toString() {
        return "DefaultConfiguration [\n\tconnected=" + (((this.connectionProvider == null || (this.connectionProvider instanceof NoConnectionProvider)) && (this.connectionFactory == null || (this.connectionFactory instanceof NoConnectionFactory))) ? false : true) + ",\n\ttransactional=" + (!(this.transactionProvider == null || (this.transactionProvider instanceof NoTransactionProvider)) || ((this.connectionFactory instanceof DefaultConnectionFactory) && (((DefaultConnectionFactory) this.connectionFactory).connection instanceof DefaultConnectionFactory.NonClosingConnection))) + ",\n\tdialect=" + String.valueOf(this.dialect) + ",\n\tdata=" + String.valueOf(this.data) + ",\n\tsettings=\n\t\t" + String.valueOf(this.settings) + "\n]";
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(serializableOrNull(this.connectionProvider));
        oos.writeObject(serializableOrNull(this.interpreterConnectionProvider));
        oos.writeObject(serializableOrNull(this.systemConnectionProvider));
        oos.writeObject(serializableOrNull(this.metaProvider));
        oos.writeObject(serializableOrNull(this.commitProvider));
        oos.writeObject(serializableOrNull(this.transactionProvider));
        oos.writeObject(serializableOrNull(this.recordMapperProvider));
        oos.writeObject(serializableOrNull(this.recordUnmapperProvider));
        oos.writeObject(cloneSerializables(this.executeListenerProviders));
        oos.writeObject(cloneSerializables(this.recordListenerProviders));
        oos.writeObject(cloneSerializables(this.visitListenerProviders));
        oos.writeObject(cloneSerializables(this.transactionListenerProviders));
        oos.writeObject(cloneSerializables(this.diagnosticsListenerProviders));
        oos.writeObject(serializableOrNull(this.unwrapperProvider));
        oos.writeObject(serializableOrNull(this.charsetProvider));
        oos.writeObject(serializableOrNull(this.converterProvider));
        oos.writeObject(serializableOrNull(this.formattingProvider));
        for (Map.Entry<Object, Object> entry : this.data.entrySet()) {
            if (!(entry.getKey() instanceof CacheType)) {
                oos.writeObject(entry.getKey());
                oos.writeObject(entry.getValue());
            }
        }
        oos.writeObject(END_OF_MAP_MARKER);
    }

    private final Serializable serializableOrNull(Object o) {
        if (!(o instanceof Serializable)) {
            return null;
        }
        Serializable s = (Serializable) o;
        return s;
    }

    private final <E> E[] cloneSerializables(E[] eArr) {
        E[] eArr2 = (E[]) ((Object[]) eArr.clone());
        for (int i = 0; i < eArr2.length; i++) {
            if (!(eArr2[i] instanceof Serializable)) {
                eArr2[i] = null;
            }
        }
        return eArr2;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.connectionProvider = (ConnectionProvider) ois.readObject();
        this.interpreterConnectionProvider = (ConnectionProvider) ois.readObject();
        this.systemConnectionProvider = (ConnectionProvider) ois.readObject();
        this.metaProvider = (MetaProvider) ois.readObject();
        this.commitProvider = (CommitProvider) ois.readObject();
        this.transactionProvider = (TransactionProvider) ois.readObject();
        this.recordMapperProvider = (RecordMapperProvider) ois.readObject();
        this.recordUnmapperProvider = (RecordUnmapperProvider) ois.readObject();
        this.executeListenerProviders = (ExecuteListenerProvider[]) ois.readObject();
        this.recordListenerProviders = (RecordListenerProvider[]) ois.readObject();
        this.visitListenerProviders = (VisitListenerProvider[]) ois.readObject();
        this.transactionListenerProviders = (TransactionListenerProvider[]) ois.readObject();
        this.diagnosticsListenerProviders = (DiagnosticsListenerProvider[]) ois.readObject();
        this.unwrapperProvider = (UnwrapperProvider) ois.readObject();
        this.charsetProvider = (CharsetProvider) ois.readObject();
        this.converterProvider = (ConverterProvider) ois.readObject();
        this.formattingProvider = (FormattingProvider) ois.readObject();
        this.data = new ConcurrentHashMap<>();
        while (true) {
            Object key = ois.readObject();
            if (!END_OF_MAP_MARKER.equals(key)) {
                Object value = ois.readObject();
                this.data.put(key, value);
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultConfiguration$ExecutorWrapper.class */
    public final class ExecutorWrapper implements ExecutorProvider {
        private final Executor newExecutor;

        private ExecutorWrapper(Executor newExecutor) {
            this.newExecutor = newExecutor;
        }

        @Override // org.jooq.ExecutorProvider
        public Executor provide() {
            return this.newExecutor;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultConfiguration$RecordMapperWrapper.class */
    public final class RecordMapperWrapper implements RecordMapperProvider {
        private final RecordMapper<?, ?> newRecordMapper;

        private RecordMapperWrapper(RecordMapper<?, ?> newRecordMapper) {
            this.newRecordMapper = newRecordMapper;
        }

        @Override // org.jooq.RecordMapperProvider
        public <R extends Record, E> RecordMapper<R, E> provide(RecordType<R> recordType, Class<? extends E> cls) {
            return (RecordMapper<R, E>) this.newRecordMapper;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultConfiguration$RecordUnmapperWrapper.class */
    public final class RecordUnmapperWrapper implements RecordUnmapperProvider {
        private final RecordUnmapper<?, ?> newRecordUnmapper;

        private RecordUnmapperWrapper(RecordUnmapper<?, ?> newRecordUnmapper) {
            this.newRecordUnmapper = newRecordUnmapper;
        }

        @Override // org.jooq.RecordUnmapperProvider
        public <E, R extends Record> RecordUnmapper<E, R> provide(Class<? extends E> cls, RecordType<R> recordType) {
            return (RecordUnmapper<E, R>) this.newRecordUnmapper;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultConfiguration$UnwrapperWrapper.class */
    public final class UnwrapperWrapper implements UnwrapperProvider {
        private final Unwrapper newUnwrapper;

        private UnwrapperWrapper(Unwrapper newUnwrapper) {
            this.newUnwrapper = newUnwrapper;
        }

        @Override // org.jooq.UnwrapperProvider
        public Unwrapper provide() {
            return this.newUnwrapper;
        }
    }
}
