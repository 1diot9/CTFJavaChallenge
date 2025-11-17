package org.jooq.impl;

import java.util.function.BiFunction;
import org.jooq.CommonTableExpression;
import org.jooq.Context;
import org.jooq.DerivedColumnList;
import org.jooq.DerivedColumnList1;
import org.jooq.DerivedColumnList10;
import org.jooq.DerivedColumnList11;
import org.jooq.DerivedColumnList12;
import org.jooq.DerivedColumnList13;
import org.jooq.DerivedColumnList14;
import org.jooq.DerivedColumnList15;
import org.jooq.DerivedColumnList16;
import org.jooq.DerivedColumnList17;
import org.jooq.DerivedColumnList18;
import org.jooq.DerivedColumnList19;
import org.jooq.DerivedColumnList2;
import org.jooq.DerivedColumnList20;
import org.jooq.DerivedColumnList21;
import org.jooq.DerivedColumnList22;
import org.jooq.DerivedColumnList3;
import org.jooq.DerivedColumnList4;
import org.jooq.DerivedColumnList5;
import org.jooq.DerivedColumnList6;
import org.jooq.DerivedColumnList7;
import org.jooq.DerivedColumnList8;
import org.jooq.DerivedColumnList9;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.ResultQuery;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DerivedColumnListImpl.class */
public final class DerivedColumnListImpl extends AbstractQueryPart implements DerivedColumnList1, DerivedColumnList2, DerivedColumnList3, DerivedColumnList4, DerivedColumnList5, DerivedColumnList6, DerivedColumnList7, DerivedColumnList8, DerivedColumnList9, DerivedColumnList10, DerivedColumnList11, DerivedColumnList12, DerivedColumnList13, DerivedColumnList14, DerivedColumnList15, DerivedColumnList16, DerivedColumnList17, DerivedColumnList18, DerivedColumnList19, DerivedColumnList20, DerivedColumnList21, DerivedColumnList22, DerivedColumnList {
    final Name name;
    final Name[] fieldNames;
    final BiFunction<? super Field<?>, ? super Integer, ? extends String> fieldNameFunction;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DerivedColumnListImpl(Name name, Name[] fieldNames) {
        this.name = name;
        this.fieldNames = fieldNames;
        this.fieldNameFunction = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DerivedColumnListImpl(String name, BiFunction<? super Field<?>, ? super Integer, ? extends String> fieldNameFunction) {
        this.name = DSL.name(name);
        this.fieldNames = null;
        this.fieldNameFunction = fieldNameFunction;
    }

    final CommonTableExpression as0(ResultQuery query, QOM.Materialized materialized) {
        return this.fieldNameFunction != null ? new CommonTableExpressionImpl(new DerivedColumnListImpl(this.name, (Name[]) Tools.map(FieldsImpl.fieldsRow0((FieldsTrait) query).fields(), (f, i) -> {
            return DSL.name(this.fieldNameFunction.apply(f, Integer.valueOf(i)));
        }, x$0 -> {
            return new Name[x$0];
        })), query, materialized) : new CommonTableExpressionImpl(this, query, materialized);
    }

    @Override // org.jooq.DerivedColumnList1, org.jooq.DerivedColumnList2, org.jooq.DerivedColumnList3, org.jooq.DerivedColumnList4, org.jooq.DerivedColumnList5, org.jooq.DerivedColumnList6, org.jooq.DerivedColumnList7, org.jooq.DerivedColumnList8, org.jooq.DerivedColumnList9, org.jooq.DerivedColumnList10, org.jooq.DerivedColumnList11, org.jooq.DerivedColumnList12, org.jooq.DerivedColumnList13, org.jooq.DerivedColumnList14, org.jooq.DerivedColumnList15, org.jooq.DerivedColumnList16, org.jooq.DerivedColumnList17, org.jooq.DerivedColumnList18, org.jooq.DerivedColumnList19, org.jooq.DerivedColumnList20, org.jooq.DerivedColumnList21, org.jooq.DerivedColumnList22, org.jooq.DerivedColumnList
    public final CommonTableExpression as(ResultQuery query) {
        return as0(query, null);
    }

    @Override // org.jooq.DerivedColumnList1, org.jooq.DerivedColumnList2, org.jooq.DerivedColumnList3, org.jooq.DerivedColumnList4, org.jooq.DerivedColumnList5, org.jooq.DerivedColumnList6, org.jooq.DerivedColumnList7, org.jooq.DerivedColumnList8, org.jooq.DerivedColumnList9, org.jooq.DerivedColumnList10, org.jooq.DerivedColumnList11, org.jooq.DerivedColumnList12, org.jooq.DerivedColumnList13, org.jooq.DerivedColumnList14, org.jooq.DerivedColumnList15, org.jooq.DerivedColumnList16, org.jooq.DerivedColumnList17, org.jooq.DerivedColumnList18, org.jooq.DerivedColumnList19, org.jooq.DerivedColumnList20, org.jooq.DerivedColumnList21, org.jooq.DerivedColumnList22, org.jooq.DerivedColumnList
    public final CommonTableExpression asMaterialized(ResultQuery query) {
        return as0(query, QOM.Materialized.MATERIALIZED);
    }

    @Override // org.jooq.DerivedColumnList1, org.jooq.DerivedColumnList2, org.jooq.DerivedColumnList3, org.jooq.DerivedColumnList4, org.jooq.DerivedColumnList5, org.jooq.DerivedColumnList6, org.jooq.DerivedColumnList7, org.jooq.DerivedColumnList8, org.jooq.DerivedColumnList9, org.jooq.DerivedColumnList10, org.jooq.DerivedColumnList11, org.jooq.DerivedColumnList12, org.jooq.DerivedColumnList13, org.jooq.DerivedColumnList14, org.jooq.DerivedColumnList15, org.jooq.DerivedColumnList16, org.jooq.DerivedColumnList17, org.jooq.DerivedColumnList18, org.jooq.DerivedColumnList19, org.jooq.DerivedColumnList20, org.jooq.DerivedColumnList21, org.jooq.DerivedColumnList22, org.jooq.DerivedColumnList
    public final CommonTableExpression asNotMaterialized(ResultQuery query) {
        return as0(query, QOM.Materialized.NOT_MATERIALIZED);
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(this.name);
        if (this.fieldNames != null && this.fieldNames.length > 0) {
            ctx.sql('(');
            for (int i = 0; i < this.fieldNames.length; i++) {
                if (i > 0) {
                    ctx.sql(", ");
                }
                ctx.visit(this.fieldNames[i]);
            }
            ctx.sql(')');
        }
    }

    @Override // org.jooq.DerivedColumnList
    public final Name $tableName() {
        return this.name;
    }

    @Override // org.jooq.DerivedColumnList
    public final QOM.UnmodifiableList<? extends Name> $columnNames() {
        return QOM.unmodifiable(this.fieldNames != null ? this.fieldNames : Tools.EMPTY_NAME);
    }
}
