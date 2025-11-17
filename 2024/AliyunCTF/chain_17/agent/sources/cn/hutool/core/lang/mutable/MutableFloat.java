package cn.hutool.core.lang.mutable;

import cn.hutool.core.util.NumberUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/mutable/MutableFloat.class */
public class MutableFloat extends Number implements Comparable<MutableFloat>, Mutable<Number> {
    private static final long serialVersionUID = 1;
    private float value;

    public MutableFloat() {
    }

    public MutableFloat(float value) {
        this.value = value;
    }

    public MutableFloat(Number value) {
        this(value.floatValue());
    }

    public MutableFloat(String value) throws NumberFormatException {
        this.value = Float.parseFloat(value);
    }

    @Override // cn.hutool.core.lang.mutable.Mutable
    /* renamed from: get, reason: merged with bridge method [inline-methods] */
    public Number get2() {
        return Float.valueOf(this.value);
    }

    public void set(float value) {
        this.value = value;
    }

    @Override // cn.hutool.core.lang.mutable.Mutable
    public void set(Number value) {
        this.value = value.floatValue();
    }

    public MutableFloat increment() {
        this.value += 1.0f;
        return this;
    }

    public MutableFloat decrement() {
        this.value -= 1.0f;
        return this;
    }

    public MutableFloat add(float operand) {
        this.value += operand;
        return this;
    }

    public MutableFloat add(Number operand) {
        this.value += operand.floatValue();
        return this;
    }

    public MutableFloat subtract(float operand) {
        this.value -= operand;
        return this;
    }

    public MutableFloat subtract(Number operand) {
        this.value -= operand.floatValue();
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
        return this.value;
    }

    @Override // java.lang.Number
    public double doubleValue() {
        return this.value;
    }

    public boolean equals(Object obj) {
        return (obj instanceof MutableFloat) && Float.floatToIntBits(((MutableFloat) obj).value) == Float.floatToIntBits(this.value);
    }

    public int hashCode() {
        return Float.floatToIntBits(this.value);
    }

    @Override // java.lang.Comparable
    public int compareTo(MutableFloat other) {
        return NumberUtil.compare(this.value, other.value);
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}
