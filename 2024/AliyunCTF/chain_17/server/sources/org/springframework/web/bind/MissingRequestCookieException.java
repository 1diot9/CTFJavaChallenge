package org.springframework.web.bind;

import org.springframework.core.MethodParameter;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/MissingRequestCookieException.class */
public class MissingRequestCookieException extends MissingRequestValueException {
    private final String cookieName;
    private final MethodParameter parameter;

    public MissingRequestCookieException(String cookieName, MethodParameter parameter) {
        this(cookieName, parameter, false);
    }

    public MissingRequestCookieException(String cookieName, MethodParameter parameter, boolean missingAfterConversion) {
        super("", missingAfterConversion, null, new Object[]{cookieName});
        this.cookieName = cookieName;
        this.parameter = parameter;
        getBody().setDetail("Required cookie '" + this.cookieName + "' is not present.");
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "Required cookie '" + this.cookieName + "' for method parameter type " + this.parameter.getNestedParameterType().getSimpleName() + " is " + (isMissingAfterConversion() ? "present but converted to null" : "not present");
    }

    public final String getCookieName() {
        return this.cookieName;
    }

    public final MethodParameter getParameter() {
        return this.parameter;
    }
}
