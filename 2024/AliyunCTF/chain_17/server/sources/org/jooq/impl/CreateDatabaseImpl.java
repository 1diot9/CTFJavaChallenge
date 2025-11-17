package org.jooq.impl;

import java.util.Set;
import org.jooq.Catalog;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.CreateDatabaseFinalStep;
import org.jooq.Function2;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CreateDatabaseImpl.class */
final class CreateDatabaseImpl extends AbstractDDLQuery implements QOM.CreateDatabase, CreateDatabaseFinalStep {
    final Catalog database;
    final boolean ifNotExists;
    private static final Set<SQLDialect> NO_SUPPORT_IF_NOT_EXISTS = SQLDialect.supportedUntil(SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);

    /* JADX INFO: Access modifiers changed from: package-private */
    public CreateDatabaseImpl(Configuration configuration, Catalog database, boolean ifNotExists) {
        super(configuration);
        this.database = database;
        this.ifNotExists = ifNotExists;
    }

    private final boolean supportsIfNotExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_NOT_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifNotExists && !supportsIfNotExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.CREATE_DATABASE, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    private final void accept0(Context<?> ctx) {
        ctx.visit(Keywords.K_CREATE).sql(' ').visit(Keywords.K_DATABASE);
        if (this.ifNotExists && supportsIfNotExists(ctx)) {
            ctx.sql(' ').visit(Keywords.K_IF_NOT_EXISTS);
        }
        ctx.sql(' ').visit(this.database);
    }

    @Override // org.jooq.impl.QOM.CreateDatabase
    public final Catalog $database() {
        return this.database;
    }

    @Override // org.jooq.impl.QOM.CreateDatabase
    public final boolean $ifNotExists() {
        return this.ifNotExists;
    }

    @Override // org.jooq.impl.QOM.CreateDatabase
    public final QOM.CreateDatabase $database(Catalog newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifNotExists()));
    }

    @Override // org.jooq.impl.QOM.CreateDatabase
    public final QOM.CreateDatabase $ifNotExists(boolean newValue) {
        return $constructor().apply($database(), Boolean.valueOf(newValue));
    }

    public final Function2<? super Catalog, ? super Boolean, ? extends QOM.CreateDatabase> $constructor() {
        return (a1, a2) -> {
            return new CreateDatabaseImpl(configuration(), a1, a2.booleanValue());
        };
    }
}
