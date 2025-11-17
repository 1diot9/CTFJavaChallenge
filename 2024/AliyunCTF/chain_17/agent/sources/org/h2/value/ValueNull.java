package org.h2.value;

import ch.qos.logback.core.joran.JoranConstants;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueNull.class */
public final class ValueNull extends Value {
    public static final ValueNull INSTANCE = new ValueNull();
    static final int PRECISION = 1;
    static final int DISPLAY_SIZE = 4;

    private ValueNull() {
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return sb.append(JoranConstants.NULL);
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_NULL;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 0;
    }

    @Override // org.h2.value.Value
    public int getMemory() {
        return 0;
    }

    @Override // org.h2.value.Value
    public String getString() {
        return null;
    }

    @Override // org.h2.value.Value
    public Reader getReader() {
        return null;
    }

    @Override // org.h2.value.Value
    public Reader getReader(long j, long j2) {
        return null;
    }

    @Override // org.h2.value.Value
    public byte[] getBytes() {
        return null;
    }

    @Override // org.h2.value.Value
    public InputStream getInputStream() {
        return null;
    }

    @Override // org.h2.value.Value
    public InputStream getInputStream(long j, long j2) {
        return null;
    }

    @Override // org.h2.value.Value
    public boolean getBoolean() {
        throw DbException.getInternalError();
    }

    @Override // org.h2.value.Value
    public byte getByte() {
        throw DbException.getInternalError();
    }

    @Override // org.h2.value.Value
    public short getShort() {
        throw DbException.getInternalError();
    }

    @Override // org.h2.value.Value
    public int getInt() {
        throw DbException.getInternalError();
    }

    @Override // org.h2.value.Value
    public long getLong() {
        throw DbException.getInternalError();
    }

    @Override // org.h2.value.Value
    public BigDecimal getBigDecimal() {
        return null;
    }

    @Override // org.h2.value.Value
    public float getFloat() {
        throw DbException.getInternalError();
    }

    @Override // org.h2.value.Value
    public double getDouble() {
        throw DbException.getInternalError();
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        throw DbException.getInternalError("compare null");
    }

    @Override // org.h2.value.Value
    public boolean containsNull() {
        return true;
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return 0;
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        return obj == this;
    }
}
