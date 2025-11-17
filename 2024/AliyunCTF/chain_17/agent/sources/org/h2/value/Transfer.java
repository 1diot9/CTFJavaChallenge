package org.h2.value;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import org.h2.api.ErrorCode;
import org.h2.api.IntervalQualifier;
import org.h2.engine.Session;
import org.h2.message.DbException;
import org.h2.security.SHA256;
import org.h2.store.Data;
import org.h2.store.DataReader;
import org.h2.util.Bits;
import org.h2.util.DateTimeUtils;
import org.h2.util.IOUtils;
import org.h2.util.MathUtils;
import org.h2.util.NetUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.lob.LobData;
import org.h2.value.lob.LobDataDatabase;
import org.h2.value.lob.LobDataFetchOnDemand;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/Transfer.class */
public final class Transfer {
    private static final int BUFFER_SIZE = 65536;
    private static final int LOB_MAGIC = 4660;
    private static final int LOB_MAC_SALT_LENGTH = 16;
    private static final int NULL = 0;
    private static final int BOOLEAN = 1;
    private static final int TINYINT = 2;
    private static final int SMALLINT = 3;
    private static final int INTEGER = 4;
    private static final int BIGINT = 5;
    private static final int NUMERIC = 6;
    private static final int DOUBLE = 7;
    private static final int REAL = 8;
    private static final int TIME = 9;
    private static final int DATE = 10;
    private static final int TIMESTAMP = 11;
    private static final int VARBINARY = 12;
    private static final int VARCHAR = 13;
    private static final int VARCHAR_IGNORECASE = 14;
    private static final int BLOB = 15;
    private static final int CLOB = 16;
    private static final int ARRAY = 17;
    private static final int JAVA_OBJECT = 19;
    private static final int UUID = 20;
    private static final int CHAR = 21;
    private static final int GEOMETRY = 22;
    private static final int TIMESTAMP_TZ = 24;
    private static final int ENUM = 25;
    private static final int INTERVAL = 26;
    private static final int ROW = 27;
    private static final int JSON = 28;
    private static final int TIME_TZ = 29;
    private static final int BINARY = 30;
    private static final int DECFLOAT = 31;
    private static final int[] VALUE_TO_TI = new int[43];
    private static final int[] TI_TO_VALUE = new int[45];
    private final ReentrantLock lock = new ReentrantLock();
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Session session;
    private boolean ssl;
    private int version;
    private byte[] lobMacSalt;

    static {
        addType(-1, -1);
        addType(0, 0);
        addType(1, 8);
        addType(2, 9);
        addType(3, 10);
        addType(4, 11);
        addType(5, 12);
        addType(6, 13);
        addType(7, 15);
        addType(8, 14);
        addType(9, 18);
        addType(10, 17);
        addType(11, 20);
        addType(12, 6);
        addType(13, 2);
        addType(14, 4);
        addType(15, 7);
        addType(16, 3);
        addType(17, 40);
        addType(19, 35);
        addType(20, 39);
        addType(21, 1);
        addType(22, 37);
        addType(24, 21);
        addType(25, 36);
        addType(26, 22);
        addType(27, 23);
        addType(28, 24);
        addType(29, 25);
        addType(30, 26);
        addType(31, 27);
        addType(32, 28);
        addType(33, 29);
        addType(34, 30);
        addType(35, 31);
        addType(36, 32);
        addType(37, 33);
        addType(38, 34);
        addType(39, 41);
        addType(40, 38);
        addType(41, 19);
        addType(42, 5);
        addType(43, 16);
    }

    private static void addType(int i, int i2) {
        VALUE_TO_TI[i2 + 1] = i;
        TI_TO_VALUE[i + 1] = i2;
    }

    public Transfer(Session session, Socket socket) {
        this.session = session;
        this.socket = socket;
    }

    private void lock() {
        this.lock.lock();
    }

