package org.springframework.boot.autoconfigure.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/ConditionEvaluationReport.class */
public final class ConditionEvaluationReport {
    private static final String BEAN_NAME = "autoConfigurationReport";
    private static final AncestorsMatchedCondition ANCESTOR_CONDITION = new AncestorsMatchedCondition();
    private boolean addedAncestorOutcomes;
    private ConditionEvaluationReport parent;
    private final SortedMap<String, ConditionAndOutcomes> outcomes = new TreeMap();
    private final List<String> exclusions = new ArrayList();
    private final Set<String> unconditionalClasses = new HashSet();

    private ConditionEvaluationReport() {
    }

    public void recordConditionEvaluation(String source, Condition condition, ConditionOutcome outcome) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(condition, "Condition must not be null");
        Assert.notNull(outcome, "Outcome must not be null");
        this.unconditionalClasses.remove(source);
        this.outcomes.computeIfAbsent(source, key -> {
            return new ConditionAndOutcomes();
        }).add(condition, outcome);
        this.addedAncestorOutcomes = false;
    }

    public void recordExclusions(Collection<String> exclusions) {
        Assert.notNull(exclusions, "exclusions must not be null");
        this.exclusions.addAll(exclusions);
    }

    public void recordEvaluationCandidates(List<String> evaluationCandidates) {
        Assert.notNull(evaluationCandidates, "evaluationCandidates must not be null");
        this.unconditionalClasses.addAll(evaluationCandidates);
    }

    public Map<String, ConditionAndOutcomes> getConditionAndOutcomesBySource() {
        if (!this.addedAncestorOutcomes) {
            this.outcomes.forEach((source, sourceOutcomes) -> {
                if (!sourceOutcomes.isFullMatch()) {
                    addNoMatchOutcomeToAncestors(source);
                }
            });
            this.addedAncestorOutcomes = true;
        }
        return Collections.unmodifiableMap(this.outcomes);
    }

    private void addNoMatchOutcomeToAncestors(String source) {
        String prefix = source + "$";
        this.outcomes.forEach((candidateSource, sourceOutcomes) -> {
            if (candidateSource.startsWith(prefix)) {
                ConditionOutcome outcome = ConditionOutcome.noMatch(ConditionMessage.forCondition("Ancestor " + source, new Object[0]).because("did not match"));
                sourceOutcomes.add(ANCESTOR_CONDITION, outcome);
            }
        });
    }

    public List<String> getExclusions() {
        return Collections.unmodifiableList(this.exclusions);
    }

    public Set<String> getUnconditionalClasses() {
        Set<String> filtered = new HashSet<>(this.unconditionalClasses);
        List<String> list = this.exclusions;
        Objects.requireNonNull(filtered);
        list.forEach((v1) -> {
            r1.remove(v1);
        });
        return Collections.unmodifiableSet(filtered);
    }

    public ConditionEvaluationReport getParent() {
        return this.parent;
    }

    public static ConditionEvaluationReport find(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            return get((ConfigurableListableBeanFactory) beanFactory);
        }
        return null;
    }

    public static ConditionEvaluationReport get(ConfigurableListableBeanFactory beanFactory) {
        ConditionEvaluationReport report;
        ConditionEvaluationReport conditionEvaluationReport;
        synchronized (beanFactory) {
            if (beanFactory.containsSingleton(BEAN_NAME)) {
                report = (ConditionEvaluationReport) beanFactory.getBean(BEAN_NAME, ConditionEvaluationReport.class);
            } else {
                report = new ConditionEvaluationReport();
                beanFactory.registerSingleton(BEAN_NAME, report);
            }
            locateParent(beanFactory.getParentBeanFactory(), report);
            conditionEvaluationReport = report;
        }
        return conditionEvaluationReport;
    }

    private static void locateParent(BeanFactory beanFactory, ConditionEvaluationReport report) {
        if (beanFactory != null && report.parent == null && beanFactory.containsBean(BEAN_NAME)) {
            report.parent = (ConditionEvaluationReport) beanFactory.getBean(BEAN_NAME, ConditionEvaluationReport.class);
        }
    }

    public ConditionEvaluationReport getDelta(ConditionEvaluationReport previousReport) {
        ConditionEvaluationReport delta = new ConditionEvaluationReport();
        this.outcomes.forEach((source, sourceOutcomes) -> {
            ConditionAndOutcomes previous = previousReport.outcomes.get(source);
            if (previous == null || previous.isFullMatch() != sourceOutcomes.isFullMatch()) {
                sourceOutcomes.forEach(conditionAndOutcome -> {
                    delta.recordConditionEvaluation(source, conditionAndOutcome.getCondition(), conditionAndOutcome.getOutcome());
                });
            }
        });
        List<String> newExclusions = new ArrayList<>(this.exclusions);
        newExclusions.removeAll(previousReport.getExclusions());
        delta.recordExclusions(newExclusions);
        List<String> newUnconditionalClasses = new ArrayList<>(this.unconditionalClasses);
        newUnconditionalClasses.removeAll(previousReport.unconditionalClasses);
        delta.unconditionalClasses.addAll(newUnconditionalClasses);
        return delta;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/ConditionEvaluationReport$ConditionAndOutcomes.class */
    public static class ConditionAndOutcomes implements Iterable<ConditionAndOutcome> {
        private final Set<ConditionAndOutcome> outcomes = new LinkedHashSet();

        public void add(Condition condition, ConditionOutcome outcome) {
            this.outcomes.add(new ConditionAndOutcome(condition, outcome));
        }

        public boolean isFullMatch() {
            Iterator<ConditionAndOutcome> it = iterator();
            while (it.hasNext()) {
                ConditionAndOutcome conditionAndOutcomes = it.next();
                if (!conditionAndOutcomes.getOutcome().isMatch()) {
                    return false;
                }
            }
            return true;
        }

        @Override // java.lang.Iterable
        public Iterator<ConditionAndOutcome> iterator() {
            return Collections.unmodifiableSet(this.outcomes).iterator();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/ConditionEvaluationReport$ConditionAndOutcome.class */
    public static class ConditionAndOutcome {
        private final Condition condition;
        private final ConditionOutcome outcome;

        public ConditionAndOutcome(Condition condition, ConditionOutcome outcome) {
            this.condition = condition;
            this.outcome = outcome;
        }

        public Condition getCondition() {
            return this.condition;
        }

        public ConditionOutcome getOutcome() {
            return this.outcome;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ConditionAndOutcome other = (ConditionAndOutcome) obj;
            return ObjectUtils.nullSafeEquals(this.condition.getClass(), other.condition.getClass()) && ObjectUtils.nullSafeEquals(this.outcome, other.outcome);
        }

        public int hashCode() {
            return (this.condition.getClass().hashCode() * 31) + this.outcome.hashCode();
        }

        public String toString() {
            return this.condition.getClass() + " " + this.outcome;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/ConditionEvaluationReport$AncestorsMatchedCondition.class */
    public static final class AncestorsMatchedCondition implements Condition {
        private AncestorsMatchedCondition() {
        }

        @Override // org.springframework.context.annotation.Condition
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            throw new UnsupportedOperationException();
        }
    }
}
