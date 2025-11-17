package org.jooq.impl;

import java.math.BigDecimal;
import java.util.Set;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Digits.class */
public final class Digits extends AbstractField<String> implements QOM.Digits {
    final Field<? extends Number> value;
    private static final Set<SQLDialect> NO_SUPPORT_DIGITS = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Digits(Field<? extends Number> value) {
        super(Names.N_DIGITS, Tools.allNotNull(SQLDataType.VARCHAR, value));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (NO_SUPPORT_DIGITS.contains(ctx.dialect())) {
            DataType<? extends Number> dataType = this.value.getDataType();
            if (dataType.getType() == Byte.class) {
                ctx.visit((Field<?>) DSL.lpad((Field<String>) DSL.abs(this.value).cast(SQLDataType.VARCHAR(DefaultDataType.BYTE_PRECISION)), DSL.inline(DefaultDataType.BYTE_PRECISION), DSL.inline(CustomBooleanEditor.VALUE_0)));
                return;
            }
            if (dataType.getType() == Short.class) {
                ctx.visit((Field<?>) DSL.lpad((Field<String>) DSL.abs(this.value).cast(SQLDataType.VARCHAR(DefaultDataType.SHORT_PRECISION)), DSL.inline(DefaultDataType.SHORT_PRECISION), DSL.inline(CustomBooleanEditor.VALUE_0)));
                return;
            }
            if (dataType.getType() == Integer.class) {
                ctx.visit((Field<?>) DSL.lpad((Field<String>) DSL.abs(this.value).cast(SQLDataType.VARCHAR(DefaultDataType.INTEGER_PRECISION)), DSL.inline(DefaultDataType.INTEGER_PRECISION), DSL.inline(CustomBooleanEditor.VALUE_0)));
                return;
            }
            if (dataType.getType() == Long.class) {
                ctx.visit((Field<?>) DSL.lpad((Field<String>) DSL.abs(this.value).cast(SQLDataType.VARCHAR(DefaultDataType.LONG_PRECISION)), DSL.inline(DefaultDataType.LONG_PRECISION), DSL.inline(CustomBooleanEditor.VALUE_0)));
                return;
            } else if (dataType.scaleDefined()) {
                ctx.visit((Field<?>) DSL.lpad((Field<String>) DSL.abs(this.value.mul(DSL.inline(BigDecimal.TEN.pow(dataType.scale())))).cast(dataType.scale(0)).cast(SQLDataType.VARCHAR(dataType.precision())), DSL.inline(dataType.precision()), DSL.inline(CustomBooleanEditor.VALUE_0)));
                return;
            } else {
                ctx.visit((Field<?>) DSL.lpad((Field<String>) DSL.abs(this.value).cast(SQLDataType.VARCHAR(dataType.precision())), DSL.inline(dataType.precision()), DSL.inline(CustomBooleanEditor.VALUE_0)));
                return;
            }
        }
        ctx.visit(Names.N_DIGITS).sql('(').visit((Field<?>) this.value).sql(')');
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<? extends Number> $arg1() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Digits $arg1(Field<? extends Number> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<? extends Number>, ? extends QOM.Digits> $constructor() {
        return a1 -> {
            return new Digits(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Digits) {
            QOM.Digits o = (QOM.Digits) that;
            return StringUtils.equals($value(), o.$value());
        }
        return super.equals(that);
    }
}
