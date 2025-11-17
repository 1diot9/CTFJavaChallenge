package com.fasterxml.jackson.core.sym;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.exc.StreamConstraintsException;
import com.fasterxml.jackson.core.util.InternCache;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.asm.Opcodes;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/sym/ByteQuadsCanonicalizer.class */
public final class ByteQuadsCanonicalizer {
    private static final int DEFAULT_T_SIZE = 64;
    private static final int MAX_T_SIZE = 65536;
    private static final int MIN_HASH_SIZE = 16;
    protected static final int MAX_ENTRIES_FOR_REUSE = 6000;
    protected final ByteQuadsCanonicalizer _parent;
    protected final AtomicReference<TableInfo> _tableInfo;
    protected final int _seed;
    protected final boolean _intern;
    protected final boolean _failOnDoS;
    protected int[] _hashArea;
    protected int _hashSize;
    protected int _secondaryStart;
    protected int _tertiaryStart;
    protected int _tertiaryShift;
    protected int _count;
    protected String[] _names;
    protected int _spilloverEnd;
    protected int _longNameOffset;
    protected boolean _hashShared;
    private static final int MULT = 33;
    private static final int MULT2 = 65599;
    private static final int MULT3 = 31;

    private ByteQuadsCanonicalizer(int sz, int seed) {
        int curr;
        this._parent = null;
        this._count = 0;
        this._hashShared = true;
        this._seed = seed;
        this._intern = false;
        this._failOnDoS = true;
        if (sz < 16) {
            sz = 16;
        } else if ((sz & (sz - 1)) != 0) {
            int i = 16;
            while (true) {
                curr = i;
                if (curr >= sz) {
                    break;
                } else {
                    i = curr + curr;
                }
            }
            sz = curr;
        }
        this._tableInfo = new AtomicReference<>(TableInfo.createInitial(sz));
    }

    private ByteQuadsCanonicalizer(ByteQuadsCanonicalizer parent, int seed, TableInfo state, boolean intern, boolean failOnDoS) {
        this._parent = parent;
        this._seed = seed;
        this._intern = intern;
        this._failOnDoS = failOnDoS;
        this._tableInfo = null;
        this._count = state.count;
        this._hashSize = state.size;
        this._secondaryStart = this._hashSize << 2;
        this._tertiaryStart = this._secondaryStart + (this._secondaryStart >> 1);
        this._tertiaryShift = state.tertiaryShift;
        this._hashArea = state.mainHash;
        this._names = state.names;
        this._spilloverEnd = state.spilloverEnd;
        this._longNameOffset = state.longNameOffset;
        this._hashShared = true;
    }

    private ByteQuadsCanonicalizer(TableInfo state) {
        this._parent = null;
        this._seed = 0;
        this._intern = false;
        this._failOnDoS = true;
        this._tableInfo = null;
        this._count = -1;
        this._hashArea = state.mainHash;
        this._names = state.names;
        this._hashSize = state.size;
        int end = this._hashArea.length;
        this._secondaryStart = end;
        this._tertiaryStart = end;
        this._tertiaryShift = 1;
        this._spilloverEnd = end;
        this._longNameOffset = end;
        this._hashShared = true;
    }

    public static ByteQuadsCanonicalizer createRoot() {
        long now = System.currentTimeMillis();
        int seed = (((int) now) + ((int) (now >>> 32))) | 1;
        return createRoot(seed);
    }

    protected static ByteQuadsCanonicalizer createRoot(int seed) {
        return new ByteQuadsCanonicalizer(64, seed);
    }

    public ByteQuadsCanonicalizer makeChild(int flags) {
        return new ByteQuadsCanonicalizer(this, this._seed, this._tableInfo.get(), JsonFactory.Feature.INTERN_FIELD_NAMES.enabledIn(flags), JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW.enabledIn(flags));
    }

