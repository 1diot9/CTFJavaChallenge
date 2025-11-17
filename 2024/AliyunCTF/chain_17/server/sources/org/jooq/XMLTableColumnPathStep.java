package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/XMLTableColumnPathStep.class */
public interface XMLTableColumnPathStep extends XMLTableColumnForOrdinalityStep, XMLTableColumnsStep {
    @Support({SQLDialect.POSTGRES})
    @NotNull
    XMLTableColumnsStep path(String str);
}
