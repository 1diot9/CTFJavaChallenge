package org.springframework.beans.factory.support;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/ImplicitlyAppearedSingletonException.class */
class ImplicitlyAppearedSingletonException extends IllegalStateException {
    public ImplicitlyAppearedSingletonException() {
        super("About-to-be-created singleton instance implicitly appeared through the creation of the factory bean that its bean definition points to");
    }
}
