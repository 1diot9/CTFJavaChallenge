package cn.hutool.db.sql;

import ch.qos.logback.core.CoreConstants;
import cn.hutool.core.clone.CloneSupport;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/sql/Condition.class */
public class Condition extends CloneSupport<Condition> {
    private static final String OPERATOR_LIKE = "LIKE";
    private static final String OPERATOR_IS = "IS";
    private static final String OPERATOR_IS_NOT = "IS NOT";
    private static final String OPERATOR_BETWEEN = "BETWEEN";
    private static final String VALUE_NULL = "NULL";
    private String field;
    private String operator;
    private Object value;
    private boolean isPlaceHolder;
    private Object secondValue;
    private LogicalOperator linkOperator;
    private static final String OPERATOR_IN = "IN";
    private static final List<String> OPERATORS = Arrays.asList("<>", "<=", "<", ">=", ">", "=", "!=", OPERATOR_IN);

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/sql/Condition$LikeType.class */
    public enum LikeType {
        StartWith,
        EndWith,
        Contains
    }

    public static Condition parse(String field, Object expression) {
        return new Condition(field, expression);
    }

    public Condition() {
        this.isPlaceHolder = true;
        this.linkOperator = LogicalOperator.AND;
    }

    public Condition(boolean isPlaceHolder) {
        this.isPlaceHolder = true;
        this.linkOperator = LogicalOperator.AND;
        this.isPlaceHolder = isPlaceHolder;
    }

    public Condition(String field, Object value) {
        this(field, "=", value);
        parseValue();
    }

