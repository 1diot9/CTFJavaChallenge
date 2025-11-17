package org.springframework.web.server;

import java.lang.reflect.Method;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/ServerErrorException.class */
public class ServerErrorException extends ResponseStatusException {

    @Nullable
    private final Method handlerMethod;

    @Nullable
    private final MethodParameter parameter;

    public ServerErrorException(String reason, @Nullable Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, reason, cause, null, new Object[]{reason});
        this.handlerMethod = null;
        this.parameter = null;
    }

    public ServerErrorException(String reason, Method handlerMethod, @Nullable Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, reason, cause, null, new Object[]{reason});
        this.handlerMethod = handlerMethod;
        this.parameter = null;
    }

    public ServerErrorException(String reason, MethodParameter parameter, @Nullable Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, reason, cause, null, new Object[]{reason});
        this.handlerMethod = parameter.getMethod();
        this.parameter = parameter;
    }

    @Nullable
    public Method getHandlerMethod() {
        return this.handlerMethod;
    }

    @Nullable
    public MethodParameter getMethodParameter() {
        return this.parameter;
    }
}
