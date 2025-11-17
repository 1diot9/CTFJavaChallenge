package org.springframework.web.method.annotation;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.BindErrorUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/HandlerMethodValidationException.class */
public class HandlerMethodValidationException extends ResponseStatusException implements MethodValidationResult {
    private final MethodValidationResult validationResult;
    private final Predicate<MethodParameter> modelAttribitePredicate;
    private final Predicate<MethodParameter> requestParamPredicate;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/HandlerMethodValidationException$Visitor.class */
    public interface Visitor {
        void cookieValue(CookieValue cookieValue, ParameterValidationResult result);

        void matrixVariable(MatrixVariable matrixVariable, ParameterValidationResult result);

        void modelAttribute(@Nullable ModelAttribute modelAttribute, ParameterErrors errors);

        void pathVariable(PathVariable pathVariable, ParameterValidationResult result);

        void requestBody(RequestBody requestBody, ParameterErrors errors);

        void requestHeader(RequestHeader requestHeader, ParameterValidationResult result);

        void requestParam(@Nullable RequestParam requestParam, ParameterValidationResult result);

        void requestPart(RequestPart requestPart, ParameterErrors errors);

        void other(ParameterValidationResult result);
    }

    public HandlerMethodValidationException(MethodValidationResult validationResult) {
        this(validationResult, param -> {
            return param.hasParameterAnnotation(ModelAttribute.class);
        }, param2 -> {
            return param2.hasParameterAnnotation(RequestParam.class);
        });
    }

    public HandlerMethodValidationException(MethodValidationResult validationResult, Predicate<MethodParameter> modelAttribitePredicate, Predicate<MethodParameter> requestParamPredicate) {
        super(initHttpStatus(validationResult), "Validation failure", null, null, null);
        this.validationResult = validationResult;
        this.modelAttribitePredicate = modelAttribitePredicate;
        this.requestParamPredicate = requestParamPredicate;
    }

    private static HttpStatus initHttpStatus(MethodValidationResult validationResult) {
        return !validationResult.isForReturnValue() ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override // org.springframework.web.ErrorResponse
    public Object[] getDetailMessageArguments(MessageSource messageSource, Locale locale) {
        return new Object[]{BindErrorUtils.resolveAndJoin(getAllErrors(), messageSource, locale)};
    }

    @Override // org.springframework.web.ErrorResponseException, org.springframework.web.ErrorResponse
    public Object[] getDetailMessageArguments() {
        return new Object[]{BindErrorUtils.resolveAndJoin(getAllErrors())};
    }

    @Override // org.springframework.validation.method.MethodValidationResult
    public Object getTarget() {
        return this.validationResult.getTarget();
    }

    @Override // org.springframework.validation.method.MethodValidationResult
    public Method getMethod() {
        return this.validationResult.getMethod();
    }

    @Override // org.springframework.validation.method.MethodValidationResult
    public boolean isForReturnValue() {
        return this.validationResult.isForReturnValue();
    }

    @Override // org.springframework.validation.method.MethodValidationResult
    public List<ParameterValidationResult> getAllValidationResults() {
        return this.validationResult.getAllValidationResults();
    }

    public void visitResults(Visitor visitor) {
        for (ParameterValidationResult result : getAllValidationResults()) {
            MethodParameter param = result.getMethodParameter();
            CookieValue cookieValue = (CookieValue) param.getParameterAnnotation(CookieValue.class);
            if (cookieValue != null) {
                visitor.cookieValue(cookieValue, result);
            } else {
                MatrixVariable matrixVariable = (MatrixVariable) param.getParameterAnnotation(MatrixVariable.class);
                if (matrixVariable != null) {
                    visitor.matrixVariable(matrixVariable, result);
                } else if (this.modelAttribitePredicate.test(param)) {
                    ModelAttribute modelAttribute = (ModelAttribute) param.getParameterAnnotation(ModelAttribute.class);
                    visitor.modelAttribute(modelAttribute, asErrors(result));
                } else {
                    PathVariable pathVariable = (PathVariable) param.getParameterAnnotation(PathVariable.class);
                    if (pathVariable != null) {
                        visitor.pathVariable(pathVariable, result);
                    } else {
                        RequestBody requestBody = (RequestBody) param.getParameterAnnotation(RequestBody.class);
                        if (requestBody != null) {
                            visitor.requestBody(requestBody, asErrors(result));
                        } else {
                            RequestHeader requestHeader = (RequestHeader) param.getParameterAnnotation(RequestHeader.class);
                            if (requestHeader != null) {
                                visitor.requestHeader(requestHeader, result);
                            } else if (this.requestParamPredicate.test(param)) {
                                RequestParam requestParam = (RequestParam) param.getParameterAnnotation(RequestParam.class);
                                visitor.requestParam(requestParam, result);
                            } else {
                                RequestPart requestPart = (RequestPart) param.getParameterAnnotation(RequestPart.class);
                                if (requestPart != null) {
                                    visitor.requestPart(requestPart, asErrors(result));
                                } else {
                                    visitor.other(result);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static ParameterErrors asErrors(ParameterValidationResult result) {
        Assert.state(result instanceof ParameterErrors, "Expected ParameterErrors");
        return (ParameterErrors) result;
    }
}
