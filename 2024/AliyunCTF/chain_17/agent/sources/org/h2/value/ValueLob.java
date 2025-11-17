package org.h2.value;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.h2.engine.Constants;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.store.DataHandler;
import org.h2.store.LobStorageInterface;
import org.h2.store.RangeInputStream;
import org.h2.store.RangeReader;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.lob.LobData;
import org.h2.value.lob.LobDataDatabase;
import org.h2.value.lob.LobDataFetchOnDemand;
import org.h2.value.lob.LobDataInMemory;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueLob.class */
public abstract class ValueLob extends Value {
    static final int BLOCK_COMPARISON_SIZE = 512;
    private TypeInfo type;
    final LobData lobData;
    long octetLength;
    long charLength;
    private int hash;

    public abstract ValueLob copy(DataHandler dataHandler, int i);

    abstract byte[] getBytesInternal();

    private static void rangeCheckUnknown(long j, long j2) {
        if (j < 0) {
            throw DbException.getInvalidValueException("offset", Long.valueOf(j + 1));
        }
        if (j2 < 0) {
            throw DbException.getInvalidValueException("length", Long.valueOf(j2));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static InputStream rangeInputStream(InputStream inputStream, long j, long j2, long j3) {
        if (j3 > 0) {
            rangeCheck(j - 1, j2, j3);
        } else {
            rangeCheckUnknown(j - 1, j2);
        }
        try {
            return new RangeInputStream(inputStream, j - 1, j2);
        } catch (IOException e) {
            throw DbException.getInvalidValueException("offset", Long.valueOf(j));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Reader rangeReader(Reader reader, long j, long j2, long j3) {
        if (j3 > 0) {
            rangeCheck(j - 1, j2, j3);
        } else {
            rangeCheckUnknown(j - 1, j2);
        }
        try {
            return new RangeReader(reader, j - 1, j2);
        } catch (IOException e) {
            throw DbException.getInvalidValueException("offset", Long.valueOf(j));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValueLob(LobData lobData, long j, long j2) {
        this.lobData = lobData;
        this.octetLength = j;
        this.charLength = j2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String createTempLobFileName(DataHandler dataHandler) throws IOException {
        String databasePath = dataHandler.getDatabasePath();
        if (databasePath.isEmpty()) {
            databasePath = SysProperties.PREFIX_TEMP_FILE;
        }
        return FileUtils.createTempFile(databasePath, Constants.SUFFIX_TEMP_FILE, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getBufferSize(DataHandler dataHandler, long j) {
        if (j < 0 || j > 2147483647L) {
            j = 2147483647L;
        }
        int maxLengthInplaceLob = dataHandler.getMaxLengthInplaceLob();
        long j2 = 4096;
        if (4096 < j && 4096 <= maxLengthInplaceLob) {
            j2 = MathUtils.roundUpLong(Math.min(j, maxLengthInplaceLob + 1), 4096L);
        }
        long convertLongToInt = MathUtils.convertLongToInt(Math.min(j, j2));
        if (convertLongToInt < 0) {
            convertLongToInt = 2147483647L;
        }
        return (int) convertLongToInt;
    }

    public boolean isLinkedToTable() {
        return this.lobData.isLinkedToTable();
    }

    public void remove() {
        this.lobData.remove(this);
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        TypeInfo typeInfo = this.type;
        if (typeInfo == null) {
            int valueType = getValueType();
            TypeInfo typeInfo2 = new TypeInfo(valueType, valueType == 3 ? this.charLength : this.octetLength, 0, null);
            typeInfo = typeInfo2;
            this.type = typeInfo2;
        }
        return typeInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DbException getStringTooLong(long j) {
        return DbException.getValueTooLongException("CHARACTER VARYING", readString(81), j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String readString(int i) {
        try {
            return IOUtils.readStringAndClose(getReader(), i);
        } catch (IOException e) {
            throw DbException.convertIOException(e, toString());
        }
    }

    @Override // org.h2.value.Value
    public Reader getReader() {
        return IOUtils.getReader(getInputStream());
    }

    @Override // org.h2.value.Value
    public byte[] getBytes() {
        if (this.lobData instanceof LobDataInMemory) {
            return Utils.cloneByteArray(getSmall());
        }
        return getBytesInternal();
    }

    @Override // org.h2.value.Value
    public byte[] getBytesNoCopy() {
        if (this.lobData instanceof LobDataInMemory) {
            return getSmall();
        }
        return getBytesInternal();
    }

    private byte[] getSmall() {
        byte[] small = ((LobDataInMemory) this.lobData).getSmall();
        int length = small.length;
        if (length > 1000000000) {
            throw DbException.getValueTooLongException("BINARY VARYING", StringUtils.convertBytesToHex(small, 41), length);
        }
        return small;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DbException getBinaryTooLong(long j) {
        return DbException.getValueTooLongException("BINARY VARYING", StringUtils.convertBytesToHex(readBytes(41)), j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] readBytes(int i) {
        try {
            return IOUtils.readBytesAndClose(getInputStream(), i);
        } catch (IOException e) {
            throw DbException.convertIOException(e, toString());
        }
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        if (this.hash == 0) {
            long j = getValueType() == 3 ? this.charLength : this.octetLength;
            if (j > 4096) {
                return (int) (j ^ (j >>> 32));
            }
            this.hash = Utils.getByteArrayHash(getBytesNoCopy());
        }
        return this.hash;
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        return (obj instanceof ValueLob) && hashCode() == ((ValueLob) obj).hashCode() && compareTypeSafe((Value) obj, null, null) == 0;
    }

    @Override // org.h2.value.Value
    public int getMemory() {
        return this.lobData.getMemory();
    }

    public LobData getLobData() {
        return this.lobData;
    }

    public ValueLob copyToResult() {
        if (this.lobData instanceof LobDataDatabase) {
            LobStorageInterface lobStorage = this.lobData.getDataHandler().getLobStorage();
            if (!lobStorage.isReadOnly()) {
                return lobStorage.copyLob(this, -3);
            }
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void formatLobDataComment(StringBuilder sb) {
        if (this.lobData instanceof LobDataDatabase) {
            LobDataDatabase lobDataDatabase = (LobDataDatabase) this.lobData;
            sb.append(" /* table: ").append(lobDataDatabase.getTableId()).append(" id: ").append(lobDataDatabase.getLobId()).append(" */)");
        } else if (this.lobData instanceof LobDataFetchOnDemand) {
            LobDataFetchOnDemand lobDataFetchOnDemand = (LobDataFetchOnDemand) this.lobData;
            sb.append(" /* table: ").append(lobDataFetchOnDemand.getTableId()).append(" id: ").append(lobDataFetchOnDemand.getLobId()).append(" */)");
        } else {
            sb.append(" /* ").append(this.lobData.toString().replaceAll("\\*/", "\\\\*\\\\/")).append(" */");
        }
    }
}
