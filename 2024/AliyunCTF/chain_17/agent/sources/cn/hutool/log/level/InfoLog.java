package cn.hutool.log.level;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/level/InfoLog.class */
public interface InfoLog {
    boolean isInfoEnabled();

    void info(Throwable th);

    void info(String str, Object... objArr);

    void info(Throwable th, String str, Object... objArr);

    void info(String str, Throwable th, String str2, Object... objArr);
}
