package org.springframework.core.codec;

import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/codec/DecodingException.class */
public class DecodingException extends CodecException {
    public DecodingException(String msg) {
        super(msg);
    }

    public DecodingException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
