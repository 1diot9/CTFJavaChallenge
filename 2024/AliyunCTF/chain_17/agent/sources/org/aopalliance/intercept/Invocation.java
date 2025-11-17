package org.aopalliance.intercept;

import javax.annotation.Nonnull;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/aopalliance/intercept/Invocation.class */
public interface Invocation extends Joinpoint {
    @Nonnull
    Object[] getArguments();
}
