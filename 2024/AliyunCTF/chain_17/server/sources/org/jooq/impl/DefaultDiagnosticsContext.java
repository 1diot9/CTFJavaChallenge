package org.jooq.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.jooq.Configuration;
import org.jooq.DiagnosticsContext;
import org.jooq.QueryPart;
import org.jooq.tools.JooqLogger;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultDiagnosticsContext.class */
final class DefaultDiagnosticsContext extends AbstractScope implements DiagnosticsContext {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) DefaultDiagnosticsContext.class);
    final QueryPart part;
    final QueryPart transformedPart;
    final String message;
    ResultSet resultSet;
    DiagnosticsResultSet resultSetWrapper;
    boolean resultSetClosing;
    int resultSetFetchedColumnCount;
    int resultSetConsumedColumnCount;
    int resultSetFetchedRows;
    boolean resultSetFetchedRowsComputed;
    int resultSetConsumedRows;
    final String actualStatement;
    final String normalisedStatement;
    final Set<String> duplicateStatements;
    final List<String> repeatedStatements;
    boolean resultSetUnnecessaryWasNullCall;
    boolean resultSetMissingWasNullCall;
    int resultSetColumnIndex;
    final Throwable exception;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultDiagnosticsContext(Configuration configuration, String message, String actualStatement) {
        this(configuration, message, actualStatement, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultDiagnosticsContext(Configuration configuration, String message, String actualStatement, Throwable exception) {
        this(configuration, message, actualStatement, actualStatement, Collections.singleton(actualStatement), Collections.singletonList(actualStatement), null, null, exception);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultDiagnosticsContext(Configuration configuration, String message, String actualStatement, String normalisedStatement, Set<String> duplicateStatements, List<String> repeatedStatements, QueryPart part, QueryPart transformedPart, Throwable exception) {
        super(configuration);
        this.message = message;
        this.actualStatement = actualStatement;
        this.normalisedStatement = normalisedStatement;
        this.duplicateStatements = duplicateStatements == null ? Collections.emptySet() : duplicateStatements;
        this.repeatedStatements = repeatedStatements == null ? Collections.emptyList() : repeatedStatements;
        this.part = part;
        this.transformedPart = transformedPart;
        this.exception = exception;
    }

    @Override // org.jooq.DiagnosticsContext
    public final QueryPart part() {
        return this.part;
    }

    @Override // org.jooq.DiagnosticsContext
    public final QueryPart transformedPart() {
        return this.transformedPart;
    }

    @Override // org.jooq.DiagnosticsContext
    public final String message() {
        return this.message;
    }

    @Override // org.jooq.DiagnosticsContext
    public final ResultSet resultSet() {
        return this.resultSet;
    }

    @Override // org.jooq.DiagnosticsContext
    public final int resultSetConsumedRows() {
        if (this.resultSet == null) {
            return -1;
        }
        return this.resultSetConsumedRows;
    }

    @Override // org.jooq.DiagnosticsContext
    public final int resultSetFetchedRows() {
        if (this.resultSet == null) {
            return -1;
        }
        if (!this.resultSetFetchedRowsComputed) {
            this.resultSetFetchedRowsComputed = true;
            try {
                if (this.resultSetClosing || this.resultSet.getType() != 1003) {
                    while (this.resultSet.next()) {
                        this.resultSetFetchedRows++;
                    }
                    this.resultSet.absolute(this.resultSetConsumedRows);
                }
            } catch (SQLException e) {
            }
        }
        return this.resultSetFetchedRows;
    }

    @Override // org.jooq.DiagnosticsContext
    public final int resultSetConsumedColumnCount() {
        if (this.resultSet == null) {
            return -1;
        }
        return this.resultSetConsumedColumnCount;
    }

    @Override // org.jooq.DiagnosticsContext
    public final int resultSetFetchedColumnCount() {
        if (this.resultSet == null) {
            return -1;
        }
        return this.resultSetFetchedColumnCount;
    }

    @Override // org.jooq.DiagnosticsContext
    public final List<String> resultSetConsumedColumnNames() {
        return resultSetColumnNames(false);
    }

    @Override // org.jooq.DiagnosticsContext
    public final List<String> resultSetFetchedColumnNames() {
        return resultSetColumnNames(true);
    }

    private final List<String> resultSetColumnNames(boolean fetched) {
        List<String> result = new ArrayList<>();
        if (this.resultSet != null) {
            try {
                if (!this.resultSet.isClosed()) {
                    ResultSetMetaData meta = this.resultSet.getMetaData();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        if (fetched || this.resultSetWrapper.read.get(i - 1)) {
                            result.add(meta.getColumnLabel(i));
                        }
                    }
                }
            } catch (SQLException e) {
                log.info(e);
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override // org.jooq.DiagnosticsContext
    public final boolean resultSetUnnecessaryWasNullCall() {
        if (this.resultSet == null) {
            return false;
        }
        return this.resultSetUnnecessaryWasNullCall;
    }

    @Override // org.jooq.DiagnosticsContext
    public final boolean resultSetMissingWasNullCall() {
        if (this.resultSet == null) {
            return false;
        }
        return this.resultSetMissingWasNullCall;
    }

    @Override // org.jooq.DiagnosticsContext
    public final int resultSetColumnIndex() {
        if (this.resultSet == null) {
            return 0;
        }
        return this.resultSetColumnIndex;
    }

    @Override // org.jooq.DiagnosticsContext
    public final String actualStatement() {
        return this.actualStatement;
    }

    @Override // org.jooq.DiagnosticsContext
    public final String normalisedStatement() {
        return this.normalisedStatement;
    }

    @Override // org.jooq.DiagnosticsContext
    public final Set<String> duplicateStatements() {
        return Collections.unmodifiableSet(this.duplicateStatements);
    }

    @Override // org.jooq.DiagnosticsContext
    public final List<String> repeatedStatements() {
        return Collections.unmodifiableList(this.repeatedStatements);
    }

    @Override // org.jooq.DiagnosticsContext
    public final Throwable exception() {
        return this.exception;
    }
}
