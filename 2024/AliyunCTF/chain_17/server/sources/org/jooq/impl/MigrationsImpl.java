package org.jooq.impl;

import java.util.Collections;
import org.jooq.Commits;
import org.jooq.Configuration;
import org.jooq.ContentType;
import org.jooq.File;
import org.jooq.Migration;
import org.jooq.Migrations;
import org.jooq.Version;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/MigrationsImpl.class */
final class MigrationsImpl extends AbstractScope implements Migrations {
    /* JADX INFO: Access modifiers changed from: package-private */
    public MigrationsImpl(Configuration configuration) {
        super(configuration);
    }

    @Override // org.jooq.Migrations
    public final File file(String path, String content, ContentType type) {
        return new FileImpl(path, content, type);
    }

    @Override // org.jooq.Migrations
    public final org.jooq.History history() {
        return new HistoryImpl(configuration());
    }

    @Override // org.jooq.Migrations
    public final Version version(String id) {
        return new VersionImpl(configuration(), id);
    }

    @Override // org.jooq.Migrations
    public final Commits commits() {
        return new CommitsImpl(configuration(), new CommitImpl(configuration(), "init", "init", null, Collections.emptyList(), Collections.emptyList()));
    }

    @Override // org.jooq.Migrations
    public final Migration migrateTo(org.jooq.Commit to) {
        return new MigrationImpl(configuration(), to);
    }
}
