package cn.hutool.log.level;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/level/ErrorLog.class */
public interface ErrorLog {
    boolean isErrorEnabled();

    void error(Throwable th);

    void error(String str, Object... objArr);

    void error(Throwable th, String str, Object... objArr);

    void error(String str, Throwable th, String str2, Object... objArr);
}
