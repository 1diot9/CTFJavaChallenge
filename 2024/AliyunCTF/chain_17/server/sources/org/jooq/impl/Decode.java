package org.jooq.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Decode.class */
public final class Decode<V, T> extends AbstractCaseSimple<V, T, Decode<V, T>> implements QOM.Decode<V, T> {
    private static final Set<SQLDialect> EMULATE_DECODE_ORACLE = SQLDialect.supportedBy(SQLDialect.MARIADB);
    private static final Set<SQLDialect> EMULATE_DISTINCT = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);

    @Override // org.jooq.impl.QOM.UOperator3
    public /* bridge */ /* synthetic */ QOM.UOperator3 $arg3(Object obj) {
        return (QOM.UOperator3) super.$arg3((Field) obj);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public /* bridge */ /* synthetic */ QOM.UOperator3 $arg2(Object obj) {
        return (QOM.UOperator3) super.$arg2((QOM.UnmodifiableList) obj);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public /* bridge */ /* synthetic */ QOM.UOperator3 $arg1(Object obj) {
        return (QOM.UOperator3) super.$arg1((Field) obj);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public /* bridge */ /* synthetic */ Object $arg3() {
        return super.$arg3();
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public /* bridge */ /* synthetic */ Object $arg2() {
        return super.$arg2();
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public /* bridge */ /* synthetic */ Object $arg1() {
        return super.$arg1();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Decode(Field<V> field, Field<V> search, Field<T> result, Field<?>[] more) {
        super(Names.N_DECODE, field, result.getDataType());
        when((Field) search, (Field) result);
        if (more.length > 1) {
            for (int i = 0; i + 1 < more.length; i += 2) {
                when((Field) more[i], (Field) more[i + 1]);
            }
        }
        if (more.length % 2 != 0) {
            else_((Field) more[more.length - 1]);
        }
    }

    Decode(Field<V> field, DataType<T> type) {
        super(Names.N_DECODE, field, type);
    }

    @Override // org.jooq.impl.AbstractCaseSimple
    final void accept0(Context<?> ctx) {
        if (EMULATE_DISTINCT.contains(ctx.dialect())) {
            ctx.visit(Tools.derivedTableIf(ctx, this.when.size() > 1, this.value, f -> {
                CaseSearched<T> c = new CaseSearched<>(getDataType());
                this.when.forEach(t -> {
                    c.when(f.isNotDistinctFrom((Field) t.$1()), (Field) t.$2());
                });
                if (this.else_ == null) {
                    return c;
                }
                return c.else_((Field) this.else_);
            }));
        } else if (EMULATE_DECODE_ORACLE.contains(ctx.dialect())) {
            ctx.visit(DSL.function(Names.N_DECODE_ORACLE, getDataType(), args()));
        } else {
            ctx.visit(DSL.function(Names.N_DECODE, getDataType(), args()));
        }
    }

    final Field<?>[] args() {
        List<Field<?>> result = new ArrayList<>();
        result.add(this.value);
        this.when.forEach(t -> {
            result.add((Field) t.$1());
            result.add((Field) t.$2());
        });
        if (this.else_ != null) {
            result.add(this.else_);
        }
        return (Field[]) result.toArray(Tools.EMPTY_FIELD);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractCaseSimple
    public final Decode<V, T> construct(Field<V> v, DataType<T> t) {
        return new Decode<>(v, t);
    }
}
