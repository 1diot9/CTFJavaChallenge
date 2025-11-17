package org.springframework.web.bind;

import org.springframework.core.MethodParameter;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/MissingRequestHeaderException.class */
public class MissingRequestHeaderException extends MissingRequestValueException {
    private final String headerName;
    private final MethodParameter parameter;

    public MissingRequestHeaderException(String headerName, MethodParameter parameter) {
        this(headerName, parameter, false);
    }

    public MissingRequestHeaderException(String headerName, MethodParameter parameter, boolean missingAfterConversion) {
        super("", missingAfterConversion, null, new Object[]{headerName});
        this.headerName = headerName;
        this.parameter = parameter;
        getBody().setDetail("Required header '" + this.headerName + "' is not present.");
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        String typeName = this.parameter.getNestedParameterType().getSimpleName();
        return "Required request header '" + this.headerName + "' for method parameter type " + typeName + " is " + (isMissingAfterConversion() ? "present but converted to null" : "not present");
    }

    public final String getHeaderName() {
        return this.headerName;
    }

    public final MethodParameter getParameter() {
        return this.parameter;
    }
}
