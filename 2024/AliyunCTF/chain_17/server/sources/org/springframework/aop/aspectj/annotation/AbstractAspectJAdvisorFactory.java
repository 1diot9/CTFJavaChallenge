package org.springframework.aop.aspectj.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.PerClauseKind;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/AbstractAspectJAdvisorFactory.class */
public abstract class AbstractAspectJAdvisorFactory implements AspectJAdvisorFactory {
    private static final String AJC_MAGIC = "ajc$";
    private static final Class<?>[] ASPECTJ_ANNOTATION_CLASSES = {Pointcut.class, Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class};
    protected final Log logger = LogFactory.getLog(getClass());
    protected final ParameterNameDiscoverer parameterNameDiscoverer = new AspectJAnnotationParameterNameDiscoverer();

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/AbstractAspectJAdvisorFactory$AspectJAnnotationType.class */
    public enum AspectJAnnotationType {
        AtPointcut,
        AtAround,
        AtBefore,
        AtAfter,
        AtAfterReturning,
        AtAfterThrowing
    }

    @Override // org.springframework.aop.aspectj.annotation.AspectJAdvisorFactory
    public boolean isAspect(Class<?> clazz) {
        return hasAspectAnnotation(clazz) && !compiledByAjc(clazz);
    }

    private boolean hasAspectAnnotation(Class<?> clazz) {
        return AnnotationUtils.findAnnotation(clazz, Aspect.class) != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean compiledByAjc(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().startsWith(AJC_MAGIC)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.aop.aspectj.annotation.AspectJAdvisorFactory
    public void validate(Class<?> aspectClass) throws AopConfigException {
        AjType<?> ajType = AjTypeSystem.getAjType(aspectClass);
        if (!ajType.isAspect()) {
            throw new NotAnAtAspectException(aspectClass);
        }
        if (ajType.getPerClause().getKind() == PerClauseKind.PERCFLOW) {
            throw new AopConfigException(aspectClass.getName() + " uses percflow instantiation model: This is not supported in Spring AOP.");
        }
        if (ajType.getPerClause().getKind() == PerClauseKind.PERCFLOWBELOW) {
            throw new AopConfigException(aspectClass.getName() + " uses percflowbelow instantiation model: This is not supported in Spring AOP.");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public static AspectJAnnotation findAspectJAnnotationOnMethod(Method method) {
        for (Class<?> annotationType : ASPECTJ_ANNOTATION_CLASSES) {
            AspectJAnnotation annotation = findAnnotation(method, annotationType);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }

    @Nullable
    private static AspectJAnnotation findAnnotation(Method method, Class<? extends Annotation> annotationType) {
        Annotation annotation = AnnotationUtils.findAnnotation(method, (Class<Annotation>) annotationType);
        if (annotation != null) {
            return new AspectJAnnotation(annotation);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/AbstractAspectJAdvisorFactory$AspectJAnnotation.class */
    public static class AspectJAnnotation {
        private static final String[] EXPRESSION_ATTRIBUTES = {"pointcut", "value"};
        private static final Map<Class<?>, AspectJAnnotationType> annotationTypeMap = Map.of(Pointcut.class, AspectJAnnotationType.AtPointcut, Around.class, AspectJAnnotationType.AtAround, Before.class, AspectJAnnotationType.AtBefore, After.class, AspectJAnnotationType.AtAfter, AfterReturning.class, AspectJAnnotationType.AtAfterReturning, AfterThrowing.class, AspectJAnnotationType.AtAfterThrowing);
        private final Annotation annotation;
        private final AspectJAnnotationType annotationType;
        private final String pointcutExpression;
        private final String argumentNames;

        public AspectJAnnotation(Annotation annotation) {
            String str;
            this.annotation = annotation;
            this.annotationType = determineAnnotationType(annotation);
            try {
                this.pointcutExpression = resolvePointcutExpression(annotation);
                Object argNames = AnnotationUtils.getValue(annotation, "argNames");
                if (argNames instanceof String) {
                    String names = (String) argNames;
                    str = names;
                } else {
                    str = "";
                }
                this.argumentNames = str;
            } catch (Exception ex) {
                throw new IllegalArgumentException(annotation + " is not a valid AspectJ annotation", ex);
            }
        }

        private AspectJAnnotationType determineAnnotationType(Annotation annotation) {
            AspectJAnnotationType type = annotationTypeMap.get(annotation.annotationType());
            if (type != null) {
                return type;
            }
            throw new IllegalStateException("Unknown annotation type: " + annotation);
        }

        private String resolvePointcutExpression(Annotation annotation) {
            for (String attributeName : EXPRESSION_ATTRIBUTES) {
                Object val = AnnotationUtils.getValue(annotation, attributeName);
                if (val instanceof String) {
                    String str = (String) val;
                    if (!str.isEmpty()) {
                        return str;
                    }
                }
            }
            throw new IllegalStateException("Failed to resolve pointcut expression in: " + annotation);
        }

        public AspectJAnnotationType getAnnotationType() {
            return this.annotationType;
        }

        public Annotation getAnnotation() {
            return this.annotation;
        }

        public String getPointcutExpression() {
            return this.pointcutExpression;
        }

        public String getArgumentNames() {
            return this.argumentNames;
        }

        public String toString() {
            return this.annotation.toString();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/AbstractAspectJAdvisorFactory$AspectJAnnotationParameterNameDiscoverer.class */
    private static class AspectJAnnotationParameterNameDiscoverer implements ParameterNameDiscoverer {
        private static final String[] EMPTY_ARRAY = new String[0];

        private AspectJAnnotationParameterNameDiscoverer() {
        }

        @Override // org.springframework.core.ParameterNameDiscoverer
        @Nullable
        public String[] getParameterNames(Method method) {
            StringTokenizer nameTokens;
            int numTokens;
            if (method.getParameterCount() == 0) {
                return EMPTY_ARRAY;
            }
            AspectJAnnotation annotation = AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(method);
            if (annotation != null && (numTokens = (nameTokens = new StringTokenizer(annotation.getArgumentNames(), ",")).countTokens()) > 0) {
                String[] names = new String[numTokens];
                for (int i = 0; i < names.length; i++) {
                    names[i] = nameTokens.nextToken();
                }
                return names;
            }
            return null;
        }

        @Override // org.springframework.core.ParameterNameDiscoverer
        @Nullable
        public String[] getParameterNames(Constructor<?> ctor) {
            throw new UnsupportedOperationException("Spring AOP cannot handle constructor advice");
        }
    }
}
