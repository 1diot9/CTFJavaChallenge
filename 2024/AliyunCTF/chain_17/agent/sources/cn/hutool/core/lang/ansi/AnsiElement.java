package cn.hutool.core.lang.ansi;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/ansi/AnsiElement.class */
public interface AnsiElement {
    String toString();

    default int getCode() {
        return -1;
    }
}
