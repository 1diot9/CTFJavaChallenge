package org.h2.value;

import java.util.Arrays;
import java.util.Locale;
import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ExtTypeInfoEnum.class */
public final class ExtTypeInfoEnum extends ExtTypeInfo {
    private final String[] enumerators;
    private final String[] cleaned;
    private TypeInfo type;

    public static ExtTypeInfoEnum getEnumeratorsForBinaryOperation(Value value, Value value2) {
        if (value.getValueType() == 36) {
            return ((ValueEnum) value).getEnumerators();
        }
        if (value2.getValueType() == 36) {
            return ((ValueEnum) value2).getEnumerators();
        }
        throw DbException.get(ErrorCode.UNKNOWN_DATA_TYPE_1, "type1=" + value.getValueType() + ", type2=" + value2.getValueType());
    }

    private static String sanitize(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (length > 1000000000) {
            throw DbException.getValueTooLongException("ENUM", str, length);
        }
        return str.trim().toUpperCase(Locale.ENGLISH);
    }

    private static StringBuilder toSQL(StringBuilder sb, String[] strArr) {
        sb.append('(');
        for (int i = 0; i < strArr.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append('\'');
            String str = strArr[i];
            int length = str.length();
            for (int i2 = 0; i2 < length; i2++) {
                char charAt = str.charAt(i2);
                if (charAt == '\'') {
                    sb.append('\'');
                }
                sb.append(charAt);
            }
            sb.append('\'');
        }
        return sb.append(')');
    }

    public ExtTypeInfoEnum(String[] strArr) {
        int length;
        if (strArr == null || (length = strArr.length) == 0) {
            throw DbException.get(ErrorCode.ENUM_EMPTY);
        }
        if (length > 65536) {
            throw DbException.getValueTooLongException("ENUM", "(" + length + " elements)", length);
        }
        String[] strArr2 = new String[length];
        for (int i = 0; i < length; i++) {
            String sanitize = sanitize(strArr[i]);
            if (sanitize == null || sanitize.isEmpty()) {
                throw DbException.get(ErrorCode.ENUM_EMPTY);
            }
            for (int i2 = 0; i2 < i; i2++) {
                if (sanitize.equals(strArr2[i2])) {
                    throw DbException.get(ErrorCode.ENUM_DUPLICATE, toSQL(new StringBuilder(), strArr).toString());
                }
            }
            strArr2[i] = sanitize;
        }
        this.enumerators = strArr;
        this.cleaned = Arrays.equals(strArr2, strArr) ? strArr : strArr2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TypeInfo getType() {
        TypeInfo typeInfo = this.type;
        if (typeInfo == null) {
            int i = 0;
            for (String str : this.enumerators) {
                int length = str.length();
                if (length > i) {
                    i = length;
                }
            }
            TypeInfo typeInfo2 = new TypeInfo(36, i, 0, this);
            typeInfo = typeInfo2;
            this.type = typeInfo2;
        }
        return typeInfo;
    }

    public int getCount() {
        return this.enumerators.length;
    }

    public String getEnumerator(int i) {
        return this.enumerators[i];
    }

    public ValueEnum getValue(int i, CastDataProvider castDataProvider) {
        String str;
        if (castDataProvider == null || !castDataProvider.zeroBasedEnums()) {
            if (i < 1 || i > this.enumerators.length) {
                throw DbException.get(ErrorCode.ENUM_VALUE_NOT_PERMITTED, getTraceSQL(), Integer.toString(i));
            }
            str = this.enumerators[i - 1];
        } else {
            if (i < 0 || i >= this.enumerators.length) {
                throw DbException.get(ErrorCode.ENUM_VALUE_NOT_PERMITTED, getTraceSQL(), Integer.toString(i));
            }
            str = this.enumerators[i];
        }
        return new ValueEnum(this, str, i);
    }

    public ValueEnum getValue(String str, CastDataProvider castDataProvider) {
        ValueEnum valueOrNull = getValueOrNull(str, castDataProvider);
        if (valueOrNull == null) {
            throw DbException.get(ErrorCode.ENUM_VALUE_NOT_PERMITTED, toString(), str);
        }
        return valueOrNull;
    }

    private ValueEnum getValueOrNull(String str, CastDataProvider castDataProvider) {
        String sanitize = sanitize(str);
        if (sanitize != null) {
            int i = 0;
            int i2 = (castDataProvider == null || !castDataProvider.zeroBasedEnums()) ? 1 : 0;
            while (i < this.cleaned.length) {
                if (!sanitize.equals(this.cleaned[i])) {
                    i++;
                    i2++;
                } else {
                    return new ValueEnum(this, this.enumerators[i], i2);
                }
            }
            return null;
        }
        return null;
    }

    public int hashCode() {
        return Arrays.hashCode(this.enumerators) + 203117;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != ExtTypeInfoEnum.class) {
            return false;
        }
        return Arrays.equals(this.enumerators, ((ExtTypeInfoEnum) obj).enumerators);
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return toSQL(sb, this.enumerators);
    }
}
