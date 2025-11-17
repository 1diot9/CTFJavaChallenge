package org.springframework.aop.aspectj.annotation;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.aopalliance.aop.Advice;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclareParents;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AbstractAspectJAdvice;
import org.springframework.aop.aspectj.AspectJAfterAdvice;
import org.springframework.aop.aspectj.AspectJAfterReturningAdvice;
import org.springframework.aop.aspectj.AspectJAfterThrowingAdvice;
import org.springframework.aop.aspectj.AspectJAroundAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJMethodBeforeAdvice;
import org.springframework.aop.aspectj.DeclareParentsAdvisor;
import org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactory;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.converter.ConvertingComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.comparator.InstanceComparator;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/ReflectiveAspectJAdvisorFactory.class */
public class ReflectiveAspectJAdvisorFactory extends AbstractAspectJAdvisorFactory implements Serializable {
    private static final ReflectionUtils.MethodFilter adviceMethodFilter = ReflectionUtils.USER_DECLARED_METHODS.and(method -> {
        return AnnotationUtils.getAnnotation(method, Pointcut.class) == null;
    });
    private static final Comparator<Method> adviceMethodComparator;

    @Nullable
    private final BeanFactory beanFactory;

    static {
        Comparator<Method> adviceKindComparator = new ConvertingComparator<>(new InstanceComparator(Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class), method -> {
            AbstractAspectJAdvisorFactory.AspectJAnnotation ann = AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(method);
            if (ann != null) {
                return ann.getAnnotation();
            }
            return null;
        });
        Comparator<Method> methodNameComparator = new ConvertingComparator<>((v0) -> {
            return v0.getName();
        });
        adviceMethodComparator = adviceKindComparator.thenComparing(methodNameComparator);
    }

    public ReflectiveAspectJAdvisorFactory() {
        this(null);
    }

    public ReflectiveAspectJAdvisorFactory(@Nullable BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.aop.aspectj.annotation.AspectJAdvisorFactory
    public List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory aspectInstanceFactory) {
        Class<?> aspectClass = aspectInstanceFactory.getAspectMetadata().getAspectClass();
        String aspectName = aspectInstanceFactory.getAspectMetadata().getAspectName();
        validate(aspectClass);
        MetadataAwareAspectInstanceFactory lazySingletonAspectInstanceFactory = new LazySingletonAspectInstanceFactoryDecorator(aspectInstanceFactory);
        List<Advisor> advisors = new ArrayList<>();
        for (Method method : getAdvisorMethods(aspectClass)) {
            Advisor advisor = getAdvisor(method, lazySingletonAspectInstanceFactory, 0, aspectName);
            if (advisor != null) {
                advisors.add(advisor);
            }
        }
        if (!advisors.isEmpty() && lazySingletonAspectInstanceFactory.getAspectMetadata().isLazilyInstantiated()) {
            Advisor instantiationAdvisor = new SyntheticInstantiationAdvisor(lazySingletonAspectInstanceFactory);
            advisors.add(0, instantiationAdvisor);
        }
        for (Field field : aspectClass.getDeclaredFields()) {
            Advisor advisor2 = getDeclareParentsAdvisor(field);
            if (advisor2 != null) {
                advisors.add(advisor2);
            }
        }
        return advisors;
    }

    private List<Method> getAdvisorMethods(Class<?> aspectClass) {
        List<Method> methods = new ArrayList<>();
        Objects.requireNonNull(methods);
        ReflectionUtils.doWithMethods(aspectClass, (v1) -> {
            r1.add(v1);
        }, adviceMethodFilter);
        if (methods.size() > 1) {
            methods.sort(adviceMethodComparator);
        }
        return methods;
    }

    @Nullable
    private Advisor getDeclareParentsAdvisor(Field introductionField) {
        DeclareParents declareParents = introductionField.getAnnotation(DeclareParents.class);
        if (declareParents == null) {
            return null;
        }
        if (DeclareParents.class == declareParents.defaultImpl()) {
            throw new IllegalStateException("'defaultImpl' attribute must be set on DeclareParents");
        }
        return new DeclareParentsAdvisor(introductionField.getType(), declareParents.value(), (Class<?>) declareParents.defaultImpl());
    }

