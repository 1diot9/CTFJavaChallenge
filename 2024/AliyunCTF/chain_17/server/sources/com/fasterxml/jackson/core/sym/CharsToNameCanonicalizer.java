package com.fasterxml.jackson.core.sym;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.exc.StreamConstraintsException;
import com.fasterxml.jackson.core.util.InternCache;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/sym/CharsToNameCanonicalizer.class */
public final class CharsToNameCanonicalizer {
    public static final int HASH_MULT = 33;
    private static final int DEFAULT_T_SIZE = 64;
    private static final int MAX_T_SIZE = 65536;
    static final int MAX_ENTRIES_FOR_REUSE = 12000;
    static final int MAX_COLL_CHAIN_LENGTH = 150;
    protected final CharsToNameCanonicalizer _parent;
    protected final AtomicReference<TableInfo> _tableInfo;
    protected final int _seed;
    protected final int _flags;
    protected boolean _canonicalize;
    protected String[] _symbols;
    protected Bucket[] _buckets;
    protected int _size;
    protected int _sizeThreshold;
    protected int _indexMask;
    protected int _longestCollisionList;
    protected boolean _hashShared;
    protected BitSet _overflows;

    private CharsToNameCanonicalizer(int seed) {
        this._parent = null;
        this._seed = seed;
        this._canonicalize = true;
        this._flags = -1;
        this._hashShared = false;
        this._longestCollisionList = 0;
        this._tableInfo = new AtomicReference<>(TableInfo.createInitial(64));
    }

    private CharsToNameCanonicalizer(CharsToNameCanonicalizer parent, int flags, int seed, TableInfo parentState) {
        this._parent = parent;
        this._seed = seed;
        this._tableInfo = null;
        this._flags = flags;
        this._canonicalize = JsonFactory.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(flags);
        this._symbols = parentState.symbols;
        this._buckets = parentState.buckets;
        this._size = parentState.size;
        this._longestCollisionList = parentState.longestCollisionList;
        int arrayLen = this._symbols.length;
        this._sizeThreshold = _thresholdSize(arrayLen);
        this._indexMask = arrayLen - 1;
        this._hashShared = true;
    }

    private static int _thresholdSize(int hashAreaSize) {
        return hashAreaSize - (hashAreaSize >> 2);
    }

    public static CharsToNameCanonicalizer createRoot() {
        long now = System.currentTimeMillis();
        int seed = (((int) now) + ((int) (now >>> 32))) | 1;
        return createRoot(seed);
    }

    protected static CharsToNameCanonicalizer createRoot(int seed) {
        return new CharsToNameCanonicalizer(seed);
    }

    public CharsToNameCanonicalizer makeChild(int flags) {
        return new CharsToNameCanonicalizer(this, flags, this._seed, this._tableInfo.get());
    }

    public void release() {
        if (maybeDirty() && this._parent != null && this._canonicalize) {
            this._parent.mergeChild(new TableInfo(this));
            this._hashShared = true;
        }
    }

    private void mergeChild(TableInfo childState) {
        int childCount = childState.size;
        TableInfo currState = this._tableInfo.get();
        if (childCount == currState.size) {
            return;
        }
        if (childCount > MAX_ENTRIES_FOR_REUSE) {
            childState = TableInfo.createInitial(64);
        }
        this._tableInfo.compareAndSet(currState, childState);
    }

    public int size() {
        if (this._tableInfo != null) {
            return this._tableInfo.get().size;
        }
        return this._size;
    }

    public int bucketCount() {
        return this._symbols.length;
    }

    public boolean maybeDirty() {
        return !this._hashShared;
    }

    public int hashSeed() {
        return this._seed;
    }

    public int collisionCount() {
        int count = 0;
        for (Bucket bucket : this._buckets) {
            if (bucket != null) {
                count += bucket.length;
            }
        }
        return count;
    }

    public int maxCollisionLength() {
        return this._longestCollisionList;
    }

