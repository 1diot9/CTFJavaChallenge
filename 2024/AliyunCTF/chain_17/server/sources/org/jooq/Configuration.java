package org.jooq;

import io.r2dbc.spi.ConnectionFactory;
import java.io.Serializable;
import java.sql.Connection;
import java.time.Clock;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Configuration.class */
public interface Configuration extends Serializable {
    @NotNull
    DSLContext dsl();

    @NotNull
    Map<Object, Object> data();

    @Nullable
    Object data(Object obj);

    @Nullable
    Object data(Object obj, Object obj2);

    @NotNull
    Clock clock();

    @NotNull
    ConnectionProvider connectionProvider();

    @NotNull
    ConnectionFactory connectionFactory();

    @NotNull
    ConnectionProvider interpreterConnectionProvider();

    @NotNull
    ConnectionProvider systemConnectionProvider();

    @NotNull
    MetaProvider metaProvider();

    @NotNull
    CommitProvider commitProvider();

    @NotNull
    ExecutorProvider executorProvider();

    @NotNull
    CacheProvider cacheProvider();

    @NotNull
    TransactionProvider transactionProvider();

    @NotNull
    TransactionListenerProvider[] transactionListenerProviders();

    @NotNull
    DiagnosticsListenerProvider[] diagnosticsListenerProviders();

    @NotNull
    UnwrapperProvider unwrapperProvider();

    @NotNull
    CharsetProvider charsetProvider();

    @NotNull
    RecordMapperProvider recordMapperProvider();

    @NotNull
    RecordUnmapperProvider recordUnmapperProvider();

    @NotNull
    RecordListenerProvider[] recordListenerProviders();

    @NotNull
    ExecuteListenerProvider[] executeListenerProviders();

    @NotNull
    MigrationListenerProvider[] migrationListenerProviders();

    @NotNull
    VisitListenerProvider[] visitListenerProviders();

    @NotNull
    ConverterProvider converterProvider();

    @NotNull
    FormattingProvider formattingProvider();

    @Deprecated(forRemoval = true, since = "2.0")
    @NotNull
    SchemaMapping schemaMapping();

    @NotNull
    SQLDialect dialect();

    @NotNull
    SQLDialect family();

    @NotNull
    Settings settings();

    @NotNull
    Configuration set(Clock clock);

    @NotNull
    Configuration set(ConnectionProvider connectionProvider);

    @NotNull
    Configuration set(MetaProvider metaProvider);

    @NotNull
    Configuration set(CommitProvider commitProvider);

    @NotNull
    Configuration set(ExecutorProvider executorProvider);

    @NotNull
    Configuration set(CacheProvider cacheProvider);

    @NotNull
    Configuration set(Executor executor);

    @NotNull
    Configuration set(Connection connection);

    @NotNull
    Configuration set(DataSource dataSource);

    @NotNull
    Configuration set(ConnectionFactory connectionFactory);

    @NotNull
    Configuration set(TransactionProvider transactionProvider);

    @NotNull
    Configuration set(RecordMapper<?, ?> recordMapper);

    @NotNull
    Configuration set(RecordMapperProvider recordMapperProvider);

    @NotNull
    Configuration set(RecordUnmapper<?, ?> recordUnmapper);

    @NotNull
    Configuration set(RecordUnmapperProvider recordUnmapperProvider);

    @NotNull
    Configuration set(RecordListener... recordListenerArr);

    @NotNull
    Configuration setAppending(RecordListener... recordListenerArr);

    @NotNull
    Configuration set(RecordListenerProvider... recordListenerProviderArr);

    @NotNull
    Configuration setAppending(RecordListenerProvider... recordListenerProviderArr);

    @NotNull
    Configuration set(ExecuteListener... executeListenerArr);

    @NotNull
    Configuration setAppending(ExecuteListener... executeListenerArr);

    @NotNull
    Configuration set(ExecuteListenerProvider... executeListenerProviderArr);

    @NotNull
    Configuration setAppending(ExecuteListenerProvider... executeListenerProviderArr);

    @NotNull
    Configuration set(MigrationListener... migrationListenerArr);

    @NotNull
    Configuration setAppending(MigrationListener... migrationListenerArr);

    @NotNull
    Configuration set(MigrationListenerProvider... migrationListenerProviderArr);

    @NotNull
    Configuration setAppending(MigrationListenerProvider... migrationListenerProviderArr);

    @NotNull
    Configuration set(VisitListener... visitListenerArr);

    @NotNull
    Configuration setAppending(VisitListener... visitListenerArr);

    @NotNull
    Configuration set(VisitListenerProvider... visitListenerProviderArr);

    @NotNull
    Configuration setAppending(VisitListenerProvider... visitListenerProviderArr);

    @NotNull
    Configuration set(TransactionListener... transactionListenerArr);

    @NotNull
    Configuration setAppending(TransactionListener... transactionListenerArr);

    @NotNull
    Configuration set(TransactionListenerProvider... transactionListenerProviderArr);

    @NotNull
    Configuration setAppending(TransactionListenerProvider... transactionListenerProviderArr);

