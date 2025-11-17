package cn.hutool.log.level;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/level/TraceLog.class */
public interface TraceLog {
    boolean isTraceEnabled();

    void trace(Throwable th);

    void trace(String str, Object... objArr);

    void trace(Throwable th, String str, Object... objArr);

    void trace(String str, Throwable th, String str2, Object... objArr);
}
