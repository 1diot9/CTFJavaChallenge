package cn.hutool.aop.interceptor;

import cn.hutool.aop.aspects.Aspect;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/aop/interceptor/SpringCglibInterceptor.class */
public class SpringCglibInterceptor implements MethodInterceptor, Serializable {
    private static final long serialVersionUID = 1;
    private final Object target;
    private final Aspect aspect;

    public SpringCglibInterceptor(Object target, Aspect aspect) {
        this.target = target;
        this.aspect = aspect;
    }

    public Object getTarget() {
        return this.target;
    }

    @Override // org.springframework.cglib.proxy.MethodInterceptor
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object target = this.target;
        Object result = null;
        if (this.aspect.before(target, method, args)) {
            try {
                result = proxy.invoke(target, args);
            } catch (InvocationTargetException e) {
                if (this.aspect.afterException(target, method, args, e.getTargetException())) {
                    throw e;
                }
            }
        }
        if (this.aspect.after(target, method, args, result)) {
            return result;
        }
        return null;
    }
}
