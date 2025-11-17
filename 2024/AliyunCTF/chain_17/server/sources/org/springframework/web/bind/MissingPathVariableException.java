package org.springframework.web.bind;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/MissingPathVariableException.class */
public class MissingPathVariableException extends MissingRequestValueException {
    private final String variableName;
    private final MethodParameter parameter;

    public MissingPathVariableException(String variableName, MethodParameter parameter) {
        this(variableName, parameter, false);
    }

    public MissingPathVariableException(String variableName, MethodParameter parameter, boolean missingAfterConversion) {
        super("", missingAfterConversion, null, new Object[]{variableName});
        this.variableName = variableName;
        this.parameter = parameter;
        getBody().setDetail("Required path variable '" + this.variableName + "' is not present.");
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "Required URI template variable '" + this.variableName + "' for method parameter type " + this.parameter.getNestedParameterType().getSimpleName() + " is " + (isMissingAfterConversion() ? "present but converted to null" : "not present");
    }

    public final String getVariableName() {
        return this.variableName;
    }

    public final MethodParameter getParameter() {
        return this.parameter;
    }

    @Override // org.springframework.web.bind.ServletRequestBindingException, org.springframework.web.ErrorResponse
    public HttpStatusCode getStatusCode() {
        return isMissingAfterConversion() ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
