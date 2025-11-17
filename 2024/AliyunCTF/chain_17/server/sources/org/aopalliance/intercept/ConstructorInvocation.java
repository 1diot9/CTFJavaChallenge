package org.aopalliance.intercept;

import java.lang.reflect.Constructor;
import javax.annotation.Nonnull;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/aopalliance/intercept/ConstructorInvocation.class */
public interface ConstructorInvocation extends Invocation {
    @Nonnull
    Constructor<?> getConstructor();
}
