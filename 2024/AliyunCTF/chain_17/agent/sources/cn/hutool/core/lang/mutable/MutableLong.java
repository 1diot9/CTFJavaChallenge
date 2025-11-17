package cn.hutool.core.lang.mutable;

import cn.hutool.core.util.NumberUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/mutable/MutableLong.class */
public class MutableLong extends Number implements Comparable<MutableLong>, Mutable<Number> {
    private static final long serialVersionUID = 1;
    private long value;

    public MutableLong() {
    }

    public MutableLong(long value) {
        this.value = value;
    }

    public MutableLong(Number value) {
        this(value.longValue());
    }

    public MutableLong(String value) throws NumberFormatException {
        this.value = Long.parseLong(value);
    }

    @Override // cn.hutool.core.lang.mutable.Mutable
    /* renamed from: get, reason: merged with bridge method [inline-methods] */
    public Number get2() {
        return Long.valueOf(this.value);
    }

    public void set(long value) {
        this.value = value;
    }

    @Override // cn.hutool.core.lang.mutable.Mutable
    public void set(Number value) {
        this.value = value.longValue();
    }

    public MutableLong increment() {
        this.value += serialVersionUID;
        return this;
    }

    public MutableLong decrement() {
        this.value -= serialVersionUID;
        return this;
    }

    public MutableLong add(long operand) {
        this.value += operand;
        return this;
    }

    public MutableLong add(Number operand) {
        this.value += operand.longValue();
        return this;
    }

    public MutableLong subtract(long operand) {
        this.value -= operand;
        return this;
    }

    public MutableLong subtract(Number operand) {
        this.value -= operand.longValue();
        return this;
    }

    @Override // java.lang.Number
    public int intValue() {
        return (int) this.value;
    }

    @Override // java.lang.Number
    public long longValue() {
        return this.value;
    }

    @Override // java.lang.Number
    public float floatValue() {
        return (float) this.value;
    }

    @Override // java.lang.Number
    public double doubleValue() {
        return this.value;
    }

    public boolean equals(Object obj) {
        return (obj instanceof MutableLong) && this.value == ((MutableLong) obj).longValue();
    }

    public int hashCode() {
        return (int) (this.value ^ (this.value >>> 32));
    }

    @Override // java.lang.Comparable
    public int compareTo(MutableLong other) {
        return NumberUtil.compare(this.value, other.value);
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}
