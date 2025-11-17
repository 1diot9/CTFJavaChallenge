package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractFunction.class */
abstract class AbstractFunction<T> extends AbstractField<T> implements QOM.Function<T> {
    private final boolean applySchemaMapping;

    /* renamed from: org.jooq.impl.AbstractFunction$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractFunction$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    abstract QueryPart arguments();

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractFunction(Name name, DataType<T> type, boolean applySchemaMapping) {
        super(name, type);
        this.applySchemaMapping = applySchemaMapping;
    }

    /* JADX WARN: Type inference failed for: r0v4, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                acceptFunctionName(ctx, this.applySchemaMapping, getQualifiedName());
                ctx.sql('(').visit(arguments()).sql(')');
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void acceptFunctionName(Context<?> ctx, boolean applySchemaMapping, Name name) {
        if (applySchemaMapping && name.qualified()) {
            Schema mapped = Tools.getMappedSchema(ctx, DSL.schema(name.qualifier()));
            if (mapped != null) {
                ctx.visit(mapped.getQualifiedName().append(name.unqualifiedName()));
                return;
            } else {
                ctx.visit(name.unqualifiedName());
                return;
            }
        }
        ctx.visit(name);
    }
}
