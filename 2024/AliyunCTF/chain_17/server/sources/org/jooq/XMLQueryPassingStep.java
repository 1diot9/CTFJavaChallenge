package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/XMLQueryPassingStep.class */
public interface XMLQueryPassingStep {
    @Support({SQLDialect.POSTGRES})
    @NotNull
    Field<XML> passing(XML xml);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    Field<XML> passing(Field<XML> field);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    Field<XML> passingByRef(XML xml);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    Field<XML> passingByRef(Field<XML> field);
}
