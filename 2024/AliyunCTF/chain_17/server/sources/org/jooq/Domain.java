package org.jooq;

import java.util.List;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Domain.class */
public interface Domain<T> extends Qualified, Typed<T> {
    @NotNull
    List<Check<?>> getChecks();
}
