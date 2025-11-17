package org.springframework.aop.aspectj.annotation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import org.aopalliance.aop.Advice;
import org.aspectj.lang.reflect.PerClauseKind;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJPrecedenceInformation;
import org.springframework.aop.aspectj.InstantiationModelAwarePointcutAdvisor;
import org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactory;
import org.springframework.aop.support.DynamicMethodMatcherPointcut;
import org.springframework.aop.support.Pointcuts;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/InstantiationModelAwarePointcutAdvisorImpl.class */
final class InstantiationModelAwarePointcutAdvisorImpl implements InstantiationModelAwarePointcutAdvisor, AspectJPrecedenceInformation, Serializable {
    private static final Advice EMPTY_ADVICE = new Advice() { // from class: org.springframework.aop.aspectj.annotation.InstantiationModelAwarePointcutAdvisorImpl.1
    };
    private final AspectJExpressionPointcut declaredPointcut;
    private final Class<?> declaringClass;
    private final String methodName;
    private final Class<?>[] parameterTypes;
    private transient Method aspectJAdviceMethod;
    private final AspectJAdvisorFactory aspectJAdvisorFactory;
    private final MetadataAwareAspectInstanceFactory aspectInstanceFactory;
    private final int declarationOrder;
    private final String aspectName;
    private final Pointcut pointcut;
    private final boolean lazy;

    @Nullable
    private Advice instantiatedAdvice;

    @Nullable
    private Boolean isBeforeAdvice;

    @Nullable
    private Boolean isAfterAdvice;

    public InstantiationModelAwarePointcutAdvisorImpl(AspectJExpressionPointcut declaredPointcut, Method aspectJAdviceMethod, AspectJAdvisorFactory aspectJAdvisorFactory, MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName) {
        this.declaredPointcut = declaredPointcut;
        this.declaringClass = aspectJAdviceMethod.getDeclaringClass();
        this.methodName = aspectJAdviceMethod.getName();
        this.parameterTypes = aspectJAdviceMethod.getParameterTypes();
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.aspectJAdvisorFactory = aspectJAdvisorFactory;
        this.aspectInstanceFactory = aspectInstanceFactory;
        this.declarationOrder = declarationOrder;
        this.aspectName = aspectName;
        if (aspectInstanceFactory.getAspectMetadata().isLazilyInstantiated()) {
            Pointcut preInstantiationPointcut = Pointcuts.union(aspectInstanceFactory.getAspectMetadata().getPerClausePointcut(), this.declaredPointcut);
            this.pointcut = new PerTargetInstantiationModelPointcut(this.declaredPointcut, preInstantiationPointcut, aspectInstanceFactory);
            this.lazy = true;
        } else {
            this.pointcut = this.declaredPointcut;
            this.lazy = false;
            this.instantiatedAdvice = instantiateAdvice(this.declaredPointcut);
        }
    }

    @Override // org.springframework.aop.PointcutAdvisor
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override // org.springframework.aop.aspectj.InstantiationModelAwarePointcutAdvisor
    public boolean isLazy() {
        return this.lazy;
    }

    @Override // org.springframework.aop.aspectj.InstantiationModelAwarePointcutAdvisor
    public synchronized boolean isAdviceInstantiated() {
        return this.instantiatedAdvice != null;
    }

    @Override // org.springframework.aop.Advisor
    public synchronized Advice getAdvice() {
        if (this.instantiatedAdvice == null) {
            this.instantiatedAdvice = instantiateAdvice(this.declaredPointcut);
        }
        return this.instantiatedAdvice;
    }

    private Advice instantiateAdvice(AspectJExpressionPointcut pointcut) {
        Advice advice = this.aspectJAdvisorFactory.getAdvice(this.aspectJAdviceMethod, pointcut, this.aspectInstanceFactory, this.declarationOrder, this.aspectName);
        return advice != null ? advice : EMPTY_ADVICE;
    }

    @Override // org.springframework.aop.Advisor
    public boolean isPerInstance() {
        return getAspectMetadata().getAjType().getPerClause().getKind() != PerClauseKind.SINGLETON;
    }

    public AspectMetadata getAspectMetadata() {
        return this.aspectInstanceFactory.getAspectMetadata();
    }

    public MetadataAwareAspectInstanceFactory getAspectInstanceFactory() {
        return this.aspectInstanceFactory;
    }

    public AspectJExpressionPointcut getDeclaredPointcut() {
        return this.declaredPointcut;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.aspectInstanceFactory.getOrder();
    }

