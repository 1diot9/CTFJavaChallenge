package org.h2.server.pg;

import ch.qos.logback.core.spi.AbstractComponentTracker;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.apache.tomcat.util.bcel.Const;
import org.h2.api.ErrorCode;
import org.h2.command.Command;
import org.h2.command.CommandInterface;
import org.h2.engine.Constants;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.engine.SysProperties;
import org.h2.expression.ParameterInterface;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.util.DateTimeUtils;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;
import org.h2.util.Utils10;
import org.h2.value.CaseInsensitiveMap;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBigint;
import org.h2.value.ValueDate;
import org.h2.value.ValueDecfloat;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueNumeric;
import org.h2.value.ValueReal;
import org.h2.value.ValueSmallint;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;
import org.springframework.asm.Opcodes;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/server/pg/PgServerThread.class */
public final class PgServerThread implements Runnable {
    private static final boolean INTEGER_DATE_TYPES = false;
    private final PgServer server;
    private Socket socket;
    private SessionLocal session;
    private boolean stop;
    private DataInputStream dataInRaw;
    private DataInputStream dataIn;
    private OutputStream out;
    private int messageType;
    private DataOutputStream dataOut;
    private Thread thread;
    private boolean initDone;
    private String userName;
    private String databaseName;
    private int processId;
    private CommandInterface activeRequest;
    private static final int MAX_GROUP_SCALE = 4;
    private static final short NUMERIC_POSITIVE = 0;
    private static final short NUMERIC_NEGATIVE = 16384;
    private static final short NUMERIC_NAN = -16384;
    private static final Pattern SHOULD_QUOTE = Pattern.compile(".*[\",\\\\{}].*");
    private static final int[] POWERS10 = {1, 10, 100, 1000, 10000};
    private static final int MAX_GROUP_SIZE = POWERS10[4];
    private static final BigInteger NUMERIC_CHUNK_MULTIPLIER = BigInteger.valueOf(AbstractComponentTracker.LINGERING_TIMEOUT);
    private ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
    private String clientEncoding = SysProperties.PG_DEFAULT_CLIENT_ENCODING;
    private String dateStyle = "ISO, MDY";
    private TimeZoneProvider timeZone = DateTimeUtils.getTimeZone();
    private final HashMap<String, Prepared> prepared = new CaseInsensitiveMap();
    private final HashMap<String, Portal> portals = new CaseInsensitiveMap();
    private final int secret = (int) MathUtils.secureRandomLong();

    private static String pgTimeZone(String str) {
        if (str.startsWith("GMT+")) {
            return convertTimeZone(str, "GMT-");
        }
        if (str.startsWith("GMT-")) {
            return convertTimeZone(str, "GMT+");
        }
        if (str.startsWith("UTC+")) {
            return convertTimeZone(str, "UTC-");
        }
        if (str.startsWith("UTC-")) {
            return convertTimeZone(str, "UTC+");
        }
        return str;
    }

    private static String convertTimeZone(String str, String str2) {
        int length = str.length();
        return new StringBuilder(length).append(str2).append((CharSequence) str, 4, length).toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PgServerThread(Socket socket, PgServer pgServer) {
        this.server = pgServer;
        this.socket = socket;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.server.trace("Connect");
            InputStream inputStream = this.socket.getInputStream();
            this.out = this.socket.getOutputStream();
            this.dataInRaw = new DataInputStream(inputStream);
            while (!this.stop) {
                process();
                this.out.flush();
            }
        } catch (EOFException e) {
        } catch (Exception e2) {
            this.server.traceError(e2);
        } finally {
            this.server.trace("Disconnect");
            close();
        }
    }

