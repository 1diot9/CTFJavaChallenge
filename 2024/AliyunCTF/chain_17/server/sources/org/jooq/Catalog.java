package org.jooq;

import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Catalog.class */
public interface Catalog extends Named {
    @NotNull
    List<Schema> getSchemas();

    @Nullable
    Schema getSchema(String str);

    @Nullable
    Schema getSchema(Name name);

    @NotNull
    Stream<Schema> schemaStream();
}
