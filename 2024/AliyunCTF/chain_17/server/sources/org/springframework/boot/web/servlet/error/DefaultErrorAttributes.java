package org.springframework.boot.web.servlet.error;

import ch.qos.logback.classic.encoder.JsonEncoder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.tags.BindErrorsTag;
import org.springframework.web.servlet.tags.BindTag;

@Order(Integer.MIN_VALUE)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/error/DefaultErrorAttributes.class */
public class DefaultErrorAttributes implements ErrorAttributes, HandlerExceptionResolver, Ordered {
    private static final String ERROR_INTERNAL_ATTRIBUTE = DefaultErrorAttributes.class.getName() + ".ERROR";

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Override // org.springframework.web.servlet.HandlerExceptionResolver
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        storeErrorAttributes(request, ex);
        return null;
    }

    private void storeErrorAttributes(HttpServletRequest request, Exception ex) {
        request.setAttribute(ERROR_INTERNAL_ATTRIBUTE, ex);
    }

    @Override // org.springframework.boot.web.servlet.error.ErrorAttributes
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = getErrorAttributes(webRequest, options.isIncluded(ErrorAttributeOptions.Include.STACK_TRACE));
        if (!options.isIncluded(ErrorAttributeOptions.Include.EXCEPTION)) {
            errorAttributes.remove(SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE);
        }
        if (!options.isIncluded(ErrorAttributeOptions.Include.STACK_TRACE)) {
            errorAttributes.remove("trace");
        }
        if (!options.isIncluded(ErrorAttributeOptions.Include.MESSAGE) && errorAttributes.get(JsonEncoder.MESSAGE_ATTR_NAME) != null) {
            errorAttributes.remove(JsonEncoder.MESSAGE_ATTR_NAME);
        }
        if (!options.isIncluded(ErrorAttributeOptions.Include.BINDING_ERRORS)) {
            errorAttributes.remove(BindErrorsTag.ERRORS_VARIABLE_NAME);
        }
        return errorAttributes;
    }

    private Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        errorAttributes.put(JsonEncoder.TIMESTAMP_ATTR_NAME, new Date());
        addStatus(errorAttributes, webRequest);
        addErrorDetails(errorAttributes, webRequest, includeStackTrace);
        addPath(errorAttributes, webRequest);
        return errorAttributes;
    }

    private void addStatus(Map<String, Object> errorAttributes, RequestAttributes requestAttributes) {
        Integer status = (Integer) getAttribute(requestAttributes, "jakarta.servlet.error.status_code");
        if (status == null) {
            errorAttributes.put(BindTag.STATUS_VARIABLE_NAME, 999);
            errorAttributes.put("error", "None");
        } else {
            errorAttributes.put(BindTag.STATUS_VARIABLE_NAME, status);
            try {
                errorAttributes.put("error", HttpStatus.valueOf(status.intValue()).getReasonPhrase());
            } catch (Exception e) {
                errorAttributes.put("error", "Http Status " + status);
            }
        }
    }

    private void addErrorDetails(Map<String, Object> errorAttributes, WebRequest webRequest, boolean includeStackTrace) {
        Throwable error = getError(webRequest);
        if (error != null) {
            while ((error instanceof ServletException) && error.getCause() != null) {
                error = error.getCause();
            }
            errorAttributes.put(SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE, error.getClass().getName());
            if (includeStackTrace) {
                addStackTrace(errorAttributes, error);
            }
        }
        addErrorMessage(errorAttributes, webRequest, error);
    }

    private void addErrorMessage(Map<String, Object> errorAttributes, WebRequest webRequest, Throwable error) {
        BindingResult result = extractBindingResult(error);
        if (result == null) {
            addExceptionErrorMessage(errorAttributes, webRequest, error);
        } else {
            addBindingResultErrorMessage(errorAttributes, result);
        }
    }

    private void addExceptionErrorMessage(Map<String, Object> errorAttributes, WebRequest webRequest, Throwable error) {
        errorAttributes.put(JsonEncoder.MESSAGE_ATTR_NAME, getMessage(webRequest, error));
    }

    protected String getMessage(WebRequest webRequest, Throwable error) {
        Object message = getAttribute(webRequest, "jakarta.servlet.error.message");
        if (!ObjectUtils.isEmpty(message)) {
            return message.toString();
        }
        if (error != null && StringUtils.hasLength(error.getMessage())) {
            return error.getMessage();
        }
        return "No message available";
    }

    private void addBindingResultErrorMessage(Map<String, Object> errorAttributes, BindingResult result) {
        errorAttributes.put(JsonEncoder.MESSAGE_ATTR_NAME, "Validation failed for object='" + result.getObjectName() + "'. Error count: " + result.getErrorCount());
        errorAttributes.put(BindErrorsTag.ERRORS_VARIABLE_NAME, result.getAllErrors());
    }

    /* JADX WARN: Multi-variable type inference failed */
    private BindingResult extractBindingResult(Throwable error) {
        if (error instanceof BindingResult) {
            BindingResult bindingResult = (BindingResult) error;
            return bindingResult;
        }
        return null;
    }

    private void addStackTrace(Map<String, Object> errorAttributes, Throwable error) {
        StringWriter stackTrace = new StringWriter();
        error.printStackTrace(new PrintWriter(stackTrace));
        stackTrace.flush();
        errorAttributes.put("trace", stackTrace.toString());
    }

    private void addPath(Map<String, Object> errorAttributes, RequestAttributes requestAttributes) {
        String path = (String) getAttribute(requestAttributes, "jakarta.servlet.error.request_uri");
        if (path != null) {
            errorAttributes.put("path", path);
        }
    }

    @Override // org.springframework.boot.web.servlet.error.ErrorAttributes
    public Throwable getError(WebRequest webRequest) {
        Throwable exception = (Throwable) getAttribute(webRequest, ERROR_INTERNAL_ATTRIBUTE);
        if (exception == null) {
            exception = (Throwable) getAttribute(webRequest, "jakarta.servlet.error.exception");
        }
        return exception;
    }

    private <T> T getAttribute(RequestAttributes requestAttributes, String str) {
        return (T) requestAttributes.getAttribute(str, 0);
    }
}
