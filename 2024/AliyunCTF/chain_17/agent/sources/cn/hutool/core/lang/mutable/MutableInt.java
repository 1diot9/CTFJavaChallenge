package cn.hutool.core.lang.mutable;

import cn.hutool.core.util.NumberUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/mutable/MutableInt.class */
public class MutableInt extends Number implements Comparable<MutableInt>, Mutable<Number> {
    private static final long serialVersionUID = 1;
    private int value;

    public MutableInt() {
    }

    public MutableInt(int value) {
        this.value = value;
    }

    public MutableInt(Number value) {
        this(value.intValue());
    }

    public MutableInt(String value) throws NumberFormatException {
        this.value = Integer.parseInt(value);
    }

    @Override // cn.hutool.core.lang.mutable.Mutable
    /* renamed from: get */
    public Number get2() {
        return Integer.valueOf(this.value);
    }

    public void set(int value) {
        this.value = value;
    }

    @Override // cn.hutool.core.lang.mutable.Mutable
    public void set(Number value) {
        this.value = value.intValue();
    }

    public MutableInt increment() {
        this.value++;
        return this;
    }

    public MutableInt decrement() {
        this.value--;
        return this;
    }

    public MutableInt add(int operand) {
        this.value += operand;
        return this;
    }

    public MutableInt add(Number operand) {
        this.value += operand.intValue();
        return this;
    }

    public MutableInt subtract(int operand) {
        this.value -= operand;
        return this;
    }

    public MutableInt subtract(Number operand) {
        this.value -= operand.intValue();
        return this;
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
        return (obj instanceof MutableInt) && this.value == ((MutableInt) obj).intValue();
    }

    public int hashCode() {
        return this.value;
    }

    @Override // java.lang.Comparable
    public int compareTo(MutableInt other) {
        return NumberUtil.compare(this.value, other.value);
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}
