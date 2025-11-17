package org.jooq;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ExecuteContext.class */
public interface ExecuteContext extends Scope {

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ExecuteContext$BatchMode.class */
    public enum BatchMode {
        NONE,
        SINGLE,
        MULTIPLE
    }

    ConverterContext converterContext();

    Connection connection();

    @NotNull
    ExecuteType type();

    @Nullable
    Query query();

    @NotNull
    BatchMode batchMode();

    @NotNull
    Query[] batchQueries();

    @Nullable
    Routine<?> routine();

    @Nullable
    String sql();

    void sql(String str);

    int skipUpdateCounts();

    void skipUpdateCounts(int i);

    @NotNull
    String[] batchSQL();

    void connectionProvider(ConnectionProvider connectionProvider);

    @Nullable
    PreparedStatement statement();

    void statement(PreparedStatement preparedStatement);

    int statementExecutionCount();

    @Nullable
    ResultSet resultSet();

    void resultSet(ResultSet resultSet);

    int recordLevel();

    @Nullable
    Record record();

    void record(Record record);

    int rows();

    void rows(int i);

    int[] batchRows();

    int resultLevel();

    @Nullable
    Result<?> result();

    void result(Result<?> result);

    @Nullable
    RuntimeException exception();

    void exception(RuntimeException runtimeException);

    @Nullable
    SQLException sqlException();

    void sqlException(SQLException sQLException);

    @Nullable
    SQLWarning sqlWarning();

    void sqlWarning(SQLWarning sQLWarning);

    @NotNull
    String[] serverOutput();

    void serverOutput(String[] strArr);
}
