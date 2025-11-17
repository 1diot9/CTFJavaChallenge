package org.h2.mvstore.db;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.message.DbException;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.MVStoreException;
import org.h2.mvstore.StreamStore;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.tx.TransactionStore;
import org.h2.mvstore.type.BasicDataType;
import org.h2.mvstore.type.ByteArrayDataType;
import org.h2.mvstore.type.LongDataType;
import org.h2.store.CountingReaderInputStream;
import org.h2.store.LobStorageInterface;
import org.h2.store.RangeInputStream;
import org.h2.util.IOUtils;
import org.h2.util.Utils;
import org.h2.value.Value;
import org.h2.value.ValueBlob;
import org.h2.value.ValueClob;
import org.h2.value.ValueLob;
import org.h2.value.ValueNull;
import org.h2.value.lob.LobDataDatabase;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/LobStorageMap.class */
public final class LobStorageMap implements LobStorageInterface {
    private static final boolean TRACE = false;
    private final Database database;
    final MVStore mvStore;
    private final ThreadPoolExecutor cleanupExecutor;
    private final MVMap<Long, BlobMeta> lobMap;
    private final MVMap<Long, byte[]> tempLobMap;
    private final MVMap<BlobReference, Value> refMap;
    private final StreamStore streamStore;
    static final /* synthetic */ boolean $assertionsDisabled;
    private final AtomicLong nextLobId = new AtomicLong(0);
    private final Queue<LobRemovalInfo> pendingLobRemovals = new ConcurrentLinkedQueue();

    static {
        $assertionsDisabled = !LobStorageMap.class.desiredAssertionStatus();
    }

    public static MVMap<Long, BlobMeta> openLobMap(TransactionStore transactionStore) {
        return transactionStore.openMap("lobMap", LongDataType.INSTANCE, BlobMeta.Type.INSTANCE);
    }

    public static MVMap<Long, byte[]> openLobDataMap(TransactionStore transactionStore) {
        return transactionStore.openMap("lobData", LongDataType.INSTANCE, ByteArrayDataType.INSTANCE);
    }

    public LobStorageMap(Database database) {
        this.database = database;
        Store store = database.getStore();
        TransactionStore transactionStore = store.getTransactionStore();
        this.mvStore = store.getMvStore();
        if (this.mvStore.isVersioningRequired()) {
            this.cleanupExecutor = Utils.createSingleThreadExecutor("H2-lob-cleaner", new SynchronousQueue());
            this.mvStore.setOldestVersionTracker(j -> {
                if (needCleanup()) {
                    try {
                        this.cleanupExecutor.execute(() -> {
                            try {
                                cleanup(j);
                            } catch (MVStoreException e) {
                                this.mvStore.panic(e);
                            }
                        });
                    } catch (RejectedExecutionException e) {
                    }
                }
            });
        } else {
            this.cleanupExecutor = null;
        }
        MVStore.TxCounter registerVersionUsage = this.mvStore.registerVersionUsage();
        try {
            this.lobMap = openLobMap(transactionStore);
            this.tempLobMap = transactionStore.openMap("tempLobMap", LongDataType.INSTANCE, ByteArrayDataType.INSTANCE);
            this.refMap = transactionStore.openMap("lobRef", BlobReference.Type.INSTANCE, NullValueDataType.INSTANCE);
            MVMap<Long, byte[]> openLobDataMap = openLobDataMap(transactionStore);
            this.streamStore = new StreamStore(openLobDataMap);
            if (!database.isReadOnly()) {
                Long lastKey = openLobDataMap.lastKey();
                if (lastKey != null) {
                    this.streamStore.setNextKey(lastKey.longValue() + 1);
                }
                Long lastKey2 = this.lobMap.lastKey();
                Long lastKey3 = this.tempLobMap.lastKey();
                long longValue = lastKey2 != null ? lastKey2.longValue() + 1 : 1L;
                this.nextLobId.set(lastKey3 != null ? Math.max(longValue, lastKey3.longValue() + 1) : longValue);
            }
        } finally {
            this.mvStore.deregisterVersionUsage(registerVersionUsage);
        }
    }

