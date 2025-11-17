package cn.hutool.core.lang.hash;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/hash/Number128.class */
public class Number128 extends Number {
    private static final long serialVersionUID = 1;
    private long lowValue;
    private long highValue;

    public Number128(long lowValue, long highValue) {
        this.lowValue = lowValue;
        this.highValue = highValue;
    }

    public long getLowValue() {
        return this.lowValue;
    }

    public void setLowValue(long lowValue) {
        this.lowValue = lowValue;
    }

    public long getHighValue() {
        return this.highValue;
    }

    public void setHighValue(long hiValue) {
        this.highValue = hiValue;
    }

    public long[] getLongArray() {
        return new long[]{this.lowValue, this.highValue};
    }

    @Override // java.lang.Number
    public int intValue() {
        return (int) longValue();
    }

    @Override // java.lang.Number
    public long longValue() {
        return this.lowValue;
    }

    @Override // java.lang.Number
    public float floatValue() {
        return (float) longValue();
    }

    @Override // java.lang.Number
    public double doubleValue() {
        return longValue();
    }
}
