package org.springframework.web.server;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/ServerWebInputException.class */
public class ServerWebInputException extends ResponseStatusException {

    @Nullable
    private final MethodParameter parameter;

    public ServerWebInputException(String reason) {
        this(reason, null, null);
    }

    public ServerWebInputException(String reason, @Nullable MethodParameter parameter) {
        this(reason, parameter, null);
    }

    public ServerWebInputException(String reason, @Nullable MethodParameter parameter, @Nullable Throwable cause) {
        this(reason, parameter, cause, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ServerWebInputException(String reason, @Nullable MethodParameter parameter, @Nullable Throwable cause, @Nullable String messageDetailCode, @Nullable Object[] messageDetailArguments) {
        super(HttpStatus.BAD_REQUEST, reason, cause, messageDetailCode, messageDetailArguments);
        this.parameter = parameter;
    }

    @Nullable
    public MethodParameter getMethodParameter() {
        return this.parameter;
    }
}
