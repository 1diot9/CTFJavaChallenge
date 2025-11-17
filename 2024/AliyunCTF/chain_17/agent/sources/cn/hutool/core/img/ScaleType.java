package cn.hutool.core.img;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/img/ScaleType.class */
public enum ScaleType {
    DEFAULT(1),
    FAST(2),
    SMOOTH(4),
    REPLICATE(8),
    AREA_AVERAGING(16);

    private final int value;

    ScaleType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
