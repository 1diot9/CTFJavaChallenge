package org.springframework.web.servlet.mvc.method.annotation;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.SpringObjenesis;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.CompositeUriComponentsContributor;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/MvcUriComponentsBuilder.class */
public class MvcUriComponentsBuilder {
    public static final String MVC_URI_COMPONENTS_CONTRIBUTOR_BEAN_NAME = "mvcUriComponentsContributor";
    private static final Log logger = LogFactory.getLog((Class<?>) MvcUriComponentsBuilder.class);
    private static final SpringObjenesis objenesis = new SpringObjenesis();
    private static final PathMatcher pathMatcher = new AntPathMatcher();
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private static final CompositeUriComponentsContributor defaultUriComponentsContributor = new CompositeUriComponentsContributor(new PathVariableMethodArgumentResolver(), new RequestParamMethodArgumentResolver(false));
    private final UriComponentsBuilder baseUrl;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/MvcUriComponentsBuilder$MethodInvocationInfo.class */
    public interface MethodInvocationInfo {
        Class<?> getControllerType();

        Method getControllerMethod();

        Object[] getArgumentValues();
    }

    protected MvcUriComponentsBuilder(UriComponentsBuilder baseUrl) {
        Assert.notNull(baseUrl, "'baseUrl' is required");
        this.baseUrl = baseUrl;
    }

    public static MvcUriComponentsBuilder relativeTo(UriComponentsBuilder baseUrl) {
        return new MvcUriComponentsBuilder(baseUrl);
    }

    public static UriComponentsBuilder fromController(Class<?> controllerType) {
        return fromController(null, controllerType);
    }

    public static UriComponentsBuilder fromController(@Nullable UriComponentsBuilder builder, Class<?> controllerType) {
        UriComponentsBuilder builder2 = getBaseUrlToUse(builder);
        String prefix = getPathPrefix(controllerType);
        builder2.path(prefix);
        String mapping = getClassMapping(controllerType);
        builder2.path(!StringUtils.hasText(prefix + mapping) ? "/" : mapping);
        return builder2;
    }

    public static UriComponentsBuilder fromMethodName(Class<?> controllerType, String methodName, Object... args) {
        Method method = getMethod(controllerType, methodName, args);
        return fromMethodInternal(null, controllerType, method, args);
    }

    public static UriComponentsBuilder fromMethodName(UriComponentsBuilder builder, Class<?> controllerType, String methodName, Object... args) {
        Method method = getMethod(controllerType, methodName, args);
        return fromMethodInternal(builder, controllerType, method, args);
    }

    public static UriComponentsBuilder fromMethod(Class<?> controllerType, Method method, Object... args) {
        return fromMethodInternal(null, controllerType, method, args);
    }

    public static UriComponentsBuilder fromMethod(UriComponentsBuilder baseUrl, @Nullable Class<?> controllerType, Method method, Object... args) {
        return fromMethodInternal(baseUrl, controllerType != null ? controllerType : method.getDeclaringClass(), method, args);
    }

    public static UriComponentsBuilder fromMethodCall(Object info) {
        Assert.isInstanceOf((Class<?>) MethodInvocationInfo.class, info, "MethodInvocationInfo required");
        MethodInvocationInfo invocationInfo = (MethodInvocationInfo) info;
        Class<?> controllerType = invocationInfo.getControllerType();
        Method method = invocationInfo.getControllerMethod();
        Object[] arguments = invocationInfo.getArgumentValues();
        return fromMethodInternal(null, controllerType, method, arguments);
    }

    public static UriComponentsBuilder fromMethodCall(UriComponentsBuilder builder, Object info) {
        Assert.isInstanceOf((Class<?>) MethodInvocationInfo.class, info, "MethodInvocationInfo required");
        MethodInvocationInfo invocationInfo = (MethodInvocationInfo) info;
        Class<?> controllerType = invocationInfo.getControllerType();
        Method method = invocationInfo.getControllerMethod();
        Object[] arguments = invocationInfo.getArgumentValues();
        return fromMethodInternal(builder, controllerType, method, arguments);
    }

    public static <T> T on(Class<T> cls) {
        return (T) controller(cls);
    }

    public static <T> T controller(Class<T> cls) {
        Assert.notNull(cls, "'controllerType' must not be null");
        return (T) ControllerMethodInvocationInterceptor.initProxy(cls, null);
    }

    public static MethodArgumentBuilder fromMappingName(String mappingName) {
        return fromMappingName(null, mappingName);
    }