    public Condition(String field, String operator, Object value) {
        this.isPlaceHolder = true;
        this.linkOperator = LogicalOperator.AND;
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public Condition(String field, String value, LikeType likeType) {
        this.isPlaceHolder = true;
        this.linkOperator = LogicalOperator.AND;
        this.field = field;
        this.operator = OPERATOR_LIKE;
        this.value = SqlUtil.buildLikeValue(value, likeType, false);
    }

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOperator() {
        return this.operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        setValue(value, false);
    }

    public void setValue(Object value, boolean isParse) {
        this.value = value;
        if (isParse) {
            parseValue();
        }
    }

    public boolean isPlaceHolder() {
        return this.isPlaceHolder;
    }

    public void setPlaceHolder(boolean isPlaceHolder) {
        this.isPlaceHolder = isPlaceHolder;
    }

    public boolean isOperatorBetween() {
        return OPERATOR_BETWEEN.equalsIgnoreCase(this.operator);
    }

    public boolean isOperatorIn() {
        return OPERATOR_IN.equalsIgnoreCase(this.operator);
    }

    public boolean isOperatorIs() {
        return OPERATOR_IS.equalsIgnoreCase(this.operator);
    }

    public boolean isOperatorLike() {
        return OPERATOR_LIKE.equalsIgnoreCase(this.operator);
    }

    public Condition checkValueNull() {
        if (null == this.value) {
            this.operator = OPERATOR_IS;
            this.value = "NULL";
        }
        return this;
    }

    public Object getSecondValue() {
        return this.secondValue;
    }

    public void setSecondValue(Object secondValue) {
        this.secondValue = secondValue;
    }

    public LogicalOperator getLinkOperator() {
        return this.linkOperator;
    }

    public void setLinkOperator(LogicalOperator linkOperator) {
        this.linkOperator = linkOperator;
    }

    public String toString() {
        return toString(null);
    }

    public String toString(List<Object> paramValues) {
        StringBuilder conditionStrBuilder = StrUtil.builder();
        checkValueNull();
        conditionStrBuilder.append(this.field).append(CharSequenceUtil.SPACE).append(this.operator);
        if (isOperatorBetween()) {
            buildValuePartForBETWEEN(conditionStrBuilder, paramValues);
        } else if (isOperatorIn()) {
            buildValuePartForIN(conditionStrBuilder, paramValues);
        } else if (isPlaceHolder() && false == isOperatorIs()) {
            conditionStrBuilder.append(" ?");
            if (null != paramValues) {
                paramValues.add(this.value);
            }
        } else {
            String valueStr = String.valueOf(this.value);
            conditionStrBuilder.append(CharSequenceUtil.SPACE).append(isOperatorLike() ? StrUtil.wrap(valueStr, "'") : valueStr);
        }
        return conditionStrBuilder.toString();
    }

    private void buildValuePartForBETWEEN(StringBuilder conditionStrBuilder, List<Object> paramValues) {
        if (isPlaceHolder()) {
            conditionStrBuilder.append(" ?");
            if (null != paramValues) {
                paramValues.add(this.value);
            }
        } else {
            conditionStrBuilder.append(' ').append(this.value);
        }
        conditionStrBuilder.append(CharSequenceUtil.SPACE).append(LogicalOperator.AND);
        if (isPlaceHolder()) {
            conditionStrBuilder.append(" ?");
            if (null != paramValues) {
                paramValues.add(this.secondValue);
                return;
            }
            return;
        }
        conditionStrBuilder.append(' ').append(this.secondValue);
    }

    private void buildValuePartForIN(StringBuilder conditionStrBuilder, List<Object> paramValues) {
        Collection<? extends Object> asList;
        conditionStrBuilder.append(" (");
        Object value = this.value;
        if (isPlaceHolder()) {
            if (value instanceof Collection) {
                asList = (Collection) value;
            } else if (value instanceof CharSequence) {
                asList = StrUtil.split((CharSequence) value, ',');
            } else {
                asList = Arrays.asList((Object[]) Convert.convert(Object[].class, value));
            }
            conditionStrBuilder.append(StrUtil.repeatAndJoin(CoreConstants.NA, asList.size(), ","));
            if (null != paramValues) {
                paramValues.addAll(asList);
            }
        } else {
            conditionStrBuilder.append(StrUtil.join(",", value));
        }
        conditionStrBuilder.append(')');
    }

    private void parseValue() {
        if (null == this.value) {
            this.operator = OPERATOR_IS;
            this.value = "NULL";
            return;
        }
        if ((this.value instanceof Collection) || ArrayUtil.isArray(this.value)) {
            this.operator = OPERATOR_IN;
            return;
        }
        if (false == (this.value instanceof String)) {
            return;
        }
        String valueStr = (String) this.value;
        if (StrUtil.isBlank(valueStr)) {
            return;
        }
        String valueStr2 = StrUtil.trim(valueStr);
        if (StrUtil.endWithIgnoreCase(valueStr2, "null")) {
            if (StrUtil.equalsIgnoreCase("= null", valueStr2) || StrUtil.equalsIgnoreCase("is null", valueStr2)) {
                this.operator = OPERATOR_IS;
                this.value = "NULL";
                this.isPlaceHolder = false;
                return;
            } else if (StrUtil.equalsIgnoreCase("!= null", valueStr2) || StrUtil.equalsIgnoreCase("is not null", valueStr2)) {
                this.operator = OPERATOR_IS_NOT;
                this.value = "NULL";
                this.isPlaceHolder = false;
                return;
            }
        }
        List<String> strs = StrUtil.split(valueStr2, ' ', 2);
        if (strs.size() < 2) {
            return;
        }
        String firstPart = strs.get(0).trim().toUpperCase();
        if (OPERATORS.contains(firstPart)) {
            this.operator = firstPart;
            String valuePart = strs.get(1);
            this.value = isOperatorIn() ? valuePart : tryToNumber(valuePart);
        } else if (OPERATOR_LIKE.equals(firstPart)) {
            this.operator = OPERATOR_LIKE;
            this.value = unwrapQuote(strs.get(1));
        } else if (OPERATOR_BETWEEN.equals(firstPart)) {
            List<String> betweenValueStrs = StrSplitter.splitTrimIgnoreCase(strs.get(1), LogicalOperator.AND.toString(), 2, true);
            if (betweenValueStrs.size() < 2) {
                return;
            }
            this.operator = OPERATOR_BETWEEN;
            this.value = unwrapQuote(betweenValueStrs.get(0));
            this.secondValue = unwrapQuote(betweenValueStrs.get(1));
        }
    }

    private static String unwrapQuote(String value) {
        if (null == value) {
            return null;
        }
        String value2 = value.trim();
        int from = 0;
        int to = value2.length();
        char startChar = value2.charAt(0);
        char endChar = value2.charAt(value2.length() - 1);
        if (startChar == endChar && ('\'' == startChar || '\"' == startChar)) {
            from = 1;
            to--;
        }
        if (from == 0) {
            return value2;
        }
        return value2.substring(from, to);
    }

    private static Object tryToNumber(String value) {
        String value2 = StrUtil.trim(value);
        if (false == NumberUtil.isNumber(value2)) {
            return value2;
        }
        try {
            return NumberUtil.parseNumber(value2);
        } catch (Exception e) {
            return value2;
        }
    }
}
