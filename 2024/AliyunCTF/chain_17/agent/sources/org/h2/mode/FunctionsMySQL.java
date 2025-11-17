package org.h2.mode;

import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import cn.hutool.core.date.DatePattern;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueVarchar;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/FunctionsMySQL.class */
public final class FunctionsMySQL extends ModeFunction {
    private static final int UNIX_TIMESTAMP = 1001;
    private static final int FROM_UNIXTIME = 1002;
    private static final int DATE = 1003;
    private static final int LAST_INSERT_ID = 1004;
    private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap<>();
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String[] FORMAT_REPLACE;

    static {
        FUNCTIONS.put("UNIX_TIMESTAMP", new FunctionInfo("UNIX_TIMESTAMP", UNIX_TIMESTAMP, -1, 11, true, false));
        FUNCTIONS.put("FROM_UNIXTIME", new FunctionInfo("FROM_UNIXTIME", FROM_UNIXTIME, -1, 2, true, true));
        FUNCTIONS.put("DATE", new FunctionInfo("DATE", DATE, 1, 17, true, true));
        FUNCTIONS.put("LAST_INSERT_ID", new FunctionInfo("LAST_INSERT_ID", LAST_INSERT_ID, -1, 12, false, false));
        FORMAT_REPLACE = new String[]{"%a", "EEE", "%b", "MMM", "%c", "MM", "%d", "dd", "%e", DateTokenConverter.CONVERTER_KEY, "%H", "HH", "%h", "hh", "%I", "hh", "%i", "mm", "%j", "DDD", "%k", "H", "%l", "h", "%M", "MMMM", "%m", "MM", "%p", "a", "%r", "hh:mm:ss a", "%S", "ss", "%s", "ss", "%T", DatePattern.NORM_TIME_PATTERN, "%W", "EEEE", "%w", "F", "%Y", DatePattern.NORM_YEAR_PATTERN, "%y", "yy", "%%", QuickTargetSourceCreator.PREFIX_THREAD_LOCAL};
    }

    public static int unixTimestamp(SessionLocal sessionLocal, Value value) {
        long epochSecondsFromLocal;
        if (value instanceof ValueTimestampTimeZone) {
            ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) value;
            epochSecondsFromLocal = ((DateTimeUtils.absoluteDayFromDateValue(valueTimestampTimeZone.getDateValue()) * DateTimeUtils.SECONDS_PER_DAY) + (valueTimestampTimeZone.getTimeNanos() / DateTimeUtils.NANOS_PER_SECOND)) - valueTimestampTimeZone.getTimeZoneOffsetSeconds();
        } else {
            ValueTimestamp valueTimestamp = (ValueTimestamp) value.convertTo(TypeInfo.TYPE_TIMESTAMP, sessionLocal);
            epochSecondsFromLocal = sessionLocal.currentTimeZone().getEpochSecondsFromLocal(valueTimestamp.getDateValue(), valueTimestamp.getTimeNanos());
        }
        return (int) epochSecondsFromLocal;
    }

    public static String fromUnixTime(int i) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date(i * 1000));
    }

    public static String fromUnixTime(int i, String str) {
        return new SimpleDateFormat(convertToSimpleDateFormat(str), Locale.ENGLISH).format(new Date(i * 1000));
    }

    private static String convertToSimpleDateFormat(String str) {
        String[] strArr = FORMAT_REPLACE;
        for (int i = 0; i < strArr.length; i += 2) {
            str = StringUtils.replaceAll(str, strArr[i], strArr[i + 1]);
        }
        return str;
    }

    public static FunctionsMySQL getFunction(String str) {
        FunctionInfo functionInfo = FUNCTIONS.get(str);
        if (functionInfo != null) {
            return new FunctionsMySQL(functionInfo);
        }
        return null;
    }

    FunctionsMySQL(FunctionInfo functionInfo) {
        super(functionInfo);
    }

    @Override // org.h2.mode.ModeFunction
    protected void checkParameterCount(int i) {
        int i2;
        int i3;
        switch (this.info.type) {
            case UNIX_TIMESTAMP /* 1001 */:
                i2 = 0;
                i3 = 1;
                break;
            case FROM_UNIXTIME /* 1002 */:
                i2 = 1;
                i3 = 2;
                break;
            case DATE /* 1003 */:
                i2 = 1;
                i3 = 1;
                break;
            case LAST_INSERT_ID /* 1004 */:
                i2 = 0;
                i3 = 1;
                break;
            default:
                throw DbException.getInternalError("type=" + this.info.type);
        }
        if (i < i2 || i > i3) {
            throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, this.info.name, i2 + ".." + i3);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        boolean optimizeArguments = optimizeArguments(sessionLocal);
        this.type = TypeInfo.getTypeInfo(this.info.returnDataType);
        if (optimizeArguments) {
            return ValueExpression.get(getValue(sessionLocal));
        }
        return this;
    }

    @Override // org.h2.expression.function.FunctionN, org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value;
        Value[] argumentsValues = getArgumentsValues(sessionLocal, this.args);
        if (argumentsValues == null) {
            return ValueNull.INSTANCE;
        }
        Value nullOrValue = getNullOrValue(sessionLocal, this.args, argumentsValues, 0);
        Value nullOrValue2 = getNullOrValue(sessionLocal, this.args, argumentsValues, 1);
        switch (this.info.type) {
            case UNIX_TIMESTAMP /* 1001 */:
                value = ValueInteger.get(unixTimestamp(sessionLocal, nullOrValue == null ? sessionLocal.currentTimestamp() : nullOrValue));
                break;
            case FROM_UNIXTIME /* 1002 */:
                value = ValueVarchar.get(nullOrValue2 == null ? fromUnixTime(nullOrValue.getInt()) : fromUnixTime(nullOrValue.getInt(), nullOrValue2.getString()));
                break;
            case DATE /* 1003 */:
                switch (nullOrValue.getValueType()) {
                    case 17:
                        value = nullOrValue;
                        break;
                    case 18:
                    case 19:
                    default:
                        try {
                            nullOrValue = nullOrValue.convertTo(TypeInfo.TYPE_TIMESTAMP, sessionLocal);
                        } catch (DbException e) {
                            value = ValueNull.INSTANCE;
                            break;
                        }
                    case 20:
                    case 21:
                        value = nullOrValue.convertToDate(sessionLocal);
                        break;
                }
            case LAST_INSERT_ID /* 1004 */:
                if (this.args.length == 0) {
                    Value lastIdentity = sessionLocal.getLastIdentity();
                    if (lastIdentity == ValueNull.INSTANCE) {
                        value = ValueBigint.get(0L);
                        break;
                    } else {
                        value = lastIdentity.convertToBigint(null);
                        break;
                    }
                } else {
                    value = nullOrValue;
                    if (value == ValueNull.INSTANCE) {
                        sessionLocal.setLastIdentity(ValueNull.INSTANCE);
                        break;
                    } else {
                        Value convertToBigint = value.convertToBigint(null);
                        value = convertToBigint;
                        sessionLocal.setLastIdentity(convertToBigint);
                        break;
                    }
                }
            default:
                throw DbException.getInternalError("type=" + this.info.type);
        }
        return value;
    }
}
