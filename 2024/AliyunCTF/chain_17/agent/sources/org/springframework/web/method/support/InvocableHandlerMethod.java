package org.springframework.web.method.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import kotlin.Unit;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.jvm.KCallablesJvm;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.context.MessageSource;
import org.springframework.core.CoroutinesUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.method.MethodValidator;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/support/InvocableHandlerMethod.class */
public class InvocableHandlerMethod extends HandlerMethod {
    private static final Object[] EMPTY_ARGS = new Object[0];
    private static final Class<?>[] EMPTY_GROUPS = new Class[0];
    private static final ReflectionUtils.MethodFilter boxImplFilter = method -> {
        return method.isSynthetic() && Modifier.isStatic(method.getModifiers()) && method.getName().equals("box-impl");
    };
    private HandlerMethodArgumentResolverComposite resolvers;
    private ParameterNameDiscoverer parameterNameDiscoverer;

    @Nullable
    private WebDataBinderFactory dataBinderFactory;

    @Nullable
    private MethodValidator methodValidator;

    public InvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
        this.resolvers = new HandlerMethodArgumentResolverComposite();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    public InvocableHandlerMethod(Object bean, Method method) {
        super(bean, method);
        this.resolvers = new HandlerMethodArgumentResolverComposite();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public InvocableHandlerMethod(Object bean, Method method, @Nullable MessageSource messageSource) {
        super(bean, method, messageSource);
        this.resolvers = new HandlerMethodArgumentResolverComposite();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    public InvocableHandlerMethod(Object bean, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        super(bean, methodName, parameterTypes);
        this.resolvers = new HandlerMethodArgumentResolverComposite();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    public void setHandlerMethodArgumentResolvers(HandlerMethodArgumentResolverComposite argumentResolvers) {
        this.resolvers = argumentResolvers;
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    public void setDataBinderFactory(WebDataBinderFactory dataBinderFactory) {
        this.dataBinderFactory = dataBinderFactory;
    }

    public void setMethodValidator(@Nullable MethodValidator methodValidator) {
        this.methodValidator = methodValidator;
    }

    @Nullable
    public Object invokeForRequest(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
        Object[] args = getMethodArgumentValues(request, mavContainer, providedArgs);
        if (logger.isTraceEnabled()) {
            logger.trace("Arguments: " + Arrays.toString(args));
        }
        Class<?>[] groups = getValidationGroups();
        if (shouldValidateArguments() && this.methodValidator != null) {
            this.methodValidator.applyArgumentValidation(getBean(), getBridgedMethod(), getMethodParameters(), args, groups);
        }
        Object returnValue = doInvoke(args);
        if (shouldValidateReturnValue() && this.methodValidator != null) {
            this.methodValidator.applyReturnValueValidation(getBean(), getBridgedMethod(), getReturnType(), returnValue, groups);
        }
        return returnValue;
    }

    protected Object[] getMethodArgumentValues(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
        String exMsg;
        MethodParameter[] parameters = getMethodParameters();
        if (ObjectUtils.isEmpty((Object[]) parameters)) {
            return EMPTY_ARGS;
        }
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
            args[i] = findProvidedArgument(parameter, providedArgs);
            if (args[i] == null) {
                if (!this.resolvers.supportsParameter(parameter)) {
                    throw new IllegalStateException(formatArgumentError(parameter, "No suitable resolver"));
                }
                try {
                    args[i] = this.resolvers.resolveArgument(parameter, mavContainer, request, this.dataBinderFactory);
                } catch (Exception ex) {
                    if (logger.isDebugEnabled() && (exMsg = ex.getMessage()) != null && !exMsg.contains(parameter.getExecutable().toGenericString())) {
                        logger.debug(formatArgumentError(parameter, exMsg));
                    }
                    throw ex;
                }
            }
        }
        return args;
    }

    private Class<?>[] getValidationGroups() {
        return ((shouldValidateArguments() || shouldValidateReturnValue()) && this.methodValidator != null) ? this.methodValidator.determineValidationGroups(getBean(), getBridgedMethod()) : EMPTY_GROUPS;
    }

    @Nullable
    protected Object doInvoke(Object... args) throws Exception {
        Method method = getBridgedMethod();
        try {
            if (KotlinDetector.isKotlinReflectPresent()) {
                if (KotlinDetector.isSuspendingFunction(method)) {
                    return invokeSuspendingFunction(method, getBean(), args);
                }
                if (KotlinDetector.isKotlinType(method.getDeclaringClass())) {
                    return KotlinDelegate.invokeFunction(method, getBean(), args);
                }
            }
            return method.invoke(getBean(), args);
        } catch (IllegalArgumentException ex) {
            assertTargetBean(method, getBean(), args);
            String text = (ex.getMessage() == null || (ex.getCause() instanceof NullPointerException)) ? "Illegal argument" : ex.getMessage();
            throw new IllegalStateException(formatInvokeError(text, args), ex);
        } catch (InvocationTargetException ex2) {
            Throwable targetException = ex2.getCause();
            if (targetException instanceof RuntimeException) {
                RuntimeException runtimeException = (RuntimeException) targetException;
                throw runtimeException;
            }
            if (targetException instanceof Error) {
                Error error = (Error) targetException;
                throw error;
            }
            if (targetException instanceof Exception) {
                Exception exception = (Exception) targetException;
                throw exception;
            }
            throw new IllegalStateException(formatInvokeError("Invocation failure", args), targetException);
        }
    }

    protected Object invokeSuspendingFunction(Method method, Object target, Object[] args) {
        return CoroutinesUtils.invokeSuspendingFunction(method, target, args);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/support/InvocableHandlerMethod$KotlinDelegate.class */
    public static class KotlinDelegate {
        private KotlinDelegate() {
        }

        @Nullable
        public static Object invokeFunction(Method method, Object target, Object[] args) throws InvocationTargetException, IllegalAccessException {
            KFunction<?> function = ReflectJvmMapping.getKotlinFunction(method);
            if (function == null) {
                return method.invoke(target, args);
            }
            if (method.isAccessible() && !KCallablesJvm.isAccessible(function)) {
                KCallablesJvm.setAccessible(function, true);
            }
            Map<KParameter, Object> argMap = CollectionUtils.newHashMap(args.length + 1);
            int index = 0;
            for (KParameter parameter : function.getParameters()) {
                switch (AnonymousClass1.$SwitchMap$kotlin$reflect$KParameter$Kind[parameter.getKind().ordinal()]) {
                    case 1:
                        argMap.put(parameter, target);
                        break;
                    case 2:
                    case 3:
                        if (!parameter.isOptional() || args[index] != null) {
                            KClass<?> classifier = parameter.getType().getClassifier();
                            if (classifier instanceof KClass) {
                                KClass<?> kClass = classifier;
                                if (kClass.isValue()) {
                                    Class<?> javaClass = JvmClassMappingKt.getJavaClass(kClass);
                                    Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(javaClass, InvocableHandlerMethod.boxImplFilter);
                                    Assert.state(methods.length == 1, "Unable to find a single box-impl synthetic static method in " + javaClass.getName());
                                    argMap.put(parameter, ReflectionUtils.invokeMethod(methods[0], null, args[index]));
                                }
                            }
                            argMap.put(parameter, args[index]);
                        }
                        index++;
                        break;
                }
            }
            Object result = function.callBy(argMap);
            if (result == Unit.INSTANCE) {
                return null;
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.springframework.web.method.support.InvocableHandlerMethod$1, reason: invalid class name */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/support/InvocableHandlerMethod$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$kotlin$reflect$KParameter$Kind = new int[KParameter.Kind.values().length];

        static {
            try {
                $SwitchMap$kotlin$reflect$KParameter$Kind[KParameter.Kind.INSTANCE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$kotlin$reflect$KParameter$Kind[KParameter.Kind.VALUE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$kotlin$reflect$KParameter$Kind[KParameter.Kind.EXTENSION_RECEIVER.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }
}
