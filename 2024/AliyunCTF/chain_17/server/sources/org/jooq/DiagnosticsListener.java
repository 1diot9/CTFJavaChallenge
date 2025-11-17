package org.jooq;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DiagnosticsListener.class */
public interface DiagnosticsListener {
    default void tooManyRowsFetched(DiagnosticsContext ctx) {
    }

    default void tooManyColumnsFetched(DiagnosticsContext ctx) {
    }

    default void unnecessaryWasNullCall(DiagnosticsContext ctx) {
    }

    default void missingWasNullCall(DiagnosticsContext ctx) {
    }

    default void duplicateStatements(DiagnosticsContext ctx) {
    }

    default void repeatedStatements(DiagnosticsContext ctx) {
    }

    default void exception(DiagnosticsContext ctx) {
    }
}
