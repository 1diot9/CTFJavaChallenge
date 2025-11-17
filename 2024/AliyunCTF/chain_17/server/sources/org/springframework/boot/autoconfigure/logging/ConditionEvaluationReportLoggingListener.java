package org.springframework.boot.autoconfigure.logging;

import java.util.function.Supplier;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/logging/ConditionEvaluationReportLoggingListener.class */
public class ConditionEvaluationReportLoggingListener implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private final LogLevel logLevelForReport;

    public ConditionEvaluationReportLoggingListener() {
        this(LogLevel.DEBUG);
    }

    private ConditionEvaluationReportLoggingListener(LogLevel logLevelForReport) {
        Assert.isTrue(isInfoOrDebug(logLevelForReport), "LogLevel must be INFO or DEBUG");
        this.logLevelForReport = logLevelForReport;
    }

    private boolean isInfoOrDebug(LogLevel logLevelForReport) {
        return LogLevel.INFO.equals(logLevelForReport) || LogLevel.DEBUG.equals(logLevelForReport);
    }

    public static ConditionEvaluationReportLoggingListener forLogLevel(LogLevel logLevelForReport) {
        return new ConditionEvaluationReportLoggingListener(logLevelForReport);
    }

    @Override // org.springframework.context.ApplicationContextInitializer
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.addApplicationListener(new ConditionEvaluationReportListener(applicationContext));
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/logging/ConditionEvaluationReportLoggingListener$ConditionEvaluationReportListener.class */
    private final class ConditionEvaluationReportListener implements GenericApplicationListener {
        private final ConfigurableApplicationContext context;
        private final ConditionEvaluationReportLogger logger;

        private ConditionEvaluationReportListener(ConfigurableApplicationContext context) {
            Supplier<ConditionEvaluationReport> reportSupplier;
            this.context = context;
            if (context instanceof GenericApplicationContext) {
                ConditionEvaluationReport report = getReport();
                reportSupplier = () -> {
                    return report;
                };
            } else {
                reportSupplier = this::getReport;
            }
            this.logger = new ConditionEvaluationReportLogger(ConditionEvaluationReportLoggingListener.this.logLevelForReport, reportSupplier);
        }

        private ConditionEvaluationReport getReport() {
            return ConditionEvaluationReport.get(this.context.getBeanFactory());
        }

        @Override // org.springframework.context.event.SmartApplicationListener, org.springframework.core.Ordered
        public int getOrder() {
            return Integer.MAX_VALUE;
        }

        @Override // org.springframework.context.event.GenericApplicationListener
        public boolean supportsEventType(ResolvableType resolvableType) {
            Class<?> type = resolvableType.getRawClass();
            if (type == null) {
                return false;
            }
            return ContextRefreshedEvent.class.isAssignableFrom(type) || ApplicationFailedEvent.class.isAssignableFrom(type);
        }

        @Override // org.springframework.context.event.SmartApplicationListener
        public boolean supportsSourceType(Class<?> sourceType) {
            return true;
        }

        @Override // org.springframework.context.ApplicationListener
        public void onApplicationEvent(ApplicationEvent event) {
            if (event instanceof ContextRefreshedEvent) {
                ContextRefreshedEvent contextRefreshedEvent = (ContextRefreshedEvent) event;
                if (contextRefreshedEvent.getApplicationContext() == this.context) {
                    this.logger.logReport(false);
                    return;
                }
                return;
            }
            if (event instanceof ApplicationFailedEvent) {
                ApplicationFailedEvent applicationFailedEvent = (ApplicationFailedEvent) event;
                if (applicationFailedEvent.getApplicationContext() == this.context) {
                    this.logger.logReport(true);
                }
            }
        }
    }
}
