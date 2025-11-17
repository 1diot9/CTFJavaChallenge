package org.springframework.web.servlet.mvc.method.annotation;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ResponseEntityExceptionHandler.class */
public abstract class ResponseEntityExceptionHandler implements MessageSourceAware {
    public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";
    protected static final Log pageNotFoundLogger = LogFactory.getLog("org.springframework.web.servlet.PageNotFound");
    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    private MessageSource messageSource;

    @Override // org.springframework.context.MessageSourceAware
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Nullable
    protected MessageSource getMessageSource() {
        return this.messageSource;
    }

    @Nullable
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class, HttpMediaTypeNotSupportedException.class, HttpMediaTypeNotAcceptableException.class, MissingPathVariableException.class, MissingServletRequestParameterException.class, MissingServletRequestPartException.class, ServletRequestBindingException.class, MethodArgumentNotValidException.class, HandlerMethodValidationException.class, NoHandlerFoundException.class, NoResourceFoundException.class, AsyncRequestTimeoutException.class, ErrorResponseException.class, MaxUploadSizeExceededException.class, ConversionNotSupportedException.class, TypeMismatchException.class, HttpMessageNotReadableException.class, HttpMessageNotWritableException.class, MethodValidationException.class, BindException.class})
    public final ResponseEntity<Object> handleException(Exception ex, WebRequest request) throws Exception {
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            HttpRequestMethodNotSupportedException subEx = (HttpRequestMethodNotSupportedException) ex;
            return handleHttpRequestMethodNotSupported(subEx, subEx.getHeaders(), subEx.getStatusCode(), request);
        }
        if (ex instanceof HttpMediaTypeNotSupportedException) {
            HttpMediaTypeNotSupportedException subEx2 = (HttpMediaTypeNotSupportedException) ex;
            return handleHttpMediaTypeNotSupported(subEx2, subEx2.getHeaders(), subEx2.getStatusCode(), request);
        }
        if (ex instanceof HttpMediaTypeNotAcceptableException) {
            HttpMediaTypeNotAcceptableException subEx3 = (HttpMediaTypeNotAcceptableException) ex;
            return handleHttpMediaTypeNotAcceptable(subEx3, subEx3.getHeaders(), subEx3.getStatusCode(), request);
        }
        if (ex instanceof MissingPathVariableException) {
            MissingPathVariableException subEx4 = (MissingPathVariableException) ex;
            return handleMissingPathVariable(subEx4, subEx4.getHeaders(), subEx4.getStatusCode(), request);
        }
        if (ex instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException subEx5 = (MissingServletRequestParameterException) ex;
            return handleMissingServletRequestParameter(subEx5, subEx5.getHeaders(), subEx5.getStatusCode(), request);
        }
        if (ex instanceof MissingServletRequestPartException) {
            MissingServletRequestPartException subEx6 = (MissingServletRequestPartException) ex;
            return handleMissingServletRequestPart(subEx6, subEx6.getHeaders(), subEx6.getStatusCode(), request);
        }
        if (ex instanceof ServletRequestBindingException) {
            ServletRequestBindingException subEx7 = (ServletRequestBindingException) ex;
            return handleServletRequestBindingException(subEx7, subEx7.getHeaders(), subEx7.getStatusCode(), request);
        }
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException subEx8 = (MethodArgumentNotValidException) ex;
            return handleMethodArgumentNotValid(subEx8, subEx8.getHeaders(), subEx8.getStatusCode(), request);
        }
        if (ex instanceof HandlerMethodValidationException) {
            HandlerMethodValidationException subEx9 = (HandlerMethodValidationException) ex;
            return handleHandlerMethodValidationException(subEx9, subEx9.getHeaders(), subEx9.getStatusCode(), request);
        }
        if (ex instanceof NoHandlerFoundException) {
            NoHandlerFoundException subEx10 = (NoHandlerFoundException) ex;
            return handleNoHandlerFoundException(subEx10, subEx10.getHeaders(), subEx10.getStatusCode(), request);
        }
        if (ex instanceof NoResourceFoundException) {
            NoResourceFoundException subEx11 = (NoResourceFoundException) ex;
            return handleNoResourceFoundException(subEx11, subEx11.getHeaders(), subEx11.getStatusCode(), request);
        }
        if (ex instanceof AsyncRequestTimeoutException) {
            AsyncRequestTimeoutException subEx12 = (AsyncRequestTimeoutException) ex;
            return handleAsyncRequestTimeoutException(subEx12, subEx12.getHeaders(), subEx12.getStatusCode(), request);
        }
        if (ex instanceof ErrorResponseException) {
            ErrorResponseException subEx13 = (ErrorResponseException) ex;
            return handleErrorResponseException(subEx13, subEx13.getHeaders(), subEx13.getStatusCode(), request);
        }
        if (ex instanceof MaxUploadSizeExceededException) {
            MaxUploadSizeExceededException subEx14 = (MaxUploadSizeExceededException) ex;
            return handleMaxUploadSizeExceededException(subEx14, subEx14.getHeaders(), subEx14.getStatusCode(), request);
        }
        HttpHeaders headers = new HttpHeaders();
        if (ex instanceof ConversionNotSupportedException) {
            ConversionNotSupportedException theEx = (ConversionNotSupportedException) ex;
            return handleConversionNotSupported(theEx, headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
        if (ex instanceof TypeMismatchException) {
            TypeMismatchException theEx2 = (TypeMismatchException) ex;
            return handleTypeMismatch(theEx2, headers, HttpStatus.BAD_REQUEST, request);
        }
        if (ex instanceof HttpMessageNotReadableException) {
            HttpMessageNotReadableException theEx3 = (HttpMessageNotReadableException) ex;
            return handleHttpMessageNotReadable(theEx3, headers, HttpStatus.BAD_REQUEST, request);
        }
        if (ex instanceof HttpMessageNotWritableException) {
            HttpMessageNotWritableException theEx4 = (HttpMessageNotWritableException) ex;
            return handleHttpMessageNotWritable(theEx4, headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
        if (ex instanceof MethodValidationException) {
            return handleMethodValidationException((MethodValidationException) ex, headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
        if (ex instanceof BindException) {
            BindException theEx5 = (BindException) ex;
            return handleBindException(theEx5, headers, HttpStatus.BAD_REQUEST, request);
        }
        throw ex;
    }

    @Nullable
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        pageNotFoundLogger.warn(ex.getMessage());
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleErrorResponseException(ErrorResponseException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Object[] args = {ex.getPropertyName(), ex.getValue()};
        String defaultDetail = "Failed to convert '" + args[0] + "' with value: '" + args[1] + "'";
        ProblemDetail body = createProblemDetail(ex, status, defaultDetail, null, args, request);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Object[] args = {ex.getPropertyName(), ex.getValue()};
        String defaultDetail = "Failed to convert '" + args[0] + "' with value: '" + args[1] + "'";
        String messageCode = ErrorResponse.getDefaultDetailMessageCode(TypeMismatchException.class, null);
        ProblemDetail body = createProblemDetail(ex, status, defaultDetail, messageCode, args, request);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail body = createProblemDetail(ex, status, "Failed to read request", null, null, request);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail body = createProblemDetail(ex, status, "Failed to write request", null, null, request);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Nullable
    @Deprecated(since = "6.0", forRemoval = true)
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(status, "Failed to bind request");
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleMethodValidationException(MethodValidationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ProblemDetail body = createProblemDetail(ex, status, "Validation failed", null, null, request);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    protected ProblemDetail createProblemDetail(Exception ex, HttpStatusCode status, String defaultDetail, @Nullable String detailMessageCode, @Nullable Object[] detailMessageArguments, WebRequest request) {
        ErrorResponse.Builder builder = ErrorResponse.builder(ex, status, defaultDetail);
        if (detailMessageCode != null) {
            builder.detailMessageCode(detailMessageCode);
        }
        if (detailMessageArguments != null) {
            builder.detailMessageArguments(detailMessageArguments);
        }
        return builder.build().updateAndGetBody(this.messageSource, LocaleContextHolder.getLocale());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        if (request instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) request;
            HttpServletResponse response = servletWebRequest.getResponse();
            if (response != null && response.isCommitted()) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Response already committed. Ignoring: " + ex);
                    return null;
                }
                return null;
            }
        }
        if (body == null && (ex instanceof ErrorResponse)) {
            ErrorResponse errorResponse = (ErrorResponse) ex;
            body = errorResponse.updateAndGetBody(this.messageSource, LocaleContextHolder.getLocale());
        }
        if (statusCode.equals(HttpStatus.INTERNAL_SERVER_ERROR) && body == null) {
            request.setAttribute("jakarta.servlet.error.exception", ex, 0);
        }
        return createResponseEntity(body, headers, statusCode, request);
    }

    protected ResponseEntity<Object> createResponseEntity(@Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        return new ResponseEntity<>(body, headers, statusCode);
    }
}
