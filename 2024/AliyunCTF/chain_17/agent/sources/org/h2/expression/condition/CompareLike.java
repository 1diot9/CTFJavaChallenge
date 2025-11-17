package org.h2.expression.condition;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.SearchedCase;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.ValueExpression;
import org.h2.index.IndexCondition;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.CompareMode;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;
import org.h2.value.ValueVarcharIgnoreCase;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/CompareLike.class */
public final class CompareLike extends Condition {
    private static final int MATCH = 0;
    private static final int ONE = 1;
    private static final int ANY = 2;
    private final CompareMode compareMode;
    private final String defaultEscape;
    private final LikeType likeType;
    private Expression left;
    private final boolean not;
    private final boolean whenOperand;
    private Expression right;
    private Expression escape;
    private boolean isInit;
    private char[] patternChars;
    private String patternString;
    private int[] patternTypes;
    private int patternLength;
    private Pattern patternRegexp;
    private boolean ignoreCase;
    private boolean fastCompare;
    private boolean invalidPattern;
    private boolean shortcutToStartsWith;
    private boolean shortcutToEndsWith;
    private boolean shortcutToContains;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/CompareLike$LikeType.class */
    public enum LikeType {
        LIKE,
        ILIKE,
        REGEXP
    }

    @Override // org.h2.expression.condition.Condition, org.h2.expression.Expression, org.h2.value.Typed
    public /* bridge */ /* synthetic */ TypeInfo getType() {
        return super.getType();
    }

    public CompareLike(Database database, Expression expression, boolean z, boolean z2, Expression expression2, Expression expression3, LikeType likeType) {
        this(database.getCompareMode(), database.getSettings().defaultEscape, expression, z, z2, expression2, expression3, likeType);
    }

    public CompareLike(CompareMode compareMode, String str, Expression expression, boolean z, boolean z2, Expression expression2, Expression expression3, LikeType likeType) {
        this.compareMode = compareMode;
        this.defaultEscape = str;
        this.likeType = likeType;
        this.left = expression;
        this.not = z;
        this.whenOperand = z2;
        this.right = expression2;
        this.escape = expression3;
    }

    private static Character getEscapeChar(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        return Character.valueOf(str.charAt(0));
    }

