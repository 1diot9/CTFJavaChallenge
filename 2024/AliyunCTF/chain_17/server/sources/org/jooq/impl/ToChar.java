package org.jooq.impl;

import java.util.Set;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ToChar.class */
public final class ToChar extends AbstractField<String> implements QOM.ToChar {
    final Field<?> value;
    final Field<String> formatMask;
    private static final Set<SQLDialect> NO_SUPPORT_NATIVE_WITHOUT_MASK = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> NO_SUPPORT_NATIVE_WITH_MASK = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE);

    /* JADX INFO: Access modifiers changed from: package-private */
    public ToChar(Field<?> value) {
        super(Names.N_TO_CHAR, Tools.allNotNull(SQLDataType.VARCHAR, value));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.OTHER);
        this.formatMask = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ToChar(Field<?> value, Field<String> formatMask) {
        super(Names.N_TO_CHAR, Tools.allNotNull(SQLDataType.VARCHAR, value, formatMask));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.OTHER);
        this.formatMask = Tools.nullSafeNotNull(formatMask, SQLDataType.VARCHAR);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.formatMask == null && NO_SUPPORT_NATIVE_WITHOUT_MASK.contains(ctx.dialect())) {
            acceptCast(ctx);
        } else if (this.formatMask != null && NO_SUPPORT_NATIVE_WITH_MASK.contains(ctx.dialect())) {
            acceptCast(ctx);
        } else {
            acceptNative(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    private final void acceptNative(Context<?> ctx) {
        ctx.visit(Names.N_TO_CHAR).sql('(').visit(this.value);
        if (this.formatMask != null) {
            ctx.sql(", ").visit(this.formatMask);
        }
        ctx.sql(')');
    }

    private final void acceptCast(Context<?> ctx) {
        ctx.visit(DSL.cast(this.value, (DataType) SQLDataType.VARCHAR));
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<?> $arg1() {
        return this.value;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<String> $arg2() {
        return this.formatMask;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.ToChar $arg1(Field<?> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.ToChar $arg2(Field<String> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<?>, ? super Field<String>, ? extends QOM.ToChar> $constructor() {
        return (a1, a2) -> {
            return new ToChar(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.ToChar)) {
            return super.equals(that);
        }
        QOM.ToChar o = (QOM.ToChar) that;
        return StringUtils.equals($value(), o.$value()) && StringUtils.equals($formatMask(), o.$formatMask());
    }
}