    public ByteQuadsCanonicalizer makeChildOrPlaceholder(int flags) {
        if (JsonFactory.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(flags)) {
            return new ByteQuadsCanonicalizer(this, this._seed, this._tableInfo.get(), JsonFactory.Feature.INTERN_FIELD_NAMES.enabledIn(flags), JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW.enabledIn(flags));
        }
        return new ByteQuadsCanonicalizer(this._tableInfo.get());
    }

    public void release() {
        if (this._parent != null && maybeDirty()) {
            this._parent.mergeChild(new TableInfo(this));
            this._hashShared = true;
        }
    }

    private void mergeChild(TableInfo childState) {
        int childCount = childState.count;
        TableInfo currState = this._tableInfo.get();
        if (childCount == currState.count) {
            return;
        }
        if (childCount > MAX_ENTRIES_FOR_REUSE) {
            childState = TableInfo.createInitial(64);
        }
        this._tableInfo.compareAndSet(currState, childState);
    }

    public int size() {
        if (this._tableInfo != null) {
            return this._tableInfo.get().count;
        }
        return this._count;
    }

    public int bucketCount() {
        return this._hashSize;
    }

    public boolean maybeDirty() {
        return !this._hashShared;
    }

    public int hashSeed() {
        return this._seed;
    }

    public boolean isCanonicalizing() {
        return this._parent != null;
    }

    public int primaryCount() {
        int count = 0;
        int end = this._secondaryStart;
        for (int offset = 3; offset < end; offset += 4) {
            if (this._hashArea[offset] != 0) {
                count++;
            }
        }
        return count;
    }

    public int secondaryCount() {
        int count = 0;
        int end = this._tertiaryStart;
        for (int offset = this._secondaryStart + 3; offset < end; offset += 4) {
            if (this._hashArea[offset] != 0) {
                count++;
            }
        }
        return count;
    }

    public int tertiaryCount() {
        int count = 0;
        int offset = this._tertiaryStart + 3;
        int end = offset + this._hashSize;
        while (offset < end) {
            if (this._hashArea[offset] != 0) {
                count++;
            }
            offset += 4;
        }
        return count;
    }

    public int spilloverCount() {
        return (this._spilloverEnd - _spilloverStart()) >> 2;
    }

    public int totalCount() {
        int count = 0;
        int end = this._hashSize << 3;
        for (int offset = 3; offset < end; offset += 4) {
            if (this._hashArea[offset] != 0) {
                count++;
            }
        }
        return count;
    }

    public String toString() {
        int pri = primaryCount();
        int sec = secondaryCount();
        int tert = tertiaryCount();
        int spill = spilloverCount();
        int total = totalCount();
        return String.format("[%s: size=%d, hashSize=%d, %d/%d/%d/%d pri/sec/ter/spill (=%s), total:%d]", getClass().getName(), Integer.valueOf(this._count), Integer.valueOf(this._hashSize), Integer.valueOf(pri), Integer.valueOf(sec), Integer.valueOf(tert), Integer.valueOf(spill), Integer.valueOf(pri + sec + tert + spill), Integer.valueOf(total));
    }

    public String findName(int q1) {
        int offset = _calcOffset(calcHash(q1));
        int[] hashArea = this._hashArea;
        int len = hashArea[offset + 3];
        if (len == 1) {
            if (hashArea[offset] == q1) {
                return this._names[offset >> 2];
            }
        } else if (len == 0) {
            return null;
        }
        int offset2 = this._secondaryStart + ((offset >> 3) << 2);
        int len2 = hashArea[offset2 + 3];
        if (len2 == 1) {
            if (hashArea[offset2] == q1) {
                return this._names[offset2 >> 2];
            }
        } else if (len2 == 0) {
            return null;
        }
        return _findSecondary(offset, q1);
    }

