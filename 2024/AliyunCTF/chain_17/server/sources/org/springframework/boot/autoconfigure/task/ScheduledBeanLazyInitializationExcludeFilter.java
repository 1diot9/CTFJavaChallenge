package org.springframework.boot.autoconfigure.task;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/ScheduledBeanLazyInitializationExcludeFilter.class */
class ScheduledBeanLazyInitializationExcludeFilter implements LazyInitializationExcludeFilter {
    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap(64));

    /* JADX INFO: Access modifiers changed from: package-private */
    public ScheduledBeanLazyInitializationExcludeFilter() {
        this.nonAnnotatedClasses.add(AopInfrastructureBean.class);
        this.nonAnnotatedClasses.add(TaskScheduler.class);
        this.nonAnnotatedClasses.add(ScheduledExecutorService.class);
    }

    @Override // org.springframework.boot.LazyInitializationExcludeFilter
    public boolean isExcluded(String beanName, BeanDefinition beanDefinition, Class<?> beanType) {
        return hasScheduledTask(beanType);
    }

    private boolean hasScheduledTask(Class<?> type) {
        Class<?> targetType = ClassUtils.getUserClass(type);
        if (!this.nonAnnotatedClasses.contains(targetType) && AnnotationUtils.isCandidateClass(targetType, Arrays.asList(Scheduled.class, Schedules.class))) {
            Map<Method, Set<Scheduled>> annotatedMethods = MethodIntrospector.selectMethods(targetType, method -> {
                Set<Scheduled> scheduledAnnotations = AnnotatedElementUtils.getMergedRepeatableAnnotations(method, Scheduled.class, Schedules.class);
                if (scheduledAnnotations.isEmpty()) {
                    return null;
                }
                return scheduledAnnotations;
            });
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(targetType);
            }
            return !annotatedMethods.isEmpty();
        }
        return false;
    }
}
