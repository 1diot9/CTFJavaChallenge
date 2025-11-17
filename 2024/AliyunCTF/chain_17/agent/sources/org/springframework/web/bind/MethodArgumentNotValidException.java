package org.springframework.web.bind;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.util.BindErrorUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/MethodArgumentNotValidException.class */
public class MethodArgumentNotValidException extends BindException implements ErrorResponse {
    private final MethodParameter parameter;
    private final ProblemDetail body;

    public MethodArgumentNotValidException(MethodParameter parameter, BindingResult bindingResult) {
        super(bindingResult);
        this.parameter = parameter;
        this.body = ProblemDetail.forStatusAndDetail(getStatusCode(), "Invalid request content.");
    }

    public final MethodParameter getParameter() {
        return this.parameter;
    }

    @Override // org.springframework.web.ErrorResponse
    public HttpStatusCode getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override // org.springframework.web.ErrorResponse
    public ProblemDetail getBody() {
        return this.body;
    }

    @Override // org.springframework.web.ErrorResponse
    public Object[] getDetailMessageArguments(MessageSource source, Locale locale) {
        return new Object[]{BindErrorUtils.resolveAndJoin(getGlobalErrors(), source, locale), BindErrorUtils.resolveAndJoin(getFieldErrors(), source, locale)};
    }

    @Override // org.springframework.web.ErrorResponse
    public Object[] getDetailMessageArguments() {
        return new Object[]{BindErrorUtils.resolveAndJoin(getGlobalErrors()), BindErrorUtils.resolveAndJoin(getFieldErrors())};
    }

    @Deprecated(since = "6.1", forRemoval = true)
    public static List<String> errorsToStringList(List<? extends ObjectError> errors) {
        return BindErrorUtils.resolve(errors).values().stream().toList();
    }

    @Deprecated(since = "6.1", forRemoval = true)
    public static List<String> errorsToStringList(List<? extends ObjectError> errors, @Nullable MessageSource messageSource, Locale locale) {
        if (messageSource != null) {
            return BindErrorUtils.resolve(errors, messageSource, locale).values().stream().toList();
        }
        return BindErrorUtils.resolve(errors).values().stream().toList();
    }

    @Deprecated(since = "6.1", forRemoval = true)
    public Map<ObjectError, String> resolveErrorMessages(MessageSource messageSource, Locale locale) {
        return BindErrorUtils.resolve(getAllErrors(), messageSource, locale);
    }

    @Override // org.springframework.validation.BindException, java.lang.Throwable
    public String getMessage() {
        StringBuilder sb = new StringBuilder("Validation failed for argument [").append(this.parameter.getParameterIndex()).append("] in ").append(this.parameter.getExecutable().toGenericString());
        BindingResult bindingResult = getBindingResult();
        if (bindingResult.getErrorCount() > 1) {
            sb.append(" with ").append(bindingResult.getErrorCount()).append(" errors");
        }
        sb.append(": ");
        for (ObjectError error : bindingResult.getAllErrors()) {
            sb.append('[').append(error).append("] ");
        }
        return sb.toString();
    }
}
