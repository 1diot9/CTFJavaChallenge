package org.springframework.web.method.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.ExceptionDepthComparator;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/ExceptionHandlerMethodResolver.class */
public class ExceptionHandlerMethodResolver {
    public static final ReflectionUtils.MethodFilter EXCEPTION_HANDLER_METHODS = method -> {
        return AnnotatedElementUtils.hasAnnotation(method, ExceptionHandler.class);
    };
    private static final Method NO_MATCHING_EXCEPTION_HANDLER_METHOD;
    private final Map<Class<? extends Throwable>, Method> mappedMethods = new HashMap(16);
    private final Map<Class<? extends Throwable>, Method> exceptionLookupCache = new ConcurrentReferenceHashMap(16);

    static {
        try {
            NO_MATCHING_EXCEPTION_HANDLER_METHOD = ExceptionHandlerMethodResolver.class.getDeclaredMethod("noMatchingExceptionHandler", new Class[0]);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Expected method not found: " + ex);
        }
    }

    public ExceptionHandlerMethodResolver(Class<?> handlerType) {
        for (Method method : MethodIntrospector.selectMethods(handlerType, EXCEPTION_HANDLER_METHODS)) {
            for (Class<? extends Throwable> exceptionType : detectExceptionMappings(method)) {
                addExceptionMapping(exceptionType, method);
            }
        }
    }

    private List<Class<? extends Throwable>> detectExceptionMappings(Method method) {
        ArrayList arrayList = new ArrayList();
        detectAnnotationExceptionMappings(method, arrayList);
        if (arrayList.isEmpty()) {
            for (Class<?> paramType : method.getParameterTypes()) {
                if (Throwable.class.isAssignableFrom(paramType)) {
                    arrayList.add(paramType);
                }
            }
        }
        if (arrayList.isEmpty()) {
            throw new IllegalStateException("No exception types mapped to " + method);
        }
        return arrayList;
    }

    private void detectAnnotationExceptionMappings(Method method, List<Class<? extends Throwable>> result) {
        ExceptionHandler ann = (ExceptionHandler) AnnotatedElementUtils.findMergedAnnotation(method, ExceptionHandler.class);
        Assert.state(ann != null, "No ExceptionHandler annotation");
        result.addAll(Arrays.asList(ann.value()));
    }

    private void addExceptionMapping(Class<? extends Throwable> exceptionType, Method method) {
        Method oldMethod = this.mappedMethods.put(exceptionType, method);
        if (oldMethod != null && !oldMethod.equals(method)) {
            throw new IllegalStateException("Ambiguous @ExceptionHandler method mapped for [" + exceptionType + "]: {" + oldMethod + ", " + method + "}");
        }
    }

    public boolean hasExceptionMappings() {
        return !this.mappedMethods.isEmpty();
    }

    @Nullable
    public Method resolveMethod(Exception exception) {
        return resolveMethodByThrowable(exception);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    public Method resolveMethodByThrowable(Throwable exception) {
        Throwable cause;
        Method method = resolveMethodByExceptionType(exception.getClass());
        if (method == null && (cause = exception.getCause()) != null) {
            method = resolveMethodByThrowable(cause);
        }
        return method;
    }

    @Nullable
    public Method resolveMethodByExceptionType(Class<? extends Throwable> exceptionType) {
        Method method = this.exceptionLookupCache.get(exceptionType);
        if (method == null) {
            method = getMappedMethod(exceptionType);
            this.exceptionLookupCache.put(exceptionType, method);
        }
        if (method != NO_MATCHING_EXCEPTION_HANDLER_METHOD) {
            return method;
        }
        return null;
    }

    private Method getMappedMethod(Class<? extends Throwable> exceptionType) {
        List<Class<? extends Throwable>> matches = new ArrayList<>();
        for (Class<? extends Throwable> mappedException : this.mappedMethods.keySet()) {
            if (mappedException.isAssignableFrom(exceptionType)) {
                matches.add(mappedException);
            }
        }
        if (!matches.isEmpty()) {
            if (matches.size() > 1) {
                matches.sort(new ExceptionDepthComparator(exceptionType));
            }
            return this.mappedMethods.get(matches.get(0));
        }
        return NO_MATCHING_EXCEPTION_HANDLER_METHOD;
    }

    private void noMatchingExceptionHandler() {
    }
}
