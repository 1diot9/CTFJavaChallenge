package org.jooq.impl;

import java.util.List;
import java.util.Set;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.Operator;
import org.jooq.Param;
import org.jooq.QuantifiedSelect;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectField;
import org.jooq.TableLike;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/EqQuantified.class */
public final class EqQuantified<T> extends AbstractCondition implements QOM.EqQuantified<T> {
    final Field<T> arg1;
    final QuantifiedSelect<? extends Record1<T>> arg2;
    private static final Set<SQLDialect> NO_SUPPORT_QUANTIFIED_LIKE = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE, SQLDialect.TRINO);
    private static final Set<SQLDialect> NO_SUPPORT_QUANTIFIED_SIMILAR_TO = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> SUPPORTS_QUANTIFIED_ARRAYS = SQLDialect.supportedBy(SQLDialect.POSTGRES);

    /* JADX INFO: Access modifiers changed from: package-private */
    public EqQuantified(Field<T> arg1, QuantifiedSelect<? extends Record1<T>> arg2) {
        this.arg1 = Tools.nullSafeNotNull(arg1, SQLDataType.OTHER);
        this.arg2 = arg2;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        acceptCompareCondition(ctx, this, this.arg1, Comparator.EQUALS, this.arg2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public static final <T> void acceptCompareCondition(Context<?> ctx, AbstractCondition condition, Field<T> arg1, Comparator op, QuantifiedSelect<? extends Record1<T>> arg2) {
        acceptCompareCondition(ctx, condition, arg1, op, arg2, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public static final <T> void acceptCompareCondition(Context<?> ctx, AbstractCondition condition, Field<T> arg1, Comparator op, QuantifiedSelect<? extends Record1<T>> arg2, Character escape) {
        SelectQueryImpl<?> s;
        boolean emulateOperator;
        List map;
        Condition c;
        Field<Boolean> lhs;
        TableLike<?> as;
        QOM.Quantifier q;
        if (arg1.getDataType().isEmbeddable()) {
            ctx.visit(DSL.row((SelectField<?>[]) Tools.embeddedFields((Field<?>) arg1)).compare(op, arg2));
            return;
        }
        if ((op == Comparator.EQUALS || op == Comparator.NOT_EQUALS) && (arg2 instanceof QOM.QuantifiedSelect) && (s = Transformations.subqueryWithLimit(((QOM.QuantifiedSelect) arg2).$query())) != null && Transformations.NO_SUPPORT_IN_LIMIT.contains(ctx.dialect())) {
            ctx.visit(arg1.compare(op, (QuantifiedSelect) Tools.quantify(((QOM.QuantifiedSelect) arg2).$quantifier(), DSL.select(DSL.asterisk()).from(s.asTable("t")))));
            return;
        }
        boolean quantifiedArrayParam = arg2 instanceof QOM.QuantifiedArray ? ((QOM.QuantifiedArray) arg2).$arg2() instanceof Param : false;
        boolean quantifiedArray = arg2 instanceof QOM.QuantifiedArray ? ((QOM.QuantifiedArray) arg2).$arg2() instanceof Array : false;
        switch (op) {
            case LIKE:
            case NOT_LIKE:
            case LIKE_IGNORE_CASE:
            case NOT_LIKE_IGNORE_CASE:
                emulateOperator = escape != null || NO_SUPPORT_QUANTIFIED_LIKE.contains(ctx.dialect());
                break;
            case SIMILAR_TO:
            case NOT_SIMILAR_TO:
                emulateOperator = escape != null || NO_SUPPORT_QUANTIFIED_SIMILAR_TO.contains(ctx.dialect());
                break;
            default:
                emulateOperator = false;
                break;
        }
        if ((quantifiedArrayParam || quantifiedArray) && SUPPORTS_QUANTIFIED_ARRAYS.contains(ctx.dialect()) && !emulateOperator) {
            if (quantifiedArray && ((Array) ((QOM.QuantifiedArray) arg2).$arg2()).fields.fields.length == 0) {
                ctx.data(Tools.ExtendedDataKey.DATA_EMPTY_ARRAY_BASE_TYPE, arg1.getDataType(), c2 -> {
                    accept1(c2, arg1, op, arg2);
                });
                return;
            } else {
                accept1(ctx, arg1, op, arg2);
                return;
            }
        }
        if (quantifiedArrayParam || quantifiedArray) {
            QOM.QuantifiedArray<?> a = (QOM.QuantifiedArray) arg2;
            Operator operator = a.$quantifier() == QOM.Quantifier.ALL ? Operator.AND : Operator.OR;
            if (a.$array() instanceof Array) {
                map = Tools.map(((Array) a.$array()).$elements(), v -> {
                    return comparisonCondition((Field<?>) arg1, op, (Field<String>) v, escape);
                });
            } else {
                map = Tools.map((Object[]) ((Param) a.$array()).getValue(), v2 -> {
                    return v2 instanceof Field ? comparisonCondition((Field<?>) arg1, op, (Field<String>) v2, escape) : comparisonCondition((Field<?>) arg1, op, v2, escape);
                });
            }
            ctx.visit(DSL.condition(operator, map));
            return;
        }
        if (emulateOperator) {
            Field<String> pattern = DSL.field(DSL.name("pattern"), SQLDataType.VARCHAR);
            switch (op) {
                case LIKE:
                case LIKE_IGNORE_CASE:
                case SIMILAR_TO:
                    c = comparisonCondition((Field<?>) arg1, op, pattern, escape);
                    lhs = DSL.inline(true);
                    break;
                case NOT_LIKE:
                case NOT_LIKE_IGNORE_CASE:
                case NOT_SIMILAR_TO:
                    c = comparisonCondition((Field<?>) arg1, inverse(op), pattern, escape);
                    lhs = DSL.inline(false);
                    break;
                default:
                    throw new IllegalStateException();
            }
            if (arg2 instanceof QuantifiedArray) {
                QuantifiedArray<?> a2 = (QuantifiedArray) arg2;
                as = new ArrayTable(a2.$array()).asTable("t", "pattern");
                q = a2.$quantifier();
            } else {
                QOM.QuantifiedSelect<?> qs = (QOM.QuantifiedSelect) arg2;
                as = new AliasedSelect(qs.$query(), true, true, false, DSL.name("pattern")).as("t");
                q = qs.$quantifier();
            }
            ctx.visit(lhs.eq(Tools.quantify(q, DSL.select(c).from(as))));
            return;
        }
        accept1(ctx, arg1, op, arg2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.EqQuantified$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/EqQuantified$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];

        static {
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.LIKE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.NOT_LIKE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.LIKE_IGNORE_CASE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.NOT_LIKE_IGNORE_CASE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.SIMILAR_TO.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.NOT_SIMILAR_TO.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.IN.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.NOT_IN.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.EQUALS.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.NOT_EQUALS.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.LESS.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.LESS_OR_EQUAL.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.GREATER.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.GREATER_OR_EQUAL.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.IS_DISTINCT_FROM.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.IS_NOT_DISTINCT_FROM.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    public static final void accept1(Context<?> ctx, Field<?> arg1, Comparator op, QuantifiedSelect<?> arg2) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(arg1).sql(' ').visit(op.toKeyword()).sql(' ').visit(arg2);
                return;
        }
    }

    private static final Comparator inverse(Comparator operator) {
        switch (operator) {
            case LIKE:
                return Comparator.NOT_LIKE;
            case NOT_LIKE:
                return Comparator.LIKE;
            case LIKE_IGNORE_CASE:
                return Comparator.NOT_LIKE_IGNORE_CASE;
            case NOT_LIKE_IGNORE_CASE:
                return Comparator.LIKE_IGNORE_CASE;
            case SIMILAR_TO:
                return Comparator.NOT_SIMILAR_TO;
            case NOT_SIMILAR_TO:
                return Comparator.SIMILAR_TO;
            case IN:
                return Comparator.NOT_IN;
            case NOT_IN:
                return Comparator.IN;
            case EQUALS:
                return Comparator.NOT_EQUALS;
            case NOT_EQUALS:
                return Comparator.EQUALS;
            case LESS:
                return Comparator.GREATER_OR_EQUAL;
            case LESS_OR_EQUAL:
                return Comparator.GREATER;
            case GREATER:
                return Comparator.LESS_OR_EQUAL;
            case GREATER_OR_EQUAL:
                return Comparator.LESS;
            case IS_DISTINCT_FROM:
                return Comparator.IS_NOT_DISTINCT_FROM;
            case IS_NOT_DISTINCT_FROM:
                return Comparator.IS_DISTINCT_FROM;
            default:
                throw new IllegalStateException();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Condition comparisonCondition(Field<?> arg1, Comparator op, Field<String> arg2, Character escape) {
        switch (op) {
            case LIKE:
                return escape != null ? arg1.like(arg2, escape.charValue()) : arg1.like(arg2);
            case NOT_LIKE:
                return escape != null ? arg1.notLike(arg2, escape.charValue()) : arg1.notLike(arg2);
            case LIKE_IGNORE_CASE:
                return escape != null ? arg1.likeIgnoreCase(arg2, escape.charValue()) : arg1.likeIgnoreCase(arg2);
            case NOT_LIKE_IGNORE_CASE:
                return escape != null ? arg1.notLikeIgnoreCase(arg2, escape.charValue()) : arg1.notLikeIgnoreCase(arg2);
            case SIMILAR_TO:
                return escape != null ? arg1.similarTo(arg2, escape.charValue()) : arg1.similarTo(arg2);
            case NOT_SIMILAR_TO:
                return escape != null ? arg1.notSimilarTo(arg2, escape.charValue()) : arg1.notSimilarTo(arg2);
            default:
                return arg1.compare(op, (Field<?>) arg2);
        }
    }

    private static final Condition comparisonCondition(Field<?> arg1, Comparator op, Object arg2, Character escape) {
        switch (op) {
            case LIKE:
                return escape != null ? arg1.like((String) Convert.convert(arg2, String.class), escape.charValue()) : arg1.like((String) Convert.convert(arg2, String.class));
            case NOT_LIKE:
                return escape != null ? arg1.notLike((String) Convert.convert(arg2, String.class), escape.charValue()) : arg1.notLike((String) Convert.convert(arg2, String.class));
            case LIKE_IGNORE_CASE:
                return escape != null ? arg1.likeIgnoreCase((String) Convert.convert(arg2, String.class), escape.charValue()) : arg1.likeIgnoreCase((String) Convert.convert(arg2, String.class));
            case NOT_LIKE_IGNORE_CASE:
                return escape != null ? arg1.notLikeIgnoreCase((String) Convert.convert(arg2, String.class), escape.charValue()) : arg1.notLikeIgnoreCase((String) Convert.convert(arg2, String.class));
            case SIMILAR_TO:
                return escape != null ? arg1.similarTo((String) Convert.convert(arg2, String.class), escape.charValue()) : arg1.similarTo((String) Convert.convert(arg2, String.class));
            case NOT_SIMILAR_TO:
                return escape != null ? arg1.notSimilarTo((String) Convert.convert(arg2, String.class), escape.charValue()) : arg1.notSimilarTo((String) Convert.convert(arg2, String.class));
            default:
                return arg1.compare(op, (Comparator) arg2);
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.arg1;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QuantifiedSelect<? extends Record1<T>> $arg2() {
        return this.arg2;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.EqQuantified<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.EqQuantified<T> $arg2(QuantifiedSelect<? extends Record1<T>> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super QuantifiedSelect<? extends Record1<T>>, ? extends QOM.EqQuantified<T>> $constructor() {
        return (a1, a2) -> {
            return new EqQuantified(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.EqQuantified)) {
            return super.equals(that);
        }
        QOM.EqQuantified<?> o = (QOM.EqQuantified) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}
