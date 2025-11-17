package com.alibaba.com.caucho.hessian.io;

import cn.hutool.core.text.CharSequenceUtil;
import java.io.IOException;
import java.util.Date;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/BasicSerializer.class */
public class BasicSerializer extends AbstractSerializer {
    public static final int NULL = 0;
    public static final int BOOLEAN = 1;
    public static final int BYTE = 2;
    public static final int SHORT = 3;
    public static final int INTEGER = 4;
    public static final int LONG = 5;
    public static final int FLOAT = 6;
    public static final int DOUBLE = 7;
    public static final int CHARACTER = 8;
    public static final int CHARACTER_OBJECT = 9;
    public static final int STRING = 10;
    public static final int DATE = 11;
    public static final int NUMBER = 12;
    public static final int OBJECT = 13;
    public static final int BOOLEAN_ARRAY = 14;
    public static final int BYTE_ARRAY = 15;
    public static final int SHORT_ARRAY = 16;
    public static final int INTEGER_ARRAY = 17;
    public static final int LONG_ARRAY = 18;
    public static final int FLOAT_ARRAY = 19;
    public static final int DOUBLE_ARRAY = 20;
    public static final int CHARACTER_ARRAY = 21;
    public static final int STRING_ARRAY = 22;
    public static final int OBJECT_ARRAY = 23;
    private int code;

    public BasicSerializer(int code) {
        this.code = code;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializer, com.alibaba.com.caucho.hessian.io.Serializer
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        switch (this.code) {
            case 0:
                out.writeNull();
                return;
            case 1:
                out.writeBoolean(((Boolean) obj).booleanValue());
                return;
            case 2:
            case 3:
            case 4:
                out.writeInt(((Number) obj).intValue());
                return;
            case 5:
                out.writeLong(((Number) obj).longValue());
                return;
            case 6:
                out.writeDouble(Double.parseDouble(String.valueOf(((Number) obj).floatValue())));
                return;
            case 7:
                out.writeDouble(((Number) obj).doubleValue());
                return;
            case 8:
            case 9:
                out.writeString(String.valueOf(obj));
                return;
            case 10:
                out.writeString((String) obj);
                return;
            case 11:
                out.writeUTCDate(((Date) obj).getTime());
                return;
            case 12:
            case 13:
            default:
                throw new RuntimeException(this.code + CharSequenceUtil.SPACE + String.valueOf(obj.getClass()));
            case 14:
                if (out.addRef(obj)) {
                    return;
                }
                boolean[] data = (boolean[]) obj;
                boolean hasEnd = out.writeListBegin(data.length, "[boolean");
                for (boolean z : data) {
                    out.writeBoolean(z);
                }
                if (hasEnd) {
                    out.writeListEnd();
                    return;
                }
                return;
            case 15:
                byte[] data2 = (byte[]) obj;
                out.writeBytes(data2, 0, data2.length);
                return;
            case 16:
                if (out.addRef(obj)) {
                    return;
                }
                short[] data3 = (short[]) obj;
                boolean hasEnd2 = out.writeListBegin(data3.length, "[short");
                for (short s : data3) {
                    out.writeInt(s);
                }
                if (hasEnd2) {
                    out.writeListEnd();
                    return;
                }
                return;
            case 17:
                if (out.addRef(obj)) {
                    return;
                }
                int[] data4 = (int[]) obj;
                boolean hasEnd3 = out.writeListBegin(data4.length, "[int");
                for (int i : data4) {
                    out.writeInt(i);
                }
                if (hasEnd3) {
                    out.writeListEnd();
                    return;
                }
                return;
            case 18:
                if (out.addRef(obj)) {
                    return;
                }
                long[] data5 = (long[]) obj;
                boolean hasEnd4 = out.writeListBegin(data5.length, "[long");
                for (long j : data5) {
                    out.writeLong(j);
                }
                if (hasEnd4) {
                    out.writeListEnd();
                    return;
                }
                return;
            case 19:
                if (out.addRef(obj)) {
                    return;
                }
                float[] data6 = (float[]) obj;
                boolean hasEnd5 = out.writeListBegin(data6.length, "[float");
                for (float f : data6) {
                    out.writeDouble(f);
                }
                if (hasEnd5) {
                    out.writeListEnd();
                    return;
                }
                return;
            case 20:
                if (out.addRef(obj)) {
                    return;
                }
                double[] data7 = (double[]) obj;
                boolean hasEnd6 = out.writeListBegin(data7.length, "[double");
                for (double d : data7) {
                    out.writeDouble(d);
                }
                if (hasEnd6) {
                    out.writeListEnd();
                    return;
                }
                return;
            case 21:
                char[] data8 = (char[]) obj;
                out.writeString(data8, 0, data8.length);
                return;
            case 22:
                if (out.addRef(obj)) {
                    return;
                }
                String[] data9 = (String[]) obj;
                boolean hasEnd7 = out.writeListBegin(data9.length, "[string");
                for (String str : data9) {
                    out.writeString(str);
                }
                if (hasEnd7) {
                    out.writeListEnd();
                    return;
                }
                return;
            case 23:
                if (out.addRef(obj)) {
                    return;
                }
                Object[] data10 = (Object[]) obj;
                boolean hasEnd8 = out.writeListBegin(data10.length, "[object");
                for (Object obj2 : data10) {
                    out.writeObject(obj2);
                }
                if (hasEnd8) {
                    out.writeListEnd();
                    return;
                }
                return;
        }
    }
}