    public static MethodArgumentBuilder fromMappingName(@Nullable UriComponentsBuilder builder, String name) {
        WebApplicationContext wac = getWebApplicationContext();
        Assert.state(wac != null, "No WebApplicationContext");
        Map<String, RequestMappingInfoHandlerMapping> map = wac.getBeansOfType(RequestMappingInfoHandlerMapping.class);
        List<HandlerMethod> handlerMethods = null;
        for (RequestMappingInfoHandlerMapping mapping : map.values()) {
            handlerMethods = mapping.getHandlerMethodsForMappingName(name);
            if (handlerMethods != null) {
                break;
            }
        }
        if (handlerMethods == null) {
            throw new IllegalArgumentException("Mapping not found: " + name);
        }
        if (handlerMethods.size() != 1) {
            throw new IllegalArgumentException("No unique match for mapping " + name + ": " + handlerMethods);
        }
        HandlerMethod handlerMethod = handlerMethods.get(0);
        Class<?> controllerType = handlerMethod.getBeanType();
        Method method = handlerMethod.getMethod();
        return new MethodArgumentBuilder(builder, controllerType, method);
    }

    public UriComponentsBuilder withController(Class<?> controllerType) {
        return fromController(this.baseUrl, controllerType);
    }

    public UriComponentsBuilder withMethodName(Class<?> controllerType, String methodName, Object... args) {
        return fromMethodName(this.baseUrl, controllerType, methodName, args);
    }

    public UriComponentsBuilder withMethodCall(Object invocationInfo) {
        return fromMethodCall(this.baseUrl, invocationInfo);
    }

    public MethodArgumentBuilder withMappingName(String mappingName) {
        return fromMappingName(this.baseUrl, mappingName);
    }

    public UriComponentsBuilder withMethod(Class<?> controllerType, Method method, Object... args) {
        return fromMethod(this.baseUrl, controllerType, method, args);
    }

    private static UriComponentsBuilder fromMethodInternal(@Nullable UriComponentsBuilder builder, Class<?> controllerType, Method method, Object... args) {
        UriComponentsBuilder builder2 = getBaseUrlToUse(builder);
        String prefix = getPathPrefix(controllerType);
        builder2.path(prefix);
        String typePath = getClassMapping(controllerType);
        String methodPath = getMethodMapping(method);
        String path = PathPatternParser.defaultInstance.initFullPathPattern(pathMatcher.combine(typePath, methodPath));
        if (!StringUtils.hasText(prefix + path)) {
            path = "/";
        }
        builder2.path(path);
        return applyContributors(builder2, method, args);
    }

    private static UriComponentsBuilder getBaseUrlToUse(@Nullable UriComponentsBuilder baseUrl) {
        if (baseUrl == null) {
            return ServletUriComponentsBuilder.fromCurrentServletMapping();
        }
        return baseUrl.cloneBuilder();
    }

    private static String getPathPrefix(Class<?> controllerType) {
        String prefix;
        WebApplicationContext wac = getWebApplicationContext();
        if (wac != null) {
            Map<String, RequestMappingHandlerMapping> map = wac.getBeansOfType(RequestMappingHandlerMapping.class);
            for (RequestMappingHandlerMapping mapping : map.values()) {
                if (mapping.isHandler(controllerType) && (prefix = mapping.getPathPrefix(controllerType)) != null) {
                    return prefix;
                }
            }
            return "";
        }
        return "";
    }

    private static String getClassMapping(Class<?> controllerType) {
        Assert.notNull(controllerType, "'controllerType' must not be null");
        RequestMapping mapping = (RequestMapping) AnnotatedElementUtils.findMergedAnnotation(controllerType, RequestMapping.class);
        if (mapping == null) {
            return "";
        }
        return getPathMapping(mapping, controllerType.getName());
    }

    private static String getMethodMapping(Method method) {
        Assert.notNull(method, "'method' must not be null");
        RequestMapping requestMapping = (RequestMapping) AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if (requestMapping == null) {
            throw new IllegalArgumentException("No @RequestMapping on: " + method.toGenericString());
        }
        return getPathMapping(requestMapping, method.toGenericString());
    }

    private static String getPathMapping(RequestMapping requestMapping, String source) {
        String[] paths = requestMapping.path();
        if (ObjectUtils.isEmpty((Object[]) paths) || !StringUtils.hasLength(paths[0])) {
            return "";
        }
        if (paths.length > 1 && logger.isTraceEnabled()) {
            logger.trace("Using first of multiple paths on " + source);
        }
        return resolveEmbeddedValue(paths[0]);
    }

