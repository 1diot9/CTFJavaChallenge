package org.springframework.web.servlet.mvc.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/support/DefaultHandlerExceptionResolver.class */
public class DefaultHandlerExceptionResolver extends AbstractHandlerExceptionResolver {
    public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";
    protected static final Log pageNotFoundLogger = LogFactory.getLog("org.springframework.web.servlet.PageNotFound");

    public DefaultHandlerExceptionResolver() {
        setOrder(Integer.MAX_VALUE);
        setWarnLogCategory(getClass().getName());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver
    @Nullable
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        try {
            if (ex instanceof ErrorResponse) {
                ErrorResponse errorResponse = (ErrorResponse) ex;
                ModelAndView mav = null;
                if (ex instanceof HttpRequestMethodNotSupportedException) {
                    HttpRequestMethodNotSupportedException theEx = (HttpRequestMethodNotSupportedException) ex;
                    mav = handleHttpRequestMethodNotSupported(theEx, request, response, handler);
                } else if (ex instanceof HttpMediaTypeNotSupportedException) {
                    HttpMediaTypeNotSupportedException theEx2 = (HttpMediaTypeNotSupportedException) ex;
                    mav = handleHttpMediaTypeNotSupported(theEx2, request, response, handler);
                } else if (ex instanceof HttpMediaTypeNotAcceptableException) {
                    HttpMediaTypeNotAcceptableException theEx3 = (HttpMediaTypeNotAcceptableException) ex;
                    mav = handleHttpMediaTypeNotAcceptable(theEx3, request, response, handler);
                } else if (ex instanceof MissingPathVariableException) {
                    MissingPathVariableException theEx4 = (MissingPathVariableException) ex;
                    mav = handleMissingPathVariable(theEx4, request, response, handler);
                } else if (ex instanceof MissingServletRequestParameterException) {
                    MissingServletRequestParameterException theEx5 = (MissingServletRequestParameterException) ex;
                    mav = handleMissingServletRequestParameter(theEx5, request, response, handler);
                } else if (ex instanceof MissingServletRequestPartException) {
                    MissingServletRequestPartException theEx6 = (MissingServletRequestPartException) ex;
                    mav = handleMissingServletRequestPartException(theEx6, request, response, handler);
                } else if (ex instanceof ServletRequestBindingException) {
                    ServletRequestBindingException theEx7 = (ServletRequestBindingException) ex;
                    mav = handleServletRequestBindingException(theEx7, request, response, handler);
                } else if (ex instanceof MethodArgumentNotValidException) {
                    MethodArgumentNotValidException theEx8 = (MethodArgumentNotValidException) ex;
                    mav = handleMethodArgumentNotValidException(theEx8, request, response, handler);
                } else if (ex instanceof HandlerMethodValidationException) {
                    HandlerMethodValidationException theEx9 = (HandlerMethodValidationException) ex;
                    mav = handleHandlerMethodValidationException(theEx9, request, response, handler);
                } else if (ex instanceof NoHandlerFoundException) {
                    NoHandlerFoundException theEx10 = (NoHandlerFoundException) ex;
                    mav = handleNoHandlerFoundException(theEx10, request, response, handler);
                } else if (ex instanceof NoResourceFoundException) {
                    NoResourceFoundException theEx11 = (NoResourceFoundException) ex;
                    mav = handleNoResourceFoundException(theEx11, request, response, handler);
                } else if (ex instanceof AsyncRequestTimeoutException) {
                    AsyncRequestTimeoutException theEx12 = (AsyncRequestTimeoutException) ex;
                    mav = handleAsyncRequestTimeoutException(theEx12, request, response, handler);
                }
                return mav != null ? mav : handleErrorResponse(errorResponse, request, response, handler);
            }
            if (ex instanceof ConversionNotSupportedException) {
                ConversionNotSupportedException theEx13 = (ConversionNotSupportedException) ex;
                return handleConversionNotSupported(theEx13, request, response, handler);
            }
            if (ex instanceof TypeMismatchException) {
                TypeMismatchException theEx14 = (TypeMismatchException) ex;
                return handleTypeMismatch(theEx14, request, response, handler);
            }
            if (ex instanceof HttpMessageNotReadableException) {
                HttpMessageNotReadableException theEx15 = (HttpMessageNotReadableException) ex;
                return handleHttpMessageNotReadable(theEx15, request, response, handler);
            }
            if (ex instanceof HttpMessageNotWritableException) {
                HttpMessageNotWritableException theEx16 = (HttpMessageNotWritableException) ex;
                return handleHttpMessageNotWritable(theEx16, request, response, handler);
            }
            if (ex instanceof MethodValidationException) {
                MethodValidationException theEx17 = (MethodValidationException) ex;
                return handleMethodValidationException(theEx17, request, response, handler);
            }
            if (ex instanceof BindException) {
                BindException theEx18 = (BindException) ex;
                return handleBindException(theEx18, request, response, handler);
            }
            return null;
        } catch (Exception handlerEx) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn("Failure while trying to resolve exception [" + ex.getClass().getName() + "]", handlerEx);
                return null;
            }
            return null;
        }
    }

    @Nullable
    protected ModelAndView handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        return null;
    }

    @Nullable
    protected ModelAndView handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        return null;
    }

    @Nullable
    protected ModelAndView handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        return null;
    }

    @Nullable
    protected ModelAndView handleMissingPathVariable(MissingPathVariableException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        return null;
    }

    @Nullable
    protected ModelAndView handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        return null;
    }

    @Nullable
    protected ModelAndView handleMissingServletRequestPartException(MissingServletRequestPartException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        return null;
    }

    @Nullable
    protected ModelAndView handleServletRequestBindingException(ServletRequestBindingException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        return null;
    }

    @Nullable
    protected ModelAndView handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        return null;
    }

    @Nullable
    protected ModelAndView handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        return null;
    }

    @Nullable
    protected ModelAndView handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        pageNotFoundLogger.warn(ex.getMessage());
        return null;
    }

    @Nullable
    protected ModelAndView handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        return null;
    }

    @Nullable
    protected ModelAndView handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        return null;
    }

    protected ModelAndView handleErrorResponse(ErrorResponse errorResponse, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        if (!response.isCommitted()) {
            HttpHeaders headers = errorResponse.getHeaders();
            headers.forEach((name, values) -> {
                values.forEach(value -> {
                    response.addHeader(name, value);
                });
            });
            int status = errorResponse.getStatusCode().value();
            String message = errorResponse.getBody().getDetail();
            if (message != null) {
                response.sendError(status, message);
            } else {
                response.sendError(status);
            }
        } else {
            this.logger.warn("Ignoring exception, response committed. : " + errorResponse);
        }
        return new ModelAndView();
    }

    protected ModelAndView handleConversionNotSupported(ConversionNotSupportedException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        sendServerError(ex, request, response);
        return new ModelAndView();
    }

    protected ModelAndView handleTypeMismatch(TypeMismatchException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        response.sendError(400);
        return new ModelAndView();
    }

    protected ModelAndView handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        response.sendError(400);
        return new ModelAndView();
    }

    protected ModelAndView handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        sendServerError(ex, request, response);
        return new ModelAndView();
    }

    protected ModelAndView handleMethodValidationException(MethodValidationException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        sendServerError(ex, request, response);
        return new ModelAndView();
    }

    @Deprecated(since = "6.0", forRemoval = true)
    protected ModelAndView handleBindException(BindException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        response.sendError(400);
        return new ModelAndView();
    }

    protected void sendServerError(Exception ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setAttribute("jakarta.servlet.error.exception", ex);
        response.sendError(500);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver
    public void logException(Exception ex, HttpServletRequest request) {
        if ((ex instanceof NoHandlerFoundException) || (ex instanceof NoResourceFoundException)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(buildLogMessage(ex, request));
                return;
            }
            return;
        }
        super.logException(ex, request);
    }
}
