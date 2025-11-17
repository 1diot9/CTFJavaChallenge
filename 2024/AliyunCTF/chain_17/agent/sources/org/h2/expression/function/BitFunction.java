package org.h2.expression.function;

import java.util.Arrays;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.aggregate.Aggregate;
import org.h2.expression.aggregate.AggregateType;
import org.h2.message.DbException;
import org.h2.mvstore.db.Store;
import org.h2.util.Bits;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBinary;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueInteger;
import org.h2.value.ValueSmallint;
import org.h2.value.ValueTinyint;
import org.h2.value.ValueVarbinary;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/BitFunction.class */
public final class BitFunction extends Function1_2 {
    public static final int BITAND = 0;
    public static final int BITOR = 1;
    public static final int BITXOR = 2;
    public static final int BITNOT = 3;
    public static final int BITNAND = 4;
    public static final int BITNOR = 5;
    public static final int BITXNOR = 6;
    public static final int BITGET = 7;
    public static final int BITCOUNT = 8;
    public static final int LSHIFT = 9;
    public static final int RSHIFT = 10;
    public static final int ULSHIFT = 11;
    public static final int URSHIFT = 12;
    public static final int ROTATELEFT = 13;
    public static final int ROTATERIGHT = 14;
    private static final String[] NAMES = {"BITAND", "BITOR", "BITXOR", "BITNOT", "BITNAND", "BITNOR", "BITXNOR", "BITGET", "BITCOUNT", "LSHIFT", "RSHIFT", "ULSHIFT", "URSHIFT", "ROTATELEFT", "ROTATERIGHT"};
    private final int function;

    public BitFunction(Expression expression, Expression expression2, int i) {
        super(expression, expression2);
        this.function = i;
    }

