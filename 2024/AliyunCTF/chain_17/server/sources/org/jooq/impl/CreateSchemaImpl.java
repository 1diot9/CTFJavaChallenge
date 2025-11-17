package org.jooq.impl;

import java.util.Set;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.CreateSchemaFinalStep;
import org.jooq.Function2;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CreateSchemaImpl.class */
public final class CreateSchemaImpl extends AbstractDDLQuery implements QOM.CreateSchema, CreateSchemaFinalStep {
    final Schema schema;
    final boolean ifNotExists;
    private static final Clause[] CLAUSES = {Clause.CREATE_SCHEMA};
    private static final Set<SQLDialect> NO_SUPPORT_IF_NOT_EXISTS = SQLDialect.supportedUntil(SQLDialect.DERBY, SQLDialect.FIREBIRD);

    /* JADX INFO: Access modifiers changed from: package-private */
    public CreateSchemaImpl(Configuration configuration, Schema schema, boolean ifNotExists) {
        super(configuration);
        this.schema = schema;
        this.ifNotExists = ifNotExists;
    }

    private final boolean supportsIfNotExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_NOT_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        accept0(ctx);
    }

    private final void accept0(Context<?> ctx) {
        if (this.ifNotExists && !supportsIfNotExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.CREATE_SCHEMA, c -> {
                accept1(c);
            });
        } else {
            accept1(ctx);
        }
    }

    private final void accept1(Context<?> ctx) {
        accept2(ctx);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v4, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    private final void accept2(Context<?> ctx) {
        ctx.start(Clause.CREATE_SCHEMA_NAME).visit(Keywords.K_CREATE);
        ctx.sql(' ').visit(Keywords.K_SCHEMA);
        if (this.ifNotExists && supportsIfNotExists(ctx)) {
            ctx.sql(' ').visit(Keywords.K_IF_NOT_EXISTS);
        }
        ctx.sql(' ').visit(this.schema);
        ctx.end(Clause.CREATE_SCHEMA_NAME);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.QOM.CreateSchema
    public final Schema $schema() {
        return this.schema;
    }

    @Override // org.jooq.impl.QOM.CreateSchema
    public final boolean $ifNotExists() {
        return this.ifNotExists;
    }

    @Override // org.jooq.impl.QOM.CreateSchema
    public final QOM.CreateSchema $schema(Schema newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifNotExists()));
    }

    @Override // org.jooq.impl.QOM.CreateSchema
    public final QOM.CreateSchema $ifNotExists(boolean newValue) {
        return $constructor().apply($schema(), Boolean.valueOf(newValue));
    }

    public final Function2<? super Schema, ? super Boolean, ? extends QOM.CreateSchema> $constructor() {
        return (a1, a2) -> {
            return new CreateSchemaImpl(configuration(), a1, a2.booleanValue());
        };
    }
}