    private void unlock() {
        this.lock.unlock();
    }

    public void init() throws IOException {
        lock();
        try {
            if (this.socket != null) {
                this.in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream(), 65536));
                this.out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream(), 65536));
            }
        } finally {
            unlock();
        }
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    public Transfer writeBoolean(boolean z) throws IOException {
        this.out.writeByte((byte) (z ? 1 : 0));
        return this;
    }

    public boolean readBoolean() throws IOException {
        return this.in.readByte() != 0;
    }

    public Transfer writeByte(byte b) throws IOException {
        this.out.writeByte(b);
        return this;
    }

    public byte readByte() throws IOException {
        return this.in.readByte();
    }

    private Transfer writeShort(short s) throws IOException {
        this.out.writeShort(s);
        return this;
    }

    private short readShort() throws IOException {
        return this.in.readShort();
    }

    public Transfer writeInt(int i) throws IOException {
        this.out.writeInt(i);
        return this;
    }

    public int readInt() throws IOException {
        return this.in.readInt();
    }

    public Transfer writeLong(long j) throws IOException {
        this.out.writeLong(j);
        return this;
    }

    public long readLong() throws IOException {
        return this.in.readLong();
    }

    private Transfer writeDouble(double d) throws IOException {
        this.out.writeDouble(d);
        return this;
    }

    private Transfer writeFloat(float f) throws IOException {
        this.out.writeFloat(f);
        return this;
    }

    private double readDouble() throws IOException {
        return this.in.readDouble();
    }

    private float readFloat() throws IOException {
        return this.in.readFloat();
    }

    public Transfer writeString(String str) throws IOException {
        if (str == null) {
            this.out.writeInt(-1);
        } else {
            this.out.writeInt(str.length());
            this.out.writeChars(str);
        }
        return this;
    }

    public String readString() throws IOException {
        int readInt = this.in.readInt();
        if (readInt == -1) {
            return null;
        }
        StringBuilder sb = new StringBuilder(readInt);
        for (int i = 0; i < readInt; i++) {
            sb.append(this.in.readChar());
        }
        return StringUtils.cache(sb.toString());
    }

    public Transfer writeBytes(byte[] bArr) throws IOException {
        if (bArr == null) {
            writeInt(-1);
        } else {
            writeInt(bArr.length);
            this.out.write(bArr);
        }
        return this;
    }

    public Transfer writeBytes(byte[] bArr, int i, int i2) throws IOException {
        this.out.write(bArr, i, i2);
        return this;
    }

    public byte[] readBytes() throws IOException {
        int readInt = readInt();
        if (readInt == -1) {
            return null;
        }
        byte[] newBytes = Utils.newBytes(readInt);
        this.in.readFully(newBytes);
        return newBytes;
    }

    public void readBytes(byte[] bArr, int i, int i2) throws IOException {
        this.in.readFully(bArr, i, i2);
    }

    public void close() {
        lock();
        try {
            if (this.socket != null) {
                try {
                    try {
                        if (this.out != null) {
                            this.out.flush();
                        }
                        this.socket.close();
                        this.socket = null;
                    } catch (Throwable th) {
                        this.socket = null;
                        throw th;
                    }
                } catch (IOException e) {
                    DbException.traceThrowable(e);
                    this.socket = null;
                }
            }
        } finally {
            unlock();
        }
    }

    public Transfer writeTypeInfo(TypeInfo typeInfo) throws IOException {
        if (this.version >= 20) {
            writeTypeInfo20(typeInfo);
        } else {
            writeTypeInfo19(typeInfo);
        }
        return this;
    }

    private void writeTypeInfo20(TypeInfo typeInfo) throws IOException {
        int valueType = typeInfo.getValueType();
        writeInt(VALUE_TO_TI[valueType + 1]);
        switch (valueType) {
            case -1:
            case 0:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 17:
            case 39:
                return;
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
            case 16:
            case 35:
            case 38:
                writeInt((int) typeInfo.getDeclaredPrecision());
                return;
            case 3:
            case 7:
                writeLong(typeInfo.getDeclaredPrecision());
                return;
            case 13:
                writeInt((int) typeInfo.getDeclaredPrecision());
                writeInt(typeInfo.getDeclaredScale());
                writeBoolean(typeInfo.getExtTypeInfo() != null);
                return;
            case 14:
            case 15:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 28:
            case 29:
            case 30:
            case 32:
                writeBytePrecisionWithDefault(typeInfo.getDeclaredPrecision());
                return;
            case 18:
            case 19:
            case 20:
            case 21:
                writeByteScaleWithDefault(typeInfo.getDeclaredScale());
                return;
            case 27:
            case 31:
            case 33:
            case 34:
                writeBytePrecisionWithDefault(typeInfo.getDeclaredPrecision());
                writeByteScaleWithDefault(typeInfo.getDeclaredScale());
                return;
            case 36:
                writeTypeInfoEnum(typeInfo);
                return;
            case 37:
                writeTypeInfoGeometry(typeInfo);
                return;
            case 40:
                writeInt((int) typeInfo.getDeclaredPrecision());
                writeTypeInfo((TypeInfo) typeInfo.getExtTypeInfo());
                return;
            case 41:
                writeTypeInfoRow(typeInfo);
                return;
            default:
                throw DbException.getUnsupportedException("value type " + valueType);
        }
    }

    private void writeBytePrecisionWithDefault(long j) throws IOException {
        writeByte(j >= 0 ? (byte) j : (byte) -1);
    }

    private void writeByteScaleWithDefault(int i) throws IOException {
        writeByte(i >= 0 ? (byte) i : (byte) -1);
    }

    private void writeTypeInfoEnum(TypeInfo typeInfo) throws IOException {
        ExtTypeInfoEnum extTypeInfoEnum = (ExtTypeInfoEnum) typeInfo.getExtTypeInfo();
        if (extTypeInfoEnum != null) {
            int count = extTypeInfoEnum.getCount();
            writeInt(count);
            for (int i = 0; i < count; i++) {
                writeString(extTypeInfoEnum.getEnumerator(i));
            }
            return;
        }
        writeInt(0);
    }

    private void writeTypeInfoGeometry(TypeInfo typeInfo) throws IOException {
        ExtTypeInfoGeometry extTypeInfoGeometry = (ExtTypeInfoGeometry) typeInfo.getExtTypeInfo();
        if (extTypeInfoGeometry == null) {
            writeByte((byte) 0);
            return;
        }
        int type = extTypeInfoGeometry.getType();
        Integer srid = extTypeInfoGeometry.getSrid();
        if (type == 0) {
            if (srid == null) {
                writeByte((byte) 0);
                return;
            } else {
                writeByte((byte) 2);
                writeInt(srid.intValue());
                return;
            }
        }
        if (srid == null) {
            writeByte((byte) 1);
            writeShort((short) type);
        } else {
            writeByte((byte) 3);
            writeShort((short) type);
            writeInt(srid.intValue());
        }
    }

    private void writeTypeInfoRow(TypeInfo typeInfo) throws IOException {
        Set<Map.Entry<String, TypeInfo>> fields = ((ExtTypeInfoRow) typeInfo.getExtTypeInfo()).getFields();
        writeInt(fields.size());
        for (Map.Entry<String, TypeInfo> entry : fields) {
            writeString(entry.getKey()).writeTypeInfo(entry.getValue());
        }
    }

    private void writeTypeInfo19(TypeInfo typeInfo) throws IOException {
        int valueType = typeInfo.getValueType();
        switch (valueType) {
            case 5:
                valueType = 6;
                break;
            case 16:
                valueType = 13;
                break;
        }
        writeInt(VALUE_TO_TI[valueType + 1]).writeLong(typeInfo.getPrecision()).writeInt(typeInfo.getScale());
    }

    public TypeInfo readTypeInfo() throws IOException {
        if (this.version >= 20) {
            return readTypeInfo20();
        }
        return readTypeInfo19();
    }

    private TypeInfo readTypeInfo20() throws IOException {
        int i = TI_TO_VALUE[readInt() + 1];
        long j = -1;
        int i2 = -1;
        ExtTypeInfo extTypeInfo = null;
        switch (i) {
            case -1:
            case 0:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 17:
            case 39:
                break;
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
            case 16:
            case 35:
            case 38:
                j = readInt();
                break;
            case 3:
            case 7:
                j = readLong();
                break;
            case 13:
                j = readInt();
                i2 = readInt();
                if (readBoolean()) {
                    extTypeInfo = ExtTypeInfoNumeric.DECIMAL;
                    break;
                }
                break;
            case 14:
            case 15:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 28:
            case 29:
            case 30:
            case 32:
                j = readByte();
                break;
            case 18:
            case 19:
            case 20:
            case 21:
                i2 = readByte();
                break;
            case 27:
            case 31:
            case 33:
            case 34:
                j = readByte();
                i2 = readByte();
                break;
            case 36:
                extTypeInfo = readTypeInfoEnum();
                break;
            case 37:
                extTypeInfo = readTypeInfoGeometry();
                break;
            case 40:
                j = readInt();
                extTypeInfo = readTypeInfo();
                break;
            case 41:
                extTypeInfo = readTypeInfoRow();
                break;
            default:
                throw DbException.getUnsupportedException("value type " + i);
        }
        return TypeInfo.getTypeInfo(i, j, i2, extTypeInfo);
    }

    private ExtTypeInfo readTypeInfoEnum() throws IOException {
        ExtTypeInfoEnum extTypeInfoEnum;
        int readInt = readInt();
        if (readInt > 0) {
            String[] strArr = new String[readInt];
            for (int i = 0; i < readInt; i++) {
                strArr[i] = readString();
            }
            extTypeInfoEnum = new ExtTypeInfoEnum(strArr);
        } else {
            extTypeInfoEnum = null;
        }
        return extTypeInfoEnum;
    }

    private ExtTypeInfo readTypeInfoGeometry() throws IOException {
        ExtTypeInfoGeometry extTypeInfoGeometry;
        byte readByte = readByte();
        switch (readByte) {
            case 0:
                extTypeInfoGeometry = null;
                break;
            case 1:
                extTypeInfoGeometry = new ExtTypeInfoGeometry(readShort(), null);
                break;
            case 2:
                extTypeInfoGeometry = new ExtTypeInfoGeometry(0, Integer.valueOf(readInt()));
                break;
            case 3:
                extTypeInfoGeometry = new ExtTypeInfoGeometry(readShort(), Integer.valueOf(readInt()));
                break;
            default:
                throw DbException.getUnsupportedException("GEOMETRY type encoding " + ((int) readByte));
        }
        return extTypeInfoGeometry;
    }

    private ExtTypeInfo readTypeInfoRow() throws IOException {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        int readInt = readInt();
        for (int i = 0; i < readInt; i++) {
            String readString = readString();
            if (linkedHashMap.putIfAbsent(readString, readTypeInfo()) != null) {
                throw DbException.get(ErrorCode.DUPLICATE_COLUMN_NAME_1, readString);
            }
        }
        return new ExtTypeInfoRow((LinkedHashMap<String, TypeInfo>) linkedHashMap);
    }

    private TypeInfo readTypeInfo19() throws IOException {
        return TypeInfo.getTypeInfo(TI_TO_VALUE[readInt() + 1], readLong(), readInt(), null);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0006. Please report as an issue. */
    public void writeValue(Value value) throws IOException {
        int valueType = value.getValueType();
        switch (valueType) {
            case 0:
                writeInt(0);
                return;
            case 1:
                writeInt(21);
                writeString(value.getString());
                return;
            case 2:
                writeInt(13);
                writeString(value.getString());
                return;
            case 3:
                writeInt(16);
                ValueClob valueClob = (ValueClob) value;
                LobData lobData = valueClob.getLobData();
                long charLength = valueClob.charLength();
                if (!(lobData instanceof LobDataDatabase)) {
                    if (charLength < 0) {
                        throw DbException.get(ErrorCode.CONNECTION_BROKEN_1, "length=" + charLength);
                    }
                    writeLong(charLength);
                    Data.copyString(valueClob.getReader(), this.out);
                    writeInt(LOB_MAGIC);
                    return;
                }
                LobDataDatabase lobDataDatabase = (LobDataDatabase) lobData;
                writeLong(-1L);
                writeInt(lobDataDatabase.getTableId());
                writeLong(lobDataDatabase.getLobId());
                writeBytes(calculateLobMac(lobDataDatabase.getLobId()));
                if (this.version >= 20) {
                    writeLong(valueClob.octetLength());
                }
                writeLong(charLength);
                return;
            case 4:
                writeInt(14);
                writeString(value.getString());
                return;
            case 5:
                if (this.version >= 20) {
                    writeInt(30);
                    writeBytes(value.getBytesNoCopy());
                    return;
                }
                writeInt(12);
                writeBytes(value.getBytesNoCopy());
                return;
            case 6:
                writeInt(12);
                writeBytes(value.getBytesNoCopy());
                return;
            case 7:
                writeInt(15);
                ValueBlob valueBlob = (ValueBlob) value;
                LobData lobData2 = valueBlob.getLobData();
                long octetLength = valueBlob.octetLength();
                if (lobData2 instanceof LobDataDatabase) {
                    LobDataDatabase lobDataDatabase2 = (LobDataDatabase) lobData2;
                    writeLong(-1L);
                    writeInt(lobDataDatabase2.getTableId());
                    writeLong(lobDataDatabase2.getLobId());
                    writeBytes(calculateLobMac(lobDataDatabase2.getLobId()));
                    writeLong(octetLength);
                    return;
                }
                if (octetLength < 0) {
                    throw DbException.get(ErrorCode.CONNECTION_BROKEN_1, "length=" + octetLength);
                }
                writeLong(octetLength);
                long copyAndCloseInput = IOUtils.copyAndCloseInput(valueBlob.getInputStream(), this.out);
                if (copyAndCloseInput != octetLength) {
                    throw DbException.get(ErrorCode.CONNECTION_BROKEN_1, "length:" + octetLength + " written:" + copyAndCloseInput);
                }
                writeInt(LOB_MAGIC);
                return;
            case 8:
                writeInt(1);
                writeBoolean(value.getBoolean());
                return;
            case 9:
                writeInt(2);
                writeByte(value.getByte());
                return;
            case 10:
                writeInt(3);
                if (this.version >= 20) {
                    writeShort(value.getShort());
                    return;
                } else {
                    writeInt(value.getShort());
                    return;
                }
            case 11:
                writeInt(4);
                writeInt(value.getInt());
                return;
            case 12:
                writeInt(5);
                writeLong(value.getLong());
                return;
            case 13:
                writeInt(6);
                writeString(value.getString());
                return;
            case 14:
                writeInt(8);
                writeFloat(value.getFloat());
                return;
            case 15:
                writeInt(7);
                writeDouble(value.getDouble());
                return;
            case 16:
                if (this.version >= 20) {
                    writeInt(31);
                    writeString(value.getString());
                    return;
                }
                writeInt(6);
                writeString(value.getString());
                return;
            case 17:
                writeInt(10);
                writeLong(((ValueDate) value).getDateValue());
                return;
            case 18:
                writeInt(9);
                writeLong(((ValueTime) value).getNanos());
                return;
            case 19:
                ValueTimeTimeZone valueTimeTimeZone = (ValueTimeTimeZone) value;
                if (this.version >= 19) {
                    writeInt(29);
                    writeLong(valueTimeTimeZone.getNanos());
                    writeInt(valueTimeTimeZone.getTimeZoneOffsetSeconds());
                    return;
                } else {
                    writeInt(9);
                    writeLong(DateTimeUtils.normalizeNanosOfDay(valueTimeTimeZone.getNanos() + ((valueTimeTimeZone.getTimeZoneOffsetSeconds() - (this.session.isRemote() ? DateTimeUtils.currentTimestamp(DateTimeUtils.getTimeZone()) : this.session.currentTimestamp()).getTimeZoneOffsetSeconds()) * DateTimeUtils.NANOS_PER_DAY)));
                    return;
                }
            case 20:
                writeInt(11);
                ValueTimestamp valueTimestamp = (ValueTimestamp) value;
                writeLong(valueTimestamp.getDateValue());
                writeLong(valueTimestamp.getTimeNanos());
                return;
            case 21:
                writeInt(24);
                ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) value;
                writeLong(valueTimestampTimeZone.getDateValue());
                writeLong(valueTimestampTimeZone.getTimeNanos());
                int timeZoneOffsetSeconds = valueTimestampTimeZone.getTimeZoneOffsetSeconds();
                writeInt(this.version >= 19 ? timeZoneOffsetSeconds : timeZoneOffsetSeconds / 60);
                return;
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
                if (this.version >= 18) {
                    ValueInterval valueInterval = (ValueInterval) value;
                    int i = valueType - 22;
                    if (valueInterval.isNegative()) {
                        i ^= -1;
                    }
                    writeInt(26);
                    writeByte((byte) i);
                    writeLong(valueInterval.getLeading());
                    return;
                }
                writeInt(13);
                writeString(value.getString());
                return;
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                if (this.version >= 18) {
                    ValueInterval valueInterval2 = (ValueInterval) value;
                    int i2 = valueType - 22;
                    if (valueInterval2.isNegative()) {
                        i2 ^= -1;
                    }
                    writeInt(26);
                    writeByte((byte) i2);
                    writeLong(valueInterval2.getLeading());
                    writeLong(valueInterval2.getRemaining());
                    return;
                }
                writeInt(13);
                writeString(value.getString());
                return;
            case 35:
                writeInt(19);
                writeBytes(value.getBytesNoCopy());
                return;
            case 36:
                writeInt(25);
                writeInt(value.getInt());
                if (this.version < 20) {
                    writeString(value.getString());
                    return;
                }
                return;
            case 37:
                writeInt(22);
                writeBytes(value.getBytesNoCopy());
                return;
            case 38:
                writeInt(28);
                writeBytes(value.getBytesNoCopy());
                return;
            case 39:
                writeInt(20);
                ValueUuid valueUuid = (ValueUuid) value;
                writeLong(valueUuid.getHigh());
                writeLong(valueUuid.getLow());
                return;
            case 40:
                writeInt(17);
                Value[] list = ((ValueArray) value).getList();
                writeInt(list.length);
                for (Value value2 : list) {
                    writeValue(value2);
                }
                return;
            case 41:
                writeInt(this.version >= 18 ? 27 : 17);
                Value[] list2 = ((ValueRow) value).getList();
                writeInt(list2.length);
                for (Value value3 : list2) {
                    writeValue(value3);
                }
                return;
            default:
                throw DbException.get(ErrorCode.CONNECTION_BROKEN_1, "type=" + valueType);
        }
    }

    public Value readValue(TypeInfo typeInfo) throws IOException {
        int readInt = readInt();
        switch (readInt) {
            case 0:
                return ValueNull.INSTANCE;
            case 1:
                return ValueBoolean.get(readBoolean());
            case 2:
                return ValueTinyint.get(readByte());
            case 3:
                if (this.version >= 20) {
                    return ValueSmallint.get(readShort());
                }
                return ValueSmallint.get((short) readInt());
            case 4:
                return ValueInteger.get(readInt());
            case 5:
                return ValueBigint.get(readLong());
            case 6:
                return ValueNumeric.get(new BigDecimal(readString()));
            case 7:
                return ValueDouble.get(readDouble());
            case 8:
                return ValueReal.get(readFloat());
            case 9:
                return ValueTime.fromNanos(readLong());
            case 10:
                return ValueDate.fromDateValue(readLong());
            case 11:
                return ValueTimestamp.fromDateValueAndNanos(readLong(), readLong());
            case 12:
                return ValueVarbinary.getNoCopy(readBytes());
            case 13:
                return ValueVarchar.get(readString());
            case 14:
                return ValueVarcharIgnoreCase.get(readString());
            case 15:
                long readLong = readLong();
                if (readLong == -1) {
                    return new ValueBlob(new LobDataFetchOnDemand(this.session.getDataHandler(), readInt(), readLong(), readBytes()), readLong());
                }
                ValueBlob createBlob = this.session.getDataHandler().getLobStorage().createBlob(this.in, readLong);
                int readInt2 = readInt();
                if (readInt2 != LOB_MAGIC) {
                    throw DbException.get(ErrorCode.CONNECTION_BROKEN_1, "magic=" + readInt2);
                }
                return createBlob;
            case 16:
                long readLong2 = readLong();
                if (readLong2 == -1) {
                    return new ValueClob(new LobDataFetchOnDemand(this.session.getDataHandler(), readInt(), readLong(), readBytes()), this.version >= 20 ? readLong() : -1L, readLong());
                }
                if (readLong2 < 0) {
                    throw DbException.get(ErrorCode.CONNECTION_BROKEN_1, "length=" + readLong2);
                }
                ValueClob createClob = this.session.getDataHandler().getLobStorage().createClob(new DataReader(this.in), readLong2);
                int readInt3 = readInt();
                if (readInt3 != LOB_MAGIC) {
                    throw DbException.get(ErrorCode.CONNECTION_BROKEN_1, "magic=" + readInt3);
                }
                return createClob;
            case 17:
                int readInt4 = readInt();
                if (readInt4 < 0) {
                    readInt4 ^= -1;
                    readString();
                }
                if (typeInfo != null) {
                    TypeInfo typeInfo2 = (TypeInfo) typeInfo.getExtTypeInfo();
                    return ValueArray.get(typeInfo2, readArrayElements(readInt4, typeInfo2), this.session);
                }
                return ValueArray.get(readArrayElements(readInt4, null), this.session);
            case 18:
            case 23:
            default:
                throw DbException.get(ErrorCode.CONNECTION_BROKEN_1, "type=" + readInt);
            case 19:
                return ValueJavaObject.getNoCopy(readBytes());
            case 20:
                return ValueUuid.get(readLong(), readLong());
            case 21:
                return ValueChar.get(readString());
            case 22:
                return ValueGeometry.get(readBytes());
            case 24:
                long readLong3 = readLong();
                long readLong4 = readLong();
                int readInt5 = readInt();
                return ValueTimestampTimeZone.fromDateValueAndNanos(readLong3, readLong4, this.version >= 19 ? readInt5 : readInt5 * 60);
            case 25:
                int readInt6 = readInt();
                if (this.version >= 20) {
                    return ((ExtTypeInfoEnum) typeInfo.getExtTypeInfo()).getValue(readInt6, this.session);
                }
                return ValueEnumBase.get(readString(), readInt6);
            case 26:
                byte readByte = readByte();
                boolean z = readByte < 0;
                if (z) {
                    readByte = (readByte ^ (-1)) == true ? 1 : 0;
                }
                return ValueInterval.from(IntervalQualifier.valueOf(readByte), z, readLong(), readByte < 5 ? 0L : readLong());
            case 27:
                int readInt7 = readInt();
                Value[] valueArr = new Value[readInt7];
                if (typeInfo != null) {
                    Iterator<Map.Entry<String, TypeInfo>> it = ((ExtTypeInfoRow) typeInfo.getExtTypeInfo()).getFields().iterator();
                    for (int i = 0; i < readInt7; i++) {
                        valueArr[i] = readValue(it.next().getValue());
                    }
                    return ValueRow.get(typeInfo, valueArr);
                }
                for (int i2 = 0; i2 < readInt7; i2++) {
                    valueArr[i2] = readValue(null);
                }
                return ValueRow.get(valueArr);
            case 28:
                return ValueJson.fromJson(readBytes());
            case 29:
                return ValueTimeTimeZone.fromNanos(readLong(), readInt());
            case 30:
                return ValueBinary.getNoCopy(readBytes());
            case 31:
                String readString = readString();
                boolean z2 = -1;
                boolean z3 = z2;
                switch (readString.hashCode()) {
                    case 78043:
                        z3 = z2;
                        if (readString.equals("NaN")) {
                            z3 = 2;
                            break;
                        }
                        break;
                    case 237817416:
                        z3 = z2;
                        if (readString.equals("Infinity")) {
                            z3 = true;
                            break;
                        }
                        break;
                    case 506745205:
                        z3 = z2;
                        if (readString.equals("-Infinity")) {
                            z3 = false;
                            break;
                        }
                        break;
                }
                switch (z3) {
                    case false:
                        return ValueDecfloat.NEGATIVE_INFINITY;
                    case true:
                        return ValueDecfloat.POSITIVE_INFINITY;
                    case true:
                        return ValueDecfloat.NAN;
                    default:
                        return ValueDecfloat.get(new BigDecimal(readString));
                }
        }
    }

    private Value[] readArrayElements(int i, TypeInfo typeInfo) throws IOException {
        Value[] valueArr = new Value[i];
        for (int i2 = 0; i2 < i; i2++) {
            valueArr[i2] = readValue(typeInfo);
        }
        return valueArr;
    }

    public long readRowCount() throws IOException {
        return this.version >= 20 ? readLong() : readInt();
    }

    public Transfer writeRowCount(long j) throws IOException {
        if (this.version >= 20) {
            return writeLong(j);
        }
        return writeInt(j < 2147483647L ? (int) j : Integer.MAX_VALUE);
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setSSL(boolean z) {
        this.ssl = z;
    }

    public Transfer openNewConnection() throws IOException {
        Transfer transfer = new Transfer(null, NetUtils.createSocket(this.socket.getInetAddress(), this.socket.getPort(), this.ssl));
        transfer.setSSL(this.ssl);
        return transfer;
    }

    public void setVersion(int i) {
        this.version = i;
    }

    public int getVersion() {
        return this.version;
    }

    public boolean isClosed() {
        boolean z;
        lock();
        try {
            if (this.socket != null) {
                if (!this.socket.isClosed()) {
                    z = false;
                    return z;
                }
            }
            z = true;
            return z;
        } finally {
            unlock();
        }
    }

    public void verifyLobMac(byte[] bArr, long j) {
        if (!Utils.compareSecure(bArr, calculateLobMac(j))) {
            throw DbException.get(ErrorCode.CONNECTION_BROKEN_1, "Invalid lob hmac; possibly the connection was re-opened internally");
        }
    }

    private byte[] calculateLobMac(long j) {
        if (this.lobMacSalt == null) {
            this.lobMacSalt = MathUtils.secureRandomBytes(16);
        }
        byte[] bArr = new byte[8];
        Bits.writeLong(bArr, 0, j);
        return SHA256.getHashWithSalt(bArr, this.lobMacSalt);
    }
}
