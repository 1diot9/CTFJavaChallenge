package org.aopalliance.intercept;

import java.lang.reflect.AccessibleObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/aopalliance/intercept/Joinpoint.class */
public interface Joinpoint {
    @Nullable
    Object proceed() throws Throwable;

    @Nullable
    Object getThis();

    @Nonnull
    AccessibleObject getStaticPart();
}
