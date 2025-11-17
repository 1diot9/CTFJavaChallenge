package org.jooq.impl;

import java.util.Set;
import org.jooq.Catalog;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DropDatabaseFinalStep;
import org.jooq.Function2;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DropDatabaseImpl.class */
final class DropDatabaseImpl extends AbstractDDLQuery implements QOM.DropDatabase, DropDatabaseFinalStep {
    final Catalog database;
    final boolean ifExists;
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS = SQLDialect.supportedUntil(SQLDialect.DERBY, SQLDialect.FIREBIRD);

    /* JADX INFO: Access modifiers changed from: package-private */
    public DropDatabaseImpl(Configuration configuration, Catalog database, boolean ifExists) {
        super(configuration);
        this.database = database;
        this.ifExists = ifExists;
    }

    private final boolean supportsIfExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifExists && !supportsIfExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.DROP_DATABASE, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    private void accept0(Context<?> ctx) {
        ctx.visit(Keywords.K_DROP).sql(' ').visit(Keywords.K_DATABASE);
        if (this.ifExists && supportsIfExists(ctx)) {
            ctx.sql(' ').visit(Keywords.K_IF_EXISTS);
        }
        ctx.sql(' ').visit(this.database);
    }

    @Override // org.jooq.impl.QOM.DropDatabase
    public final Catalog $database() {
        return this.database;
    }

    @Override // org.jooq.impl.QOM.DropDatabase
    public final boolean $ifExists() {
        return this.ifExists;
    }

    @Override // org.jooq.impl.QOM.DropDatabase
    public final QOM.DropDatabase $database(Catalog newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifExists()));
    }

    @Override // org.jooq.impl.QOM.DropDatabase
    public final QOM.DropDatabase $ifExists(boolean newValue) {
        return $constructor().apply($database(), Boolean.valueOf(newValue));
    }

    public final Function2<? super Catalog, ? super Boolean, ? extends QOM.DropDatabase> $constructor() {
        return (a1, a2) -> {
            return new DropDatabaseImpl(configuration(), a1, a2.booleanValue());
        };
    }
}