    public String findSymbol(char[] buffer, int start, int len, int h) throws IOException {
        if (len < 1) {
            return "";
        }
        if (!this._canonicalize) {
            return new String(buffer, start, len);
        }
        int index = _hashToIndex(h);
        String sym = this._symbols[index];
        if (sym != null) {
            if (sym.length() == len) {
                int i = 0;
                while (sym.charAt(i) == buffer[start + i]) {
                    i++;
                    if (i == len) {
                        return sym;
                    }
                }
            }
            Bucket b = this._buckets[index >> 1];
            if (b != null) {
                String sym2 = b.has(buffer, start, len);
                if (sym2 != null) {
                    return sym2;
                }
                String sym3 = _findSymbol2(buffer, start, len, b.next);
                if (sym3 != null) {
                    return sym3;
                }
            }
        }
        return _addSymbol(buffer, start, len, h, index);
    }

    private String _findSymbol2(char[] buffer, int start, int len, Bucket b) {
        while (b != null) {
            String sym = b.has(buffer, start, len);
            if (sym != null) {
                return sym;
            }
            b = b.next;
        }
        return null;
    }

    private String _addSymbol(char[] buffer, int start, int len, int h, int index) throws IOException {
        if (this._hashShared) {
            copyArrays();
            this._hashShared = false;
        } else if (this._size >= this._sizeThreshold) {
            rehash();
            index = _hashToIndex(calcHash(buffer, start, len));
        }
        String newSymbol = new String(buffer, start, len);
        if (JsonFactory.Feature.INTERN_FIELD_NAMES.enabledIn(this._flags)) {
            newSymbol = InternCache.instance.intern(newSymbol);
        }
        this._size++;
        if (this._symbols[index] == null) {
            this._symbols[index] = newSymbol;
        } else {
            int bix = index >> 1;
            Bucket newB = new Bucket(newSymbol, this._buckets[bix]);
            int collLen = newB.length;
            if (collLen > 150) {
                _handleSpillOverflow(bix, newB, index);
            } else {
                this._buckets[bix] = newB;
                this._longestCollisionList = Math.max(collLen, this._longestCollisionList);
            }
        }
        return newSymbol;
    }

    private void _handleSpillOverflow(int bucketIndex, Bucket newBucket, int mainIndex) throws IOException {
        if (this._overflows == null) {
            this._overflows = new BitSet();
            this._overflows.set(bucketIndex);
        } else if (this._overflows.get(bucketIndex)) {
            if (JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW.enabledIn(this._flags)) {
                _reportTooManyCollisions(150);
            }
            this._canonicalize = false;
        } else {
            this._overflows.set(bucketIndex);
        }
        this._symbols[mainIndex] = newBucket.symbol;
        this._buckets[bucketIndex] = null;
        this._size -= newBucket.length;
        this._longestCollisionList = -1;
    }

    public int _hashToIndex(int rawHash) {
        int rawHash2 = rawHash + (rawHash >>> 15);
        int rawHash3 = rawHash2 ^ (rawHash2 << 7);
        return (rawHash3 + (rawHash3 >>> 3)) & this._indexMask;
    }

    public int calcHash(char[] buffer, int start, int len) {
        int hash = this._seed;
        int end = start + len;
        for (int i = start; i < end; i++) {
            hash = (hash * 33) + buffer[i];
        }
        if (hash == 0) {
            return 1;
        }
        return hash;
    }

    public int calcHash(String key) {
        int len = key.length();
        int hash = this._seed;
        for (int i = 0; i < len; i++) {
            hash = (hash * 33) + key.charAt(i);
        }
        if (hash == 0) {
            return 1;
        }
        return hash;
    }

    private void copyArrays() {
        String[] oldSyms = this._symbols;
        this._symbols = (String[]) Arrays.copyOf(oldSyms, oldSyms.length);
        Bucket[] oldBuckets = this._buckets;
        this._buckets = (Bucket[]) Arrays.copyOf(oldBuckets, oldBuckets.length);
    }

