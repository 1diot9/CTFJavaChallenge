package org.h2.value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import org.h2.engine.CastDataProvider;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.store.DataHandler;
import org.h2.store.FileStore;
import org.h2.store.FileStoreOutputStream;
import org.h2.store.RangeReader;
import org.h2.util.DateTimeUtils;
import org.h2.util.IOUtils;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.value.lob.LobData;
import org.h2.value.lob.LobDataDatabase;
import org.h2.value.lob.LobDataFetchOnDemand;
import org.h2.value.lob.LobDataFile;
import org.h2.value.lob.LobDataInMemory;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueClob.class */
public final class ValueClob extends ValueLob {
    public static ValueClob createSmall(byte[] bArr) {
        return new ValueClob(new LobDataInMemory(bArr), bArr.length, new String(bArr, StandardCharsets.UTF_8).length());
    }

    public static ValueClob createSmall(byte[] bArr, long j) {
        return new ValueClob(new LobDataInMemory(bArr), bArr.length, j);
    }

    public static ValueClob createSmall(String str) {
        return new ValueClob(new LobDataInMemory(str.getBytes(StandardCharsets.UTF_8)), r0.length, str.length());
    }

    public static ValueClob createTempClob(Reader reader, long j, DataHandler dataHandler) {
        BufferedReader bufferedReader;
        char[] cArr;
        int readFully;
        if (j >= 0) {
            try {
                reader = new RangeReader(reader, 0L, j);
            } catch (IOException e) {
                throw DbException.convert(e);
            }
        }
        if (reader instanceof BufferedReader) {
            bufferedReader = (BufferedReader) reader;
        } else {
            bufferedReader = new BufferedReader(reader, 4096);
        }
        long j2 = Long.MAX_VALUE;
        if (j >= 0 && j < Long.MAX_VALUE) {
            j2 = j;
        }
        try {
            int bufferSize = ValueLob.getBufferSize(dataHandler, j2);
            if (bufferSize >= Integer.MAX_VALUE) {
                cArr = IOUtils.readStringAndClose(bufferedReader, -1).toCharArray();
                readFully = cArr.length;
            } else {
                cArr = new char[bufferSize];
                bufferedReader.mark(bufferSize);
                readFully = IOUtils.readFully(bufferedReader, cArr, bufferSize);
            }
            if (readFully <= dataHandler.getMaxLengthInplaceLob()) {
                return createSmall(new String(cArr, 0, readFully));
            }
            bufferedReader.reset();
            return createTemporary(dataHandler, bufferedReader, j2);
        } catch (IOException e2) {
            throw DbException.convertIOException(e2, null);
        }
    }

