package org.jooq.impl;

import java.util.Set;
import org.jooq.AlterDatabaseFinalStep;
import org.jooq.AlterDatabaseStep;
import org.jooq.Catalog;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Function3;
import org.jooq.Name;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AlterDatabaseImpl.class */
final class AlterDatabaseImpl extends AbstractDDLQuery implements QOM.AlterDatabase, AlterDatabaseStep, AlterDatabaseFinalStep {
    final Catalog database;
    final boolean ifExists;
    Catalog renameTo;
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlterDatabaseImpl(Configuration configuration, Catalog database, boolean ifExists) {
        this(configuration, database, ifExists, null);
    }

    AlterDatabaseImpl(Configuration configuration, Catalog database, boolean ifExists, Catalog renameTo) {
        super(configuration);
        this.database = database;
        this.ifExists = ifExists;
        this.renameTo = renameTo;
    }

    @Override // org.jooq.AlterDatabaseStep
    public final AlterDatabaseImpl renameTo(String renameTo) {
        return renameTo(DSL.catalog(DSL.name(renameTo)));
    }

    @Override // org.jooq.AlterDatabaseStep
    public final AlterDatabaseImpl renameTo(Name renameTo) {
        return renameTo(DSL.catalog(renameTo));
    }

    @Override // org.jooq.AlterDatabaseStep
    public final AlterDatabaseImpl renameTo(Catalog renameTo) {
        this.renameTo = renameTo;
        return this;
    }

    private final boolean supportsIfExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifExists && !supportsIfExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.ALTER_DATABASE, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v22, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    private final void accept0(Context<?> ctx) {
        if (0 != 0) {
            ctx.visit(Keywords.K_RENAME);
        } else {
            ctx.visit(Keywords.K_ALTER);
        }
        ctx.sql(' ').visit(Keywords.K_DATABASE);
        if (this.ifExists && supportsIfExists(ctx)) {
            ctx.sql(' ').visit(Keywords.K_IF_EXISTS);
        }
        ctx.sql(' ').visit(this.database);
        if (this.renameTo != null) {
            ctx.sql(' ').visit(0 != 0 ? Keywords.K_TO : Keywords.K_RENAME_TO).sql(' ').qualify(false, c -> {
                c.visit(this.renameTo);
            });
        }
    }

    @Override // org.jooq.impl.QOM.AlterDatabase
    public final Catalog $database() {
        return this.database;
    }

    @Override // org.jooq.impl.QOM.AlterDatabase
    public final boolean $ifExists() {
        return this.ifExists;
    }

    @Override // org.jooq.impl.QOM.AlterDatabase
    public final Catalog $renameTo() {
        return this.renameTo;
    }

    @Override // org.jooq.impl.QOM.AlterDatabase
    public final QOM.AlterDatabase $database(Catalog newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifExists()), $renameTo());
    }

    @Override // org.jooq.impl.QOM.AlterDatabase
    public final QOM.AlterDatabase $ifExists(boolean newValue) {
        return $constructor().apply($database(), Boolean.valueOf(newValue), $renameTo());
    }

    @Override // org.jooq.impl.QOM.AlterDatabase
    public final QOM.AlterDatabase $renameTo(Catalog newValue) {
        return $constructor().apply($database(), Boolean.valueOf($ifExists()), newValue);
    }

    public final Function3<? super Catalog, ? super Boolean, ? super Catalog, ? extends QOM.AlterDatabase> $constructor() {
        return (a1, a2, a3) -> {
            return new AlterDatabaseImpl(configuration(), a1, a2.booleanValue(), a3);
        };
    }
}
