package org.jooq.impl;

import java.util.Collection;
import java.util.Set;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;
import org.springframework.util.ClassUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Array.class */
public final class Array<T> extends AbstractField<T[]> implements QOM.Array<T> {
    private static final Set<SQLDialect> REQUIRES_CAST = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    final FieldsImpl<Record> fields;

    /* renamed from: org.jooq.impl.Array$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Array$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Array(Collection<? extends Field<T>> fields) {
        super(Names.N_ARRAY, type(fields));
        this.fields = new FieldsImpl<>(fields);
    }

    private static <T> DataType<T[]> type(Collection<? extends Field<T>> collection) {
        if (collection == null || collection.isEmpty()) {
            return (DataType<T[]>) SQLDataType.OTHER.getArrayDataType();
        }
        return collection.iterator().next().getDataType().getArrayDataType();
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                Cast.renderCastIf(ctx, c -> {
                    switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
                        default:
                            ctx.visit(Keywords.K_ARRAY).sql('[').visit(this.fields).sql(']');
                            return;
                    }
                }, c2 -> {
                    DataType<?> type = (DataType) c2.data(Tools.ExtendedDataKey.DATA_EMPTY_ARRAY_BASE_TYPE);
                    if (type != null && !type.isOther()) {
                        c2.sql(type.getCastTypeName(ctx.configuration())).sql(ClassUtils.ARRAY_SUFFIX);
                    } else {
                        c2.visit(Keywords.K_INT).sql(ClassUtils.ARRAY_SUFFIX);
                    }
                }, () -> {
                    return this.fields.fields.length == 0 && REQUIRES_CAST.contains(ctx.dialect());
                });
                return;
        }
    }

    @Override // org.jooq.impl.QOM.Array
    public final QOM.UnmodifiableList<? extends Field<T>> $elements() {
        return QOM.unmodifiable(this.fields.fields);
    }
}
