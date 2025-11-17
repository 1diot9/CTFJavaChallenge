package org.springframework.web.servlet.mvc.annotation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/annotation/ResponseStatusExceptionResolver.class */
public class ResponseStatusExceptionResolver extends AbstractHandlerExceptionResolver implements MessageSourceAware {

    @Nullable
    private MessageSource messageSource;

    @Override // org.springframework.context.MessageSourceAware
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver
    @Nullable
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        try {
            if (ex instanceof ResponseStatusException) {
                ResponseStatusException rse = (ResponseStatusException) ex;
                return resolveResponseStatusException(rse, request, response, handler);
            }
            ResponseStatus status = (ResponseStatus) AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseStatus.class);
            if (status != null) {
                return resolveResponseStatus(status, request, response, handler, ex);
            }
            Throwable cause = ex.getCause();
            if (cause instanceof Exception) {
                Exception cause2 = (Exception) cause;
                return doResolveException(request, response, handler, cause2);
            }
            return null;
        } catch (Exception resolveEx) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn("Failure while trying to resolve exception [" + ex.getClass().getName() + "]", resolveEx);
                return null;
            }
            return null;
        }
    }

    protected ModelAndView resolveResponseStatus(ResponseStatus responseStatus, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) throws Exception {
        int statusCode = responseStatus.code().value();
        String reason = responseStatus.reason();
        return applyStatusAndReason(statusCode, reason, response);
    }

    protected ModelAndView resolveResponseStatusException(ResponseStatusException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws Exception {
        ex.getHeaders().forEach((name, values) -> {
            values.forEach(value -> {
                response.addHeader(name, value);
            });
        });
        return applyStatusAndReason(ex.getStatusCode().value(), ex.getReason(), response);
    }

    protected ModelAndView applyStatusAndReason(int statusCode, @Nullable String reason, HttpServletResponse response) throws IOException {
        String str;
        if (!StringUtils.hasLength(reason)) {
            response.sendError(statusCode);
        } else {
            if (this.messageSource != null) {
                str = this.messageSource.getMessage(reason, null, reason, LocaleContextHolder.getLocale());
            } else {
                str = reason;
            }
            String resolvedReason = str;
            response.sendError(statusCode, resolvedReason);
        }
        return new ModelAndView();
    }
}