    @Override // org.h2.expression.function.Function1_2
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2) {
        switch (this.function) {
            case 7:
                return bitGet(value, value2);
            case 8:
                return bitCount(value);
            case 9:
                return shift(value, value2.getLong(), false);
            case 10:
                long j = value2.getLong();
                return shift(value, j != Long.MIN_VALUE ? -j : Long.MAX_VALUE, false);
            case 11:
                return shift(value, value2.getLong(), true);
            case 12:
                return shift(value, -value2.getLong(), true);
            case 13:
                return rotate(value, value2.getLong(), false);
            case 14:
                return rotate(value, value2.getLong(), true);
            default:
                return getBitwise(this.function, this.type, value, value2);
        }
    }

    private static ValueBoolean bitGet(Value value, Value value2) {
        boolean z;
        long j = value2.getLong();
        if (j >= 0) {
            switch (value.getValueType()) {
                case 5:
                case 6:
                    byte[] bytesNoCopy = value.getBytesNoCopy();
                    int i = (int) (j & 7);
                    long j2 = j >>> 3;
                    z = j2 < ((long) bytesNoCopy.length) && (bytesNoCopy[(int) j2] & (1 << i)) != 0;
                    break;
                case 7:
                case 8:
                default:
                    throw DbException.getInvalidValueException("bit function parameter", value.getTraceSQL());
                case 9:
                    z = j < 8 && (value.getByte() & (1 << ((int) j))) != 0;
                    break;
                case 10:
                    z = j < 16 && (value.getShort() & (1 << ((int) j))) != 0;
                    break;
                case 11:
                    z = j < 32 && (value.getInt() & (1 << ((int) j))) != 0;
                    break;
                case 12:
                    z = (value.getLong() & (1 << ((int) j))) != 0;
                    break;
            }
        } else {
            z = false;
        }
        return ValueBoolean.get(z);
    }

    private static ValueBigint bitCount(Value value) {
        long bitCount;
        switch (value.getValueType()) {
            case 5:
            case 6:
                int length = value.getBytesNoCopy().length;
                bitCount = 0;
                int i = 0;
                while (i < (length & (-8))) {
                    bitCount += Long.bitCount(Bits.readLong(r0, i));
                    i += 8;
                }
                while (i < length) {
                    bitCount += Integer.bitCount(r0[i] & 255);
                    i++;
                }
                break;
            case 7:
            case 8:
            default:
                throw DbException.getInvalidValueException("bit function parameter", value.getTraceSQL());
            case 9:
                bitCount = Integer.bitCount(value.getByte() & 255);
                break;
            case 10:
                bitCount = Integer.bitCount(value.getShort() & 65535);
                break;
            case 11:
                bitCount = Integer.bitCount(value.getInt());
                break;
            case 12:
                bitCount = Long.bitCount(value.getLong());
                break;
        }
        return ValueBigint.get(bitCount);
    }

    private static Value shift(Value value, long j, boolean z) {
        long j2;
        int i;
        short s;
        byte b;
        if (j == 0) {
            return value;
        }
        int valueType = value.getValueType();
        switch (valueType) {
            case 5:
            case 6:
                byte[] bytesNoCopy = value.getBytesNoCopy();
                int length = bytesNoCopy.length;
                if (length == 0) {
                    return value;
                }
                byte[] bArr = new byte[length];
                if (j > (-8) * length && j < 8 * length) {
                    if (j > 0) {
                        int i2 = (int) (j >> 3);
                        int i3 = ((int) j) & 7;
                        if (i3 == 0) {
                            System.arraycopy(bytesNoCopy, i2, bArr, 0, length - i2);
                        } else {
                            int i4 = 8 - i3;
                            int i5 = 0;
                            int i6 = i2;
                            int i7 = length - 1;
                            while (i6 < i7) {
                                int i8 = i5;
                                i5++;
                                int i9 = i6;
                                i6++;
                                bArr[i8] = (byte) ((bytesNoCopy[i9] << i3) | ((bytesNoCopy[i6] & 255) >>> i4));
                            }
                            bArr[i5] = (byte) (bytesNoCopy[i6] << i3);
                        }
                    } else {
                        long j3 = -j;
                        int i10 = (int) (j3 >> 3);
                        int i11 = ((int) j3) & 7;
                        if (i11 == 0) {
                            System.arraycopy(bytesNoCopy, 0, bArr, i10, length - i10);
                        } else {
                            int i12 = 8 - i11;
                            int i13 = 0;
                            int i14 = i10 + 1;
                            bArr[i10] = (byte) ((bytesNoCopy[0] & 255) >>> i11);
                            while (i14 < length) {
                                int i15 = i14;
                                i14++;
                                int i16 = i13;
                                i13++;
                                bArr[i15] = (byte) ((bytesNoCopy[i16] << i12) | ((bytesNoCopy[i13] & 255) >>> i11));
                            }
                        }
                    }
                }
                return valueType == 5 ? ValueBinary.getNoCopy(bArr) : ValueVarbinary.getNoCopy(bArr);
            case 7:
            case 8:
            default:
                throw DbException.getInvalidValueException("bit function parameter", value.getTraceSQL());
            case 9:
                if (j < 8) {
                    byte b2 = value.getByte();
                    if (j > -8) {
                        if (j > 0) {
                            b = (byte) (b2 << ((int) j));
                        } else if (z) {
                            b = (byte) ((b2 & 255) >>> ((int) (-j)));
                        } else {
                            b = (byte) (b2 >> ((int) (-j)));
                        }
                    } else if (z) {
                        b = 0;
                    } else {
                        b = (byte) (b2 >> 7);
                    }
                } else {
                    b = 0;
                }
                return ValueTinyint.get(b);
            case 10:
                if (j < 16) {
                    short s2 = value.getShort();
                    if (j > -16) {
                        if (j > 0) {
                            s = (short) (s2 << ((int) j));
                        } else if (z) {
                            s = (short) ((s2 & 65535) >>> ((int) (-j)));
                        } else {
                            s = (short) (s2 >> ((int) (-j)));
                        }
                    } else if (z) {
                        s = 0;
                    } else {
                        s = (short) (s2 >> 15);
                    }
                } else {
                    s = 0;
                }
                return ValueSmallint.get(s);
            case 11:
                if (j < 32) {
                    int i17 = value.getInt();
                    if (j > -32) {
                        if (j > 0) {
                            i = i17 << ((int) j);
                        } else if (z) {
                            i = i17 >>> ((int) (-j));
                        } else {
                            i = i17 >> ((int) (-j));
                        }
                    } else if (z) {
                        i = 0;
                    } else {
                        i = i17 >> 31;
                    }
                } else {
                    i = 0;
                }
                return ValueInteger.get(i);
            case 12:
                if (j < 64) {
                    long j4 = value.getLong();
                    if (j > -64) {
                        if (j > 0) {
                            j2 = j4 << ((int) j);
                        } else if (z) {
                            j2 = j4 >>> ((int) (-j));
                        } else {
                            j2 = j4 >> ((int) (-j));
                        }
                    } else if (z) {
                        j2 = 0;
                    } else {
                        j2 = j4 >> 63;
                    }
                } else {
                    j2 = 0;
                }
                return ValueBigint.get(j2);
        }
    }

    private static Value rotate(Value value, long j, boolean z) {
        int valueType = value.getValueType();
        switch (valueType) {
            case 5:
            case 6:
                byte[] bytesNoCopy = value.getBytesNoCopy();
                int length = bytesNoCopy.length;
                if (length == 0) {
                    return value;
                }
                long j2 = length << 3;
                long j3 = j % j2;
                if (z) {
                    j3 = -j3;
                }
                if (j3 == 0) {
                    return value;
                }
                if (j3 < 0) {
                    j3 += j2;
                }
                byte[] bArr = new byte[length];
                int i = (int) (j3 >> 3);
                int i2 = ((int) j3) & 7;
                if (i2 == 0) {
                    System.arraycopy(bytesNoCopy, i, bArr, 0, length - i);
                    System.arraycopy(bytesNoCopy, 0, bArr, length - i, i);
                } else {
                    int i3 = 8 - i2;
                    int i4 = 0;
                    int i5 = i;
                    while (i4 < length) {
                        int i6 = i4;
                        i4++;
                        int i7 = bytesNoCopy[i5] << i2;
                        int i8 = (i5 + 1) % length;
                        i5 = i8;
                        bArr[i6] = (byte) (i7 | ((bytesNoCopy[i8] & 255) >>> i3));
                    }
                }
                return valueType == 5 ? ValueBinary.getNoCopy(bArr) : ValueVarbinary.getNoCopy(bArr);
            case 7:
            case 8:
            default:
                throw DbException.getInvalidValueException("bit function parameter", value.getTraceSQL());
            case 9:
                int i9 = (int) j;
                if (z) {
                    i9 = -i9;
                }
                int i10 = i9 & 7;
                if (i10 == 0) {
                    return value;
                }
                int i11 = value.getByte() & 255;
                return ValueTinyint.get((byte) ((i11 << i10) | (i11 >>> (8 - i10))));
            case 10:
                int i12 = (int) j;
                if (z) {
                    i12 = -i12;
                }
                int i13 = i12 & 15;
                if (i13 == 0) {
                    return value;
                }
                int i14 = value.getShort() & 65535;
                return ValueSmallint.get((short) ((i14 << i13) | (i14 >>> (16 - i13))));
            case 11:
                int i15 = (int) j;
                if (z) {
                    i15 = -i15;
                }
                int i16 = i15 & 31;
                if (i16 == 0) {
                    return value;
                }
                return ValueInteger.get(Integer.rotateLeft(value.getInt(), i16));
            case 12:
                int i17 = (int) j;
                if (z) {
                    i17 = -i17;
                }
                int i18 = i17 & 63;
                if (i18 == 0) {
                    return value;
                }
                return ValueBigint.get(Long.rotateLeft(value.getLong(), i18));
        }
    }

    public static Value getBitwise(int i, TypeInfo typeInfo, Value value, Value value2) {
        return typeInfo.getValueType() < 9 ? getBinaryString(i, typeInfo, value, value2) : getNumeric(i, typeInfo, value, value2);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:22:0x0099. Please report as an issue. */
    private static Value getBinaryString(int i, TypeInfo typeInfo, Value value, Value value2) {
        int i2;
        int i3;
        byte[] bArr;
        if (i == 3) {
            bArr = value.getBytes();
            int length = bArr.length;
            for (int i4 = 0; i4 < length; i4++) {
                bArr[i4] = (byte) (bArr[i4] ^ (-1));
            }
        } else {
            byte[] bytesNoCopy = value.getBytesNoCopy();
            byte[] bytesNoCopy2 = value2.getBytesNoCopy();
            int length2 = bytesNoCopy.length;
            int length3 = bytesNoCopy2.length;
            if (length2 <= length3) {
                i2 = length2;
                i3 = length3;
            } else {
                i2 = length3;
                i3 = length2;
                bytesNoCopy = bytesNoCopy2;
                bytesNoCopy2 = bytesNoCopy;
            }
            int precision = (int) typeInfo.getPrecision();
            if (i2 > precision) {
                i2 = precision;
                i3 = precision;
            } else if (i3 > precision) {
                i3 = precision;
            }
            bArr = new byte[i3];
            int i5 = 0;
            switch (i) {
                case 0:
                    while (i5 < i2) {
                        bArr[i5] = (byte) (bytesNoCopy[i5] & bytesNoCopy2[i5]);
                        i5++;
                    }
                    break;
                case 1:
                    while (i5 < i2) {
                        bArr[i5] = (byte) (bytesNoCopy[i5] | bytesNoCopy2[i5]);
                        i5++;
                    }
                    System.arraycopy(bytesNoCopy2, i5, bArr, i5, i3 - i5);
                    break;
                case 2:
                    while (i5 < i2) {
                        bArr[i5] = (byte) (bytesNoCopy[i5] ^ bytesNoCopy2[i5]);
                        i5++;
                    }
                    System.arraycopy(bytesNoCopy2, i5, bArr, i5, i3 - i5);
                    break;
                case 3:
                default:
                    throw DbException.getInternalError("function=" + i);
                case 4:
                    while (i5 < i2) {
                        bArr[i5] = (byte) ((bytesNoCopy[i5] & bytesNoCopy2[i5]) ^ (-1));
                        i5++;
                    }
                    Arrays.fill(bArr, i5, i3, (byte) -1);
                    break;
                case 5:
                    while (i5 < i2) {
                        bArr[i5] = (byte) ((bytesNoCopy[i5] | bytesNoCopy2[i5]) ^ (-1));
                        i5++;
                    }
                    while (i5 < i3) {
                        bArr[i5] = (byte) (bytesNoCopy2[i5] ^ (-1));
                        i5++;
                    }
                    break;
                case 6:
                    while (i5 < i2) {
                        bArr[i5] = (byte) ((bytesNoCopy[i5] ^ bytesNoCopy2[i5]) ^ (-1));
                        i5++;
                    }
                    while (i5 < i3) {
                        bArr[i5] = (byte) (bytesNoCopy2[i5] ^ (-1));
                        i5++;
                    }
                    break;
            }
        }
        return typeInfo.getValueType() == 5 ? ValueBinary.getNoCopy(bArr) : ValueVarbinary.getNoCopy(bArr);
    }

    private static Value getNumeric(int i, TypeInfo typeInfo, Value value, Value value2) {
        long j;
        long j2 = value.getLong();
        switch (i) {
            case 0:
                j = j2 & value2.getLong();
                break;
            case 1:
                j = j2 | value2.getLong();
                break;
            case 2:
                j = j2 ^ value2.getLong();
                break;
            case 3:
                j = j2 ^ (-1);
                break;
            case 4:
                j = (j2 & value2.getLong()) ^ (-1);
                break;
            case 5:
                j = (j2 | value2.getLong()) ^ (-1);
                break;
            case 6:
                j = (j2 ^ value2.getLong()) ^ (-1);
                break;
            default:
                throw DbException.getInternalError("function=" + i);
        }
        switch (typeInfo.getValueType()) {
            case 9:
                return ValueTinyint.get((byte) j);
            case 10:
                return ValueSmallint.get((short) j);
            case 11:
                return ValueInteger.get((int) j);
            case 12:
                return ValueBigint.get(j);
            default:
                throw DbException.getInternalError();
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        if (this.right != null) {
            this.right = this.right.optimize(sessionLocal);
        }
        switch (this.function) {
            case 3:
                return optimizeNot(sessionLocal);
            case 4:
            case 5:
            case 6:
            default:
                this.type = getCommonType(this.left, this.right);
                break;
            case 7:
                this.type = TypeInfo.TYPE_BOOLEAN;
                break;
            case 8:
                this.type = TypeInfo.TYPE_BIGINT;
                break;
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                this.type = checkArgType(this.left);
                break;
        }
        if (this.left.isConstant() && (this.right == null || this.right.isConstant())) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    private Expression optimizeNot(SessionLocal sessionLocal) {
        AggregateType aggregateType;
        int i;
        this.type = checkArgType(this.left);
        if (this.left.isConstant()) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        if (this.left instanceof BitFunction) {
            BitFunction bitFunction = (BitFunction) this.left;
            int i2 = bitFunction.function;
            switch (i2) {
                case 0:
                case 1:
                case 2:
                    i = i2 + 4;
                    break;
                case 3:
                    return bitFunction.left;
                case 4:
                case 5:
                case 6:
                    i = i2 - 4;
                    break;
                default:
                    return this;
            }
            return new BitFunction(bitFunction.left, bitFunction.right, i).optimize(sessionLocal);
        }
        if (this.left instanceof Aggregate) {
            Aggregate aggregate = (Aggregate) this.left;
            switch (aggregate.getAggregateType()) {
                case BIT_AND_AGG:
                    aggregateType = AggregateType.BIT_NAND_AGG;
                    break;
                case BIT_OR_AGG:
                    aggregateType = AggregateType.BIT_NOR_AGG;
                    break;
                case BIT_XOR_AGG:
                    aggregateType = AggregateType.BIT_XNOR_AGG;
                    break;
                case BIT_NAND_AGG:
                    aggregateType = AggregateType.BIT_AND_AGG;
                    break;
                case BIT_NOR_AGG:
                    aggregateType = AggregateType.BIT_OR_AGG;
                    break;
                case BIT_XNOR_AGG:
                    aggregateType = AggregateType.BIT_XOR_AGG;
                    break;
                default:
                    return this;
            }
            Aggregate aggregate2 = new Aggregate(aggregateType, new Expression[]{aggregate.getSubexpression(0)}, aggregate.getSelect(), aggregate.isDistinct());
            aggregate2.setFilterCondition(aggregate.getFilterCondition());
            aggregate2.setOverCondition(aggregate.getOverCondition());
            return aggregate2.optimize(sessionLocal);
        }
        return this;
    }

    private static TypeInfo getCommonType(Expression expression, Expression expression2) {
        long max;
        TypeInfo checkArgType = checkArgType(expression);
        TypeInfo checkArgType2 = checkArgType(expression2);
        int valueType = checkArgType.getValueType();
        int valueType2 = checkArgType2.getValueType();
        boolean isBinaryStringType = DataType.isBinaryStringType(valueType);
        if (isBinaryStringType != DataType.isBinaryStringType(valueType2)) {
            throw DbException.getInvalidValueException("bit function parameters", checkArgType2.getSQL(checkArgType.getSQL(new StringBuilder(), 3).append(" vs "), 3).toString());
        }
        if (isBinaryStringType) {
            if (valueType == 5) {
                max = checkArgType.getDeclaredPrecision();
                if (valueType2 == 5) {
                    max = Math.max(max, checkArgType2.getDeclaredPrecision());
                }
            } else if (valueType2 == 5) {
                valueType = 5;
                max = checkArgType2.getDeclaredPrecision();
            } else {
                long declaredPrecision = checkArgType.getDeclaredPrecision();
                long declaredPrecision2 = checkArgType2.getDeclaredPrecision();
                max = (declaredPrecision <= 0 || declaredPrecision2 <= 0) ? -1L : Math.max(declaredPrecision, declaredPrecision2);
            }
            return TypeInfo.getTypeInfo(valueType, max, 0, null);
        }
        return TypeInfo.getTypeInfo(Math.max(valueType, valueType2));
    }

    public static TypeInfo checkArgType(Expression expression) {
        TypeInfo type = expression.getType();
        switch (type.getValueType()) {
            case 0:
            case 5:
            case 6:
            case 9:
            case 10:
            case 11:
            case 12:
                return type;
            case 1:
            case 2:
            case 3:
            case 4:
            case 7:
            case 8:
            default:
                throw Store.getInvalidExpressionTypeException("bit function argument", expression);
        }
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}
