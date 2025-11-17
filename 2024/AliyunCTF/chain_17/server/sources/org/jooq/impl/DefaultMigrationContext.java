package org.jooq.impl;

import java.util.Collections;
import java.util.Set;
import org.jooq.Configuration;
import org.jooq.MigrationContext;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.Schema;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultMigrationContext.class */
final class DefaultMigrationContext extends AbstractScope implements MigrationContext {
    final Set<Schema> migratedSchemas;
    final org.jooq.Commit migrationFrom;
    final org.jooq.Commit migrationTo;
    final Queries migrationQueries;
    final Queries revertUntrackedQueries;
    Query query;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultMigrationContext(Configuration configuration, Set<Schema> migratedSchemas, org.jooq.Commit migrationFrom, org.jooq.Commit migrationTo, Queries migrationQueries, Queries revertUntrackedQueries) {
        super(configuration);
        this.migratedSchemas = migratedSchemas;
        this.migrationFrom = migrationFrom;
        this.migrationTo = migrationTo;
        this.migrationQueries = migrationQueries;
        this.revertUntrackedQueries = revertUntrackedQueries;
    }

    @Override // org.jooq.MigrationContext
    public final Set<Schema> migratedSchemas() {
        return Collections.unmodifiableSet(this.migratedSchemas);
    }

    @Override // org.jooq.MigrationContext
    public final org.jooq.Commit migrationFrom() {
        return this.migrationFrom;
    }

    @Override // org.jooq.MigrationContext
    public final org.jooq.Commit migrationTo() {
        return this.migrationTo;
    }

    @Override // org.jooq.MigrationContext
    public final Queries migrationQueries() {
        return this.migrationQueries;
    }

    @Override // org.jooq.MigrationContext
    public final org.jooq.Commit queriesFrom() {
        return this.migrationFrom;
    }

    @Override // org.jooq.MigrationContext
    public final org.jooq.Commit queriesTo() {
        return this.migrationTo;
    }

    @Override // org.jooq.MigrationContext
    public final Queries queries() {
        return this.migrationQueries;
    }

    @Override // org.jooq.MigrationContext
    public final Query query() {
        return this.query;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void query(Query q) {
        this.query = q;
    }
}
