package org.springframework.web.bind;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/MissingServletRequestParameterException.class */
public class MissingServletRequestParameterException extends MissingRequestValueException {
    private final String parameterName;
    private final String parameterType;

    @Nullable
    private final MethodParameter parameter;

    public MissingServletRequestParameterException(String parameterName, String parameterType) {
        super("", false, null, new Object[]{parameterName});
        this.parameterName = parameterName;
        this.parameterType = parameterType;
        this.parameter = null;
        getBody().setDetail(initBodyDetail(this.parameterName));
    }

    public MissingServletRequestParameterException(String parameterName, MethodParameter parameter, boolean missingAfterConversion) {
        super("", missingAfterConversion, null, new Object[]{parameterName});
        this.parameterName = parameterName;
        this.parameterType = parameter.getNestedParameterType().getSimpleName();
        this.parameter = parameter;
        getBody().setDetail(initBodyDetail(this.parameterName));
    }

    @Deprecated(since = "6.1", forRemoval = true)
    public MissingServletRequestParameterException(String parameterName, String parameterType, boolean missingAfterConversion) {
        super("", missingAfterConversion, null, new Object[]{parameterName});
        this.parameterName = parameterName;
        this.parameterType = parameterType;
        this.parameter = null;
        getBody().setDetail(initBodyDetail(this.parameterName));
    }

    private static String initBodyDetail(String name) {
        return "Required parameter '" + name + "' is not present.";
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "Required request parameter '" + this.parameterName + "' for method parameter type " + this.parameterType + " is " + (isMissingAfterConversion() ? "present but converted to null" : "not present");
    }

    public final String getParameterName() {
        return this.parameterName;
    }

    public final String getParameterType() {
        return this.parameterType;
    }

    @Nullable
    public MethodParameter getMethodParameter() {
        return this.parameter;
    }
}
