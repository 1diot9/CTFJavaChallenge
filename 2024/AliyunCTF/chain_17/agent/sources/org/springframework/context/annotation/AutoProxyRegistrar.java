package org.springframework.context.annotation;

import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/AutoProxyRegistrar.class */
public class AutoProxyRegistrar implements ImportBeanDefinitionRegistrar {
    private final Log logger = LogFactory.getLog(getClass());

    @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean candidateFound = false;
        Set<String> annTypes = importingClassMetadata.getAnnotationTypes();
        for (String annType : annTypes) {
            AnnotationAttributes candidate = AnnotationConfigUtils.attributesFor(importingClassMetadata, annType);
            if (candidate != null) {
                Object mode = candidate.get(AdviceModeImportSelector.DEFAULT_ADVICE_MODE_ATTRIBUTE_NAME);
                Object proxyTargetClass = candidate.get("proxyTargetClass");
                if (mode != null && proxyTargetClass != null && AdviceMode.class == mode.getClass() && Boolean.class == proxyTargetClass.getClass()) {
                    candidateFound = true;
                    if (mode == AdviceMode.PROXY) {
                        AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
                        if (((Boolean) proxyTargetClass).booleanValue()) {
                            AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
                            return;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        if (!candidateFound && this.logger.isInfoEnabled()) {
            String name = getClass().getSimpleName();
            this.logger.info(String.format("%s was imported but no annotations were found having both 'mode' and 'proxyTargetClass' attributes of type AdviceMode and boolean respectively. This means that auto proxy creator registration and configuration may not have occurred as intended, and components may not be proxied as expected. Check to ensure that %s has been @Import'ed on the same class where these annotations are declared; otherwise remove the import of %s altogether.", name, name, name));
        }
    }
}
