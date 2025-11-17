package org.springframework.aop.support;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.IntroductionInfo;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/IntroductionInfoSupport.class */
public class IntroductionInfoSupport implements IntroductionInfo, Serializable {
    protected final Set<Class<?>> publishedInterfaces = new LinkedHashSet();
    private transient Map<Method, Boolean> rememberedMethods = new ConcurrentHashMap(32);

    public void suppressInterface(Class<?> ifc) {
        this.publishedInterfaces.remove(ifc);
    }

    @Override // org.springframework.aop.IntroductionInfo
    public Class<?>[] getInterfaces() {
        return ClassUtils.toClassArray(this.publishedInterfaces);
    }

    public boolean implementsInterface(Class<?> ifc) {
        for (Class<?> pubIfc : this.publishedInterfaces) {
            if (ifc.isInterface() && ifc.isAssignableFrom(pubIfc)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void implementInterfacesOnObject(Object delegate) {
        this.publishedInterfaces.addAll(ClassUtils.getAllInterfacesAsSet(delegate));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean isMethodOnIntroducedInterface(MethodInvocation mi) {
        Boolean rememberedResult = this.rememberedMethods.get(mi.getMethod());
        if (rememberedResult != null) {
            return rememberedResult.booleanValue();
        }
        boolean result = implementsInterface(mi.getMethod().getDeclaringClass());
        this.rememberedMethods.put(mi.getMethod(), Boolean.valueOf(result));
        return result;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.rememberedMethods = new ConcurrentHashMap(32);
    }
}
