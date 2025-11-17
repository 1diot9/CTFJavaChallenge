package org.springframework.core.codec;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/codec/CodecException.class */
public class CodecException extends NestedRuntimeException {
    public CodecException(String msg) {
        super(msg);
    }

    public CodecException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