    private static Method getMethod(Class<?> controllerType, final String methodName, final Object... args) {
        ReflectionUtils.MethodFilter selector = method -> {
            String name = method.getName();
            int argLength = method.getParameterCount();
            return name.equals(methodName) && argLength == args.length;
        };
        Set<Method> methods = MethodIntrospector.selectMethods(controllerType, selector);
        if (methods.size() == 1) {
            return methods.iterator().next();
        }
        if (methods.size() > 1) {
            throw new IllegalArgumentException(String.format("Found two methods named '%s' accepting arguments %s in controller %s: [%s]", methodName, Arrays.asList(args), controllerType.getName(), methods));
        }
        throw new IllegalArgumentException("No method named '" + methodName + "' with " + args.length + " arguments found in controller " + controllerType.getName());
    }

    private static UriComponentsBuilder applyContributors(UriComponentsBuilder builder, Method method, Object... args) {
        CompositeUriComponentsContributor contributor = getUriComponentsContributor();
        int paramCount = method.getParameterCount();
        int argCount = args.length;
        if (paramCount != argCount) {
            throw new IllegalArgumentException("Number of method parameters " + paramCount + " does not match number of argument values " + argCount);
        }
        Map<String, Object> uriVars = new HashMap<>();
        for (int i = 0; i < paramCount; i++) {
            MethodParameter param = new SynthesizingMethodParameter(method, i);
            param.initParameterNameDiscovery(parameterNameDiscoverer);
            contributor.contributeMethodArgument(param, args[i], builder, uriVars);
        }
        return builder.uriVariables(uriVars);
    }

    private static CompositeUriComponentsContributor getUriComponentsContributor() {
        WebApplicationContext wac = getWebApplicationContext();
        if (wac != null) {
            try {
                return (CompositeUriComponentsContributor) wac.getBean(MVC_URI_COMPONENTS_CONTRIBUTOR_BEAN_NAME, CompositeUriComponentsContributor.class);
            } catch (NoSuchBeanDefinitionException e) {
            }
        }
        return defaultUriComponentsContributor;
    }

