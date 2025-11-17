package org.springframework.validation.beanvalidation;

import jakarta.validation.NoProviderFoundException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.metadata.BeanDescriptor;
import jakarta.validation.metadata.ConstraintDescriptor;
import jakarta.validation.metadata.ConstructorDescriptor;
import jakarta.validation.metadata.MethodDescriptor;
import jakarta.validation.metadata.MethodType;
import jakarta.validation.metadata.ParameterDescriptor;
import jakarta.validation.metadata.PropertyDescriptor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationCode;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.core.KotlinDetector;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/BeanValidationBeanRegistrationAotProcessor.class */
public class BeanValidationBeanRegistrationAotProcessor implements BeanRegistrationAotProcessor {
    private static final boolean beanValidationPresent = ClassUtils.isPresent("jakarta.validation.Validation", BeanValidationBeanRegistrationAotProcessor.class.getClassLoader());
    private static final Log logger = LogFactory.getLog((Class<?>) BeanValidationBeanRegistrationAotProcessor.class);

    BeanValidationBeanRegistrationAotProcessor() {
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationAotProcessor
    @Nullable
    public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
        if (beanValidationPresent) {
            return BeanValidationDelegate.processAheadOfTime(registeredBean);
        }
        return null;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/BeanValidationBeanRegistrationAotProcessor$BeanValidationDelegate.class */
    private static class BeanValidationDelegate {

        @Nullable
        private static final Validator validator = getValidatorIfAvailable();

        private BeanValidationDelegate() {
        }

        @Nullable
        private static Validator getValidatorIfAvailable() {
            try {
                return Validation.buildDefaultValidatorFactory().getValidator();
            } catch (NoProviderFoundException e) {
                BeanValidationBeanRegistrationAotProcessor.logger.info("No Bean Validation provider available - skipping validation constraint hint inference");
                return null;
            }
        }

        @Nullable
        public static BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
            if (validator == null) {
                return null;
            }
            try {
                BeanDescriptor descriptor = validator.getConstraintsForClass(registeredBean.getBeanClass());
                Set<ConstraintDescriptor<?>> constraintDescriptors = new HashSet<>();
                for (MethodDescriptor methodDescriptor : descriptor.getConstrainedMethods(MethodType.NON_GETTER, new MethodType[]{MethodType.GETTER})) {
                    for (ParameterDescriptor parameterDescriptor : methodDescriptor.getParameterDescriptors()) {
                        constraintDescriptors.addAll(parameterDescriptor.getConstraintDescriptors());
                    }
                }
                for (ConstructorDescriptor constructorDescriptor : descriptor.getConstrainedConstructors()) {
                    for (ParameterDescriptor parameterDescriptor2 : constructorDescriptor.getParameterDescriptors()) {
                        constraintDescriptors.addAll(parameterDescriptor2.getConstraintDescriptors());
                    }
                }
                for (PropertyDescriptor propertyDescriptor : descriptor.getConstrainedProperties()) {
                    constraintDescriptors.addAll(propertyDescriptor.getConstraintDescriptors());
                }
                if (!constraintDescriptors.isEmpty()) {
                    return new AotContribution(constraintDescriptors);
                }
                return null;
            } catch (RuntimeException ex) {
                if (KotlinDetector.isKotlinType(registeredBean.getBeanClass()) && (ex instanceof ArrayIndexOutOfBoundsException)) {
                    BeanValidationBeanRegistrationAotProcessor.logger.warn("Skipping validation constraint hint inference for bean " + registeredBean.getBeanName() + " due to an ArrayIndexOutOfBoundsException at validator level");
                    return null;
                }
                if (ex instanceof TypeNotPresentException) {
                    BeanValidationBeanRegistrationAotProcessor.logger.debug("Skipping validation constraint hint inference for bean " + registeredBean.getBeanName() + " due to a TypeNotPresentException at validator level: " + ex.getMessage());
                    return null;
                }
                BeanValidationBeanRegistrationAotProcessor.logger.warn("Skipping validation constraint hint inference for bean " + registeredBean.getBeanName(), ex);
                return null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/BeanValidationBeanRegistrationAotProcessor$AotContribution.class */
    public static class AotContribution implements BeanRegistrationAotContribution {
        private final Collection<ConstraintDescriptor<?>> constraintDescriptors;

        public AotContribution(Collection<ConstraintDescriptor<?>> constraintDescriptors) {
            this.constraintDescriptors = constraintDescriptors;
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationAotContribution
        public void applyTo(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode) {
            for (ConstraintDescriptor<?> constraintDescriptor : this.constraintDescriptors) {
                for (Class<?> constraintValidatorClass : constraintDescriptor.getConstraintValidatorClasses()) {
                    generationContext.getRuntimeHints().reflection().registerType(constraintValidatorClass, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
                }
            }
        }
    }
}