    private void rehash() throws IOException {
        int size = this._symbols.length;
        int newSize = size + size;
        if (newSize > 65536) {
            this._size = 0;
            this._canonicalize = false;
            this._symbols = new String[64];
            this._buckets = new Bucket[32];
            this._indexMask = 63;
            this._hashShared = false;
            return;
        }
        String[] oldSyms = this._symbols;
        Bucket[] oldBuckets = this._buckets;
        this._symbols = new String[newSize];
        this._buckets = new Bucket[newSize >> 1];
        this._indexMask = newSize - 1;
        this._sizeThreshold = _thresholdSize(newSize);
        int count = 0;
        int maxColl = 0;
        for (int i = 0; i < size; i++) {
            String symbol = oldSyms[i];
            if (symbol != null) {
                count++;
                int index = _hashToIndex(calcHash(symbol));
                if (this._symbols[index] == null) {
                    this._symbols[index] = symbol;
                } else {
                    int bix = index >> 1;
                    Bucket newB = new Bucket(symbol, this._buckets[bix]);
                    this._buckets[bix] = newB;
                    maxColl = Math.max(maxColl, newB.length);
                }
            }
        }
        int bucketSize = size >> 1;
        for (int i2 = 0; i2 < bucketSize; i2++) {
            Bucket bucket = oldBuckets[i2];
            while (true) {
                Bucket b = bucket;
                if (b != null) {
                    count++;
                    String symbol2 = b.symbol;
                    int index2 = _hashToIndex(calcHash(symbol2));
                    if (this._symbols[index2] == null) {
                        this._symbols[index2] = symbol2;
                    } else {
                        int bix2 = index2 >> 1;
                        Bucket newB2 = new Bucket(symbol2, this._buckets[bix2]);
                        this._buckets[bix2] = newB2;
                        maxColl = Math.max(maxColl, newB2.length);
                    }
                    bucket = b.next;
                }
            }
        }
        this._longestCollisionList = maxColl;
        this._overflows = null;
        if (count != this._size) {
            throw new IllegalStateException(String.format("Internal error on SymbolTable.rehash(): had %d entries; now have %d", Integer.valueOf(this._size), Integer.valueOf(count)));
        }
    }

    protected void _reportTooManyCollisions(int maxLen) throws StreamConstraintsException {
        throw new StreamConstraintsException("Longest collision chain in symbol table (of size " + this._size + ") now exceeds maximum, " + maxLen + " -- suspect a DoS attack based on hash collisions");
    }

    protected void verifyInternalConsistency() {
        int count = 0;
        int size = this._symbols.length;
        for (int i = 0; i < size; i++) {
            String symbol = this._symbols[i];
            if (symbol != null) {
                count++;
            }
        }
        int bucketSize = size >> 1;
        for (int i2 = 0; i2 < bucketSize; i2++) {
            Bucket bucket = this._buckets[i2];
            while (true) {
                Bucket b = bucket;
                if (b != null) {
                    count++;
                    bucket = b.next;
                }
            }
        }
        if (count != this._size) {
            throw new IllegalStateException(String.format("Internal error: expected internal size %d vs calculated count %d", Integer.valueOf(this._size), Integer.valueOf(count)));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/sym/CharsToNameCanonicalizer$Bucket.class */
    public static final class Bucket {
        public final String symbol;
        public final Bucket next;
        public final int length;

        public Bucket(String s, Bucket n) {
            this.symbol = s;
            this.next = n;
            this.length = n == null ? 1 : n.length + 1;
        }

        public String has(char[] buf, int start, int len) {
            if (this.symbol.length() != len) {
                return null;
            }
            int i = 0;
            while (this.symbol.charAt(i) == buf[start + i]) {
                i++;
                if (i >= len) {
                    return this.symbol;
                }
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/sym/CharsToNameCanonicalizer$TableInfo.class */
    public static final class TableInfo {
        final int size;
        final int longestCollisionList;
        final String[] symbols;
        final Bucket[] buckets;

        public TableInfo(int size, int longestCollisionList, String[] symbols, Bucket[] buckets) {
            this.size = size;
            this.longestCollisionList = longestCollisionList;
            this.symbols = symbols;
            this.buckets = buckets;
        }

        public TableInfo(CharsToNameCanonicalizer src) {
            this.size = src._size;
            this.longestCollisionList = src._longestCollisionList;
            this.symbols = src._symbols;
            this.buckets = src._buckets;
        }

        public static TableInfo createInitial(int sz) {
            return new TableInfo(0, 0, new String[sz], new Bucket[sz >> 1]);
        }
    }
}
