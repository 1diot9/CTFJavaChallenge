package org.h2.util.json;

import java.io.ByteArrayOutputStream;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueJson;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/json/JsonConstructorUtils.class */
public final class JsonConstructorUtils {
    public static final int JSON_ABSENT_ON_NULL = 1;
    public static final int JSON_WITH_UNIQUE_KEYS = 2;

    private JsonConstructorUtils() {
    }

    public static void jsonObjectAppend(ByteArrayOutputStream byteArrayOutputStream, String str, Value value) {
        if (byteArrayOutputStream.size() > 1) {
            byteArrayOutputStream.write(44);
        }
        JSONByteArrayTarget.encodeString(byteArrayOutputStream, str).write(58);
        byte[] bytesNoCopy = value.convertTo(TypeInfo.TYPE_JSON).getBytesNoCopy();
        byteArrayOutputStream.write(bytesNoCopy, 0, bytesNoCopy.length);
    }

    public static Value jsonObjectFinish(ByteArrayOutputStream byteArrayOutputStream, int i) {
        byteArrayOutputStream.write(125);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        if ((i & 2) != 0) {
            try {
                JSONBytesSource.parse(byteArray, new JSONValidationTargetWithUniqueKeys());
            } catch (RuntimeException e) {
                String str = (String) JSONBytesSource.parse(byteArray, new JSONStringTarget());
                throw DbException.getInvalidValueException("JSON WITH UNIQUE KEYS", str.length() < 128 ? byteArray : str.substring(0, 128) + "...");
            }
        }
        return ValueJson.getInternal(byteArray);
    }

    public static void jsonArrayAppend(ByteArrayOutputStream byteArrayOutputStream, Value value, int i) {
        if (value == ValueNull.INSTANCE) {
            if ((i & 1) != 0) {
                return;
            } else {
                value = ValueJson.NULL;
            }
        }
        if (byteArrayOutputStream.size() > 1) {
            byteArrayOutputStream.write(44);
        }
        byte[] bytesNoCopy = value.convertTo(TypeInfo.TYPE_JSON).getBytesNoCopy();
        byteArrayOutputStream.write(bytesNoCopy, 0, bytesNoCopy.length);
    }
}