    @Override // org.h2.store.LobStorageInterface
    public ValueBlob createBlob(InputStream inputStream, long j) {
        MVStore.TxCounter registerVersionUsage = this.mvStore.registerVersionUsage();
        try {
            if (j != -1) {
                try {
                    if (j <= this.database.getMaxLengthInplaceLob()) {
                        byte[] bArr = new byte[(int) j];
                        int readFully = IOUtils.readFully(inputStream, bArr, (int) j);
                        if (readFully > j) {
                            throw new IllegalStateException("len > blobLength, " + readFully + " > " + j);
                        }
                        if (readFully < bArr.length) {
                            bArr = Arrays.copyOf(bArr, readFully);
                        }
                        ValueBlob createSmall = ValueBlob.createSmall(bArr);
                        this.mvStore.deregisterVersionUsage(registerVersionUsage);
                        return createSmall;
                    }
                } catch (IOException e) {
                    throw DbException.convertIOException(e, null);
                } catch (IllegalStateException e2) {
                    throw DbException.get(ErrorCode.OBJECT_CLOSED, e2, new String[0]);
                }
            }
            if (j != -1) {
                inputStream = new RangeInputStream(inputStream, 0L, j);
            }
            ValueBlob createBlob = createBlob(inputStream);
            this.mvStore.deregisterVersionUsage(registerVersionUsage);
            return createBlob;
        } catch (Throwable th) {
            this.mvStore.deregisterVersionUsage(registerVersionUsage);
            throw th;
        }
    }

    @Override // org.h2.store.LobStorageInterface
    public ValueClob createClob(Reader reader, long j) {
        MVStore.TxCounter registerVersionUsage = this.mvStore.registerVersionUsage();
        try {
            if (j != -1) {
                try {
                    if (j * 3 <= this.database.getMaxLengthInplaceLob()) {
                        char[] cArr = new char[(int) j];
                        int readFully = IOUtils.readFully(reader, cArr, (int) j);
                        if (readFully > j) {
                            throw new IllegalStateException("len > blobLength, " + readFully + " > " + j);
                        }
                        byte[] bytes = new String(cArr, 0, readFully).getBytes(StandardCharsets.UTF_8);
                        if (bytes.length > this.database.getMaxLengthInplaceLob()) {
                            throw new IllegalStateException("len > maxinplace, " + bytes.length + " > " + this.database.getMaxLengthInplaceLob());
                        }
                        ValueClob createSmall = ValueClob.createSmall(bytes, readFully);
                        this.mvStore.deregisterVersionUsage(registerVersionUsage);
                        return createSmall;
                    }
                } catch (IOException e) {
                    throw DbException.convertIOException(e, null);
                } catch (IllegalStateException e2) {
                    throw DbException.get(ErrorCode.OBJECT_CLOSED, e2, new String[0]);
                }
            }
            if (j < 0) {
                j = Long.MAX_VALUE;
            }
            CountingReaderInputStream countingReaderInputStream = new CountingReaderInputStream(reader, j);
            ValueBlob createBlob = createBlob(countingReaderInputStream);
            ValueClob valueClob = new ValueClob(createBlob.getLobData(), createBlob.octetLength(), countingReaderInputStream.getLength());
            this.mvStore.deregisterVersionUsage(registerVersionUsage);
            return valueClob;
        } catch (Throwable th) {
            this.mvStore.deregisterVersionUsage(registerVersionUsage);
            throw th;
        }
    }

    private ValueBlob createBlob(InputStream inputStream) throws IOException {
        try {
            byte[] put = this.streamStore.put(inputStream);
            long generateLobId = generateLobId();
            long length = this.streamStore.length(put);
            this.tempLobMap.put(Long.valueOf(generateLobId), put);
            this.refMap.put(new BlobReference(put, generateLobId), ValueNull.INSTANCE);
            return new ValueBlob(new LobDataDatabase(this.database, -2, generateLobId), length);
        } catch (Exception e) {
            throw DataUtils.convertToIOException(e);
        }
    }

    private long generateLobId() {
        return this.nextLobId.getAndIncrement();
    }

