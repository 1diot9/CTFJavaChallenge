package cn.hutool.core.thread.threadlocal;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/thread/threadlocal/NamedInheritableThreadLocal.class */
public class NamedInheritableThreadLocal<T> extends InheritableThreadLocal<T> {
    private final String name;

    public NamedInheritableThreadLocal(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
