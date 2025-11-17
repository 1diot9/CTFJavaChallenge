package org.springframework.http.codec.support;

import java.util.List;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.ServerSentEventHttpMessageWriter;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/support/ServerDefaultCodecsImpl.class */
class ServerDefaultCodecsImpl extends BaseDefaultCodecs implements ServerCodecConfigurer.ServerDefaultCodecs {

    @Nullable
    private Encoder<?> sseEncoder;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ServerDefaultCodecsImpl() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ServerDefaultCodecsImpl(ServerDefaultCodecsImpl other) {
        super(other);
        this.sseEncoder = other.sseEncoder;
    }

    @Override // org.springframework.http.codec.ServerCodecConfigurer.ServerDefaultCodecs
    public void serverSentEventEncoder(Encoder<?> encoder) {
        this.sseEncoder = encoder;
        initObjectWriters();
    }

    @Override // org.springframework.http.codec.support.BaseDefaultCodecs
    protected void extendObjectWriters(List<HttpMessageWriter<?>> objectWriters) {
        objectWriters.add(new ServerSentEventHttpMessageWriter(getSseEncoder()));
    }

    @Nullable
    private Encoder<?> getSseEncoder() {
        if (this.sseEncoder != null) {
            return this.sseEncoder;
        }
        if (jackson2Present) {
            return getJackson2JsonEncoder();
        }
        if (kotlinSerializationJsonPresent) {
            return getKotlinSerializationJsonEncoder();
        }
        return null;
    }
}
