package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Version.class */
public interface Version extends Node<Version> {
    @NotNull
    Meta meta();

    @NotNull
    Queries migrateTo(Version version);

    @NotNull
    Version commit(String str, Meta meta);

    @NotNull
    Version commit(String str, String... strArr);

    @NotNull
    Version commit(String str, Source... sourceArr);

    @NotNull
    Version merge(String str, Version version);

    @NotNull
    Version apply(String str, Queries queries);

    @NotNull
    Version apply(String str, Query... queryArr);

    @NotNull
    Version apply(String str, Collection<? extends Query> collection);

    @NotNull
    Version apply(String str, String str2);
}