    @Override // org.springframework.aop.aspectj.AspectJPrecedenceInformation
    public String getAspectName() {
        return this.aspectName;
    }

    @Override // org.springframework.aop.aspectj.AspectJPrecedenceInformation
    public int getDeclarationOrder() {
        return this.declarationOrder;
    }

    @Override // org.springframework.aop.aspectj.AspectJPrecedenceInformation
    public boolean isBeforeAdvice() {
        if (this.isBeforeAdvice == null) {
            determineAdviceType();
        }
        return this.isBeforeAdvice.booleanValue();
    }

    @Override // org.springframework.aop.aspectj.AspectJPrecedenceInformation
    public boolean isAfterAdvice() {
        if (this.isAfterAdvice == null) {
            determineAdviceType();
        }
        return this.isAfterAdvice.booleanValue();
    }

    private void determineAdviceType() {
        AbstractAspectJAdvisorFactory.AspectJAnnotation aspectJAnnotation = AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(this.aspectJAdviceMethod);
        if (aspectJAnnotation == null) {
            this.isBeforeAdvice = false;
            this.isAfterAdvice = false;
            return;
        }
        switch (aspectJAnnotation.getAnnotationType()) {
            case AtPointcut:
            case AtAround:
                this.isBeforeAdvice = false;
                this.isAfterAdvice = false;
                return;
            case AtBefore:
                this.isBeforeAdvice = true;
                this.isAfterAdvice = false;
                return;
            case AtAfter:
            case AtAfterReturning:
            case AtAfterThrowing:
                this.isBeforeAdvice = false;
                this.isAfterAdvice = true;
                return;
            default:
                return;
        }
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        try {
            this.aspectJAdviceMethod = this.declaringClass.getMethod(this.methodName, this.parameterTypes);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Failed to find advice method on deserialization", ex);
        }
    }

    public String toString() {
        return "InstantiationModelAwarePointcutAdvisor: expression [" + getDeclaredPointcut().getExpression() + "]; advice method [" + this.aspectJAdviceMethod + "]; perClauseKind=" + this.aspectInstanceFactory.getAspectMetadata().getAjType().getPerClause().getKind();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/InstantiationModelAwarePointcutAdvisorImpl$PerTargetInstantiationModelPointcut.class */
    private static final class PerTargetInstantiationModelPointcut extends DynamicMethodMatcherPointcut {
        private final AspectJExpressionPointcut declaredPointcut;
        private final Pointcut preInstantiationPointcut;

        @Nullable
        private LazySingletonAspectInstanceFactoryDecorator aspectInstanceFactory;

        public PerTargetInstantiationModelPointcut(AspectJExpressionPointcut declaredPointcut, Pointcut preInstantiationPointcut, MetadataAwareAspectInstanceFactory aspectInstanceFactory) {
            this.declaredPointcut = declaredPointcut;
            this.preInstantiationPointcut = preInstantiationPointcut;
            if (aspectInstanceFactory instanceof LazySingletonAspectInstanceFactoryDecorator) {
                LazySingletonAspectInstanceFactoryDecorator lazyFactory = (LazySingletonAspectInstanceFactoryDecorator) aspectInstanceFactory;
                this.aspectInstanceFactory = lazyFactory;
            }
        }

        @Override // org.springframework.aop.support.DynamicMethodMatcher, org.springframework.aop.MethodMatcher
        public boolean matches(Method method, Class<?> targetClass) {
            return (isAspectMaterialized() && this.declaredPointcut.matches(method, targetClass)) || this.preInstantiationPointcut.getMethodMatcher().matches(method, targetClass);
        }

        @Override // org.springframework.aop.MethodMatcher
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            return isAspectMaterialized() && this.declaredPointcut.matches(method, targetClass, args);
        }

        private boolean isAspectMaterialized() {
            return this.aspectInstanceFactory == null || this.aspectInstanceFactory.isMaterialized();
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof PerTargetInstantiationModelPointcut) {
                    PerTargetInstantiationModelPointcut that = (PerTargetInstantiationModelPointcut) other;
                    if (ObjectUtils.nullSafeEquals(this.preInstantiationPointcut, that.preInstantiationPointcut)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.declaredPointcut.getExpression());
        }

        public String toString() {
            return PerTargetInstantiationModelPointcut.class.getName() + ": " + this.declaredPointcut.getExpression();
        }
    }
}
