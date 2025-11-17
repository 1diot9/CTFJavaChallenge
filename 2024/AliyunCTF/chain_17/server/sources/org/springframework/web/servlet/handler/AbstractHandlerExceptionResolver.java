package org.springframework.web.servlet.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/AbstractHandlerExceptionResolver.class */
public abstract class AbstractHandlerExceptionResolver implements HandlerExceptionResolver, Ordered {
    private static final String HEADER_CACHE_CONTROL = "Cache-Control";

    @Nullable
    private Predicate<Object> mappedHandlerPredicate;

    @Nullable
    private Set<?> mappedHandlers;

    @Nullable
    private Class<?>[] mappedHandlerClasses;

    @Nullable
    private Log warnLogger;
    protected final Log logger = LogFactory.getLog(getClass());
    private int order = Integer.MAX_VALUE;
    private boolean preventResponseCaching = false;

    @Nullable
    protected abstract ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex);

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setMappedHandlerPredicate(Predicate<Object> predicate) {
        this.mappedHandlerPredicate = this.mappedHandlerPredicate != null ? this.mappedHandlerPredicate.and(predicate) : predicate;
    }

    public void setMappedHandlers(Set<?> mappedHandlers) {
        this.mappedHandlers = mappedHandlers;
    }

    public void setMappedHandlerClasses(Class<?>... mappedHandlerClasses) {
        this.mappedHandlerClasses = mappedHandlerClasses;
    }

    public void addMappedHandlerClass(Class<?> mappedHandlerClass) {
        Class<?>[] clsArr;
        if (this.mappedHandlerClasses != null) {
            clsArr = (Class[]) ObjectUtils.addObjectToArray(this.mappedHandlerClasses, mappedHandlerClass);
        } else {
            clsArr = new Class[]{mappedHandlerClass};
        }
        this.mappedHandlerClasses = clsArr;
    }

    @Nullable
    protected Class<?>[] getMappedHandlerClasses() {
        return this.mappedHandlerClasses;
    }

    public void setWarnLogCategory(String loggerName) {
        this.warnLogger = StringUtils.hasLength(loggerName) ? LogFactory.getLog(loggerName) : null;
    }

    public void setPreventResponseCaching(boolean preventResponseCaching) {
        this.preventResponseCaching = preventResponseCaching;
    }

    @Override // org.springframework.web.servlet.HandlerExceptionResolver
    @Nullable
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        if (shouldApplyTo(request, handler)) {
            prepareResponse(ex, response);
            ModelAndView result = doResolveException(request, response, handler, ex);
            if (result != null) {
                if (this.logger.isDebugEnabled() && (this.warnLogger == null || !this.warnLogger.isWarnEnabled())) {
                    this.logger.debug(buildLogMessage(ex, request) + (result.isEmpty() ? "" : " to " + result));
                }
                logException(ex, request);
            }
            return result;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean shouldApplyTo(HttpServletRequest request, @Nullable Object handler) {
        if (this.mappedHandlerPredicate != null) {
            return this.mappedHandlerPredicate.test(handler);
        }
        if (handler != null) {
            if (this.mappedHandlers != null && this.mappedHandlers.contains(handler)) {
                return true;
            }
            if (this.mappedHandlerClasses != null) {
                for (Class<?> handlerClass : this.mappedHandlerClasses) {
                    if (handlerClass.isInstance(handler)) {
                        return true;
                    }
                }
            }
        }
        return !hasHandlerMappings();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasHandlerMappings() {
        return (this.mappedHandlers == null && this.mappedHandlerClasses == null && this.mappedHandlerPredicate == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logException(Exception ex, HttpServletRequest request) {
        if (this.warnLogger != null && this.warnLogger.isWarnEnabled()) {
            this.warnLogger.warn(buildLogMessage(ex, request));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String buildLogMessage(Exception ex, HttpServletRequest request) {
        return "Resolved [" + LogFormatUtils.formatValue(ex, -1, true) + "]";
    }

    protected void prepareResponse(Exception ex, HttpServletResponse response) {
        if (this.preventResponseCaching) {
            preventCaching(response);
        }
    }

    protected void preventCaching(HttpServletResponse response) {
        response.addHeader("Cache-Control", "no-store");
    }
}
