package cn.hutool.core.lang.mutable;

import cn.hutool.core.util.NumberUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/mutable/MutableShort.class */
public class MutableShort extends Number implements Comparable<MutableShort>, Mutable<Number> {
    private static final long serialVersionUID = 1;
    private short value;

    public MutableShort() {
    }

    public MutableShort(short value) {
        this.value = value;
    }

    public MutableShort(Number value) {
        this(value.shortValue());
    }

    public MutableShort(String value) throws NumberFormatException {
        this.value = Short.parseShort(value);
    }

    @Override // cn.hutool.core.lang.mutable.Mutable
    /* renamed from: get */
    public Number get2() {
        return Short.valueOf(this.value);
    }

    public void set(short value) {
        this.value = value;
    }

    @Override // cn.hutool.core.lang.mutable.Mutable
    public void set(Number value) {
        this.value = value.shortValue();
    }

    public MutableShort increment() {
        this.value = (short) (this.value + 1);
        return this;
    }

    public MutableShort decrement() {
        this.value = (short) (this.value - 1);
        return this;
    }

    public MutableShort add(short operand) {
        this.value = (short) (this.value + operand);
        return this;
    }

    public MutableShort add(Number operand) {
        this.value = (short) (this.value + operand.shortValue());
        return this;
    }

    public MutableShort subtract(short operand) {
        this.value = (short) (this.value - operand);
        return this;
    }

    public MutableShort subtract(Number operand) {
        this.value = (short) (this.value - operand.shortValue());
        return this;
    }

    @Override // java.lang.Number
    public short shortValue() {
        return this.value;
    }

    @Override // java.lang.Number
    public int intValue() {
        return this.value;
    }

    @Override // java.lang.Number
    public long longValue() {
        return this.value;
    }

    @Override // java.lang.Number
    public float floatValue() {
        return this.value;
    }

    @Override // java.lang.Number
    public double doubleValue() {
        return this.value;
    }

    public boolean equals(Object obj) {
        return (obj instanceof MutableShort) && this.value == ((MutableShort) obj).shortValue();
    }

    public int hashCode() {
        return this.value;
    }

    @Override // java.lang.Comparable
    public int compareTo(MutableShort other) {
        return NumberUtil.compare(this.value, other.value);
    }

    public String toString() {
        return String.valueOf((int) this.value);
    }
}
