package org.springframework.web.servlet.mvc.method.annotation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.method.MethodValidator;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.bind.support.DefaultSessionAttributeStore;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.AsyncWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ErrorsMethodArgumentResolver;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.HandlerMethodValidator;
import org.springframework.web.method.annotation.InitBinderDataBinderFactory;
import org.springframework.web.method.annotation.MapMethodProcessor;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.annotation.ModelFactory;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.annotation.RequestHeaderMapMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.annotation.SessionAttributesHandler;
import org.springframework.web.method.annotation.SessionStatusMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver;
import org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerAdapter.class */
public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter implements BeanFactoryAware, InitializingBean {
    public static final ReflectionUtils.MethodFilter INIT_BINDER_METHODS = method -> {
        return AnnotatedElementUtils.hasAnnotation(method, InitBinder.class);
    };
    public static final ReflectionUtils.MethodFilter MODEL_ATTRIBUTE_METHODS = method -> {
        return !AnnotatedElementUtils.hasAnnotation(method, RequestMapping.class) && AnnotatedElementUtils.hasAnnotation(method, ModelAttribute.class);
    };
    private static final boolean BEAN_VALIDATION_PRESENT = ClassUtils.isPresent("jakarta.validation.Validator", HandlerMethod.class.getClassLoader());

    @Nullable
    private List<HandlerMethodArgumentResolver> customArgumentResolvers;

    @Nullable
    private HandlerMethodArgumentResolverComposite argumentResolvers;

    @Nullable
    private HandlerMethodArgumentResolverComposite initBinderArgumentResolvers;

    @Nullable
    private List<HandlerMethodReturnValueHandler> customReturnValueHandlers;

    @Nullable
    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

    @Nullable
    private List<ModelAndViewResolver> modelAndViewResolvers;

    @Nullable
    private WebBindingInitializer webBindingInitializer;

    @Nullable
    private MethodValidator methodValidator;

    @Nullable
    private Long asyncRequestTimeout;

    @Nullable
    private ConfigurableBeanFactory beanFactory;
    private ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();
    private final List<HttpMessageConverter<?>> messageConverters = new ArrayList();
    private final List<Object> requestResponseBodyAdvice = new ArrayList();
    private AsyncTaskExecutor taskExecutor = new MvcSimpleAsyncTaskExecutor();
    private CallableProcessingInterceptor[] callableInterceptors = new CallableProcessingInterceptor[0];
    private DeferredResultProcessingInterceptor[] deferredResultInterceptors = new DeferredResultProcessingInterceptor[0];
    private ReactiveAdapterRegistry reactiveAdapterRegistry = ReactiveAdapterRegistry.getSharedInstance();
    private boolean ignoreDefaultModelOnRedirect = true;
    private int cacheSecondsForSessionAttributeHandlers = 0;
    private boolean synchronizeOnSession = false;
    private SessionAttributeStore sessionAttributeStore = new DefaultSessionAttributeStore();
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private final Map<Class<?>, SessionAttributesHandler> sessionAttributesHandlerCache = new ConcurrentHashMap(64);
    private final Map<Class<?>, Set<Method>> initBinderCache = new ConcurrentHashMap(64);
    private final Map<ControllerAdviceBean, Set<Method>> initBinderAdviceCache = new LinkedHashMap();
    private final Map<Class<?>, Set<Method>> modelAttributeCache = new ConcurrentHashMap(64);
    private final Map<ControllerAdviceBean, Set<Method>> modelAttributeAdviceCache = new LinkedHashMap();

    public void setCustomArgumentResolvers(@Nullable List<HandlerMethodArgumentResolver> argumentResolvers) {
        this.customArgumentResolvers = argumentResolvers;
    }

    @Nullable
    public List<HandlerMethodArgumentResolver> getCustomArgumentResolvers() {
        return this.customArgumentResolvers;
    }

    public void setArgumentResolvers(@Nullable List<HandlerMethodArgumentResolver> argumentResolvers) {
        if (argumentResolvers == null) {
            this.argumentResolvers = null;
        } else {
            this.argumentResolvers = new HandlerMethodArgumentResolverComposite();
            this.argumentResolvers.addResolvers(argumentResolvers);
        }
    }

    @Nullable
    public List<HandlerMethodArgumentResolver> getArgumentResolvers() {
        if (this.argumentResolvers != null) {
            return this.argumentResolvers.getResolvers();
        }
        return null;
    }

    public void setInitBinderArgumentResolvers(@Nullable List<HandlerMethodArgumentResolver> argumentResolvers) {
        if (argumentResolvers == null) {
            this.initBinderArgumentResolvers = null;
        } else {
            this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite();
            this.initBinderArgumentResolvers.addResolvers(argumentResolvers);
        }
    }

    @Nullable
    public List<HandlerMethodArgumentResolver> getInitBinderArgumentResolvers() {
        if (this.initBinderArgumentResolvers != null) {
            return this.initBinderArgumentResolvers.getResolvers();
        }
        return null;
    }

    public void setCustomReturnValueHandlers(@Nullable List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        this.customReturnValueHandlers = returnValueHandlers;
    }

    @Nullable
    public List<HandlerMethodReturnValueHandler> getCustomReturnValueHandlers() {
        return this.customReturnValueHandlers;
    }

    public void setReturnValueHandlers(@Nullable List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        if (returnValueHandlers == null) {
            this.returnValueHandlers = null;
        } else {
            this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();
            this.returnValueHandlers.addHandlers(returnValueHandlers);
        }
    }

    @Nullable
    public List<HandlerMethodReturnValueHandler> getReturnValueHandlers() {
        if (this.returnValueHandlers != null) {
            return this.returnValueHandlers.getHandlers();
        }
        return null;
    }

    public void setModelAndViewResolvers(@Nullable List<ModelAndViewResolver> modelAndViewResolvers) {
        this.modelAndViewResolvers = modelAndViewResolvers;
    }

    @Nullable
    public List<ModelAndViewResolver> getModelAndViewResolvers() {
        return this.modelAndViewResolvers;
    }

    public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
        this.contentNegotiationManager = contentNegotiationManager;
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters.clear();
        this.messageConverters.addAll(messageConverters);
    }

    public List<HttpMessageConverter<?>> getMessageConverters() {
        return this.messageConverters;
    }

    public void setRequestBodyAdvice(@Nullable List<RequestBodyAdvice> requestBodyAdvice) {
        if (requestBodyAdvice != null) {
            this.requestResponseBodyAdvice.addAll(requestBodyAdvice);
        }
    }

    public void setResponseBodyAdvice(@Nullable List<ResponseBodyAdvice<?>> responseBodyAdvice) {
        if (responseBodyAdvice != null) {
            this.requestResponseBodyAdvice.addAll(responseBodyAdvice);
        }
    }

    public void setWebBindingInitializer(@Nullable WebBindingInitializer webBindingInitializer) {
        this.webBindingInitializer = webBindingInitializer;
    }

    @Nullable
    public WebBindingInitializer getWebBindingInitializer() {
        return this.webBindingInitializer;
    }

    public void setTaskExecutor(AsyncTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void setAsyncRequestTimeout(long timeout) {
        this.asyncRequestTimeout = Long.valueOf(timeout);
    }

    public void setCallableInterceptors(List<CallableProcessingInterceptor> interceptors) {
        this.callableInterceptors = (CallableProcessingInterceptor[]) interceptors.toArray(new CallableProcessingInterceptor[0]);
    }

    public void setDeferredResultInterceptors(List<DeferredResultProcessingInterceptor> interceptors) {
        this.deferredResultInterceptors = (DeferredResultProcessingInterceptor[]) interceptors.toArray(new DeferredResultProcessingInterceptor[0]);
    }

    public void setReactiveAdapterRegistry(ReactiveAdapterRegistry reactiveAdapterRegistry) {
        this.reactiveAdapterRegistry = reactiveAdapterRegistry;
    }

    public ReactiveAdapterRegistry getReactiveAdapterRegistry() {
        return this.reactiveAdapterRegistry;
    }

    @Deprecated(since = "6.0")
    public void setIgnoreDefaultModelOnRedirect(boolean ignoreDefaultModelOnRedirect) {
        this.ignoreDefaultModelOnRedirect = ignoreDefaultModelOnRedirect;
    }

    public void setSessionAttributeStore(SessionAttributeStore sessionAttributeStore) {
        this.sessionAttributeStore = sessionAttributeStore;
    }

    public void setCacheSecondsForSessionAttributeHandlers(int cacheSecondsForSessionAttributeHandlers) {
        this.cacheSecondsForSessionAttributeHandlers = cacheSecondsForSessionAttributeHandlers;
    }

    public void setSynchronizeOnSession(boolean synchronizeOnSession) {
        this.synchronizeOnSession = synchronizeOnSession;
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
            this.beanFactory = cbf;
        }
    }

    @Nullable
    protected ConfigurableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        initControllerAdviceCache();
        initMessageConverters();
        if (this.argumentResolvers == null) {
            this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(getDefaultArgumentResolvers());
        }
        if (this.initBinderArgumentResolvers == null) {
            this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(getDefaultInitBinderArgumentResolvers());
        }
        if (this.returnValueHandlers == null) {
            List<HandlerMethodReturnValueHandler> handlers = getDefaultReturnValueHandlers();
            this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
        }
        if (BEAN_VALIDATION_PRESENT) {
            List<HandlerMethodArgumentResolver> resolvers = this.argumentResolvers.getResolvers();
            this.methodValidator = HandlerMethodValidator.from(this.webBindingInitializer, this.parameterNameDiscoverer, methodParamPredicate(resolvers, ModelAttributeMethodProcessor.class), methodParamPredicate(resolvers, RequestParamMethodArgumentResolver.class));
        }
    }

    private void initMessageConverters() {
        if (!this.messageConverters.isEmpty()) {
            return;
        }
        this.messageConverters.add(new ByteArrayHttpMessageConverter());
        this.messageConverters.add(new StringHttpMessageConverter());
        this.messageConverters.add(new AllEncompassingFormHttpMessageConverter());
    }

    private void initControllerAdviceCache() {
        if (getApplicationContext() == null) {
            return;
        }
        List<ControllerAdviceBean> adviceBeans = ControllerAdviceBean.findAnnotatedBeans(getApplicationContext());
        List<Object> requestResponseBodyAdviceBeans = new ArrayList<>();
        for (ControllerAdviceBean adviceBean : adviceBeans) {
            Class<?> beanType = adviceBean.getBeanType();
            if (beanType == null) {
                throw new IllegalStateException("Unresolvable type for ControllerAdviceBean: " + adviceBean);
            }
            Set<Method> attrMethods = MethodIntrospector.selectMethods(beanType, MODEL_ATTRIBUTE_METHODS);
            if (!attrMethods.isEmpty()) {
                this.modelAttributeAdviceCache.put(adviceBean, attrMethods);
            }
            Set<Method> binderMethods = MethodIntrospector.selectMethods(beanType, INIT_BINDER_METHODS);
            if (!binderMethods.isEmpty()) {
                this.initBinderAdviceCache.put(adviceBean, binderMethods);
            }
            if (RequestBodyAdvice.class.isAssignableFrom(beanType) || ResponseBodyAdvice.class.isAssignableFrom(beanType)) {
                requestResponseBodyAdviceBeans.add(adviceBean);
            }
        }
        if (!requestResponseBodyAdviceBeans.isEmpty()) {
            this.requestResponseBodyAdvice.addAll(0, requestResponseBodyAdviceBeans);
        }
        if (this.logger.isDebugEnabled()) {
            int modelSize = this.modelAttributeAdviceCache.size();
            int binderSize = this.initBinderAdviceCache.size();
            int reqCount = getBodyAdviceCount(RequestBodyAdvice.class);
            int resCount = getBodyAdviceCount(ResponseBodyAdvice.class);
            if (modelSize == 0 && binderSize == 0 && reqCount == 0 && resCount == 0) {
                this.logger.debug("ControllerAdvice beans: none");
            } else {
                this.logger.debug("ControllerAdvice beans: " + modelSize + " @ModelAttribute, " + binderSize + " @InitBinder, " + reqCount + " RequestBodyAdvice, " + resCount + " ResponseBodyAdvice");
            }
        }
    }

    private int getBodyAdviceCount(Class<?> adviceType) {
        List<Object> advice = this.requestResponseBodyAdvice;
        if (RequestBodyAdvice.class.isAssignableFrom(adviceType)) {
            return RequestResponseBodyAdviceChain.getAdviceByType(advice, RequestBodyAdvice.class).size();
        }
        return RequestResponseBodyAdviceChain.getAdviceByType(advice, ResponseBodyAdvice.class).size();
    }

    private List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>(30);
        resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), false));
        resolvers.add(new RequestParamMapMethodArgumentResolver());
        resolvers.add(new PathVariableMethodArgumentResolver());
        resolvers.add(new PathVariableMapMethodArgumentResolver());
        resolvers.add(new MatrixVariableMethodArgumentResolver());
        resolvers.add(new MatrixVariableMapMethodArgumentResolver());
        resolvers.add(new ServletModelAttributeMethodProcessor(false));
        resolvers.add(new RequestResponseBodyMethodProcessor(getMessageConverters(), this.requestResponseBodyAdvice));
        resolvers.add(new RequestPartMethodArgumentResolver(getMessageConverters(), this.requestResponseBodyAdvice));
        resolvers.add(new RequestHeaderMethodArgumentResolver(getBeanFactory()));
        resolvers.add(new RequestHeaderMapMethodArgumentResolver());
        resolvers.add(new ServletCookieValueMethodArgumentResolver(getBeanFactory()));
        resolvers.add(new ExpressionValueMethodArgumentResolver(getBeanFactory()));
        resolvers.add(new SessionAttributeMethodArgumentResolver());
        resolvers.add(new RequestAttributeMethodArgumentResolver());
        resolvers.add(new ServletRequestMethodArgumentResolver());
        resolvers.add(new ServletResponseMethodArgumentResolver());
        resolvers.add(new HttpEntityMethodProcessor(getMessageConverters(), this.requestResponseBodyAdvice));
        resolvers.add(new RedirectAttributesMethodArgumentResolver());
        resolvers.add(new ModelMethodProcessor());
        resolvers.add(new MapMethodProcessor());
        resolvers.add(new ErrorsMethodArgumentResolver());
        resolvers.add(new SessionStatusMethodArgumentResolver());
        resolvers.add(new UriComponentsBuilderMethodArgumentResolver());
        if (KotlinDetector.isKotlinPresent()) {
            resolvers.add(new ContinuationHandlerMethodArgumentResolver());
        }
        if (getCustomArgumentResolvers() != null) {
            resolvers.addAll(getCustomArgumentResolvers());
        }
        resolvers.add(new PrincipalMethodArgumentResolver());
        resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), true));
        resolvers.add(new ServletModelAttributeMethodProcessor(true));
        return resolvers;
    }

    private List<HandlerMethodArgumentResolver> getDefaultInitBinderArgumentResolvers() {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>(20);
        resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), false));
        resolvers.add(new RequestParamMapMethodArgumentResolver());
        resolvers.add(new PathVariableMethodArgumentResolver());
        resolvers.add(new PathVariableMapMethodArgumentResolver());
        resolvers.add(new MatrixVariableMethodArgumentResolver());
        resolvers.add(new MatrixVariableMapMethodArgumentResolver());
        resolvers.add(new ExpressionValueMethodArgumentResolver(getBeanFactory()));
        resolvers.add(new SessionAttributeMethodArgumentResolver());
        resolvers.add(new RequestAttributeMethodArgumentResolver());
        resolvers.add(new ServletRequestMethodArgumentResolver());
        resolvers.add(new ServletResponseMethodArgumentResolver());
        if (getCustomArgumentResolvers() != null) {
            resolvers.addAll(getCustomArgumentResolvers());
        }
        resolvers.add(new PrincipalMethodArgumentResolver());
        resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), true));
        return resolvers;
    }

    private List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>(20);
        handlers.add(new ModelAndViewMethodReturnValueHandler());
        handlers.add(new ModelMethodProcessor());
        handlers.add(new ViewMethodReturnValueHandler());
        handlers.add(new ResponseBodyEmitterReturnValueHandler(getMessageConverters(), this.reactiveAdapterRegistry, this.taskExecutor, this.contentNegotiationManager));
        handlers.add(new StreamingResponseBodyReturnValueHandler());
        handlers.add(new HttpEntityMethodProcessor(getMessageConverters(), this.contentNegotiationManager, this.requestResponseBodyAdvice));
        handlers.add(new HttpHeadersReturnValueHandler());
        handlers.add(new CallableMethodReturnValueHandler());
        handlers.add(new DeferredResultMethodReturnValueHandler());
        handlers.add(new AsyncTaskMethodReturnValueHandler(this.beanFactory));
        handlers.add(new ServletModelAttributeMethodProcessor(false));
        handlers.add(new RequestResponseBodyMethodProcessor(getMessageConverters(), this.contentNegotiationManager, this.requestResponseBodyAdvice));
        handlers.add(new ViewNameMethodReturnValueHandler());
        handlers.add(new MapMethodProcessor());
        if (getCustomReturnValueHandlers() != null) {
            handlers.addAll(getCustomReturnValueHandlers());
        }
        if (!CollectionUtils.isEmpty(getModelAndViewResolvers())) {
            handlers.add(new ModelAndViewResolverMethodReturnValueHandler(getModelAndViewResolvers()));
        } else {
            handlers.add(new ServletModelAttributeMethodProcessor(true));
        }
        return handlers;
    }

    private static Predicate<MethodParameter> methodParamPredicate(List<HandlerMethodArgumentResolver> resolvers, Class<?> resolverType) {
        return parameter -> {
            Iterator it = resolvers.iterator();
            while (it.hasNext()) {
                HandlerMethodArgumentResolver resolver = (HandlerMethodArgumentResolver) it.next();
                if (resolver.supportsParameter(parameter)) {
                    return resolverType.isInstance(resolver);
                }
            }
            return false;
        };
    }

    @Override // org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter
    protected boolean supportsInternal(HandlerMethod handlerMethod) {
        return true;
    }

    @Override // org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter
    protected ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        ModelAndView mav;
        checkRequest(request);
        if (this.synchronizeOnSession) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object mutex = WebUtils.getSessionMutex(session);
                synchronized (mutex) {
                    mav = invokeHandlerMethod(request, response, handlerMethod);
                }
            } else {
                mav = invokeHandlerMethod(request, response, handlerMethod);
            }
        } else {
            mav = invokeHandlerMethod(request, response, handlerMethod);
        }
        if (!response.containsHeader(HttpHeaders.CACHE_CONTROL)) {
            if (getSessionAttributesHandler(handlerMethod).hasSessionAttributes()) {
                applyCacheSeconds(response, this.cacheSecondsForSessionAttributeHandlers);
            } else {
                prepareResponse(response);
            }
        }
        return mav;
    }

    @Override // org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter
    protected long getLastModifiedInternal(HttpServletRequest request, HandlerMethod handlerMethod) {
        return -1L;
    }

    private SessionAttributesHandler getSessionAttributesHandler(HandlerMethod handlerMethod) {
        return this.sessionAttributesHandlerCache.computeIfAbsent(handlerMethod.getBeanType(), type -> {
            return new SessionAttributesHandler(type, this.sessionAttributeStore);
        });
    }

    @Nullable
    protected ModelAndView invokeHandlerMethod(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        WebDataBinderFactory binderFactory = getDataBinderFactory(handlerMethod);
        ModelFactory modelFactory = getModelFactory(handlerMethod, binderFactory);
        ServletInvocableHandlerMethod invocableMethod = createInvocableHandlerMethod(handlerMethod);
        if (this.argumentResolvers != null) {
            invocableMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
        }
        if (this.returnValueHandlers != null) {
            invocableMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
        }
        invocableMethod.setDataBinderFactory(binderFactory);
        invocableMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
        invocableMethod.setMethodValidator(this.methodValidator);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        mavContainer.addAllAttributes(RequestContextUtils.getInputFlashMap(request));
        modelFactory.initModel(webRequest, mavContainer, invocableMethod);
        mavContainer.setIgnoreDefaultModelOnRedirect(this.ignoreDefaultModelOnRedirect);
        AsyncWebRequest asyncWebRequest = WebAsyncUtils.createAsyncWebRequest(request, response);
        asyncWebRequest.setTimeout(this.asyncRequestTimeout);
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
        asyncManager.setTaskExecutor(this.taskExecutor);
        asyncManager.setAsyncWebRequest(asyncWebRequest);
        asyncManager.registerCallableInterceptors(this.callableInterceptors);
        asyncManager.registerDeferredResultInterceptors(this.deferredResultInterceptors);
        if (asyncManager.hasConcurrentResult()) {
            Object result = asyncManager.getConcurrentResult();
            mavContainer = (ModelAndViewContainer) asyncManager.getConcurrentResultContext()[0];
            asyncManager.clearConcurrentResult();
            LogFormatUtils.traceDebug(this.logger, traceOn -> {
                String formatted = LogFormatUtils.formatValue(result, !traceOn.booleanValue());
                return "Resume with async result [" + formatted + "]";
            });
            invocableMethod = invocableMethod.wrapConcurrentResult(result);
        }
        invocableMethod.invokeAndHandle(webRequest, mavContainer, new Object[0]);
        if (asyncManager.isConcurrentHandlingStarted()) {
            return null;
        }
        return getModelAndView(mavContainer, modelFactory, webRequest);
    }

    protected ServletInvocableHandlerMethod createInvocableHandlerMethod(HandlerMethod handlerMethod) {
        return new ServletInvocableHandlerMethod(handlerMethod);
    }

    private ModelFactory getModelFactory(HandlerMethod handlerMethod, WebDataBinderFactory binderFactory) {
        SessionAttributesHandler sessionAttrHandler = getSessionAttributesHandler(handlerMethod);
        Class<?> handlerType = handlerMethod.getBeanType();
        Set<Method> methods = this.modelAttributeCache.get(handlerType);
        if (methods == null) {
            methods = MethodIntrospector.selectMethods(handlerType, MODEL_ATTRIBUTE_METHODS);
            this.modelAttributeCache.put(handlerType, methods);
        }
        List<InvocableHandlerMethod> attrMethods = new ArrayList<>();
        this.modelAttributeAdviceCache.forEach((controllerAdviceBean, methodSet) -> {
            if (controllerAdviceBean.isApplicableToBeanType(handlerType)) {
                Object bean = controllerAdviceBean.resolveBean();
                Iterator it = methodSet.iterator();
                while (it.hasNext()) {
                    Method method = (Method) it.next();
                    attrMethods.add(createModelAttributeMethod(binderFactory, bean, method));
                }
            }
        });
        for (Method method : methods) {
            Object bean = handlerMethod.getBean();
            attrMethods.add(createModelAttributeMethod(binderFactory, bean, method));
        }
        return new ModelFactory(attrMethods, binderFactory, sessionAttrHandler);
    }

    private InvocableHandlerMethod createModelAttributeMethod(WebDataBinderFactory factory, Object bean, Method method) {
        InvocableHandlerMethod attrMethod = new InvocableHandlerMethod(bean, method);
        if (this.argumentResolvers != null) {
            attrMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
        }
        attrMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
        attrMethod.setDataBinderFactory(factory);
        return attrMethod;
    }

    private WebDataBinderFactory getDataBinderFactory(HandlerMethod handlerMethod) throws Exception {
        Class<?> handlerType = handlerMethod.getBeanType();
        Set<Method> methods = this.initBinderCache.get(handlerType);
        if (methods == null) {
            methods = MethodIntrospector.selectMethods(handlerType, INIT_BINDER_METHODS);
            this.initBinderCache.put(handlerType, methods);
        }
        List<InvocableHandlerMethod> initBinderMethods = new ArrayList<>();
        this.initBinderAdviceCache.forEach((controllerAdviceBean, methodSet) -> {
            if (controllerAdviceBean.isApplicableToBeanType(handlerType)) {
                Object bean = controllerAdviceBean.resolveBean();
                Iterator it = methodSet.iterator();
                while (it.hasNext()) {
                    Method method = (Method) it.next();
                    initBinderMethods.add(createInitBinderMethod(bean, method));
                }
            }
        });
        for (Method method : methods) {
            Object bean = handlerMethod.getBean();
            initBinderMethods.add(createInitBinderMethod(bean, method));
        }
        DefaultDataBinderFactory factory = createDataBinderFactory(initBinderMethods);
        factory.setMethodValidationApplicable(this.methodValidator != null && handlerMethod.shouldValidateArguments());
        return factory;
    }

    private InvocableHandlerMethod createInitBinderMethod(Object bean, Method method) {
        InvocableHandlerMethod binderMethod = new InvocableHandlerMethod(bean, method);
        if (this.initBinderArgumentResolvers != null) {
            binderMethod.setHandlerMethodArgumentResolvers(this.initBinderArgumentResolvers);
        }
        binderMethod.setDataBinderFactory(new DefaultDataBinderFactory(this.webBindingInitializer));
        binderMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
        return binderMethod;
    }

    protected InitBinderDataBinderFactory createDataBinderFactory(List<InvocableHandlerMethod> binderMethods) throws Exception {
        return new ServletRequestDataBinderFactory(binderMethods, getWebBindingInitializer());
    }

    @Nullable
    private ModelAndView getModelAndView(ModelAndViewContainer mavContainer, ModelFactory modelFactory, NativeWebRequest webRequest) throws Exception {
        modelFactory.updateModel(webRequest, mavContainer);
        if (mavContainer.isRequestHandled()) {
            return null;
        }
        Map model = mavContainer.getModel();
        ModelAndView mav = new ModelAndView(mavContainer.getViewName(), (Map<String, ?>) model, mavContainer.getStatus());
        if (!mavContainer.isViewReference()) {
            mav.setView((View) mavContainer.getView());
        }
        if (model instanceof RedirectAttributes) {
            RedirectAttributes redirectAttributes = (RedirectAttributes) model;
            Map<String, ?> flashAttributes = redirectAttributes.getFlashAttributes();
            HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
            if (request != null) {
                RequestContextUtils.getOutputFlashMap(request).putAll(flashAttributes);
            }
        }
        return mav;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerAdapter$MvcSimpleAsyncTaskExecutor.class */
    private class MvcSimpleAsyncTaskExecutor extends SimpleAsyncTaskExecutor {
        private static boolean taskExecutorWarning = true;

        MvcSimpleAsyncTaskExecutor() {
            super("MvcAsync");
        }

        @Override // org.springframework.core.task.SimpleAsyncTaskExecutor, org.springframework.core.task.TaskExecutor, java.util.concurrent.Executor
        public void execute(Runnable task) {
            if (taskExecutorWarning && RequestMappingHandlerAdapter.this.logger.isWarnEnabled()) {
                synchronized (this) {
                    if (taskExecutorWarning) {
                        RequestMappingHandlerAdapter.this.logger.warn("!!!\nPerforming asynchronous handling through the default Spring MVC SimpleAsyncTaskExecutor.\nThis executor is not suitable for production use under load.\nPlease, configure an AsyncTaskExecutor through the WebMvc config.\n-------------------------------\n!!!");
                        taskExecutorWarning = false;
                    }
                }
            }
            super.execute(task);
        }
    }
}
