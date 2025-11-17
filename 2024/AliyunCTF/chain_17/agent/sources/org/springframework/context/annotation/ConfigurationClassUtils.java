package org.springframework.context.annotation;

import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.core.Conventions;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClassUtils.class */
public abstract class ConfigurationClassUtils {
    static final String CONFIGURATION_CLASS_FULL = "full";
    static final String CONFIGURATION_CLASS_LITE = "lite";
    static final String CANDIDATE_ATTRIBUTE = Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "candidate");
    static final String CONFIGURATION_CLASS_ATTRIBUTE = Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "configurationClass");
    static final String ORDER_ATTRIBUTE = Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, AbstractBeanDefinition.ORDER_ATTRIBUTE);
    private static final Log logger = LogFactory.getLog((Class<?>) ConfigurationClassUtils.class);
    private static final Set<String> candidateIndicators = Set.of(Component.class.getName(), ComponentScan.class.getName(), Import.class.getName(), ImportResource.class.getName());

    public static Class<?> initializeConfigurationClass(Class<?> userClass) {
        Class<?> configurationClass = new ConfigurationClassEnhancer().enhance(userClass, null);
        Enhancer.registerStaticCallbacks(configurationClass, ConfigurationClassEnhancer.CALLBACKS);
        return configurationClass;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:12:0x00d5  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x012e  */
    /* JADX WARN: Removed duplicated region for block: B:20:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x00fa  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static boolean checkConfigurationClassCandidate(org.springframework.beans.factory.config.BeanDefinition r4, org.springframework.core.type.classreading.MetadataReaderFactory r5) {
        /*
            Method dump skipped, instructions count: 315
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.context.annotation.ConfigurationClassUtils.checkConfigurationClassCandidate(org.springframework.beans.factory.config.BeanDefinition, org.springframework.core.type.classreading.MetadataReaderFactory):boolean");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isConfigurationCandidate(AnnotationMetadata metadata) {
        if (metadata.isInterface()) {
            return false;
        }
        for (String indicator : candidateIndicators) {
            if (metadata.isAnnotated(indicator)) {
                return true;
            }
        }
        return hasBeanMethods(metadata);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean hasBeanMethods(AnnotationMetadata metadata) {
        try {
            return metadata.hasAnnotatedMethods(Bean.class.getName());
        } catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to introspect @Bean methods on class [" + metadata.getClassName() + "]: " + ex);
                return false;
            }
            return false;
        }
    }

    @Nullable
    public static Integer getOrder(AnnotationMetadata metadata) {
        Map<String, Object> orderAttributes = metadata.getAnnotationAttributes(Order.class.getName());
        if (orderAttributes != null) {
            return (Integer) orderAttributes.get("value");
        }
        return null;
    }

    public static int getOrder(BeanDefinition beanDef) {
        Integer order = (Integer) beanDef.getAttribute(ORDER_ATTRIBUTE);
        if (order != null) {
            return order.intValue();
        }
        return Integer.MAX_VALUE;
    }
}