    @Override // org.h2.expression.Expression
    public boolean needParentheses() {
        return true;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return getWhenSQL(this.left.getSQL(sb, i, 0), i);
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getWhenSQL(StringBuilder sb, int i) {
        if (this.not) {
            sb.append(" NOT");
        }
        switch (this.likeType) {
            case LIKE:
            case ILIKE:
                sb.append(this.likeType == LikeType.LIKE ? " LIKE " : " ILIKE ");
                this.right.getSQL(sb, i, 0);
                if (this.escape != null) {
                    this.escape.getSQL(sb.append(" ESCAPE "), i, 0);
                    break;
                }
                break;
            case REGEXP:
                sb.append(" REGEXP ");
                this.right.getSQL(sb, i, 0);
                break;
            default:
                throw DbException.getUnsupportedException(this.likeType.name());
        }
        return sb;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        this.right = this.right.optimize(sessionLocal);
        if (this.likeType == LikeType.ILIKE || this.left.getType().getValueType() == 4) {
            this.ignoreCase = true;
        }
        if (this.escape != null) {
            this.escape = this.escape.optimize(sessionLocal);
        }
        if (this.whenOperand) {
            return this;
        }
        if (this.left.isValueSet() && this.left.getValue(sessionLocal) == ValueNull.INSTANCE) {
            return TypedValueExpression.UNKNOWN;
        }
        if (this.right.isValueSet() && (this.escape == null || this.escape.isValueSet())) {
            if (this.left.isValueSet()) {
                return ValueExpression.getBoolean(getValue(sessionLocal));
            }
            Value value = this.right.getValue(sessionLocal);
            if (value == ValueNull.INSTANCE) {
                return TypedValueExpression.UNKNOWN;
            }
            Value value2 = this.escape == null ? null : this.escape.getValue(sessionLocal);
            if (value2 == ValueNull.INSTANCE) {
                return TypedValueExpression.UNKNOWN;
            }
            String string = value.getString();
            initPattern(string, getEscapeChar(value2));
            if (this.invalidPattern) {
                return TypedValueExpression.UNKNOWN;
            }
            if (this.likeType != LikeType.REGEXP && QuickTargetSourceCreator.PREFIX_THREAD_LOCAL.equals(string)) {
                Expression[] expressionArr = new Expression[3];
                expressionArr[0] = new NullPredicate(this.left, true, false);
                expressionArr[1] = ValueExpression.getBoolean(!this.not);
                expressionArr[2] = TypedValueExpression.UNKNOWN;
                return new SearchedCase(expressionArr).optimize(sessionLocal);
            }
            if (isFullMatch()) {
                return new Comparison(this.not ? 1 : 0, this.left, ValueExpression.get(this.ignoreCase ? ValueVarcharIgnoreCase.get(this.patternString) : ValueVarchar.get(this.patternString)), false).optimize(sessionLocal);
            }
            this.isInit = true;
        }
        return this;
    }

    private Character getEscapeChar(Value value) {
        Character valueOf;
        if (value == null) {
            return getEscapeChar(this.defaultEscape);
        }
        String string = value.getString();
        if (string == null) {
            valueOf = getEscapeChar(this.defaultEscape);
        } else if (string.length() == 0) {
            valueOf = null;
        } else {
            if (string.length() > 1) {
                throw DbException.get(ErrorCode.LIKE_ESCAPE_ERROR_1, string);
            }
            valueOf = Character.valueOf(string.charAt(0));
        }
        return valueOf;
    }

    @Override // org.h2.expression.Expression
    public void createIndexConditions(SessionLocal sessionLocal, TableFilter tableFilter) {
        if (this.not || this.whenOperand || this.likeType == LikeType.REGEXP || !(this.left instanceof ExpressionColumn)) {
            return;
        }
        ExpressionColumn expressionColumn = (ExpressionColumn) this.left;
        if (tableFilter != expressionColumn.getTableFilter()) {
            return;
        }
        if (!TypeInfo.haveSameOrdering(expressionColumn.getType(), this.ignoreCase ? TypeInfo.TYPE_VARCHAR_IGNORECASE : TypeInfo.TYPE_VARCHAR) || !this.right.isEverything(ExpressionVisitor.INDEPENDENT_VISITOR)) {
            return;
        }
        if (this.escape != null && !this.escape.isEverything(ExpressionVisitor.INDEPENDENT_VISITOR)) {
            return;
        }
        String string = this.right.getValue(sessionLocal).getString();
        if (!this.isInit) {
            Value value = this.escape == null ? null : this.escape.getValue(sessionLocal);
            if (value == ValueNull.INSTANCE) {
                throw DbException.getInternalError();
            }
            initPattern(string, getEscapeChar(value));
        }
        if (this.invalidPattern || this.patternLength <= 0 || this.patternTypes[0] != 0 || !DataType.isStringType(expressionColumn.getColumn().getType().getValueType())) {
            return;
        }
        int i = 0;
        StringBuilder sb = new StringBuilder();
        while (i < this.patternLength && this.patternTypes[i] == 0) {
            int i2 = i;
            i++;
            sb.append(this.patternChars[i2]);
        }
        String sb2 = sb.toString();
        if (i == this.patternLength) {
            tableFilter.addIndexCondition(IndexCondition.get(0, expressionColumn, ValueExpression.get(ValueVarchar.get(sb2))));
            return;
        }
        if (sb2.length() > 0) {
            tableFilter.addIndexCondition(IndexCondition.get(5, expressionColumn, ValueExpression.get(ValueVarchar.get(sb2))));
            int charAt = sb2.charAt(sb2.length() - 1);
            for (int i3 = 1; i3 < 2000; i3++) {
                String str = sb2.substring(0, sb2.length() - 1) + ((char) (charAt + i3));
                if (this.compareMode.compareString(sb2, str, this.ignoreCase) < 0) {
                    tableFilter.addIndexCondition(IndexCondition.get(2, expressionColumn, ValueExpression.get(ValueVarchar.get(str))));
                    return;
                }
            }
        }
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        return getValue(sessionLocal, this.left.getValue(sessionLocal));
    }

    @Override // org.h2.expression.Expression
    public boolean getWhenValue(SessionLocal sessionLocal, Value value) {
        if (!this.whenOperand) {
            return super.getWhenValue(sessionLocal, value);
        }
        return getValue(sessionLocal, value).isTrue();
    }

    private Value getValue(SessionLocal sessionLocal, Value value) {
        boolean compareAt;
        if (value == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        if (!this.isInit) {
            Value value2 = this.right.getValue(sessionLocal);
            if (value2 == ValueNull.INSTANCE) {
                return ValueNull.INSTANCE;
            }
            String string = value2.getString();
            Value value3 = this.escape == null ? null : this.escape.getValue(sessionLocal);
            if (value3 == ValueNull.INSTANCE) {
                return ValueNull.INSTANCE;
            }
            initPattern(string, getEscapeChar(value3));
        }
        if (this.invalidPattern) {
            return ValueNull.INSTANCE;
        }
        String string2 = value.getString();
        if (this.likeType == LikeType.REGEXP) {
            compareAt = this.patternRegexp.matcher(string2).find();
        } else if (this.shortcutToStartsWith) {
            compareAt = string2.regionMatches(this.ignoreCase, 0, this.patternString, 0, this.patternLength - 1);
        } else if (this.shortcutToEndsWith) {
            compareAt = string2.regionMatches(this.ignoreCase, (string2.length() - this.patternLength) + 1, this.patternString, 1, this.patternLength - 1);
        } else if (this.shortcutToContains) {
            String substring = this.patternString.substring(1, this.patternString.length() - 1);
            if (this.ignoreCase) {
                compareAt = containsIgnoreCase(string2, substring);
            } else {
                compareAt = string2.contains(substring);
            }
        } else {
            compareAt = compareAt(string2, 0, 0, string2.length(), this.patternChars, this.patternTypes);
        }
        return ValueBoolean.get(this.not ^ compareAt);
    }

    private static boolean containsIgnoreCase(String str, String str2) {
        int length = str2.length();
        if (length == 0) {
            return true;
        }
        char lowerCase = Character.toLowerCase(str2.charAt(0));
        char upperCase = Character.toUpperCase(str2.charAt(0));
        for (int length2 = str.length() - length; length2 >= 0; length2--) {
            char charAt = str.charAt(length2);
            if ((charAt == lowerCase || charAt == upperCase) && str.regionMatches(true, length2, str2, 0, length)) {
                return true;
            }
        }
        return false;
    }

    private boolean compareAt(String str, int i, int i2, int i3, char[] cArr, int[] iArr) {
        while (i < this.patternLength) {
            switch (iArr[i]) {
                case 0:
                    if (i2 >= i3) {
                        return false;
                    }
                    int i4 = i2;
                    i2++;
                    if (!compare(cArr, str, i, i4)) {
                        return false;
                    }
                    break;
                case 1:
                    int i5 = i2;
                    i2++;
                    if (i5 < i3) {
                        break;
                    } else {
                        return false;
                    }
                case 2:
                    int i6 = i + 1;
                    if (i6 >= this.patternLength) {
                        return true;
                    }
                    while (i2 < i3) {
                        if (compare(cArr, str, i6, i2) && compareAt(str, i6, i2, i3, cArr, iArr)) {
                            return true;
                        }
                        i2++;
                    }
                    return false;
                default:
                    throw DbException.getInternalError(Integer.toString(iArr[i]));
            }
            i++;
        }
        return i2 == i3;
    }

    private boolean compare(char[] cArr, String str, int i, int i2) {
        return cArr[i] == str.charAt(i2) || (!this.fastCompare && this.compareMode.equalsChars(this.patternString, i, str, i2, this.ignoreCase));
    }

    @Override // org.h2.expression.Expression
    public boolean isWhenConditionOperand() {
        return this.whenOperand;
    }

    public boolean test(String str, String str2, char c) {
        initPattern(str, Character.valueOf(c));
        return test(str2);
    }

    public boolean test(String str) {
        if (this.invalidPattern) {
            return false;
        }
        return compareAt(str, 0, 0, str.length(), this.patternChars, this.patternTypes);
    }

    public void initPattern(String str, Character ch2) {
        int i;
        if (this.compareMode.getName().equals(CompareMode.OFF) && !this.ignoreCase) {
            this.fastCompare = true;
        }
        if (this.likeType == LikeType.REGEXP) {
            this.patternString = str;
            try {
                if (this.ignoreCase) {
                    this.patternRegexp = Pattern.compile(str, 2);
                } else {
                    this.patternRegexp = Pattern.compile(str);
                }
                return;
            } catch (PatternSyntaxException e) {
                throw DbException.get(ErrorCode.LIKE_ESCAPE_ERROR_1, e, str);
            }
        }
        this.patternLength = 0;
        if (str == null) {
            this.patternTypes = null;
            this.patternChars = null;
            return;
        }
        int length = str.length();
        this.patternChars = new char[length];
        this.patternTypes = new int[length];
        boolean z = false;
        int i2 = 0;
        while (i2 < length) {
            char charAt = str.charAt(i2);
            if (ch2 != null && ch2.charValue() == charAt) {
                if (i2 >= length - 1) {
                    this.invalidPattern = true;
                    return;
                }
                i2++;
                charAt = str.charAt(i2);
                i = 0;
                z = false;
            } else if (charAt == '%') {
                if (z) {
                    i2++;
                } else {
                    i = 2;
                    z = true;
                }
            } else if (charAt == '_') {
                i = 1;
            } else {
                i = 0;
                z = false;
            }
            this.patternTypes[this.patternLength] = i;
            char[] cArr = this.patternChars;
            int i3 = this.patternLength;
            this.patternLength = i3 + 1;
            cArr[i3] = charAt;
            i2++;
        }
        for (int i4 = 0; i4 < this.patternLength - 1; i4++) {
            if (this.patternTypes[i4] == 2 && this.patternTypes[i4 + 1] == 1) {
                this.patternTypes[i4] = 1;
                this.patternTypes[i4 + 1] = 2;
            }
        }
        this.patternString = new String(this.patternChars, 0, this.patternLength);
        this.shortcutToStartsWith = false;
        this.shortcutToEndsWith = false;
        this.shortcutToContains = false;
        if (this.compareMode.getName().equals(CompareMode.OFF) && this.patternLength > 1) {
            int i5 = 0;
            while (i5 < this.patternLength && this.patternTypes[i5] == 0) {
                i5++;
            }
            if (i5 == this.patternLength - 1 && this.patternTypes[this.patternLength - 1] == 2) {
                this.shortcutToStartsWith = true;
                return;
            }
        }
        if (this.compareMode.getName().equals(CompareMode.OFF) && this.patternLength > 1 && this.patternTypes[0] == 2) {
            int i6 = 1;
            while (i6 < this.patternLength && this.patternTypes[i6] == 0) {
                i6++;
            }
            if (i6 == this.patternLength) {
                this.shortcutToEndsWith = true;
                return;
            }
        }
        if (this.compareMode.getName().equals(CompareMode.OFF) && this.patternLength > 2 && this.patternTypes[0] == 2) {
            int i7 = 1;
            while (i7 < this.patternLength && this.patternTypes[i7] == 0) {
                i7++;
            }
            if (i7 == this.patternLength - 1 && this.patternTypes[this.patternLength - 1] == 2) {
                this.shortcutToContains = true;
            }
        }
    }

    private boolean isFullMatch() {
        if (this.patternTypes == null) {
            return false;
        }
        for (int i : this.patternTypes) {
            if (i != 0) {
                return false;
            }
        }
        return true;
    }

    @Override // org.h2.expression.Expression
    public Expression getNotIfPossible(SessionLocal sessionLocal) {
        if (this.whenOperand) {
            return null;
        }
        return new CompareLike(this.compareMode, this.defaultEscape, this.left, !this.not, false, this.right, this.escape, this.likeType);
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        this.left.mapColumns(columnResolver, i, i2);
        this.right.mapColumns(columnResolver, i, i2);
        if (this.escape != null) {
            this.escape.mapColumns(columnResolver, i, i2);
        }
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        this.left.setEvaluatable(tableFilter, z);
        this.right.setEvaluatable(tableFilter, z);
        if (this.escape != null) {
            this.escape.setEvaluatable(tableFilter, z);
        }
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        this.left.updateAggregate(sessionLocal, i);
        this.right.updateAggregate(sessionLocal, i);
        if (this.escape != null) {
            this.escape.updateAggregate(sessionLocal, i);
        }
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return this.left.isEverything(expressionVisitor) && this.right.isEverything(expressionVisitor) && (this.escape == null || this.escape.isEverything(expressionVisitor));
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return this.left.getCost() + this.right.getCost() + 3;
    }

    @Override // org.h2.expression.Expression
    public int getSubexpressionCount() {
        return this.escape == null ? 2 : 3;
    }

    @Override // org.h2.expression.Expression
    public Expression getSubexpression(int i) {
        switch (i) {
            case 0:
                return this.left;
            case 1:
                return this.right;
            case 2:
                if (this.escape != null) {
                    return this.escape;
                }
                break;
        }
        throw new IndexOutOfBoundsException();
    }
}
