package org.jooq.impl;

import java.util.AbstractList;
import java.util.List;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.RowN;
import org.jooq.SQLDialect;
import org.jooq.SelectField;
import org.jooq.conf.ParamType;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractInList.class */
abstract class AbstractInList<T> extends AbstractCondition {
    static final Set<SQLDialect> REQUIRES_IN_LIMIT = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD);
    static final Set<SQLDialect> NO_SUPPORT_EMPTY_LISTS = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
    final Field<T> field;
    final QueryPartList<Field<T>> values;

    abstract Function2<? super RowN, ? super RowN[], ? extends Condition> rowCondition();

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractInList(Field<T> field, List<? extends Field<?>> values) {
        this.field = field;
        this.values = new QueryPartList<>(values);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final int limit(Context<?> ctx) {
        if (REQUIRES_IN_LIMIT.contains(ctx.dialect())) {
            return 1000;
        }
        return Integer.MAX_VALUE;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.field.getDataType().isEmbeddable()) {
            ctx.visit(rowCondition().apply(DSL.row((SelectField<?>[]) Tools.embeddedFields((Field<?>) this.field)), rows(this.values)));
        } else if (this.field.getDataType().isMultiset() && !Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_MULTISET_CONDITION))) {
            ctx.data(Tools.BooleanDataKey.DATA_MULTISET_CONDITION, true, c -> {
                c.visit((Condition) this);
            });
        } else {
            accept0(ctx);
        }
    }

    private final void accept0(Context<?> ctx) {
        accept1(ctx, this instanceof QOM.InList, this.field, this.values);
    }

    /* JADX WARN: Type inference failed for: r0v23, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v27, types: [org.jooq.Context] */
    private static final <T> void accept1(Context<?> ctx, boolean in, Field<T> field, QueryPartList<Field<T>> values) {
        int limit = limit(ctx);
        if (values.size() == 0 && NO_SUPPORT_EMPTY_LISTS.contains(ctx.dialect())) {
            if (in) {
                ctx.visit((Condition) DSL.falseCondition());
                return;
            } else {
                ctx.visit((Condition) DSL.trueCondition());
                return;
            }
        }
        if (values.size() > limit) {
            switch (ctx.family()) {
                case DERBY:
                case FIREBIRD:
                    ctx.sqlIndentStart('(');
                    int i = 0;
                    while (true) {
                        int i2 = i;
                        if (i2 < values.size()) {
                            if (i2 > 0) {
                                if (in) {
                                    ctx.formatSeparator().visit(Keywords.K_OR).sql(' ');
                                } else {
                                    ctx.formatSeparator().visit(Keywords.K_AND).sql(' ');
                                }
                            }
                            toSQLSubValues(ctx, field, in, padded(ctx, values.subList(i2, Math.min(i2 + limit, values.size())), limit));
                            i = i2 + limit;
                        } else {
                            ctx.sqlIndentEnd(')');
                            return;
                        }
                    }
                default:
                    toSQLSubValues(ctx, field, in, padded(ctx, values, limit));
                    return;
            }
        } else {
            toSQLSubValues(ctx, field, in, padded(ctx, values, limit));
        }
    }

    static final RowN[] rows(List<? extends Field<?>> values) {
        return (RowN[]) Tools.map(values, v -> {
            return DSL.row((SelectField<?>[]) Tools.embeddedFields((Field<?>) v));
        }, x$0 -> {
            return new RowN[x$0];
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> List<T> padded(Context<?> ctx, List<T> list, int limit) {
        int i;
        if (ctx.paramType() == ParamType.INDEXED && Boolean.TRUE.equals(ctx.settings().isInListPadding())) {
            if (REQUIRES_IN_LIMIT.contains(ctx.dialect())) {
                i = limit;
            } else {
                i = Integer.MAX_VALUE;
            }
            return new PaddedList(list, i, ((Integer) StringUtils.defaultIfNull(ctx.settings().getInListPadBase(), 2)).intValue());
        }
        return list;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v23, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v28, types: [org.jooq.Context] */
    static final void toSQLSubValues(Context<?> ctx, Field<?> field, boolean in, List<? extends Field<?>> subValues) {
        ctx.visit(field).sql(' ').visit(in ? Keywords.K_IN : Keywords.K_NOT_IN).sql(" (");
        if (subValues.size() > 1) {
            ctx.formatIndentStart().formatNewLine();
        }
        String separator = "";
        for (Field<?> value : subValues) {
            ctx.sql(separator).formatNewLineAfterPrintMargin().visit(value);
            separator = ", ";
        }
        if (subValues.size() > 1) {
            ctx.formatIndentEnd().formatNewLine();
        }
        ctx.sql(')');
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractInList$PaddedList.class */
    public static final class PaddedList<T> extends AbstractList<T> {
        private final List<T> delegate;
        private final int realSize;
        private final int padSize;

        PaddedList(List<T> delegate, int maxPadding, int padBase) {
            int b = Math.max(2, padBase);
            this.delegate = delegate;
            this.realSize = delegate.size();
            this.padSize = Math.min(maxPadding, (int) Math.round(Math.pow(b, Math.ceil(Math.log(this.realSize) / Math.log(b)))));
        }

        @Override // java.util.AbstractList, java.util.List
        public T get(int index) {
            return index < this.realSize ? this.delegate.get(index) : this.delegate.get(this.realSize - 1);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.padSize;
        }
    }

    public final Field<T> $arg1() {
        return this.field;
    }

    public final QOM.UnmodifiableList<? extends Field<T>> $arg2() {
        return QOM.unmodifiable((List) this.values);
    }
}
