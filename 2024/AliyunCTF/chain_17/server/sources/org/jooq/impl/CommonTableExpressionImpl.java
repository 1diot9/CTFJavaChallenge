package org.jooq.impl;

import java.util.Set;
import org.jooq.CommonTableExpression;
import org.jooq.Context;
import org.jooq.DerivedColumnList;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.TableOptions;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CommonTableExpressionImpl.class */
public final class CommonTableExpressionImpl<R extends Record> extends AbstractTable<R> implements CommonTableExpression<R> {
    private static final Set<SQLDialect> SUPPORT_MATERIALIZED = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.SQLITE);
    private final DerivedColumnListImpl name;
    private final ResultQuery<R> query;
    private final FieldsImpl<R> fields;
    private final QOM.Materialized materialized;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CommonTableExpressionImpl(DerivedColumnListImpl name, ResultQuery<R> query, QOM.Materialized materialized) {
        super(TableOptions.expression(), name.name);
        this.name = name;
        this.query = query;
        this.fields = fields1();
        this.materialized = materialized;
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends R> getRecordType() {
        return this.query.getRecordType();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresCTE() {
        return true;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresTables() {
        return true;
    }

    /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v29, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v34, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (ctx.declareCTE()) {
            QueryPart s = this.query;
            ctx.visit(this.name);
            ctx.sql(' ').visit(Keywords.K_AS).sql(' ');
            if (this.materialized != null && SUPPORT_MATERIALIZED.contains(ctx.dialect())) {
                if (this.materialized == QOM.Materialized.MATERIALIZED) {
                    ctx.visit(Keywords.K_MATERIALIZED).sql(' ');
                } else {
                    ctx.visit(Keywords.K_NOT).sql(' ').visit(Keywords.K_MATERIALIZED).sql(' ');
                }
            }
            Tools.visitSubquery(ctx, s, 1);
            return;
        }
        if (!ctx.declareTables() || !(this.query instanceof Select) || !Transformations.transformInlineCTE(ctx.configuration())) {
            ctx.visit(this.name.name);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<R> fields0() {
        return this.fields;
    }

    final FieldsImpl<R> fields1() {
        Name unqualifiedName;
        Field<?>[] s = FieldsImpl.fieldsRow0((FieldsTrait) this.query).fields();
        Field<?>[] f = new Field[Tools.degree(this.query)];
        for (int i = 0; i < f.length; i++) {
            int i2 = i;
            Name[] nameArr = new Name[2];
            nameArr[0] = this.name.name;
            if (this.name.fieldNames.length > 0) {
                unqualifiedName = this.name.fieldNames[i];
            } else {
                unqualifiedName = s[i].getUnqualifiedName();
            }
            nameArr[1] = unqualifiedName;
            f[i2] = DSL.field(DSL.name(nameArr), f.length == 1 ? Tools.scalarType(this.query) : s[i].getDataType());
        }
        return new FieldsImpl<>(f);
    }

    @Override // org.jooq.CommonTableExpression
    public final DerivedColumnList $derivedColumnList() {
        return this.name;
    }

    @Override // org.jooq.CommonTableExpression
    public final ResultQuery<R> $query() {
        return this.query;
    }

    @Override // org.jooq.CommonTableExpression
    public final QOM.Materialized $materialized() {
        return this.materialized;
    }
}
