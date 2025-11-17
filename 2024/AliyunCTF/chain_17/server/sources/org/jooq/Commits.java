package org.jooq;

import java.io.IOException;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.exception.DataMigrationVerificationException;
import org.jooq.migrations.xml.jaxb.MigrationsType;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Commits.class */
public interface Commits extends Iterable<Commit> {
    @NotNull
    Commit root();

    @NotNull
    Commit current() throws DataMigrationVerificationException;

    @NotNull
    Commit latest() throws DataMigrationVerificationException;

    @Nullable
    Commit get(String str);

    @NotNull
    Commits add(Commit commit);

    @NotNull
    Commits addAll(Commit... commitArr);

    @NotNull
    Commits addAll(Collection<? extends Commit> collection);

    @NotNull
    Commits load(java.io.File file) throws IOException, DataMigrationVerificationException;

    @NotNull
    Commits load(MigrationsType migrationsType) throws DataMigrationVerificationException;

    @NotNull
    MigrationsType export();
}
