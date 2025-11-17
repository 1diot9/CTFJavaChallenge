package cn.hutool.core.lang.mutable;

import cn.hutool.core.util.NumberUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/mutable/MutableByte.class */
public class MutableByte extends Number implements Comparable<MutableByte>, Mutable<Number> {
    private static final long serialVersionUID = 1;
    private byte value;

    public MutableByte() {
    }

    public MutableByte(byte value) {
        this.value = value;
    }

    public MutableByte(Number value) {
        this(value.byteValue());
    }

    public MutableByte(String value) throws NumberFormatException {
        this.value = Byte.parseByte(value);
    }

    @Override // cn.hutool.core.lang.mutable.Mutable
    /* renamed from: get */
    public Number get2() {
        return Byte.valueOf(this.value);
    }

    public void set(byte value) {
        this.value = value;
    }

    @Override // cn.hutool.core.lang.mutable.Mutable
    public void set(Number value) {
        this.value = value.byteValue();
    }

    public MutableByte increment() {
        this.value = (byte) (this.value + 1);
        return this;
    }

    public MutableByte decrement() {
        this.value = (byte) (this.value - 1);
        return this;
    }

    public MutableByte add(byte operand) {
        this.value = (byte) (this.value + operand);
        return this;
    }

    public MutableByte add(Number operand) {
        this.value = (byte) (this.value + operand.byteValue());
        return this;
    }

    public MutableByte subtract(byte operand) {
        this.value = (byte) (this.value - operand);
        return this;
    }

    public MutableByte subtract(Number operand) {
        this.value = (byte) (this.value - operand.byteValue());
        return this;
    }

    @Override // java.lang.Number
    public byte byteValue() {
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
        return (obj instanceof MutableByte) && this.value == ((MutableByte) obj).byteValue();
    }

    public int hashCode() {
        return this.value;
    }

    @Override // java.lang.Comparable
    public int compareTo(MutableByte other) {
        return NumberUtil.compare(this.value, other.value);
    }

    public String toString() {
        return String.valueOf((int) this.value);
    }
}