    private static String resolveEmbeddedValue(String value) {
        WebApplicationContext webApplicationContext;
        if (value.contains("${") && (webApplicationContext = getWebApplicationContext()) != null) {
            AutowireCapableBeanFactory autowireCapableBeanFactory = webApplicationContext.getAutowireCapableBeanFactory();
            if (autowireCapableBeanFactory instanceof ConfigurableBeanFactory) {
                ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) autowireCapableBeanFactory;
                String resolvedEmbeddedValue = cbf.resolveEmbeddedValue(value);
                if (resolvedEmbeddedValue != null) {
                    return resolvedEmbeddedValue;
                }
            }
        }
        return value;
    }

    @Nullable
    private static WebApplicationContext getWebApplicationContext() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String attributeName = DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE;
        WebApplicationContext wac = (WebApplicationContext) request.getAttribute(attributeName);
        if (wac == null) {
            return null;
        }
        return wac;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/MvcUriComponentsBuilder$ControllerMethodInvocationInterceptor.class */
    public static class ControllerMethodInvocationInterceptor implements MethodInterceptor, InvocationHandler, MethodInvocationInfo {
        private final Class<?> controllerType;

        @Nullable
        private Method controllerMethod;

        @Nullable
        private Object[] argumentValues;

        ControllerMethodInvocationInterceptor(Class<?> controllerType) {
            this.controllerType = controllerType;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        @Nullable
        public Object intercept(@Nullable Object obj, Method method, Object[] args, @Nullable MethodProxy proxy) {
            String name = method.getName();
            boolean z = -1;
            switch (name.hashCode()) {
                case -1801653451:
                    if (name.equals("getArgumentValues")) {
                        z = 2;
                        break;
                    }
                    break;
                case -1514708205:
                    if (name.equals("getControllerMethod")) {
                        z = true;
                        break;
                    }
                    break;
                case 1634403820:
                    if (name.equals("getControllerType")) {
                        z = false;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                    return this.controllerType;
                case true:
                    return this.controllerMethod;
                case true:
                    return this.argumentValues;
                default:
                    if (ReflectionUtils.isObjectMethod(method)) {
                        return ReflectionUtils.invokeMethod(method, obj, args);
                    }
                    this.controllerMethod = method;
                    this.argumentValues = args;
                    Class<?> returnType = method.getReturnType();
                    try {
                        if (returnType == Void.TYPE) {
                            return null;
                        }
                        return returnType.cast(initProxy(returnType, this));
                    } catch (Throwable ex) {
                        throw new IllegalStateException("Failed to create proxy for controller method return type: " + method, ex);
                    }
            }
        }

        @Override // java.lang.reflect.InvocationHandler
        @Nullable
        public Object invoke(Object proxy, Method method, @Nullable Object[] args) {
            return intercept(proxy, method, args != null ? args : new Object[0], null);
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.MethodInvocationInfo
        public Class<?> getControllerType() {
            return this.controllerType;
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.MethodInvocationInfo
        public Method getControllerMethod() {
            Assert.state(this.controllerMethod != null, "Not initialized yet");
            return this.controllerMethod;
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.MethodInvocationInfo
        public Object[] getArgumentValues() {
            Assert.state(this.argumentValues != null, "Not initialized yet");
            return this.argumentValues;
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r7v1, types: [T, java.lang.reflect.InvocationHandler] */
        private static <T> T initProxy(Class<?> cls, @Nullable ControllerMethodInvocationInterceptor controllerMethodInvocationInterceptor) {
            ?? r7 = (T) (controllerMethodInvocationInterceptor != null ? controllerMethodInvocationInterceptor : new ControllerMethodInvocationInterceptor(cls));
            if (cls == Object.class) {
                return r7;
            }
            if (cls.isInterface()) {
                ClassLoader classLoader = cls.getClassLoader();
                if (classLoader == null) {
                    classLoader = MethodInvocationInfo.class.getClassLoader();
                } else if (classLoader.getParent() == null) {
                    ClassLoader classLoader2 = MethodInvocationInfo.class.getClassLoader();
                    ClassLoader parent = classLoader2.getParent();
                    while (true) {
                        ClassLoader classLoader3 = parent;
                        if (classLoader3 == null) {
                            break;
                        }
                        if (classLoader == classLoader3) {
                            classLoader = classLoader2;
                            break;
                        }
                        parent = classLoader3.getParent();
                    }
                }
                return (T) Proxy.newProxyInstance(classLoader, new Class[]{cls, MethodInvocationInfo.class}, r7);
            }
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(cls);
            enhancer.setInterfaces(new Class[]{MethodInvocationInfo.class});
            enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
            enhancer.setAttemptLoad(true);
            enhancer.setCallbackType(MethodInterceptor.class);
            Class<T> createClass = enhancer.createClass();
            Object obj = null;
            if (MvcUriComponentsBuilder.objenesis.isWorthTrying()) {
                try {
                    obj = MvcUriComponentsBuilder.objenesis.newInstance(createClass, enhancer.getUseCache());
                } catch (ObjenesisException e) {
                    MvcUriComponentsBuilder.logger.debug("Failed to create controller proxy, falling back on default constructor", e);
                }
            }
            if (obj == null) {
                try {
                    obj = ReflectionUtils.accessibleConstructor(createClass, new Class[0]).newInstance(new Object[0]);
                } catch (Throwable th) {
                    throw new IllegalStateException("Failed to create controller proxy or use default constructor", th);
                }
            }
            ((Factory) obj).setCallbacks(new Callback[]{r7});
            return (T) obj;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/MvcUriComponentsBuilder$MethodArgumentBuilder.class */
    public static class MethodArgumentBuilder {
        private final Class<?> controllerType;
        private final Method method;
        private final Object[] argumentValues;
        private final UriComponentsBuilder baseUrl;

        public MethodArgumentBuilder(Class<?> controllerType, Method method) {
            this(null, controllerType, method);
        }

        public MethodArgumentBuilder(@Nullable UriComponentsBuilder baseUrl, Class<?> controllerType, Method method) {
            Assert.notNull(controllerType, "'controllerType' is required");
            Assert.notNull(method, "'method' is required");
            this.baseUrl = baseUrl != null ? baseUrl : UriComponentsBuilder.fromPath(getPath());
            this.controllerType = controllerType;
            this.method = method;
            this.argumentValues = new Object[method.getParameterCount()];
        }

        private static String getPath() {
            UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentServletMapping();
            String path = builder.build().getPath();
            return path != null ? path : "";
        }

        public MethodArgumentBuilder arg(int index, Object value) {
            this.argumentValues[index] = value;
            return this;
        }

        public MethodArgumentBuilder encode() {
            this.baseUrl.encode();
            return this;
        }

        public String build() {
            return MvcUriComponentsBuilder.fromMethodInternal(this.baseUrl, this.controllerType, this.method, this.argumentValues).build().encode().toUriString();
        }

        public String buildAndExpand(Object... uriVars) {
            return MvcUriComponentsBuilder.fromMethodInternal(this.baseUrl, this.controllerType, this.method, this.argumentValues).buildAndExpand(uriVars).encode().toString();
        }
    }
}
