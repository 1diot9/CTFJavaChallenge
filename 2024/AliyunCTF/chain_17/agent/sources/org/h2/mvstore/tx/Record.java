package org.h2.mvstore.tx;

import java.nio.ByteBuffer;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.BasicDataType;
import org.h2.value.VersionedValue;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/Record.class */
public final class Record<K, V> {
    static final Record<?, ?> COMMIT_MARKER = new Record<>(-1, null, null);
    final int mapId;
    final K key;
    final VersionedValue<V> oldValue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Record(int i, K k, VersionedValue<V> versionedValue) {
        this.mapId = i;
        this.key = k;
        this.oldValue = versionedValue;
    }

    public String toString() {
        return "mapId=" + this.mapId + ", key=" + this.key + ", value=" + this.oldValue;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/Record$Type.class */
    static final class Type<K, V> extends BasicDataType<Record<K, V>> {
        private final TransactionStore transactionStore;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Type(TransactionStore transactionStore) {
            this.transactionStore = transactionStore;
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(Record<K, V> record) {
            int i = 52;
            if (record.mapId >= 0) {
                MVMap<K, VersionedValue<V>> map = this.transactionStore.getMap(record.mapId);
                i = 52 + map.getKeyType().getMemory(record.key) + map.getValueType().getMemory(record.oldValue);
            }
            return i;
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(Record<K, V> record, Record<K, V> record2) {
            throw new UnsupportedOperationException();
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, Record<K, V> record) {
            writeBuffer.putVarInt(record.mapId);
            if (record.mapId >= 0) {
                MVMap<K, VersionedValue<V>> map = this.transactionStore.getMap(record.mapId);
                map.getKeyType().write(writeBuffer, record.key);
                VersionedValue<V> versionedValue = record.oldValue;
                if (versionedValue == null) {
                    writeBuffer.put((byte) 0);
                } else {
                    writeBuffer.put((byte) 1);
                    map.getValueType().write(writeBuffer, versionedValue);
                }
            }
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public Record<K, V> read(ByteBuffer byteBuffer) {
            int readVarInt = DataUtils.readVarInt(byteBuffer);
            if (readVarInt < 0) {
                return (Record<K, V>) Record.COMMIT_MARKER;
            }
            MVMap<K, VersionedValue<V>> map = this.transactionStore.getMap(readVarInt);
            K read = map.getKeyType().read(byteBuffer);
            VersionedValue<V> versionedValue = null;
            if (byteBuffer.get() == 1) {
                versionedValue = map.getValueType().read(byteBuffer);
            }
            return new Record<>(readVarInt, read, versionedValue);
        }

        @Override // org.h2.mvstore.type.DataType
        public Record<K, V>[] createStorage(int i) {
            return new Record[i];
        }
    }
}
