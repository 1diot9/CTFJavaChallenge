package org.springframework.http.codec;

import org.springframework.core.codec.Decoder;
import org.springframework.http.codec.CodecConfigurer;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/ClientCodecConfigurer.class */
public interface ClientCodecConfigurer extends CodecConfigurer {

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/ClientCodecConfigurer$ClientDefaultCodecs.class */
    public interface ClientDefaultCodecs extends CodecConfigurer.DefaultCodecs {
        void serverSentEventDecoder(Decoder<?> decoder);
    }

    @Override // org.springframework.http.codec.CodecConfigurer
    ClientDefaultCodecs defaultCodecs();

    @Override // org.springframework.http.codec.CodecConfigurer
    ClientCodecConfigurer clone();

    static ClientCodecConfigurer create() {
        return (ClientCodecConfigurer) CodecConfigurerFactory.create(ClientCodecConfigurer.class);
    }
}
