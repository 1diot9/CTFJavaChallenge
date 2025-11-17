package org.springframework.http.codec;

import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.CodecConfigurer;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/ServerCodecConfigurer.class */
public interface ServerCodecConfigurer extends CodecConfigurer {

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/ServerCodecConfigurer$ServerDefaultCodecs.class */
    public interface ServerDefaultCodecs extends CodecConfigurer.DefaultCodecs {
        void serverSentEventEncoder(Encoder<?> encoder);
    }

    @Override // org.springframework.http.codec.CodecConfigurer
    ServerDefaultCodecs defaultCodecs();

    @Override // org.springframework.http.codec.CodecConfigurer
    ServerCodecConfigurer clone();

    static ServerCodecConfigurer create() {
        return (ServerCodecConfigurer) CodecConfigurerFactory.create(ServerCodecConfigurer.class);
    }
}
