package cn.hutool.core.thread.threadlocal;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/thread/threadlocal/NamedThreadLocal.class */
public class NamedThreadLocal<T> extends ThreadLocal<T> {
    private final String name;

    public NamedThreadLocal(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
