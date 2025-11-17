package cn.hutool.core.lang.mutable;

import cn.hutool.core.util.NumberUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/mutable/MutableDouble.class */
public class MutableDouble extends Number implements Comparable<MutableDouble>, Mutable<Number> {
    private static final long serialVersionUID = 1;
    private double value;

    public MutableDouble() {
    }

    public MutableDouble(double value) {
        this.value = value;
    }

    public MutableDouble(Number value) {
        this(value.doubleValue());
    }

    public MutableDouble(String value) throws NumberFormatException {
        this.value = Double.parseDouble(value);
    }

    @Override // cn.hutool.core.lang.mutable.Mutable
    /* renamed from: get, reason: merged with bridge method [inline-methods] */
    public Number get2() {
        return Double.valueOf(this.value);
    }

    public void set(double value) {
        this.value = value;
    }

    @Override // cn.hutool.core.lang.mutable.Mutable
    public void set(Number value) {
        this.value = value.doubleValue();
    }

    public MutableDouble increment() {
        this.value += 1.0d;
        return this;
    }

    public MutableDouble decrement() {
        this.value -= 1.0d;
        return this;
    }

    public MutableDouble add(double operand) {
        this.value += operand;
        return this;
    }

    public MutableDouble add(Number operand) {
        this.value += operand.doubleValue();
        return this;
    }

    public MutableDouble subtract(double operand) {
        this.value -= operand;
        return this;
    }

    public MutableDouble subtract(Number operand) {
        this.value -= operand.doubleValue();
        return this;
    }

    @Override // java.lang.Number
    public int intValue() {
        return (int) this.value;
    }

    @Override // java.lang.Number
    public long longValue() {
        return (long) this.value;
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
        return (obj instanceof MutableDouble) && Double.doubleToLongBits(((MutableDouble) obj).value) == Double.doubleToLongBits(this.value);
    }

    public int hashCode() {
        long bits = Double.doubleToLongBits(this.value);
        return (int) (bits ^ (bits >>> 32));
    }

    @Override // java.lang.Comparable
    public int compareTo(MutableDouble other) {
        return NumberUtil.compare(this.value, other.value);
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}
