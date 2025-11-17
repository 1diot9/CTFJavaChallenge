package org.springframework.web.servlet.support;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/support/RequestContextUtils.class */
public abstract class RequestContextUtils {
    public static final String REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME = "requestDataValueProcessor";

    @Nullable
    public static WebApplicationContext findWebApplicationContext(HttpServletRequest request, @Nullable ServletContext servletContext) {
        WebApplicationContext webApplicationContext = (WebApplicationContext) request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (webApplicationContext == null) {
            if (servletContext != null) {
                webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            }
            if (webApplicationContext == null) {
                webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
            }
        }
        return webApplicationContext;
    }

    @Nullable
    public static WebApplicationContext findWebApplicationContext(HttpServletRequest request) {
        return findWebApplicationContext(request, request.getServletContext());
    }

    @Nullable
    public static LocaleResolver getLocaleResolver(HttpServletRequest request) {
        return (LocaleResolver) request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE);
    }

    public static Locale getLocale(HttpServletRequest request) {
        LocaleResolver localeResolver = getLocaleResolver(request);
        return localeResolver != null ? localeResolver.resolveLocale(request) : request.getLocale();
    }

    @Nullable
    public static TimeZone getTimeZone(HttpServletRequest request) {
        LocaleResolver localeResolver = getLocaleResolver(request);
        if (localeResolver instanceof LocaleContextResolver) {
            LocaleContextResolver localeContextResolver = (LocaleContextResolver) localeResolver;
            LocaleContext localeContext = localeContextResolver.resolveLocaleContext(request);
            if (localeContext instanceof TimeZoneAwareLocaleContext) {
                TimeZoneAwareLocaleContext timeZoneAwareLocaleContext = (TimeZoneAwareLocaleContext) localeContext;
                return timeZoneAwareLocaleContext.getTimeZone();
            }
            return null;
        }
        return null;
    }

    @Nullable
    @Deprecated(since = "6.0")
    public static ThemeResolver getThemeResolver(HttpServletRequest request) {
        return (ThemeResolver) request.getAttribute(DispatcherServlet.THEME_RESOLVER_ATTRIBUTE);
    }

    @Nullable
    @Deprecated(since = "6.0")
    public static ThemeSource getThemeSource(HttpServletRequest request) {
        return (ThemeSource) request.getAttribute(DispatcherServlet.THEME_SOURCE_ATTRIBUTE);
    }

    @Nullable
    @Deprecated(since = "6.0")
    public static Theme getTheme(HttpServletRequest request) {
        ThemeResolver themeResolver = getThemeResolver(request);
        ThemeSource themeSource = getThemeSource(request);
        if (themeResolver != null && themeSource != null) {
            String themeName = themeResolver.resolveThemeName(request);
            return themeSource.getTheme(themeName);
        }
        return null;
    }

    @Nullable
    public static Map<String, ?> getInputFlashMap(HttpServletRequest request) {
        return (Map) request.getAttribute(DispatcherServlet.INPUT_FLASH_MAP_ATTRIBUTE);
    }

    public static FlashMap getOutputFlashMap(HttpServletRequest request) {
        return (FlashMap) request.getAttribute(DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE);
    }

    @Nullable
    public static FlashMapManager getFlashMapManager(HttpServletRequest request) {
        return (FlashMapManager) request.getAttribute(DispatcherServlet.FLASH_MAP_MANAGER_ATTRIBUTE);
    }

    public static void saveOutputFlashMap(String location, HttpServletRequest request, HttpServletResponse response) {
        FlashMap flashMap = getOutputFlashMap(request);
        if (CollectionUtils.isEmpty(flashMap)) {
            return;
        }
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(location).build();
        flashMap.setTargetRequestPath(uriComponents.getPath());
        flashMap.addTargetRequestParams(uriComponents.getQueryParams());
        FlashMapManager manager = getFlashMapManager(request);
        Assert.state(manager != null, "No FlashMapManager. Is this a DispatcherServlet handled request?");
        manager.saveOutputFlashMap(flashMap, request, response);
    }
}
