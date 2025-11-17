package cn.hutool.core.lang.caller;

import cn.hutool.core.util.ArrayUtil;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/caller/SecurityManagerCaller.class */
public class SecurityManagerCaller extends SecurityManager implements Caller, Serializable {
    private static final long serialVersionUID = 1;
    private static final int OFFSET = 1;

    @Override // cn.hutool.core.lang.caller.Caller
    public Class<?> getCaller() {
        Class<?>[] context = getClassContext();
        if (null != context && 2 < context.length) {
            return context[2];
        }
        return null;
    }

    @Override // cn.hutool.core.lang.caller.Caller
    public Class<?> getCallerCaller() {
        Class<?>[] context = getClassContext();
        if (null != context && 3 < context.length) {
            return context[3];
        }
        return null;
    }

    @Override // cn.hutool.core.lang.caller.Caller
    public Class<?> getCaller(int depth) {
        Class<?>[] context = getClassContext();
        if (null != context && 1 + depth < context.length) {
            return context[1 + depth];
        }
        return null;
    }

    @Override // cn.hutool.core.lang.caller.Caller
    public boolean isCalledBy(Class<?> clazz) {
        Class<?>[] classes = getClassContext();
        if (ArrayUtil.isNotEmpty((Object[]) classes)) {
            for (Class<?> contextClass : classes) {
                if (contextClass.equals(clazz)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
