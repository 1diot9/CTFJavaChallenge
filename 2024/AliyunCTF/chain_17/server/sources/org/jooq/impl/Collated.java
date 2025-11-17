package org.jooq.impl;

import org.jooq.Binding;
import org.jooq.Collation;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Collated.class */
public final class Collated extends AbstractField<String> implements QOM.Collated {
    private final Field<?> field;
    private final Collation collation;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Collated(Field<?> field, Collation collation) {
        super(field.getQualifiedName(), type(field).collation(collation), field.getCommentPart(), binding(field));
        this.field = field;
        this.collation = collation;
    }

    private static final Binding<?, String> binding(Field<?> field) {
        return field.getType() == String.class ? field.getBinding() : SQLDataType.VARCHAR.getBinding();
    }

    private static final DataType<String> type(Field<?> field) {
        return field.getType() == String.class ? field.getDataType() : SQLDataType.VARCHAR;
    }

    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (ctx.configuration().data("org.jooq.ddl.ignore-storage-clauses") == null) {
            ctx.sql("((").visit(this.field).sql(") ").visit(Keywords.K_COLLATE).sql(' ').visit(this.collation).sql(')');
        } else {
            ctx.visit(this.field);
        }
    }

    @Override // org.jooq.impl.QOM.Collated
    public final Field<?> $field() {
        return this.field;
    }

    @Override // org.jooq.impl.QOM.Collated
    public final Collation $collation() {
        return this.collation;
    }
}
