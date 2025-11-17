package org.jooq;

import io.r2dbc.spi.ConnectionFactory;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.exception.ConfigurationException;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DataDefinitionException;
import org.jooq.exception.InvalidResultException;
import org.jooq.exception.NoDataFoundException;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.ParserException;
import org.jooq.tools.jdbc.MockCallable;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockRunnable;
import org.jooq.util.xml.jaxb.InformationSchema;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DSLContext.class */
public interface DSLContext extends Scope {
    @Nullable
    Schema map(Schema schema);

    @Nullable
    <R extends Record> Table<R> map(Table<R> table);

    @NotNull
    Parser parser();

    @NotNull
    Connection parsingConnection();

    @NotNull
    DataSource parsingDataSource();

    @NotNull
    ConnectionFactory parsingConnectionFactory();

    @NotNull
    Connection diagnosticsConnection();

    @NotNull
    DataSource diagnosticsDataSource();

    @ApiStatus.Experimental
    @NotNull
    Migrations migrations();

    @NotNull
    Meta meta();

    @NotNull
    Meta meta(DatabaseMetaData databaseMetaData);

    @NotNull
    Meta meta(Catalog... catalogArr);

    @NotNull
    Meta meta(Schema... schemaArr);

    @NotNull
    Meta meta(Table<?>... tableArr);

    @NotNull
    Meta meta(InformationSchema informationSchema);

    @NotNull
    Meta meta(String... strArr) throws DataDefinitionException, ParserException;

    @NotNull
    Meta meta(Source... sourceArr) throws DataDefinitionException, ParserException;

    @NotNull
    Meta meta(Query... queryArr) throws DataDefinitionException;

    @NotNull
    InformationSchema informationSchema(Catalog catalog);

    @NotNull
    InformationSchema informationSchema(Catalog... catalogArr);

    @NotNull
    InformationSchema informationSchema(Schema schema);

    @NotNull
    InformationSchema informationSchema(Schema... schemaArr);

    @NotNull
    InformationSchema informationSchema(Table<?> table);

    @NotNull
    InformationSchema informationSchema(Table<?>... tableArr);

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    Explain explain(Query query);

    @Blocking
    <T> T transactionResult(TransactionalCallable<T> transactionalCallable);

    @Blocking
    <T> T transactionResult(ContextTransactionalCallable<T> contextTransactionalCallable) throws ConfigurationException;

    @Blocking
    void transaction(TransactionalRunnable transactionalRunnable);

    @Blocking
    void transaction(ContextTransactionalRunnable contextTransactionalRunnable) throws ConfigurationException;

    @NotNull
    <T> CompletionStage<T> transactionResultAsync(TransactionalCallable<T> transactionalCallable) throws ConfigurationException;

    @NotNull
    CompletionStage<Void> transactionAsync(TransactionalRunnable transactionalRunnable) throws ConfigurationException;

    @NotNull
    <T> CompletionStage<T> transactionResultAsync(Executor executor, TransactionalCallable<T> transactionalCallable) throws ConfigurationException;

    @NotNull
    CompletionStage<Void> transactionAsync(Executor executor, TransactionalRunnable transactionalRunnable) throws ConfigurationException;

    @NotNull
    <T> Publisher<T> transactionPublisher(TransactionalPublishable<T> transactionalPublishable);

    @Blocking
    <T> T connectionResult(ConnectionCallable<T> connectionCallable);

    @Blocking
    void connection(ConnectionRunnable connectionRunnable);

    <T> T mockResult(MockDataProvider mockDataProvider, MockCallable<T> mockCallable);

    void mock(MockDataProvider mockDataProvider, MockRunnable mockRunnable);

    @ApiStatus.Internal
    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    RenderContext renderContext();

    @NotNull
    String render(QueryPart queryPart);

    @NotNull
    String renderNamedParams(QueryPart queryPart);

    @NotNull
    String renderNamedOrInlinedParams(QueryPart queryPart);

    @NotNull
    String renderInlined(QueryPart queryPart);

    @NotNull
    List<Object> extractBindValues(QueryPart queryPart);

    @NotNull
    Map<String, Param<?>> extractParams(QueryPart queryPart);

    @Nullable
    Param<?> extractParam(QueryPart queryPart, String str);

    @ApiStatus.Internal
    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    BindContext bindContext(PreparedStatement preparedStatement);

    void attach(Attachable... attachableArr);

    void attach(Collection<? extends Attachable> collection);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record> LoaderOptionsStep<R> loadInto(Table<R> table);

    @Support
    @CheckReturnValue
    @NotNull
    Queries queries(Query... queryArr);

