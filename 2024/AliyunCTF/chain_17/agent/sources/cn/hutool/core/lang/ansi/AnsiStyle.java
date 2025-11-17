package cn.hutool.core.lang.ansi;

import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/ansi/AnsiStyle.class */
public enum AnsiStyle implements AnsiElement {
    NORMAL(0),
    BOLD(1),
    FAINT(2),
    ITALIC(3),
    UNDERLINE(4);

    private final int code;

    AnsiStyle(int code) {
        this.code = code;
    }

    @Override // cn.hutool.core.lang.ansi.AnsiElement
    public int getCode() {
        return this.code;
    }

    @Override // java.lang.Enum, cn.hutool.core.lang.ansi.AnsiElement
    public String toString() {
        return StrUtil.toString(Integer.valueOf(this.code));
    }
}
