package org.jooq;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/FieldLike.class */
public interface FieldLike {
    @NotNull
    <T> Field<T> asField();

    @NotNull
    <T> Field<T> asField(String str);

    @Support
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <T> Field<T> asField(Function<? super Field<T>, ? extends String> function);
}
