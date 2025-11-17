package org.springframework.web.bind;

import org.springframework.core.MethodParameter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/MissingMatrixVariableException.class */
public class MissingMatrixVariableException extends MissingRequestValueException {
    private final String variableName;
    private final MethodParameter parameter;

    public MissingMatrixVariableException(String variableName, MethodParameter parameter) {
        this(variableName, parameter, false);
    }

    public MissingMatrixVariableException(String variableName, MethodParameter parameter, boolean missingAfterConversion) {
        super("", missingAfterConversion, null, new Object[]{variableName});
        this.variableName = variableName;
        this.parameter = parameter;
        getBody().setDetail("Required path parameter '" + this.variableName + "' is not present.");
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "Required matrix variable '" + this.variableName + "' for method parameter type " + this.parameter.getNestedParameterType().getSimpleName() + " is " + (isMissingAfterConversion() ? "present but converted to null" : "not present");
    }

    public final String getVariableName() {
        return this.variableName;
    }

    public final MethodParameter getParameter() {
        return this.parameter;
    }
}
