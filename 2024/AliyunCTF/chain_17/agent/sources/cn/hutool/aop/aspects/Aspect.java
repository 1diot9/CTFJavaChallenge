package cn.hutool.aop.aspects;

import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/aop/aspects/Aspect.class */
public interface Aspect {
    boolean before(Object obj, Method method, Object[] objArr);

    boolean after(Object obj, Method method, Object[] objArr, Object obj2);

    boolean afterException(Object obj, Method method, Object[] objArr, Throwable th);
}
