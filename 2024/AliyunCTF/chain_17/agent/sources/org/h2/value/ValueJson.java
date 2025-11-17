package org.h2.value;

import java.io.ByteArrayOutputStream;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.util.StringUtils;
import org.h2.util.json.JSONBoolean;
import org.h2.util.json.JSONByteArrayTarget;
import org.h2.util.json.JSONBytesSource;
import org.h2.util.json.JSONItemType;
import org.h2.util.json.JSONNull;
import org.h2.util.json.JSONNumber;
import org.h2.util.json.JSONStringSource;
import org.h2.util.json.JSONStringTarget;
import org.h2.util.json.JSONValue;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueJson.class */
public final class ValueJson extends ValueBytesBase {
    private static final byte[] NULL_BYTES = "null".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] TRUE_BYTES = "true".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] FALSE_BYTES = "false".getBytes(StandardCharsets.ISO_8859_1);
    public static final ValueJson NULL = new ValueJson(NULL_BYTES);
    public static final ValueJson TRUE = new ValueJson(TRUE_BYTES);
    public static final ValueJson FALSE = new ValueJson(FALSE_BYTES);
    public static final ValueJson ZERO = new ValueJson(new byte[]{48});
    private volatile SoftReference<JSONValue> decompositionRef;

    private ValueJson(byte[] bArr) {
        super(bArr);
    }

    @Override // org.h2.value.ValueBytesBase, org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return sb.append("JSON '").append((String) JSONBytesSource.parse(this.value, new JSONStringTarget(true))).append('\'');
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_JSON;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 38;
    }

    @Override // org.h2.value.Value
    public String getString() {
        return new String(this.value, StandardCharsets.UTF_8);
    }

    public JSONItemType getItemType() {
        switch (this.value[0]) {
            case 91:
                return JSONItemType.ARRAY;
            case 123:
                return JSONItemType.OBJECT;
            default:
                return JSONItemType.SCALAR;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:4:0x0012, code lost:            if (r0 == null) goto L6;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.h2.util.json.JSONValue getDecomposition() {
        /*
            r5 = this;
            r0 = r5
            java.lang.ref.SoftReference<org.h2.util.json.JSONValue> r0 = r0.decompositionRef
            r6 = r0
            r0 = r6
            if (r0 == 0) goto L15
            r0 = r6
            java.lang.Object r0 = r0.get()
            org.h2.util.json.JSONValue r0 = (org.h2.util.json.JSONValue) r0
            r1 = r0
            r7 = r1
            if (r0 != 0) goto L33
        L15:
            r0 = r5
            byte[] r0 = r0.value
            org.h2.util.json.JSONValueTarget r1 = new org.h2.util.json.JSONValueTarget
            r2 = r1
            r2.<init>()
            java.lang.Object r0 = org.h2.util.json.JSONBytesSource.parse(r0, r1)
            org.h2.util.json.JSONValue r0 = (org.h2.util.json.JSONValue) r0
            r7 = r0
            r0 = r5
            java.lang.ref.SoftReference r1 = new java.lang.ref.SoftReference
            r2 = r1
            r3 = r7
            r2.<init>(r3)
            r0.decompositionRef = r1
        L33:
            r0 = r7
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.value.ValueJson.getDecomposition():org.h2.util.json.JSONValue");
    }

    public static ValueJson fromJson(String str) {
        try {
            return getInternal(JSONStringSource.normalize(str));
        } catch (RuntimeException e) {
            if (str.length() > 80) {
                str = new StringBuilder(83).append((CharSequence) str, 0, 80).append("...").toString();
            }
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, str);
        }
    }

    public static ValueJson fromJson(byte[] bArr) {
        try {
            bArr = JSONBytesSource.normalize(bArr);
            return getInternal(bArr);
        } catch (RuntimeException e) {
            StringBuilder append = new StringBuilder().append("X'");
            if (bArr.length > 40) {
                StringUtils.convertBytesToHex(append, bArr, 40).append("...");
            } else {
                StringUtils.convertBytesToHex(append, bArr);
            }
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, append.append('\'').toString());
        }
    }

    public static ValueJson fromJson(JSONValue jSONValue) {
        if (jSONValue instanceof JSONNull) {
            return NULL;
        }
        if (jSONValue instanceof JSONBoolean) {
            return ((JSONBoolean) jSONValue).getBoolean() ? TRUE : FALSE;
        }
        if ((jSONValue instanceof JSONNumber) && ((JSONNumber) jSONValue).getBigDecimal().equals(BigDecimal.ZERO)) {
            return ZERO;
        }
        JSONByteArrayTarget jSONByteArrayTarget = new JSONByteArrayTarget();
        jSONValue.addTo(jSONByteArrayTarget);
        ValueJson valueJson = new ValueJson(jSONByteArrayTarget.getResult());
        valueJson.decompositionRef = new SoftReference<>(jSONValue);
        return valueJson;
    }

    public static ValueJson get(boolean z) {
        return z ? TRUE : FALSE;
    }

    public static ValueJson get(int i) {
        return i != 0 ? getNumber(Integer.toString(i)) : ZERO;
    }

    public static ValueJson get(long j) {
        return j != 0 ? getNumber(Long.toString(j)) : ZERO;
    }

    public static ValueJson get(BigDecimal bigDecimal) {
        if (bigDecimal.signum() == 0 && bigDecimal.scale() == 0) {
            return ZERO;
        }
        String bigDecimal2 = bigDecimal.toString();
        int indexOf = bigDecimal2.indexOf(69);
        if (indexOf >= 0) {
            int i = indexOf + 1;
            if (bigDecimal2.charAt(i) == '+') {
                int length = bigDecimal2.length();
                bigDecimal2 = new StringBuilder(length - 1).append((CharSequence) bigDecimal2, 0, i).append((CharSequence) bigDecimal2, i + 1, length).toString();
            }
        }
        return getNumber(bigDecimal2);
    }

    public static ValueJson get(String str) {
        return new ValueJson(JSONByteArrayTarget.encodeString(new ByteArrayOutputStream(str.length() + 2), str).toByteArray());
    }

    public static ValueJson getInternal(byte[] bArr) {
        switch (bArr.length) {
            case 1:
                if (bArr[0] == 48) {
                    return ZERO;
                }
                break;
            case 4:
                if (Arrays.equals(TRUE_BYTES, bArr)) {
                    return TRUE;
                }
                if (Arrays.equals(NULL_BYTES, bArr)) {
                    return NULL;
                }
                break;
            case 5:
                if (Arrays.equals(FALSE_BYTES, bArr)) {
                    return FALSE;
                }
                break;
        }
        return new ValueJson(bArr);
    }

    private static ValueJson getNumber(String str) {
        return new ValueJson(str.getBytes(StandardCharsets.ISO_8859_1));
    }

    @Override // org.h2.value.ValueBytesBase, org.h2.value.Value
    public int getMemory() {
        return this.value.length + 96;
    }
}
