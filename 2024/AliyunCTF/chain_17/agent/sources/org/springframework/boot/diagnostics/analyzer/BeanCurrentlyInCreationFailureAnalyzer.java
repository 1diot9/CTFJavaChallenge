package org.springframework.boot.diagnostics.analyzer;

import cn.hutool.core.text.CharSequenceUtil;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/analyzer/BeanCurrentlyInCreationFailureAnalyzer.class */
class BeanCurrentlyInCreationFailureAnalyzer extends AbstractFailureAnalyzer<BeanCurrentlyInCreationException> {
    private final AbstractAutowireCapableBeanFactory beanFactory;

    BeanCurrentlyInCreationFailureAnalyzer(BeanFactory beanFactory) {
        if (beanFactory instanceof AbstractAutowireCapableBeanFactory) {
            AbstractAutowireCapableBeanFactory autowireCapableBeanFactory = (AbstractAutowireCapableBeanFactory) beanFactory;
            this.beanFactory = autowireCapableBeanFactory;
        } else {
            this.beanFactory = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, BeanCurrentlyInCreationException cause) {
        DependencyCycle dependencyCycle = findCycle(rootFailure);
        if (dependencyCycle == null) {
            return null;
        }
        return new FailureAnalysis(buildMessage(dependencyCycle), action(), cause);
    }

    private String action() {
        if (this.beanFactory != null && this.beanFactory.isAllowCircularReferences()) {
            return "Despite circular references being allowed, the dependency cycle between beans could not be broken. Update your application to remove the dependency cycle.";
        }
        return "Relying upon circular references is discouraged and they are prohibited by default. Update your application to remove the dependency cycle between beans. As a last resort, it may be possible to break the cycle automatically by setting spring.main.allow-circular-references to true.";
    }

    private DependencyCycle findCycle(Throwable rootFailure) {
        List<BeanInCycle> beansInCycle = new ArrayList<>();
        int cycleStart = -1;
        for (Throwable candidate = rootFailure; candidate != null; candidate = candidate.getCause()) {
            BeanInCycle beanInCycle = BeanInCycle.get(candidate);
            if (beanInCycle != null) {
                int index = beansInCycle.indexOf(beanInCycle);
                if (index == -1) {
                    beansInCycle.add(beanInCycle);
                }
                cycleStart = cycleStart != -1 ? cycleStart : index;
            }
        }
        if (cycleStart == -1) {
            return null;
        }
        return new DependencyCycle(beansInCycle, cycleStart);
    }

    private String buildMessage(DependencyCycle dependencyCycle) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("The dependencies of some of the beans in the application context form a cycle:%n%n", new Object[0]));
        List<BeanInCycle> beansInCycle = dependencyCycle.getBeansInCycle();
        boolean singleBean = beansInCycle.size() == 1;
        int cycleStart = dependencyCycle.getCycleStart();
        int i = 0;
        while (i < beansInCycle.size()) {
            BeanInCycle beanInCycle = beansInCycle.get(i);
            if (i == cycleStart) {
                message.append(String.format(singleBean ? "┌──->──┐%n" : "┌─────┐%n", new Object[0]));
            } else if (i > 0) {
                String leftSide = i < cycleStart ? CharSequenceUtil.SPACE : "↑";
                message.append(String.format("%s     ↓%n", leftSide));
            }
            String leftSide2 = i < cycleStart ? CharSequenceUtil.SPACE : "|";
            message.append(String.format("%s  %s%n", leftSide2, beanInCycle));
            i++;
        }
        message.append(String.format(singleBean ? "└──<-──┘%n" : "└─────┘%n", new Object[0]));
        return message.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/analyzer/BeanCurrentlyInCreationFailureAnalyzer$DependencyCycle.class */
    public static final class DependencyCycle {
        private final List<BeanInCycle> beansInCycle;
        private final int cycleStart;

        private DependencyCycle(List<BeanInCycle> beansInCycle, int cycleStart) {
            this.beansInCycle = beansInCycle;
            this.cycleStart = cycleStart;
        }

        List<BeanInCycle> getBeansInCycle() {
            return this.beansInCycle;
        }

        int getCycleStart() {
            return this.cycleStart;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/analyzer/BeanCurrentlyInCreationFailureAnalyzer$BeanInCycle.class */
    public static final class BeanInCycle {
        private final String name;
        private final String description;

        private BeanInCycle(BeanCreationException ex) {
            this.name = ex.getBeanName();
            this.description = determineDescription(ex);
        }

        private String determineDescription(BeanCreationException ex) {
            if (StringUtils.hasText(ex.getResourceDescription())) {
                return String.format(" defined in %s", ex.getResourceDescription());
            }
            InjectionPoint failedInjectionPoint = findFailedInjectionPoint(ex);
            if (failedInjectionPoint != null && failedInjectionPoint.getField() != null) {
                return String.format(" (field %s)", failedInjectionPoint.getField());
            }
            return "";
        }

        private InjectionPoint findFailedInjectionPoint(BeanCreationException ex) {
            if (ex instanceof UnsatisfiedDependencyException) {
                UnsatisfiedDependencyException unsatisfiedDependencyException = (UnsatisfiedDependencyException) ex;
                return unsatisfiedDependencyException.getInjectionPoint();
            }
            return null;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return this.name.equals(((BeanInCycle) obj).name);
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public String toString() {
            return this.name + this.description;
        }

        static BeanInCycle get(Throwable ex) {
            if (ex instanceof BeanCreationException) {
                BeanCreationException beanCreationException = (BeanCreationException) ex;
                return get(beanCreationException);
            }
            return null;
        }

        private static BeanInCycle get(BeanCreationException ex) {
            if (StringUtils.hasText(ex.getBeanName())) {
                return new BeanInCycle(ex);
            }
            return null;
        }
    }
}
