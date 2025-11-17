package org.springframework.web.method;

import cn.hutool.core.text.CharSequenceUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotatedMethod;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotationPredicates;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ResponseStatus;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/HandlerMethod.class */
public class HandlerMethod extends AnnotatedMethod {
    protected static final Log logger = LogFactory.getLog((Class<?>) HandlerMethod.class);
    private final Object bean;

    @Nullable
    private final BeanFactory beanFactory;

    @Nullable
    private final MessageSource messageSource;
    private final Class<?> beanType;
    private final boolean validateArguments;
    private final boolean validateReturnValue;

    @Nullable
    private HttpStatusCode responseStatus;

    @Nullable
    private String responseStatusReason;

    @Nullable
    private HandlerMethod resolvedFromHandlerMethod;
    private final String description;

    public HandlerMethod(Object bean, Method method) {
        this(bean, method, (MessageSource) null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HandlerMethod(Object bean, Method method, @Nullable MessageSource messageSource) {
        super(method);
        this.bean = bean;
        this.beanFactory = null;
        this.messageSource = messageSource;
        this.beanType = ClassUtils.getUserClass(bean);
        this.validateArguments = false;
        this.validateReturnValue = false;
        evaluateResponseStatus();
        this.description = initDescription(this.beanType, method);
    }

    public HandlerMethod(Object bean, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        super(bean.getClass().getMethod(methodName, parameterTypes));
        this.bean = bean;
        this.beanFactory = null;
        this.messageSource = null;
        this.beanType = ClassUtils.getUserClass(bean);
        this.validateArguments = false;
        this.validateReturnValue = false;
        evaluateResponseStatus();
        this.description = initDescription(this.beanType, getMethod());
    }

    public HandlerMethod(String beanName, BeanFactory beanFactory, Method method) {
        this(beanName, beanFactory, null, method);
    }

    public HandlerMethod(String beanName, BeanFactory beanFactory, @Nullable MessageSource messageSource, Method method) {
        super(method);
        Assert.hasText(beanName, "Bean name is required");
        Assert.notNull(beanFactory, "BeanFactory is required");
        this.bean = beanName;
        this.beanFactory = beanFactory;
        this.messageSource = messageSource;
        Class<?> beanType = beanFactory.getType(beanName);
        if (beanType == null) {
            throw new IllegalStateException("Cannot resolve bean type for bean with name '" + beanName + "'");
        }
        this.beanType = ClassUtils.getUserClass(beanType);
        this.validateArguments = false;
        this.validateReturnValue = false;
        evaluateResponseStatus();
        this.description = initDescription(this.beanType, method);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HandlerMethod(HandlerMethod handlerMethod) {
        this(handlerMethod, (Object) null, false);
    }

    private HandlerMethod(HandlerMethod handlerMethod, @Nullable Object handler, boolean initValidateFlags) {
        super(handlerMethod);
        boolean z;
        boolean z2;
        this.bean = handler != null ? handler : handlerMethod.bean;
        this.beanFactory = handlerMethod.beanFactory;
        this.messageSource = handlerMethod.messageSource;
        this.beanType = handlerMethod.beanType;
        if (initValidateFlags) {
            z = MethodValidationInitializer.checkArguments(this.beanType, getMethodParameters());
        } else {
            z = handlerMethod.validateArguments;
        }
        this.validateArguments = z;
        if (initValidateFlags) {
            z2 = MethodValidationInitializer.checkReturnValue(this.beanType, getBridgedMethod());
        } else {
            z2 = handlerMethod.validateReturnValue;
        }
        this.validateReturnValue = z2;
        this.responseStatus = handlerMethod.responseStatus;
        this.responseStatusReason = handlerMethod.responseStatusReason;
        this.resolvedFromHandlerMethod = handlerMethod;
        this.description = handlerMethod.description;
    }

    private void evaluateResponseStatus() {
        String str;
        ResponseStatus annotation = (ResponseStatus) getMethodAnnotation(ResponseStatus.class);
        if (annotation == null) {
            annotation = (ResponseStatus) AnnotatedElementUtils.findMergedAnnotation(getBeanType(), ResponseStatus.class);
        }
        if (annotation != null) {
            String reason = annotation.reason();
            if (StringUtils.hasText(reason) && this.messageSource != null) {
                str = this.messageSource.getMessage(reason, null, reason, LocaleContextHolder.getLocale());
            } else {
                str = reason;
            }
            String resolvedReason = str;
            this.responseStatus = annotation.code();
            this.responseStatusReason = resolvedReason;
            if (StringUtils.hasText(this.responseStatusReason) && getMethod().getReturnType() != Void.TYPE) {
                logger.warn("Return value of [" + getMethod() + "] will be ignored since @ResponseStatus 'reason' attribute is set.");
            }
        }
    }

    private static String initDescription(Class<?> beanType, Method method) {
        StringJoiner joiner = new StringJoiner(", ", "(", ")");
        for (Class<?> paramType : method.getParameterTypes()) {
            joiner.add(paramType.getSimpleName());
        }
        return beanType.getName() + "#" + method.getName() + joiner;
    }

    public Object getBean() {
        return this.bean;
    }

    public Class<?> getBeanType() {
        return this.beanType;
    }

    @Override // org.springframework.core.annotation.AnnotatedMethod
    protected Class<?> getContainingClass() {
        return this.beanType;
    }

    public boolean shouldValidateArguments() {
        return this.validateArguments;
    }

    public boolean shouldValidateReturnValue() {
        return this.validateReturnValue;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public HttpStatusCode getResponseStatus() {
        return this.responseStatus;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public String getResponseStatusReason() {
        return this.responseStatusReason;
    }

    @Nullable
    public HandlerMethod getResolvedFromHandlerMethod() {
        return this.resolvedFromHandlerMethod;
    }

    public HandlerMethod createWithValidateFlags() {
        return new HandlerMethod(this, (Object) null, true);
    }

    public HandlerMethod createWithResolvedBean() {
        Object handler = this.bean;
        Object obj = this.bean;
        if (obj instanceof String) {
            String beanName = (String) obj;
            Assert.state(this.beanFactory != null, "Cannot resolve bean name without BeanFactory");
            handler = this.beanFactory.getBean(beanName);
        }
        Assert.notNull(handler, "No handler instance");
        return new HandlerMethod(this, handler, false);
    }

    public String getShortLogMessage() {
        return getBeanType().getName() + "#" + getMethod().getName() + "[" + getMethod().getParameterCount() + " args]";
    }

    @Override // org.springframework.core.annotation.AnnotatedMethod
    public boolean equals(@Nullable Object other) {
        return this == other || (super.equals(other) && this.bean.equals(((HandlerMethod) other).bean));
    }

    @Override // org.springframework.core.annotation.AnnotatedMethod
    public int hashCode() {
        return (this.bean.hashCode() * 31) + super.hashCode();
    }

    @Override // org.springframework.core.annotation.AnnotatedMethod
    public String toString() {
        return this.description;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void assertTargetBean(Method method, Object targetBean, Object[] args) {
        Class<?> methodDeclaringClass = method.getDeclaringClass();
        Class<?> targetBeanClass = targetBean.getClass();
        if (!methodDeclaringClass.isAssignableFrom(targetBeanClass)) {
            String text = "The mapped handler method class '" + methodDeclaringClass.getName() + "' is not an instance of the actual controller bean class '" + targetBeanClass.getName() + "'. If the controller requires proxying (e.g. due to @Transactional), please use class-based proxying.";
            throw new IllegalStateException(formatInvokeError(text, args));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String formatInvokeError(String text, Object[] args) {
        String formattedArgs = (String) IntStream.range(0, args.length).mapToObj(i -> {
            if (args[i] != null) {
                return "[" + i + "] [type=" + args[i].getClass().getName() + "] [value=" + args[i] + "]";
            }
            return "[" + i + "] [null]";
        }).collect(Collectors.joining(",\n", CharSequenceUtil.SPACE, CharSequenceUtil.SPACE));
        return text + "\nController [" + getBeanType().getName() + "]\nMethod [" + getBridgedMethod().toGenericString() + "] with argument values:\n" + formattedArgs;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/HandlerMethod$MethodValidationInitializer.class */
    private static class MethodValidationInitializer {
        private static final boolean BEAN_VALIDATION_PRESENT = ClassUtils.isPresent("jakarta.validation.Validator", HandlerMethod.class.getClassLoader());
        private static final Predicate<MergedAnnotation<? extends Annotation>> CONSTRAINT_PREDICATE = MergedAnnotationPredicates.typeIn("jakarta.validation.Constraint");
        private static final Predicate<MergedAnnotation<? extends Annotation>> VALID_PREDICATE = MergedAnnotationPredicates.typeIn("jakarta.validation.Valid");

        private MethodValidationInitializer() {
        }

        public static boolean checkArguments(Class<?> beanType, MethodParameter[] parameters) {
            if (BEAN_VALIDATION_PRESENT && AnnotationUtils.findAnnotation(beanType, Validated.class) == null) {
                for (MethodParameter param : parameters) {
                    MergedAnnotations merged = MergedAnnotations.from(param.getParameterAnnotations());
                    if (merged.stream().anyMatch(CONSTRAINT_PREDICATE)) {
                        return true;
                    }
                    Class<?> type = param.getParameterType();
                    if ((merged.stream().anyMatch(VALID_PREDICATE) && isIndexOrKeyBasedContainer(type)) || MergedAnnotations.from(getContainerElementAnnotations(param)).stream().anyMatch(CONSTRAINT_PREDICATE)) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }

        public static boolean checkReturnValue(Class<?> beanType, Method method) {
            if (BEAN_VALIDATION_PRESENT && AnnotationUtils.findAnnotation(beanType, Validated.class) == null) {
                MergedAnnotations merged = MergedAnnotations.from(method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
                return merged.stream().anyMatch(CONSTRAINT_PREDICATE.or(VALID_PREDICATE));
            }
            return false;
        }

        private static boolean isIndexOrKeyBasedContainer(Class<?> type) {
            return List.class.isAssignableFrom(type) || Object[].class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
        }

        private static Annotation[] getContainerElementAnnotations(MethodParameter param) {
            List<Annotation> result = null;
            int i = param.getParameterIndex();
            Method method = param.getMethod();
            if (method != null) {
                AnnotatedParameterizedType annotatedParameterizedType = method.getAnnotatedParameterTypes()[i];
                if (annotatedParameterizedType instanceof AnnotatedParameterizedType) {
                    AnnotatedParameterizedType apt = annotatedParameterizedType;
                    for (AnnotatedType type : apt.getAnnotatedActualTypeArguments()) {
                        for (Annotation annot : type.getAnnotations()) {
                            result = result != null ? result : new ArrayList<>();
                            result.add(annot);
                        }
                    }
                }
            }
            return (Annotation[]) (result != null ? result : Collections.emptyList()).toArray(new Annotation[0]);
        }
    }
}
