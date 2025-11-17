package cn.hutool.poi.word;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/word/PicType.class */
public enum PicType {
    EMF(2),
    WMF(3),
    PICT(4),
    JPEG(5),
    PNG(6),
    DIB(7),
    GIF(8),
    TIFF(9),
    EPS(10),
    WPG(12);

    private final int value;

    PicType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