    public String findName(int q1, int q2) {
        int offset = _calcOffset(calcHash(q1, q2));
        int[] hashArea = this._hashArea;
        int len = hashArea[offset + 3];
        if (len == 2) {
            if (q1 == hashArea[offset] && q2 == hashArea[offset + 1]) {
                return this._names[offset >> 2];
            }
        } else if (len == 0) {
            return null;
        }
        int offset2 = this._secondaryStart + ((offset >> 3) << 2);
        int len2 = hashArea[offset2 + 3];
        if (len2 == 2) {
            if (q1 == hashArea[offset2] && q2 == hashArea[offset2 + 1]) {
                return this._names[offset2 >> 2];
            }
        } else if (len2 == 0) {
            return null;
        }
        return _findSecondary(offset, q1, q2);
    }

    public String findName(int q1, int q2, int q3) {
        int offset = _calcOffset(calcHash(q1, q2, q3));
        int[] hashArea = this._hashArea;
        int len = hashArea[offset + 3];
        if (len == 3) {
            if (q1 == hashArea[offset] && hashArea[offset + 1] == q2 && hashArea[offset + 2] == q3) {
                return this._names[offset >> 2];
            }
        } else if (len == 0) {
            return null;
        }
        int offset2 = this._secondaryStart + ((offset >> 3) << 2);
        int len2 = hashArea[offset2 + 3];
        if (len2 == 3) {
            if (q1 == hashArea[offset2] && hashArea[offset2 + 1] == q2 && hashArea[offset2 + 2] == q3) {
                return this._names[offset2 >> 2];
            }
        } else if (len2 == 0) {
            return null;
        }
        return _findSecondary(offset, q1, q2, q3);
    }

    public String findName(int[] q, int qlen) {
        if (qlen < 4) {
            switch (qlen) {
                case 1:
                    return findName(q[0]);
                case 2:
                    return findName(q[0], q[1]);
                case 3:
                    return findName(q[0], q[1], q[2]);
                default:
                    return "";
            }
        }
        int hash = calcHash(q, qlen);
        int offset = _calcOffset(hash);
        int[] hashArea = this._hashArea;
        int len = hashArea[offset + 3];
        if (hash == hashArea[offset] && len == qlen && _verifyLongName(q, qlen, hashArea[offset + 1])) {
            return this._names[offset >> 2];
        }
        if (len == 0) {
            return null;
        }
        int offset2 = this._secondaryStart + ((offset >> 3) << 2);
        int len2 = hashArea[offset2 + 3];
        if (hash == hashArea[offset2] && len2 == qlen && _verifyLongName(q, qlen, hashArea[offset2 + 1])) {
            return this._names[offset2 >> 2];
        }
        return _findSecondary(offset, hash, q, qlen);
    }

    private final int _calcOffset(int hash) {
        int ix = hash & (this._hashSize - 1);
        return ix << 2;
    }

    private String _findSecondary(int origOffset, int q1) {
        int offset = this._tertiaryStart + ((origOffset >> (this._tertiaryShift + 2)) << this._tertiaryShift);
        int[] hashArea = this._hashArea;
        int bucketSize = 1 << this._tertiaryShift;
        int end = offset + bucketSize;
        while (offset < end) {
            int len = hashArea[offset + 3];
            if (q1 == hashArea[offset] && 1 == len) {
                return this._names[offset >> 2];
            }
            if (len != 0) {
                offset += 4;
            } else {
                return null;
            }
        }
        for (int offset2 = _spilloverStart(); offset2 < this._spilloverEnd; offset2 += 4) {
            if (q1 == hashArea[offset2] && 1 == hashArea[offset2 + 3]) {
                return this._names[offset2 >> 2];
            }
        }
        return null;
    }

