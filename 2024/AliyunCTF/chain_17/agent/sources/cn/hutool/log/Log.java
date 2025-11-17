package cn.hutool.log;

import cn.hutool.core.lang.caller.CallerUtil;
import cn.hutool.log.level.DebugLog;
import cn.hutool.log.level.ErrorLog;
import cn.hutool.log.level.InfoLog;
import cn.hutool.log.level.Level;
import cn.hutool.log.level.TraceLog;
import cn.hutool.log.level.WarnLog;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/Log.class */
public interface Log extends TraceLog, DebugLog, InfoLog, WarnLog, ErrorLog {
    String getName();

    boolean isEnabled(Level level);

    void log(Level level, String str, Object... objArr);

    void log(Level level, Throwable th, String str, Object... objArr);

    void log(String str, Level level, Throwable th, String str2, Object... objArr);

    static Log get(Class<?> clazz) {
        return LogFactory.get(clazz);
    }

    static Log get(String name) {
        return LogFactory.get(name);
    }

    static Log get() {
        return LogFactory.get(CallerUtil.getCallerCaller());
    }
}
