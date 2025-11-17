package org.springframework.boot.autoconfigure.condition;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.FilteringSpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

@Order(Integer.MIN_VALUE)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/OnClassCondition.class */
class OnClassCondition extends FilteringSpringBootCondition {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/OnClassCondition$OutcomesResolver.class */
    public interface OutcomesResolver {
        ConditionOutcome[] resolveOutcomes();
    }

    OnClassCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.FilteringSpringBootCondition
    protected final ConditionOutcome[] getOutcomes(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        if (autoConfigurationClasses.length > 1 && Runtime.getRuntime().availableProcessors() > 1) {
            return resolveOutcomesThreaded(autoConfigurationClasses, autoConfigurationMetadata);
        }
        OutcomesResolver outcomesResolver = new StandardOutcomesResolver(autoConfigurationClasses, 0, autoConfigurationClasses.length, autoConfigurationMetadata, getBeanClassLoader());
        return outcomesResolver.resolveOutcomes();
    }

    private ConditionOutcome[] resolveOutcomesThreaded(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        int split = autoConfigurationClasses.length / 2;
        OutcomesResolver firstHalfResolver = createOutcomesResolver(autoConfigurationClasses, 0, split, autoConfigurationMetadata);
        OutcomesResolver secondHalfResolver = new StandardOutcomesResolver(autoConfigurationClasses, split, autoConfigurationClasses.length, autoConfigurationMetadata, getBeanClassLoader());
        ConditionOutcome[] secondHalf = secondHalfResolver.resolveOutcomes();
        ConditionOutcome[] firstHalf = firstHalfResolver.resolveOutcomes();
        ConditionOutcome[] outcomes = new ConditionOutcome[autoConfigurationClasses.length];
        System.arraycopy(firstHalf, 0, outcomes, 0, firstHalf.length);
        System.arraycopy(secondHalf, 0, outcomes, split, secondHalf.length);
        return outcomes;
    }

    private OutcomesResolver createOutcomesResolver(String[] autoConfigurationClasses, int start, int end, AutoConfigurationMetadata autoConfigurationMetadata) {
        OutcomesResolver outcomesResolver = new StandardOutcomesResolver(autoConfigurationClasses, start, end, autoConfigurationMetadata, getBeanClassLoader());
        return new ThreadedOutcomesResolver(outcomesResolver);
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ClassLoader classLoader = context.getClassLoader();
        ConditionMessage matchMessage = ConditionMessage.empty();
        List<String> onClasses = getCandidates(metadata, ConditionalOnClass.class);
        if (onClasses != null) {
            List<String> missing = filter(onClasses, FilteringSpringBootCondition.ClassNameFilter.MISSING, classLoader);
            if (!missing.isEmpty()) {
                return ConditionOutcome.noMatch(ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnClass.class, new Object[0]).didNotFind("required class", "required classes").items(ConditionMessage.Style.QUOTE, missing));
            }
            matchMessage = matchMessage.andCondition(ConditionalOnClass.class, new Object[0]).found("required class", "required classes").items(ConditionMessage.Style.QUOTE, filter(onClasses, FilteringSpringBootCondition.ClassNameFilter.PRESENT, classLoader));
        }
        List<String> onMissingClasses = getCandidates(metadata, ConditionalOnMissingClass.class);
        if (onMissingClasses != null) {
            List<String> present = filter(onMissingClasses, FilteringSpringBootCondition.ClassNameFilter.PRESENT, classLoader);
            if (!present.isEmpty()) {
                return ConditionOutcome.noMatch(ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnMissingClass.class, new Object[0]).found("unwanted class", "unwanted classes").items(ConditionMessage.Style.QUOTE, present));
            }
            matchMessage = matchMessage.andCondition(ConditionalOnMissingClass.class, new Object[0]).didNotFind("unwanted class", "unwanted classes").items(ConditionMessage.Style.QUOTE, filter(onMissingClasses, FilteringSpringBootCondition.ClassNameFilter.MISSING, classLoader));
        }
        return ConditionOutcome.match(matchMessage);
    }

    private List<String> getCandidates(AnnotatedTypeMetadata metadata, Class<?> annotationType) {
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(annotationType.getName(), true);
        if (attributes == null) {
            return null;
        }
        List<String> candidates = new ArrayList<>();
        addAll(candidates, (List) attributes.get("value"));
        addAll(candidates, (List) attributes.get("name"));
        return candidates;
    }

    private void addAll(List<String> list, List<Object> itemsToAdd) {
        if (itemsToAdd != null) {
            for (Object item : itemsToAdd) {
                Collections.addAll(list, (String[]) item);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/OnClassCondition$ThreadedOutcomesResolver.class */
    public static final class ThreadedOutcomesResolver implements OutcomesResolver {
        private final Thread thread;
        private volatile ConditionOutcome[] outcomes;

        private ThreadedOutcomesResolver(OutcomesResolver outcomesResolver) {
            this.thread = new Thread(() -> {
                this.outcomes = outcomesResolver.resolveOutcomes();
            });
            this.thread.start();
        }

        @Override // org.springframework.boot.autoconfigure.condition.OnClassCondition.OutcomesResolver
        public ConditionOutcome[] resolveOutcomes() {
            try {
                this.thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return this.outcomes;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/OnClassCondition$StandardOutcomesResolver.class */
    public static final class StandardOutcomesResolver implements OutcomesResolver {
        private final String[] autoConfigurationClasses;
        private final int start;
        private final int end;
        private final AutoConfigurationMetadata autoConfigurationMetadata;
        private final ClassLoader beanClassLoader;

        private StandardOutcomesResolver(String[] autoConfigurationClasses, int start, int end, AutoConfigurationMetadata autoConfigurationMetadata, ClassLoader beanClassLoader) {
            this.autoConfigurationClasses = autoConfigurationClasses;
            this.start = start;
            this.end = end;
            this.autoConfigurationMetadata = autoConfigurationMetadata;
            this.beanClassLoader = beanClassLoader;
        }

        @Override // org.springframework.boot.autoconfigure.condition.OnClassCondition.OutcomesResolver
        public ConditionOutcome[] resolveOutcomes() {
            return getOutcomes(this.autoConfigurationClasses, this.start, this.end, this.autoConfigurationMetadata);
        }

        private ConditionOutcome[] getOutcomes(String[] autoConfigurationClasses, int start, int end, AutoConfigurationMetadata autoConfigurationMetadata) {
            String candidates;
            ConditionOutcome[] outcomes = new ConditionOutcome[end - start];
            for (int i = start; i < end; i++) {
                String autoConfigurationClass = autoConfigurationClasses[i];
                if (autoConfigurationClass != null && (candidates = autoConfigurationMetadata.get(autoConfigurationClass, "ConditionalOnClass")) != null) {
                    outcomes[i - start] = getOutcome(candidates);
                }
            }
            return outcomes;
        }

        private ConditionOutcome getOutcome(String candidates) {
            try {
                if (!candidates.contains(",")) {
                    return getOutcome(candidates, this.beanClassLoader);
                }
                for (String candidate : StringUtils.commaDelimitedListToStringArray(candidates)) {
                    ConditionOutcome outcome = getOutcome(candidate, this.beanClassLoader);
                    if (outcome != null) {
                        return outcome;
                    }
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }

        private ConditionOutcome getOutcome(String className, ClassLoader classLoader) {
            if (FilteringSpringBootCondition.ClassNameFilter.MISSING.matches(className, classLoader)) {
                return ConditionOutcome.noMatch(ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnClass.class, new Object[0]).didNotFind("required class").items(ConditionMessage.Style.QUOTE, className));
            }
            return null;
        }
    }
}
