package org.springframework.core.type.classreading;

import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/classreading/ClassFormatException.class */
public class ClassFormatException extends IOException {
    public ClassFormatException(String message) {
        super(message);
    }

    public ClassFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