    @Support
    @CheckReturnValue
    @NotNull
    Queries queries(Collection<? extends Query> collection);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    Block begin(Statement... statementArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    Block begin(Collection<? extends Statement> collection);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    RowCountQuery query(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    RowCountQuery query(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    RowCountQuery query(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    RowCountQuery query(String str, QueryPart... queryPartArr);

    @PlainSQL
    @Blocking
    @Support
    @CheckReturnValue
    @NotNull
    Result<Record> fetch(SQL sql) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Result<Record> fetch(String str) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Result<Record> fetch(String str, Object... objArr) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Result<Record> fetch(String str, QueryPart... queryPartArr) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Cursor<Record> fetchLazy(SQL sql) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Cursor<Record> fetchLazy(String str) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Cursor<Record> fetchLazy(String str, Object... objArr) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Cursor<Record> fetchLazy(String str, QueryPart... queryPartArr) throws DataAccessException;

    @PlainSQL
    @Support
    @NotNull
    CompletionStage<Result<Record>> fetchAsync(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    CompletionStage<Result<Record>> fetchAsync(String str);

    @PlainSQL
    @Support
    @NotNull
    CompletionStage<Result<Record>> fetchAsync(String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    CompletionStage<Result<Record>> fetchAsync(String str, QueryPart... queryPartArr);

    @PlainSQL
    @Support
    @NotNull
    CompletionStage<Result<Record>> fetchAsync(Executor executor, SQL sql);

    @PlainSQL
    @Support
    @NotNull
    CompletionStage<Result<Record>> fetchAsync(Executor executor, String str);

    @PlainSQL
    @Support
    @NotNull
    CompletionStage<Result<Record>> fetchAsync(Executor executor, String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    CompletionStage<Result<Record>> fetchAsync(Executor executor, String str, QueryPart... queryPartArr);

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Stream<Record> fetchStream(SQL sql) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Stream<Record> fetchStream(String str) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Stream<Record> fetchStream(String str, Object... objArr) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Stream<Record> fetchStream(String str, QueryPart... queryPartArr) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Results fetchMany(SQL sql) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Results fetchMany(String str) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Results fetchMany(String str, Object... objArr) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Results fetchMany(String str, QueryPart... queryPartArr) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    @Nullable
    Record fetchOne(SQL sql) throws DataAccessException, TooManyRowsException;

    @PlainSQL
    @Blocking
    @Support
    @Nullable
    Record fetchOne(String str) throws DataAccessException, TooManyRowsException;

    @PlainSQL
    @Blocking
    @Support
    @Nullable
    Record fetchOne(String str, Object... objArr) throws DataAccessException, TooManyRowsException;

    @PlainSQL
    @Blocking
    @Support
    @Nullable
    Record fetchOne(String str, QueryPart... queryPartArr) throws DataAccessException, TooManyRowsException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Record fetchSingle(SQL sql) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Record fetchSingle(String str) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Record fetchSingle(String str, Object... objArr) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Record fetchSingle(String str, QueryPart... queryPartArr) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Optional<Record> fetchOptional(SQL sql) throws DataAccessException, TooManyRowsException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Optional<Record> fetchOptional(String str) throws DataAccessException, TooManyRowsException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Optional<Record> fetchOptional(String str, Object... objArr) throws DataAccessException, TooManyRowsException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Optional<Record> fetchOptional(String str, QueryPart... queryPartArr) throws DataAccessException, TooManyRowsException;

    @PlainSQL
    @Blocking
    @Support
    @Nullable
    Object fetchValue(SQL sql) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @PlainSQL
    @Blocking
    @Support
    @Nullable
    Object fetchValue(String str) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @PlainSQL
    @Blocking
    @Support
    @Nullable
    Object fetchValue(String str, Object... objArr) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @PlainSQL
    @Blocking
    @Support
    @Nullable
    Object fetchValue(String str, QueryPart... queryPartArr) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Optional<?> fetchOptionalValue(SQL sql) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Optional<?> fetchOptionalValue(String str) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Optional<?> fetchOptionalValue(String str, Object... objArr) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    Optional<?> fetchOptionalValue(String str, QueryPart... queryPartArr) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    List<?> fetchValues(SQL sql) throws DataAccessException, InvalidResultException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    List<?> fetchValues(String str) throws DataAccessException, InvalidResultException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    List<?> fetchValues(String str, Object... objArr) throws DataAccessException, InvalidResultException;

    @PlainSQL
    @Blocking
    @Support
    @NotNull
    List<?> fetchValues(String str, QueryPart... queryPartArr) throws DataAccessException, InvalidResultException;

    @PlainSQL
    @Blocking
    @Support
    int execute(SQL sql) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    int execute(String str) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    int execute(String str, Object... objArr) throws DataAccessException;

    @PlainSQL
    @Blocking
    @Support
    int execute(String str, QueryPart... queryPartArr) throws DataAccessException;

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    ResultQuery<Record> resultQuery(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    ResultQuery<Record> resultQuery(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    ResultQuery<Record> resultQuery(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    ResultQuery<Record> resultQuery(String str, QueryPart... queryPartArr);

    @Blocking
    @Support
    @NotNull
    Result<Record> fetch(ResultSet resultSet) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    Result<Record> fetch(ResultSet resultSet, Field<?>... fieldArr) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    Result<Record> fetch(ResultSet resultSet, DataType<?>... dataTypeArr) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    Result<Record> fetch(ResultSet resultSet, Class<?>... clsArr) throws DataAccessException;

    @Blocking
    @Support
    @Nullable
    Record fetchOne(ResultSet resultSet) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @Nullable
    Record fetchOne(ResultSet resultSet, Field<?>... fieldArr) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @Nullable
    Record fetchOne(ResultSet resultSet, DataType<?>... dataTypeArr) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @Nullable
    Record fetchOne(ResultSet resultSet, Class<?>... clsArr) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    Record fetchSingle(ResultSet resultSet) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    Record fetchSingle(ResultSet resultSet, Field<?>... fieldArr) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    Record fetchSingle(ResultSet resultSet, DataType<?>... dataTypeArr) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    Record fetchSingle(ResultSet resultSet, Class<?>... clsArr) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    Optional<Record> fetchOptional(ResultSet resultSet) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    Optional<Record> fetchOptional(ResultSet resultSet, Field<?>... fieldArr) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    Optional<Record> fetchOptional(ResultSet resultSet, DataType<?>... dataTypeArr) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    Optional<Record> fetchOptional(ResultSet resultSet, Class<?>... clsArr) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @Nullable
    Object fetchValue(ResultSet resultSet) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @Blocking
    @Support
    @Nullable
    <T> T fetchValue(ResultSet resultSet, Field<T> field) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @Blocking
    @Support
    @Nullable
    <T> T fetchValue(ResultSet resultSet, DataType<T> dataType) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @Blocking
    @Support
    @Nullable
    <T> T fetchValue(ResultSet resultSet, Class<T> cls) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @Blocking
    @Support
    @NotNull
    Optional<?> fetchOptionalValue(ResultSet resultSet) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @Blocking
    @Support
    @NotNull
    <T> Optional<T> fetchOptionalValue(ResultSet resultSet, Field<T> field) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @Blocking
    @Support
    @NotNull
    <T> Optional<T> fetchOptionalValue(ResultSet resultSet, DataType<T> dataType) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @Blocking
    @Support
    @NotNull
    <T> Optional<T> fetchOptionalValue(ResultSet resultSet, Class<T> cls) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @Blocking
    @Support
    @NotNull
    List<?> fetchValues(ResultSet resultSet) throws DataAccessException, InvalidResultException;

    @Blocking
    @Support
    @NotNull
    <T> List<T> fetchValues(ResultSet resultSet, Field<T> field) throws DataAccessException, InvalidResultException;

    @Blocking
    @Support
    @NotNull
    <T> List<T> fetchValues(ResultSet resultSet, DataType<T> dataType) throws DataAccessException, InvalidResultException;

    @Blocking
    @Support
    @NotNull
    <T> List<T> fetchValues(ResultSet resultSet, Class<T> cls) throws DataAccessException, InvalidResultException;

    @Blocking
    @Support
    @NotNull
    Cursor<Record> fetchLazy(ResultSet resultSet) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    Cursor<Record> fetchLazy(ResultSet resultSet, Field<?>... fieldArr) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    Cursor<Record> fetchLazy(ResultSet resultSet, DataType<?>... dataTypeArr) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    Cursor<Record> fetchLazy(ResultSet resultSet, Class<?>... clsArr) throws DataAccessException;

    @Support
    @NotNull
    CompletionStage<Result<Record>> fetchAsync(ResultSet resultSet);

    @Support
    @NotNull
    CompletionStage<Result<Record>> fetchAsync(ResultSet resultSet, Field<?>... fieldArr);

    @Support
    @NotNull
    CompletionStage<Result<Record>> fetchAsync(ResultSet resultSet, DataType<?>... dataTypeArr);

    @Support
    @NotNull
    CompletionStage<Result<Record>> fetchAsync(ResultSet resultSet, Class<?>... clsArr);

    @Support
    @NotNull
    CompletionStage<Result<Record>> fetchAsync(Executor executor, ResultSet resultSet);

    @Support
    @NotNull
    CompletionStage<Result<Record>> fetchAsync(Executor executor, ResultSet resultSet, Field<?>... fieldArr);

    @Support
    @NotNull
    CompletionStage<Result<Record>> fetchAsync(Executor executor, ResultSet resultSet, DataType<?>... dataTypeArr);

    @Support
    @NotNull
    CompletionStage<Result<Record>> fetchAsync(Executor executor, ResultSet resultSet, Class<?>... clsArr);

    @Blocking
    @Support
    @NotNull
    Stream<Record> fetchStream(ResultSet resultSet) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    Stream<Record> fetchStream(ResultSet resultSet, Field<?>... fieldArr) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    Stream<Record> fetchStream(ResultSet resultSet, DataType<?>... dataTypeArr) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    Stream<Record> fetchStream(ResultSet resultSet, Class<?>... clsArr) throws DataAccessException;

    @Support
    @NotNull
    Result<Record> fetchFromTXT(String str) throws DataAccessException;

    @Support
    @NotNull
    Result<Record> fetchFromTXT(String str, String str2) throws DataAccessException;

    @Support
    @NotNull
    Result<Record> fetchFromHTML(String str) throws DataAccessException;

    @Support
    @NotNull
    Result<Record> fetchFromCSV(String str) throws DataAccessException;

    @Support
    @NotNull
    Result<Record> fetchFromCSV(String str, char c) throws DataAccessException;

    @Support
    @NotNull
    Result<Record> fetchFromCSV(String str, boolean z) throws DataAccessException;

    @Support
    @NotNull
    Result<Record> fetchFromCSV(String str, boolean z, char c) throws DataAccessException;

    @Support
    @NotNull
    Result<Record> fetchFromJSON(String str);

    @Support
    @NotNull
    Result<Record> fetchFromXML(String str);

    @Support
    @NotNull
    Result<Record> fetchFromStringData(String[]... strArr);

    @Support
    @NotNull
    Result<Record> fetchFromStringData(List<String[]> list);

    @Support
    @NotNull
    Result<Record> fetchFromStringData(List<String[]> list, boolean z);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep with(String str);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep with(String str, String... strArr);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep with(String str, Collection<String> collection);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep with(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep with(Name name, Name... nameArr);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep with(Name name, Collection<? extends Name> collection);

    @Support
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    WithAsStep with(String str, Function<? super Field<?>, ? extends String> function);

    @Support
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    WithAsStep with(String str, BiFunction<? super Field<?>, ? super Integer, ? extends String> biFunction);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep1 with(String str, String str2);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep2 with(String str, String str2, String str3);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep3 with(String str, String str2, String str3, String str4);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep4 with(String str, String str2, String str3, String str4, String str5);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep5 with(String str, String str2, String str3, String str4, String str5, String str6);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep6 with(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep7 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep8 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep9 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep10 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep11 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep12 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep13 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep14 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep15 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep16 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep17 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep18 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep19 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep20 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep21 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21, String str22);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep22 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21, String str22, String str23);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep1 with(Name name, Name name2);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep2 with(Name name, Name name2, Name name3);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep3 with(Name name, Name name2, Name name3, Name name4);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep4 with(Name name, Name name2, Name name3, Name name4, Name name5);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep5 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep6 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep7 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep8 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep9 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep10 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep11 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep12 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep13 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep14 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep15 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep16 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep17 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep18 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep19 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep20 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20, Name name21);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep21 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20, Name name21, Name name22);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep22 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20, Name name21, Name name22, Name name23);

    @Support
    @CheckReturnValue
    @NotNull
    WithStep with(CommonTableExpression<?>... commonTableExpressionArr);

    @Support
    @CheckReturnValue
    @NotNull
    WithStep with(Collection<? extends CommonTableExpression<?>> collection);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep withRecursive(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep withRecursive(String str, String... strArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep withRecursive(String str, Collection<String> collection);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep withRecursive(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep withRecursive(Name name, Name... nameArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep withRecursive(Name name, Collection<? extends Name> collection);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    WithAsStep withRecursive(String str, Function<? super Field<?>, ? extends String> function);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    WithAsStep withRecursive(String str, BiFunction<? super Field<?>, ? super Integer, ? extends String> biFunction);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep1 withRecursive(String str, String str2);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep2 withRecursive(String str, String str2, String str3);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep3 withRecursive(String str, String str2, String str3, String str4);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep4 withRecursive(String str, String str2, String str3, String str4, String str5);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep5 withRecursive(String str, String str2, String str3, String str4, String str5, String str6);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep6 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep7 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep8 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep9 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep10 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep11 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep12 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep13 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep14 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep15 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep16 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep17 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep18 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep19 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep20 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep21 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21, String str22);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep22 withRecursive(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21, String str22, String str23);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep1 withRecursive(Name name, Name name2);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep2 withRecursive(Name name, Name name2, Name name3);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep3 withRecursive(Name name, Name name2, Name name3, Name name4);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep4 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep5 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep6 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep7 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep8 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep9 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep10 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep11 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep12 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep13 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep14 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep15 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep16 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep17 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep18 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep19 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep20 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20, Name name21);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep21 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20, Name name21, Name name22);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithAsStep22 withRecursive(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20, Name name21, Name name22, Name name23);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithStep withRecursive(CommonTableExpression<?>... commonTableExpressionArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    WithStep withRecursive(Collection<? extends CommonTableExpression<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record> SelectWhereStep<R> selectFrom(TableLike<R> tableLike);

    @Support
    @CheckReturnValue
    @NotNull
    SelectWhereStep<Record> selectFrom(Name name);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectWhereStep<Record> selectFrom(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectWhereStep<Record> selectFrom(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectWhereStep<Record> selectFrom(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectWhereStep<Record> selectFrom(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSelectStep<Record> select(Collection<? extends SelectFieldOrAsterisk> collection);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSelectStep<Record> select(SelectFieldOrAsterisk... selectFieldOrAsteriskArr);

    @Support
    @CheckReturnValue
    @NotNull
    <T1> SelectSelectStep<Record1<T1>> select(SelectField<T1> selectField);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2> SelectSelectStep<Record2<T1, T2>> select(SelectField<T1> selectField, SelectField<T2> selectField2);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3> SelectSelectStep<Record3<T1, T2, T3>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4> SelectSelectStep<Record4<T1, T2, T3, T4>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5> SelectSelectStep<Record5<T1, T2, T3, T4, T5>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6> SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7> SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8> SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9> SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21, SelectField<T22> selectField22);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSelectStep<Record> selectDistinct(Collection<? extends SelectFieldOrAsterisk> collection);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSelectStep<Record> selectDistinct(SelectFieldOrAsterisk... selectFieldOrAsteriskArr);

    @Support
    @CheckReturnValue
    @NotNull
    <T1> SelectSelectStep<Record1<T1>> selectDistinct(SelectField<T1> selectField);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2> SelectSelectStep<Record2<T1, T2>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3> SelectSelectStep<Record3<T1, T2, T3>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4> SelectSelectStep<Record4<T1, T2, T3, T4>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5> SelectSelectStep<Record5<T1, T2, T3, T4, T5>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6> SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7> SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8> SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9> SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21, SelectField<T22> selectField22);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSelectStep<Record1<Integer>> selectZero();

    @Support
    @CheckReturnValue
    @NotNull
    SelectSelectStep<Record1<Integer>> selectOne();

    @Support
    @CheckReturnValue
    @NotNull
    SelectSelectStep<Record1<Integer>> selectCount();

    @Support
    @CheckReturnValue
    @NotNull
    SelectQuery<Record> selectQuery();

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record> SelectQuery<R> selectQuery(TableLike<R> tableLike);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record> InsertQuery<R> insertQuery(Table<R> table);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record> InsertSetStep<R> insertInto(Table<R> table);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1> InsertValuesStep1<R, T1> insertInto(Table<R> table, Field<T1> field);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2> InsertValuesStep2<R, T1, T2> insertInto(Table<R> table, Field<T1> field, Field<T2> field2);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3> InsertValuesStep3<R, T1, T2, T3> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4> InsertValuesStep4<R, T1, T2, T3, T4> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5> InsertValuesStep5<R, T1, T2, T3, T4, T5> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6> InsertValuesStep6<R, T1, T2, T3, T4, T5, T6> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7> InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8> InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9> InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> InsertValuesStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> InsertValuesStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> InsertValuesStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> InsertValuesStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> InsertValuesStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> InsertValuesStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> InsertValuesStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> InsertValuesStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> InsertValuesStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> InsertValuesStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> InsertValuesStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record> InsertValuesStepN<R> insertInto(Table<R> table, Field<?>... fieldArr);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record> InsertValuesStepN<R> insertInto(Table<R> table, Collection<? extends Field<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record> UpdateQuery<R> updateQuery(Table<R> table);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record> UpdateSetFirstStep<R> update(Table<R> table);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record> MergeUsingStep<R> mergeInto(Table<R> table);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1> MergeKeyStep1<R, T1> mergeInto(Table<R> table, Field<T1> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2> MergeKeyStep2<R, T1, T2> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3> MergeKeyStep3<R, T1, T2, T3> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4> MergeKeyStep4<R, T1, T2, T3, T4> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5> MergeKeyStep5<R, T1, T2, T3, T4, T5> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6> MergeKeyStep6<R, T1, T2, T3, T4, T5, T6> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7> MergeKeyStep7<R, T1, T2, T3, T4, T5, T6, T7> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8> MergeKeyStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9> MergeKeyStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> MergeKeyStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> MergeKeyStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> MergeKeyStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> MergeKeyStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> MergeKeyStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> MergeKeyStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> MergeKeyStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> MergeKeyStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> MergeKeyStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> MergeKeyStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> MergeKeyStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> MergeKeyStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> MergeKeyStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record> MergeKeyStepN<R> mergeInto(Table<R> table, Field<?>... fieldArr);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record> MergeKeyStepN<R> mergeInto(Table<R> table, Collection<? extends Field<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record> DeleteQuery<R> deleteQuery(Table<R> table);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record> DeleteUsingStep<R> deleteFrom(Table<R> table);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record> DeleteUsingStep<R> delete(Table<R> table);

    void batched(BatchedRunnable batchedRunnable);

    <T> T batchedResult(BatchedCallable<T> batchedCallable);

    @Support
    @CheckReturnValue
    @NotNull
    Batch batch(Query... queryArr);

    @Support
    @CheckReturnValue
    @NotNull
    Batch batch(Queries queries);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    Batch batch(String... strArr);

    @Support
    @CheckReturnValue
    @NotNull
    Batch batch(Collection<? extends Query> collection);

    @Support
    @CheckReturnValue
    @NotNull
    BatchBindStep batch(Query query);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    BatchBindStep batch(String str);

    @Support
    @CheckReturnValue
    @NotNull
    Batch batch(Query query, Object[]... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    Batch batch(String str, Object[]... objArr);

    @Support
    @CheckReturnValue
    @NotNull
    Batch batchStore(UpdatableRecord<?>... updatableRecordArr);

    @Support
    @CheckReturnValue
    @NotNull
    Batch batchStore(Collection<? extends UpdatableRecord<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    Batch batchInsert(TableRecord<?>... tableRecordArr);

    @Support
    @CheckReturnValue
    @NotNull
    Batch batchInsert(Collection<? extends TableRecord<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    Batch batchUpdate(UpdatableRecord<?>... updatableRecordArr);

    @Support
    @CheckReturnValue
    @NotNull
    Batch batchUpdate(Collection<? extends UpdatableRecord<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    Batch batchMerge(UpdatableRecord<?>... updatableRecordArr);

    @Support
    @CheckReturnValue
    @NotNull
    Batch batchMerge(Collection<? extends UpdatableRecord<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    Batch batchDelete(UpdatableRecord<?>... updatableRecordArr);

    @Support
    @CheckReturnValue
    @NotNull
    Batch batchDelete(Collection<? extends UpdatableRecord<?>> collection);

    @CheckReturnValue
    @NotNull
    Queries ddl(Catalog catalog);

    @CheckReturnValue
    @NotNull
    Queries ddl(Catalog catalog, DDLExportConfiguration dDLExportConfiguration);

    @CheckReturnValue
    @NotNull
    Queries ddl(Catalog catalog, DDLFlag... dDLFlagArr);

    @CheckReturnValue
    @NotNull
    Queries ddl(Schema schema);

    @CheckReturnValue
    @NotNull
    Queries ddl(Schema schema, DDLExportConfiguration dDLExportConfiguration);

    @CheckReturnValue
    @NotNull
    Queries ddl(Schema schema, DDLFlag... dDLFlagArr);

    @CheckReturnValue
    @NotNull
    Queries ddl(Table<?> table);

    @CheckReturnValue
    @NotNull
    Queries ddl(Table<?> table, DDLExportConfiguration dDLExportConfiguration);

    @CheckReturnValue
    @NotNull
    Queries ddl(Table<?> table, DDLFlag... dDLFlagArr);

    @CheckReturnValue
    @NotNull
    Queries ddl(Table<?>... tableArr);

    @CheckReturnValue
    @NotNull
    Queries ddl(Table<?>[] tableArr, DDLExportConfiguration dDLExportConfiguration);

    @CheckReturnValue
    @NotNull
    Queries ddl(Table<?>[] tableArr, DDLFlag... dDLFlagArr);

    @CheckReturnValue
    @NotNull
    Queries ddl(Collection<? extends Table<?>> collection);

    @CheckReturnValue
    @NotNull
    Queries ddl(Collection<? extends Table<?>> collection, DDLFlag... dDLFlagArr);

    @CheckReturnValue
    @NotNull
    Queries ddl(Collection<? extends Table<?>> collection, DDLExportConfiguration dDLExportConfiguration);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterDatabaseStep alterDatabase(String str);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterDatabaseStep alterDatabase(Name name);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterDatabaseStep alterDatabase(Catalog catalog);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterDatabaseStep alterDatabaseIfExists(String str);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterDatabaseStep alterDatabaseIfExists(Name name);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterDatabaseStep alterDatabaseIfExists(Catalog catalog);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <T> AlterDomainStep<T> alterDomain(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <T> AlterDomainStep<T> alterDomain(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <T> AlterDomainStep<T> alterDomain(Domain<T> domain);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <T> AlterDomainStep<T> alterDomainIfExists(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <T> AlterDomainStep<T> alterDomainIfExists(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <T> AlterDomainStep<T> alterDomainIfExists(Domain<T> domain);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterIndexOnStep alterIndex(String str);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterIndexOnStep alterIndex(Name name);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterIndexOnStep alterIndex(Index index);

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterIndexOnStep alterIndexIfExists(String str);

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterIndexOnStep alterIndexIfExists(Name name);

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterIndexOnStep alterIndexIfExists(Index index);

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterSchemaStep alterSchema(String str);

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterSchemaStep alterSchema(Name name);

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterSchemaStep alterSchema(Schema schema);

    @Support({SQLDialect.H2})
    @CheckReturnValue
    @NotNull
    AlterSchemaStep alterSchemaIfExists(String str);

    @Support({SQLDialect.H2})
    @CheckReturnValue
    @NotNull
    AlterSchemaStep alterSchemaIfExists(Name name);

    @Support({SQLDialect.H2})
    @CheckReturnValue
    @NotNull
    AlterSchemaStep alterSchemaIfExists(Schema schema);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterSequenceStep<Number> alterSequence(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterSequenceStep<Number> alterSequence(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <T extends Number> AlterSequenceStep<T> alterSequence(Sequence<T> sequence);

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterSequenceStep<Number> alterSequenceIfExists(String str);

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterSequenceStep<Number> alterSequenceIfExists(Name name);

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <T extends Number> AlterSequenceStep<T> alterSequenceIfExists(Sequence<T> sequence);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTypeStep alterType(String str);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTypeStep alterType(Name name);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTypeStep alterTypeIfExists(String str);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTypeStep alterTypeIfExists(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterViewStep alterView(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterViewStep alterView(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterViewStep alterView(Table<?> table);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterViewStep alterViewIfExists(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterViewStep alterViewIfExists(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterViewStep alterViewIfExists(Table<?> table);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterViewStep alterMaterializedView(String str);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterViewStep alterMaterializedView(Name name);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterViewStep alterMaterializedView(Table<?> table);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterViewStep alterMaterializedViewIfExists(String str);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterViewStep alterMaterializedViewIfExists(Name name);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterViewStep alterMaterializedViewIfExists(Table<?> table);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterViewStep alterView(Table<?> table, Field<?>... fieldArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterViewStep alterView(Table<?> table, Collection<? extends Field<?>> collection);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CommentOnIsStep commentOnTable(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CommentOnIsStep commentOnTable(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CommentOnIsStep commentOnTable(Table<?> table);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CommentOnIsStep commentOnView(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CommentOnIsStep commentOnView(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CommentOnIsStep commentOnView(Table<?> table);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CommentOnIsStep commentOnMaterializedView(String str);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CommentOnIsStep commentOnMaterializedView(Name name);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CommentOnIsStep commentOnMaterializedView(Table<?> table);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CommentOnIsStep commentOnColumn(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CommentOnIsStep commentOnColumn(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CommentOnIsStep commentOnColumn(Field<?> field);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateDatabaseFinalStep createDatabase(String str);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateDatabaseFinalStep createDatabase(Name name);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateDatabaseFinalStep createDatabase(Catalog catalog);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateDatabaseFinalStep createDatabaseIfNotExists(String str);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateDatabaseFinalStep createDatabaseIfNotExists(Name name);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateDatabaseFinalStep createDatabaseIfNotExists(Catalog catalog);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateDomainAsStep createDomain(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateDomainAsStep createDomain(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateDomainAsStep createDomain(Domain<?> domain);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateDomainAsStep createDomainIfNotExists(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateDomainAsStep createDomainIfNotExists(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateDomainAsStep createDomainIfNotExists(Domain<?> domain);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexStep createIndex(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexStep createIndex(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexStep createIndex(Index index);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexStep createIndex();

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexStep createIndexIfNotExists(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexStep createIndexIfNotExists(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexStep createIndexIfNotExists(Index index);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexStep createIndexIfNotExists();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexStep createUniqueIndex(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexStep createUniqueIndex(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexStep createUniqueIndex(Index index);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexStep createUniqueIndex();

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexStep createUniqueIndexIfNotExists(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexStep createUniqueIndexIfNotExists(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexStep createUniqueIndexIfNotExists(Index index);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexStep createUniqueIndexIfNotExists();

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createTable(String str);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createTable(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createTable(Table<?> table);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createTableIfNotExists(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createTableIfNotExists(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createTableIfNotExists(Table<?> table);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createTemporaryTable(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createTemporaryTable(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createTemporaryTable(Table<?> table);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createTemporaryTableIfNotExists(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createTemporaryTableIfNotExists(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createTemporaryTableIfNotExists(Table<?> table);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createGlobalTemporaryTable(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createGlobalTemporaryTable(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createGlobalTemporaryTable(Table<?> table);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createGlobalTemporaryTableIfNotExists(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createGlobalTemporaryTableIfNotExists(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep createGlobalTemporaryTableIfNotExists(Table<?> table);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createView(String str, String... strArr);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createView(Name name, Name... nameArr);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createView(Table<?> table, Field<?>... fieldArr);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createView(String str, Collection<? extends String> collection);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createView(Name name, Collection<? extends Name> collection);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createView(Table<?> table, Collection<? extends Field<?>> collection);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createViewIfNotExists(String str, String... strArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createViewIfNotExists(Name name, Name... nameArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createViewIfNotExists(Table<?> table, Field<?>... fieldArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createViewIfNotExists(String str, Collection<? extends String> collection);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createViewIfNotExists(Name name, Collection<? extends Name> collection);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createViewIfNotExists(Table<?> table, Collection<? extends Field<?>> collection);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createOrReplaceView(String str, String... strArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createOrReplaceView(Name name, Name... nameArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createOrReplaceView(Table<?> table, Field<?>... fieldArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createOrReplaceView(String str, Collection<? extends String> collection);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createOrReplaceView(Name name, Collection<? extends Name> collection);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createOrReplaceView(Table<?> table, Collection<? extends Field<?>> collection);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createMaterializedView(String str, String... strArr);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createMaterializedView(Name name, Name... nameArr);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createMaterializedView(Table<?> table, Field<?>... fieldArr);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createMaterializedView(String str, Collection<? extends String> collection);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createMaterializedView(Name name, Collection<? extends Name> collection);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createMaterializedView(Table<?> table, Collection<? extends Field<?>> collection);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createMaterializedViewIfNotExists(String str, String... strArr);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createMaterializedViewIfNotExists(Name name, Name... nameArr);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createMaterializedViewIfNotExists(Table<?> table, Field<?>... fieldArr);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createMaterializedViewIfNotExists(String str, Collection<? extends String> collection);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createMaterializedViewIfNotExists(Name name, Collection<? extends Name> collection);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createMaterializedViewIfNotExists(Table<?> table, Collection<? extends Field<?>> collection);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createOrReplaceMaterializedView(String str, String... strArr);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createOrReplaceMaterializedView(Name name, Name... nameArr);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createOrReplaceMaterializedView(Table<?> table, Field<?>... fieldArr);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createOrReplaceMaterializedView(String str, Collection<? extends String> collection);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createOrReplaceMaterializedView(Name name, Collection<? extends Name> collection);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    CreateViewAsStep<Record> createOrReplaceMaterializedView(Table<?> table, Collection<? extends Field<?>> collection);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTypeStep createType(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTypeStep createType(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTypeStep createType(Type<?> type);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTypeStep createTypeIfNotExists(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTypeStep createTypeIfNotExists(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTypeStep createTypeIfNotExists(Type<?> type);

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateSchemaFinalStep createSchema(String str);

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateSchemaFinalStep createSchema(Name name);

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateSchemaFinalStep createSchema(Schema schema);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateSchemaFinalStep createSchemaIfNotExists(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateSchemaFinalStep createSchemaIfNotExists(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateSchemaFinalStep createSchemaIfNotExists(Schema schema);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateSequenceFlagsStep createSequence(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateSequenceFlagsStep createSequence(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateSequenceFlagsStep createSequence(Sequence<?> sequence);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateSequenceFlagsStep createSequenceIfNotExists(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateSequenceFlagsStep createSequenceIfNotExists(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateSequenceFlagsStep createSequenceIfNotExists(Sequence<?> sequence);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropDatabaseFinalStep dropDatabase(String str);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropDatabaseFinalStep dropDatabase(Name name);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropDatabaseFinalStep dropDatabase(Catalog catalog);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropDatabaseFinalStep dropDatabaseIfExists(String str);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropDatabaseFinalStep dropDatabaseIfExists(Name name);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropDatabaseFinalStep dropDatabaseIfExists(Catalog catalog);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropDomainCascadeStep dropDomain(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropDomainCascadeStep dropDomain(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropDomainCascadeStep dropDomain(Domain<?> domain);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropDomainCascadeStep dropDomainIfExists(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropDomainCascadeStep dropDomainIfExists(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropDomainCascadeStep dropDomainIfExists(Domain<?> domain);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropIndexOnStep dropIndex(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropIndexOnStep dropIndex(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropIndexOnStep dropIndex(Index index);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropIndexOnStep dropIndexIfExists(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropIndexOnStep dropIndexIfExists(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropIndexOnStep dropIndexIfExists(Index index);

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropSchemaStep dropSchema(String str);

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropSchemaStep dropSchema(Name name);

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropSchemaStep dropSchema(Schema schema);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropSchemaStep dropSchemaIfExists(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropSchemaStep dropSchemaIfExists(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropSchemaStep dropSchemaIfExists(Schema schema);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropSequenceFinalStep dropSequence(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropSequenceFinalStep dropSequence(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropSequenceFinalStep dropSequence(Sequence<?> sequence);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropSequenceFinalStep dropSequenceIfExists(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropSequenceFinalStep dropSequenceIfExists(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropSequenceFinalStep dropSequenceIfExists(Sequence<?> sequence);

    @Support
    @CheckReturnValue
    @NotNull
    DropTableStep dropTable(String str);

    @Support
    @CheckReturnValue
    @NotNull
    DropTableStep dropTable(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    DropTableStep dropTable(Table<?> table);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTableStep dropTableIfExists(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTableStep dropTableIfExists(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTableStep dropTableIfExists(Table<?> table);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTableStep dropTemporaryTable(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTableStep dropTemporaryTable(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTableStep dropTemporaryTable(Table<?> table);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTableStep dropTemporaryTableIfExists(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTableStep dropTemporaryTableIfExists(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTableStep dropTemporaryTableIfExists(Table<?> table);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTypeStep dropType(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTypeStep dropType(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTypeStep dropType(Type<?> type);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTypeStep dropType(String... strArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTypeStep dropType(Name... nameArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTypeStep dropType(Type<?>... typeArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTypeStep dropType(Collection<? extends Type<?>> collection);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTypeStep dropTypeIfExists(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTypeStep dropTypeIfExists(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTypeStep dropTypeIfExists(Type<?> type);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTypeStep dropTypeIfExists(String... strArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTypeStep dropTypeIfExists(Name... nameArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTypeStep dropTypeIfExists(Type<?>... typeArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTypeStep dropTypeIfExists(Collection<? extends Type<?>> collection);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropViewFinalStep dropView(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropViewFinalStep dropView(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropViewFinalStep dropView(Table<?> table);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropViewFinalStep dropViewIfExists(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropViewFinalStep dropViewIfExists(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropViewFinalStep dropViewIfExists(Table<?> table);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropViewFinalStep dropMaterializedView(String str);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropViewFinalStep dropMaterializedView(Name name);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropViewFinalStep dropMaterializedView(Table<?> table);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropViewFinalStep dropMaterializedViewIfExists(String str);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropViewFinalStep dropMaterializedViewIfExists(Name name);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropViewFinalStep dropMaterializedViewIfExists(Table<?> table);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    GrantOnStep grant(Privilege privilege);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    GrantOnStep grant(Privilege... privilegeArr);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    GrantOnStep grant(Collection<? extends Privilege> collection);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    RevokeOnStep revoke(Privilege privilege);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    RevokeOnStep revoke(Privilege... privilegeArr);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    RevokeOnStep revoke(Collection<? extends Privilege> collection);

    @Support({SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    RevokeOnStep revokeGrantOptionFor(Privilege privilege);

    @Support({SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    RevokeOnStep revokeGrantOptionFor(Privilege... privilegeArr);

    @Support({SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    RevokeOnStep revokeGrantOptionFor(Collection<? extends Privilege> collection);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    RowCountQuery set(String str, Param<?> param);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    RowCountQuery set(Name name, Param<?> param);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    RowCountQuery setLocal(String str, Param<?> param);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    RowCountQuery setLocal(Name name, Param<?> param);

    @Support({SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    RowCountQuery setCatalog(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    RowCountQuery setCatalog(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    RowCountQuery setCatalog(Catalog catalog);

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    RowCountQuery setSchema(String str);

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    RowCountQuery setSchema(Name name);

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    RowCountQuery setSchema(Schema schema);

    @Support
    @CheckReturnValue
    @NotNull
    TruncateIdentityStep<Record> truncate(String str);

    @Support
    @CheckReturnValue
    @NotNull
    TruncateIdentityStep<Record> truncate(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record> TruncateIdentityStep<R> truncate(Table<R> table);

    @Support
    @CheckReturnValue
    @NotNull
    TruncateIdentityStep<Record> truncate(String... strArr);

    @Support
    @CheckReturnValue
    @NotNull
    TruncateIdentityStep<Record> truncate(Name... nameArr);

    @Support
    @CheckReturnValue
    @NotNull
    TruncateIdentityStep<Record> truncate(Table<?>... tableArr);

    @Support
    @CheckReturnValue
    @NotNull
    TruncateIdentityStep<Record> truncate(Collection<? extends Table<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    TruncateIdentityStep<Record> truncateTable(String str);

    @Support
    @CheckReturnValue
    @NotNull
    TruncateIdentityStep<Record> truncateTable(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record> TruncateIdentityStep<R> truncateTable(Table<R> table);

    @Support
    @CheckReturnValue
    @NotNull
    TruncateIdentityStep<Record> truncateTable(String... strArr);

    @Support
    @CheckReturnValue
    @NotNull
    TruncateIdentityStep<Record> truncateTable(Name... nameArr);

    @Support
    @CheckReturnValue
    @NotNull
    TruncateIdentityStep<Record> truncateTable(Table<?>... tableArr);

    @Support
    @CheckReturnValue
    @NotNull
    TruncateIdentityStep<Record> truncateTable(Collection<? extends Table<?>> collection);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    Query startTransaction();

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    Query savepoint(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    Query savepoint(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    Query releaseSavepoint(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    Query releaseSavepoint(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    Query commit();

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    RollbackToSavepointStep rollback();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createView(String str, Function<? super Field<?>, ? extends String> function);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createView(String str, BiFunction<? super Field<?>, ? super Integer, ? extends String> biFunction);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createView(Name name, Function<? super Field<?>, ? extends Name> function);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createView(Name name, BiFunction<? super Field<?>, ? super Integer, ? extends Name> biFunction);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createView(Table<?> table, Function<? super Field<?>, ? extends Field<?>> function);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createView(Table<?> table, BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> biFunction);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createOrReplaceView(String str, Function<? super Field<?>, ? extends String> function);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createOrReplaceView(String str, BiFunction<? super Field<?>, ? super Integer, ? extends String> biFunction);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createOrReplaceView(Name name, Function<? super Field<?>, ? extends Name> function);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createOrReplaceView(Name name, BiFunction<? super Field<?>, ? super Integer, ? extends Name> biFunction);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createOrReplaceView(Table<?> table, Function<? super Field<?>, ? extends Field<?>> function);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createOrReplaceView(Table<?> table, BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> biFunction);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createViewIfNotExists(String str, Function<? super Field<?>, ? extends String> function);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createViewIfNotExists(String str, BiFunction<? super Field<?>, ? super Integer, ? extends String> biFunction);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createViewIfNotExists(Name name, Function<? super Field<?>, ? extends Name> function);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createViewIfNotExists(Name name, BiFunction<? super Field<?>, ? super Integer, ? extends Name> biFunction);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createViewIfNotExists(Table<?> table, Function<? super Field<?>, ? extends Field<?>> function);

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    CreateViewAsStep<Record> createViewIfNotExists(Table<?> table, BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> biFunction);

    @Support
    @CheckReturnValue
    @NotNull
    AlterTableStep alterTable(String str);

    @Support
    @CheckReturnValue
    @NotNull
    AlterTableStep alterTable(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    AlterTableStep alterTable(Table<?> table);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableStep alterTableIfExists(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableStep alterTableIfExists(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableStep alterTableIfExists(Table<?> table);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    BigInteger lastID() throws DataAccessException;

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    BigInteger nextval(String str) throws DataAccessException;

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    BigInteger nextval(Name name) throws DataAccessException;

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    <T extends Number> T nextval(Sequence<T> sequence) throws DataAccessException;

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    <T extends Number> List<T> nextvals(Sequence<T> sequence, int i) throws DataAccessException;

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    BigInteger currval(String str) throws DataAccessException;

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    BigInteger currval(Name name) throws DataAccessException;

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    <T extends Number> T currval(Sequence<T> sequence) throws DataAccessException;

    @NotNull
    <R extends UDTRecord<R>> R newRecord(UDT<R> udt);

    @NotNull
    <R extends Record> R newRecord(Table<R> table);

    @NotNull
    <R extends Record> R newRecord(Table<R> table, Object obj);

    @NotNull
    Record newRecord(Field<?>... fieldArr);

    @NotNull
    Record newRecord(Collection<? extends Field<?>> collection);

    @NotNull
    <T1> Record1<T1> newRecord(Field<T1> field);

    @NotNull
    <T1, T2> Record2<T1, T2> newRecord(Field<T1> field, Field<T2> field2);

    @NotNull
    <T1, T2, T3> Record3<T1, T2, T3> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @NotNull
    <T1, T2, T3, T4> Record4<T1, T2, T3, T4> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @NotNull
    <T1, T2, T3, T4, T5> Record5<T1, T2, T3, T4, T5> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @NotNull
    <T1, T2, T3, T4, T5, T6> Record6<T1, T2, T3, T4, T5, T6> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7> Record7<T1, T2, T3, T4, T5, T6, T7> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8> Record8<T1, T2, T3, T4, T5, T6, T7, T8> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9> Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> newRecord(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22);

    @NotNull
    <R extends Record> Result<R> newResult(Table<R> table);

    @NotNull
    Result<Record> newResult(Field<?>... fieldArr);

    @NotNull
    Result<Record> newResult(Collection<? extends Field<?>> collection);

    @NotNull
    <T1> Result<Record1<T1>> newResult(Field<T1> field);

    @NotNull
    <T1, T2> Result<Record2<T1, T2>> newResult(Field<T1> field, Field<T2> field2);

    @NotNull
    <T1, T2, T3> Result<Record3<T1, T2, T3>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @NotNull
    <T1, T2, T3, T4> Result<Record4<T1, T2, T3, T4>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @NotNull
    <T1, T2, T3, T4, T5> Result<Record5<T1, T2, T3, T4, T5>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @NotNull
    <T1, T2, T3, T4, T5, T6> Result<Record6<T1, T2, T3, T4, T5, T6>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7> Result<Record7<T1, T2, T3, T4, T5, T6, T7>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8> Result<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9> Result<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Result<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Result<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Result<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Result<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Result<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Result<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Result<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Result<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Result<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Result<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Result<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Result<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Result<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> newResult(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22);

    @Blocking
    @Support
    @NotNull
    <R extends Record> Result<R> fetch(ResultQuery<R> resultQuery) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Cursor<R> fetchLazy(ResultQuery<R> resultQuery) throws DataAccessException;

    @Support
    @NotNull
    <R extends Record> CompletionStage<Result<R>> fetchAsync(ResultQuery<R> resultQuery);

    @Support
    @NotNull
    <R extends Record> CompletionStage<Result<R>> fetchAsync(Executor executor, ResultQuery<R> resultQuery);

    @Blocking
    @Support
    @NotNull
    <R extends Record> Stream<R> fetchStream(ResultQuery<R> resultQuery) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Results fetchMany(ResultQuery<R> resultQuery) throws DataAccessException;

    @Blocking
    @Support
    @Nullable
    <R extends Record> R fetchOne(ResultQuery<R> resultQuery) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(ResultQuery<R> resultQuery) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Optional<R> fetchOptional(ResultQuery<R> resultQuery) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    <T> T fetchValue(Table<? extends Record1<T>> table) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    <T, R extends Record1<T>> T fetchValue(ResultQuery<R> resultQuery) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    <T> T fetchValue(TableField<?, T> tableField) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    <T> T fetchValue(TableField<?, T> tableField, Condition condition) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    <T> T fetchValue(SelectField<T> selectField) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T, R extends Record1<T>> Optional<T> fetchOptionalValue(ResultQuery<R> resultQuery) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @Blocking
    @Support
    @NotNull
    <T> Optional<T> fetchOptionalValue(TableField<?, T> tableField) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @Blocking
    @Support
    @NotNull
    <T> Optional<T> fetchOptionalValue(TableField<?, T> tableField, Condition condition) throws DataAccessException, TooManyRowsException, InvalidResultException;

    @Blocking
    @Support
    @NotNull
    <T> List<T> fetchValues(Table<? extends Record1<T>> table) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T, R extends Record1<T>> List<T> fetchValues(ResultQuery<R> resultQuery) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T> List<T> fetchValues(TableField<?, T> tableField) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T> List<T> fetchValues(TableField<?, T> tableField, Condition condition) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <K, V> Map<K, V> fetchMap(ResultQuery<? extends Record2<K, V>> resultQuery) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <K, V> Map<K, List<V>> fetchGroups(ResultQuery<? extends Record2<K, V>> resultQuery) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <R extends TableRecord<R>> Result<R> fetchByExample(R r) throws DataAccessException;

    @Blocking
    @Support
    int fetchCount(Select<?> select) throws DataAccessException;

    @Blocking
    @Support
    int fetchCount(Table<?> table) throws DataAccessException;

    @Blocking
    @Support
    int fetchCount(Table<?> table, Condition condition) throws DataAccessException;

    @Blocking
    @Support
    int fetchCount(Table<?> table, Condition... conditionArr) throws DataAccessException;

    @Blocking
    @Support
    int fetchCount(Table<?> table, Collection<? extends Condition> collection) throws DataAccessException;

    @Blocking
    @Support
    boolean fetchExists(Select<?> select) throws DataAccessException;

    @Blocking
    @Support
    boolean fetchExists(Table<?> table) throws DataAccessException;

    @Blocking
    @Support
    boolean fetchExists(Table<?> table, Condition condition) throws DataAccessException;

    @Blocking
    @Support
    boolean fetchExists(Table<?> table, Condition... conditionArr) throws DataAccessException;

    @Blocking
    @Support
    boolean fetchExists(Table<?> table, Collection<? extends Condition> collection) throws DataAccessException;

    @Blocking
    @Support
    int execute(Query query) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Result<R> fetch(Table<R> table) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Result<R> fetch(Table<R> table, Condition condition) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Result<R> fetch(Table<R> table, Condition... conditionArr) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Result<R> fetch(Table<R> table, Collection<? extends Condition> collection) throws DataAccessException;

    @Blocking
    @Support
    @Nullable
    <R extends Record> R fetchOne(Table<R> table) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @Nullable
    <R extends Record> R fetchOne(Table<R> table, Condition condition) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @Nullable
    <R extends Record> R fetchOne(Table<R> table, Condition... conditionArr) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @Nullable
    <R extends Record> R fetchOne(Table<R> table, Collection<? extends Condition> collection) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition... conditionArr) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Collection<? extends Condition> collection) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    Record fetchSingle(SelectField<?>... selectFieldArr) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    Record fetchSingle(Collection<? extends SelectField<?>> collection) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1> Record1<T1> fetchSingle(SelectField<T1> selectField) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2> Record2<T1, T2> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3> Record3<T1, T2, T3> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4> Record4<T1, T2, T3, T4> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5> Record5<T1, T2, T3, T4, T5> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6> Record6<T1, T2, T3, T4, T5, T6> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7> Record7<T1, T2, T3, T4, T5, T6, T7> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8> Record8<T1, T2, T3, T4, T5, T6, T7, T8> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9> Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> fetchSingle(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21, SelectField<T22> selectField22) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14, Condition condition15) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14, Condition condition15, Condition condition16) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14, Condition condition15, Condition condition16, Condition condition17) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14, Condition condition15, Condition condition16, Condition condition17, Condition condition18) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14, Condition condition15, Condition condition16, Condition condition17, Condition condition18, Condition condition19) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14, Condition condition15, Condition condition16, Condition condition17, Condition condition18, Condition condition19, Condition condition20) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14, Condition condition15, Condition condition16, Condition condition17, Condition condition18, Condition condition19, Condition condition20, Condition condition21) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> R fetchSingle(Table<R> table, Condition condition, Condition condition2, Condition condition3, Condition condition4, Condition condition5, Condition condition6, Condition condition7, Condition condition8, Condition condition9, Condition condition10, Condition condition11, Condition condition12, Condition condition13, Condition condition14, Condition condition15, Condition condition16, Condition condition17, Condition condition18, Condition condition19, Condition condition20, Condition condition21, Condition condition22) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Optional<R> fetchOptional(Table<R> table) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Optional<R> fetchOptional(Table<R> table, Condition condition) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Optional<R> fetchOptional(Table<R> table, Condition... conditionArr) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Optional<R> fetchOptional(Table<R> table, Collection<? extends Condition> collection) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Support
    @Nullable
    <R extends Record> R fetchAny(Table<R> table) throws DataAccessException;

    @Blocking
    @Support
    @Nullable
    <R extends Record> R fetchAny(Table<R> table, Condition condition) throws DataAccessException;

    @Blocking
    @Support
    @Nullable
    <R extends Record> R fetchAny(Table<R> table, Condition... conditionArr) throws DataAccessException;

    @Blocking
    @Support
    @Nullable
    <R extends Record> R fetchAny(Table<R> table, Collection<? extends Condition> collection) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Cursor<R> fetchLazy(Table<R> table) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Cursor<R> fetchLazy(Table<R> table, Condition condition) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Cursor<R> fetchLazy(Table<R> table, Condition... conditionArr) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Cursor<R> fetchLazy(Table<R> table, Collection<? extends Condition> collection) throws DataAccessException;

    @Support
    @NotNull
    <R extends Record> CompletionStage<Result<R>> fetchAsync(Table<R> table);

    @Support
    @NotNull
    <R extends Record> CompletionStage<Result<R>> fetchAsync(Table<R> table, Condition condition);

    @Support
    @NotNull
    <R extends Record> CompletionStage<Result<R>> fetchAsync(Table<R> table, Condition... conditionArr);

    @Support
    @NotNull
    <R extends Record> CompletionStage<Result<R>> fetchAsync(Table<R> table, Collection<? extends Condition> collection);

    @Support
    @NotNull
    <R extends Record> CompletionStage<Result<R>> fetchAsync(Executor executor, Table<R> table);

    @Support
    @NotNull
    <R extends Record> CompletionStage<Result<R>> fetchAsync(Executor executor, Table<R> table, Condition condition);

    @Support
    @NotNull
    <R extends Record> CompletionStage<Result<R>> fetchAsync(Executor executor, Table<R> table, Condition... conditionArr);

    @Support
    @NotNull
    <R extends Record> CompletionStage<Result<R>> fetchAsync(Executor executor, Table<R> table, Collection<? extends Condition> collection);

    @Blocking
    @Support
    @NotNull
    <R extends Record> Stream<R> fetchStream(Table<R> table) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Stream<R> fetchStream(Table<R> table, Condition condition) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Stream<R> fetchStream(Table<R> table, Condition... conditionArr) throws DataAccessException;

    @Blocking
    @Support
    @NotNull
    <R extends Record> Stream<R> fetchStream(Table<R> table, Collection<? extends Condition> collection) throws DataAccessException;

    @Blocking
    @Support
    int executeInsert(TableRecord<?> tableRecord) throws DataAccessException;

    @Blocking
    @Support
    int executeUpdate(UpdatableRecord<?> updatableRecord) throws DataAccessException;

    @Blocking
    @Support
    int executeUpdate(TableRecord<?> tableRecord, Condition condition) throws DataAccessException;

    @Blocking
    @Support
    int executeDelete(UpdatableRecord<?> updatableRecord) throws DataAccessException;

    @Blocking
    @Support
    int executeDelete(TableRecord<?> tableRecord, Condition condition) throws DataAccessException;
}
