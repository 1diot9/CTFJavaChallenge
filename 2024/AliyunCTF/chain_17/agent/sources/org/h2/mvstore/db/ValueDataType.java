package org.h2.mvstore.db;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import org.apache.juli.JdkLoggerFormatter;
import org.h2.api.ErrorCode;
import org.h2.api.IntervalQualifier;
import org.h2.engine.CastDataProvider;
import org.h2.engine.Database;
import org.h2.message.DbException;
import org.h2.mode.DefaultNullOrdering;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.BasicDataType;
import org.h2.mvstore.type.DataType;
import org.h2.mvstore.type.MetaType;
import org.h2.mvstore.type.StatefulDataType;
import org.h2.result.RowFactory;
import org.h2.result.SearchRow;
import org.h2.store.DataHandler;
import org.h2.store.LobStorageFrontend;
import org.h2.util.DateTimeUtils;
import org.h2.util.Utils;
import org.h2.value.CompareMode;
import org.h2.value.ExtTypeInfoEnum;
import org.h2.value.ExtTypeInfoRow;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBinary;
import org.h2.value.ValueBlob;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueChar;
import org.h2.value.ValueClob;
import org.h2.value.ValueCollectionBase;
import org.h2.value.ValueDate;
import org.h2.value.ValueDecfloat;
import org.h2.value.ValueDouble;
import org.h2.value.ValueGeometry;
import org.h2.value.ValueInteger;
import org.h2.value.ValueInterval;
import org.h2.value.ValueJavaObject;
import org.h2.value.ValueJson;
import org.h2.value.ValueNull;
import org.h2.value.ValueNumeric;
import org.h2.value.ValueReal;
import org.h2.value.ValueRow;
import org.h2.value.ValueSmallint;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueTinyint;
import org.h2.value.ValueUuid;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;
import org.h2.value.ValueVarcharIgnoreCase;
import org.h2.value.lob.LobData;
import org.h2.value.lob.LobDataDatabase;
import org.h2.value.lob.LobDataInMemory;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/ValueDataType.class */
public final class ValueDataType extends BasicDataType<Value> implements StatefulDataType<Database> {
    private static final byte NULL = 0;
    private static final byte TINYINT = 2;
    private static final byte SMALLINT = 3;
    private static final byte INTEGER = 4;
    private static final byte BIGINT = 5;
    private static final byte NUMERIC = 6;
    private static final byte DOUBLE = 7;
    private static final byte REAL = 8;
    private static final byte TIME = 9;
    private static final byte DATE = 10;
    private static final byte TIMESTAMP = 11;
    private static final byte VARBINARY = 12;
    private static final byte VARCHAR = 13;
    private static final byte VARCHAR_IGNORECASE = 14;
    private static final byte BLOB = 15;
    private static final byte CLOB = 16;
    private static final byte ARRAY = 17;
    private static final byte JAVA_OBJECT = 19;
    private static final byte UUID = 20;
    private static final byte CHAR = 21;
    private static final byte GEOMETRY = 22;
    private static final byte TIMESTAMP_TZ_OLD = 24;
    private static final byte ENUM = 25;
    private static final byte INTERVAL = 26;
    private static final byte ROW = 27;
    private static final byte INT_0_15 = 32;
    private static final byte BIGINT_0_7 = 48;
    private static final byte NUMERIC_0_1 = 56;
    private static final byte NUMERIC_SMALL_0 = 58;
    private static final byte NUMERIC_SMALL = 59;
    private static final byte DOUBLE_0_1 = 60;
    private static final byte REAL_0_1 = 62;
    private static final byte BOOLEAN_FALSE = 64;
    private static final byte BOOLEAN_TRUE = 65;
    private static final byte INT_NEG = 66;
    private static final byte BIGINT_NEG = 67;
    private static final byte VARCHAR_0_31 = 68;
    private static final int VARBINARY_0_31 = 100;
    private static final int JSON = 134;
    private static final int TIMESTAMP_TZ = 135;
    private static final int TIME_TZ = 136;
    private static final int BINARY = 137;
    private static final int DECFLOAT = 138;
    final DataHandler handler;
    final CastDataProvider provider;
    final CompareMode compareMode;
    final int[] sortTypes;
    private RowFactory rowFactory;
    private static final Factory FACTORY;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !ValueDataType.class.desiredAssertionStatus();
        FACTORY = new Factory();
    }

    public ValueDataType() {
        this(null, CompareMode.getInstance(null, 0), null, null);
    }

    public ValueDataType(Database database, int[] iArr) {
        this(database, database.getCompareMode(), database, iArr);
    }

    public ValueDataType(CastDataProvider castDataProvider, CompareMode compareMode, DataHandler dataHandler, int[] iArr) {
        this.provider = castDataProvider;
        this.compareMode = compareMode;
        this.handler = dataHandler;
        this.sortTypes = iArr;
    }

    public RowFactory getRowFactory() {
        return this.rowFactory;
    }

    public void setRowFactory(RowFactory rowFactory) {
        this.rowFactory = rowFactory;
    }

    @Override // org.h2.mvstore.type.DataType
    public Value[] createStorage(int i) {
        return new Value[i];
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
    public int compare(Value value, Value value2) {
        if (value == value2) {
            return 0;
        }
        if ((value instanceof SearchRow) && (value2 instanceof SearchRow)) {
            return compare((SearchRow) value, (SearchRow) value2);
        }
        if ((value instanceof ValueCollectionBase) && (value2 instanceof ValueCollectionBase)) {
            Value[] list = ((ValueCollectionBase) value).getList();
            Value[] list2 = ((ValueCollectionBase) value2).getList();
            int length = list.length;
            int length2 = list2.length;
            int min = Math.min(length, length2);
            for (int i = 0; i < min; i++) {
                int i2 = this.sortTypes == null ? 0 : this.sortTypes[i];
                Value value3 = list[i];
                Value value4 = list2[i];
                if (value3 == null || value4 == null) {
                    return compareValues(list[min - 1], list2[min - 1], 0);
                }
                int compareValues = compareValues(value3, value4, i2);
                if (compareValues != 0) {
                    return compareValues;
                }
            }
            if (min < length) {
                return -1;
            }
            if (min < length2) {
                return 1;
            }
            return 0;
        }
        return compareValues(value, value2, 0);
    }

    private int compare(SearchRow searchRow, SearchRow searchRow2) {
        if (searchRow == searchRow2) {
            return 0;
        }
        int[] indexes = this.rowFactory.getIndexes();
        if (indexes == null) {
            int columnCount = searchRow.getColumnCount();
            if (!$assertionsDisabled && columnCount != searchRow2.getColumnCount()) {
                throw new AssertionError(columnCount + " != " + searchRow2.getColumnCount());
            }
            for (int i = 0; i < columnCount; i++) {
                int compareValues = compareValues(searchRow.getValue(i), searchRow2.getValue(i), this.sortTypes[i]);
                if (compareValues != 0) {
                    return compareValues;
                }
            }
            return 0;
        }
        if (!$assertionsDisabled && this.sortTypes.length != indexes.length) {
            throw new AssertionError();
        }
        for (int i2 = 0; i2 < indexes.length; i2++) {
            int i3 = indexes[i2];
            Value value = searchRow.getValue(i3);
            Value value2 = searchRow2.getValue(i3);
            if (value == null || value2 == null) {
                break;
            }
            int compareValues2 = compareValues(searchRow.getValue(i3), searchRow2.getValue(i3), this.sortTypes[i2]);
            if (compareValues2 != 0) {
                return compareValues2;
            }
        }
        long key = searchRow.getKey();
        long key2 = searchRow2.getKey();
        if (key == SearchRow.MATCH_ALL_ROW_KEY || key2 == SearchRow.MATCH_ALL_ROW_KEY) {
            return 0;
        }
        return Long.compare(key, key2);
    }

    public int compareValues(Value value, Value value2, int i) {
        if (value == value2) {
            return 0;
        }
        boolean z = value == ValueNull.INSTANCE;
        if (z || value2 == ValueNull.INSTANCE) {
            return DefaultNullOrdering.LOW.compareNull(z, i);
        }
        int compareTo = value.compareTo(value2, this.provider, this.compareMode);
        if ((i & 1) != 0) {
            compareTo = -compareTo;
        }
        return compareTo;
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public int getMemory(Value value) {
        if (value == null) {
            return 0;
        }
        return value.getMemory();
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public Value read(ByteBuffer byteBuffer) {
        return readValue(byteBuffer, null);
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public void write(WriteBuffer writeBuffer, Value value) {
        int i;
        if (value == ValueNull.INSTANCE) {
            writeBuffer.put((byte) 0);
            return;
        }
        int valueType = value.getValueType();
        switch (valueType) {
            case 1:
                writeString(writeBuffer.put((byte) 21), value.getString());
                return;
            case 2:
                String string = value.getString();
                int length = string.length();
                if (length < 32) {
                    writeBuffer.put((byte) (68 + length)).putStringData(string, length);
                    return;
                } else {
                    writeString(writeBuffer.put((byte) 13), string);
                    return;
                }
            case 3:
                writeBuffer.put((byte) 16);
                ValueClob valueClob = (ValueClob) value;
                LobData lobData = valueClob.getLobData();
                if (lobData instanceof LobDataDatabase) {
                    LobDataDatabase lobDataDatabase = (LobDataDatabase) lobData;
                    writeBuffer.putVarInt(-3).putVarInt(lobDataDatabase.getTableId()).putVarLong(lobDataDatabase.getLobId()).putVarLong(valueClob.octetLength()).putVarLong(valueClob.charLength());
                    return;
                } else {
                    byte[] small = ((LobDataInMemory) lobData).getSmall();
                    writeBuffer.putVarInt(small.length).put(small).putVarLong(valueClob.charLength());
                    return;
                }
            case 4:
                writeString(writeBuffer.put((byte) 14), value.getString());
                return;
            case 5:
                writeBinary((byte) -119, writeBuffer, value);
                return;
            case 6:
                byte[] bytesNoCopy = value.getBytesNoCopy();
                int length2 = bytesNoCopy.length;
                if (length2 < 32) {
                    writeBuffer.put((byte) (100 + length2)).put(bytesNoCopy);
                    return;
                } else {
                    writeBuffer.put((byte) 12).putVarInt(length2).put(bytesNoCopy);
                    return;
                }
            case 7:
                writeBuffer.put((byte) 15);
                ValueBlob valueBlob = (ValueBlob) value;
                LobData lobData2 = valueBlob.getLobData();
                if (lobData2 instanceof LobDataDatabase) {
                    LobDataDatabase lobDataDatabase2 = (LobDataDatabase) lobData2;
                    writeBuffer.putVarInt(-3).putVarInt(lobDataDatabase2.getTableId()).putVarLong(lobDataDatabase2.getLobId()).putVarLong(valueBlob.octetLength());
                    return;
                } else {
                    byte[] small2 = ((LobDataInMemory) lobData2).getSmall();
                    writeBuffer.putVarInt(small2.length).put(small2);
                    return;
                }
            case 8:
                writeBuffer.put(value.getBoolean() ? (byte) 65 : (byte) 64);
                return;
            case 9:
                writeBuffer.put((byte) 2).put(value.getByte());
                return;
            case 10:
                writeBuffer.put((byte) 3).putShort(value.getShort());
                return;
            case 11:
            case 36:
                int i2 = value.getInt();
                if (i2 < 0) {
                    writeBuffer.put((byte) 66).putVarInt(-i2);
                    return;
                } else if (i2 < 16) {
                    writeBuffer.put((byte) (32 + i2));
                    return;
                } else {
                    writeBuffer.put(valueType == 11 ? (byte) 4 : (byte) 25).putVarInt(i2);
                    return;
                }
            case 12:
                writeLong(writeBuffer, value.getLong());
                return;
            case 13:
                BigDecimal bigDecimal = value.getBigDecimal();
                if (BigDecimal.ZERO.equals(bigDecimal)) {
                    writeBuffer.put((byte) 56);
                    return;
                }
                if (BigDecimal.ONE.equals(bigDecimal)) {
                    writeBuffer.put((byte) 57);
                    return;
                }
                int scale = bigDecimal.scale();
                BigInteger unscaledValue = bigDecimal.unscaledValue();
                if (unscaledValue.bitLength() <= 63) {
                    if (scale == 0) {
                        writeBuffer.put((byte) 58).putVarLong(unscaledValue.longValue());
                        return;
                    } else {
                        writeBuffer.put((byte) 59).putVarInt(scale).putVarLong(unscaledValue.longValue());
                        return;
                    }
                }
                byte[] byteArray = unscaledValue.toByteArray();
                writeBuffer.put((byte) 6).putVarInt(scale).putVarInt(byteArray.length).put(byteArray);
                return;
            case 14:
                float f = value.getFloat();
                if (f == 1.0f) {
                    writeBuffer.put((byte) 63);
                    return;
                }
                int floatToIntBits = Float.floatToIntBits(f);
                if (floatToIntBits == 0) {
                    writeBuffer.put((byte) 62);
                    return;
                } else {
                    writeBuffer.put((byte) 8).putVarInt(Integer.reverse(floatToIntBits));
                    return;
                }
            case 15:
                double d = value.getDouble();
                if (d == 1.0d) {
                    writeBuffer.put((byte) 61);
                    return;
                }
                long doubleToLongBits = Double.doubleToLongBits(d);
                if (doubleToLongBits == 0) {
                    writeBuffer.put((byte) 60);
                    return;
                } else {
                    writeBuffer.put((byte) 7).putVarLong(Long.reverse(doubleToLongBits));
                    return;
                }
            case 16:
                ValueDecfloat valueDecfloat = (ValueDecfloat) value;
                writeBuffer.put((byte) -118);
                if (valueDecfloat.isFinite()) {
                    BigDecimal bigDecimal2 = valueDecfloat.getBigDecimal();
                    byte[] byteArray2 = bigDecimal2.unscaledValue().toByteArray();
                    writeBuffer.putVarInt(bigDecimal2.scale()).putVarInt(byteArray2.length).put(byteArray2);
                    return;
                } else {
                    if (valueDecfloat == ValueDecfloat.NEGATIVE_INFINITY) {
                        i = -3;
                    } else if (valueDecfloat == ValueDecfloat.POSITIVE_INFINITY) {
                        i = -2;
                    } else {
                        i = -1;
                    }
                    writeBuffer.putVarInt(0).putVarInt(i);
                    return;
                }
            case 17:
                writeBuffer.put((byte) 10).putVarLong(((ValueDate) value).getDateValue());
                return;
            case 18:
                writeTimestampTime(writeBuffer.put((byte) 9), ((ValueTime) value).getNanos());
                return;
            case 19:
                ValueTimeTimeZone valueTimeTimeZone = (ValueTimeTimeZone) value;
                long nanos = valueTimeTimeZone.getNanos();
                writeBuffer.put((byte) -120).putVarInt((int) (nanos / DateTimeUtils.NANOS_PER_SECOND)).putVarInt((int) (nanos % DateTimeUtils.NANOS_PER_SECOND));
                writeTimeZone(writeBuffer, valueTimeTimeZone.getTimeZoneOffsetSeconds());
                return;
            case 20:
                ValueTimestamp valueTimestamp = (ValueTimestamp) value;
                writeBuffer.put((byte) 11).putVarLong(valueTimestamp.getDateValue());
                writeTimestampTime(writeBuffer, valueTimestamp.getTimeNanos());
                return;
            case 21:
                ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) value;
                writeBuffer.put((byte) -121).putVarLong(valueTimestampTimeZone.getDateValue());
                writeTimestampTime(writeBuffer, valueTimestampTimeZone.getTimeNanos());
                writeTimeZone(writeBuffer, valueTimestampTimeZone.getTimeZoneOffsetSeconds());
                return;
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
                ValueInterval valueInterval = (ValueInterval) value;
                int i3 = valueType - 22;
                if (valueInterval.isNegative()) {
                    i3 ^= -1;
                }
                writeBuffer.put((byte) 26).put((byte) i3).putVarLong(valueInterval.getLeading());
                return;
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                ValueInterval valueInterval2 = (ValueInterval) value;
                int i4 = valueType - 22;
                if (valueInterval2.isNegative()) {
                    i4 ^= -1;
                }
                writeBuffer.put((byte) 26).put((byte) i4).putVarLong(valueInterval2.getLeading()).putVarLong(valueInterval2.getRemaining());
                return;
            case 35:
                writeBinary((byte) 19, writeBuffer, value);
                return;
            case 37:
                writeBinary((byte) 22, writeBuffer, value);
                return;
            case 38:
                writeBinary((byte) -122, writeBuffer, value);
                return;
            case 39:
                ValueUuid valueUuid = (ValueUuid) value;
                writeBuffer.put((byte) 20).putLong(valueUuid.getHigh()).putLong(valueUuid.getLow());
                return;
            case 40:
            case 41:
                Value[] list = ((ValueCollectionBase) value).getList();
                writeBuffer.put(valueType == 40 ? (byte) 17 : (byte) 27).putVarInt(list.length);
                for (Value value2 : list) {
                    write(writeBuffer, value2);
                }
                return;
            default:
                throw DbException.getInternalError("type=" + value.getValueType());
        }
    }

    private static void writeBinary(byte b, WriteBuffer writeBuffer, Value value) {
        byte[] bytesNoCopy = value.getBytesNoCopy();
        writeBuffer.put(b).putVarInt(bytesNoCopy.length).put(bytesNoCopy);
    }

    public static void writeLong(WriteBuffer writeBuffer, long j) {
        if (j < 0) {
            writeBuffer.put((byte) 67).putVarLong(-j);
        } else if (j < 8) {
            writeBuffer.put((byte) (48 + j));
        } else {
            writeBuffer.put((byte) 5).putVarLong(j);
        }
    }

    private static void writeString(WriteBuffer writeBuffer, String str) {
        int length = str.length();
        writeBuffer.putVarInt(length).putStringData(str, length);
    }

    private static void writeTimestampTime(WriteBuffer writeBuffer, long j) {
        long j2 = j / 1000000;
        writeBuffer.putVarLong(j2).putVarInt((int) (j - (j2 * 1000000)));
    }

    private static void writeTimeZone(WriteBuffer writeBuffer, int i) {
        if (i % JdkLoggerFormatter.LOG_LEVEL_WARN == 0) {
            writeBuffer.put((byte) (i / JdkLoggerFormatter.LOG_LEVEL_WARN));
        } else if (i > 0) {
            writeBuffer.put(Byte.MAX_VALUE).putVarInt(i);
        } else {
            writeBuffer.put(Byte.MIN_VALUE).putVarInt(-i);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Value readValue(ByteBuffer byteBuffer, TypeInfo typeInfo) {
        int i = byteBuffer.get() & 255;
        switch (i) {
            case 0:
                return ValueNull.INSTANCE;
            case 1:
            case 18:
            case 23:
            case 28:
            case 29:
            case 30:
            case 31:
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
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case Opcodes.DMUL /* 107 */:
            case 108:
            case Opcodes.LDIV /* 109 */:
            case Opcodes.FDIV /* 110 */:
            case Opcodes.DDIV /* 111 */:
            case 112:
            case Opcodes.LREM /* 113 */:
            case Opcodes.FREM /* 114 */:
            case 115:
            case 116:
            case Opcodes.LNEG /* 117 */:
            case Opcodes.FNEG /* 118 */:
            case Opcodes.DNEG /* 119 */:
            case 120:
            case Opcodes.LSHL /* 121 */:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case Opcodes.LOR /* 129 */:
            case 130:
            case Opcodes.LXOR /* 131 */:
            case Opcodes.IINC /* 132 */:
            case Opcodes.I2L /* 133 */:
            default:
                if (i >= 32 && i < 48) {
                    int i2 = i - 32;
                    if (typeInfo != null && typeInfo.getValueType() == 36) {
                        return ((ExtTypeInfoEnum) typeInfo.getExtTypeInfo()).getValue(i2, this.provider);
                    }
                    return ValueInteger.get(i2);
                }
                if (i >= 48 && i < 56) {
                    return ValueBigint.get(i - 48);
                }
                if (i >= 100 && i < 132) {
                    int i3 = i - 100;
                    byte[] newBytes = Utils.newBytes(i3);
                    byteBuffer.get(newBytes, 0, i3);
                    return ValueVarbinary.getNoCopy(newBytes);
                }
                if (i >= 68 && i < 100) {
                    return ValueVarchar.get(DataUtils.readString(byteBuffer, i - 68));
                }
                throw DbException.get(ErrorCode.FILE_CORRUPTED_1, "type: " + i);
            case 2:
                return ValueTinyint.get(byteBuffer.get());
            case 3:
                return ValueSmallint.get(byteBuffer.getShort());
            case 4:
                return ValueInteger.get(DataUtils.readVarInt(byteBuffer));
            case 5:
                return ValueBigint.get(DataUtils.readVarLong(byteBuffer));
            case 6:
                return ValueNumeric.get(new BigDecimal(new BigInteger(readVarBytes(byteBuffer)), DataUtils.readVarInt(byteBuffer)));
            case 7:
                return ValueDouble.get(Double.longBitsToDouble(Long.reverse(DataUtils.readVarLong(byteBuffer))));
            case 8:
                return ValueReal.get(Float.intBitsToFloat(Integer.reverse(DataUtils.readVarInt(byteBuffer))));
            case 9:
                return ValueTime.fromNanos(readTimestampTime(byteBuffer));
            case 10:
                return ValueDate.fromDateValue(DataUtils.readVarLong(byteBuffer));
            case 11:
                return ValueTimestamp.fromDateValueAndNanos(DataUtils.readVarLong(byteBuffer), readTimestampTime(byteBuffer));
            case 12:
                return ValueVarbinary.getNoCopy(readVarBytes(byteBuffer));
            case 13:
                return ValueVarchar.get(DataUtils.readString(byteBuffer));
            case 14:
                return ValueVarcharIgnoreCase.get(DataUtils.readString(byteBuffer));
            case 15:
                int readVarInt = DataUtils.readVarInt(byteBuffer);
                if (readVarInt >= 0) {
                    byte[] newBytes2 = Utils.newBytes(readVarInt);
                    byteBuffer.get(newBytes2, 0, readVarInt);
                    return ValueBlob.createSmall(newBytes2);
                }
                if (readVarInt == -3) {
                    return new ValueBlob(readLobDataDatabase(byteBuffer), DataUtils.readVarLong(byteBuffer));
                }
                throw DbException.get(ErrorCode.FILE_CORRUPTED_1, "lob type: " + readVarInt);
            case 16:
                int readVarInt2 = DataUtils.readVarInt(byteBuffer);
                if (readVarInt2 >= 0) {
                    byte[] newBytes3 = Utils.newBytes(readVarInt2);
                    byteBuffer.get(newBytes3, 0, readVarInt2);
                    return ValueClob.createSmall(newBytes3, DataUtils.readVarLong(byteBuffer));
                }
                if (readVarInt2 == -3) {
                    return new ValueClob(readLobDataDatabase(byteBuffer), DataUtils.readVarLong(byteBuffer), DataUtils.readVarLong(byteBuffer));
                }
                throw DbException.get(ErrorCode.FILE_CORRUPTED_1, "lob type: " + readVarInt2);
            case 17:
                if (typeInfo != null) {
                    TypeInfo typeInfo2 = (TypeInfo) typeInfo.getExtTypeInfo();
                    return ValueArray.get(typeInfo2, readArrayElements(byteBuffer, typeInfo2), this.provider);
                }
                return ValueArray.get(readArrayElements(byteBuffer, null), this.provider);
            case 19:
                return ValueJavaObject.getNoCopy(readVarBytes(byteBuffer));
            case 20:
                return ValueUuid.get(byteBuffer.getLong(), byteBuffer.getLong());
            case 21:
                return ValueChar.get(DataUtils.readString(byteBuffer));
            case 22:
                return ValueGeometry.get(readVarBytes(byteBuffer));
            case 24:
                return ValueTimestampTimeZone.fromDateValueAndNanos(DataUtils.readVarLong(byteBuffer), readTimestampTime(byteBuffer), DataUtils.readVarInt(byteBuffer) * 60);
            case 25:
                int readVarInt3 = DataUtils.readVarInt(byteBuffer);
                if (typeInfo != null) {
                    return ((ExtTypeInfoEnum) typeInfo.getExtTypeInfo()).getValue(readVarInt3, this.provider);
                }
                return ValueInteger.get(readVarInt3);
            case 26:
                int i4 = byteBuffer.get();
                boolean z = i4 < 0;
                if (z) {
                    i4 = (i4 ^ (-1)) == true ? 1 : 0;
                }
                return ValueInterval.from(IntervalQualifier.valueOf(i4), z, DataUtils.readVarLong(byteBuffer), i4 < 5 ? 0L : DataUtils.readVarLong(byteBuffer));
            case 27:
                int readVarInt4 = DataUtils.readVarInt(byteBuffer);
                Value[] valueArr = new Value[readVarInt4];
                if (typeInfo != null) {
                    Iterator<Map.Entry<String, TypeInfo>> it = ((ExtTypeInfoRow) typeInfo.getExtTypeInfo()).getFields().iterator();
                    for (int i5 = 0; i5 < readVarInt4; i5++) {
                        valueArr[i5] = readValue(byteBuffer, it.next().getValue());
                    }
                    return ValueRow.get(typeInfo, valueArr);
                }
                TypeInfo[] columnTypes = this.rowFactory.getColumnTypes();
                for (int i6 = 0; i6 < readVarInt4; i6++) {
                    valueArr[i6] = readValue(byteBuffer, columnTypes[i6]);
                }
                return ValueRow.get(valueArr);
            case 56:
                return ValueNumeric.ZERO;
            case 57:
                return ValueNumeric.ONE;
            case 58:
                return ValueNumeric.get(BigDecimal.valueOf(DataUtils.readVarLong(byteBuffer)));
            case 59:
                return ValueNumeric.get(BigDecimal.valueOf(DataUtils.readVarLong(byteBuffer), DataUtils.readVarInt(byteBuffer)));
            case 60:
                return ValueDouble.ZERO;
            case 61:
                return ValueDouble.ONE;
            case 62:
                return ValueReal.ZERO;
            case 63:
                return ValueReal.ONE;
            case 64:
                return ValueBoolean.FALSE;
            case 65:
                return ValueBoolean.TRUE;
            case 66:
                return ValueInteger.get(-DataUtils.readVarInt(byteBuffer));
            case 67:
                return ValueBigint.get(-DataUtils.readVarLong(byteBuffer));
            case 134:
                return ValueJson.getInternal(readVarBytes(byteBuffer));
            case 135:
                return ValueTimestampTimeZone.fromDateValueAndNanos(DataUtils.readVarLong(byteBuffer), readTimestampTime(byteBuffer), readTimeZone(byteBuffer));
            case 136:
                return ValueTimeTimeZone.fromNanos((DataUtils.readVarInt(byteBuffer) * DateTimeUtils.NANOS_PER_SECOND) + DataUtils.readVarInt(byteBuffer), readTimeZone(byteBuffer));
            case 137:
                return ValueBinary.getNoCopy(readVarBytes(byteBuffer));
            case 138:
                int readVarInt5 = DataUtils.readVarInt(byteBuffer);
                int readVarInt6 = DataUtils.readVarInt(byteBuffer);
                switch (readVarInt6) {
                    case LobStorageFrontend.TABLE_RESULT /* -3 */:
                        return ValueDecfloat.NEGATIVE_INFINITY;
                    case -2:
                        return ValueDecfloat.POSITIVE_INFINITY;
                    case -1:
                        return ValueDecfloat.NAN;
                    default:
                        byte[] newBytes4 = Utils.newBytes(readVarInt6);
                        byteBuffer.get(newBytes4, 0, readVarInt6);
                        return ValueDecfloat.get(new BigDecimal(new BigInteger(newBytes4), readVarInt5));
                }
        }
    }

    private LobDataDatabase readLobDataDatabase(ByteBuffer byteBuffer) {
        return new LobDataDatabase(this.handler, DataUtils.readVarInt(byteBuffer), DataUtils.readVarLong(byteBuffer));
    }

    private Value[] readArrayElements(ByteBuffer byteBuffer, TypeInfo typeInfo) {
        int readVarInt = DataUtils.readVarInt(byteBuffer);
        Value[] valueArr = new Value[readVarInt];
        for (int i = 0; i < readVarInt; i++) {
            valueArr[i] = readValue(byteBuffer, typeInfo);
        }
        return valueArr;
    }

    private static byte[] readVarBytes(ByteBuffer byteBuffer) {
        int readVarInt = DataUtils.readVarInt(byteBuffer);
        byte[] newBytes = Utils.newBytes(readVarInt);
        byteBuffer.get(newBytes, 0, readVarInt);
        return newBytes;
    }

    private static long readTimestampTime(ByteBuffer byteBuffer) {
        return (DataUtils.readVarLong(byteBuffer) * 1000000) + DataUtils.readVarInt(byteBuffer);
    }

    private static int readTimeZone(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get();
        if (b == Byte.MAX_VALUE) {
            return DataUtils.readVarInt(byteBuffer);
        }
        if (b == Byte.MIN_VALUE) {
            return -DataUtils.readVarInt(byteBuffer);
        }
        return b * JdkLoggerFormatter.LOG_LEVEL_WARN;
    }

    @Override // org.h2.mvstore.type.BasicDataType, java.util.Comparator
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ValueDataType)) {
            return false;
        }
        ValueDataType valueDataType = (ValueDataType) obj;
        if (this.compareMode.equals(valueDataType.compareMode)) {
            return Arrays.equals(this.sortTypes, valueDataType.sortTypes) && Arrays.equals(this.rowFactory == null ? null : this.rowFactory.getIndexes(), valueDataType.rowFactory == null ? null : valueDataType.rowFactory.getIndexes());
        }
        return false;
    }

    @Override // org.h2.mvstore.type.BasicDataType
    public int hashCode() {
        return ((super.hashCode() ^ Arrays.hashCode(this.rowFactory == null ? null : this.rowFactory.getIndexes())) ^ this.compareMode.hashCode()) ^ Arrays.hashCode(this.sortTypes);
    }

    @Override // org.h2.mvstore.type.StatefulDataType
    public void save(WriteBuffer writeBuffer, MetaType<Database> metaType) {
        writeIntArray(writeBuffer, this.sortTypes);
        writeBuffer.putVarInt(this.rowFactory == null ? 0 : this.rowFactory.getColumnCount());
        writeIntArray(writeBuffer, this.rowFactory == null ? null : this.rowFactory.getIndexes());
        writeBuffer.put((this.rowFactory == null || this.rowFactory.getRowDataType().isStoreKeys()) ? (byte) 1 : (byte) 0);
    }

    private static void writeIntArray(WriteBuffer writeBuffer, int[] iArr) {
        if (iArr == null) {
            writeBuffer.putVarInt(0);
            return;
        }
        writeBuffer.putVarInt(iArr.length + 1);
        for (int i : iArr) {
            writeBuffer.putVarInt(i);
        }
    }

    @Override // org.h2.mvstore.type.StatefulDataType
    /* renamed from: getFactory, reason: merged with bridge method [inline-methods] */
    public StatefulDataType.Factory<Database> getFactory2() {
        return FACTORY;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/ValueDataType$Factory.class */
    public static final class Factory implements StatefulDataType.Factory<Database> {
        @Override // org.h2.mvstore.type.StatefulDataType.Factory
        public DataType<?> create(ByteBuffer byteBuffer, MetaType<Database> metaType, Database database) {
            int[] readIntArray = readIntArray(byteBuffer);
            int readVarInt = DataUtils.readVarInt(byteBuffer);
            int[] readIntArray2 = readIntArray(byteBuffer);
            boolean z = byteBuffer.get() != 0;
            CompareMode compareMode = database == null ? CompareMode.getInstance(null, 0) : database.getCompareMode();
            if (database == null) {
                return new ValueDataType();
            }
            if (readIntArray == null) {
                return new ValueDataType(database, null);
            }
            return RowFactory.getDefaultRowFactory().createRowFactory(database, compareMode, database, readIntArray, readIntArray2, null, readVarInt, z).getRowDataType();
        }

        private static int[] readIntArray(ByteBuffer byteBuffer) {
            int readVarInt = DataUtils.readVarInt(byteBuffer) - 1;
            if (readVarInt < 0) {
                return null;
            }
            int[] iArr = new int[readVarInt];
            for (int i = 0; i < iArr.length; i++) {
                iArr[i] = DataUtils.readVarInt(byteBuffer);
            }
            return iArr;
        }
    }
}