    @NotNull
    Configuration set(DiagnosticsListener... diagnosticsListenerArr);

    @NotNull
    Configuration setAppending(DiagnosticsListener... diagnosticsListenerArr);

    @NotNull
    Configuration set(DiagnosticsListenerProvider... diagnosticsListenerProviderArr);

    @NotNull
    Configuration setAppending(DiagnosticsListenerProvider... diagnosticsListenerProviderArr);

    @NotNull
    Configuration set(Unwrapper unwrapper);

    @NotNull
    Configuration set(UnwrapperProvider unwrapperProvider);

    @NotNull
    Configuration set(CharsetProvider charsetProvider);

    @NotNull
    Configuration set(ConverterProvider converterProvider);

    @NotNull
    Configuration set(FormattingProvider formattingProvider);

    @NotNull
    Configuration set(SQLDialect sQLDialect);

    @NotNull
    Configuration set(Settings settings);

    @NotNull
    Configuration derive();

    @NotNull
    Configuration derive(Clock clock);

    @NotNull
    Configuration derive(Connection connection);

    @NotNull
    Configuration derive(DataSource dataSource);

    @NotNull
    Configuration derive(ConnectionFactory connectionFactory);

    @NotNull
    Configuration derive(ConnectionProvider connectionProvider);

    @NotNull
    Configuration derive(MetaProvider metaProvider);

    @NotNull
    Configuration derive(CommitProvider commitProvider);

    @NotNull
    Configuration derive(Executor executor);

    @NotNull
    Configuration derive(ExecutorProvider executorProvider);

    @NotNull
    Configuration derive(CacheProvider cacheProvider);

    @NotNull
    Configuration derive(TransactionProvider transactionProvider);

    @NotNull
    Configuration derive(RecordMapper<?, ?> recordMapper);

    @NotNull
    Configuration derive(RecordMapperProvider recordMapperProvider);

    @NotNull
    Configuration derive(RecordUnmapper<?, ?> recordUnmapper);

    @NotNull
    Configuration derive(RecordUnmapperProvider recordUnmapperProvider);

    @NotNull
    Configuration derive(RecordListener... recordListenerArr);

    @NotNull
    Configuration deriveAppending(RecordListener... recordListenerArr);

    @NotNull
    Configuration derive(RecordListenerProvider... recordListenerProviderArr);

    @NotNull
    Configuration deriveAppending(RecordListenerProvider... recordListenerProviderArr);

    @NotNull
    Configuration derive(ExecuteListener... executeListenerArr);

    @NotNull
    Configuration deriveAppending(ExecuteListener... executeListenerArr);

    @NotNull
    Configuration derive(ExecuteListenerProvider... executeListenerProviderArr);

    @NotNull
    Configuration deriveAppending(ExecuteListenerProvider... executeListenerProviderArr);

    @NotNull
    Configuration derive(MigrationListener... migrationListenerArr);

    @NotNull
    Configuration deriveAppending(MigrationListener... migrationListenerArr);

    @NotNull
    Configuration derive(MigrationListenerProvider... migrationListenerProviderArr);

    @NotNull
    Configuration deriveAppending(MigrationListenerProvider... migrationListenerProviderArr);

    @NotNull
    Configuration derive(VisitListener... visitListenerArr);

    @NotNull
    Configuration deriveAppending(VisitListener... visitListenerArr);

    @NotNull
    Configuration derive(VisitListenerProvider... visitListenerProviderArr);

    @NotNull
    Configuration deriveAppending(VisitListenerProvider... visitListenerProviderArr);

    @NotNull
    Configuration derive(TransactionListener... transactionListenerArr);

    @NotNull
    Configuration deriveAppending(TransactionListener... transactionListenerArr);

    @NotNull
    Configuration derive(TransactionListenerProvider... transactionListenerProviderArr);

    @NotNull
    Configuration deriveAppending(TransactionListenerProvider... transactionListenerProviderArr);

    @NotNull
    Configuration derive(DiagnosticsListener... diagnosticsListenerArr);

    @NotNull
    Configuration deriveAppending(DiagnosticsListener... diagnosticsListenerArr);

    @NotNull
    Configuration derive(DiagnosticsListenerProvider... diagnosticsListenerProviderArr);

    @NotNull
    Configuration deriveAppending(DiagnosticsListenerProvider... diagnosticsListenerProviderArr);

    @NotNull
    Configuration derive(Unwrapper unwrapper);

    @NotNull
    Configuration derive(UnwrapperProvider unwrapperProvider);

    @NotNull
    Configuration derive(CharsetProvider charsetProvider);

    @NotNull
    Configuration derive(ConverterProvider converterProvider);

    @NotNull
    Configuration derive(FormattingProvider formattingProvider);

    @NotNull
    Configuration derive(SQLDialect sQLDialect);

    @NotNull
    Configuration derive(Settings settings);

    @NotNull
    Configuration deriveSettings(Function<? super Settings, ? extends Settings> function);

    boolean commercial(Supplier<String> supplier);

    boolean requireCommercial(Supplier<String> supplier) throws DataAccessException;

    default boolean commercial() {
        return false;
    }
}