    @Override // org.springframework.aop.aspectj.annotation.AspectJAdvisorFactory
    @Nullable
    public Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrderInAspect, String aspectName) {
        validate(aspectInstanceFactory.getAspectMetadata().getAspectClass());
        AspectJExpressionPointcut expressionPointcut = getPointcut(candidateAdviceMethod, aspectInstanceFactory.getAspectMetadata().getAspectClass());
        if (expressionPointcut == null) {
            return null;
        }
        return new InstantiationModelAwarePointcutAdvisorImpl(expressionPointcut, candidateAdviceMethod, this, aspectInstanceFactory, declarationOrderInAspect, aspectName);
    }

    @Nullable
    private AspectJExpressionPointcut getPointcut(Method candidateAdviceMethod, Class<?> candidateAspectClass) {
        AbstractAspectJAdvisorFactory.AspectJAnnotation aspectJAnnotation = AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
        if (aspectJAnnotation == null) {
            return null;
        }
        AspectJExpressionPointcut ajexp = new AspectJExpressionPointcut(candidateAspectClass, new String[0], new Class[0]);
        ajexp.setExpression(aspectJAnnotation.getPointcutExpression());
        if (this.beanFactory != null) {
            ajexp.setBeanFactory(this.beanFactory);
        }
        return ajexp;
    }

    @Override // org.springframework.aop.aspectj.annotation.AspectJAdvisorFactory
    @Nullable
    public Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut expressionPointcut, MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName) {
        AbstractAspectJAdvice springAdvice;
        Class<?> candidateAspectClass = aspectInstanceFactory.getAspectMetadata().getAspectClass();
        validate(candidateAspectClass);
        AbstractAspectJAdvisorFactory.AspectJAnnotation aspectJAnnotation = AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
        if (aspectJAnnotation == null) {
            return null;
        }
        if (!isAspect(candidateAspectClass)) {
            throw new AopConfigException("Advice must be declared inside an aspect type: Offending method '" + candidateAdviceMethod + "' in class [" + candidateAspectClass.getName() + "]");
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Found AspectJ method: " + candidateAdviceMethod);
        }
        switch (aspectJAnnotation.getAnnotationType()) {
            case AtPointcut:
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Processing pointcut '" + candidateAdviceMethod.getName() + "'");
                    return null;
                }
                return null;
            case AtAround:
                springAdvice = new AspectJAroundAdvice(candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                break;
            case AtBefore:
                springAdvice = new AspectJMethodBeforeAdvice(candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                break;
            case AtAfter:
                springAdvice = new AspectJAfterAdvice(candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                break;
            case AtAfterReturning:
                springAdvice = new AspectJAfterReturningAdvice(candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                AfterReturning afterReturningAnnotation = aspectJAnnotation.getAnnotation();
                if (StringUtils.hasText(afterReturningAnnotation.returning())) {
                    springAdvice.setReturningName(afterReturningAnnotation.returning());
                    break;
                }
                break;
            case AtAfterThrowing:
                springAdvice = new AspectJAfterThrowingAdvice(candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                AfterThrowing afterThrowingAnnotation = aspectJAnnotation.getAnnotation();
                if (StringUtils.hasText(afterThrowingAnnotation.throwing())) {
                    springAdvice.setThrowingName(afterThrowingAnnotation.throwing());
                    break;
                }
                break;
            default:
                throw new UnsupportedOperationException("Unsupported advice type on method: " + candidateAdviceMethod);
        }
        springAdvice.setAspectName(aspectName);
        springAdvice.setDeclarationOrder(declarationOrder);
        String[] argNames = this.parameterNameDiscoverer.getParameterNames(candidateAdviceMethod);
        if (argNames != null) {
            springAdvice.setArgumentNamesFromStringArray(argNames);
        }
        springAdvice.calculateArgumentBindings();
        return springAdvice;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/ReflectiveAspectJAdvisorFactory$SyntheticInstantiationAdvisor.class */
    protected static class SyntheticInstantiationAdvisor extends DefaultPointcutAdvisor {
        public SyntheticInstantiationAdvisor(final MetadataAwareAspectInstanceFactory aif) {
            super(aif.getAspectMetadata().getPerClausePointcut(), (method, args, target) -> {
                aif.getAspectInstance();
            });
        }
    }
}
