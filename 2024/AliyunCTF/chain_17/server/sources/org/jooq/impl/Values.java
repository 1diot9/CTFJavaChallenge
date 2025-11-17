package org.jooq.impl;

import java.util.Set;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Values.class */
public final class Values<R extends Record> extends AbstractAutoAliasTable<R> implements QOM.Values<R> {
    static final Set<SQLDialect> NO_SUPPORT_VALUES = SQLDialect.supportedUntil(SQLDialect.FIREBIRD, SQLDialect.MARIADB);
    static final Set<SQLDialect> REQUIRE_ROWTYPE_CAST = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD);
    static final Set<SQLDialect> REQUIRE_ROWTYPE_CAST_FIRST_ROW = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> NO_SUPPORT_PARENTHESES = SQLDialect.supportedBy(new SQLDialect[0]);
    private final Row[] rows;
    private transient DataType<?>[] types;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Values(Row[] rows) {
        this(rows, DSL.name("v"), fieldAliases(degree(rows)));
    }

    Values(Row[] rows, Name alias, Name[] fieldAliases) {
        super(alias, fieldAliases);
        this.rows = assertNotEmpty(rows);
    }

    private static Name[] fieldAliases(int degree) {
        Name[] result = new Name[degree];
        for (int i = 0; i < result.length; i++) {
            result[i] = DSL.name("c" + (i + 1));
        }
        return result;
    }

    private static final int degree(Row[] rows) {
        if (Tools.isEmpty(rows)) {
            return 0;
        }
        return rows[0].size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Row[] assertNotEmpty(Row[] rows) {
        if (Tools.isEmpty(rows)) {
            throw new IllegalArgumentException("Cannot create a VALUES() constructor with an empty set of rows");
        }
        return rows;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractAutoAliasTable
    /* renamed from: construct */
    public final Values<R> construct2(Name newAlias, Name[] newFieldAliases) {
        return new Values<>(this.rows, newAlias, newFieldAliases);
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends R> getRecordType() {
        return RecordImplN.class;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<R> fields0() {
        return new FieldsImpl<>(Tools.map(this.fieldAliases, (n, i) -> {
            return DSL.field(this.alias.append(n), this.rows[0].dataType(i));
        }));
    }

    private final DataType<?>[] rowType() {
        if (this.types == null) {
            this.types = new DataType[this.rows[0].size()];
            for (int i = 0; i < this.types.length; i++) {
                this.types[i] = this.rows[0].dataType(i);
                if (this.types[i].isOther()) {
                    int j = 1;
                    while (true) {
                        if (j < this.rows.length) {
                            DataType<?> type = this.rows[j].dataType(i);
                            if (type.isOther()) {
                                j++;
                            } else {
                                this.types[i] = type;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return this.types;
    }

    private final Field<?>[] castToRowType(Field<?>[] fields) {
        Field<?>[] result = new Field[fields.length];
        for (int i = 0; i < result.length; i++) {
            DataType<?> type = rowType()[i];
            result[i] = fields[i].getDataType().equals(type) ? fields[i] : fields[i].cast(type);
        }
        return result;
    }

    private final Row castNullLiteralToRowType(Context<?> ctx, Row row) {
        if (Tools.anyMatch(row.fields(), f -> {
            return nullLiteralOrUntypedNullBind(ctx, f);
        })) {
            Field[] fieldArr = new Field[row.size()];
            for (int i = 0; i < fieldArr.length; i++) {
                if (nullLiteralOrUntypedNullBind(ctx, row.field(i)) && !rowType()[i].isOther()) {
                    fieldArr[i] = row.field(i).cast(rowType()[i]);
                } else {
                    fieldArr[i] = row.field(i);
                }
            }
            return DSL.row((SelectField<?>[]) fieldArr);
        }
        return row;
    }

    private final boolean nullLiteralOrUntypedNullBind(Context<?> ctx, Field<?> field) {
        return (Tools.isVal(field) && ((Val) field).getValue() == 0 && (((Val) field).isInline(ctx) || field.getDataType().isOther())) || (field instanceof NullCondition);
    }

    /* JADX WARN: Type inference failed for: r0v25, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v42, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v45, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v48, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Select<Record> unionAll;
        if (NO_SUPPORT_VALUES.contains(ctx.dialect())) {
            Select<Record> selects = null;
            boolean cast = REQUIRE_ROWTYPE_CAST.contains(ctx.dialect());
            for (Row row : this.rows) {
                Select<Record> select = DSL.select(cast ? castToRowType(row.fields()) : row.fields());
                if (selects == null) {
                    unionAll = select;
                } else {
                    unionAll = selects.unionAll(select);
                }
                selects = unionAll;
            }
            Tools.visitSubquery(ctx, selects, 1, true);
            return;
        }
        ctx.start(Clause.TABLE_VALUES);
        if (!NO_SUPPORT_PARENTHESES.contains(ctx.dialect())) {
            ctx.sqlIndentStart('(');
        }
        ctx.visit(Keywords.K_VALUES);
        if (this.rows.length > 1) {
            ctx.formatIndentStart().formatSeparator();
        } else {
            ctx.sql(' ');
        }
        for (int i = 0; i < this.rows.length; i++) {
            if (i > 0) {
                ctx.sql(',').formatSeparator();
            }
            if (ctx.family() == SQLDialect.MYSQL) {
                ctx.visit(Keywords.K_ROW).sql(" ");
            }
            if (i == 0 && REQUIRE_ROWTYPE_CAST_FIRST_ROW.contains(ctx.dialect())) {
                ctx.visit(castNullLiteralToRowType(ctx, this.rows[i]));
            } else if (REQUIRE_ROWTYPE_CAST.contains(ctx.dialect())) {
                ctx.visit(castNullLiteralToRowType(ctx, this.rows[i]));
            } else {
                ctx.visit(this.rows[i]);
            }
        }
        if (this.rows.length > 1) {
            ctx.formatIndentEnd().formatNewLine();
        }
        if (!NO_SUPPORT_PARENTHESES.contains(ctx.dialect())) {
            ctx.sqlIndentEnd(')');
        }
        ctx.end(Clause.TABLE_VALUES);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super QOM.UnmodifiableList<? extends Row>, ? extends QOM.Values<R>> $constructor() {
        return r -> {
            return new Values((Row[]) r.toArray(Tools.EMPTY_ROW));
        };
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.UnmodifiableList<? extends Row> $arg1() {
        return QOM.unmodifiable(this.rows);
    }
}