    private String readString() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (true) {
            int read = this.dataIn.read();
            if (read > 0) {
                byteArrayOutputStream.write(read);
            } else {
                return Utils10.byteArrayOutputStreamToString(byteArrayOutputStream, getEncoding());
            }
        }
    }

    private int readInt() throws IOException {
        return this.dataIn.readInt();
    }

    private short readShort() throws IOException {
        return this.dataIn.readShort();
    }

    private byte readByte() throws IOException {
        return this.dataIn.readByte();
    }

    private void readFully(byte[] bArr) throws IOException {
        this.dataIn.readFully(bArr);
    }

    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0244  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x024d  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x025d  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0298  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x02c1 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x02ed A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void process() throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 2564
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.server.pg.PgServerThread.process():void");
    }

    private void executeQuery(Prepared prepared, CommandInterface commandInterface, int[] iArr, int i) throws Exception {
        ResultInterface resultInterface = prepared.result;
        if (resultInterface == null) {
            resultInterface = commandInterface.executeQuery(0L, false);
        }
        try {
            if (i == 0) {
                while (resultInterface.next()) {
                    sendDataRow(resultInterface, iArr);
                }
            } else {
                while (i > 0 && resultInterface.next()) {
                    sendDataRow(resultInterface, iArr);
                    i--;
                }
                if (resultInterface.hasNext()) {
                    prepared.result = resultInterface;
                    sendCommandSuspended();
                    return;
                }
            }
            prepared.closeResult();
            sendCommandComplete(commandInterface, 0L);
        } catch (Exception e) {
            prepared.closeResult();
            throw e;
        }
    }

    private String getSQL(String str) {
        String lowerEnglish = StringUtils.toLowerEnglish(str);
        if (lowerEnglish.startsWith("show max_identifier_length")) {
            str = "CALL 63";
        } else if (lowerEnglish.startsWith("set client_encoding to")) {
            str = "set DATESTYLE ISO";
        }
        if (this.server.getTrace()) {
            this.server.trace(str + ";");
        }
        return str;
    }

    private void sendCommandComplete(CommandInterface commandInterface, long j) throws IOException {
        startMessage(67);
        switch (commandInterface.getCommandType()) {
            case 57:
            case 66:
                writeString("SELECT");
                break;
            case 58:
                writeStringPart("DELETE ");
                writeString(Long.toString(j));
                break;
            case 61:
                writeStringPart("INSERT 0 ");
                writeString(Long.toString(j));
                break;
            case 68:
                writeStringPart("UPDATE ");
                writeString(Long.toString(j));
                break;
            case 83:
                writeString("BEGIN");
                break;
            default:
                this.server.trace("check CommandComplete tag for command " + commandInterface);
                writeStringPart("UPDATE ");
                writeString(Long.toString(j));
                break;
        }
        sendMessage();
    }

    private void sendCommandSuspended() throws IOException {
        startMessage(115);
        sendMessage();
    }

    private void sendDataRow(ResultInterface resultInterface, int[] iArr) throws IOException {
        int visibleColumnCount = resultInterface.getVisibleColumnCount();
        startMessage(68);
        writeShort(visibleColumnCount);
        Value[] currentRow = resultInterface.currentRow();
        for (int i = 0; i < visibleColumnCount; i++) {
            int convertType = PgServer.convertType(resultInterface.getColumnType(i));
            writeDataColumn(currentRow[i], convertType, formatAsText(convertType, iArr, i));
        }
        sendMessage();
    }

    private static long toPostgreDays(long j) {
        return DateTimeUtils.absoluteDayFromDateValue(j) - 10957;
    }

    private void writeDataColumn(Value value, int i, boolean z) throws IOException {
        if (value == ValueNull.INSTANCE) {
            writeInt(-1);
            return;
        }
        if (z) {
            switch (i) {
                case 16:
                    writeInt(1);
                    this.dataOut.writeByte(value.getBoolean() ? 116 : 102);
                    return;
                case 17:
                    byte[] bytesNoCopy = value.getBytesNoCopy();
                    int length = bytesNoCopy.length;
                    for (byte b : bytesNoCopy) {
                        if (b < 32 || b > 126) {
                            length += 3;
                        } else if (b == 92) {
                            length++;
                        }
                    }
                    byte[] bArr = new byte[length];
                    int i2 = 0;
                    for (byte b2 : bytesNoCopy) {
                        if (b2 < 32 || b2 > 126) {
                            int i3 = i2;
                            int i4 = i2 + 1;
                            bArr[i3] = 92;
                            int i5 = i4 + 1;
                            bArr[i4] = (byte) (((b2 >>> 6) & 3) + 48);
                            int i6 = i5 + 1;
                            bArr[i5] = (byte) (((b2 >>> 3) & 7) + 48);
                            i2 = i6 + 1;
                            bArr[i6] = (byte) ((b2 & 7) + 48);
                        } else if (b2 == 92) {
                            int i7 = i2;
                            int i8 = i2 + 1;
                            bArr[i7] = 92;
                            i2 = i8 + 1;
                            bArr[i8] = 92;
                        } else {
                            int i9 = i2;
                            i2++;
                            bArr[i9] = b2;
                        }
                    }
                    writeInt(bArr.length);
                    write(bArr);
                    return;
                case PgServer.PG_TYPE_INT2_ARRAY /* 1005 */:
                case 1007:
                case PgServer.PG_TYPE_VARCHAR_ARRAY /* 1015 */:
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byteArrayOutputStream.write(123);
                    Value[] list = ((ValueArray) value).getList();
                    Charset encoding = getEncoding();
                    for (int i10 = 0; i10 < list.length; i10++) {
                        if (i10 > 0) {
                            byteArrayOutputStream.write(44);
                        }
                        String string = list[i10].getString();
                        if (SHOULD_QUOTE.matcher(string).matches()) {
                            ArrayList arrayList = new ArrayList();
                            for (String str : string.split("\\\\")) {
                                arrayList.add(str.replace("\"", "\\\""));
                            }
                            string = "\"" + String.join("\\\\", arrayList) + "\"";
                        }
                        byteArrayOutputStream.write(string.getBytes(encoding));
                    }
                    byteArrayOutputStream.write(125);
                    writeInt(byteArrayOutputStream.size());
                    write(byteArrayOutputStream);
                    return;
                default:
                    byte[] bytes = value.getString().getBytes(getEncoding());
                    writeInt(bytes.length);
                    write(bytes);
                    return;
            }
        }
        switch (i) {
            case 16:
                writeInt(1);
                this.dataOut.writeByte(value.getBoolean() ? 1 : 0);
                return;
            case 17:
                byte[] bytesNoCopy2 = value.getBytesNoCopy();
                writeInt(bytesNoCopy2.length);
                write(bytesNoCopy2);
                return;
            case 20:
                writeInt(8);
                this.dataOut.writeLong(value.getLong());
                return;
            case 21:
                writeInt(2);
                writeShort(value.getShort());
                return;
            case 23:
                writeInt(4);
                writeInt(value.getInt());
                return;
            case PgServer.PG_TYPE_FLOAT4 /* 700 */:
                writeInt(4);
                this.dataOut.writeFloat(value.getFloat());
                return;
            case PgServer.PG_TYPE_FLOAT8 /* 701 */:
                writeInt(8);
                this.dataOut.writeDouble(value.getDouble());
                return;
            case PgServer.PG_TYPE_DATE /* 1082 */:
                writeInt(4);
                writeInt((int) toPostgreDays(((ValueDate) value).getDateValue()));
                return;
            case PgServer.PG_TYPE_TIME /* 1083 */:
                writeTimeBinary(((ValueTime) value).getNanos(), 8);
                return;
            case PgServer.PG_TYPE_TIMESTAMP /* 1114 */:
                ValueTimestamp valueTimestamp = (ValueTimestamp) value;
                writeTimestampBinary(toPostgreDays(valueTimestamp.getDateValue()) * DateTimeUtils.SECONDS_PER_DAY, valueTimestamp.getTimeNanos());
                return;
            case PgServer.PG_TYPE_TIMESTAMPTZ /* 1184 */:
                ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) value;
                long postgreDays = toPostgreDays(valueTimestampTimeZone.getDateValue()) * DateTimeUtils.SECONDS_PER_DAY;
                long timeNanos = valueTimestampTimeZone.getTimeNanos() - (valueTimestampTimeZone.getTimeZoneOffsetSeconds() * DateTimeUtils.NANOS_PER_SECOND);
                if (timeNanos < 0) {
                    postgreDays--;
                    timeNanos += DateTimeUtils.NANOS_PER_DAY;
                }
                writeTimestampBinary(postgreDays, timeNanos);
                return;
            case PgServer.PG_TYPE_TIMETZ /* 1266 */:
                ValueTimeTimeZone valueTimeTimeZone = (ValueTimeTimeZone) value;
                writeTimeBinary(valueTimeTimeZone.getNanos(), 12);
                this.dataOut.writeInt(-valueTimeTimeZone.getTimeZoneOffsetSeconds());
                return;
            case PgServer.PG_TYPE_NUMERIC /* 1700 */:
                writeNumericBinary(value.getBigDecimal());
                return;
            default:
                throw new IllegalStateException("output binary format is undefined");
        }
    }

    private static int divide(BigInteger[] bigIntegerArr, int i) {
        BigInteger[] divideAndRemainder = bigIntegerArr[0].divideAndRemainder(BigInteger.valueOf(i));
        bigIntegerArr[0] = divideAndRemainder[0];
        return divideAndRemainder[1].intValue();
    }

    private void writeNumericBinary(BigDecimal bigDecimal) throws IOException {
        int i = 0;
        ArrayList arrayList = new ArrayList();
        int scale = bigDecimal.scale();
        int signum = bigDecimal.signum();
        if (signum != 0) {
            BigInteger[] bigIntegerArr = {null};
            if (scale < 0) {
                bigIntegerArr[0] = bigDecimal.setScale(0).unscaledValue();
                scale = 0;
            } else {
                bigIntegerArr[0] = bigDecimal.unscaledValue();
            }
            if (signum < 0) {
                bigIntegerArr[0] = bigIntegerArr[0].negate();
            }
            i = ((-scale) / 4) - 1;
            int i2 = 0;
            int i3 = scale % 4;
            if (i3 > 0) {
                i2 = divide(bigIntegerArr, POWERS10[i3]) * POWERS10[4 - i3];
                if (i2 != 0) {
                    i--;
                }
            }
            if (i2 == 0) {
                while (true) {
                    int divide = divide(bigIntegerArr, MAX_GROUP_SIZE);
                    i2 = divide;
                    if (divide != 0) {
                        break;
                    } else {
                        i++;
                    }
                }
            }
            arrayList.add(Integer.valueOf(i2));
            while (bigIntegerArr[0].signum() != 0) {
                arrayList.add(Integer.valueOf(divide(bigIntegerArr, MAX_GROUP_SIZE)));
            }
        }
        int size = arrayList.size();
        if (size + i > 32767 || scale > 32767) {
            throw DbException.get(ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_1, bigDecimal.toString());
        }
        writeInt(8 + (size * 2));
        writeShort(size);
        writeShort(size + i);
        writeShort(signum < 0 ? 16384 : 0);
        writeShort(scale);
        for (int i4 = size - 1; i4 >= 0; i4--) {
            writeShort(((Integer) arrayList.get(i4)).intValue());
        }
    }

    private void writeTimeBinary(long j, int i) throws IOException {
        writeInt(i);
        this.dataOut.writeLong(Double.doubleToLongBits(j * 1.0E-9d));
    }

    private void writeTimestampBinary(long j, long j2) throws IOException {
        writeInt(8);
        this.dataOut.writeLong(Double.doubleToLongBits(j + (j2 * 1.0E-9d)));
    }

    private Charset getEncoding() {
        if ("UNICODE".equals(this.clientEncoding)) {
            return StandardCharsets.UTF_8;
        }
        return Charset.forName(this.clientEncoding);
    }

    private void setParameter(ArrayList<? extends ParameterInterface> arrayList, int i, int i2, int[] iArr) throws IOException {
        Value value;
        boolean z = true;
        if (iArr.length == 1) {
            z = iArr[0] == 0;
        } else if (i2 < iArr.length) {
            z = iArr[i2] == 0;
        }
        int readInt = readInt();
        if (readInt == -1) {
            value = ValueNull.INSTANCE;
        } else if (z) {
            byte[] newBytes = Utils.newBytes(readInt);
            readFully(newBytes);
            String str = new String(newBytes, getEncoding());
            switch (i) {
                case PgServer.PG_TYPE_DATE /* 1082 */:
                    int indexOf = str.indexOf(32);
                    if (indexOf > 0) {
                        str = str.substring(0, indexOf);
                        break;
                    }
                    break;
                case PgServer.PG_TYPE_TIME /* 1083 */:
                    int indexOf2 = str.indexOf(43);
                    if (indexOf2 <= 0) {
                        indexOf2 = str.indexOf(45);
                    }
                    if (indexOf2 > 0) {
                        str = str.substring(0, indexOf2);
                        break;
                    }
                    break;
            }
            value = ValueVarchar.get(str, this.session);
        } else {
            switch (i) {
                case 17:
                    byte[] newBytes2 = Utils.newBytes(readInt);
                    readFully(newBytes2);
                    value = ValueVarbinary.getNoCopy(newBytes2);
                    break;
                case 20:
                    checkParamLength(8, readInt);
                    value = ValueBigint.get(this.dataIn.readLong());
                    break;
                case 21:
                    checkParamLength(2, readInt);
                    value = ValueSmallint.get(readShort());
                    break;
                case 23:
                    checkParamLength(4, readInt);
                    value = ValueInteger.get(readInt());
                    break;
                case PgServer.PG_TYPE_FLOAT4 /* 700 */:
                    checkParamLength(4, readInt);
                    value = ValueReal.get(this.dataIn.readFloat());
                    break;
                case PgServer.PG_TYPE_FLOAT8 /* 701 */:
                    checkParamLength(8, readInt);
                    value = ValueDouble.get(this.dataIn.readDouble());
                    break;
                case PgServer.PG_TYPE_NUMERIC /* 1700 */:
                    value = readNumericBinary(readInt);
                    break;
                default:
                    this.server.trace("Binary format for type: " + i + " is unsupported");
                    byte[] newBytes3 = Utils.newBytes(readInt);
                    readFully(newBytes3);
                    value = ValueVarchar.get(new String(newBytes3, getEncoding()), this.session);
                    break;
            }
        }
        arrayList.get(i2).setValue(value, true);
    }

    private static void checkParamLength(int i, int i2) {
        if (i != i2) {
            throw DbException.getInvalidValueException("paramLen", Integer.valueOf(i2));
        }
    }

    private Value readNumericBinary(int i) throws IOException {
        if (i < 8) {
            throw DbException.getInvalidValueException("numeric binary length", Integer.valueOf(i));
        }
        short readShort = readShort();
        short readShort2 = readShort();
        short readShort3 = readShort();
        short readShort4 = readShort();
        if ((readShort * 2) + 8 != i) {
            throw DbException.getInvalidValueException("numeric binary length", Integer.valueOf(i));
        }
        if (readShort3 == NUMERIC_NAN) {
            return ValueDecfloat.NAN;
        }
        if (readShort3 != 0 && readShort3 != 16384) {
            throw DbException.getInvalidValueException("numeric sign", Short.valueOf(readShort3));
        }
        if ((readShort4 & 16383) != readShort4) {
            throw DbException.getInvalidValueException("numeric scale", Short.valueOf(readShort4));
        }
        if (readShort == 0) {
            return readShort4 == 0 ? ValueNumeric.ZERO : ValueNumeric.get(new BigDecimal(BigInteger.ZERO, readShort4));
        }
        BigInteger bigInteger = BigInteger.ZERO;
        for (int i2 = 0; i2 < readShort; i2++) {
            short readShort5 = readShort();
            if (readShort5 < 0 || readShort5 > 9999) {
                throw DbException.getInvalidValueException("numeric chunk", Short.valueOf(readShort5));
            }
            bigInteger = bigInteger.multiply(NUMERIC_CHUNK_MULTIPLIER).add(BigInteger.valueOf(readShort5));
        }
        if (readShort3 != 0) {
            bigInteger = bigInteger.negate();
        }
        return ValueNumeric.get(new BigDecimal(bigInteger, ((readShort - readShort2) - 1) * 4).setScale(readShort4));
    }

    private void sendErrorOrCancelResponse(Exception exc) throws IOException {
        if ((exc instanceof DbException) && ((DbException) exc).getErrorCode() == 57014) {
            sendCancelQueryResponse();
        } else {
            sendErrorResponse(exc);
        }
    }

    private void sendErrorResponse(Exception exc) throws IOException {
        SQLException sQLException = DbException.toSQLException(exc);
        this.server.traceError(sQLException);
        startMessage(69);
        write(83);
        writeString("ERROR");
        write(67);
        writeString(sQLException.getSQLState());
        write(77);
        writeString(sQLException.getMessage());
        write(68);
        writeString(sQLException.toString());
        write(0);
        sendMessage();
    }

    private void sendCancelQueryResponse() throws IOException {
        this.server.trace("CancelSuccessResponse");
        startMessage(69);
        write(83);
        writeString("ERROR");
        write(67);
        writeString("57014");
        write(77);
        writeString("canceling statement due to user request");
        write(0);
        sendMessage();
    }

    private void sendParameterDescription(ArrayList<? extends ParameterInterface> arrayList, int[] iArr) throws Exception {
        int i;
        int size = arrayList.size();
        startMessage(116);
        writeShort(size);
        for (int i2 = 0; i2 < size; i2++) {
            if (iArr != null && iArr[i2] != 0) {
                i = iArr[i2];
            } else {
                i = PgServer.PG_TYPE_VARCHAR;
            }
            int i3 = i;
            this.server.checkType(i3);
            writeInt(i3);
        }
        sendMessage();
    }

    private void sendNoData() throws IOException {
        startMessage(Opcodes.FDIV);
        sendMessage();
    }

    private void sendRowDescription(ResultInterface resultInterface, int[] iArr) throws IOException {
        Table findTableOrView;
        if (resultInterface == null) {
            sendNoData();
            return;
        }
        int visibleColumnCount = resultInterface.getVisibleColumnCount();
        int[] iArr2 = new int[visibleColumnCount];
        int[] iArr3 = new int[visibleColumnCount];
        int[] iArr4 = new int[visibleColumnCount];
        int[] iArr5 = new int[visibleColumnCount];
        String[] strArr = new String[visibleColumnCount];
        Database database = this.session.getDatabase();
        for (int i = 0; i < visibleColumnCount; i++) {
            String columnName = resultInterface.getColumnName(i);
            Schema findSchema = database.findSchema(resultInterface.getSchemaName(i));
            if (findSchema != null && (findTableOrView = findSchema.findTableOrView(this.session, resultInterface.getTableName(i))) != null) {
                iArr2[i] = findTableOrView.getId();
                Column findColumn = findTableOrView.findColumn(columnName);
                if (findColumn != null) {
                    iArr3[i] = findColumn.getColumnId() + 1;
                }
            }
            strArr[i] = columnName;
            TypeInfo columnType = resultInterface.getColumnType(i);
            int convertType = PgServer.convertType(columnType);
            iArr5[i] = columnType.getDisplaySize();
            if (columnType.getValueType() != 0) {
                this.server.checkType(convertType);
            }
            iArr4[i] = convertType;
        }
        startMessage(84);
        writeShort(visibleColumnCount);
        for (int i2 = 0; i2 < visibleColumnCount; i2++) {
            writeString(StringUtils.toLowerEnglish(strArr[i2]));
            writeInt(iArr2[i2]);
            writeShort(iArr3[i2]);
            writeInt(iArr4[i2]);
            writeShort(getTypeSize(iArr4[i2], iArr5[i2]));
            writeInt(-1);
            writeShort(formatAsText(iArr4[i2], iArr, i2) ? 0 : 1);
        }
        sendMessage();
    }

    private static boolean formatAsText(int i, int[] iArr, int i2) {
        boolean z = true;
        if (iArr != null && iArr.length > 0) {
            if (iArr.length == 1) {
                z = iArr[0] == 0;
            } else if (i2 < iArr.length) {
                z = iArr[i2] == 0;
            }
        }
        return z;
    }

    private static int getTypeSize(int i, int i2) {
        switch (i) {
            case 16:
                return 1;
            case PgServer.PG_TYPE_VARCHAR /* 1043 */:
                return Math.max(Const.MAX_ARRAY_DIMENSIONS, i2 + 10);
            default:
                return i2 + 4;
        }
    }

    private void sendErrorResponse(String str) throws IOException {
        this.server.trace("Exception: " + str);
        startMessage(69);
        write(83);
        writeString("ERROR");
        write(67);
        writeString("08P01");
        write(77);
        writeString(str);
        sendMessage();
    }

    private void sendParseComplete() throws IOException {
        startMessage(49);
        sendMessage();
    }

    private void sendBindComplete() throws IOException {
        startMessage(50);
        sendMessage();
    }

    private void sendCloseComplete() throws IOException {
        startMessage(51);
        sendMessage();
    }

    private void initDb() {
        this.session.setTimeZone(this.timeZone);
        Command prepareLocal = this.session.prepareLocal("set search_path = public, pg_catalog");
        Throwable th = null;
        try {
            prepareLocal.executeUpdate(null);
            if (prepareLocal != null) {
                if (0 != 0) {
                    try {
                        prepareLocal.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    prepareLocal.close();
                }
            }
            HashSet<Integer> typeSet = this.server.getTypeSet();
            if (typeSet.isEmpty()) {
                Command prepareLocal2 = this.session.prepareLocal("select oid from pg_catalog.pg_type");
                Throwable th3 = null;
                try {
                    ResultInterface executeQuery = prepareLocal2.executeQuery(0L, false);
                    Throwable th4 = null;
                    while (executeQuery.next()) {
                        try {
                            try {
                                typeSet.add(Integer.valueOf(executeQuery.currentRow()[0].getInt()));
                            } catch (Throwable th5) {
                                if (executeQuery != null) {
                                    if (th4 != null) {
                                        try {
                                            executeQuery.close();
                                        } catch (Throwable th6) {
                                            th4.addSuppressed(th6);
                                        }
                                    } else {
                                        executeQuery.close();
                                    }
                                }
                                throw th5;
                            }
                        } catch (Throwable th7) {
                            th4 = th7;
                            throw th7;
                        }
                    }
                    if (executeQuery != null) {
                        if (0 != 0) {
                            try {
                                executeQuery.close();
                            } catch (Throwable th8) {
                                th4.addSuppressed(th8);
                            }
                        } else {
                            executeQuery.close();
                        }
                    }
                    if (prepareLocal2 != null) {
                        if (0 == 0) {
                            prepareLocal2.close();
                            return;
                        }
                        try {
                            prepareLocal2.close();
                        } catch (Throwable th9) {
                            th3.addSuppressed(th9);
                        }
                    }
                } catch (Throwable th10) {
                    if (prepareLocal2 != null) {
                        if (0 != 0) {
                            try {
                                prepareLocal2.close();
                            } catch (Throwable th11) {
                                th3.addSuppressed(th11);
                            }
                        } else {
                            prepareLocal2.close();
                        }
                    }
                    throw th10;
                }
            }
        } catch (Throwable th12) {
            if (prepareLocal != null) {
                if (0 != 0) {
                    try {
                        prepareLocal.close();
                    } catch (Throwable th13) {
                        th.addSuppressed(th13);
                    }
                } else {
                    prepareLocal.close();
                }
            }
            throw th12;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void close() {
        Iterator<Prepared> it = this.prepared.values().iterator();
        while (it.hasNext()) {
            it.next().close();
        }
        try {
            this.stop = true;
            try {
                this.session.close();
            } catch (Exception e) {
            }
            if (this.socket != null) {
                this.socket.close();
            }
            this.server.trace("Close");
        } catch (Exception e2) {
            this.server.traceError(e2);
        }
        this.session = null;
        this.socket = null;
        this.server.remove(this);
    }

    private void sendAuthenticationCleartextPassword() throws IOException {
        startMessage(82);
        writeInt(3);
        sendMessage();
    }

    private void sendAuthenticationOk() throws IOException {
        startMessage(82);
        writeInt(0);
        sendMessage();
        sendParameterStatus("client_encoding", this.clientEncoding);
        sendParameterStatus("DateStyle", this.dateStyle);
        sendParameterStatus("is_superuser", CustomBooleanEditor.VALUE_OFF);
        sendParameterStatus("server_encoding", "SQL_ASCII");
        sendParameterStatus("server_version", Constants.PG_VERSION);
        sendParameterStatus("session_authorization", this.userName);
        sendParameterStatus("standard_conforming_strings", CustomBooleanEditor.VALUE_OFF);
        sendParameterStatus("TimeZone", pgTimeZone(this.timeZone.getId()));
        sendParameterStatus("integer_datetimes", CustomBooleanEditor.VALUE_OFF);
        sendBackendKeyData();
        sendReadyForQuery();
    }

    private void sendReadyForQuery() throws IOException {
        startMessage(90);
        write((byte) (this.session.getAutoCommit() ? 73 : 84));
        sendMessage();
    }

    private void sendBackendKeyData() throws IOException {
        startMessage(75);
        writeInt(this.processId);
        writeInt(this.secret);
        sendMessage();
    }

    private void writeString(String str) throws IOException {
        writeStringPart(str);
        write(0);
    }

    private void writeStringPart(String str) throws IOException {
        write(str.getBytes(getEncoding()));
    }

    private void writeInt(int i) throws IOException {
        this.dataOut.writeInt(i);
    }

    private void writeShort(int i) throws IOException {
        this.dataOut.writeShort(i);
    }

    private void write(byte[] bArr) throws IOException {
        this.dataOut.write(bArr);
    }

    private void write(ByteArrayOutputStream byteArrayOutputStream) throws IOException {
        byteArrayOutputStream.writeTo(this.dataOut);
    }

    private void write(int i) throws IOException {
        this.dataOut.write(i);
    }

    private void startMessage(int i) {
        this.messageType = i;
        if (this.outBuffer.size() <= 65536) {
            this.outBuffer.reset();
        } else {
            this.outBuffer = new ByteArrayOutputStream();
        }
        this.dataOut = new DataOutputStream(this.outBuffer);
    }

    private void sendMessage() throws IOException {
        this.dataOut.flush();
        this.dataOut = new DataOutputStream(this.out);
        write(this.messageType);
        writeInt(this.outBuffer.size() + 4);
        write(this.outBuffer);
        this.dataOut.flush();
    }

    private void sendParameterStatus(String str, String str2) throws IOException {
        startMessage(83);
        writeString(str);
        writeString(str2);
        sendMessage();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setThread(Thread thread) {
        this.thread = thread;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Thread getThread() {
        return this.thread;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setProcessId(int i) {
        this.processId = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getProcessId() {
        return this.processId;
    }

    private synchronized void setActiveRequest(CommandInterface commandInterface) {
        this.activeRequest = commandInterface;
    }

    private synchronized void cancelRequest() {
        if (this.activeRequest != null) {
            this.activeRequest.cancel();
            this.activeRequest = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/server/pg/PgServerThread$Prepared.class */
    public static class Prepared {
        String name;
        String sql;
        CommandInterface prep;
        ResultInterface result;
        int[] paramType;

        Prepared() {
        }

        void close() {
            try {
                closeResult();
                this.prep.close();
            } catch (Exception e) {
            }
        }

        void closeResult() {
            ResultInterface resultInterface = this.result;
            if (resultInterface != null) {
                this.result = null;
                resultInterface.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/server/pg/PgServerThread$Portal.class */
    public static class Portal {
        String name;
        int[] resultColumnFormat;
        Prepared prep;

        Portal() {
        }
    }
}
