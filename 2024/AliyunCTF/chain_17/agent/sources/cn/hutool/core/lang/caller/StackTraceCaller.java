package cn.hutool.core.lang.caller;

import cn.hutool.core.exceptions.UtilException;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/caller/StackTraceCaller.class */
public class StackTraceCaller implements Caller, Serializable {
    private static final long serialVersionUID = 1;
    private static final int OFFSET = 2;

    @Override // cn.hutool.core.lang.caller.Caller
    public Class<?> getCaller() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (3 >= stackTrace.length) {
            return null;
        }
        String className = stackTrace[3].getClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new UtilException(e, "[{}] not found!", className);
        }
    }

    @Override // cn.hutool.core.lang.caller.Caller
    public Class<?> getCallerCaller() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (4 >= stackTrace.length) {
            return null;
        }
        String className = stackTrace[4].getClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new UtilException(e, "[{}] not found!", className);
        }
    }

    @Override // cn.hutool.core.lang.caller.Caller
    public Class<?> getCaller(int depth) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (2 + depth >= stackTrace.length) {
            return null;
        }
        String className = stackTrace[2 + depth].getClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new UtilException(e, "[{}] not found!", className);
        }
    }

    @Override // cn.hutool.core.lang.caller.Caller
    public boolean isCalledBy(Class<?> clazz) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().equals(clazz.getName())) {
                return true;
            }
        }
        return false;
    }
}
