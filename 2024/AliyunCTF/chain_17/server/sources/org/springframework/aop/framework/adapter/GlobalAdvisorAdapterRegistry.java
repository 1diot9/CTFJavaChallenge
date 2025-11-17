package org.springframework.aop.framework.adapter;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/adapter/GlobalAdvisorAdapterRegistry.class */
public final class GlobalAdvisorAdapterRegistry {
    private static AdvisorAdapterRegistry instance = new DefaultAdvisorAdapterRegistry();

    private GlobalAdvisorAdapterRegistry() {
    }

    public static AdvisorAdapterRegistry getInstance() {
        return instance;
    }

    static void reset() {
        instance = new DefaultAdvisorAdapterRegistry();
    }
}
