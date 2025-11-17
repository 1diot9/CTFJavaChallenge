package org.jooq.impl;

import java.util.Set;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DropSchemaFinalStep;
import org.jooq.DropSchemaStep;
import org.jooq.Function3;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DropSchemaImpl.class */
public final class DropSchemaImpl extends AbstractDDLQuery implements QOM.DropSchema, DropSchemaStep, DropSchemaFinalStep {
    final Schema schema;
    final boolean ifExists;
    QOM.Cascade cascade;
    private static final Clause[] CLAUSES = {Clause.DROP_SCHEMA};
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS = SQLDialect.supportedUntil(SQLDialect.DERBY, SQLDialect.FIREBIRD);
    private static final Set<SQLDialect> REQUIRES_RESTRICT = SQLDialect.supportedBy(SQLDialect.DERBY);

    /* JADX INFO: Access modifiers changed from: package-private */
    public DropSchemaImpl(Configuration configuration, Schema schema, boolean ifExists) {
        this(configuration, schema, ifExists, null);
    }

    DropSchemaImpl(Configuration configuration, Schema schema, boolean ifExists, QOM.Cascade cascade) {
        super(configuration);
        this.schema = schema;
        this.ifExists = ifExists;
        this.cascade = cascade;
    }

    @Override // org.jooq.DropSchemaStep
    public final DropSchemaImpl cascade() {
        this.cascade = QOM.Cascade.CASCADE;
        return this;
    }

    @Override // org.jooq.DropSchemaStep
    public final DropSchemaImpl restrict() {
        this.cascade = QOM.Cascade.RESTRICT;
        return this;
    }

    private final boolean supportsIfExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        accept0(ctx);
    }

    private void accept0(Context<?> ctx) {
        if (this.ifExists && !supportsIfExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.DROP_SCHEMA, c -> {
                accept1(c);
            });
        } else {
            accept1(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v23, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v28, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v4, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    private void accept1(Context<?> ctx) {
        ctx.start(Clause.DROP_SCHEMA_SCHEMA).visit(Keywords.K_DROP);
        ctx.sql(' ').visit(Keywords.K_SCHEMA);
        if (this.ifExists && supportsIfExists(ctx)) {
            ctx.sql(' ').visit(Keywords.K_IF_EXISTS);
        }
        ctx.sql(' ').visit(this.schema);
        if (this.cascade == QOM.Cascade.CASCADE) {
            ctx.sql(' ').visit(Keywords.K_CASCADE);
        } else if (this.cascade == QOM.Cascade.RESTRICT || REQUIRES_RESTRICT.contains(ctx.dialect())) {
            ctx.sql(' ').visit(Keywords.K_RESTRICT);
        }
        ctx.end(Clause.DROP_SCHEMA_SCHEMA);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.QOM.DropSchema
    public final Schema $schema() {
        return this.schema;
    }

    @Override // org.jooq.impl.QOM.DropSchema
    public final boolean $ifExists() {
        return this.ifExists;
    }

    @Override // org.jooq.impl.QOM.DropSchema
    public final QOM.Cascade $cascade() {
        return this.cascade;
    }

    @Override // org.jooq.impl.QOM.DropSchema
    public final QOM.DropSchema $schema(Schema newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifExists()), $cascade());
    }

    @Override // org.jooq.impl.QOM.DropSchema
    public final QOM.DropSchema $ifExists(boolean newValue) {
        return $constructor().apply($schema(), Boolean.valueOf(newValue), $cascade());
    }

    @Override // org.jooq.impl.QOM.DropSchema
    public final QOM.DropSchema $cascade(QOM.Cascade newValue) {
        return $constructor().apply($schema(), Boolean.valueOf($ifExists()), newValue);
    }

    public final Function3<? super Schema, ? super Boolean, ? super QOM.Cascade, ? extends QOM.DropSchema> $constructor() {
        return (a1, a2, a3) -> {
            return new DropSchemaImpl(configuration(), a1, a2.booleanValue(), a3);
        };
    }
}
