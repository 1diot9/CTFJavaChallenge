package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/XMLExistsPassingStep.class */
public interface XMLExistsPassingStep {
    @Support({SQLDialect.POSTGRES})
    @NotNull
    Condition passing(XML xml);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    Condition passing(Field<XML> field);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    Condition passingByRef(XML xml);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    Condition passingByRef(Field<XML> field);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    Condition passingByValue(XML xml);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    Condition passingByValue(Field<XML> field);
}
