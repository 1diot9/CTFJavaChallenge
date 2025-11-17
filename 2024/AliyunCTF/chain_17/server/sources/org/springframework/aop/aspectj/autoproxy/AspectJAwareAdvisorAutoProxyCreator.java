package org.springframework.aop.aspectj.autoproxy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.aopalliance.aop.Advice;
import org.aspectj.util.PartialOrder;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AbstractAspectJAdvice;
import org.springframework.aop.aspectj.AspectJPointcutAdvisor;
import org.springframework.aop.aspectj.AspectJProxyUtils;
import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/autoproxy/AspectJAwareAdvisorAutoProxyCreator.class */
public class AspectJAwareAdvisorAutoProxyCreator extends AbstractAdvisorAutoProxyCreator {
    private static final Comparator<Advisor> DEFAULT_PRECEDENCE_COMPARATOR = new AspectJPrecedenceComparator();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator
    public List<Advisor> sortAdvisors(List<Advisor> advisors) {
        List<PartiallyComparableAdvisorHolder> partiallyComparableAdvisors = new ArrayList<>(advisors.size());
        for (Advisor advisor : advisors) {
            partiallyComparableAdvisors.add(new PartiallyComparableAdvisorHolder(advisor, DEFAULT_PRECEDENCE_COMPARATOR));
        }
        List<PartiallyComparableAdvisorHolder> sorted = PartialOrder.sort(partiallyComparableAdvisors);
        if (sorted != null) {
            List<Advisor> result = new ArrayList<>(advisors.size());
            for (PartiallyComparableAdvisorHolder pcAdvisor : sorted) {
                result.add(pcAdvisor.getAdvisor());
            }
            return result;
        }
        return super.sortAdvisors(advisors);
    }

    @Override // org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator
    protected void extendAdvisors(List<Advisor> candidateAdvisors) {
        AspectJProxyUtils.makeAdvisorChainAspectJCapableIfNecessary(candidateAdvisors);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator
    public boolean shouldSkip(Class<?> beanClass, String beanName) {
        List<Advisor> candidateAdvisors = findCandidateAdvisors();
        for (Advisor advisor : candidateAdvisors) {
            if (advisor instanceof AspectJPointcutAdvisor) {
                AspectJPointcutAdvisor pointcutAdvisor = (AspectJPointcutAdvisor) advisor;
                if (pointcutAdvisor.getAspectName().equals(beanName)) {
                    return true;
                }
            }
        }
        return super.shouldSkip(beanClass, beanName);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/autoproxy/AspectJAwareAdvisorAutoProxyCreator$PartiallyComparableAdvisorHolder.class */
    private static class PartiallyComparableAdvisorHolder implements PartialOrder.PartialComparable {
        private final Advisor advisor;
        private final Comparator<Advisor> comparator;

        public PartiallyComparableAdvisorHolder(Advisor advisor, Comparator<Advisor> comparator) {
            this.advisor = advisor;
            this.comparator = comparator;
        }

        public int compareTo(Object obj) {
            Advisor otherAdvisor = ((PartiallyComparableAdvisorHolder) obj).advisor;
            return this.comparator.compare(this.advisor, otherAdvisor);
        }

        public int fallbackCompareTo(Object obj) {
            return 0;
        }

        public Advisor getAdvisor() {
            return this.advisor;
        }

        public String toString() {
            Advice advice = this.advisor.getAdvice();
            StringBuilder sb = new StringBuilder(ClassUtils.getShortName(advice.getClass()));
            boolean appended = false;
            Advisor advisor = this.advisor;
            if (advisor instanceof Ordered) {
                Ordered ordered = (Ordered) advisor;
                sb.append(": order = ").append(ordered.getOrder());
                appended = true;
            }
            if (advice instanceof AbstractAspectJAdvice) {
                AbstractAspectJAdvice ajAdvice = (AbstractAspectJAdvice) advice;
                sb.append(!appended ? ": " : ", ");
                sb.append("aspect name = ");
                sb.append(ajAdvice.getAspectName());
                sb.append(", declaration order = ");
                sb.append(ajAdvice.getDeclarationOrder());
            }
            return sb.toString();
        }
    }
}
