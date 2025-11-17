package org.h2.mvstore;

import cn.hutool.core.text.CharSequenceUtil;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import org.h2.compress.Compressor;
import org.h2.mvstore.FileStore;
import org.h2.util.Utils;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/Page.class */
public abstract class Page<K, V> implements Cloneable {
    public final MVMap<K, V> map;
    private volatile long pos;
    public int pageNo;
    private int cachedCompare;
    private int memory;
    private int diskSpaceUsed;
    private K[] keys;
    private static final AtomicLongFieldUpdater<Page> posUpdater;
    static final int PAGE_MEMORY_CHILD = 24;
    private static final int PAGE_MEMORY = 81;
    static final int PAGE_NODE_MEMORY = 121;
    static final int PAGE_LEAF_MEMORY = 113;
    private static final int IN_MEMORY = Integer.MIN_VALUE;
    private static final PageReference[] SINGLE_EMPTY;
    static final /* synthetic */ boolean $assertionsDisabled;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract Page<K, V> copy(MVMap<K, V> mVMap, boolean z);

    public abstract Page<K, V> getChildPage(int i);

    public abstract long getChildPagePos(int i);

    public abstract V getValue(int i);

    public abstract int getNodeType();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract Page<K, V> split(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void expand(int i, K[] kArr, V[] vArr);

    public abstract long getTotalCount();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract long getCounts(int i);

    public abstract void setChild(int i, Page<K, V> page);

    public abstract V setValue(int i, V v);

    public abstract void insertLeaf(int i, K k, V v);

    public abstract void insertNode(int i, K k, Page<K, V> page);

    protected abstract void readPayLoad(ByteBuffer byteBuffer);

    protected abstract void writeValues(WriteBuffer writeBuffer);

    protected abstract void writeChildren(WriteBuffer writeBuffer, boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void writeUnsavedRecursive(FileStore.PageSerializationManager pageSerializationManager);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void releaseSavedPages();

    public abstract int getRawChildPageCount();

    public abstract CursorPos<K, V> getPrependCursorPos(CursorPos<K, V> cursorPos);

    public abstract CursorPos<K, V> getAppendCursorPos(CursorPos<K, V> cursorPos);

    public abstract int removeAllRecursive(long j);

    static {
        $assertionsDisabled = !Page.class.desiredAssertionStatus();
        posUpdater = AtomicLongFieldUpdater.newUpdater(Page.class, "pos");
        SINGLE_EMPTY = new PageReference[]{PageReference.EMPTY};
    }

    Page(MVMap<K, V> mVMap) {
        this.pageNo = -1;
        this.map = mVMap;
    }

    Page(MVMap<K, V> mVMap, Page<K, V> page) {
        this(mVMap, page.keys);
        this.memory = page.memory;
    }

    Page(MVMap<K, V> mVMap, K[] kArr) {
        this.pageNo = -1;
        this.map = mVMap;
        this.keys = kArr;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> Page<K, V> createEmptyLeaf(MVMap<K, V> mVMap) {
        return createLeaf(mVMap, mVMap.getKeyType().createStorage(0), mVMap.getValueType().createStorage(0), 113);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> Page<K, V> createEmptyNode(MVMap<K, V> mVMap) {
        return createNode(mVMap, mVMap.getKeyType().createStorage(0), SINGLE_EMPTY, 0L, 153);
    }

    public static <K, V> Page<K, V> createNode(MVMap<K, V> mVMap, K[] kArr, PageReference<K, V>[] pageReferenceArr, long j, int i) {
        if (!$assertionsDisabled && kArr == null) {
            throw new AssertionError();
        }
        NonLeaf nonLeaf = new NonLeaf(mVMap, kArr, pageReferenceArr, j);
        nonLeaf.initMemoryAccount(i);
        return nonLeaf;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> Page<K, V> createLeaf(MVMap<K, V> mVMap, K[] kArr, V[] vArr, int i) {
        if (!$assertionsDisabled && kArr == null) {
            throw new AssertionError();
        }
        Leaf leaf = new Leaf(mVMap, kArr, vArr);
        leaf.initMemoryAccount(i);
        return leaf;
    }

    private void initMemoryAccount(int i) {
        if (!this.map.isPersistent()) {
            this.memory = Integer.MIN_VALUE;
            return;
        }
        if (i == 0) {
            recalculateMemory();
            return;
        }
        addMemory(i);
        if (!$assertionsDisabled && i != getMemory()) {
            throw new AssertionError();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> V get(Page<K, V> page, K k) {
        int binarySearch;
        while (true) {
            binarySearch = page.binarySearch(k);
            if (page.isLeaf()) {
                break;
            }
            int i = binarySearch + 1;
            if (binarySearch < 0) {
                i = -i;
            }
            page = page.getChildPage(i);
        }
        if (binarySearch >= 0) {
            return page.getValue(binarySearch);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> Page<K, V> read(ByteBuffer byteBuffer, long j, MVMap<K, V> mVMap) {
        Page<K, V> leaf = (DataUtils.getPageType(j) & 1) == 0 ? new Leaf<>(mVMap) : new NonLeaf<>(mVMap);
        ((Page) leaf).pos = j;
        leaf.read(byteBuffer);
        return leaf;
    }

    public final int getMapId() {
        return this.map.getId();
    }

    public K getKey(int i) {
        return this.keys[i];
    }

    public final int getKeyCount() {
        return this.keys.length;
    }

    public final boolean isLeaf() {
        return getNodeType() == 0;
    }

    public final long getPos() {
        return this.pos;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        dump(sb);
        return sb.toString();
    }

    protected void dump(StringBuilder sb) {
        sb.append("id: ").append(System.identityHashCode(this)).append('\n');
        sb.append("pos: ").append(Long.toHexString(this.pos)).append('\n');
        if (isSaved()) {
            sb.append("chunk:").append(Long.toHexString(DataUtils.getPageChunkId(this.pos)));
            if (this.pageNo >= 0) {
                sb.append(",no:").append(Long.toHexString(this.pageNo));
            }
            sb.append('\n');
        }
    }

    public final Page<K, V> copy() {
        Page<K, V> mo1598clone = mo1598clone();
        mo1598clone.pos = 0L;
        mo1598clone.pageNo = -1;
        return mo1598clone;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // 
    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public final Page<K, V> mo1598clone() {
        try {
            return (Page) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int binarySearch(K k) {
        int binarySearch = this.map.getKeyType().binarySearch(k, this.keys, getKeyCount(), this.cachedCompare);
        this.cachedCompare = binarySearch < 0 ? binarySearch ^ (-1) : binarySearch + 1;
        return binarySearch;
    }

    final K[] splitKeys(int i, int i2) {
        if (!$assertionsDisabled && i + i2 > getKeyCount()) {
            throw new AssertionError();
        }
        K[] createKeyStorage = createKeyStorage(i);
        K[] createKeyStorage2 = createKeyStorage(i2);
        System.arraycopy(this.keys, 0, createKeyStorage, 0, i);
        System.arraycopy(this.keys, getKeyCount() - i2, createKeyStorage2, 0, i2);
        this.keys = createKeyStorage;
        return createKeyStorage2;
    }

    final void expandKeys(int i, K[] kArr) {
        int keyCount = getKeyCount();
        K[] createKeyStorage = createKeyStorage(keyCount + i);
        System.arraycopy(this.keys, 0, createKeyStorage, 0, keyCount);
        System.arraycopy(kArr, 0, createKeyStorage, keyCount, i);
        this.keys = createKeyStorage;
    }

    public final void setKey(int i, K k) {
        this.keys = (K[]) ((Object[]) this.keys.clone());
        if (isPersistent()) {
            K k2 = this.keys[i];
            if (!this.map.isMemoryEstimationAllowed() || k2 == null) {
                int evaluateMemoryForKey = this.map.evaluateMemoryForKey(k);
                if (k2 != null) {
                    evaluateMemoryForKey -= this.map.evaluateMemoryForKey(k2);
                }
                addMemory(evaluateMemoryForKey);
            }
        }
        this.keys[i] = k;
    }

    final void insertKey(int i, K k) {
        int keyCount = getKeyCount();
        if (!$assertionsDisabled && i > keyCount) {
            throw new AssertionError(i + " > " + keyCount);
        }
        K[] createKeyStorage = createKeyStorage(keyCount + 1);
        DataUtils.copyWithGap(this.keys, createKeyStorage, keyCount, i);
        this.keys = createKeyStorage;
        this.keys[i] = k;
        if (isPersistent()) {
            addMemory(8 + this.map.evaluateMemoryForKey(k));
        }
    }

    public void remove(int i) {
        int keyCount = getKeyCount();
        if (i == keyCount) {
            i--;
        }
        if (isPersistent() && !this.map.isMemoryEstimationAllowed()) {
            addMemory((-8) - this.map.evaluateMemoryForKey(getKey(i)));
        }
        K[] createKeyStorage = createKeyStorage(keyCount - 1);
        DataUtils.copyExcept(this.keys, createKeyStorage, keyCount, i);
        this.keys = createKeyStorage;
    }

    private void read(ByteBuffer byteBuffer) {
        Compressor compressorFast;
        byte[] newBytes;
        int pageChunkId = DataUtils.getPageChunkId(this.pos);
        int pageOffset = DataUtils.getPageOffset(this.pos);
        int position = byteBuffer.position();
        int i = byteBuffer.getInt();
        int remaining = byteBuffer.remaining() + 4;
        if (i > remaining || i < 4) {
            throw DataUtils.newMVStoreException(6, "File corrupted in chunk {0}, expected page length 4..{1}, got {2}", Integer.valueOf(pageChunkId), Integer.valueOf(remaining), Integer.valueOf(i));
        }
        short s = byteBuffer.getShort();
        int checkValue = (DataUtils.getCheckValue(pageChunkId) ^ DataUtils.getCheckValue(pageOffset)) ^ DataUtils.getCheckValue(i);
        if (s != ((short) checkValue)) {
            throw DataUtils.newMVStoreException(6, "File corrupted in chunk {0}, expected check value {1}, got {2}", Integer.valueOf(pageChunkId), Integer.valueOf(checkValue), Short.valueOf(s));
        }
        this.pageNo = DataUtils.readVarInt(byteBuffer);
        if (this.pageNo < 0) {
            throw DataUtils.newMVStoreException(6, "File corrupted in chunk {0}, got negative page No {1}", Integer.valueOf(pageChunkId), Integer.valueOf(this.pageNo));
        }
        int readVarInt = DataUtils.readVarInt(byteBuffer);
        if (readVarInt != this.map.getId()) {
            throw DataUtils.newMVStoreException(6, "File corrupted in chunk {0}, expected map id {1}, got {2}", Integer.valueOf(pageChunkId), Integer.valueOf(this.map.getId()), Integer.valueOf(readVarInt));
        }
        int readVarInt2 = DataUtils.readVarInt(byteBuffer);
        this.keys = createKeyStorage(readVarInt2);
        byte b = byteBuffer.get();
        if (isLeaf() != ((b & 1) == 0)) {
            Object[] objArr = new Object[3];
            objArr[0] = Integer.valueOf(pageChunkId);
            objArr[1] = isLeaf() ? CustomBooleanEditor.VALUE_0 : CustomBooleanEditor.VALUE_1;
            objArr[2] = Integer.valueOf(b);
            throw DataUtils.newMVStoreException(6, "File corrupted in chunk {0}, expected node type {1}, got {2}", objArr);
        }
        byteBuffer.limit(position + i);
        if (!isLeaf()) {
            readPayLoad(byteBuffer);
        }
        if ((b & 2) != 0) {
            if ((b & 6) == 6) {
                compressorFast = this.map.getStore().getCompressorHigh();
            } else {
                compressorFast = this.map.getStore().getCompressorFast();
            }
            int readVarInt3 = DataUtils.readVarInt(byteBuffer);
            int remaining2 = byteBuffer.remaining();
            int i2 = 0;
            if (byteBuffer.hasArray()) {
                newBytes = byteBuffer.array();
                i2 = byteBuffer.arrayOffset() + byteBuffer.position();
            } else {
                newBytes = Utils.newBytes(remaining2);
                byteBuffer.get(newBytes);
            }
            int i3 = remaining2 + readVarInt3;
            byteBuffer = ByteBuffer.allocate(i3);
            compressorFast.expand(newBytes, i2, remaining2, byteBuffer.array(), byteBuffer.arrayOffset(), i3);
        }
        this.map.getKeyType().read(byteBuffer, this.keys, readVarInt2);
        if (isLeaf()) {
            readPayLoad(byteBuffer);
        }
        this.diskSpaceUsed = i;
        recalculateMemory();
    }

    public final boolean isSaved() {
        return DataUtils.isPageSaved(this.pos);
    }

    public final boolean isRemoved() {
        return DataUtils.isPageRemoved(this.pos);
    }

    private boolean markAsRemoved() {
        if (!$assertionsDisabled && getTotalCount() <= 0) {
            throw new AssertionError(this);
        }
        do {
            long j = this.pos;
            if (DataUtils.isPageSaved(j)) {
                return false;
            }
            if (!$assertionsDisabled && DataUtils.isPageRemoved(j)) {
                throw new AssertionError();
            }
        } while (!posUpdater.compareAndSet(this, 0L, 1L));
        return true;
    }

    protected final int write(FileStore<?>.PageSerializationManager pageSerializationManager) {
        boolean z;
        int compressionLevel;
        Compressor compressorHigh;
        int i;
        byte[] newBytes;
        this.pageNo = pageSerializationManager.getPageNo();
        int keyCount = getKeyCount();
        WriteBuffer buffer = pageSerializationManager.getBuffer();
        int position = buffer.position();
        buffer.putInt(0).putShort((short) 0).putVarInt(this.pageNo).putVarInt(this.map.getId()).putVarInt(keyCount);
        int position2 = buffer.position();
        int i2 = isLeaf() ? 0 : 1;
        buffer.put((byte) i2);
        int position3 = buffer.position();
        writeChildren(buffer, true);
        int position4 = buffer.position();
        this.map.getKeyType().write(buffer, this.keys, keyCount);
        writeValues(buffer);
        MVStore store = this.map.getStore();
        int position5 = buffer.position() - position4;
        if (position5 > 16 && (compressionLevel = store.getCompressionLevel()) > 0) {
            if (compressionLevel == 1) {
                compressorHigh = store.getCompressorFast();
                i = 2;
            } else {
                compressorHigh = store.getCompressorHigh();
                i = 6;
            }
            byte[] bArr = new byte[position5 * 2];
            ByteBuffer buffer2 = buffer.getBuffer();
            int i3 = 0;
            if (buffer2.hasArray()) {
                newBytes = buffer2.array();
                i3 = buffer2.arrayOffset() + position4;
            } else {
                newBytes = Utils.newBytes(position5);
                buffer.position(position4).get(newBytes);
            }
            int compress = compressorHigh.compress(newBytes, i3, position5, bArr, 0);
            if (compress + DataUtils.getVarIntLen(position5 - compress) < position5) {
                buffer.position(position2).put((byte) (i2 | i));
                buffer.position(position4).putVarInt(position5 - compress).put(bArr, 0, compress);
            }
        }
        int position6 = buffer.position() - position;
        long pagePosition = pageSerializationManager.getPagePosition(getMapId(), position, position6, i2);
        if (isSaved()) {
            throw DataUtils.newMVStoreException(3, "Page already stored", new Object[0]);
        }
        boolean isRemoved = isRemoved();
        while (true) {
            z = isRemoved;
            if (posUpdater.compareAndSet(this, z ? 1L : 0L, pagePosition)) {
                break;
            }
            isRemoved = isRemoved();
        }
        int pageMaxLength = DataUtils.getPageMaxLength(pagePosition);
        this.diskSpaceUsed = pageMaxLength != 2097152 ? pageMaxLength : position6;
        pageSerializationManager.onPageSerialized(this, z, pageMaxLength, this.map.isSingleWriter());
        return position3;
    }

    protected final boolean isPersistent() {
        return this.memory != Integer.MIN_VALUE;
    }

    public final int getMemory() {
        if (isPersistent()) {
            return this.memory;
        }
        return 0;
    }

    public long getDiskSpaceUsed() {
        long j = 0;
        if (isPersistent()) {
            j = 0 + this.diskSpaceUsed;
            if (!isLeaf()) {
                for (int i = 0; i < getRawChildPageCount(); i++) {
                    if (getChildPagePos(i) != 0) {
                        j += getChildPage(i).getDiskSpaceUsed();
                    }
                }
            }
        }
        return j;
    }

    final void addMemory(int i) {
        this.memory += i;
        if (!$assertionsDisabled && this.memory < 0) {
            throw new AssertionError();
        }
    }

    final void recalculateMemory() {
        if (!$assertionsDisabled && !isPersistent()) {
            throw new AssertionError();
        }
        this.memory = calculateMemory();
    }

    protected int calculateMemory() {
        return this.map.evaluateMemoryForKeys(this.keys, getKeyCount());
    }

    public boolean isComplete() {
        return true;
    }

    public void setComplete() {
    }

    public final int removePage(long j) {
        if (isPersistent() && getTotalCount() > 0) {
            MVStore mVStore = this.map.store;
            if (!markAsRemoved()) {
                mVStore.accountForRemovedPage(this.pos, j, this.map.isSingleWriter(), this.pageNo);
                return 0;
            }
            return -this.memory;
        }
        return 0;
    }

    public final K[] createKeyStorage(int i) {
        return this.map.getKeyType().createStorage(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final V[] createValueStorage(int i) {
        return this.map.getValueType().createStorage(i);
    }

    public static <K, V> PageReference<K, V>[] createRefStorage(int i) {
        return new PageReference[i];
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/Page$PageReference.class */
    public static final class PageReference<K, V> {
        static final PageReference EMPTY;
        private long pos;
        private Page<K, V> page;
        final long count;
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !Page.class.desiredAssertionStatus();
            EMPTY = new PageReference(null, 0L, 0L);
        }

        public static <X, Y> PageReference<X, Y> empty() {
            return EMPTY;
        }

        public PageReference(Page<K, V> page) {
            this(page, page.getPos(), page.getTotalCount());
        }

        PageReference(long j, long j2) {
            this(null, j, j2);
            if (!$assertionsDisabled && !DataUtils.isPageSaved(j)) {
                throw new AssertionError();
            }
        }

        private PageReference(Page<K, V> page, long j, long j2) {
            this.page = page;
            this.pos = j;
            this.count = j2;
        }

        public Page<K, V> getPage() {
            return this.page;
        }

        void clearPageReference() {
            if (this.page != null) {
                this.page.releaseSavedPages();
                if (!$assertionsDisabled && !this.page.isSaved() && this.page.isComplete()) {
                    throw new AssertionError();
                }
                if (this.page.isSaved()) {
                    if (!$assertionsDisabled && this.pos != this.page.getPos()) {
                        throw new AssertionError();
                    }
                    if (!$assertionsDisabled && this.count != this.page.getTotalCount()) {
                        throw new AssertionError(this.count + " != " + this.page.getTotalCount());
                    }
                    this.page = null;
                }
            }
        }

        long getPos() {
            return this.pos;
        }

        void resetPos() {
            Page<K, V> page = this.page;
            if (page != null && page.isSaved()) {
                this.pos = page.getPos();
                if (!$assertionsDisabled && this.count != page.getTotalCount()) {
                    throw new AssertionError();
                }
            }
        }

        public String toString() {
            String str;
            StringBuilder append = new StringBuilder().append("Cnt:").append(this.count).append(", pos:");
            if (this.pos == 0) {
                str = CustomBooleanEditor.VALUE_0;
            } else {
                str = DataUtils.getPageChunkId(this.pos) + (this.page == null ? "" : "/" + this.page.pageNo) + "-" + DataUtils.getPageOffset(this.pos) + ":" + DataUtils.getPageMaxLength(this.pos);
            }
            return append.append(str).append((this.page != null ? !this.page.isLeaf() : DataUtils.getPageType(this.pos) != 0) ? " node" : " leaf").append(", page:{").append(this.page).append("}").toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/Page$NonLeaf.class */
    public static class NonLeaf<K, V> extends Page<K, V> {
        private PageReference<K, V>[] children;
        private long totalCount;
        static final /* synthetic */ boolean $assertionsDisabled;

        @Override // org.h2.mvstore.Page
        /* renamed from: clone */
        protected /* bridge */ /* synthetic */ Object mo1598clone() throws CloneNotSupportedException {
            return super.mo1598clone();
        }

        static {
            $assertionsDisabled = !Page.class.desiredAssertionStatus();
        }

        NonLeaf(MVMap<K, V> mVMap) {
            super(mVMap);
        }

        NonLeaf(MVMap<K, V> mVMap, NonLeaf<K, V> nonLeaf, PageReference<K, V>[] pageReferenceArr, long j) {
            super(mVMap, nonLeaf);
            this.children = pageReferenceArr;
            this.totalCount = j;
        }

        NonLeaf(MVMap<K, V> mVMap, K[] kArr, PageReference<K, V>[] pageReferenceArr, long j) {
            super(mVMap, kArr);
            this.children = pageReferenceArr;
            this.totalCount = j;
        }

        @Override // org.h2.mvstore.Page
        public int getNodeType() {
            return 1;
        }

        @Override // org.h2.mvstore.Page
        public Page<K, V> copy(MVMap<K, V> mVMap, boolean z) {
            return z ? new IncompleteNonLeaf(mVMap, this) : new NonLeaf(mVMap, this, this.children, this.totalCount);
        }

        @Override // org.h2.mvstore.Page
        public Page<K, V> getChildPage(int i) {
            PageReference<K, V> pageReference = this.children[i];
            Page<K, V> page = pageReference.getPage();
            if (page == null) {
                page = this.map.readPage(pageReference.getPos());
                if (!$assertionsDisabled && pageReference.getPos() != page.getPos()) {
                    throw new AssertionError();
                }
                if (!$assertionsDisabled && pageReference.count != page.getTotalCount()) {
                    throw new AssertionError();
                }
            }
            return page;
        }

        @Override // org.h2.mvstore.Page
        public long getChildPagePos(int i) {
            return this.children[i].getPos();
        }

        @Override // org.h2.mvstore.Page
        public V getValue(int i) {
            throw new UnsupportedOperationException();
        }

        @Override // org.h2.mvstore.Page
        public Page<K, V> split(int i) {
            if (!$assertionsDisabled && isSaved()) {
                throw new AssertionError();
            }
            int keyCount = getKeyCount() - i;
            K[] splitKeys = splitKeys(i, keyCount - 1);
            PageReference<K, V>[] createRefStorage = createRefStorage(i + 1);
            PageReference[] createRefStorage2 = createRefStorage(keyCount);
            System.arraycopy(this.children, 0, createRefStorage, 0, i + 1);
            System.arraycopy(this.children, i + 1, createRefStorage2, 0, keyCount);
            this.children = createRefStorage;
            long j = 0;
            for (PageReference<K, V> pageReference : createRefStorage) {
                j += pageReference.count;
            }
            this.totalCount = j;
            long j2 = 0;
            for (PageReference pageReference2 : createRefStorage2) {
                j2 += pageReference2.count;
            }
            Page<K, V> createNode = createNode(this.map, splitKeys, createRefStorage2, j2, 0);
            if (isPersistent()) {
                recalculateMemory();
            }
            return createNode;
        }

        @Override // org.h2.mvstore.Page
        public void expand(int i, Object[] objArr, Object[] objArr2) {
            throw new UnsupportedOperationException();
        }

        @Override // org.h2.mvstore.Page
        public long getTotalCount() {
            if (!$assertionsDisabled && isComplete() && this.totalCount != calculateTotalCount()) {
                throw new AssertionError("Total count: " + this.totalCount + " != " + calculateTotalCount());
            }
            return this.totalCount;
        }

        private long calculateTotalCount() {
            long j = 0;
            int keyCount = getKeyCount();
            for (int i = 0; i <= keyCount; i++) {
                j += this.children[i].count;
            }
            return j;
        }

        void recalculateTotalCount() {
            this.totalCount = calculateTotalCount();
        }

        @Override // org.h2.mvstore.Page
        long getCounts(int i) {
            return this.children[i].count;
        }

        @Override // org.h2.mvstore.Page
        public void setChild(int i, Page<K, V> page) {
            if (!$assertionsDisabled && page == null) {
                throw new AssertionError();
            }
            PageReference<K, V> pageReference = this.children[i];
            if (page != pageReference.getPage() || page.getPos() != pageReference.getPos()) {
                this.totalCount += page.getTotalCount() - pageReference.count;
                this.children = (PageReference[]) this.children.clone();
                this.children[i] = new PageReference<>(page);
            }
        }

        @Override // org.h2.mvstore.Page
        public V setValue(int i, V v) {
            throw new UnsupportedOperationException();
        }

        @Override // org.h2.mvstore.Page
        public void insertLeaf(int i, K k, V v) {
            throw new UnsupportedOperationException();
        }

        @Override // org.h2.mvstore.Page
        public void insertNode(int i, K k, Page<K, V> page) {
            int rawChildPageCount = getRawChildPageCount();
            insertKey(i, k);
            PageReference<K, V>[] createRefStorage = createRefStorage(rawChildPageCount + 1);
            DataUtils.copyWithGap(this.children, createRefStorage, rawChildPageCount, i);
            this.children = createRefStorage;
            this.children[i] = new PageReference<>(page);
            this.totalCount += page.getTotalCount();
            if (isPersistent()) {
                addMemory(32);
            }
        }

        @Override // org.h2.mvstore.Page
        public void remove(int i) {
            int rawChildPageCount = getRawChildPageCount();
            super.remove(i);
            if (isPersistent()) {
                if (this.map.isMemoryEstimationAllowed()) {
                    addMemory((-getMemory()) / rawChildPageCount);
                } else {
                    addMemory(-32);
                }
            }
            this.totalCount -= this.children[i].count;
            PageReference<K, V>[] createRefStorage = createRefStorage(rawChildPageCount - 1);
            DataUtils.copyExcept(this.children, createRefStorage, rawChildPageCount, i);
            this.children = createRefStorage;
        }

        @Override // org.h2.mvstore.Page
        public int removeAllRecursive(long j) {
            int removePage = removePage(j);
            if (isPersistent()) {
                int childPageCount = this.map.getChildPageCount(this);
                for (int i = 0; i < childPageCount; i++) {
                    PageReference<K, V> pageReference = this.children[i];
                    Page<K, V> page = pageReference.getPage();
                    if (page != null) {
                        removePage += page.removeAllRecursive(j);
                    } else {
                        long pos = pageReference.getPos();
                        if (!$assertionsDisabled && !DataUtils.isPageSaved(pos)) {
                            throw new AssertionError();
                        }
                        if (DataUtils.isLeafPosition(pos)) {
                            this.map.store.accountForRemovedPage(pos, j, this.map.isSingleWriter(), -1);
                        } else {
                            removePage += this.map.readPage(pos).removeAllRecursive(j);
                        }
                    }
                }
            }
            return removePage;
        }

        @Override // org.h2.mvstore.Page
        public CursorPos<K, V> getPrependCursorPos(CursorPos<K, V> cursorPos) {
            return getChildPage(0).getPrependCursorPos(new CursorPos<>(this, 0, cursorPos));
        }

        @Override // org.h2.mvstore.Page
        public CursorPos<K, V> getAppendCursorPos(CursorPos<K, V> cursorPos) {
            int keyCount = getKeyCount();
            return getChildPage(keyCount).getAppendCursorPos(new CursorPos<>(this, keyCount, cursorPos));
        }

        @Override // org.h2.mvstore.Page
        protected void readPayLoad(ByteBuffer byteBuffer) {
            int keyCount = getKeyCount();
            this.children = createRefStorage(keyCount + 1);
            long[] jArr = new long[keyCount + 1];
            for (int i = 0; i <= keyCount; i++) {
                jArr[i] = byteBuffer.getLong();
            }
            long j = 0;
            for (int i2 = 0; i2 <= keyCount; i2++) {
                long readVarLong = DataUtils.readVarLong(byteBuffer);
                long j2 = jArr[i2];
                if (!$assertionsDisabled) {
                    if (j2 == 0) {
                        if (readVarLong != 0) {
                            throw new AssertionError();
                        }
                    } else if (readVarLong < 0) {
                        throw new AssertionError();
                    }
                }
                j += readVarLong;
                this.children[i2] = j2 == 0 ? PageReference.empty() : new PageReference<>(j2, readVarLong);
            }
            this.totalCount = j;
        }

        @Override // org.h2.mvstore.Page
        protected void writeValues(WriteBuffer writeBuffer) {
        }

        @Override // org.h2.mvstore.Page
        protected void writeChildren(WriteBuffer writeBuffer, boolean z) {
            int keyCount = getKeyCount();
            for (int i = 0; i <= keyCount; i++) {
                writeBuffer.putLong(this.children[i].getPos());
            }
            if (z) {
                for (int i2 = 0; i2 <= keyCount; i2++) {
                    writeBuffer.putVarLong(this.children[i2].count);
                }
            }
        }

        @Override // org.h2.mvstore.Page
        void writeUnsavedRecursive(FileStore.PageSerializationManager pageSerializationManager) {
            if (!isSaved()) {
                int write = write(pageSerializationManager);
                writeChildrenRecursive(pageSerializationManager);
                WriteBuffer buffer = pageSerializationManager.getBuffer();
                int position = buffer.position();
                buffer.position(write);
                writeChildren(buffer, false);
                buffer.position(position);
            }
        }

        void writeChildrenRecursive(FileStore.PageSerializationManager pageSerializationManager) {
            int rawChildPageCount = getRawChildPageCount();
            for (int i = 0; i < rawChildPageCount; i++) {
                PageReference<K, V> pageReference = this.children[i];
                Page<K, V> page = pageReference.getPage();
                if (page != null) {
                    page.writeUnsavedRecursive(pageSerializationManager);
                    pageReference.resetPos();
                }
            }
        }

        @Override // org.h2.mvstore.Page
        void releaseSavedPages() {
            int rawChildPageCount = getRawChildPageCount();
            for (int i = 0; i < rawChildPageCount; i++) {
                this.children[i].clearPageReference();
            }
        }

        @Override // org.h2.mvstore.Page
        public int getRawChildPageCount() {
            return getKeyCount() + 1;
        }

        @Override // org.h2.mvstore.Page
        protected int calculateMemory() {
            return super.calculateMemory() + 121 + (getRawChildPageCount() * 32);
        }

        @Override // org.h2.mvstore.Page
        public void dump(StringBuilder sb) {
            super.dump(sb);
            int keyCount = getKeyCount();
            for (int i = 0; i <= keyCount; i++) {
                if (i > 0) {
                    sb.append(CharSequenceUtil.SPACE);
                }
                sb.append("[").append(Long.toHexString(this.children[i].getPos())).append("]");
                if (i < keyCount) {
                    sb.append(CharSequenceUtil.SPACE).append(getKey(i));
                }
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/Page$IncompleteNonLeaf.class */
    private static class IncompleteNonLeaf<K, V> extends NonLeaf<K, V> {
        private boolean complete;

        IncompleteNonLeaf(MVMap<K, V> mVMap, NonLeaf<K, V> nonLeaf) {
            super(mVMap, nonLeaf, constructEmptyPageRefs(nonLeaf.getRawChildPageCount()), nonLeaf.getTotalCount());
        }

        private static <K, V> PageReference<K, V>[] constructEmptyPageRefs(int i) {
            PageReference<K, V>[] createRefStorage = createRefStorage(i);
            Arrays.fill(createRefStorage, PageReference.empty());
            return createRefStorage;
        }

        @Override // org.h2.mvstore.Page.NonLeaf, org.h2.mvstore.Page
        void writeUnsavedRecursive(FileStore.PageSerializationManager pageSerializationManager) {
            if (this.complete) {
                super.writeUnsavedRecursive(pageSerializationManager);
            } else if (!isSaved()) {
                writeChildrenRecursive(pageSerializationManager);
            }
        }

        @Override // org.h2.mvstore.Page
        public boolean isComplete() {
            return this.complete;
        }

        @Override // org.h2.mvstore.Page
        public void setComplete() {
            recalculateTotalCount();
            this.complete = true;
        }

        @Override // org.h2.mvstore.Page.NonLeaf, org.h2.mvstore.Page
        public void dump(StringBuilder sb) {
            super.dump(sb);
            sb.append(", complete:").append(this.complete);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/Page$Leaf.class */
    public static class Leaf<K, V> extends Page<K, V> {
        private V[] values;
        static final /* synthetic */ boolean $assertionsDisabled;

        @Override // org.h2.mvstore.Page
        /* renamed from: clone */
        protected /* bridge */ /* synthetic */ Object mo1598clone() throws CloneNotSupportedException {
            return super.mo1598clone();
        }

        static {
            $assertionsDisabled = !Page.class.desiredAssertionStatus();
        }

        Leaf(MVMap<K, V> mVMap) {
            super(mVMap);
        }

        private Leaf(MVMap<K, V> mVMap, Leaf<K, V> leaf) {
            super(mVMap, leaf);
            this.values = leaf.values;
        }

        Leaf(MVMap<K, V> mVMap, K[] kArr, V[] vArr) {
            super(mVMap, kArr);
            this.values = vArr;
        }

        @Override // org.h2.mvstore.Page
        public int getNodeType() {
            return 0;
        }

        @Override // org.h2.mvstore.Page
        public Page<K, V> copy(MVMap<K, V> mVMap, boolean z) {
            return new Leaf(mVMap, this);
        }

        @Override // org.h2.mvstore.Page
        public Page<K, V> getChildPage(int i) {
            throw new UnsupportedOperationException();
        }

        @Override // org.h2.mvstore.Page
        public long getChildPagePos(int i) {
            throw new UnsupportedOperationException();
        }

        @Override // org.h2.mvstore.Page
        public V getValue(int i) {
            if (this.values == null) {
                return null;
            }
            return this.values[i];
        }

        @Override // org.h2.mvstore.Page
        public Page<K, V> split(int i) {
            if (!$assertionsDisabled && isSaved()) {
                throw new AssertionError();
            }
            int keyCount = getKeyCount() - i;
            K[] splitKeys = splitKeys(i, keyCount);
            V[] createValueStorage = createValueStorage(keyCount);
            if (this.values != null) {
                V[] createValueStorage2 = createValueStorage(i);
                System.arraycopy(this.values, 0, createValueStorage2, 0, i);
                System.arraycopy(this.values, i, createValueStorage, 0, keyCount);
                this.values = createValueStorage2;
            }
            Page<K, V> createLeaf = createLeaf(this.map, splitKeys, createValueStorage, 0);
            if (isPersistent()) {
                recalculateMemory();
            }
            return createLeaf;
        }

        @Override // org.h2.mvstore.Page
        public void expand(int i, K[] kArr, V[] vArr) {
            int keyCount = getKeyCount();
            expandKeys(i, kArr);
            if (this.values != null) {
                V[] createValueStorage = createValueStorage(keyCount + i);
                System.arraycopy(this.values, 0, createValueStorage, 0, keyCount);
                System.arraycopy(vArr, 0, createValueStorage, keyCount, i);
                this.values = createValueStorage;
            }
            if (isPersistent()) {
                recalculateMemory();
            }
        }

        @Override // org.h2.mvstore.Page
        public long getTotalCount() {
            return getKeyCount();
        }

        @Override // org.h2.mvstore.Page
        long getCounts(int i) {
            throw new UnsupportedOperationException();
        }

        @Override // org.h2.mvstore.Page
        public void setChild(int i, Page<K, V> page) {
            throw new UnsupportedOperationException();
        }

        @Override // org.h2.mvstore.Page
        public V setValue(int i, V v) {
            this.values = (V[]) ((Object[]) this.values.clone());
            V valueInternal = setValueInternal(i, v);
            if (isPersistent() && !this.map.isMemoryEstimationAllowed()) {
                addMemory(this.map.evaluateMemoryForValue(v) - this.map.evaluateMemoryForValue(valueInternal));
            }
            return valueInternal;
        }

        private V setValueInternal(int i, V v) {
            V v2 = this.values[i];
            this.values[i] = v;
            return v2;
        }

        @Override // org.h2.mvstore.Page
        public void insertLeaf(int i, K k, V v) {
            int keyCount = getKeyCount();
            insertKey(i, k);
            if (this.values != null) {
                V[] createValueStorage = createValueStorage(keyCount + 1);
                DataUtils.copyWithGap(this.values, createValueStorage, keyCount, i);
                this.values = createValueStorage;
                setValueInternal(i, v);
                if (isPersistent()) {
                    addMemory(8 + this.map.evaluateMemoryForValue(v));
                }
            }
        }

        @Override // org.h2.mvstore.Page
        public void insertNode(int i, K k, Page<K, V> page) {
            throw new UnsupportedOperationException();
        }

        @Override // org.h2.mvstore.Page
        public void remove(int i) {
            int keyCount = getKeyCount();
            super.remove(i);
            if (this.values != null) {
                if (isPersistent()) {
                    if (this.map.isMemoryEstimationAllowed()) {
                        addMemory((-getMemory()) / keyCount);
                    } else {
                        addMemory((-8) - this.map.evaluateMemoryForValue(getValue(i)));
                    }
                }
                V[] createValueStorage = createValueStorage(keyCount - 1);
                DataUtils.copyExcept(this.values, createValueStorage, keyCount, i);
                this.values = createValueStorage;
            }
        }

        @Override // org.h2.mvstore.Page
        public int removeAllRecursive(long j) {
            return removePage(j);
        }

        @Override // org.h2.mvstore.Page
        public CursorPos<K, V> getPrependCursorPos(CursorPos<K, V> cursorPos) {
            return new CursorPos<>(this, -1, cursorPos);
        }

        @Override // org.h2.mvstore.Page
        public CursorPos<K, V> getAppendCursorPos(CursorPos<K, V> cursorPos) {
            return new CursorPos<>(this, getKeyCount() ^ (-1), cursorPos);
        }

        @Override // org.h2.mvstore.Page
        protected void readPayLoad(ByteBuffer byteBuffer) {
            this.values = createValueStorage(getKeyCount());
            this.map.getValueType().read(byteBuffer, this.values, getKeyCount());
        }

        @Override // org.h2.mvstore.Page
        protected void writeValues(WriteBuffer writeBuffer) {
            this.map.getValueType().write(writeBuffer, this.values, getKeyCount());
        }

        @Override // org.h2.mvstore.Page
        protected void writeChildren(WriteBuffer writeBuffer, boolean z) {
        }

        @Override // org.h2.mvstore.Page
        void writeUnsavedRecursive(FileStore.PageSerializationManager pageSerializationManager) {
            if (!isSaved()) {
                write(pageSerializationManager);
            }
        }

        @Override // org.h2.mvstore.Page
        void releaseSavedPages() {
        }

        @Override // org.h2.mvstore.Page
        public int getRawChildPageCount() {
            return 0;
        }

        @Override // org.h2.mvstore.Page
        protected int calculateMemory() {
            return super.calculateMemory() + 113 + (this.values == null ? 0 : this.map.evaluateMemoryForValues(this.values, getKeyCount()));
        }

        @Override // org.h2.mvstore.Page
        public void dump(StringBuilder sb) {
            super.dump(sb);
            int keyCount = getKeyCount();
            for (int i = 0; i < keyCount; i++) {
                if (i > 0) {
                    sb.append(CharSequenceUtil.SPACE);
                }
                sb.append(getKey(i));
                if (this.values != null) {
                    sb.append(':');
                    sb.append(getValue(i));
                }
            }
        }
    }
}
