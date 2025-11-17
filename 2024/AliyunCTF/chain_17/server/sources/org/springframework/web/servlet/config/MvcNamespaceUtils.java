package org.springframework.web.servlet.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.support.SessionFlashMapManager;
import org.springframework.web.servlet.theme.FixedThemeResolver;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/MvcNamespaceUtils.class */
public abstract class MvcNamespaceUtils {
    private static final String BEAN_NAME_URL_HANDLER_MAPPING_BEAN_NAME = BeanNameUrlHandlerMapping.class.getName();
    private static final String SIMPLE_CONTROLLER_HANDLER_ADAPTER_BEAN_NAME = SimpleControllerHandlerAdapter.class.getName();
    private static final String HTTP_REQUEST_HANDLER_ADAPTER_BEAN_NAME = HttpRequestHandlerAdapter.class.getName();
    private static final String URL_PATH_HELPER_BEAN_NAME = "mvcUrlPathHelper";
    private static final String PATH_MATCHER_BEAN_NAME = "mvcPathMatcher";
    private static final String CORS_CONFIGURATION_BEAN_NAME = "mvcCorsConfigurations";
    private static final String HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME = "mvcHandlerMappingIntrospector";

    public static void registerDefaultComponents(ParserContext context, @Nullable Object source) {
        registerBeanNameUrlHandlerMapping(context, source);
        registerHttpRequestHandlerAdapter(context, source);
        registerSimpleControllerHandlerAdapter(context, source);
        registerHandlerMappingIntrospector(context, source);
        registerLocaleResolver(context, source);
        registerThemeResolver(context, source);
        registerViewNameTranslator(context, source);
        registerFlashMapManager(context, source);
    }

    public static RuntimeBeanReference registerUrlPathHelper(@Nullable RuntimeBeanReference urlPathHelperRef, ParserContext context, @Nullable Object source) {
        if (urlPathHelperRef != null) {
            if (context.getRegistry().isAlias(URL_PATH_HELPER_BEAN_NAME)) {
                context.getRegistry().removeAlias(URL_PATH_HELPER_BEAN_NAME);
            }
            context.getRegistry().registerAlias(urlPathHelperRef.getBeanName(), URL_PATH_HELPER_BEAN_NAME);
        } else if (!context.getRegistry().isAlias(URL_PATH_HELPER_BEAN_NAME) && !context.getRegistry().containsBeanDefinition(URL_PATH_HELPER_BEAN_NAME)) {
            RootBeanDefinition urlPathHelperDef = new RootBeanDefinition((Class<?>) UrlPathHelper.class);
            urlPathHelperDef.setSource(source);
            urlPathHelperDef.setRole(2);
            context.getRegistry().registerBeanDefinition(URL_PATH_HELPER_BEAN_NAME, urlPathHelperDef);
            context.registerComponent(new BeanComponentDefinition(urlPathHelperDef, URL_PATH_HELPER_BEAN_NAME));
        }
        return new RuntimeBeanReference(URL_PATH_HELPER_BEAN_NAME);
    }

    public static RuntimeBeanReference registerPathMatcher(@Nullable RuntimeBeanReference pathMatcherRef, ParserContext context, @Nullable Object source) {
        if (pathMatcherRef != null) {
            if (context.getRegistry().isAlias(PATH_MATCHER_BEAN_NAME)) {
                context.getRegistry().removeAlias(PATH_MATCHER_BEAN_NAME);
            }
            context.getRegistry().registerAlias(pathMatcherRef.getBeanName(), PATH_MATCHER_BEAN_NAME);
        } else if (!context.getRegistry().isAlias(PATH_MATCHER_BEAN_NAME) && !context.getRegistry().containsBeanDefinition(PATH_MATCHER_BEAN_NAME)) {
            RootBeanDefinition pathMatcherDef = new RootBeanDefinition((Class<?>) AntPathMatcher.class);
            pathMatcherDef.setSource(source);
            pathMatcherDef.setRole(2);
            context.getRegistry().registerBeanDefinition(PATH_MATCHER_BEAN_NAME, pathMatcherDef);
            context.registerComponent(new BeanComponentDefinition(pathMatcherDef, PATH_MATCHER_BEAN_NAME));
        }
        return new RuntimeBeanReference(PATH_MATCHER_BEAN_NAME);
    }

