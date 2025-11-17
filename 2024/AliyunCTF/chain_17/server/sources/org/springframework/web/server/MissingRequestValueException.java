package org.springframework.web.server;

import org.springframework.core.MethodParameter;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/MissingRequestValueException.class */
public class MissingRequestValueException extends ServerWebInputException {
    private final String name;
    private final Class<?> type;
    private final String label;

    public MissingRequestValueException(String name, Class<?> type, String label, MethodParameter parameter) {
        super("Required " + label + " '" + name + "' is not present.", parameter, null, null, new Object[]{label, name});
        this.name = name;
        this.type = type;
        this.label = label;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getType() {
        return this.type;
    }

    public String getLabel() {
        return this.label;
    }
}
