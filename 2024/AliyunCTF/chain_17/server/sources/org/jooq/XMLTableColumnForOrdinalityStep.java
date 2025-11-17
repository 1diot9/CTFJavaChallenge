package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/XMLTableColumnForOrdinalityStep.class */
public interface XMLTableColumnForOrdinalityStep {
    @Support({SQLDialect.POSTGRES})
    @NotNull
    XMLTableColumnsStep forOrdinality();
}
