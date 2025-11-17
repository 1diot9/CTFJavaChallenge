package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Commit.class */
public interface Commit extends Node<Commit> {
    @NotNull
    Collection<File> delta();

    @NotNull
    Collection<File> files();

    @NotNull
    Collection<Source> sources();

    @NotNull
    Collection<Tag> tags();

    @NotNull
    Commit tag(String str);

    @NotNull
    Commit tag(String str, String str2);

    @NotNull
    Version version();

    @NotNull
    Meta meta();

    @NotNull
    Files migrateTo(Commit commit);

    @NotNull
    Commit commit(String str, File... fileArr);

    @NotNull
    Commit commit(String str, Collection<? extends File> collection);

    @NotNull
    Commit commit(String str, String str2, File... fileArr);

    @NotNull
    Commit commit(String str, String str2, Collection<? extends File> collection);

    @NotNull
    Commit merge(String str, Commit commit, File... fileArr);

    @NotNull
    Commit merge(String str, Commit commit, Collection<? extends File> collection);

    @NotNull
    Commit merge(String str, String str2, Commit commit, File... fileArr);

    @NotNull
    Commit merge(String str, String str2, Commit commit, Collection<? extends File> collection);
}
