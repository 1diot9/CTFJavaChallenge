package org.jooq.impl;

import java.util.List;
import org.jooq.Field;
import org.jooq.SortField;
import org.jooq.SortOrder;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SortFieldList.class */
public final class SortFieldList extends QueryPartList<SortField<?>> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SortFieldList() {
        this(emptyList());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SortFieldList(List<SortField<?>> wrappedList) {
        super(wrappedList);
    }

    final void addAll(Field<?>... fields) {
        addAll(Tools.map(fields, f -> {
            return f.asc();
        }));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.QueryPartCollectionView
    public final boolean canAdd(SortField<?> e) {
        return super.canAdd((SortFieldList) e) && !(e instanceof NoField);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean uniform() {
        return Tools.allMatch(this, f -> {
            return (f.getOrder() == SortOrder.DESC) == (((SortField) get(0)).getOrder() == SortOrder.DESC);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean nulls() {
        return Tools.anyMatch(this, f -> {
            return f.$nullOrdering() != null;
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final List<Field<?>> fields() {
        return Tools.map(this, f -> {
            return f.$field();
        });
    }
}
