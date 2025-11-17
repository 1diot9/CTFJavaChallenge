package org.h2.value;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueStringBase.class */
public abstract class ValueStringBase extends Value {
    String value;
    private TypeInfo type;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValueStringBase(String str) {
        int length = str.length();
        if (length > 1000000000) {
            throw DbException.getValueTooLongException(getTypeName(getValueType()), str, length);
        }
        this.value = str;
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public final TypeInfo getType() {
        TypeInfo typeInfo = this.type;
        if (typeInfo == null) {
            TypeInfo typeInfo2 = new TypeInfo(getValueType(), this.value.length(), 0, null);
            typeInfo = typeInfo2;
            this.type = typeInfo2;
        }
        return typeInfo;
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        return compareMode.compareString(this.value, ((ValueStringBase) value).value, false);
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return getClass().hashCode() ^ this.value.hashCode();
    }

    @Override // org.h2.value.Value
    public final String getString() {
        return this.value;
    }

    @Override // org.h2.value.Value
    public final byte[] getBytes() {
        return this.value.getBytes(StandardCharsets.UTF_8);
    }

    @Override // org.h2.value.Value
    public final boolean getBoolean() {
        String trim = this.value.trim();
        if (trim.equalsIgnoreCase("true") || trim.equalsIgnoreCase("t") || trim.equalsIgnoreCase(CustomBooleanEditor.VALUE_YES) || trim.equalsIgnoreCase("y")) {
            return true;
        }
        if (trim.equalsIgnoreCase("false") || trim.equalsIgnoreCase("f") || trim.equalsIgnoreCase("no") || trim.equalsIgnoreCase("n")) {
            return false;
        }
        try {
            return new BigDecimal(trim).signum() != 0;
        } catch (NumberFormatException e) {
            throw getDataConversionError(8);
        }
    }

    @Override // org.h2.value.Value
    public final byte getByte() {
        try {
            return Byte.parseByte(this.value.trim());
        } catch (NumberFormatException e) {
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, e, this.value);
        }
    }

    @Override // org.h2.value.Value
    public final short getShort() {
        try {
            return Short.parseShort(this.value.trim());
        } catch (NumberFormatException e) {
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, e, this.value);
        }
    }

    @Override // org.h2.value.Value
    public final int getInt() {
        try {
            return Integer.parseInt(this.value.trim());
        } catch (NumberFormatException e) {
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, e, this.value);
        }
    }

    @Override // org.h2.value.Value
    public final long getLong() {
        try {
            return Long.parseLong(this.value.trim());
        } catch (NumberFormatException e) {
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, e, this.value);
        }
    }

    @Override // org.h2.value.Value
    public final BigDecimal getBigDecimal() {
        try {
            return new BigDecimal(this.value.trim());
        } catch (NumberFormatException e) {
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, e, this.value);
        }
    }

    @Override // org.h2.value.Value
    public final float getFloat() {
        try {
            return Float.parseFloat(this.value.trim());
        } catch (NumberFormatException e) {
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, e, this.value);
        }
    }

    @Override // org.h2.value.Value
    public final double getDouble() {
        try {
            return Double.parseDouble(this.value.trim());
        } catch (NumberFormatException e) {
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, e, this.value);
        }
    }

    @Override // org.h2.value.Value
    public final int getMemory() {
        return (this.value.length() * 2) + 94;
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        return obj != null && getClass() == obj.getClass() && this.value.equals(((ValueStringBase) obj).value);
    }
}
