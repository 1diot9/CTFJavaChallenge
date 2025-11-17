package org.jooq.impl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.Param;
import org.jooq.QueryPart;
import org.jooq.SQLDialect;
import org.jooq.Typed;
import org.jooq.conf.ParamType;
import org.jooq.conf.RenderOptionalKeyword;
import org.jooq.conf.TransformUnneededArithmeticExpressions;
import org.jooq.exception.DataTypeException;
import org.jooq.impl.QOM;
import org.jooq.types.DayToSecond;
import org.jooq.types.Interval;
import org.jooq.types.YearToMonth;
import org.jooq.types.YearToSecond;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Expression.class */
public final class Expression<T> extends AbstractTransformable<T> implements QOM.UOperator2<Field<T>, Field<?>, Expression<T>> {
    private final ExpressionOperator operator;
    private final boolean internal;
    private final Field<T> lhs;
    private final Field<?> rhs;
    static final Set<SQLDialect> HASH_OP_FOR_BIT_XOR = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> SUPPORT_YEAR_TO_SECOND = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private static final Pattern TRUNC_TO_MICROS = Pattern.compile("([^.]*\\.\\d{0,6})\\d{0,3}");

    /* JADX INFO: Access modifiers changed from: package-private */
    public Expression(ExpressionOperator operator, boolean internal, Field<T> lhs, Field<?> rhs) {
        super(DSL.name(operator.toSQL()), lhs.getDataType());
        this.operator = operator;
        this.internal = internal;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final DataType<?> getExpressionDataType(AbstractField<?> l, ExpressionOperator operator, AbstractField<?> r) {
        DataType<?> lt = l.getExpressionDataType();
        DataType<?> rt = r.getExpressionDataType();
        switch (operator) {
            case MULTIPLY:
            case DIVIDE:
                return rt.isInterval() ? rt : lt;
            case ADD:
                return lt.isInterval() ? rt : lt;
            default:
                return lt;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTypedNamed
    public final DataType<?> getExpressionDataType() {
        return getExpressionDataType((AbstractField) this.lhs, this.operator, (AbstractField) this.rhs);
    }

    /* JADX WARN: Type inference failed for: r0v18, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractTransformable
    final void accept0(Context<?> ctx) {
        ctx.family();
        if ((ExpressionOperator.ADD == this.operator || ExpressionOperator.SUBTRACT == this.operator) && this.lhs.getDataType().isDateTime() && (this.rhs.getDataType().isNumeric() || this.rhs.getDataType().isInterval())) {
            ctx.visit((Field<?>) new DateExpression(this.lhs, this.operator, this.rhs));
        } else {
            ctx.sql('(').visit(this.lhs).sql(' ').sql(this.operator.toSQL()).sql(' ').visit(this.rhs).sql(')');
        }
    }

    @Override // org.jooq.impl.Transformable
    public final Field<T> transform(TransformUnneededArithmeticExpressions transform) {
        return transform(this, this.lhs, this.operator, this.rhs, this.internal, transform);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, R extends QOM.UOperator2<Field<T>, Field<T>, R>> Field<T> transform(R expression, Field<T> lhs, ExpressionOperator operator, Field<T> rhs, boolean internal, TransformUnneededArithmeticExpressions transform) {
        return (Field) expression;
    }

    private final Field<Number> lhsAsNumber() {
        return this.lhs;
    }

    private final Field<Number> rhsAsNumber() {
        return this.rhs;
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Expression$DateExpression.class */
    private static class DateExpression<T> extends AbstractField<T> implements QOM.UTransient {
        private final Field<T> lhs;
        private final ExpressionOperator operator;
        private final Field<?> rhs;

        DateExpression(Field<T> lhs, ExpressionOperator operator, Field<?> rhs) {
            super(DSL.name(operator.toSQL()), lhs.getDataType());
            this.lhs = lhs;
            this.operator = operator;
            this.rhs = rhs;
        }

        private final <U> Field<U> p(U u) {
            Param<U> result = DSL.val(u);
            if (((Param) this.rhs).isInline()) {
                ((AbstractParamX) result).setInline0(true);
            }
            return result;
        }

        @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
        public final void accept(Context<?> ctx) {
            if (this.rhs.getType() == YearToSecond.class && !Expression.SUPPORT_YEAR_TO_SECOND.contains(ctx.dialect())) {
                acceptYTSExpression(ctx);
            } else if (this.rhs.getDataType().isInterval()) {
                acceptIntervalExpression(ctx);
            } else {
                acceptNumberExpression(ctx);
            }
        }

        private final void acceptYTSExpression(Context<?> ctx) {
            if (this.rhs instanceof Param) {
                YearToSecond yts = rhsAsYTS();
                ctx.visit((Field<?>) new DateExpression(new DateExpression(this.lhs, this.operator, p(yts.getYearToMonth())), this.operator, p(yts.getDayToSecond())));
            } else {
                acceptIntervalExpression(ctx);
            }
        }

        /* JADX WARN: Type inference failed for: r0v111, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v117, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v127, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v139, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v150, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v161, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v35, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v51, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v60, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v76, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v94, types: [org.jooq.Context] */
        private final void acceptIntervalExpression(Context<?> ctx) {
            SQLDialect family = ctx.family();
            int sign = this.operator == ExpressionOperator.ADD ? 1 : -1;
            switch (family) {
                case CUBRID:
                case MARIADB:
                case MYSQL:
                    Interval interval = rhsAsInterval();
                    if (this.operator == ExpressionOperator.SUBTRACT) {
                        interval = interval.neg();
                    }
                    if (this.rhs.getType() == YearToMonth.class) {
                        ctx.visit(Names.N_DATE_ADD).sql('(').visit((Field<?>) this.lhs).sql(", ").visit(Keywords.K_INTERVAL).sql(' ').visit(Tools.field(interval, SQLDataType.VARCHAR)).sql(' ').visit(Keywords.K_YEAR_MONTH).sql(')');
                        return;
                    } else if (family == SQLDialect.CUBRID) {
                        ctx.visit(Names.N_DATE_ADD).sql('(').visit((Field<?>) this.lhs).sql(", ").visit(Keywords.K_INTERVAL).sql(' ').visit(Tools.field(interval, SQLDataType.VARCHAR)).sql(' ').visit(Keywords.K_DAY_MILLISECOND).sql(')');
                        return;
                    } else {
                        ctx.visit(Names.N_DATE_ADD).sql('(').visit((Field<?>) this.lhs).sql(", ").visit(Keywords.K_INTERVAL).sql(' ').visit(Tools.field(Expression.TRUNC_TO_MICROS.matcher(String.valueOf(interval)).replaceAll("$1"), SQLDataType.VARCHAR)).sql(' ').visit(Keywords.K_DAY_MICROSECOND).sql(')');
                        return;
                    }
                case DERBY:
                    boolean needsCast = getDataType().getType() != Timestamp.class;
                    if (needsCast) {
                        ctx.visit(Keywords.K_CAST).sql('(');
                    }
                    if (this.rhs.getType() == YearToMonth.class) {
                        ctx.sql("{fn ").visit(Names.N_TIMESTAMPADD).sql('(').visit(Names.N_SQL_TSI_MONTH).sql(", ").visit((Field<?>) p(Integer.valueOf(sign * rhsAsYTM().intValue()))).sql(", ").visit((Field<?>) this.lhs).sql(") }");
                    } else {
                        ctx.sql("{fn ").visit(Names.N_TIMESTAMPADD).sql('(').visit(Names.N_SQL_TSI_SECOND).sql(", ").visit((Field<?>) p(Long.valueOf(sign * ((long) rhsAsDTS().getTotalSeconds())))).sql(", {fn ").visit(Names.N_TIMESTAMPADD).sql('(').visit(ctx.family() == SQLDialect.DERBY ? Names.N_SQL_TSI_FRAC_SECOND : Names.N_SQL_TSI_MILLI_SECOND).sql(", ").visit((Field<?>) p(Long.valueOf(sign * rhsAsDTS().getMilli() * (ctx.family() == SQLDialect.DERBY ? 1000000L : 1L)))).sql(", ").visit((Field<?>) this.lhs).sql(") }) }");
                    }
                    if (needsCast) {
                        ctx.sql(' ').visit(Keywords.K_AS).sql(' ').visit(DSL.keyword(getDataType().getCastTypeName(ctx.configuration()))).sql(')');
                        return;
                    }
                    return;
                case FIREBIRD:
                    if (this.rhs.getType() == YearToMonth.class) {
                        ctx.visit(Names.N_DATEADD).sql('(').visit(Keywords.K_MONTH).sql(", ").visit((Field<?>) p(Integer.valueOf(sign * rhsAsYTM().intValue()))).sql(", ").visit((Field<?>) this.lhs).sql(')');
                        return;
                    } else if (rhsAsDTS().getMilli() > 0) {
                        ctx.visit(Names.N_DATEADD).sql('(').visit(Keywords.K_MILLISECOND).sql(", ").visit((Field<?>) p(Long.valueOf(sign * rhsAsDTS().getMilli()))).sql(", ").visit(Names.N_DATEADD).sql('(').visit(Keywords.K_SECOND).sql(", ").visit((Field<?>) p(Long.valueOf(sign * ((long) rhsAsDTS().getTotalSeconds())))).sql(", ").visit((Field<?>) this.lhs).sql(')').sql(')');
                        return;
                    } else {
                        ctx.visit(Names.N_DATEADD).sql('(').visit(Keywords.K_SECOND).sql(", ").visit((Field<?>) p(Long.valueOf(sign * ((long) rhsAsDTS().getTotalSeconds())))).sql(", ").visit((Field<?>) this.lhs).sql(')');
                        return;
                    }
                case SQLITE:
                    boolean ytm = this.rhs.getType() == YearToMonth.class;
                    Field p = p(Double.valueOf(ytm ? rhsAsYTM().intValue() : rhsAsDTS().getTotalSeconds()));
                    if (sign < 0) {
                        p = p.neg();
                    }
                    Field field = p;
                    Field<?>[] fieldArr = new Field[1];
                    fieldArr[0] = DSL.inline(ytm ? " months" : " seconds");
                    ctx.visit(Names.N_STRFTIME).sql("('%Y-%m-%d %H:%M:%f', ").visit((Field<?>) this.lhs).sql(", ").visit(field.concat(fieldArr)).sql(')');
                    return;
                case TRINO:
                    ctx.sql('(').visit(this.lhs).sql(' ').sql(this.operator.toSQL()).sql(' ').paramType(ParamType.INLINED, c -> {
                        c.visit(this.rhs);
                    }).sql(')');
                    return;
                default:
                    ctx.sql('(').visit(this.lhs).sql(' ').sql(this.operator.toSQL()).sql(' ').visit(this.rhs).sql(')');
                    return;
            }
        }

        /* JADX WARN: Type inference failed for: r0v104, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v113, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v13, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v20, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v35, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v46, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v65, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v76, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v82, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v92, types: [org.jooq.Context] */
        private final void acceptNumberExpression(Context<?> ctx) {
            switch (ctx.family()) {
                case CUBRID:
                case MARIADB:
                case MYSQL:
                    if (this.operator == ExpressionOperator.ADD) {
                        ctx.visit(Names.N_DATE_ADD).sql('(').visit((Field<?>) this.lhs).sql(", ").visit(Keywords.K_INTERVAL).sql(' ').visit((Field<?>) rhsAsNumber()).sql(' ').visit(Keywords.K_DAY).sql(')');
                        return;
                    } else {
                        ctx.visit(Names.N_DATE_ADD).sql('(').visit((Field<?>) this.lhs).sql(", ").visit(Keywords.K_INTERVAL).sql(' ').visit((Field<?>) rhsAsNumber().neg()).sql(' ').visit(Keywords.K_DAY).sql(')');
                        return;
                    }
                case DERBY:
                    boolean needsCast = getDataType().getType() != Timestamp.class;
                    if (needsCast) {
                        ctx.visit(Keywords.K_CAST).sql('(');
                    }
                    if (this.operator == ExpressionOperator.ADD) {
                        ctx.sql("{fn ").visit(Names.N_TIMESTAMPADD).sql('(').visit(DSL.keyword("sql_tsi_day")).sql(", ").visit((Field<?>) rhsAsNumber()).sql(", ").visit((Field<?>) this.lhs).sql(") }");
                    } else {
                        ctx.sql("{fn ").visit(Names.N_TIMESTAMPADD).sql('(').visit(DSL.keyword("sql_tsi_day")).sql(", ").visit((Field<?>) rhsAsNumber().neg()).sql(", ").visit((Field<?>) this.lhs).sql(") }");
                    }
                    if (needsCast) {
                        ctx.sql(' ').visit(Keywords.K_AS).sql(' ').visit(DSL.keyword(getDataType().getCastTypeName(ctx.configuration()))).sql(')');
                        return;
                    }
                    return;
                case FIREBIRD:
                    if (this.operator == ExpressionOperator.ADD) {
                        ctx.visit(Names.N_DATEADD).sql('(').visit(Keywords.K_DAY).sql(", ").visit((Field<?>) rhsAsNumber()).sql(", ").visit((Field<?>) this.lhs).sql(')');
                        return;
                    } else {
                        ctx.visit(Names.N_DATEADD).sql('(').visit(Keywords.K_DAY).sql(", ").visit((Field<?>) rhsAsNumber().neg()).sql(", ").visit((Field<?>) this.lhs).sql(')');
                        return;
                    }
                case SQLITE:
                    if (this.operator == ExpressionOperator.ADD) {
                        ctx.visit(Names.N_STRFTIME).sql("('%Y-%m-%d %H:%M:%f', ").visit((Field<?>) this.lhs).sql(", ").visit((Field<?>) rhsAsNumber().concat(DSL.inline(" day"))).sql(')');
                        return;
                    } else {
                        ctx.visit(Names.N_STRFTIME).sql("('%Y-%m-%d %H:%M:%f', ").visit((Field<?>) this.lhs).sql(", ").visit((Field<?>) rhsAsNumber().neg().concat(DSL.inline(" day"))).sql(')');
                        return;
                    }
                case TRINO:
                case POSTGRES:
                case YUGABYTEDB:
                    if (this.operator == ExpressionOperator.ADD) {
                        ctx.visit((Field<?>) new DateAdd(this.lhs, rhsAsNumber(), DatePart.DAY));
                        return;
                    } else {
                        ctx.visit((Field<?>) new DateAdd(this.lhs, rhsAsNumber().neg(), DatePart.DAY));
                        return;
                    }
                case HSQLDB:
                    if (this.operator == ExpressionOperator.ADD) {
                        ctx.visit(this.lhs.add(DSL.field("({0}) day", rhsAsNumber())));
                        return;
                    } else {
                        ctx.visit(this.lhs.sub(DSL.field("({0}) day", rhsAsNumber())));
                        return;
                    }
                default:
                    ctx.sql('(').visit(this.lhs).sql(' ').sql(this.operator.toSQL()).sql(' ').visit(this.rhs).sql(')');
                    return;
            }
        }

        private final YearToSecond rhsAsYTS() {
            try {
                return (YearToSecond) ((Param) this.rhs).getValue();
            } catch (ClassCastException e) {
                throw new DataTypeException("Cannot perform datetime arithmetic with a non-numeric, non-interval data type on the right hand side of the expression: " + String.valueOf(this.rhs), e);
            }
        }

        private final YearToMonth rhsAsYTM() {
            try {
                return (YearToMonth) ((Param) this.rhs).getValue();
            } catch (ClassCastException e) {
                throw new DataTypeException("Cannot perform datetime arithmetic with a non-numeric, non-interval data type on the right hand side of the expression: " + String.valueOf(this.rhs), e);
            }
        }

        private final DayToSecond rhsAsDTS() {
            try {
                return (DayToSecond) ((Param) this.rhs).getValue();
            } catch (ClassCastException e) {
                throw new DataTypeException("Cannot perform datetime arithmetic with a non-numeric, non-interval data type on the right hand side of the expression: " + String.valueOf(this.rhs), e);
            }
        }

        private final Interval rhsAsInterval() {
            try {
                return (Interval) ((Param) this.rhs).getValue();
            } catch (ClassCastException e) {
                throw new DataTypeException("Cannot perform datetime arithmetic with a non-numeric, non-interval data type on the right hand side of the expression: " + String.valueOf(this.rhs), e);
            }
        }

        private final Field<Number> rhsAsNumber() {
            return this.rhs;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Expression$Expr.class */
    public static final class Expr<Q extends QueryPart> extends Record {
        private final Q lhs;
        private final QueryPart op;
        private final Q rhs;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Expr(Q lhs, QueryPart op, Q rhs) {
            this.lhs = lhs;
            this.op = op;
            this.rhs = rhs;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Expr.class), Expr.class, "lhs;op;rhs", "FIELD:Lorg/jooq/impl/Expression$Expr;->lhs:Lorg/jooq/QueryPart;", "FIELD:Lorg/jooq/impl/Expression$Expr;->op:Lorg/jooq/QueryPart;", "FIELD:Lorg/jooq/impl/Expression$Expr;->rhs:Lorg/jooq/QueryPart;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Expr.class), Expr.class, "lhs;op;rhs", "FIELD:Lorg/jooq/impl/Expression$Expr;->lhs:Lorg/jooq/QueryPart;", "FIELD:Lorg/jooq/impl/Expression$Expr;->op:Lorg/jooq/QueryPart;", "FIELD:Lorg/jooq/impl/Expression$Expr;->rhs:Lorg/jooq/QueryPart;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Expr.class, Object.class), Expr.class, "lhs;op;rhs", "FIELD:Lorg/jooq/impl/Expression$Expr;->lhs:Lorg/jooq/QueryPart;", "FIELD:Lorg/jooq/impl/Expression$Expr;->op:Lorg/jooq/QueryPart;", "FIELD:Lorg/jooq/impl/Expression$Expr;->rhs:Lorg/jooq/QueryPart;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Q lhs() {
            return this.lhs;
        }

        public QueryPart op() {
            return this.op;
        }

        public Q rhs() {
            return this.rhs;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Incorrect types in method signature: <Q1::Lorg/jooq/QueryPart;Q2:TQ1;>(Lorg/jooq/Context<*>;TQ2;Ljava/util/function/Function<-TQ2;+Lorg/jooq/impl/Expression$Expr<TQ1;>;>;Ljava/util/function/Consumer<-Lorg/jooq/Context<*>;>;)V */
    public static final void acceptAssociative(Context context, QueryPart queryPart, java.util.function.Function function, Consumer consumer) {
        Expr expr = (Expr) function.apply(queryPart);
        Class<?> cls = queryPart.getClass();
        ArrayList arrayList = new ArrayList();
        ArrayDeque arrayDeque = new ArrayDeque();
        arrayDeque.push(expr);
        while (true) {
            Object pollFirst = arrayDeque.pollFirst();
            if (pollFirst == null) {
                break;
            }
            if (pollFirst instanceof Expr) {
                Expr expr2 = (Expr) pollFirst;
                boolean z = (((((Q1) expr2.lhs) instanceof Typed) && (((Q1) expr2.rhs) instanceof Typed) && !((Q1) expr2.lhs).getDataType().equals(((Q1) expr2.rhs).getDataType())) || RenderOptionalKeyword.ON.equals(context.settings().getRenderOptionalAssociativityParentheses())) ? false : true;
                if (z && cls.isInstance((Q1) expr2.rhs)) {
                    arrayDeque.push(function.apply((Q1) expr2.rhs));
                } else {
                    arrayDeque.push(expr2.rhs);
                }
                if (z && cls.isInstance((Q1) expr2.lhs)) {
                    arrayDeque.push(function.apply((Q1) expr2.lhs));
                } else {
                    arrayList.add((Q1) expr2.lhs);
                }
            } else {
                arrayList.add((QueryPart) pollFirst);
            }
        }
        for (int i = 0; i < arrayList.size(); i++) {
            if (i > 0) {
                consumer.accept(context);
                context.visit(expr.op).sql(' ');
            }
            context.visit((QueryPart) arrayList.get(i));
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.lhs;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<?> $arg2() {
        return this.rhs;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Expression<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Expression<T> $arg2(Field<?> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<?>, ? extends Expression<T>> $constructor() {
        return (a1, a2) -> {
            return new Expression(this.operator, this.internal, a1, a2);
        };
    }
}
