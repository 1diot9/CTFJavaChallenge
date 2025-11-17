package org.h2.value;

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
import org.h2.util.Bits;
import org.h2.util.DateTimeUtils;
import org.h2.util.IOUtils;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.lob.LobData;
import org.h2.value.lob.LobDataDatabase;
import org.h2.value.lob.LobDataFetchOnDemand;
import org.h2.value.lob.LobDataFile;
import org.h2.value.lob.LobDataInMemory;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueBlob.class */
public final class ValueBlob extends ValueLob {
    public static ValueBlob createSmall(byte[] bArr) {
        return new ValueBlob(new LobDataInMemory(bArr), bArr.length);
    }

    public static ValueBlob createTempBlob(InputStream inputStream, long j, DataHandler dataHandler) {
        byte[] newBytes;
        int readFully;
        long j2 = Long.MAX_VALUE;
        if (j >= 0 && j < Long.MAX_VALUE) {
            j2 = j;
        }
        try {
            int bufferSize = ValueLob.getBufferSize(dataHandler, j2);
            if (bufferSize >= Integer.MAX_VALUE) {
                newBytes = IOUtils.readBytesAndClose(inputStream, -1);
                readFully = newBytes.length;
            } else {
                newBytes = Utils.newBytes(bufferSize);
                readFully = IOUtils.readFully(inputStream, newBytes, bufferSize);
            }
            if (readFully <= dataHandler.getMaxLengthInplaceLob()) {
                return createSmall(Utils.copyBytes(newBytes, readFully));
            }
            return createTemporary(dataHandler, newBytes, readFully, inputStream, j2);
        } catch (IOException e) {
            throw DbException.convertIOException(e, null);
        }
    }

