package com.alibaba.com.caucho.hessian.util;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/util/IntMap.class */
public class IntMap {
    public static final int NULL = -559038737;
    private static final Object DELETED = new Object();
    private Object[] _keys = new Object[256];
    private int[] _values = new int[256];
    private int _mask = this._keys.length - 1;
    private int _size = 0;

    public void clear() {
        Object[] keys = this._keys;
        int[] values = this._values;
        for (int i = keys.length - 1; i >= 0; i--) {
            keys[i] = null;
            values[i] = 0;
        }
        this._size = 0;
    }

    public int size() {
        return this._size;
    }

    public int get(Object key) {
        int mask = this._mask;
        int hash = (key.hashCode() % mask) & mask;
        Object[] keys = this._keys;
        while (true) {
            Object mapKey = keys[hash];
            if (mapKey == null) {
                return -559038737;
            }
            if (mapKey == key || mapKey.equals(key)) {
                break;
            }
            hash = (hash + 1) % mask;
        }
        return this._values[hash];
    }

    private void resize(int newSize) {
        int hash;
        Object[] newKeys = new Object[newSize];
        int[] newValues = new int[newSize];
        int mask = newKeys.length - 1;
        this._mask = mask;
        Object[] keys = this._keys;
        int[] values = this._values;
        for (int i = keys.length - 1; i >= 0; i--) {
            Object key = keys[i];
            if (key != null && key != DELETED) {
                int hashCode = (key.hashCode() % mask) & mask;
                while (true) {
                    hash = hashCode;
                    if (newKeys[hash] == null) {
                        break;
                    } else {
                        hashCode = (hash + 1) % mask;
                    }
                }
                newKeys[hash] = key;
                newValues[hash] = values[i];
            }
        }
        this._keys = newKeys;
        this._values = newValues;
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0073, code lost:            r0 = r4._values[r8];        r4._values[r8] = r6;     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0086, code lost:            return r0;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int put(java.lang.Object r5, int r6) {
        /*
            r4 = this;
            r0 = r4
            int r0 = r0._mask
            r7 = r0
            r0 = r5
            int r0 = r0.hashCode()
            r1 = r7
            int r0 = r0 % r1
            r1 = r7
            r0 = r0 & r1
            r8 = r0
            r0 = r4
            java.lang.Object[] r0 = r0._keys
            r9 = r0
        L15:
            r0 = r9
            r1 = r8
            r0 = r0[r1]
            r10 = r0
            r0 = r10
            if (r0 == 0) goto L29
            r0 = r10
            java.lang.Object r1 = com.alibaba.com.caucho.hessian.util.IntMap.DELETED
            if (r0 != r1) goto L59
        L29:
            r0 = r9
            r1 = r8
            r2 = r5
            r0[r1] = r2
            r0 = r4
            int[] r0 = r0._values
            r1 = r8
            r2 = r6
            r0[r1] = r2
            r0 = r4
            r1 = r0
            int r1 = r1._size
            r2 = 1
            int r1 = r1 + r2
            r0._size = r1
            r0 = r9
            int r0 = r0.length
            r1 = 4
            r2 = r4
            int r2 = r2._size
            int r1 = r1 * r2
            if (r0 > r1) goto L56
            r0 = r4
            r1 = 4
            r2 = r9
            int r2 = r2.length
            int r1 = r1 * r2
            r0.resize(r1)
        L56:
            r0 = -559038737(0xffffffffdeadbeef, float:-6.2598534E18)
            return r0
        L59:
            r0 = r5
            r1 = r10
            if (r0 == r1) goto L73
            r0 = r5
            r1 = r10
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L73
            r0 = r8
            r1 = 1
            int r0 = r0 + r1
            r1 = r7
            int r0 = r0 % r1
            r8 = r0
            goto L15
        L73:
            r0 = r4
            int[] r0 = r0._values
            r1 = r8
            r0 = r0[r1]
            r11 = r0
            r0 = r4
            int[] r0 = r0._values
            r1 = r8
            r2 = r6
            r0[r1] = r2
            r0 = r11
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.com.caucho.hessian.util.IntMap.put(java.lang.Object, int):int");
    }

    public int remove(Object key) {
        int mask = this._mask;
        int hashCode = (key.hashCode() % mask) & mask;
        while (true) {
            int hash = hashCode;
            Object mapKey = this._keys[hash];
            if (mapKey == null) {
                return -559038737;
            }
            if (mapKey == key) {
                this._keys[hash] = DELETED;
                this._size--;
                return this._values[hash];
            }
            hashCode = (hash + 1) % mask;
        }
    }

    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("IntMap[");
        boolean isFirst = true;
        for (int i = 0; i <= this._mask; i++) {
            if (this._keys[i] != null && this._keys[i] != DELETED) {
                if (!isFirst) {
                    sbuf.append(", ");
                }
                isFirst = false;
                sbuf.append(this._keys[i]);
                sbuf.append(":");
                sbuf.append(this._values[i]);
            }
        }
        sbuf.append("]");
        return sbuf.toString();
    }
}
