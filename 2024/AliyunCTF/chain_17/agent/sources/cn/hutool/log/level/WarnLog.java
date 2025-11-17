package cn.hutool.log.level;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/level/WarnLog.class */
public interface WarnLog {
    boolean isWarnEnabled();

    void warn(Throwable th);

    void warn(String str, Object... objArr);

    void warn(Throwable th, String str, Object... objArr);

    void warn(String str, Throwable th, String str2, Object... objArr);
}
