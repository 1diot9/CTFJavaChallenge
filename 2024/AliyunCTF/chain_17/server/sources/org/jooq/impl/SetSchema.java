package org.jooq.impl;

import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Schema;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SetSchema.class */
final class SetSchema extends AbstractDDLQuery implements QOM.SetSchema {
    final Schema schema;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SetSchema(Configuration configuration, Schema schema) {
        super(configuration);
        this.schema = schema;
    }

    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case MARIADB:
            case MYSQL:
                ctx.visit(Keywords.K_USE).sql(' ').visit(this.schema);
                return;
            case POSTGRES:
            case YUGABYTEDB:
                ctx.visit(Keywords.K_SET).sql(' ').visit(Keywords.K_SEARCH_PATH).sql(" = ").visit(this.schema);
                return;
            case DERBY:
            case H2:
            case HSQLDB:
            default:
                ctx.visit(Keywords.K_SET).sql(' ').visit(Keywords.K_SCHEMA).sql(' ').visit(this.schema);
                return;
        }
    }

    @Override // org.jooq.impl.QOM.SetSchema
    public final Schema $schema() {
        return this.schema;
    }

    @Override // org.jooq.impl.QOM.SetSchema
    public final QOM.SetSchema $schema(Schema newValue) {
        return $constructor().apply(newValue);
    }

    public final org.jooq.Function1<? super Schema, ? extends QOM.SetSchema> $constructor() {
        return a1 -> {
            return new SetSchema(configuration(), a1);
        };
    }
}
