package cn.hutool.core.lang.mutable;

import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/mutable/MutableBool.class */
public class MutableBool implements Comparable<MutableBool>, Mutable<Boolean>, Serializable {
    private static final long serialVersionUID = 1;
    private boolean value;

    public MutableBool() {
    }

    public MutableBool(boolean value) {
        this.value = value;
    }

    public MutableBool(String value) throws NumberFormatException {
        this.value = Boolean.parseBoolean(value);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.lang.mutable.Mutable
    public Boolean get() {
        return Boolean.valueOf(this.value);
    }

    public void set(boolean value) {
        this.value = value;
    }

    @Override // cn.hutool.core.lang.mutable.Mutable
    public void set(Boolean value) {
        this.value = value.booleanValue();
    }

    public boolean equals(Object obj) {
        return (obj instanceof MutableBool) && this.value == ((MutableBool) obj).value;
    }

    public int hashCode() {
        return this.value ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
    }

    @Override // java.lang.Comparable
    public int compareTo(MutableBool other) {
        return Boolean.compare(this.value, other.value);
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}
