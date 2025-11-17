package org.springframework.web.servlet.config;

import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import java.util.List;
import java.util.Properties;
import org.apache.naming.factory.Constants;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.smile.MappingJackson2SmileHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.method.support.CompositeUriComponentsContributor;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.ConversionServiceExposingInterceptor;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewRequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ServletWebArgumentResolverAdapter;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.w3c.dom.Element;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/AnnotationDrivenBeanDefinitionParser.class */
class AnnotationDrivenBeanDefinitionParser implements BeanDefinitionParser {
    public static final String HANDLER_MAPPING_BEAN_NAME = RequestMappingHandlerMapping.class.getName();
    public static final String HANDLER_ADAPTER_BEAN_NAME = RequestMappingHandlerAdapter.class.getName();
    public static final String CONTENT_NEGOTIATION_MANAGER_BEAN_NAME = "mvcContentNegotiationManager";
    private static final boolean javaxValidationPresent;
    private static final boolean romePresent;
    private static final boolean jaxb2Present;
    private static final boolean jackson2Present;
    private static final boolean jackson2XmlPresent;
    private static final boolean jackson2SmilePresent;
    private static final boolean jackson2CborPresent;
    private static final boolean gsonPresent;

    static {
        ClassLoader classLoader = AnnotationDrivenBeanDefinitionParser.class.getClassLoader();
        javaxValidationPresent = ClassUtils.isPresent("jakarta.validation.Validator", classLoader);
        romePresent = ClassUtils.isPresent("com.rometools.rome.feed.WireFeed", classLoader);
        jaxb2Present = ClassUtils.isPresent("jakarta.xml.bind.Binder", classLoader);
        jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader);
        jackson2XmlPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper", classLoader);
        jackson2SmilePresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.smile.SmileFactory", classLoader);
        jackson2CborPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.cbor.CBORFactory", classLoader);
        gsonPresent = ClassUtils.isPresent("com.google.gson.Gson", classLoader);
    }

    @Override // org.springframework.beans.factory.xml.BeanDefinitionParser
    @Nullable
    public BeanDefinition parse(Element element, ParserContext context) {
        Object source = context.extractSource(element);
        XmlReaderContext readerContext = context.getReaderContext();
        CompositeComponentDefinition compDefinition = new CompositeComponentDefinition(element.getTagName(), source);
        context.pushContainingComponent(compDefinition);
        RuntimeBeanReference contentNegotiationManager = getContentNegotiationManager(element, source, context);
        RootBeanDefinition handlerMappingDef = new RootBeanDefinition((Class<?>) RequestMappingHandlerMapping.class);
        handlerMappingDef.setSource(source);
        handlerMappingDef.setRole(2);
        handlerMappingDef.getPropertyValues().add(AbstractBeanDefinition.ORDER_ATTRIBUTE, 0);
        handlerMappingDef.getPropertyValues().add("contentNegotiationManager", contentNegotiationManager);
        if (element.hasAttribute("enable-matrix-variables")) {
            boolean enableMatrixVariables = Boolean.parseBoolean(element.getAttribute("enable-matrix-variables"));
            handlerMappingDef.getPropertyValues().add("removeSemicolonContent", Boolean.valueOf(!enableMatrixVariables));
        }
        configurePathMatchingProperties(handlerMappingDef, element, context);
        readerContext.getRegistry().registerBeanDefinition(HANDLER_MAPPING_BEAN_NAME, handlerMappingDef);
        RuntimeBeanReference corsRef = MvcNamespaceUtils.registerCorsConfigurations(null, context, source);
        handlerMappingDef.getPropertyValues().add("corsConfigurations", corsRef);
        RuntimeBeanReference conversionService = getConversionService(element, source, context);
        RuntimeBeanReference validator = getValidator(element, source, context);
        RuntimeBeanReference messageCodesResolver = getMessageCodesResolver(element);
        RootBeanDefinition bindingDef = new RootBeanDefinition((Class<?>) ConfigurableWebBindingInitializer.class);
        bindingDef.setSource(source);
        bindingDef.setRole(2);
        bindingDef.getPropertyValues().add(ConfigurableApplicationContext.CONVERSION_SERVICE_BEAN_NAME, conversionService);
        bindingDef.getPropertyValues().add("validator", validator);
        bindingDef.getPropertyValues().add("messageCodesResolver", messageCodesResolver);
        ManagedList<?> messageConverters = getMessageConverters(element, source, context);
        ManagedList<?> argumentResolvers = getArgumentResolvers(element, context);
        ManagedList<?> returnValueHandlers = getReturnValueHandlers(element, context);
        String asyncTimeout = getAsyncTimeout(element);
        RuntimeBeanReference asyncExecutor = getAsyncExecutor(element);
        ManagedList<?> callableInterceptors = getInterceptors(element, source, context, "callable-interceptors");
        ManagedList<?> deferredResultInterceptors = getInterceptors(element, source, context, "deferred-result-interceptors");
        RootBeanDefinition handlerAdapterDef = new RootBeanDefinition((Class<?>) RequestMappingHandlerAdapter.class);
        handlerAdapterDef.setSource(source);
        handlerAdapterDef.setRole(2);
        handlerAdapterDef.getPropertyValues().add("contentNegotiationManager", contentNegotiationManager);
        handlerAdapterDef.getPropertyValues().add("webBindingInitializer", bindingDef);
        handlerAdapterDef.getPropertyValues().add("messageConverters", messageConverters);
        addRequestBodyAdvice(handlerAdapterDef);
        addResponseBodyAdvice(handlerAdapterDef);
        if (element.hasAttribute("ignore-default-model-on-redirect")) {
            Boolean ignoreDefaultModel = Boolean.valueOf(element.getAttribute("ignore-default-model-on-redirect"));
            handlerAdapterDef.getPropertyValues().add("ignoreDefaultModelOnRedirect", ignoreDefaultModel);
        }
        if (argumentResolvers != null) {
            handlerAdapterDef.getPropertyValues().add("customArgumentResolvers", argumentResolvers);
        }
        if (returnValueHandlers != null) {
            handlerAdapterDef.getPropertyValues().add("customReturnValueHandlers", returnValueHandlers);
        }
        if (asyncTimeout != null) {
            handlerAdapterDef.getPropertyValues().add("asyncRequestTimeout", asyncTimeout);
        }
        if (asyncExecutor != null) {
            handlerAdapterDef.getPropertyValues().add("taskExecutor", asyncExecutor);
        }
        handlerAdapterDef.getPropertyValues().add("callableInterceptors", callableInterceptors);
        handlerAdapterDef.getPropertyValues().add("deferredResultInterceptors", deferredResultInterceptors);
        readerContext.getRegistry().registerBeanDefinition(HANDLER_ADAPTER_BEAN_NAME, handlerAdapterDef);
        RootBeanDefinition uriContributorDef = new RootBeanDefinition((Class<?>) CompositeUriComponentsContributorFactoryBean.class);
        uriContributorDef.setSource(source);
        uriContributorDef.getPropertyValues().addPropertyValue(DispatcherServlet.HANDLER_ADAPTER_BEAN_NAME, handlerAdapterDef);
        uriContributorDef.getPropertyValues().addPropertyValue(ConfigurableApplicationContext.CONVERSION_SERVICE_BEAN_NAME, conversionService);
        readerContext.getRegistry().registerBeanDefinition(MvcUriComponentsBuilder.MVC_URI_COMPONENTS_CONTRIBUTOR_BEAN_NAME, uriContributorDef);
        RootBeanDefinition csInterceptorDef = new RootBeanDefinition((Class<?>) ConversionServiceExposingInterceptor.class);
        csInterceptorDef.setSource(source);
        csInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(0, conversionService);
        RootBeanDefinition mappedInterceptorDef = new RootBeanDefinition((Class<?>) MappedInterceptor.class);
        mappedInterceptorDef.setSource(source);
        mappedInterceptorDef.setRole(2);
        mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object) null);
        mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(1, csInterceptorDef);
        String mappedInterceptorName = readerContext.registerWithGeneratedName(mappedInterceptorDef);
        RootBeanDefinition methodExceptionResolver = new RootBeanDefinition((Class<?>) ExceptionHandlerExceptionResolver.class);
        methodExceptionResolver.setSource(source);
        methodExceptionResolver.setRole(2);
        methodExceptionResolver.getPropertyValues().add("contentNegotiationManager", contentNegotiationManager);
        methodExceptionResolver.getPropertyValues().add("messageConverters", messageConverters);
        methodExceptionResolver.getPropertyValues().add(AbstractBeanDefinition.ORDER_ATTRIBUTE, 0);
        addResponseBodyAdvice(methodExceptionResolver);
        if (argumentResolvers != null) {
            methodExceptionResolver.getPropertyValues().add("customArgumentResolvers", argumentResolvers);
        }
        if (returnValueHandlers != null) {
            methodExceptionResolver.getPropertyValues().add("customReturnValueHandlers", returnValueHandlers);
        }
        String methodExResolverName = readerContext.registerWithGeneratedName(methodExceptionResolver);
        RootBeanDefinition statusExceptionResolver = new RootBeanDefinition((Class<?>) ResponseStatusExceptionResolver.class);
        statusExceptionResolver.setSource(source);
        statusExceptionResolver.setRole(2);
        statusExceptionResolver.getPropertyValues().add(AbstractBeanDefinition.ORDER_ATTRIBUTE, 1);
        String statusExResolverName = readerContext.registerWithGeneratedName(statusExceptionResolver);
        RootBeanDefinition defaultExceptionResolver = new RootBeanDefinition((Class<?>) DefaultHandlerExceptionResolver.class);
        defaultExceptionResolver.setSource(source);
        defaultExceptionResolver.setRole(2);
        defaultExceptionResolver.getPropertyValues().add(AbstractBeanDefinition.ORDER_ATTRIBUTE, 2);
        String defaultExResolverName = readerContext.registerWithGeneratedName(defaultExceptionResolver);
        context.registerComponent(new BeanComponentDefinition(handlerMappingDef, HANDLER_MAPPING_BEAN_NAME));
        context.registerComponent(new BeanComponentDefinition(handlerAdapterDef, HANDLER_ADAPTER_BEAN_NAME));
        context.registerComponent(new BeanComponentDefinition(uriContributorDef, MvcUriComponentsBuilder.MVC_URI_COMPONENTS_CONTRIBUTOR_BEAN_NAME));
        context.registerComponent(new BeanComponentDefinition(mappedInterceptorDef, mappedInterceptorName));
        context.registerComponent(new BeanComponentDefinition(methodExceptionResolver, methodExResolverName));
        context.registerComponent(new BeanComponentDefinition(statusExceptionResolver, statusExResolverName));
        context.registerComponent(new BeanComponentDefinition(defaultExceptionResolver, defaultExResolverName));
        MvcNamespaceUtils.registerDefaultComponents(context, source);
        context.popAndRegisterContainingComponent();
        return null;
    }

    protected void addRequestBodyAdvice(RootBeanDefinition beanDef) {
        if (jackson2Present) {
            beanDef.getPropertyValues().add("requestBodyAdvice", new RootBeanDefinition((Class<?>) JsonViewRequestBodyAdvice.class));
        }
    }

    protected void addResponseBodyAdvice(RootBeanDefinition beanDef) {
        if (jackson2Present) {
            beanDef.getPropertyValues().add("responseBodyAdvice", new RootBeanDefinition((Class<?>) JsonViewResponseBodyAdvice.class));
        }
    }

    private RuntimeBeanReference getConversionService(Element element, @Nullable Object source, ParserContext context) {
        RuntimeBeanReference conversionServiceRef;
        if (element.hasAttribute("conversion-service")) {
            conversionServiceRef = new RuntimeBeanReference(element.getAttribute("conversion-service"));
        } else {
            RootBeanDefinition conversionDef = new RootBeanDefinition((Class<?>) FormattingConversionServiceFactoryBean.class);
            conversionDef.setSource(source);
            conversionDef.setRole(2);
            String conversionName = context.getReaderContext().registerWithGeneratedName(conversionDef);
            context.registerComponent(new BeanComponentDefinition(conversionDef, conversionName));
            conversionServiceRef = new RuntimeBeanReference(conversionName);
        }
        return conversionServiceRef;
    }

    @Nullable
    private RuntimeBeanReference getValidator(Element element, @Nullable Object source, ParserContext context) {
        if (element.hasAttribute("validator")) {
            return new RuntimeBeanReference(element.getAttribute("validator"));
        }
        if (javaxValidationPresent) {
            RootBeanDefinition validatorDef = new RootBeanDefinition("org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean");
            validatorDef.setSource(source);
            validatorDef.setRole(2);
            String validatorName = context.getReaderContext().registerWithGeneratedName(validatorDef);
            context.registerComponent(new BeanComponentDefinition(validatorDef, validatorName));
            return new RuntimeBeanReference(validatorName);
        }
        return null;
    }

    private RuntimeBeanReference getContentNegotiationManager(Element element, @Nullable Object source, ParserContext context) {
        RuntimeBeanReference beanRef;
        if (element.hasAttribute("content-negotiation-manager")) {
            String name = element.getAttribute("content-negotiation-manager");
            beanRef = new RuntimeBeanReference(name);
        } else {
            RootBeanDefinition factoryBeanDef = new RootBeanDefinition((Class<?>) ContentNegotiationManagerFactoryBean.class);
            factoryBeanDef.setSource(source);
            factoryBeanDef.setRole(2);
            factoryBeanDef.getPropertyValues().add("mediaTypes", getDefaultMediaTypes());
            context.getReaderContext().getRegistry().registerBeanDefinition(CONTENT_NEGOTIATION_MANAGER_BEAN_NAME, factoryBeanDef);
            context.registerComponent(new BeanComponentDefinition(factoryBeanDef, CONTENT_NEGOTIATION_MANAGER_BEAN_NAME));
            beanRef = new RuntimeBeanReference(CONTENT_NEGOTIATION_MANAGER_BEAN_NAME);
        }
        return beanRef;
    }

    private void configurePathMatchingProperties(RootBeanDefinition handlerMappingDef, Element element, ParserContext context) {
        Element pathMatchingElement = DomUtils.getChildElementByTagName(element, "path-matching");
        if (pathMatchingElement != null) {
            Object source = context.extractSource(element);
            if (pathMatchingElement.hasAttribute("trailing-slash")) {
                boolean useTrailingSlashMatch = Boolean.parseBoolean(pathMatchingElement.getAttribute("trailing-slash"));
                handlerMappingDef.getPropertyValues().add("useTrailingSlashMatch", Boolean.valueOf(useTrailingSlashMatch));
            }
            boolean preferPathMatcher = false;
            if (pathMatchingElement.hasAttribute("suffix-pattern")) {
                boolean useSuffixPatternMatch = Boolean.parseBoolean(pathMatchingElement.getAttribute("suffix-pattern"));
                handlerMappingDef.getPropertyValues().add("useSuffixPatternMatch", Boolean.valueOf(useSuffixPatternMatch));
                preferPathMatcher = false | useSuffixPatternMatch;
            }
            if (pathMatchingElement.hasAttribute("registered-suffixes-only")) {
                boolean useRegisteredSuffixPatternMatch = Boolean.parseBoolean(pathMatchingElement.getAttribute("registered-suffixes-only"));
                handlerMappingDef.getPropertyValues().add("useRegisteredSuffixPatternMatch", Boolean.valueOf(useRegisteredSuffixPatternMatch));
                preferPathMatcher |= useRegisteredSuffixPatternMatch;
            }
            RuntimeBeanReference pathHelperRef = null;
            if (pathMatchingElement.hasAttribute("path-helper")) {
                pathHelperRef = new RuntimeBeanReference(pathMatchingElement.getAttribute("path-helper"));
                preferPathMatcher = true;
            }
            handlerMappingDef.getPropertyValues().add("urlPathHelper", MvcNamespaceUtils.registerUrlPathHelper(pathHelperRef, context, source));
            RuntimeBeanReference pathMatcherRef = null;
            if (pathMatchingElement.hasAttribute("path-matcher")) {
                pathMatcherRef = new RuntimeBeanReference(pathMatchingElement.getAttribute("path-matcher"));
                preferPathMatcher = true;
            }
            handlerMappingDef.getPropertyValues().add("pathMatcher", MvcNamespaceUtils.registerPathMatcher(pathMatcherRef, context, source));
            if (preferPathMatcher) {
                handlerMappingDef.getPropertyValues().add("patternParser", null);
            }
        }
    }

    private Properties getDefaultMediaTypes() {
        Properties defaultMediaTypes = new Properties();
        if (romePresent) {
            defaultMediaTypes.put("atom", MediaType.APPLICATION_ATOM_XML_VALUE);
            defaultMediaTypes.put("rss", MediaType.APPLICATION_RSS_XML_VALUE);
        }
        if (jaxb2Present || jackson2XmlPresent) {
            defaultMediaTypes.put("xml", "application/xml");
        }
        if (jackson2Present || gsonPresent) {
            defaultMediaTypes.put("json", "application/json");
        }
        if (jackson2SmilePresent) {
            defaultMediaTypes.put("smile", "application/x-jackson-smile");
        }
        if (jackson2CborPresent) {
            defaultMediaTypes.put("cbor", MediaType.APPLICATION_CBOR_VALUE);
        }
        return defaultMediaTypes;
    }

    @Nullable
    private RuntimeBeanReference getMessageCodesResolver(Element element) {
        if (element.hasAttribute("message-codes-resolver")) {
            return new RuntimeBeanReference(element.getAttribute("message-codes-resolver"));
        }
        return null;
    }

    @Nullable
    private String getAsyncTimeout(Element element) {
        Element asyncElement = DomUtils.getChildElementByTagName(element, "async-support");
        if (asyncElement != null) {
            return asyncElement.getAttribute("default-timeout");
        }
        return null;
    }

    @Nullable
    private RuntimeBeanReference getAsyncExecutor(Element element) {
        Element asyncElement = DomUtils.getChildElementByTagName(element, "async-support");
        if (asyncElement != null && asyncElement.hasAttribute("task-executor")) {
            return new RuntimeBeanReference(asyncElement.getAttribute("task-executor"));
        }
        return null;
    }

    private ManagedList<?> getInterceptors(Element element, @Nullable Object source, ParserContext context, String interceptorElementName) {
        Element interceptorsElement;
        ManagedList<Object> interceptors = new ManagedList<>();
        Element asyncElement = DomUtils.getChildElementByTagName(element, "async-support");
        if (asyncElement != null && (interceptorsElement = DomUtils.getChildElementByTagName(asyncElement, interceptorElementName)) != null) {
            interceptors.setSource(source);
            for (Element converter : DomUtils.getChildElementsByTagName(interceptorsElement, "bean")) {
                BeanDefinitionHolder beanDef = context.getDelegate().parseBeanDefinitionElement(converter);
                if (beanDef != null) {
                    interceptors.add(context.getDelegate().decorateBeanDefinitionIfRequired(converter, beanDef));
                }
            }
        }
        return interceptors;
    }

    @Nullable
    private ManagedList<?> getArgumentResolvers(Element element, ParserContext context) {
        Element resolversElement = DomUtils.getChildElementByTagName(element, "argument-resolvers");
        if (resolversElement != null) {
            ManagedList<Object> resolvers = extractBeanSubElements(resolversElement, context);
            return wrapLegacyResolvers(resolvers, context);
        }
        return null;
    }

    private ManagedList<Object> wrapLegacyResolvers(List<Object> list, ParserContext context) {
        ManagedList<Object> result = new ManagedList<>();
        for (Object object : list) {
            if (object instanceof BeanDefinitionHolder) {
                BeanDefinitionHolder beanDef = (BeanDefinitionHolder) object;
                String className = beanDef.getBeanDefinition().getBeanClassName();
                Assert.notNull(className, "No resolver class");
                Class<?> clazz = ClassUtils.resolveClassName(className, context.getReaderContext().getBeanClassLoader());
                if (WebArgumentResolver.class.isAssignableFrom(clazz)) {
                    RootBeanDefinition adapter = new RootBeanDefinition((Class<?>) ServletWebArgumentResolverAdapter.class);
                    adapter.getConstructorArgumentValues().addIndexedArgumentValue(0, beanDef);
                    result.add(new BeanDefinitionHolder(adapter, beanDef.getBeanName() + "Adapter"));
                }
            }
            result.add(object);
        }
        return result;
    }

    @Nullable
    private ManagedList<?> getReturnValueHandlers(Element element, ParserContext context) {
        Element handlers = DomUtils.getChildElementByTagName(element, "return-value-handlers");
        if (handlers != null) {
            return extractBeanSubElements(handlers, context);
        }
        return null;
    }

    private ManagedList<?> getMessageConverters(Element element, @Nullable Object source, ParserContext context) {
        Element convertersElement = DomUtils.getChildElementByTagName(element, "message-converters");
        ManagedList<Object> messageConverters = new ManagedList<>();
        if (convertersElement != null) {
            messageConverters.setSource(source);
            for (Element beanElement : DomUtils.getChildElementsByTagName(convertersElement, "bean", "ref")) {
                Object object = context.getDelegate().parsePropertySubElement(beanElement, null);
                messageConverters.add(object);
            }
        }
        if (convertersElement == null || Boolean.parseBoolean(convertersElement.getAttribute("register-defaults"))) {
            messageConverters.setSource(source);
            messageConverters.add(createConverterDefinition(ByteArrayHttpMessageConverter.class, source));
            RootBeanDefinition stringConverterDef = createConverterDefinition(StringHttpMessageConverter.class, source);
            stringConverterDef.getPropertyValues().add("writeAcceptCharset", false);
            messageConverters.add(stringConverterDef);
            messageConverters.add(createConverterDefinition(ResourceHttpMessageConverter.class, source));
            messageConverters.add(createConverterDefinition(ResourceRegionHttpMessageConverter.class, source));
            messageConverters.add(createConverterDefinition(AllEncompassingFormHttpMessageConverter.class, source));
            if (romePresent) {
                messageConverters.add(createConverterDefinition(AtomFeedHttpMessageConverter.class, source));
                messageConverters.add(createConverterDefinition(RssChannelHttpMessageConverter.class, source));
            }
            if (jackson2XmlPresent) {
                RootBeanDefinition jacksonConverterDef = createConverterDefinition(MappingJackson2XmlHttpMessageConverter.class, source);
                GenericBeanDefinition jacksonFactoryDef = createObjectMapperFactoryDefinition(source);
                jacksonFactoryDef.getPropertyValues().add("createXmlMapper", true);
                jacksonConverterDef.getConstructorArgumentValues().addIndexedArgumentValue(0, jacksonFactoryDef);
                messageConverters.add(jacksonConverterDef);
            } else if (jaxb2Present) {
                messageConverters.add(createConverterDefinition(Jaxb2RootElementHttpMessageConverter.class, source));
            }
            if (jackson2Present) {
                RootBeanDefinition jacksonConverterDef2 = createConverterDefinition(MappingJackson2HttpMessageConverter.class, source);
                jacksonConverterDef2.getConstructorArgumentValues().addIndexedArgumentValue(0, createObjectMapperFactoryDefinition(source));
                messageConverters.add(jacksonConverterDef2);
            } else if (gsonPresent) {
                messageConverters.add(createConverterDefinition(GsonHttpMessageConverter.class, source));
            }
            if (jackson2SmilePresent) {
                RootBeanDefinition jacksonConverterDef3 = createConverterDefinition(MappingJackson2SmileHttpMessageConverter.class, source);
                GenericBeanDefinition jacksonFactoryDef2 = createObjectMapperFactoryDefinition(source);
                jacksonFactoryDef2.getPropertyValues().add(Constants.FACTORY, new RootBeanDefinition((Class<?>) SmileFactory.class));
                jacksonConverterDef3.getConstructorArgumentValues().addIndexedArgumentValue(0, jacksonFactoryDef2);
                messageConverters.add(jacksonConverterDef3);
            }
            if (jackson2CborPresent) {
                RootBeanDefinition jacksonConverterDef4 = createConverterDefinition(MappingJackson2CborHttpMessageConverter.class, source);
                GenericBeanDefinition jacksonFactoryDef3 = createObjectMapperFactoryDefinition(source);
                jacksonFactoryDef3.getPropertyValues().add(Constants.FACTORY, new RootBeanDefinition((Class<?>) CBORFactory.class));
                jacksonConverterDef4.getConstructorArgumentValues().addIndexedArgumentValue(0, jacksonFactoryDef3);
                messageConverters.add(jacksonConverterDef4);
            }
        }
        return messageConverters;
    }

    private GenericBeanDefinition createObjectMapperFactoryDefinition(@Nullable Object source) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(Jackson2ObjectMapperFactoryBean.class);
        beanDefinition.setSource(source);
        beanDefinition.setRole(2);
        return beanDefinition;
    }

    private RootBeanDefinition createConverterDefinition(Class<?> converterClass, @Nullable Object source) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(converterClass);
        beanDefinition.setSource(source);
        beanDefinition.setRole(2);
        return beanDefinition;
    }

    private ManagedList<Object> extractBeanSubElements(Element parentElement, ParserContext context) {
        ManagedList<Object> list = new ManagedList<>();
        list.setSource(context.extractSource(parentElement));
        for (Element beanElement : DomUtils.getChildElementsByTagName(parentElement, "bean", "ref")) {
            Object object = context.getDelegate().parsePropertySubElement(beanElement, null);
            list.add(object);
        }
        return list;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/AnnotationDrivenBeanDefinitionParser$CompositeUriComponentsContributorFactoryBean.class */
    static class CompositeUriComponentsContributorFactoryBean implements FactoryBean<CompositeUriComponentsContributor>, InitializingBean {

        @Nullable
        private RequestMappingHandlerAdapter handlerAdapter;

        @Nullable
        private ConversionService conversionService;

        @Nullable
        private CompositeUriComponentsContributor uriComponentsContributor;

        CompositeUriComponentsContributorFactoryBean() {
        }

        public void setHandlerAdapter(RequestMappingHandlerAdapter handlerAdapter) {
            this.handlerAdapter = handlerAdapter;
        }

        public void setConversionService(ConversionService conversionService) {
            this.conversionService = conversionService;
        }

        @Override // org.springframework.beans.factory.InitializingBean
        public void afterPropertiesSet() {
            Assert.state(this.handlerAdapter != null, "No RequestMappingHandlerAdapter set");
            this.uriComponentsContributor = new CompositeUriComponentsContributor(this.handlerAdapter.getArgumentResolvers(), this.conversionService);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.beans.factory.FactoryBean
        @Nullable
        public CompositeUriComponentsContributor getObject() {
            return this.uriComponentsContributor;
        }

        @Override // org.springframework.beans.factory.FactoryBean
        public Class<?> getObjectType() {
            return CompositeUriComponentsContributor.class;
        }

        @Override // org.springframework.beans.factory.FactoryBean
        public boolean isSingleton() {
            return true;
        }
    }
}
