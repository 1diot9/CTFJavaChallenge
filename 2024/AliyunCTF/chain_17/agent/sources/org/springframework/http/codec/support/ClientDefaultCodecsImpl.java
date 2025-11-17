package org.springframework.http.codec.support;

import java.util.List;
import org.springframework.core.codec.Decoder;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.ServerSentEventHttpMessageReader;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/support/ClientDefaultCodecsImpl.class */
class ClientDefaultCodecsImpl extends BaseDefaultCodecs implements ClientCodecConfigurer.ClientDefaultCodecs {

    @Nullable
    private Decoder<?> sseDecoder;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ClientDefaultCodecsImpl() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ClientDefaultCodecsImpl(ClientDefaultCodecsImpl other) {
        super(other);
        this.sseDecoder = other.sseDecoder;
    }

    @Override // org.springframework.http.codec.ClientCodecConfigurer.ClientDefaultCodecs
    public void serverSentEventDecoder(Decoder<?> decoder) {
        this.sseDecoder = decoder;
        initObjectReaders();
    }

    @Override // org.springframework.http.codec.support.BaseDefaultCodecs
    protected void extendObjectReaders(List<HttpMessageReader<?>> objectReaders) {
        Decoder<?> kotlinSerializationJsonDecoder;
        if (this.sseDecoder != null) {
            kotlinSerializationJsonDecoder = this.sseDecoder;
        } else if (jackson2Present) {
            kotlinSerializationJsonDecoder = getJackson2JsonDecoder();
        } else {
            kotlinSerializationJsonDecoder = kotlinSerializationJsonPresent ? getKotlinSerializationJsonDecoder() : null;
        }
        Decoder<?> decoder = kotlinSerializationJsonDecoder;
        addCodec(objectReaders, new ServerSentEventHttpMessageReader(decoder));
    }
}