    @Override // org.h2.store.LobStorageInterface
    public boolean isReadOnly() {
        return this.database.isReadOnly();
    }

    @Override // org.h2.store.LobStorageInterface
    public ValueLob copyLob(ValueLob valueLob, int i) {
        byte[] bArr;
        MVStore.TxCounter registerVersionUsage = this.mvStore.registerVersionUsage();
        try {
            LobDataDatabase lobDataDatabase = (LobDataDatabase) valueLob.getLobData();
            int valueType = valueLob.getValueType();
            long lobId = lobDataDatabase.getLobId();
            long octetLength = valueLob.octetLength();
            if (isTemporaryLob(lobDataDatabase.getTableId())) {
                bArr = this.tempLobMap.get(Long.valueOf(lobId));
            } else {
                bArr = this.lobMap.get(Long.valueOf(lobId)).streamStoreId;
            }
            long generateLobId = generateLobId();
            if (isTemporaryLob(i)) {
                this.tempLobMap.put(Long.valueOf(generateLobId), bArr);
            } else {
                this.lobMap.put(Long.valueOf(generateLobId), new BlobMeta(bArr, i, valueType == 3 ? valueLob.charLength() : octetLength, 0L));
            }
            this.refMap.put(new BlobReference(bArr, generateLobId), ValueNull.INSTANCE);
            LobDataDatabase lobDataDatabase2 = new LobDataDatabase(this.database, i, generateLobId);
            return valueType == 7 ? new ValueBlob(lobDataDatabase2, octetLength) : new ValueClob(lobDataDatabase2, octetLength, valueLob.charLength());
        } finally {
            this.mvStore.deregisterVersionUsage(registerVersionUsage);
        }
    }

    @Override // org.h2.store.LobStorageInterface
    public InputStream getInputStream(long j, long j2) throws IOException {
        MVStore.TxCounter registerVersionUsage = this.mvStore.registerVersionUsage();
        try {
            byte[] bArr = this.tempLobMap.get(Long.valueOf(j));
            if (bArr == null) {
                bArr = this.lobMap.get(Long.valueOf(j)).streamStoreId;
            }
            if (bArr == null) {
                throw DbException.get(ErrorCode.LOB_CLOSED_ON_TIMEOUT_1, "" + j);
            }
            LobInputStream lobInputStream = new LobInputStream(this.streamStore.get(bArr));
            this.mvStore.deregisterVersionUsage(registerVersionUsage);
            return lobInputStream;
        } catch (Throwable th) {
            this.mvStore.deregisterVersionUsage(registerVersionUsage);
            throw th;
        }
    }

