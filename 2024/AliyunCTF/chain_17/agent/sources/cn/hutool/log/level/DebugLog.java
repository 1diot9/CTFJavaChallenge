package cn.hutool.log.level;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/level/DebugLog.class */
public interface DebugLog {
    boolean isDebugEnabled();

    void debug(Throwable th);

    void debug(String str, Object... objArr);

    void debug(Throwable th, String str, Object... objArr);

    void debug(String str, Throwable th, String str2, Object... objArr);
}
