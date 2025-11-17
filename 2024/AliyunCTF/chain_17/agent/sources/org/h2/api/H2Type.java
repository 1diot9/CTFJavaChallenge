package org.h2.api;

import com.fasterxml.jackson.core.JsonFactory;
import java.sql.SQLType;
import org.h2.value.ExtTypeInfoRow;
import org.h2.value.TypeInfo;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/api/H2Type.class */
public final class H2Type implements SQLType {
    public static final H2Type CHAR = new H2Type(TypeInfo.getTypeInfo(1), "CHARACTER");
    public static final H2Type VARCHAR = new H2Type(TypeInfo.TYPE_VARCHAR, "CHARACTER VARYING");
    public static final H2Type CLOB = new H2Type(TypeInfo.TYPE_CLOB, "CHARACTER LARGE OBJECT");
    public static final H2Type VARCHAR_IGNORECASE = new H2Type(TypeInfo.TYPE_VARCHAR_IGNORECASE, "VARCHAR_IGNORECASE");
    public static final H2Type BINARY = new H2Type(TypeInfo.getTypeInfo(5), "BINARY");
    public static final H2Type VARBINARY = new H2Type(TypeInfo.TYPE_VARBINARY, "BINARY VARYING");
    public static final H2Type BLOB = new H2Type(TypeInfo.TYPE_BLOB, "BINARY LARGE OBJECT");
    public static final H2Type BOOLEAN = new H2Type(TypeInfo.TYPE_BOOLEAN, "BOOLEAN");
    public static final H2Type TINYINT = new H2Type(TypeInfo.TYPE_TINYINT, "TINYINT");
    public static final H2Type SMALLINT = new H2Type(TypeInfo.TYPE_SMALLINT, "SMALLINT");
    public static final H2Type INTEGER = new H2Type(TypeInfo.TYPE_INTEGER, "INTEGER");
    public static final H2Type BIGINT = new H2Type(TypeInfo.TYPE_BIGINT, "BIGINT");
    public static final H2Type NUMERIC = new H2Type(TypeInfo.TYPE_NUMERIC_FLOATING_POINT, "NUMERIC");
    public static final H2Type REAL = new H2Type(TypeInfo.TYPE_REAL, "REAL");
    public static final H2Type DOUBLE_PRECISION = new H2Type(TypeInfo.TYPE_DOUBLE, "DOUBLE PRECISION");
    public static final H2Type DECFLOAT = new H2Type(TypeInfo.TYPE_DECFLOAT, "DECFLOAT");
    public static final H2Type DATE = new H2Type(TypeInfo.TYPE_DATE, "DATE");
    public static final H2Type TIME = new H2Type(TypeInfo.TYPE_TIME, "TIME");
    public static final H2Type TIME_WITH_TIME_ZONE = new H2Type(TypeInfo.TYPE_TIME_TZ, "TIME WITH TIME ZONE");
    public static final H2Type TIMESTAMP = new H2Type(TypeInfo.TYPE_TIMESTAMP, "TIMESTAMP");
    public static final H2Type TIMESTAMP_WITH_TIME_ZONE = new H2Type(TypeInfo.TYPE_TIMESTAMP_TZ, "TIMESTAMP WITH TIME ZONE");
    public static final H2Type INTERVAL_YEAR = new H2Type(TypeInfo.getTypeInfo(22), "INTERVAL_YEAR");
    public static final H2Type INTERVAL_MONTH = new H2Type(TypeInfo.getTypeInfo(23), "INTERVAL_MONTH");
    public static final H2Type INTERVAL_DAY = new H2Type(TypeInfo.TYPE_INTERVAL_DAY, "INTERVAL_DAY");
    public static final H2Type INTERVAL_HOUR = new H2Type(TypeInfo.getTypeInfo(25), "INTERVAL_HOUR");
    public static final H2Type INTERVAL_MINUTE = new H2Type(TypeInfo.getTypeInfo(26), "INTERVAL_MINUTE");
    public static final H2Type INTERVAL_SECOND = new H2Type(TypeInfo.getTypeInfo(27), "INTERVAL_SECOND");
    public static final H2Type INTERVAL_YEAR_TO_MONTH = new H2Type(TypeInfo.TYPE_INTERVAL_YEAR_TO_MONTH, "INTERVAL_YEAR_TO_MONTH");
    public static final H2Type INTERVAL_DAY_TO_HOUR = new H2Type(TypeInfo.getTypeInfo(29), "INTERVAL_DAY_TO_HOUR");
    public static final H2Type INTERVAL_DAY_TO_MINUTE = new H2Type(TypeInfo.getTypeInfo(30), "INTERVAL_DAY_TO_MINUTE");
    public static final H2Type INTERVAL_DAY_TO_SECOND = new H2Type(TypeInfo.TYPE_INTERVAL_DAY_TO_SECOND, "INTERVAL_DAY_TO_SECOND");
    public static final H2Type INTERVAL_HOUR_TO_MINUTE = new H2Type(TypeInfo.getTypeInfo(32), "INTERVAL_HOUR_TO_MINUTE");
    public static final H2Type INTERVAL_HOUR_TO_SECOND = new H2Type(TypeInfo.TYPE_INTERVAL_HOUR_TO_SECOND, "INTERVAL_HOUR_TO_SECOND");
    public static final H2Type INTERVAL_MINUTE_TO_SECOND = new H2Type(TypeInfo.getTypeInfo(34), "INTERVAL_MINUTE_TO_SECOND");
    public static final H2Type JAVA_OBJECT = new H2Type(TypeInfo.TYPE_JAVA_OBJECT, "JAVA_OBJECT");
    public static final H2Type ENUM = new H2Type(TypeInfo.TYPE_ENUM_UNDEFINED, "ENUM");
    public static final H2Type GEOMETRY = new H2Type(TypeInfo.TYPE_GEOMETRY, "GEOMETRY");
    public static final H2Type JSON = new H2Type(TypeInfo.TYPE_JSON, JsonFactory.FORMAT_NAME_JSON);
    public static final H2Type UUID = new H2Type(TypeInfo.TYPE_UUID, "UUID");
    private TypeInfo typeInfo;
    private String field;

    public static H2Type array(H2Type h2Type) {
        return new H2Type(TypeInfo.getTypeInfo(40, -1L, -1, h2Type.typeInfo), "array(" + h2Type.field + ')');
    }

    public static H2Type row(H2Type... h2TypeArr) {
        int length = h2TypeArr.length;
        TypeInfo[] typeInfoArr = new TypeInfo[length];
        StringBuilder sb = new StringBuilder("row(");
        for (int i = 0; i < length; i++) {
            H2Type h2Type = h2TypeArr[i];
            typeInfoArr[i] = h2Type.typeInfo;
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(h2Type.field);
        }
        return new H2Type(TypeInfo.getTypeInfo(41, -1L, -1, new ExtTypeInfoRow(typeInfoArr)), sb.append(')').toString());
    }

    private H2Type(TypeInfo typeInfo, String str) {
        this.typeInfo = typeInfo;
        this.field = "H2Type." + str;
    }

    public String getName() {
        return this.typeInfo.toString();
    }

    public String getVendor() {
        return "com.h2database";
    }

    public Integer getVendorTypeNumber() {
        return Integer.valueOf(this.typeInfo.getValueType());
    }

    public String toString() {
        return this.field;
    }
}