    @Override // org.h2.store.LobStorageInterface
    public InputStream getInputStream(long j, int i, long j2) throws IOException {
        byte[] bArr;
        MVStore.TxCounter registerVersionUsage = this.mvStore.registerVersionUsage();
        try {
            if (isTemporaryLob(i)) {
                bArr = this.tempLobMap.get(Long.valueOf(j));
            } else {
                bArr = this.lobMap.get(Long.valueOf(j)).streamStoreId;
            }
            if (bArr == null) {
                throw DbException.get(ErrorCode.LOB_CLOSED_ON_TIMEOUT_1, "" + j);
            }
            LobInputStream lobInputStream = new LobInputStream(this.streamStore.get(bArr));
            this.mvStore.deregisterVersionUsage(registerVersionUsage);
            return lobInputStream;
        } catch (Throwable th) {
            this.mvStore.deregisterVersionUsage(registerVersionUsage);
            throw th;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/LobStorageMap$LobInputStream.class */
    private final class LobInputStream extends FilterInputStream {
        public LobInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read(byte[] bArr, int i, int i2) throws IOException {
            MVStore.TxCounter registerVersionUsage = LobStorageMap.this.mvStore.registerVersionUsage();
            try {
                int read = super.read(bArr, i, i2);
                LobStorageMap.this.mvStore.deregisterVersionUsage(registerVersionUsage);
                return read;
            } catch (Throwable th) {
                LobStorageMap.this.mvStore.deregisterVersionUsage(registerVersionUsage);
                throw th;
            }
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read() throws IOException {
            MVStore.TxCounter registerVersionUsage = LobStorageMap.this.mvStore.registerVersionUsage();
            try {
                return super.read();
            } finally {
                LobStorageMap.this.mvStore.deregisterVersionUsage(registerVersionUsage);
            }
        }
    }

    @Override // org.h2.store.LobStorageInterface
    public void removeAllForTable(int i) {
        if (this.mvStore.isClosed()) {
            return;
        }
        MVStore.TxCounter registerVersionUsage = this.mvStore.registerVersionUsage();
        try {
            if (isTemporaryLob(i)) {
                Iterator<Long> keyIterator = this.tempLobMap.keyIterator(0L);
                while (keyIterator.hasNext()) {
                    doRemoveLob(i, keyIterator.next().longValue());
                }
                this.tempLobMap.clear();
            } else {
                ArrayList arrayList = new ArrayList();
                for (Map.Entry<Long, BlobMeta> entry : this.lobMap.entrySet()) {
                    if (entry.getValue().tableId == i) {
                        arrayList.add(entry.getKey());
                    }
                }
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    doRemoveLob(i, ((Long) it.next()).longValue());
                }
            }
        } finally {
            this.mvStore.deregisterVersionUsage(registerVersionUsage);
        }
    }

    @Override // org.h2.store.LobStorageInterface
    public void removeLob(ValueLob valueLob) {
        LobDataDatabase lobDataDatabase = (LobDataDatabase) valueLob.getLobData();
        requestLobRemoval(lobDataDatabase.getTableId(), lobDataDatabase.getLobId());
    }

    private void requestLobRemoval(int i, long j) {
        this.pendingLobRemovals.offer(new LobRemovalInfo(this.mvStore.getCurrentVersion(), j, i));
    }

    private boolean needCleanup() {
        return !this.pendingLobRemovals.isEmpty();
    }

    @Override // org.h2.store.LobStorageInterface
    public void close() {
        this.mvStore.setOldestVersionTracker(null);
        Utils.shutdownExecutor(this.cleanupExecutor);
        if (!this.mvStore.isClosed() && this.mvStore.isVersioningRequired()) {
            removeAllForTable(-1);
            cleanup(this.mvStore.getCurrentVersion() + 1);
        }
    }

    private void cleanup(long j) {
        LobRemovalInfo poll;
        MVStore.TxCounter registerVersionUsage = this.mvStore.registerVersionUsage();
        while (true) {
            try {
                poll = this.pendingLobRemovals.poll();
                if (poll == null || poll.version >= j) {
                    break;
                } else {
                    doRemoveLob(poll.mapId, poll.lobId);
                }
            } finally {
                this.mvStore.decrementVersionUsageCounter(registerVersionUsage);
            }
        }
        if (poll != null) {
            this.pendingLobRemovals.offer(poll);
        }
    }

    private void doRemoveLob(int i, long j) {
        byte[] bArr;
        if (isTemporaryLob(i)) {
            bArr = this.tempLobMap.remove(Long.valueOf(j));
            if (bArr == null) {
                return;
            }
        } else {
            BlobMeta remove = this.lobMap.remove(Long.valueOf(j));
            if (remove == null) {
                return;
            } else {
                bArr = remove.streamStoreId;
            }
        }
        Value remove2 = this.refMap.remove(new BlobReference(bArr, j));
        if (!$assertionsDisabled && remove2 == null) {
            throw new AssertionError();
        }
        BlobReference ceilingKey = this.refMap.ceilingKey(new BlobReference(bArr, 0L));
        boolean z = false;
        if (ceilingKey != null) {
            if (Arrays.equals(bArr, ceilingKey.streamStoreId)) {
                z = true;
            }
        }
        if (!z) {
            this.streamStore.remove(bArr);
        }
    }

    private static boolean isTemporaryLob(int i) {
        return i == -1 || i == -2 || i == -3;
    }

    private static void trace(String str) {
        System.out.println("[" + Thread.currentThread().getName() + "] LOB " + str);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/LobStorageMap$BlobReference.class */
    public static final class BlobReference implements Comparable<BlobReference> {
        public final byte[] streamStoreId;
        public final long lobId;

        public BlobReference(byte[] bArr, long j) {
            this.streamStoreId = bArr;
            this.lobId = j;
        }

        @Override // java.lang.Comparable
        public int compareTo(BlobReference blobReference) {
            int compare = Integer.compare(this.streamStoreId.length, blobReference.streamStoreId.length);
            if (compare == 0) {
                for (int i = 0; compare == 0 && i < this.streamStoreId.length; i++) {
                    compare = Byte.compare(this.streamStoreId[i], blobReference.streamStoreId[i]);
                }
                if (compare == 0) {
                    compare = Long.compare(this.lobId, blobReference.lobId);
                }
            }
            return compare;
        }

        /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/LobStorageMap$BlobReference$Type.class */
        public static final class Type extends BasicDataType<BlobReference> {
            public static final Type INSTANCE = new Type();

            private Type() {
            }

            @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
            public int getMemory(BlobReference blobReference) {
                return blobReference.streamStoreId.length + 8;
            }

            @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
            public int compare(BlobReference blobReference, BlobReference blobReference2) {
                if (blobReference == blobReference2) {
                    return 0;
                }
                if (blobReference == null) {
                    return 1;
                }
                if (blobReference2 == null) {
                    return -1;
                }
                return blobReference.compareTo(blobReference2);
            }

            @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
            public void write(WriteBuffer writeBuffer, BlobReference blobReference) {
                writeBuffer.putVarInt(blobReference.streamStoreId.length);
                writeBuffer.put(blobReference.streamStoreId);
                writeBuffer.putVarLong(blobReference.lobId);
            }

            @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
            public BlobReference read(ByteBuffer byteBuffer) {
                byte[] bArr = new byte[DataUtils.readVarInt(byteBuffer)];
                byteBuffer.get(bArr);
                return new BlobReference(bArr, DataUtils.readVarLong(byteBuffer));
            }

            @Override // org.h2.mvstore.type.DataType
            public BlobReference[] createStorage(int i) {
                return new BlobReference[i];
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/LobStorageMap$BlobMeta.class */
    public static final class BlobMeta {
        public final byte[] streamStoreId;
        final int tableId;
        final long byteCount;
        final long hash;

        public BlobMeta(byte[] bArr, int i, long j, long j2) {
            this.streamStoreId = bArr;
            this.tableId = i;
            this.byteCount = j;
            this.hash = j2;
        }

        /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/LobStorageMap$BlobMeta$Type.class */
        public static final class Type extends BasicDataType<BlobMeta> {
            public static final Type INSTANCE = new Type();

            private Type() {
            }

            @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
            public int getMemory(BlobMeta blobMeta) {
                return blobMeta.streamStoreId.length + 20;
            }

            @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
            public void write(WriteBuffer writeBuffer, BlobMeta blobMeta) {
                writeBuffer.putVarInt(blobMeta.streamStoreId.length);
                writeBuffer.put(blobMeta.streamStoreId);
                writeBuffer.putVarInt(blobMeta.tableId);
                writeBuffer.putVarLong(blobMeta.byteCount);
                writeBuffer.putLong(blobMeta.hash);
            }

            @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
            public BlobMeta read(ByteBuffer byteBuffer) {
                byte[] bArr = new byte[DataUtils.readVarInt(byteBuffer)];
                byteBuffer.get(bArr);
                return new BlobMeta(bArr, DataUtils.readVarInt(byteBuffer), DataUtils.readVarLong(byteBuffer), byteBuffer.getLong());
            }

            @Override // org.h2.mvstore.type.DataType
            public BlobMeta[] createStorage(int i) {
                return new BlobMeta[i];
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/LobStorageMap$LobRemovalInfo.class */
    public static final class LobRemovalInfo {
        final long version;
        final long lobId;
        final int mapId;

        LobRemovalInfo(long j, long j2, int i) {
            this.version = j;
            this.lobId = j2;
            this.mapId = i;
        }
    }
}
