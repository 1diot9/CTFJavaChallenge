package org.springframework.boot.autoconfigure.condition;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationImportEvent;
import org.springframework.boot.autoconfigure.AutoConfigurationImportListener;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/ConditionEvaluationReportAutoConfigurationImportListener.class */
class ConditionEvaluationReportAutoConfigurationImportListener implements AutoConfigurationImportListener, BeanFactoryAware {
    private ConfigurableListableBeanFactory beanFactory;

    ConditionEvaluationReportAutoConfigurationImportListener() {
    }

    @Override // org.springframework.boot.autoconfigure.AutoConfigurationImportListener
    public void onAutoConfigurationImportEvent(AutoConfigurationImportEvent event) {
        if (this.beanFactory != null) {
            ConditionEvaluationReport report = ConditionEvaluationReport.get(this.beanFactory);
            report.recordEvaluationCandidates(event.getCandidateConfigurations());
            report.recordExclusions(event.getExclusions());
        }
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        ConfigurableListableBeanFactory configurableListableBeanFactory;
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            ConfigurableListableBeanFactory listableBeanFactory = (ConfigurableListableBeanFactory) beanFactory;
            configurableListableBeanFactory = listableBeanFactory;
        } else {
            configurableListableBeanFactory = null;
        }
        this.beanFactory = configurableListableBeanFactory;
    }
}
