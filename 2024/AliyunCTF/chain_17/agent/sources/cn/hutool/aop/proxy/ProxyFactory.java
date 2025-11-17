package cn.hutool.aop.proxy;

import cn.hutool.aop.aspects.Aspect;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.ServiceLoaderUtil;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/aop/proxy/ProxyFactory.class */
public abstract class ProxyFactory implements Serializable {
    private static final long serialVersionUID = 1;

    public abstract <T> T proxy(T t, Aspect aspect);

    public <T> T proxy(T t, Class<? extends Aspect> cls) {
        return (T) proxy((ProxyFactory) t, (Aspect) ReflectUtil.newInstanceIfPossible(cls));
    }

    public static <T> T createProxy(T t, Class<? extends Aspect> cls) {
        return (T) createProxy(t, (Aspect) ReflectUtil.newInstance(cls, new Object[0]));
    }

    public static <T> T createProxy(T t, Aspect aspect) {
        return (T) create().proxy((ProxyFactory) t, aspect);
    }

    public static ProxyFactory create() {
        return (ProxyFactory) ServiceLoaderUtil.loadFirstAvailable(ProxyFactory.class);
    }
}
