package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/XMLTablePassingStep.class */
public interface XMLTablePassingStep extends XMLTableColumnsFirstStep {
    @Support({SQLDialect.POSTGRES})
    @NotNull
    XMLTableColumnsFirstStep passing(XML xml);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    XMLTableColumnsFirstStep passing(Field<XML> field);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    XMLTableColumnsFirstStep passingByRef(XML xml);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    XMLTableColumnsFirstStep passingByRef(Field<XML> field);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    XMLTableColumnsFirstStep passingByValue(XML xml);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    XMLTableColumnsFirstStep passingByValue(Field<XML> field);
}
