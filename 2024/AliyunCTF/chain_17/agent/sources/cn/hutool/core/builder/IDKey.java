package cn.hutool.core.builder;

import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/builder/IDKey.class */
final class IDKey implements Serializable {
    private static final long serialVersionUID = 1;
    private final Object value;
    private final int id;

    public IDKey(Object obj) {
        this.id = System.identityHashCode(obj);
        this.value = obj;
    }

    public int hashCode() {
        return this.id;
    }

    public boolean equals(Object other) {
        if (!(other instanceof IDKey)) {
            return false;
        }
        IDKey idKey = (IDKey) other;
        return this.id == idKey.id && this.value == idKey.value;
    }
}
