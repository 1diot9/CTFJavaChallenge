package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.JSONArrayAggOrderByStep;
import org.jooq.JSONB;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.SelectField;
import org.jooq.XML;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/MultisetAgg.class */
public final class MultisetAgg<R extends Record> extends AbstractAggregateFunction<Result<R>> implements QOM.MultisetAgg<R> {
    private final AbstractRow<R> row;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MultisetAgg(boolean distinct, SelectField<R> row) {
        super(distinct, Names.N_MULTISET_AGG, new MultisetDataType((AbstractRow) row, null), ((AbstractRow) row).fields());
        this.row = (AbstractRow) row;
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_MULTISET_CONDITION))) {
            ctx.data().remove(Tools.BooleanDataKey.DATA_MULTISET_CONDITION);
            ctx.data(Tools.BooleanDataKey.DATA_MULTISET_CONTENT, true, c -> {
                accept0(c, true);
            });
            ctx.data(Tools.BooleanDataKey.DATA_MULTISET_CONDITION, true);
            return;
        }
        ctx.data(Tools.BooleanDataKey.DATA_MULTISET_CONTENT, true, c2 -> {
            accept0(c2, false);
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.XMLAggOrderByStep] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    private final void accept0(Context<?> context, boolean z) {
        Field ofo;
        Field field;
        Field field2;
        switch (Tools.emulateMultiset(context.configuration())) {
            case JSON:
                JSONArrayAggOrderByStep<JSON> jsonArrayaggEmulation = Multiset.jsonArrayaggEmulation(context, this.row, true, this.distinct);
                if (z) {
                    field2 = fo((AbstractAggregateFunction) Multiset.returningClob(context, jsonArrayaggEmulation.orderBy(this.row.fields())));
                } else {
                    field2 = ofo((AbstractAggregateFunction) Multiset.returningClob(context, jsonArrayaggEmulation));
                }
                Field field3 = field2;
                if (z && Multiset.NO_SUPPORT_JSON_COMPARE.contains(context.dialect())) {
                    context.visit(field3.cast(SQLDataType.VARCHAR));
                    return;
                } else {
                    context.visit((Field<?>) field3);
                    return;
                }
            case JSONB:
                JSONArrayAggOrderByStep<JSONB> jsonbArrayaggEmulation = Multiset.jsonbArrayaggEmulation(context, this.row, true, this.distinct);
                if (z) {
                    field = fo((AbstractAggregateFunction) Multiset.returningClob(context, jsonbArrayaggEmulation.orderBy(this.row.fields())));
                } else {
                    field = ofo((AbstractAggregateFunction) Multiset.returningClob(context, jsonbArrayaggEmulation));
                }
                Field field4 = field;
                if (z && Multiset.NO_SUPPORT_JSONB_COMPARE.contains(context.dialect())) {
                    context.visit(field4.cast(SQLDataType.VARCHAR));
                    return;
                } else {
                    context.visit((Field<?>) field4);
                    return;
                }
            case XML:
                ?? xmlaggEmulation = Multiset.xmlaggEmulation(context, this.row, true);
                Name nResult = Multiset.nResult(context);
                Field[] fieldArr = new Field[1];
                if (z) {
                    ofo = fo((AbstractAggregateFunction) xmlaggEmulation.orderBy(this.row.fields()));
                } else {
                    ofo = ofo((AbstractAggregateFunction) xmlaggEmulation);
                }
                fieldArr[0] = ofo;
                Field<XML> xmlelement = DSL.xmlelement(nResult, (Field<?>[]) fieldArr);
                if (z && Multiset.NO_SUPPORT_XML_COMPARE.contains(context.dialect())) {
                    context.visit(DSL.xmlserializeContent(xmlelement, SQLDataType.VARCHAR));
                    return;
                } else {
                    context.visit((Field<?>) xmlelement);
                    return;
                }
            case NATIVE:
                context.visit(Names.N_MULTISET_AGG).sql('(');
                acceptArguments1(context, new QueryPartListView(this.arguments.get(0)));
                acceptOrderBy(context);
                context.sql(')');
                acceptFilterClause(context);
                acceptOverClause(context);
                return;
            default:
                return;
        }
    }

    @Override // org.jooq.impl.QOM.MultisetAgg
    public final Row $row() {
        return this.row;
    }
}
