package org.springframework.boot.autoconfigure.diagnostics.analyzer;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.analyzer.AbstractInjectionFailureAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/diagnostics/analyzer/NoSuchBeanDefinitionFailureAnalyzer.class */
public class NoSuchBeanDefinitionFailureAnalyzer extends AbstractInjectionFailureAnalyzer<NoSuchBeanDefinitionException> {
    private final ConfigurableListableBeanFactory beanFactory;
    private final MetadataReaderFactory metadataReaderFactory;
    private final ConditionEvaluationReport report;

    NoSuchBeanDefinitionFailureAnalyzer(BeanFactory beanFactory) {
        Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory);
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
        this.metadataReaderFactory = new CachingMetadataReaderFactory(this.beanFactory.getBeanClassLoader());
        this.report = ConditionEvaluationReport.get(this.beanFactory);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.analyzer.AbstractInjectionFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, NoSuchBeanDefinitionException cause, String description) {
        if (cause.getNumberOfBeansFound() != 0) {
            return null;
        }
        List<AutoConfigurationResult> autoConfigurationResults = getAutoConfigurationResults(cause);
        List<UserConfigurationResult> userConfigurationResults = getUserConfigurationResults(cause);
        StringBuilder message = new StringBuilder();
        Object[] objArr = new Object[2];
        objArr[0] = description != null ? description : "A component";
        objArr[1] = getBeanDescription(cause);
        message.append(String.format("%s required %s that could not be found.%n", objArr));
        InjectionPoint injectionPoint = findInjectionPoint(rootFailure);
        if (injectionPoint != null) {
            Annotation[] injectionAnnotations = injectionPoint.getAnnotations();
            if (injectionAnnotations.length > 0) {
                message.append(String.format("%nThe injection point has the following annotations:%n", new Object[0]));
                for (Annotation injectionAnnotation : injectionAnnotations) {
                    message.append(String.format("\t- %s%n", injectionAnnotation));
                }
            }
        }
        if (!autoConfigurationResults.isEmpty() || !userConfigurationResults.isEmpty()) {
            message.append(String.format("%nThe following candidates were found but could not be injected:%n", new Object[0]));
            for (AutoConfigurationResult result : autoConfigurationResults) {
                message.append(String.format("\t- %s%n", result));
            }
            for (UserConfigurationResult result2 : userConfigurationResults) {
                message.append(String.format("\t- %s%n", result2));
            }
        }
        Object[] objArr2 = new Object[2];
        objArr2[0] = (autoConfigurationResults.isEmpty() && userConfigurationResults.isEmpty()) ? "defining" : "revisiting the entries above or defining";
        objArr2[1] = getBeanDescription(cause);
        String action = String.format("Consider %s %s in your configuration.", objArr2);
        return new FailureAnalysis(message.toString(), action, cause);
    }

    private String getBeanDescription(NoSuchBeanDefinitionException cause) {
        if (cause.getResolvableType() != null) {
            Class<?> type = extractBeanType(cause.getResolvableType());
            return "a bean of type '" + type.getName() + "'";
        }
        return "a bean named '" + cause.getBeanName() + "'";
    }

    private Class<?> extractBeanType(ResolvableType resolvableType) {
        return resolvableType.getRawClass();
    }

    private List<AutoConfigurationResult> getAutoConfigurationResults(NoSuchBeanDefinitionException cause) {
        List<AutoConfigurationResult> results = new ArrayList<>();
        collectReportedConditionOutcomes(cause, results);
        collectExcludedAutoConfiguration(cause, results);
        return results;
    }

    private List<UserConfigurationResult> getUserConfigurationResults(NoSuchBeanDefinitionException cause) {
        ResolvableType type = cause.getResolvableType();
        if (type == null) {
            return Collections.emptyList();
        }
        String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this.beanFactory, type);
        return Arrays.stream(beanNames).map(beanName -> {
            return new UserConfigurationResult(getFactoryMethodMetadata(beanName), this.beanFactory.getBean(beanName).equals(null));
        }).toList();
    }

    private MethodMetadata getFactoryMethodMetadata(String beanName) {
        BeanDefinition beanDefinition = this.beanFactory.getBeanDefinition(beanName);
        if (beanDefinition instanceof AnnotatedBeanDefinition) {
            AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) beanDefinition;
            return annotatedBeanDefinition.getFactoryMethodMetadata();
        }
        return null;
    }

    private void collectReportedConditionOutcomes(NoSuchBeanDefinitionException cause, List<AutoConfigurationResult> results) {
        this.report.getConditionAndOutcomesBySource().forEach((source, sourceOutcomes) -> {
            collectReportedConditionOutcomes(cause, new Source(source), sourceOutcomes, results);
        });
    }

    private void collectReportedConditionOutcomes(NoSuchBeanDefinitionException cause, Source source, ConditionEvaluationReport.ConditionAndOutcomes sourceOutcomes, List<AutoConfigurationResult> results) {
        if (sourceOutcomes.isFullMatch()) {
            return;
        }
        BeanMethods methods = new BeanMethods(source, cause);
        Iterator<ConditionEvaluationReport.ConditionAndOutcome> it = sourceOutcomes.iterator();
        while (it.hasNext()) {
            ConditionEvaluationReport.ConditionAndOutcome conditionAndOutcome = it.next();
            if (!conditionAndOutcome.getOutcome().isMatch()) {
                Iterator<MethodMetadata> it2 = methods.iterator();
                while (it2.hasNext()) {
                    MethodMetadata method = it2.next();
                    results.add(new AutoConfigurationResult(method, conditionAndOutcome.getOutcome()));
                }
            }
        }
    }

    private void collectExcludedAutoConfiguration(NoSuchBeanDefinitionException cause, List<AutoConfigurationResult> results) {
        for (String excludedClass : this.report.getExclusions()) {
            Source source = new Source(excludedClass);
            BeanMethods methods = new BeanMethods(source, cause);
            Iterator<MethodMetadata> it = methods.iterator();
            while (it.hasNext()) {
                MethodMetadata method = it.next();
                String message = String.format("auto-configuration '%s' was excluded", ClassUtils.getShortName(excludedClass));
                results.add(new AutoConfigurationResult(method, new ConditionOutcome(false, message)));
            }
        }
    }

    private InjectionPoint findInjectionPoint(Throwable failure) {
        UnsatisfiedDependencyException unsatisfiedDependencyException = (UnsatisfiedDependencyException) findCause(failure, UnsatisfiedDependencyException.class);
        if (unsatisfiedDependencyException == null) {
            return null;
        }
        return unsatisfiedDependencyException.getInjectionPoint();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/diagnostics/analyzer/NoSuchBeanDefinitionFailureAnalyzer$Source.class */
    public static class Source {
        private final String className;
        private final String methodName;

        Source(String source) {
            String[] tokens = source.split("#");
            this.className = tokens.length > 1 ? tokens[0] : source;
            this.methodName = tokens.length != 2 ? null : tokens[1];
        }

        String getClassName() {
            return this.className;
        }

        String getMethodName() {
            return this.methodName;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/diagnostics/analyzer/NoSuchBeanDefinitionFailureAnalyzer$BeanMethods.class */
    public class BeanMethods implements Iterable<MethodMetadata> {
        private final List<MethodMetadata> methods;

        BeanMethods(Source source, NoSuchBeanDefinitionException cause) {
            this.methods = findBeanMethods(source, cause);
        }

        private List<MethodMetadata> findBeanMethods(Source source, NoSuchBeanDefinitionException cause) {
            try {
                MetadataReader classMetadata = NoSuchBeanDefinitionFailureAnalyzer.this.metadataReaderFactory.getMetadataReader(source.getClassName());
                Set<MethodMetadata> candidates = classMetadata.getAnnotationMetadata().getAnnotatedMethods(Bean.class.getName());
                List<MethodMetadata> result = new ArrayList<>();
                for (MethodMetadata candidate : candidates) {
                    if (isMatch(candidate, source, cause)) {
                        result.add(candidate);
                    }
                }
                return Collections.unmodifiableList(result);
            } catch (Exception e) {
                return Collections.emptyList();
            }
        }

        private boolean isMatch(MethodMetadata candidate, Source source, NoSuchBeanDefinitionException cause) {
            if (source.getMethodName() != null && !source.getMethodName().equals(candidate.getMethodName())) {
                return false;
            }
            String name = cause.getBeanName();
            ResolvableType resolvableType = cause.getResolvableType();
            return (name != null && hasName(candidate, name)) || (resolvableType != null && hasType(candidate, NoSuchBeanDefinitionFailureAnalyzer.this.extractBeanType(resolvableType)));
        }

        private boolean hasName(MethodMetadata methodMetadata, String name) {
            Map<String, Object> attributes = methodMetadata.getAnnotationAttributes(Bean.class.getName());
            String[] candidates = attributes != null ? (String[]) attributes.get("name") : null;
            if (candidates != null) {
                for (String candidate : candidates) {
                    if (candidate.equals(name)) {
                        return true;
                    }
                }
                return false;
            }
            return methodMetadata.getMethodName().equals(name);
        }

        private boolean hasType(MethodMetadata candidate, Class<?> type) {
            String returnTypeName = candidate.getReturnTypeName();
            if (type.getName().equals(returnTypeName)) {
                return true;
            }
            try {
                Class<?> returnType = ClassUtils.forName(returnTypeName, NoSuchBeanDefinitionFailureAnalyzer.this.beanFactory.getBeanClassLoader());
                return type.isAssignableFrom(returnType);
            } catch (Throwable th) {
                return false;
            }
        }

        @Override // java.lang.Iterable
        public Iterator<MethodMetadata> iterator() {
            return this.methods.iterator();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/diagnostics/analyzer/NoSuchBeanDefinitionFailureAnalyzer$AutoConfigurationResult.class */
    public static class AutoConfigurationResult {
        private final MethodMetadata methodMetadata;
        private final ConditionOutcome conditionOutcome;

        AutoConfigurationResult(MethodMetadata methodMetadata, ConditionOutcome conditionOutcome) {
            this.methodMetadata = methodMetadata;
            this.conditionOutcome = conditionOutcome;
        }

        public String toString() {
            return String.format("Bean method '%s' in '%s' not loaded because %s", this.methodMetadata.getMethodName(), ClassUtils.getShortName(this.methodMetadata.getDeclaringClassName()), this.conditionOutcome.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/diagnostics/analyzer/NoSuchBeanDefinitionFailureAnalyzer$UserConfigurationResult.class */
    public static class UserConfigurationResult {
        private final MethodMetadata methodMetadata;
        private final boolean nullBean;

        UserConfigurationResult(MethodMetadata methodMetadata, boolean nullBean) {
            this.methodMetadata = methodMetadata;
            this.nullBean = nullBean;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("User-defined bean");
            if (this.methodMetadata != null) {
                sb.append(String.format(" method '%s' in '%s'", this.methodMetadata.getMethodName(), ClassUtils.getShortName(this.methodMetadata.getDeclaringClassName())));
            }
            if (this.nullBean) {
                sb.append(" ignored as the bean value is null");
            }
            return sb.toString();
        }
    }
}
