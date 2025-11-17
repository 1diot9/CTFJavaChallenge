package cn.hutool.core.text.finder;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/finder/Finder.class */
public interface Finder {
    public static final int INDEX_NOT_FOUND = -1;

    int start(int i);

    int end(int i);

    default Finder reset() {
        return this;
    }
}
