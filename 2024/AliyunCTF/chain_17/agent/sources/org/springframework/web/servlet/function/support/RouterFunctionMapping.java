package org.springframework.web.servlet.function.support;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.ServerHttpObservationFilter;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/support/RouterFunctionMapping.class */
public class RouterFunctionMapping extends AbstractHandlerMapping implements InitializingBean {

    @Nullable
    private RouterFunction<?> routerFunction;
    private List<HttpMessageConverter<?>> messageConverters = Collections.emptyList();
    private boolean detectHandlerFunctionsInAncestorContexts = false;

    public RouterFunctionMapping() {
    }

    public RouterFunctionMapping(RouterFunction<?> routerFunction) {
        this.routerFunction = routerFunction;
    }

    public void setRouterFunction(@Nullable RouterFunction<?> routerFunction) {
        this.routerFunction = routerFunction;
    }

    @Nullable
    public RouterFunction<?> getRouterFunction() {
        return this.routerFunction;
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }

    public void setDetectHandlerFunctionsInAncestorContexts(boolean detectHandlerFunctionsInAncestorContexts) {
        this.detectHandlerFunctionsInAncestorContexts = detectHandlerFunctionsInAncestorContexts;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        if (this.routerFunction == null) {
            initRouterFunctions();
        }
        if (CollectionUtils.isEmpty(this.messageConverters)) {
            initMessageConverters();
        }
        if (this.routerFunction != null) {
            PathPatternParser patternParser = getPatternParser();
            if (patternParser == null) {
                patternParser = new PathPatternParser();
                setPatternParser(patternParser);
            }
            RouterFunctions.changeParser(this.routerFunction, patternParser);
        }
    }

    private void initRouterFunctions() {
        List<RouterFunction<?>> routerFunctions = (List) obtainApplicationContext().getBeanProvider(RouterFunction.class).orderedStream().map(router -> {
            return router;
        }).collect(Collectors.toList());
        ApplicationContext parentContext = obtainApplicationContext().getParent();
        if (parentContext != null && !this.detectHandlerFunctionsInAncestorContexts) {
            Stream stream = parentContext.getBeanProvider(RouterFunction.class).stream();
            Objects.requireNonNull(routerFunctions);
            stream.forEach((v1) -> {
                r1.remove(v1);
            });
        }
        this.routerFunction = routerFunctions.stream().reduce((v0, v1) -> {
            return v0.andOther(v1);
        }).orElse(null);
        logRouterFunctions(routerFunctions);
    }

    private void logRouterFunctions(List<RouterFunction<?>> routerFunctions) {
        if (this.mappingsLogger.isDebugEnabled()) {
            routerFunctions.forEach(function -> {
                this.mappingsLogger.debug("Mapped " + function);
            });
            return;
        }
        if (this.logger.isDebugEnabled()) {
            int total = routerFunctions.size();
            String message = total + " RouterFunction(s) in " + formatMappingName();
            if (this.logger.isTraceEnabled()) {
                if (total > 0) {
                    routerFunctions.forEach(function2 -> {
                        this.logger.trace("Mapped " + function2);
                    });
                    return;
                } else {
                    this.logger.trace(message);
                    return;
                }
            }
            if (total > 0) {
                this.logger.debug(message);
            }
        }
    }

    private void initMessageConverters() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>(4);
        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        this.messageConverters = messageConverters;
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerMapping
    @Nullable
    protected Object getHandlerInternal(HttpServletRequest servletRequest) throws Exception {
        if (this.routerFunction != null) {
            ServerRequest request = ServerRequest.create(servletRequest, this.messageConverters);
            HandlerFunction<?> handlerFunction = this.routerFunction.route(request).orElse(null);
            setAttributes(servletRequest, request, handlerFunction);
            return handlerFunction;
        }
        return null;
    }

    private void setAttributes(HttpServletRequest servletRequest, ServerRequest request, @Nullable HandlerFunction<?> handlerFunction) {
        PathPattern matchingPattern = (PathPattern) servletRequest.getAttribute(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE);
        if (matchingPattern != null) {
            servletRequest.removeAttribute(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE);
            servletRequest.setAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, matchingPattern.getPatternString());
            ServerHttpObservationFilter.findObservationContext(request.servletRequest()).ifPresent(context -> {
                context.setPathPattern(matchingPattern.getPatternString());
            });
        }
        servletRequest.setAttribute(BEST_MATCHING_HANDLER_ATTRIBUTE, handlerFunction);
        servletRequest.setAttribute(RouterFunctions.REQUEST_ATTRIBUTE, request);
    }
}
