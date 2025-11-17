package cn.hutool.poi.word;

import java.io.File;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/word/WordUtil.class */
public class WordUtil {
    public static Word07Writer getWriter() {
        return new Word07Writer();
    }

    public static Word07Writer getWriter(File destFile) {
        return new Word07Writer(destFile);
    }
}
