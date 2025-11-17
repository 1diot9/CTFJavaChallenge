package org.springframework.core.codec;

import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/codec/EncodingException.class */
public class EncodingException extends CodecException {
    public EncodingException(String msg) {
        super(msg);
    }

    public EncodingException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
