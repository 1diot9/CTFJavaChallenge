package com.alibaba.com.caucho.hessian.util;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/util/IdentityIntMap.class */
public class IdentityIntMap {
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
        int hash = (System.identityHashCode(key) % mask) & mask;
        Object[] keys = this._keys;
        while (true) {
            Object mapKey = keys[hash];
            if (mapKey == null) {
                return -559038737;
            }
            if (mapKey == key) {
                return this._values[hash];
            }
            hash = (hash + 1) % mask;
        }
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
                int identityHashCode = (System.identityHashCode(key) % mask) & mask;
                while (true) {
                    hash = identityHashCode;
                    if (newKeys[hash] == null) {
                        break;
                    } else {
                        identityHashCode = (hash + 1) % mask;
                    }
                }
                newKeys[hash] = key;
                newValues[hash] = values[i];
            }
        }
        this._keys = newKeys;
        this._values = newValues;
    }

    public int put(Object key, int value) {
        int mask = this._mask;
        int hash = (System.identityHashCode(key) % mask) & mask;
        Object[] keys = this._keys;
        while (true) {
            Object testKey = keys[hash];
            if (testKey == null || testKey == DELETED) {
                break;
            }
            if (key != testKey) {
                hash = (hash + 1) % mask;
            } else {
                int old = this._values[hash];
                this._values[hash] = value;
                return old;
            }
        }
        keys[hash] = key;
        this._values[hash] = value;
        this._size++;
        if (keys.length <= 4 * this._size) {
            resize(4 * keys.length);
            return -559038737;
        }
        return -559038737;
    }

    public int remove(Object key) {
        int mask = this._mask;
        int identityHashCode = (System.identityHashCode(key) % mask) & mask;
        while (true) {
            int hash = identityHashCode;
            Object mapKey = this._keys[hash];
            if (mapKey == null) {
                return -559038737;
            }
            if (mapKey == key) {
                this._keys[hash] = DELETED;
                this._size--;
                return this._values[hash];
            }
            identityHashCode = (hash + 1) % mask;
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
