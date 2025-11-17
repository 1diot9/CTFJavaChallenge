package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Row.class */
public interface Row extends Fields, FieldOrRow, FieldOrRowOrSelect {
    int size();

    @Support
    @NotNull
    Condition isNull();

    @Support
    @NotNull
    Condition isNotNull();

    @ApiStatus.Experimental
    @NotNull
    QOM.UnmodifiableList<? extends Field<?>> $fields();
}
