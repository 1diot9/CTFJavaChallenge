package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.AggregateFilterStep;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.OrderedAggregateFunctionOfDeferredType;
import org.jooq.SortField;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ModeDeferred.class */
public final class ModeDeferred implements OrderedAggregateFunctionOfDeferredType {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.OrderedAggregateFunctionOfDeferredType
    public final <T> AggregateFilterStep<T> withinGroupOrderBy(OrderField<T> field) {
        DataType<BigDecimal> dataType;
        if (field instanceof SortField) {
            SortField<T> s = (SortField) field;
            dataType = s.$field().getDataType();
        } else if (field instanceof AbstractField) {
            AbstractField<T> f = (AbstractField) field;
            dataType = f.getDataType();
        } else {
            dataType = SQLDataType.NUMERIC;
        }
        return new DefaultAggregateFunction(Names.N_MODE, dataType, (Field<?>[]) new Field[0]).withinGroupOrderBy((OrderField<?>[]) new OrderField[]{field});
    }
}
