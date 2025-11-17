package cn.hutool.aop.aspects;

import java.io.Serializable;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/aop/aspects/SimpleAspect.class */
public class SimpleAspect implements Aspect, Serializable {
    private static final long serialVersionUID = 1;

    @Override // cn.hutool.aop.aspects.Aspect
    public boolean before(Object target, Method method, Object[] args) {
        return true;
    }

    @Override // cn.hutool.aop.aspects.Aspect
    public boolean after(Object target, Method method, Object[] args, Object returnVal) {
        return true;
    }

    @Override // cn.hutool.aop.aspects.Aspect
    public boolean afterException(Object target, Method method, Object[] args, Throwable e) {
        return true;
    }
}
