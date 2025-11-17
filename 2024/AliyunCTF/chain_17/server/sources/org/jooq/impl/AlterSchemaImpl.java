package org.jooq.impl;

import org.jooq.AlterSchemaFinalStep;
import org.jooq.AlterSchemaStep;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Function3;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AlterSchemaImpl.class */
public final class AlterSchemaImpl extends AbstractDDLQuery implements QOM.AlterSchema, AlterSchemaStep, AlterSchemaFinalStep {
    final Schema schema;
    final boolean ifExists;
    Schema renameTo;
    private static final Clause[] CLAUSES = {Clause.ALTER_SCHEMA};

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlterSchemaImpl(Configuration configuration, Schema schema, boolean ifExists) {
        this(configuration, schema, ifExists, null);
    }

    AlterSchemaImpl(Configuration configuration, Schema schema, boolean ifExists, Schema renameTo) {
        super(configuration);
        this.schema = schema;
        this.ifExists = ifExists;
        this.renameTo = renameTo;
    }

    @Override // org.jooq.AlterSchemaStep
    public final AlterSchemaImpl renameTo(String renameTo) {
        return renameTo(DSL.schema(DSL.name(renameTo)));
    }

    @Override // org.jooq.AlterSchemaStep
    public final AlterSchemaImpl renameTo(Name renameTo) {
        return renameTo(DSL.schema(renameTo));
    }

    @Override // org.jooq.AlterSchemaStep
    public final AlterSchemaImpl renameTo(Schema renameTo) {
        this.renameTo = renameTo;
        return this;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        accept0(ctx);
    }

    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v22, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v25, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    private final void accept0(Context<?> ctx) {
        ctx.start(Clause.ALTER_SCHEMA_SCHEMA);
        if (0 != 0) {
            ctx.visit(Keywords.K_RENAME).sql(' ').visit(Keywords.K_SCHEMA);
        } else {
            ctx.visit(Keywords.K_ALTER_SCHEMA);
        }
        if (this.ifExists) {
            ctx.sql(' ').visit(Keywords.K_IF_EXISTS);
        }
        ctx.sql(' ').visit(this.schema).end(Clause.ALTER_SCHEMA_SCHEMA).sql(' ');
        if (this.renameTo != null) {
            ctx.start(Clause.ALTER_SCHEMA_RENAME).visit(0 != 0 ? Keywords.K_TO : Keywords.K_RENAME_TO).sql(' ').qualify(false, c -> {
                c.visit(this.renameTo);
            }).end(Clause.ALTER_SCHEMA_RENAME);
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.QOM.AlterSchema
    public final Schema $schema() {
        return this.schema;
    }

    @Override // org.jooq.impl.QOM.AlterSchema
    public final boolean $ifExists() {
        return this.ifExists;
    }

    @Override // org.jooq.impl.QOM.AlterSchema
    public final Schema $renameTo() {
        return this.renameTo;
    }

    @Override // org.jooq.impl.QOM.AlterSchema
    public final QOM.AlterSchema $schema(Schema newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifExists()), $renameTo());
    }

    @Override // org.jooq.impl.QOM.AlterSchema
    public final QOM.AlterSchema $ifExists(boolean newValue) {
        return $constructor().apply($schema(), Boolean.valueOf(newValue), $renameTo());
    }

    @Override // org.jooq.impl.QOM.AlterSchema
    public final QOM.AlterSchema $renameTo(Schema newValue) {
        return $constructor().apply($schema(), Boolean.valueOf($ifExists()), newValue);
    }

    public final Function3<? super Schema, ? super Boolean, ? super Schema, ? extends QOM.AlterSchema> $constructor() {
        return (a1, a2, a3) -> {
            return new AlterSchemaImpl(configuration(), a1, a2.booleanValue(), a3);
        };
    }
}