    private static ValueBlob createTemporary(DataHandler dataHandler, byte[] bArr, int i, InputStream inputStream, long j) throws IOException {
        String createTempLobFileName = ValueLob.createTempLobFileName(dataHandler);
        FileStore openFile = dataHandler.openFile(createTempLobFileName, "rw", false);
        openFile.autoDelete();
        long j2 = 0;
        FileStoreOutputStream fileStoreOutputStream = new FileStoreOutputStream(openFile, null);
        Throwable th = null;
        do {
            try {
                try {
                    j2 += i;
                    fileStoreOutputStream.write(bArr, 0, i);
                    j -= i;
                    if (j <= 0) {
                        break;
                    }
                    i = IOUtils.readFully(inputStream, bArr, ValueLob.getBufferSize(dataHandler, j));
                } finally {
                }
            } catch (Throwable th2) {
                if (fileStoreOutputStream != null) {
                    if (th != null) {
                        try {
                            fileStoreOutputStream.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    } else {
                        fileStoreOutputStream.close();
                    }
                }
                throw th2;
            }
        } while (i > 0);
        if (fileStoreOutputStream != null) {
            if (0 != 0) {
                try {
                    fileStoreOutputStream.close();
                } catch (Throwable th4) {
                    th.addSuppressed(th4);
                }
            } else {
                fileStoreOutputStream.close();
            }
        }
        return new ValueBlob(new LobDataFile(dataHandler, createTempLobFileName, openFile), j2);
    }

    public ValueBlob(LobData lobData, long j) {
        super(lobData, j, -1L);
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 7;
    }

    @Override // org.h2.value.Value
    public String getString() {
        String readString;
        long j = this.charLength;
        if (j >= 0) {
            if (j > DateTimeUtils.NANOS_PER_SECOND) {
                throw getStringTooLong(j);
            }
            return readString((int) j);
        }
        if (this.octetLength > 3000000000L) {
            throw getStringTooLong(charLength());
        }
        if (this.lobData instanceof LobDataInMemory) {
            readString = new String(((LobDataInMemory) this.lobData).getSmall(), StandardCharsets.UTF_8);
        } else {
            readString = readString(Integer.MAX_VALUE);
        }
        long length = readString.length();
        this.charLength = length;
        if (length > DateTimeUtils.NANOS_PER_SECOND) {
            throw getStringTooLong(length);
        }
        return readString;
    }

    @Override // org.h2.value.ValueLob
    byte[] getBytesInternal() {
        if (this.octetLength > DateTimeUtils.NANOS_PER_SECOND) {
            throw getBinaryTooLong(this.octetLength);
        }
        return readBytes((int) this.octetLength);
    }

    @Override // org.h2.value.Value
    public InputStream getInputStream() {
        return this.lobData.getInputStream(this.octetLength);
    }

    @Override // org.h2.value.Value
    public InputStream getInputStream(long j, long j2) {
        long j3 = this.octetLength;
        return rangeInputStream(this.lobData.getInputStream(j3), j, j2, j3);
    }

    @Override // org.h2.value.Value
    public Reader getReader(long j, long j2) {
        return rangeReader(getReader(), j, j2, -1L);
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        if (value == this) {
            return 0;
        }
        ValueBlob valueBlob = (ValueBlob) value;
        LobData lobData = this.lobData;
        LobData lobData2 = valueBlob.lobData;
        if (lobData.getClass() == lobData2.getClass()) {
            if (lobData instanceof LobDataInMemory) {
                return Bits.compareNotNullUnsigned(((LobDataInMemory) lobData).getSmall(), ((LobDataInMemory) lobData2).getSmall());
            }
            if (lobData instanceof LobDataDatabase) {
                if (((LobDataDatabase) lobData).getLobId() == ((LobDataDatabase) lobData2).getLobId()) {
                    return 0;
                }
            } else if ((lobData instanceof LobDataFetchOnDemand) && ((LobDataFetchOnDemand) lobData).getLobId() == ((LobDataFetchOnDemand) lobData2).getLobId()) {
                return 0;
            }
        }
        return compare(this, valueBlob);
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
    /* JADX WARN: Code restructure failed: missing block: B:19:0x006e, code lost:            if (r0 == null) goto L24;     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0073, code lost:            if (0 == 0) goto L23;     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x008a, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0076, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x007e, code lost:            r17 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0080, code lost:            r0.addSuppressed(r17);     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0059, code lost:            throw org.h2.message.DbException.getUnsupportedException("Invalid LOB");     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00be, code lost:            r0 = r0.read();        r0 = r0.read();     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00ce, code lost:            if (r0 >= 0) goto L59;     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0128, code lost:            if (r0 >= 0) goto L79;     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x017b, code lost:            if (r0 == r0) goto L103;     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x018a, code lost:            if ((r0 & org.apache.tomcat.util.bcel.Const.MAX_ARRAY_DIMENSIONS) >= (r0 & org.apache.tomcat.util.bcel.Const.MAX_ARRAY_DIMENSIONS)) goto L84;     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x018d, code lost:            r0 = -1;     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x0192, code lost:            r17 = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0196, code lost:            if (r0 == null) goto L93;     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x019b, code lost:            if (0 == 0) goto L92;     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x01b2, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x019e, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x01a6, code lost:            r18 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x01a8, code lost:            r0.addSuppressed(r18);     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x0191, code lost:            r0 = 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x0130, code lost:            if (r0 == null) goto L69;     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x0135, code lost:            if (0 == 0) goto L68;     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x014c, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x0138, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x0140, code lost:            r18 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x0142, code lost:            r0.addSuppressed(r18);     */
    /* JADX WARN: Failed to calculate best type for var: r10v0 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:156)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:133)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:238)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:221)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Failed to calculate best type for var: r10v0 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:145)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:123)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:101)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:101)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
     */
    /* JADX WARN: Failed to calculate best type for var: r11v1 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:156)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:133)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:238)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:221)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Failed to calculate best type for var: r11v1 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:145)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:123)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:101)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:101)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
     */
    /* JADX WARN: Failed to calculate best type for var: r12v0 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:156)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:133)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:238)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:221)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Failed to calculate best type for var: r12v0 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:145)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:123)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:101)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:101)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
     */
    /* JADX WARN: Failed to calculate best type for var: r9v0 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:156)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:133)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:238)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:221)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Failed to calculate best type for var: r9v0 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:145)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:123)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:101)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:101)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
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
    /* JADX WARN: Not initialized variable reg: 10, insn: 0x0221: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:155:0x0221 */
    /* JADX WARN: Not initialized variable reg: 11, insn: 0x01eb: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r11 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:134:0x01eb */
    /* JADX WARN: Not initialized variable reg: 12, insn: 0x01f0: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r12 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:136:0x01f0 */
    /* JADX WARN: Not initialized variable reg: 9, insn: 0x021c: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r9 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:153:0x021c */
    /* JADX WARN: Type inference failed for: r10v0, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r11v1, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r12v0, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r9v0, types: [java.io.InputStream] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int compare(org.h2.value.ValueBlob r5, org.h2.value.ValueBlob r6) {
        /*
            Method dump skipped, instructions count: 586
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.value.ValueBlob.compare(org.h2.value.ValueBlob, org.h2.value.ValueBlob):int");
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        if ((i & 2) != 0 && (!(this.lobData instanceof LobDataInMemory) || this.octetLength > SysProperties.MAX_TRACE_DATA_LENGTH)) {
            sb.append("CAST(REPEAT(CHAR(0), ").append(this.octetLength).append(") AS BINARY VARYING");
            formatLobDataComment(sb);
        } else if ((i & 6) == 0) {
            sb.append("CAST(X'");
            StringUtils.convertBytesToHex(sb, getBytesNoCopy()).append("' AS BINARY LARGE OBJECT(").append(this.octetLength).append("))");
        } else {
            sb.append("X'");
            StringUtils.convertBytesToHex(sb, getBytesNoCopy()).append('\'');
        }
        return sb;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValueBlob convertPrecision(long j) {
        ValueBlob createSmall;
        if (this.octetLength <= j) {
            return this;
        }
        DataHandler dataHandler = this.lobData.getDataHandler();
        if (dataHandler != null) {
            createSmall = createTempBlob(getInputStream(), j, dataHandler);
        } else {
            try {
                createSmall = createSmall(IOUtils.readBytesAndClose(getInputStream(), MathUtils.convertLongToInt(j)));
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
                ValueBlob createBlob = dataHandler.getLobStorage().createBlob(getInputStream(), this.octetLength);
                ValueLob copy = createBlob.copy(dataHandler, i);
                createBlob.remove();
                return copy;
            }
            return this;
        }
        if (this.lobData instanceof LobDataDatabase) {
            return dataHandler.getLobStorage().copyLob(this, i);
        }
        throw new UnsupportedOperationException();
    }

    /* JADX WARN: Failed to calculate best type for var: r10v0 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:156)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:133)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:238)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:221)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Failed to calculate best type for var: r10v0 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:145)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:123)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:101)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:101)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
     */
    /* JADX WARN: Failed to calculate best type for var: r9v0 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:156)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:133)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:238)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:221)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Failed to calculate best type for var: r9v0 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:145)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:123)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:101)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:101)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
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
    /* JADX WARN: Not initialized variable reg: 10, insn: 0x0088: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:38:0x0088 */
    /* JADX WARN: Not initialized variable reg: 9, insn: 0x0084: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r9 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:36:0x0084 */
    /* JADX WARN: Type inference failed for: r10v0, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r9v0, types: [java.io.Reader] */
    @Override // org.h2.value.Value
    public long charLength() {
        long j = this.charLength;
        if (j < 0) {
            if (this.lobData instanceof LobDataInMemory) {
                j = new String(((LobDataInMemory) this.lobData).getSmall(), StandardCharsets.UTF_8).length();
            } else {
                try {
                    try {
                        Reader reader = getReader();
                        Throwable th = null;
                        long j2 = 0;
                        while (true) {
                            j = j2 + reader.skip(Long.MAX_VALUE);
                            if (reader.read() < 0) {
                                break;
                            }
                            j2 = j + 1;
                        }
                        if (reader != null) {
                            if (0 != 0) {
                                try {
                                    reader.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                reader.close();
                            }
                        }
                    } finally {
                    }
                } catch (IOException e) {
                    throw DbException.convertIOException(e, null);
                }
            }
            this.charLength = j;
        }
        return j;
    }

    @Override // org.h2.value.Value
    public long octetLength() {
        return this.octetLength;
    }
}
