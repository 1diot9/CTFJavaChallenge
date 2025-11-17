package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/QualifiedAsterisk.class */
public interface QualifiedAsterisk extends SelectFieldOrAsterisk {
    @NotNull
    Table<?> qualifier();

    @Support
    @NotNull
    QualifiedAsterisk except(String... strArr);

    @Support
    @NotNull
    QualifiedAsterisk except(Name... nameArr);

    @Support
    @NotNull
    QualifiedAsterisk except(Field<?>... fieldArr);

    @Support
    @NotNull
    QualifiedAsterisk except(Collection<? extends Field<?>> collection);

    @ApiStatus.Experimental
    @NotNull
    Table<?> $table();

    @ApiStatus.Experimental
    @NotNull
    QOM.UnmodifiableList<? extends Field<?>> $except();
}
