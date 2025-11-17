package org.springframework.boot.validation.beanvalidation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/validation/beanvalidation/FilteredMethodValidationPostProcessor.class */
public class FilteredMethodValidationPostProcessor extends MethodValidationPostProcessor {
    private final Collection<MethodValidationExcludeFilter> excludeFilters;

    public FilteredMethodValidationPostProcessor(Stream<? extends MethodValidationExcludeFilter> excludeFilters) {
        Class<MethodValidationExcludeFilter> cls = MethodValidationExcludeFilter.class;
        Objects.requireNonNull(MethodValidationExcludeFilter.class);
        this.excludeFilters = excludeFilters.map((v1) -> {
            return r2.cast(v1);
        }).toList();
    }

    public FilteredMethodValidationPostProcessor(Collection<? extends MethodValidationExcludeFilter> excludeFilters) {
        this.excludeFilters = new ArrayList(excludeFilters);
    }

    @Override // org.springframework.validation.beanvalidation.MethodValidationPostProcessor, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        DefaultPointcutAdvisor advisor = (DefaultPointcutAdvisor) this.advisor;
        ClassFilter classFilter = advisor.getPointcut().getClassFilter();
        MethodMatcher methodMatcher = advisor.getPointcut().getMethodMatcher();
        advisor.setPointcut(new ComposablePointcut(classFilter, methodMatcher).intersection(this::isIncluded));
    }

    private boolean isIncluded(Class<?> candidate) {
        for (MethodValidationExcludeFilter exclusionFilter : this.excludeFilters) {
            if (exclusionFilter.isExcluded(candidate)) {
                return false;
            }
        }
        return true;
    }
}
