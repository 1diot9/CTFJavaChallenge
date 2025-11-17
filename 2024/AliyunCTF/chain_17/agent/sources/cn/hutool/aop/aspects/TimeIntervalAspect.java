package cn.hutool.aop.aspects;

import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Console;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/aop/aspects/TimeIntervalAspect.class */
public class TimeIntervalAspect extends SimpleAspect {
    private static final long serialVersionUID = 1;
    private final TimeInterval interval = new TimeInterval();

    @Override // cn.hutool.aop.aspects.SimpleAspect, cn.hutool.aop.aspects.Aspect
    public boolean before(Object target, Method method, Object[] args) {
        this.interval.start();
        return true;
    }

    @Override // cn.hutool.aop.aspects.SimpleAspect, cn.hutool.aop.aspects.Aspect
    public boolean after(Object target, Method method, Object[] args, Object returnVal) {
        Console.log("Method [{}.{}] execute spend [{}]ms return value [{}]", target.getClass().getName(), method.getName(), Long.valueOf(this.interval.intervalMs()), returnVal);
        return true;
    }
}