    private String _findSecondary(int origOffset, int q1, int q2) {
        int offset = this._tertiaryStart + ((origOffset >> (this._tertiaryShift + 2)) << this._tertiaryShift);
        int[] hashArea = this._hashArea;
        int bucketSize = 1 << this._tertiaryShift;
        int end = offset + bucketSize;
        while (offset < end) {
            int len = hashArea[offset + 3];
            if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && 2 == len) {
                return this._names[offset >> 2];
            }
            if (len != 0) {
                offset += 4;
            } else {
                return null;
            }
        }
        for (int offset2 = _spilloverStart(); offset2 < this._spilloverEnd; offset2 += 4) {
            if (q1 == hashArea[offset2] && q2 == hashArea[offset2 + 1] && 2 == hashArea[offset2 + 3]) {
                return this._names[offset2 >> 2];
            }
        }
        return null;
    }

    private String _findSecondary(int origOffset, int q1, int q2, int q3) {
        int offset = this._tertiaryStart + ((origOffset >> (this._tertiaryShift + 2)) << this._tertiaryShift);
        int[] hashArea = this._hashArea;
        int bucketSize = 1 << this._tertiaryShift;
        int end = offset + bucketSize;
        while (offset < end) {
            int len = hashArea[offset + 3];
            if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && q3 == hashArea[offset + 2] && 3 == len) {
                return this._names[offset >> 2];
            }
            if (len != 0) {
                offset += 4;
            } else {
                return null;
            }
        }
        for (int offset2 = _spilloverStart(); offset2 < this._spilloverEnd; offset2 += 4) {
            if (q1 == hashArea[offset2] && q2 == hashArea[offset2 + 1] && q3 == hashArea[offset2 + 2] && 3 == hashArea[offset2 + 3]) {
                return this._names[offset2 >> 2];
            }
        }
        return null;
    }

    private String _findSecondary(int origOffset, int hash, int[] q, int qlen) {
        int offset = this._tertiaryStart + ((origOffset >> (this._tertiaryShift + 2)) << this._tertiaryShift);
        int[] hashArea = this._hashArea;
        int bucketSize = 1 << this._tertiaryShift;
        int end = offset + bucketSize;
        while (offset < end) {
            int len = hashArea[offset + 3];
            if (hash == hashArea[offset] && qlen == len && _verifyLongName(q, qlen, hashArea[offset + 1])) {
                return this._names[offset >> 2];
            }
            if (len != 0) {
                offset += 4;
            } else {
                return null;
            }
        }
        for (int offset2 = _spilloverStart(); offset2 < this._spilloverEnd; offset2 += 4) {
            if (hash == hashArea[offset2] && qlen == hashArea[offset2 + 3] && _verifyLongName(q, qlen, hashArea[offset2 + 1])) {
                return this._names[offset2 >> 2];
            }
        }
        return null;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x000a. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:11:0x0058 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:14:0x006b A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:17:0x007e A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0091 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0093  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean _verifyLongName(int[] r6, int r7, int r8) {
        /*
            Method dump skipped, instructions count: 206
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer._verifyLongName(int[], int, int):boolean");
    }

    private boolean _verifyLongName2(int[] q, int qlen, int spillOffset) {
        int ix = 0;
        do {
            int i = ix;
            ix++;
            int i2 = spillOffset;
            spillOffset++;
            if (q[i] != this._hashArea[i2]) {
                return false;
            }
        } while (ix < qlen);
        return true;
    }

    public String addName(String name, int q1) throws StreamConstraintsException {
        _verifySharing();
        if (this._intern) {
            name = InternCache.instance.intern(name);
        }
        int offset = _findOffsetForAdd(calcHash(q1));
        this._hashArea[offset] = q1;
        this._hashArea[offset + 3] = 1;
        this._names[offset >> 2] = name;
        this._count++;
        return name;
    }

    public String addName(String name, int q1, int q2) throws StreamConstraintsException {
        _verifySharing();
        if (this._intern) {
            name = InternCache.instance.intern(name);
        }
        int hash = calcHash(q1, q2);
        int offset = _findOffsetForAdd(hash);
        this._hashArea[offset] = q1;
        this._hashArea[offset + 1] = q2;
        this._hashArea[offset + 3] = 2;
        this._names[offset >> 2] = name;
        this._count++;
        return name;
    }

    public String addName(String name, int q1, int q2, int q3) throws StreamConstraintsException {
        _verifySharing();
        if (this._intern) {
            name = InternCache.instance.intern(name);
        }
        int offset = _findOffsetForAdd(calcHash(q1, q2, q3));
        this._hashArea[offset] = q1;
        this._hashArea[offset + 1] = q2;
        this._hashArea[offset + 2] = q3;
        this._hashArea[offset + 3] = 3;
        this._names[offset >> 2] = name;
        this._count++;
        return name;
    }

    public String addName(String name, int[] q, int qlen) throws StreamConstraintsException {
        int offset;
        _verifySharing();
        if (this._intern) {
            name = InternCache.instance.intern(name);
        }
        switch (qlen) {
            case 1:
                offset = _findOffsetForAdd(calcHash(q[0]));
                this._hashArea[offset] = q[0];
                this._hashArea[offset + 3] = 1;
                break;
            case 2:
                offset = _findOffsetForAdd(calcHash(q[0], q[1]));
                this._hashArea[offset] = q[0];
                this._hashArea[offset + 1] = q[1];
                this._hashArea[offset + 3] = 2;
                break;
            case 3:
                offset = _findOffsetForAdd(calcHash(q[0], q[1], q[2]));
                this._hashArea[offset] = q[0];
                this._hashArea[offset + 1] = q[1];
                this._hashArea[offset + 2] = q[2];
                this._hashArea[offset + 3] = 3;
                break;
            default:
                int hash = calcHash(q, qlen);
                offset = _findOffsetForAdd(hash);
                this._hashArea[offset] = hash;
                int longStart = _appendLongName(q, qlen);
                this._hashArea[offset + 1] = longStart;
                this._hashArea[offset + 3] = qlen;
                break;
        }
        this._names[offset >> 2] = name;
        this._count++;
        return name;
    }

    private void _verifySharing() {
        if (this._hashShared) {
            if (this._parent == null) {
                if (this._count == 0) {
                    throw new IllegalStateException("Internal error: Cannot add names to Root symbol table");
                }
                throw new IllegalStateException("Internal error: Cannot add names to Placeholder symbol table");
            }
            this._hashArea = Arrays.copyOf(this._hashArea, this._hashArea.length);
            this._names = (String[]) Arrays.copyOf(this._names, this._names.length);
            this._hashShared = false;
        }
    }

    private int _findOffsetForAdd(int hash) throws StreamConstraintsException {
        int offset = _calcOffset(hash);
        int[] hashArea = this._hashArea;
        if (hashArea[offset + 3] == 0) {
            return offset;
        }
        if (_checkNeedForRehash()) {
            return _resizeAndFindOffsetForAdd(hash);
        }
        int offset2 = this._secondaryStart + ((offset >> 3) << 2);
        if (hashArea[offset2 + 3] == 0) {
            return offset2;
        }
        int offset22 = this._tertiaryStart + ((offset >> (this._tertiaryShift + 2)) << this._tertiaryShift);
        int bucketSize = 1 << this._tertiaryShift;
        int end = offset22 + bucketSize;
        while (offset22 < end) {
            if (hashArea[offset22 + 3] != 0) {
                offset22 += 4;
            } else {
                return offset22;
            }
        }
        int offset3 = this._spilloverEnd;
        this._spilloverEnd += 4;
        int end2 = this._hashSize << 3;
        if (this._spilloverEnd >= end2) {
            if (this._failOnDoS) {
                _reportTooManyCollisions();
            }
            return _resizeAndFindOffsetForAdd(hash);
        }
        return offset3;
    }

    private int _resizeAndFindOffsetForAdd(int hash) throws StreamConstraintsException {
        rehash();
        int offset = _calcOffset(hash);
        int[] hashArea = this._hashArea;
        if (hashArea[offset + 3] == 0) {
            return offset;
        }
        int offset2 = this._secondaryStart + ((offset >> 3) << 2);
        if (hashArea[offset2 + 3] == 0) {
            return offset2;
        }
        int offset22 = this._tertiaryStart + ((offset >> (this._tertiaryShift + 2)) << this._tertiaryShift);
        int bucketSize = 1 << this._tertiaryShift;
        int end = offset22 + bucketSize;
        while (offset22 < end) {
            if (hashArea[offset22 + 3] != 0) {
                offset22 += 4;
            } else {
                return offset22;
            }
        }
        int offset3 = this._spilloverEnd;
        this._spilloverEnd += 4;
        return offset3;
    }

    private boolean _checkNeedForRehash() {
        if (this._count > (this._hashSize >> 1)) {
            int spillCount = (this._spilloverEnd - _spilloverStart()) >> 2;
            if (spillCount > ((1 + this._count) >> 7) || this._count > this._hashSize * 0.8d) {
                return true;
            }
            return false;
        }
        return false;
    }

    private int _appendLongName(int[] quads, int qlen) {
        int start = this._longNameOffset;
        if (start + qlen > this._hashArea.length) {
            int toAdd = (start + qlen) - this._hashArea.length;
            int minAdd = Math.min(Opcodes.ACC_SYNTHETIC, this._hashSize);
            int newSize = this._hashArea.length + Math.max(toAdd, minAdd);
            this._hashArea = Arrays.copyOf(this._hashArea, newSize);
        }
        System.arraycopy(quads, 0, this._hashArea, start, qlen);
        this._longNameOffset += qlen;
        return start;
    }

    public int calcHash(int q1) {
        int hash = q1 ^ this._seed;
        int hash2 = hash + (hash >>> 16);
        int hash3 = hash2 ^ (hash2 << 3);
        return hash3 + (hash3 >>> 12);
    }

    public int calcHash(int q1, int q2) {
        int hash = q1 + (q1 >>> 15);
        int hash2 = ((hash ^ (hash >>> 9)) + (q2 * 33)) ^ this._seed;
        int hash3 = hash2 + (hash2 >>> 16);
        int hash4 = hash3 ^ (hash3 >>> 4);
        return hash4 + (hash4 << 3);
    }

    public int calcHash(int q1, int q2, int q3) {
        int hash = q1 ^ this._seed;
        int hash2 = (((hash + (hash >>> 9)) * 31) + q2) * 33;
        int hash3 = (hash2 + (hash2 >>> 15)) ^ q3;
        int hash4 = hash3 + (hash3 >>> 4);
        int hash5 = hash4 + (hash4 >>> 15);
        return hash5 ^ (hash5 << 9);
    }

    public int calcHash(int[] q, int qlen) {
        if (qlen < 4) {
            throw new IllegalArgumentException("qlen is too short, needs to be at least 4");
        }
        int hash = q[0] ^ this._seed;
        int hash2 = hash + (hash >>> 9) + q[1];
        int hash3 = ((hash2 + (hash2 >>> 15)) * 33) ^ q[2];
        int hash4 = hash3 + (hash3 >>> 4);
        for (int i = 3; i < qlen; i++) {
            int next = q[i];
            hash4 += next ^ (next >> 21);
        }
        int hash5 = hash4 * MULT2;
        int hash6 = hash5 + (hash5 >>> 19);
        return hash6 ^ (hash6 << 5);
    }

    private void rehash() throws StreamConstraintsException {
        this._hashShared = false;
        int[] oldHashArea = this._hashArea;
        String[] oldNames = this._names;
        int oldSize = this._hashSize;
        int oldCount = this._count;
        int newSize = oldSize + oldSize;
        int oldEnd = this._spilloverEnd;
        if (newSize > 65536) {
            nukeSymbols(true);
            return;
        }
        this._hashArea = new int[oldHashArea.length + (oldSize << 3)];
        this._hashSize = newSize;
        this._secondaryStart = newSize << 2;
        this._tertiaryStart = this._secondaryStart + (this._secondaryStart >> 1);
        this._tertiaryShift = _calcTertiaryShift(newSize);
        this._names = new String[oldNames.length << 1];
        nukeSymbols(false);
        int copyCount = 0;
        int[] q = new int[16];
        for (int offset = 0; offset < oldEnd; offset += 4) {
            int len = oldHashArea[offset + 3];
            if (len != 0) {
                copyCount++;
                String name = oldNames[offset >> 2];
                switch (len) {
                    case 1:
                        q[0] = oldHashArea[offset];
                        addName(name, q, 1);
                        break;
                    case 2:
                        q[0] = oldHashArea[offset];
                        q[1] = oldHashArea[offset + 1];
                        addName(name, q, 2);
                        break;
                    case 3:
                        q[0] = oldHashArea[offset];
                        q[1] = oldHashArea[offset + 1];
                        q[2] = oldHashArea[offset + 2];
                        addName(name, q, 3);
                        break;
                    default:
                        if (len > q.length) {
                            q = new int[len];
                        }
                        int qoff = oldHashArea[offset + 1];
                        System.arraycopy(oldHashArea, qoff, q, 0, len);
                        addName(name, q, len);
                        break;
                }
            }
        }
        if (copyCount != oldCount) {
            throw new IllegalStateException("Internal error: Failed rehash(), old count=" + oldCount + ", copyCount=" + copyCount);
        }
    }

    private void nukeSymbols(boolean fill) {
        this._count = 0;
        this._spilloverEnd = _spilloverStart();
        this._longNameOffset = this._hashSize << 3;
        if (fill) {
            Arrays.fill(this._hashArea, 0);
            Arrays.fill(this._names, (Object) null);
        }
    }

    private final int _spilloverStart() {
        int offset = this._hashSize;
        return (offset << 3) - offset;
    }

    protected void _reportTooManyCollisions() throws StreamConstraintsException {
        if (this._hashSize <= 1024) {
        } else {
            throw new StreamConstraintsException("Spill-over slots in symbol table with " + this._count + " entries, hash area of " + this._hashSize + " slots is now full (all " + (this._hashSize >> 3) + " slots -- suspect a DoS attack based on hash collisions. You can disable the check via `JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW`");
        }
    }

    static int _calcTertiaryShift(int primarySlots) {
        int tertSlots = primarySlots >> 2;
        if (tertSlots < 64) {
            return 4;
        }
        if (tertSlots <= 256) {
            return 5;
        }
        if (tertSlots <= 1024) {
            return 6;
        }
        return 7;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/sym/ByteQuadsCanonicalizer$TableInfo.class */
    public static final class TableInfo {
        public final int size;
        public final int count;
        public final int tertiaryShift;
        public final int[] mainHash;
        public final String[] names;
        public final int spilloverEnd;
        public final int longNameOffset;

        public TableInfo(int size, int count, int tertiaryShift, int[] mainHash, String[] names, int spilloverEnd, int longNameOffset) {
            this.size = size;
            this.count = count;
            this.tertiaryShift = tertiaryShift;
            this.mainHash = mainHash;
            this.names = names;
            this.spilloverEnd = spilloverEnd;
            this.longNameOffset = longNameOffset;
        }

        public TableInfo(ByteQuadsCanonicalizer src) {
            this.size = src._hashSize;
            this.count = src._count;
            this.tertiaryShift = src._tertiaryShift;
            this.mainHash = src._hashArea;
            this.names = src._names;
            this.spilloverEnd = src._spilloverEnd;
            this.longNameOffset = src._longNameOffset;
        }

        public static TableInfo createInitial(int sz) {
            int hashAreaSize = sz << 3;
            int tertShift = ByteQuadsCanonicalizer._calcTertiaryShift(sz);
            return new TableInfo(sz, 0, tertShift, new int[hashAreaSize], new String[sz << 1], hashAreaSize - sz, hashAreaSize);
        }
    }
}
