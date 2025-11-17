package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Asterisk.class */
public interface Asterisk extends SelectFieldOrAsterisk {
    @Support
    @NotNull
    Asterisk except(String... strArr);

    @Support
    @NotNull
    Asterisk except(Name... nameArr);

    @Support
    @NotNull
    Asterisk except(Field<?>... fieldArr);

    @Support
    @NotNull
    Asterisk except(Collection<? extends Field<?>> collection);

    @ApiStatus.Experimental
    @NotNull
    QOM.UnmodifiableList<? extends Field<?>> $except();
}
