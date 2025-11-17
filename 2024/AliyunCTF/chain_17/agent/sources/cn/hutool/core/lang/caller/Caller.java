package cn.hutool.core.lang.caller;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/caller/Caller.class */
public interface Caller {
    Class<?> getCaller();

    Class<?> getCallerCaller();

    Class<?> getCaller(int i);

    boolean isCalledBy(Class<?> cls);
}
