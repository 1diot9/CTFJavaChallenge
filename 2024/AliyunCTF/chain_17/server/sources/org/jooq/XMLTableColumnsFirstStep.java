package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/XMLTableColumnsFirstStep.class */
public interface XMLTableColumnsFirstStep {
    @Support({SQLDialect.POSTGRES})
    @NotNull
    XMLTableColumnForOrdinalityStep column(String str);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    XMLTableColumnForOrdinalityStep column(Name name);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    XMLTableColumnPathStep column(Field<?> field);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    XMLTableColumnPathStep column(String str, DataType<?> dataType);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    XMLTableColumnPathStep column(Name name, DataType<?> dataType);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    XMLTableColumnPathStep column(Field<?> field, DataType<?> dataType);
}
