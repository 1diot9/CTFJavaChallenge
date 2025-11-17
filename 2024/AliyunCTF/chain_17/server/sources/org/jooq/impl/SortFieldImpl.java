package org.jooq.impl;

import java.util.Set;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SortFieldImpl.class */
public final class SortFieldImpl<T> extends AbstractQueryPart implements SortField<T>, SimpleCheckQueryPart {
    private static final Set<SQLDialect> NO_SUPPORT_NULLS = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.MARIADB, SQLDialect.MYSQL);
    final Field<T> field;
    final SortOrder order;
    QOM.NullOrdering nullOrdering;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SortFieldImpl(Field<T> field, SortOrder order) {
        this(field, order, null);
    }

    SortFieldImpl(Field<T> field, SortOrder order, QOM.NullOrdering nullOrdering) {
        this.field = field;
        this.order = order;
        this.nullOrdering = nullOrdering;
    }

    @Override // org.jooq.impl.SimpleCheckQueryPart
    public boolean isSimple(Context<?> ctx) {
        return this.nullOrdering == null && Tools.isSimple(ctx, this.field);
    }

    @Override // org.jooq.SortField
    public final String getName() {
        return this.field.getName();
    }

    @Override // org.jooq.SortField
    public final SortOrder getOrder() {
        return this.order;
    }

    @Override // org.jooq.SortField
    public final SortField<T> nullsFirst() {
        this.nullOrdering = QOM.NullOrdering.NULLS_FIRST;
        return this;
    }

    @Override // org.jooq.SortField
    public final SortField<T> nullsLast() {
        this.nullOrdering = QOM.NullOrdering.NULLS_LAST;
        return this;
    }

    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.nullOrdering != null) {
            if (NO_SUPPORT_NULLS.contains(ctx.dialect())) {
                Field<Integer> ifNull = this.nullOrdering == QOM.NullOrdering.NULLS_FIRST ? DSL.zero() : DSL.one();
                Field<Integer> ifNotNull = this.nullOrdering == QOM.NullOrdering.NULLS_FIRST ? DSL.one() : DSL.zero();
                ctx.visit(DSL.nvl2((Field<?>) this.field, (Field) ifNotNull, (Field) ifNull)).sql(", ");
                acceptFieldAndOrder(ctx, false);
                return;
            }
            acceptFieldAndOrder(ctx, true);
            return;
        }
        acceptFieldAndOrder(ctx, false);
    }

    /* JADX WARN: Type inference failed for: r0v11, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v20, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v23, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v26, types: [org.jooq.Context] */
    private final void acceptFieldAndOrder(Context<?> ctx, boolean includeNulls) {
        String separator = "";
        for (Field<?> f : Tools.flatten(this.field)) {
            ctx.sql(separator).visit(f);
            if (this.order != SortOrder.DEFAULT) {
                ctx.sql(' ').visit(this.order.toKeyword());
            }
            if (includeNulls) {
                if (this.nullOrdering == QOM.NullOrdering.NULLS_FIRST) {
                    ctx.sql(' ').visit(Keywords.K_NULLS_FIRST);
                } else {
                    ctx.sql(' ').visit(Keywords.K_NULLS_LAST);
                }
            }
            separator = ", ";
        }
    }

    @Override // org.jooq.SortField
    public final Field<T> $field() {
        return this.field;
    }

    @Override // org.jooq.SortField
    public final <U> SortField<U> $field(Field<U> newField) {
        if (newField == this.field) {
            return this;
        }
        return new SortFieldImpl(newField, this.order, this.nullOrdering);
    }

    @Override // org.jooq.SortField
    public final SortOrder $sortOrder() {
        return this.order;
    }

    @Override // org.jooq.SortField
    public final SortField<T> $sortOrder(SortOrder newOrder) {
        if (newOrder == this.order) {
            return this;
        }
        return new SortFieldImpl(this.field, newOrder, this.nullOrdering);
    }

    @Override // org.jooq.SortField
    public final QOM.NullOrdering $nullOrdering() {
        return this.nullOrdering;
    }

    @Override // org.jooq.SortField
    public final SortField<T> $nullOrdering(QOM.NullOrdering newOrdering) {
        if (newOrdering == this.nullOrdering) {
            return this;
        }
        return new SortFieldImpl(this.field, this.order, newOrdering);
    }
}
