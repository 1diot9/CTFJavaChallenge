package org.springframework.http.codec;

import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/HttpMessageDecoder.class */
public interface HttpMessageDecoder<T> extends Decoder<T> {
    Map<String, Object> getDecodeHints(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response);
}
