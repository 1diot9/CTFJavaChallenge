package org.springframework.boot.autoconfigure.logging;

import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.boot.logging.LogLevel;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/logging/ConditionEvaluationReportLoggingProcessor.class */
class ConditionEvaluationReportLoggingProcessor implements BeanFactoryInitializationAotProcessor {
    ConditionEvaluationReportLoggingProcessor() {
    }

    @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
    public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
        logConditionEvaluationReport(beanFactory);
        return null;
    }

    private void logConditionEvaluationReport(ConfigurableListableBeanFactory beanFactory) {
        new ConditionEvaluationReportLogger(LogLevel.DEBUG, () -> {
            return ConditionEvaluationReport.get(beanFactory);
        }).logReport(false);
    }
}
