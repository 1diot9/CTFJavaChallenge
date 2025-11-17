package org.springframework.aop.framework;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/AdvisedSupportListener.class */
public interface AdvisedSupportListener {
    void activated(AdvisedSupport advised);

    void adviceChanged(AdvisedSupport advised);
}
