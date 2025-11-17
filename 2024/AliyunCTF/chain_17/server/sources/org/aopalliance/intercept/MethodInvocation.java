package org.aopalliance.intercept;

import java.lang.reflect.Method;
import javax.annotation.Nonnull;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/aopalliance/intercept/MethodInvocation.class */
public interface MethodInvocation extends Invocation {
    @Nonnull
    Method getMethod();
}
