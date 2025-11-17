package org.h2.mvstore;

import cn.hutool.core.text.CharSequenceUtil;
import java.util.BitSet;
import org.h2.util.MathUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/FreeSpaceBitSet.class */
public class FreeSpaceBitSet {
    private static final boolean DETAILED_INFO = false;
    private final int firstFreeBlock;
    private final int blockSize;
    private final BitSet set = new BitSet();
    private int failureFlags;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !FreeSpaceBitSet.class.desiredAssertionStatus();
    }

    public FreeSpaceBitSet(int i, int i2) {
        this.firstFreeBlock = i;
        this.blockSize = i2;
        clear();
    }

    public void clear() {
        this.set.clear();
        this.set.set(0, this.firstFreeBlock);
    }

    public boolean isUsed(long j, int i) {
        int block = getBlock(j);
        int blockCount = getBlockCount(i);
        for (int i2 = block; i2 < block + blockCount; i2++) {
            if (!this.set.get(i2)) {
                return false;
            }
        }
        return true;
    }

    public boolean isFree(long j, int i) {
        int block = getBlock(j);
        int blockCount = getBlockCount(i);
        for (int i2 = block; i2 < block + blockCount; i2++) {
            if (this.set.get(i2)) {
                return false;
            }
        }
        return true;
    }

    public long allocate(int i) {
        return allocate(i, 0L, 0L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long allocate(int i, long j, long j2) {
        return getPos(allocate(getBlockCount(i), (int) j, (int) j2, true));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long predictAllocation(int i, long j, long j2) {
        return allocate(i, (int) j, (int) j2, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFragmented() {
        return Integer.bitCount(this.failureFlags & 15) > 1;
    }

    private int allocate(int i, int i2, int i3, boolean z) {
        int nextClearBit;
        int nextSetBit;
        int i4 = 0;
        int i5 = 0;
        while (true) {
            nextClearBit = this.set.nextClearBit(i5);
            nextSetBit = this.set.nextSetBit(nextClearBit + 1);
            int i6 = nextSetBit - nextClearBit;
            if (nextSetBit < 0 || i6 >= i) {
                if ((i3 >= 0 && nextClearBit >= i3) || nextClearBit + i <= i2) {
                    break;
                }
                if (i3 < 0) {
                    nextClearBit = getAfterLastBlock();
                    nextSetBit = -1;
                    break;
                }
                i5 = i3;
            } else {
                i4 += i6;
                i5 = nextSetBit;
            }
        }
        if (!$assertionsDisabled && this.set.nextSetBit(nextClearBit) != -1 && this.set.nextSetBit(nextClearBit) < nextClearBit + i) {
            throw new AssertionError("Double alloc: " + Integer.toHexString(nextClearBit) + "/" + Integer.toHexString(i) + CharSequenceUtil.SPACE + this);
        }
        if (z) {
            this.set.set(nextClearBit, nextClearBit + i);
        } else {
            this.failureFlags <<= 1;
            if (nextSetBit < 0 && i4 > 4 * i) {
                this.failureFlags |= 1;
            }
        }
        return nextClearBit;
    }

    public void markUsed(long j, int i) {
        int block = getBlock(j);
        int blockCount = getBlockCount(i);
        if (this.set.nextSetBit(block) != -1 && this.set.nextSetBit(block) < block + blockCount) {
            throw DataUtils.newMVStoreException(6, "Double mark: " + Integer.toHexString(block) + "/" + Integer.toHexString(blockCount) + CharSequenceUtil.SPACE + this, new Object[0]);
        }
        this.set.set(block, block + blockCount);
    }

    public void free(long j, int i) {
        int block = getBlock(j);
        int blockCount = getBlockCount(i);
        if (!$assertionsDisabled && this.set.nextClearBit(block) < block + blockCount) {
            throw new AssertionError("Double free: " + Integer.toHexString(block) + "/" + Integer.toHexString(blockCount) + CharSequenceUtil.SPACE + this);
        }
        this.set.clear(block, block + blockCount);
    }

    private long getPos(int i) {
        return i * this.blockSize;
    }

    private int getBlock(long j) {
        return (int) (j / this.blockSize);
    }

    private int getBlockCount(int i) {
        return MathUtils.roundUpInt(i, this.blockSize) / this.blockSize;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getFillRate() {
        int cardinality = this.set.cardinality() - this.firstFreeBlock;
        int length = this.set.length() - this.firstFreeBlock;
        if (length == 0) {
            return 0;
        }
        return (int) ((((100 * cardinality) + length) - 1) / length);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getFirstFree() {
        return getPos(this.set.nextClearBit(0));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getLastFree() {
        return getPos(getAfterLastBlock());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAfterLastBlock() {
        return this.set.previousSetBit(this.set.size() - 1) + 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getMovePriority(int i) {
        int previousSetBit;
        int previousClearBit = this.set.previousClearBit(i);
        if (previousClearBit < 0) {
            previousClearBit = this.firstFreeBlock;
            previousSetBit = 0;
        } else {
            previousSetBit = previousClearBit - this.set.previousSetBit(previousClearBit);
        }
        int nextClearBit = this.set.nextClearBit(i);
        int nextSetBit = this.set.nextSetBit(nextClearBit);
        if (nextSetBit >= 0) {
            previousSetBit += nextSetBit - nextClearBit;
        }
        return (((nextClearBit - previousClearBit) - 1) * 1000) / (previousSetBit + 1);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 > 0) {
                sb.append(", ");
            }
            int nextClearBit = this.set.nextClearBit(i2);
            sb.append(Integer.toHexString(nextClearBit)).append('-');
            int nextSetBit = this.set.nextSetBit(nextClearBit + 1);
            if (nextSetBit >= 0) {
                sb.append(Integer.toHexString(nextSetBit - 1));
                i = nextSetBit + 1;
            } else {
                sb.append(']');
                return sb.toString();
            }
        }
    }
}
