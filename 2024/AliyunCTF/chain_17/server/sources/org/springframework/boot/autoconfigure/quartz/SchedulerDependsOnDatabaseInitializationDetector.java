package org.springframework.boot.autoconfigure.quartz;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.quartz.Scheduler;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/quartz/SchedulerDependsOnDatabaseInitializationDetector.class */
class SchedulerDependsOnDatabaseInitializationDetector extends AbstractBeansOfTypeDependsOnDatabaseInitializationDetector {
    SchedulerDependsOnDatabaseInitializationDetector() {
    }

    @Override // org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector
    protected Set<Class<?>> getDependsOnDatabaseInitializationBeanTypes() {
        return new HashSet(Arrays.asList(Scheduler.class, SchedulerFactoryBean.class));
    }
}
