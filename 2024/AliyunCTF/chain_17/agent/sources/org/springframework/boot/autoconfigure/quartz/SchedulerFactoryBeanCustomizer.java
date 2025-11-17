package org.springframework.boot.autoconfigure.quartz;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/quartz/SchedulerFactoryBeanCustomizer.class */
public interface SchedulerFactoryBeanCustomizer {
    void customize(SchedulerFactoryBean schedulerFactoryBean);
}
