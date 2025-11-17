package org.springframework.web.servlet.config;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.http.CacheControl;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.CachingResourceResolver;
import org.springframework.web.servlet.resource.CachingResourceTransformer;
import org.springframework.web.servlet.resource.ContentVersionStrategy;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;
import org.springframework.web.servlet.resource.FixedVersionStrategy;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.resource.ResourceUrlProviderExposingInterceptor;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.resource.WebJarsResourceResolver;
import org.w3c.dom.Element;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/ResourcesBeanDefinitionParser.class */
class ResourcesBeanDefinitionParser implements BeanDefinitionParser {
    private static final String RESOURCE_CHAIN_CACHE = "spring-resource-chain-cache";
    private static final String VERSION_RESOLVER_ELEMENT = "version-resolver";
    private static final String VERSION_STRATEGY_ELEMENT = "version-strategy";
    private static final String FIXED_VERSION_STRATEGY_ELEMENT = "fixed-version-strategy";
    private static final String CONTENT_VERSION_STRATEGY_ELEMENT = "content-version-strategy";
    private static final String RESOURCE_URL_PROVIDER = "mvcResourceUrlProvider";
    private static final boolean webJarsPresent = ClassUtils.isPresent("org.webjars.WebJarAssetLocator", ResourcesBeanDefinitionParser.class.getClassLoader());

    @Override // org.springframework.beans.factory.xml.BeanDefinitionParser
    public BeanDefinition parse(Element element, ParserContext context) {
        Object source = context.extractSource(element);
        registerUrlProvider(context, source);
        RuntimeBeanReference pathMatcherRef = MvcNamespaceUtils.registerPathMatcher(null, context, source);
        RuntimeBeanReference pathHelperRef = MvcNamespaceUtils.registerUrlPathHelper(null, context, source);
        String resourceHandlerName = registerResourceHandler(context, element, pathHelperRef, source);
        if (resourceHandlerName == null) {
            return null;
        }
        Map<String, String> urlMap = new ManagedMap<>();
        String resourceRequestPath = element.getAttribute("mapping");
        if (!StringUtils.hasText(resourceRequestPath)) {
            context.getReaderContext().error("The 'mapping' attribute is required.", context.extractSource(element));
            return null;
        }
        urlMap.put(resourceRequestPath, resourceHandlerName);
        RootBeanDefinition handlerMappingDef = new RootBeanDefinition((Class<?>) SimpleUrlHandlerMapping.class);
        handlerMappingDef.setSource(source);
        handlerMappingDef.setRole(2);
        handlerMappingDef.getPropertyValues().add("urlMap", urlMap);
        handlerMappingDef.getPropertyValues().add("pathMatcher", pathMatcherRef).add("urlPathHelper", pathHelperRef);
        String orderValue = element.getAttribute(AbstractBeanDefinition.ORDER_ATTRIBUTE);
        Object order = StringUtils.hasText(orderValue) ? orderValue : 2147483646;
        handlerMappingDef.getPropertyValues().add(AbstractBeanDefinition.ORDER_ATTRIBUTE, order);
        RuntimeBeanReference corsRef = MvcNamespaceUtils.registerCorsConfigurations(null, context, source);
        handlerMappingDef.getPropertyValues().add("corsConfigurations", corsRef);
        String beanName = context.getReaderContext().generateBeanName(handlerMappingDef);
        context.getRegistry().registerBeanDefinition(beanName, handlerMappingDef);
        context.registerComponent(new BeanComponentDefinition(handlerMappingDef, beanName));
        MvcNamespaceUtils.registerDefaultComponents(context, source);
        return null;
    }

