package org.springframework.beans.factory.aot;

import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanRegistrationAotProcessor.class */
public interface BeanRegistrationAotProcessor {
    @Nullable
    BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean);

    default boolean isBeanExcludedFromAotProcessing() {
        return true;
    }
}
