package org.h2.expression.function;

import java.text.DateFormatSymbols;
import java.util.Locale;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/DayMonthNameFunction.class */
public final class DayMonthNameFunction extends Function1 {
    public static final int DAYNAME = 0;
    public static final int MONTHNAME = 1;
    private static final String[] NAMES = {"DAYNAME", "MONTHNAME"};
    private static volatile String[][] MONTHS_AND_WEEKS;
    private final int function;

    public DayMonthNameFunction(Expression expression, int i) {
        super(expression);
        this.function = i;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        String str;
        Value value = this.arg.getValue(sessionLocal);
        if (value == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        long j = DateTimeUtils.dateAndTimeFromValue(value, sessionLocal)[0];
        switch (this.function) {
            case 0:
                str = getMonthsAndWeeks(1)[DateTimeUtils.getDayOfWeek(j, 0)];
                break;
            case 1:
                str = getMonthsAndWeeks(0)[DateTimeUtils.monthFromDateValue(j) - 1];
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return ValueVarchar.get(str, sessionLocal);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v5, types: [java.lang.String[]] */
    private static String[] getMonthsAndWeeks(int i) {
        String[][] strArr = MONTHS_AND_WEEKS;
        if (strArr == null) {
            DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(Locale.ENGLISH);
            strArr = new String[]{dateFormatSymbols.getMonths(), dateFormatSymbols.getWeekdays()};
            MONTHS_AND_WEEKS = strArr;
        }
        return strArr[i];
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.arg = this.arg.optimize(sessionLocal);
        this.type = TypeInfo.getTypeInfo(2, 20L, 0, null);
        if (this.arg.isConstant()) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}
