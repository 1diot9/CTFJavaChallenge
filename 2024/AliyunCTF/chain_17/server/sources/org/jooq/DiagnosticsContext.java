package org.jooq;

import java.sql.ResultSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DiagnosticsContext.class */
public interface DiagnosticsContext extends Scope {
    @Nullable
    QueryPart part();

    @Nullable
    QueryPart transformedPart();

    @NotNull
    String message();

    @Nullable
    ResultSet resultSet();

    int resultSetConsumedRows();

    int resultSetFetchedRows();

    int resultSetConsumedColumnCount();

    int resultSetFetchedColumnCount();

    @NotNull
    List<String> resultSetConsumedColumnNames();

    @NotNull
    List<String> resultSetFetchedColumnNames();

    boolean resultSetUnnecessaryWasNullCall();

    boolean resultSetMissingWasNullCall();

    int resultSetColumnIndex();

    @NotNull
    String actualStatement();

    @NotNull
    String normalisedStatement();

    @NotNull
    Set<String> duplicateStatements();

    @NotNull
    List<String> repeatedStatements();

    @Nullable
    Throwable exception();
}