    private void registerUrlProvider(ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(RESOURCE_URL_PROVIDER)) {
            RootBeanDefinition urlProvider = new RootBeanDefinition((Class<?>) ResourceUrlProvider.class);
            urlProvider.setSource(source);
            urlProvider.setRole(2);
            context.getRegistry().registerBeanDefinition(RESOURCE_URL_PROVIDER, urlProvider);
            context.registerComponent(new BeanComponentDefinition(urlProvider, RESOURCE_URL_PROVIDER));
            RootBeanDefinition interceptor = new RootBeanDefinition((Class<?>) ResourceUrlProviderExposingInterceptor.class);
            interceptor.setSource(source);
            interceptor.getConstructorArgumentValues().addIndexedArgumentValue(0, urlProvider);
            RootBeanDefinition mappedInterceptor = new RootBeanDefinition((Class<?>) MappedInterceptor.class);
            mappedInterceptor.setSource(source);
            mappedInterceptor.setRole(2);
            mappedInterceptor.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object) null);
            mappedInterceptor.getConstructorArgumentValues().addIndexedArgumentValue(1, interceptor);
            String mappedInterceptorName = context.getReaderContext().registerWithGeneratedName(mappedInterceptor);
            context.registerComponent(new BeanComponentDefinition(mappedInterceptor, mappedInterceptorName));
        }
    }

    @Nullable
    private String registerResourceHandler(ParserContext context, Element element, RuntimeBeanReference pathHelperRef, @Nullable Object source) {
        String locationAttr = element.getAttribute("location");
        if (!StringUtils.hasText(locationAttr)) {
            context.getReaderContext().error("The 'location' attribute is required.", context.extractSource(element));
            return null;
        }
        RootBeanDefinition resourceHandlerDef = new RootBeanDefinition((Class<?>) ResourceHttpRequestHandler.class);
        resourceHandlerDef.setSource(source);
        resourceHandlerDef.setRole(2);
        MutablePropertyValues values = resourceHandlerDef.getPropertyValues();
        values.add("urlPathHelper", pathHelperRef);
        values.add("locationValues", StringUtils.commaDelimitedListToStringArray(locationAttr));
        String cacheSeconds = element.getAttribute("cache-period");
        if (StringUtils.hasText(cacheSeconds)) {
            values.add("cacheSeconds", cacheSeconds);
        }
        Element cacheControlElement = DomUtils.getChildElementByTagName(element, "cache-control");
        if (cacheControlElement != null) {
            CacheControl cacheControl = parseCacheControl(cacheControlElement);
            values.add("cacheControl", cacheControl);
        }
        Element resourceChainElement = DomUtils.getChildElementByTagName(element, "resource-chain");
        if (resourceChainElement != null) {
            parseResourceChain(resourceHandlerDef, context, resourceChainElement, source);
        }
        Object manager = MvcNamespaceUtils.getContentNegotiationManager(context);
        if (manager != null) {
            values.add("contentNegotiationManager", manager);
        }
        String beanName = context.getReaderContext().generateBeanName(resourceHandlerDef);
        context.getRegistry().registerBeanDefinition(beanName, resourceHandlerDef);
        context.registerComponent(new BeanComponentDefinition(resourceHandlerDef, beanName));
        return beanName;
    }

    private CacheControl parseCacheControl(Element element) {
        CacheControl cacheControl;
        if ("true".equals(element.getAttribute("no-cache"))) {
            cacheControl = CacheControl.noCache();
        } else if ("true".equals(element.getAttribute("no-store"))) {
            cacheControl = CacheControl.noStore();
        } else if (element.hasAttribute("max-age")) {
            cacheControl = CacheControl.maxAge(Long.parseLong(element.getAttribute("max-age")), TimeUnit.SECONDS);
        } else {
            cacheControl = CacheControl.empty();
        }
        if ("true".equals(element.getAttribute("must-revalidate"))) {
            cacheControl = cacheControl.mustRevalidate();
        }
        if ("true".equals(element.getAttribute("no-transform"))) {
            cacheControl = cacheControl.noTransform();
        }
        if ("true".equals(element.getAttribute("cache-public"))) {
            cacheControl = cacheControl.cachePublic();
        }
        if ("true".equals(element.getAttribute("cache-private"))) {
            cacheControl = cacheControl.cachePrivate();
        }
        if ("true".equals(element.getAttribute("proxy-revalidate"))) {
            cacheControl = cacheControl.proxyRevalidate();
        }
        if (element.hasAttribute("s-maxage")) {
            cacheControl = cacheControl.sMaxAge(Long.parseLong(element.getAttribute("s-maxage")), TimeUnit.SECONDS);
        }
        if (element.hasAttribute("stale-while-revalidate")) {
            cacheControl = cacheControl.staleWhileRevalidate(Long.parseLong(element.getAttribute("stale-while-revalidate")), TimeUnit.SECONDS);
        }
        if (element.hasAttribute("stale-if-error")) {
            cacheControl = cacheControl.staleIfError(Long.parseLong(element.getAttribute("stale-if-error")), TimeUnit.SECONDS);
        }
        return cacheControl;
    }

    private void parseResourceChain(RootBeanDefinition resourceHandlerDef, ParserContext context, Element element, @Nullable Object source) {
        String autoRegistration = element.getAttribute("auto-registration");
        boolean isAutoRegistration = (StringUtils.hasText(autoRegistration) && "false".equals(autoRegistration)) ? false : true;
        ManagedList<Object> resourceResolvers = new ManagedList<>();
        resourceResolvers.setSource(source);
        ManagedList<Object> resourceTransformers = new ManagedList<>();
        resourceTransformers.setSource(source);
        parseResourceCache(resourceResolvers, resourceTransformers, element, source);
        parseResourceResolversTransformers(isAutoRegistration, resourceResolvers, resourceTransformers, context, element, source);
        if (!resourceResolvers.isEmpty()) {
            resourceHandlerDef.getPropertyValues().add("resourceResolvers", resourceResolvers);
        }
        if (!resourceTransformers.isEmpty()) {
            resourceHandlerDef.getPropertyValues().add("resourceTransformers", resourceTransformers);
        }
    }

    private void parseResourceCache(ManagedList<Object> resourceResolvers, ManagedList<Object> resourceTransformers, Element element, @Nullable Object source) {
        String resourceCache = element.getAttribute("resource-cache");
        if ("true".equals(resourceCache)) {
            ConstructorArgumentValues cargs = new ConstructorArgumentValues();
            RootBeanDefinition cachingResolverDef = new RootBeanDefinition((Class<?>) CachingResourceResolver.class);
            cachingResolverDef.setSource(source);
            cachingResolverDef.setRole(2);
            cachingResolverDef.setConstructorArgumentValues(cargs);
            RootBeanDefinition cachingTransformerDef = new RootBeanDefinition((Class<?>) CachingResourceTransformer.class);
            cachingTransformerDef.setSource(source);
            cachingTransformerDef.setRole(2);
            cachingTransformerDef.setConstructorArgumentValues(cargs);
            String cacheManagerName = element.getAttribute("cache-manager");
            String cacheName = element.getAttribute("cache-name");
            if (StringUtils.hasText(cacheManagerName) && StringUtils.hasText(cacheName)) {
                RuntimeBeanReference cacheManagerRef = new RuntimeBeanReference(cacheManagerName);
                cargs.addIndexedArgumentValue(0, cacheManagerRef);
                cargs.addIndexedArgumentValue(1, cacheName);
            } else {
                ConstructorArgumentValues cacheCavs = new ConstructorArgumentValues();
                cacheCavs.addIndexedArgumentValue(0, RESOURCE_CHAIN_CACHE);
                RootBeanDefinition cacheDef = new RootBeanDefinition((Class<?>) ConcurrentMapCache.class);
                cacheDef.setSource(source);
                cacheDef.setRole(2);
                cacheDef.setConstructorArgumentValues(cacheCavs);
                cargs.addIndexedArgumentValue(0, cacheDef);
            }
            resourceResolvers.add(cachingResolverDef);
            resourceTransformers.add(cachingTransformerDef);
        }
    }

    private void parseResourceResolversTransformers(boolean isAutoRegistration, ManagedList<Object> resourceResolvers, ManagedList<Object> resourceTransformers, ParserContext context, Element element, @Nullable Object source) {
        Element resolversElement = DomUtils.getChildElementByTagName(element, "resolvers");
        if (resolversElement != null) {
            for (Element beanElement : DomUtils.getChildElements(resolversElement)) {
                if (VERSION_RESOLVER_ELEMENT.equals(beanElement.getLocalName())) {
                    RootBeanDefinition versionResolverDef = parseVersionResolver(context, beanElement, source);
                    versionResolverDef.setSource(source);
                    resourceResolvers.add(versionResolverDef);
                    if (isAutoRegistration) {
                        RootBeanDefinition cssLinkTransformerDef = new RootBeanDefinition((Class<?>) CssLinkResourceTransformer.class);
                        cssLinkTransformerDef.setSource(source);
                        cssLinkTransformerDef.setRole(2);
                        resourceTransformers.add(cssLinkTransformerDef);
                    }
                } else {
                    Object object = context.getDelegate().parsePropertySubElement(beanElement, null);
                    resourceResolvers.add(object);
                }
            }
        }
        if (isAutoRegistration) {
            if (webJarsPresent) {
                RootBeanDefinition webJarsResolverDef = new RootBeanDefinition((Class<?>) WebJarsResourceResolver.class);
                webJarsResolverDef.setSource(source);
                webJarsResolverDef.setRole(2);
                resourceResolvers.add(webJarsResolverDef);
            }
            RootBeanDefinition pathResolverDef = new RootBeanDefinition((Class<?>) PathResourceResolver.class);
            pathResolverDef.setSource(source);
            pathResolverDef.setRole(2);
            resourceResolvers.add(pathResolverDef);
        }
        Element transformersElement = DomUtils.getChildElementByTagName(element, "transformers");
        if (transformersElement != null) {
            Iterator<Element> it = DomUtils.getChildElementsByTagName(transformersElement, "bean", "ref").iterator();
            while (it.hasNext()) {
                Object object2 = context.getDelegate().parsePropertySubElement(it.next(), null);
                resourceTransformers.add(object2);
            }
        }
    }

    private RootBeanDefinition parseVersionResolver(ParserContext context, Element element, @Nullable Object source) {
        ManagedMap<String, Object> strategyMap = new ManagedMap<>();
        strategyMap.setSource(source);
        RootBeanDefinition versionResolverDef = new RootBeanDefinition((Class<?>) VersionResourceResolver.class);
        versionResolverDef.setSource(source);
        versionResolverDef.setRole(2);
        versionResolverDef.getPropertyValues().addPropertyValue("strategyMap", strategyMap);
        for (Element beanElement : DomUtils.getChildElements(element)) {
            String[] patterns = StringUtils.commaDelimitedListToStringArray(beanElement.getAttribute("patterns"));
            Object strategy = null;
            if (FIXED_VERSION_STRATEGY_ELEMENT.equals(beanElement.getLocalName())) {
                ConstructorArgumentValues cargs = new ConstructorArgumentValues();
                cargs.addIndexedArgumentValue(0, beanElement.getAttribute("version"));
                RootBeanDefinition strategyDef = new RootBeanDefinition((Class<?>) FixedVersionStrategy.class);
                strategyDef.setSource(source);
                strategyDef.setRole(2);
                strategyDef.setConstructorArgumentValues(cargs);
                strategy = strategyDef;
            } else if (CONTENT_VERSION_STRATEGY_ELEMENT.equals(beanElement.getLocalName())) {
                RootBeanDefinition strategyDef2 = new RootBeanDefinition((Class<?>) ContentVersionStrategy.class);
                strategyDef2.setSource(source);
                strategyDef2.setRole(2);
                strategy = strategyDef2;
            } else if (VERSION_STRATEGY_ELEMENT.equals(beanElement.getLocalName())) {
                Element childElement = DomUtils.getChildElementsByTagName(beanElement, "bean", "ref").get(0);
                strategy = context.getDelegate().parsePropertySubElement(childElement, null);
            }
            for (String pattern : patterns) {
                strategyMap.put(pattern, strategy);
            }
        }
        return versionResolverDef;
    }
}