    private static void registerBeanNameUrlHandlerMapping(ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(BEAN_NAME_URL_HANDLER_MAPPING_BEAN_NAME)) {
            RootBeanDefinition mappingDef = new RootBeanDefinition((Class<?>) BeanNameUrlHandlerMapping.class);
            mappingDef.setSource(source);
            mappingDef.setRole(2);
            mappingDef.getPropertyValues().add(AbstractBeanDefinition.ORDER_ATTRIBUTE, 2);
            RuntimeBeanReference corsRef = registerCorsConfigurations(null, context, source);
            mappingDef.getPropertyValues().add("corsConfigurations", corsRef);
            context.getRegistry().registerBeanDefinition(BEAN_NAME_URL_HANDLER_MAPPING_BEAN_NAME, mappingDef);
            context.registerComponent(new BeanComponentDefinition(mappingDef, BEAN_NAME_URL_HANDLER_MAPPING_BEAN_NAME));
        }
    }

    private static void registerHttpRequestHandlerAdapter(ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(HTTP_REQUEST_HANDLER_ADAPTER_BEAN_NAME)) {
            RootBeanDefinition adapterDef = new RootBeanDefinition((Class<?>) HttpRequestHandlerAdapter.class);
            adapterDef.setSource(source);
            adapterDef.setRole(2);
            context.getRegistry().registerBeanDefinition(HTTP_REQUEST_HANDLER_ADAPTER_BEAN_NAME, adapterDef);
            context.registerComponent(new BeanComponentDefinition(adapterDef, HTTP_REQUEST_HANDLER_ADAPTER_BEAN_NAME));
        }
    }

    private static void registerSimpleControllerHandlerAdapter(ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(SIMPLE_CONTROLLER_HANDLER_ADAPTER_BEAN_NAME)) {
            RootBeanDefinition beanDef = new RootBeanDefinition((Class<?>) SimpleControllerHandlerAdapter.class);
            beanDef.setSource(source);
            beanDef.setRole(2);
            context.getRegistry().registerBeanDefinition(SIMPLE_CONTROLLER_HANDLER_ADAPTER_BEAN_NAME, beanDef);
            context.registerComponent(new BeanComponentDefinition(beanDef, SIMPLE_CONTROLLER_HANDLER_ADAPTER_BEAN_NAME));
        }
    }

    public static RuntimeBeanReference registerCorsConfigurations(@Nullable Map<String, CorsConfiguration> corsConfigurations, ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(CORS_CONFIGURATION_BEAN_NAME)) {
            RootBeanDefinition corsDef = new RootBeanDefinition((Class<?>) LinkedHashMap.class);
            corsDef.setSource(source);
            corsDef.setRole(2);
            if (corsConfigurations != null) {
                corsDef.getConstructorArgumentValues().addIndexedArgumentValue(0, corsConfigurations);
            }
            context.getReaderContext().getRegistry().registerBeanDefinition(CORS_CONFIGURATION_BEAN_NAME, corsDef);
            context.registerComponent(new BeanComponentDefinition(corsDef, CORS_CONFIGURATION_BEAN_NAME));
        } else if (corsConfigurations != null) {
            context.getRegistry().getBeanDefinition(CORS_CONFIGURATION_BEAN_NAME).getConstructorArgumentValues().addIndexedArgumentValue(0, corsConfigurations);
        }
        return new RuntimeBeanReference(CORS_CONFIGURATION_BEAN_NAME);
    }

    private static void registerHandlerMappingIntrospector(ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME)) {
            RootBeanDefinition beanDef = new RootBeanDefinition((Class<?>) HandlerMappingIntrospector.class);
            beanDef.setSource(source);
            beanDef.setRole(2);
            beanDef.setLazyInit(true);
            context.getRegistry().registerBeanDefinition(HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME, beanDef);
            context.registerComponent(new BeanComponentDefinition(beanDef, HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME));
        }
    }

    private static void registerLocaleResolver(ParserContext context, @Nullable Object source) {
        if (!containsBeanInHierarchy(context, DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)) {
            RootBeanDefinition beanDef = new RootBeanDefinition((Class<?>) AcceptHeaderLocaleResolver.class);
            beanDef.setSource(source);
            beanDef.setRole(2);
            context.getRegistry().registerBeanDefinition(DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME, beanDef);
            context.registerComponent(new BeanComponentDefinition(beanDef, DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME));
        }
    }

    private static void registerThemeResolver(ParserContext context, @Nullable Object source) {
        if (!containsBeanInHierarchy(context, DispatcherServlet.THEME_RESOLVER_BEAN_NAME)) {
            RootBeanDefinition beanDef = new RootBeanDefinition((Class<?>) FixedThemeResolver.class);
            beanDef.setSource(source);
            beanDef.setRole(2);
            context.getRegistry().registerBeanDefinition(DispatcherServlet.THEME_RESOLVER_BEAN_NAME, beanDef);
            context.registerComponent(new BeanComponentDefinition(beanDef, DispatcherServlet.THEME_RESOLVER_BEAN_NAME));
        }
    }

    private static void registerViewNameTranslator(ParserContext context, @Nullable Object source) {
        if (!containsBeanInHierarchy(context, DispatcherServlet.REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME)) {
            RootBeanDefinition beanDef = new RootBeanDefinition((Class<?>) DefaultRequestToViewNameTranslator.class);
            beanDef.setSource(source);
            beanDef.setRole(2);
            context.getRegistry().registerBeanDefinition(DispatcherServlet.REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME, beanDef);
            context.registerComponent(new BeanComponentDefinition(beanDef, DispatcherServlet.REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME));
        }
    }

    private static void registerFlashMapManager(ParserContext context, @Nullable Object source) {
        if (!containsBeanInHierarchy(context, DispatcherServlet.FLASH_MAP_MANAGER_BEAN_NAME)) {
            RootBeanDefinition beanDef = new RootBeanDefinition((Class<?>) SessionFlashMapManager.class);
            beanDef.setSource(source);
            beanDef.setRole(2);
            context.getRegistry().registerBeanDefinition(DispatcherServlet.FLASH_MAP_MANAGER_BEAN_NAME, beanDef);
            context.registerComponent(new BeanComponentDefinition(beanDef, DispatcherServlet.FLASH_MAP_MANAGER_BEAN_NAME));
        }
    }

    @Nullable
    public static Object getContentNegotiationManager(ParserContext context) {
        String name = AnnotationDrivenBeanDefinitionParser.HANDLER_MAPPING_BEAN_NAME;
        if (context.getRegistry().containsBeanDefinition(name)) {
            BeanDefinition handlerMappingBeanDef = context.getRegistry().getBeanDefinition(name);
            return handlerMappingBeanDef.getPropertyValues().get("contentNegotiationManager");
        }
        if (context.getRegistry().containsBeanDefinition(AnnotationDrivenBeanDefinitionParser.CONTENT_NEGOTIATION_MANAGER_BEAN_NAME)) {
            return new RuntimeBeanReference(AnnotationDrivenBeanDefinitionParser.CONTENT_NEGOTIATION_MANAGER_BEAN_NAME);
        }
        return null;
    }

    private static boolean containsBeanInHierarchy(ParserContext context, String beanName) {
        BeanDefinitionRegistry registry = context.getRegistry();
        if (!(registry instanceof BeanFactory)) {
            return registry.containsBeanDefinition(beanName);
        }
        BeanFactory beanFactory = (BeanFactory) registry;
        return beanFactory.containsBean(beanName);
    }
}
