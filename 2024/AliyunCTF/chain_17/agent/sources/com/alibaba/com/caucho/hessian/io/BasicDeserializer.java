package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/BasicDeserializer.class */
public class BasicDeserializer extends AbstractDeserializer {
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
    private int _code;

    public BasicDeserializer(int code) {
        this._code = code;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Class getType() {
        switch (this._code) {
            case 0:
                return Void.TYPE;
            case 1:
                return Boolean.class;
            case 2:
                return Byte.class;
            case 3:
                return Short.class;
            case 4:
                return Integer.class;
            case 5:
                return Long.class;
            case 6:
                return Float.class;
            case 7:
                return Double.class;
            case 8:
                return Character.class;
            case 9:
                return Character.class;
            case 10:
                return String.class;
            case 11:
                return Date.class;
            case 12:
                return Number.class;
            case 13:
                return Object.class;
            case 14:
                return boolean[].class;
            case 15:
                return byte[].class;
            case 16:
                return short[].class;
            case 17:
                return int[].class;
            case 18:
                return long[].class;
            case 19:
                return float[].class;
            case 20:
                return double[].class;
            case 21:
                return char[].class;
            case 22:
                return String[].class;
            case 23:
                return Object[].class;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readObject(AbstractHessianInput in) throws IOException {
        if (in.checkAndReadNull()) {
            return null;
        }
        switch (this._code) {
            case 0:
                in.readObject();
                return null;
            case 1:
                return Boolean.valueOf(in.readBoolean());
            case 2:
                return Byte.valueOf((byte) in.readInt());
            case 3:
                return Short.valueOf((short) in.readInt());
            case 4:
                return Integer.valueOf(in.readInt());
            case 5:
                return Long.valueOf(in.readLong());
            case 6:
                return Float.valueOf((float) in.readDouble());
            case 7:
                return Double.valueOf(in.readDouble());
            case 8:
                String s = in.readString();
                if (s == null || s.equals("")) {
                    return (char) 0;
                }
                return Character.valueOf(s.charAt(0));
            case 9:
                String s2 = in.readString();
                if (s2 == null || s2.equals("")) {
                    return null;
                }
                return Character.valueOf(s2.charAt(0));
            case 10:
                return in.readString();
            case 11:
                return new Date(in.readUTCDate());
            case 12:
                return in.readObject();
            case 13:
                return in.readObject();
            case 14:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 22:
                int code = in.readListStart();
                switch (code) {
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                        int length = code - 16;
                        in.readInt();
                        return readLengthList(in, length);
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 40:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                    case 45:
                    case 46:
                    case 47:
                    case 48:
                    case 49:
                    case 50:
                    case 51:
                    case 52:
                    case 53:
                    case 54:
                    case 55:
                    case 56:
                    case 57:
                    case 58:
                    case 59:
                    case 60:
                    case 61:
                    case 62:
                    case 63:
                    case 64:
                    case 65:
                    case 66:
                    case 67:
                    case 68:
                    case 69:
                    case 70:
                    case 71:
                    case 72:
                    case 73:
                    case 74:
                    case 75:
                    case 76:
                    case 77:
                    default:
                        in.readType();
                        int length2 = in.readLength();
                        return readList(in, length2);
                    case 78:
                        return null;
                }
            case 15:
                return in.readBytes();
            case 21:
                String s3 = in.readString();
                if (s3 == null) {
                    return null;
                }
                int len = s3.length();
                char[] chars = new char[len];
                s3.getChars(0, len, chars, 0);
                return chars;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readList(AbstractHessianInput in, int length) throws IOException {
        switch (this._code) {
            case 14:
                if (length >= 0) {
                    boolean[] data = new boolean[length];
                    in.addRef(data);
                    for (int i = 0; i < data.length; i++) {
                        data[i] = in.readBoolean();
                    }
                    in.readEnd();
                    return data;
                }
                ArrayList list = new ArrayList();
                while (!in.isEnd()) {
                    list.add(Boolean.valueOf(in.readBoolean()));
                }
                in.readEnd();
                boolean[] data2 = new boolean[list.size()];
                in.addRef(data2);
                for (int i2 = 0; i2 < data2.length; i2++) {
                    data2[i2] = ((Boolean) list.get(i2)).booleanValue();
                }
                return data2;
            case 15:
            case 21:
            default:
                throw new UnsupportedOperationException(String.valueOf(this));
            case 16:
                if (length >= 0) {
                    short[] data3 = new short[length];
                    in.addRef(data3);
                    for (int i3 = 0; i3 < data3.length; i3++) {
                        data3[i3] = (short) in.readInt();
                    }
                    in.readEnd();
                    return data3;
                }
                ArrayList list2 = new ArrayList();
                while (!in.isEnd()) {
                    list2.add(Short.valueOf((short) in.readInt()));
                }
                in.readEnd();
                short[] data4 = new short[list2.size()];
                for (int i4 = 0; i4 < data4.length; i4++) {
                    data4[i4] = ((Short) list2.get(i4)).shortValue();
                }
                in.addRef(data4);
                return data4;
            case 17:
                if (length >= 0) {
                    int[] data5 = new int[length];
                    in.addRef(data5);
                    for (int i5 = 0; i5 < data5.length; i5++) {
                        data5[i5] = in.readInt();
                    }
                    in.readEnd();
                    return data5;
                }
                ArrayList list3 = new ArrayList();
                while (!in.isEnd()) {
                    list3.add(Integer.valueOf(in.readInt()));
                }
                in.readEnd();
                int[] data6 = new int[list3.size()];
                for (int i6 = 0; i6 < data6.length; i6++) {
                    data6[i6] = ((Integer) list3.get(i6)).intValue();
                }
                in.addRef(data6);
                return data6;
            case 18:
                if (length >= 0) {
                    long[] data7 = new long[length];
                    in.addRef(data7);
                    for (int i7 = 0; i7 < data7.length; i7++) {
                        data7[i7] = in.readLong();
                    }
                    in.readEnd();
                    return data7;
                }
                ArrayList list4 = new ArrayList();
                while (!in.isEnd()) {
                    list4.add(Long.valueOf(in.readLong()));
                }
                in.readEnd();
                long[] data8 = new long[list4.size()];
                for (int i8 = 0; i8 < data8.length; i8++) {
                    data8[i8] = ((Long) list4.get(i8)).longValue();
                }
                in.addRef(data8);
                return data8;
            case 19:
                if (length >= 0) {
                    float[] data9 = new float[length];
                    in.addRef(data9);
                    for (int i9 = 0; i9 < data9.length; i9++) {
                        data9[i9] = (float) in.readDouble();
                    }
                    in.readEnd();
                    return data9;
                }
                ArrayList list5 = new ArrayList();
                while (!in.isEnd()) {
                    list5.add(new Float(in.readDouble()));
                }
                in.readEnd();
                float[] data10 = new float[list5.size()];
                for (int i10 = 0; i10 < data10.length; i10++) {
                    data10[i10] = ((Float) list5.get(i10)).floatValue();
                }
                in.addRef(data10);
                return data10;
            case 20:
                if (length >= 0) {
                    double[] data11 = new double[length];
                    in.addRef(data11);
                    for (int i11 = 0; i11 < data11.length; i11++) {
                        data11[i11] = in.readDouble();
                    }
                    in.readEnd();
                    return data11;
                }
                ArrayList list6 = new ArrayList();
                while (!in.isEnd()) {
                    list6.add(new Double(in.readDouble()));
                }
                in.readEnd();
                double[] data12 = new double[list6.size()];
                in.addRef(data12);
                for (int i12 = 0; i12 < data12.length; i12++) {
                    data12[i12] = ((Double) list6.get(i12)).doubleValue();
                }
                return data12;
            case 22:
                if (length >= 0) {
                    String[] data13 = new String[length];
                    in.addRef(data13);
                    for (int i13 = 0; i13 < data13.length; i13++) {
                        data13[i13] = in.readString();
                    }
                    in.readEnd();
                    return data13;
                }
                ArrayList list7 = new ArrayList();
                while (!in.isEnd()) {
                    list7.add(in.readString());
                }
                in.readEnd();
                String[] data14 = new String[list7.size()];
                in.addRef(data14);
                for (int i14 = 0; i14 < data14.length; i14++) {
                    data14[i14] = (String) list7.get(i14);
                }
                return data14;
            case 23:
                if (length >= 0) {
                    Object[] data15 = new Object[length];
                    in.addRef(data15);
                    for (int i15 = 0; i15 < data15.length; i15++) {
                        data15[i15] = in.readObject();
                    }
                    in.readEnd();
                    return data15;
                }
                ArrayList list8 = new ArrayList();
                in.addRef(list8);
                while (!in.isEnd()) {
                    list8.add(in.readObject());
                }
                in.readEnd();
                Object[] data16 = new Object[list8.size()];
                for (int i16 = 0; i16 < data16.length; i16++) {
                    data16[i16] = list8.get(i16);
                }
                return data16;
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        switch (this._code) {
            case 14:
                boolean[] data = new boolean[length];
                in.addRef(data);
                for (int i = 0; i < data.length; i++) {
                    data[i] = in.readBoolean();
                }
                return data;
            case 15:
            case 21:
            default:
                throw new UnsupportedOperationException(String.valueOf(this));
            case 16:
                short[] data2 = new short[length];
                in.addRef(data2);
                for (int i2 = 0; i2 < data2.length; i2++) {
                    data2[i2] = (short) in.readInt();
                }
                return data2;
            case 17:
                int[] data3 = new int[length];
                in.addRef(data3);
                for (int i3 = 0; i3 < data3.length; i3++) {
                    data3[i3] = in.readInt();
                }
                return data3;
            case 18:
                long[] data4 = new long[length];
                in.addRef(data4);
                for (int i4 = 0; i4 < data4.length; i4++) {
                    data4[i4] = in.readLong();
                }
                return data4;
            case 19:
                float[] data5 = new float[length];
                in.addRef(data5);
                for (int i5 = 0; i5 < data5.length; i5++) {
                    data5[i5] = (float) in.readDouble();
                }
                return data5;
            case 20:
                double[] data6 = new double[length];
                in.addRef(data6);
                for (int i6 = 0; i6 < data6.length; i6++) {
                    data6[i6] = in.readDouble();
                }
                return data6;
            case 22:
                String[] data7 = new String[length];
                in.addRef(data7);
                for (int i7 = 0; i7 < data7.length; i7++) {
                    data7[i7] = in.readString();
                }
                return data7;
            case 23:
                Object[] data8 = new Object[length];
                in.addRef(data8);
                for (int i8 = 0; i8 < data8.length; i8++) {
                    data8[i8] = in.readObject();
                }
                return data8;
        }
    }

    public String toString() {
        return getClass().getSimpleName() + "[" + this._code + "]";
    }
}