    private static ValueClob createTemporary(DataHandler dataHandler, Reader reader, long j) throws IOException {
        String createTempLobFileName = ValueLob.createTempLobFileName(dataHandler);
        FileStore openFile = dataHandler.openFile(createTempLobFileName, "rw", false);
        openFile.autoDelete();
        long j2 = 0;
        long j3 = 0;
        FileStoreOutputStream fileStoreOutputStream = new FileStoreOutputStream(openFile, null);
        Throwable th = null;
        try {
            try {
                char[] cArr = new char[4096];
                while (true) {
                    int readFully = IOUtils.readFully(reader, cArr, ValueLob.getBufferSize(dataHandler, j));
                    if (readFully == 0) {
                        break;
                    }
                    fileStoreOutputStream.write(new String(cArr, 0, readFully).getBytes(StandardCharsets.UTF_8));
                    j2 += r0.length;
                    j3 += readFully;
                }
                if (fileStoreOutputStream != null) {
                    if (0 != 0) {
                        try {
                            fileStoreOutputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        fileStoreOutputStream.close();
                    }
                }
                return new ValueClob(new LobDataFile(dataHandler, createTempLobFileName, openFile), j2, j3);
            } finally {
            }
        } catch (Throwable th3) {
            if (fileStoreOutputStream != null) {
                if (th != null) {
                    try {
                        fileStoreOutputStream.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    fileStoreOutputStream.close();
                }
            }
            throw th3;
        }
    }

    public ValueClob(LobData lobData, long j, long j2) {
        super(lobData, j, j2);
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 3;
    }

    @Override // org.h2.value.Value
    public String getString() {
        if (this.charLength > DateTimeUtils.NANOS_PER_SECOND) {
            throw getStringTooLong(this.charLength);
        }
        if (this.lobData instanceof LobDataInMemory) {
            return new String(((LobDataInMemory) this.lobData).getSmall(), StandardCharsets.UTF_8);
        }
        return readString((int) this.charLength);
    }

    @Override // org.h2.value.ValueLob
    byte[] getBytesInternal() {
        long j = this.octetLength;
        if (j >= 0) {
            if (j > DateTimeUtils.NANOS_PER_SECOND) {
                throw getBinaryTooLong(j);
            }
            return readBytes((int) j);
        }
        if (this.octetLength > DateTimeUtils.NANOS_PER_SECOND) {
            throw getBinaryTooLong(octetLength());
        }
        byte[] readBytes = readBytes(Integer.MAX_VALUE);
        long length = readBytes.length;
        this.octetLength = length;
        if (length > DateTimeUtils.NANOS_PER_SECOND) {
            throw getBinaryTooLong(length);
        }
        return readBytes;
    }

    @Override // org.h2.value.Value
    public InputStream getInputStream() {
        return this.lobData.getInputStream(-1L);
    }

    @Override // org.h2.value.Value
    public InputStream getInputStream(long j, long j2) {
        return rangeInputStream(this.lobData.getInputStream(-1L), j, j2, -1L);
    }

    @Override // org.h2.value.Value
    public Reader getReader(long j, long j2) {
        return rangeReader(getReader(), j, j2, this.charLength);
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        if (value == this) {
            return 0;
        }
        ValueClob valueClob = (ValueClob) value;
        LobData lobData = this.lobData;
        LobData lobData2 = valueClob.lobData;
        if (lobData.getClass() == lobData2.getClass()) {
            if (lobData instanceof LobDataInMemory) {
                return Integer.signum(getString().compareTo(valueClob.getString()));
            }
            if (lobData instanceof LobDataDatabase) {
                if (((LobDataDatabase) lobData).getLobId() == ((LobDataDatabase) lobData2).getLobId()) {
                    return 0;
                }
            } else if ((lobData instanceof LobDataFetchOnDemand) && ((LobDataFetchOnDemand) lobData).getLobId() == ((LobDataFetchOnDemand) lobData2).getLobId()) {
                return 0;
            }
        }
        return compare(this, valueClob);
    }

    /* JADX WARN: Code restructure failed: missing block: B:105:0x00d3, code lost:            if (r0 >= 0) goto L40;     */
    /* JADX WARN: Code restructure failed: missing block: B:106:0x00d6, code lost:            r0 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:107:0x00db, code lost:            r17 = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:108:0x00df, code lost:            if (r0 == null) goto L49;     */
    /* JADX WARN: Code restructure failed: missing block: B:110:0x00e4, code lost:            if (0 == 0) goto L48;     */
    /* JADX WARN: Code restructure failed: missing block: B:111:0x00fb, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:113:0x00e7, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:115:0x00ef, code lost:            r18 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:116:0x00f1, code lost:            r0.addSuppressed(r18);     */
    /* JADX WARN: Code restructure failed: missing block: B:129:0x00da, code lost:            r0 = -1;     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0059, code lost:            throw org.h2.message.DbException.getUnsupportedException("Invalid LOB");     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00be, code lost:            r0 = r0.read();        r0 = r0.read();     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00ce, code lost:            if (r0 >= 0) goto L59;     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0128, code lost:            if (r0 >= 0) goto L79;     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x017b, code lost:            if (r0 == r0) goto L103;     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x0182, code lost:            if (r0 >= r0) goto L84;     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0185, code lost:            r0 = -1;     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x018a, code lost:            r17 = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x018e, code lost:            if (r0 == null) goto L93;     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x0193, code lost:            if (0 == 0) goto L92;     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x01aa, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x0196, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x019e, code lost:            r18 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x01a0, code lost:            r0.addSuppressed(r18);     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x0189, code lost:            r0 = 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x0130, code lost:            if (r0 == null) goto L69;     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x0135, code lost:            if (0 == 0) goto L68;     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x014c, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x0138, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x0140, code lost:            r18 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x0142, code lost:            r0.addSuppressed(r18);     */
    /* JADX WARN: Failed to calculate best type for var: r10v0 ??
    java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r9v0 ??
    java.lang.NullPointerException
     */
    /* JADX WARN: Multi-variable type inference failed. Error: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.RegisterArg.getSVar()" because the return value of "jadx.core.dex.nodes.InsnNode.getResult()" is null
    	at jadx.core.dex.visitors.typeinference.AbstractTypeConstraint.collectRelatedVars(AbstractTypeConstraint.java:31)
    	at jadx.core.dex.visitors.typeinference.AbstractTypeConstraint.<init>(AbstractTypeConstraint.java:19)
    	at jadx.core.dex.visitors.typeinference.TypeSearch$1.<init>(TypeSearch.java:376)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.makeMoveConstraint(TypeSearch.java:376)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.makeConstraint(TypeSearch.java:361)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.collectConstraints(TypeSearch.java:341)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:60)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.runMultiVariableSearch(FixTypesVisitor.java:116)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Not initialized variable reg: 10, insn: 0x0219: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:155:0x0219 */
    /* JADX WARN: Not initialized variable reg: 9, insn: 0x0214: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r9 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:153:0x0214 */
    /* JADX WARN: Type inference failed for: r10v0, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r9v0, types: [java.io.Reader] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int compare(org.h2.value.ValueClob r5, org.h2.value.ValueClob r6) {
        /*
            Method dump skipped, instructions count: 578
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.value.ValueClob.compare(org.h2.value.ValueClob, org.h2.value.ValueClob):int");
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        if ((i & 2) != 0 && (!(this.lobData instanceof LobDataInMemory) || this.charLength > SysProperties.MAX_TRACE_DATA_LENGTH)) {
            sb.append("SPACE(").append(this.charLength);
            formatLobDataComment(sb);
        } else if ((i & 6) == 0) {
            StringUtils.quoteStringSQL(sb.append("CAST("), getString()).append(" AS CHARACTER LARGE OBJECT(").append(this.charLength).append("))");
        } else {
            StringUtils.quoteStringSQL(sb, getString());
        }
        return sb;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValueClob convertPrecision(long j) {
        ValueClob createSmall;
        if (this.charLength <= j) {
            return this;
        }
        DataHandler dataHandler = this.lobData.getDataHandler();
        if (dataHandler != null) {
            createSmall = createTempClob(getReader(), j, dataHandler);
        } else {
            try {
                createSmall = createSmall(IOUtils.readStringAndClose(getReader(), MathUtils.convertLongToInt(j)));
            } catch (IOException e) {
                throw DbException.convertIOException(e, null);
            }
        }
        return createSmall;
    }

    @Override // org.h2.value.ValueLob
    public ValueLob copy(DataHandler dataHandler, int i) {
        if (this.lobData instanceof LobDataInMemory) {
            if (((LobDataInMemory) this.lobData).getSmall().length > dataHandler.getMaxLengthInplaceLob()) {
                ValueClob createClob = dataHandler.getLobStorage().createClob(getReader(), this.charLength);
                ValueLob copy = createClob.copy(dataHandler, i);
                createClob.remove();
                return copy;
            }
            return this;
        }
        if (this.lobData instanceof LobDataDatabase) {
            return dataHandler.getLobStorage().copyLob(this, i);
        }
        throw new UnsupportedOperationException();
    }

    @Override // org.h2.value.Value
    public long charLength() {
        return this.charLength;
    }

    @Override // org.h2.value.Value
    public long octetLength() {
        long j = this.octetLength;
        if (j < 0) {
            if (this.lobData instanceof LobDataInMemory) {
                j = ((LobDataInMemory) this.lobData).getSmall().length;
            } else {
                try {
                    InputStream inputStream = getInputStream();
                    Throwable th = null;
                    long j2 = 0;
                    while (true) {
                        try {
                            try {
                                j = j2 + inputStream.skip(Long.MAX_VALUE);
                                if (inputStream.read() < 0) {
                                    break;
                                }
                                j2 = j + 1;
                            } finally {
                            }
                        } finally {
                        }
                    }
                    if (inputStream != null) {
                        if (0 != 0) {
                            try {
                                inputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            inputStream.close();
                        }
                    }
                } catch (IOException e) {
                    throw DbException.convertIOException(e, null);
                }
            }
            this.octetLength = j;
        }
        return j;
    }
}
