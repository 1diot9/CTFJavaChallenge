package org.springframework.http.codec;

import java.util.List;
import java.util.function.Consumer;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/CodecConfigurer.class */
public interface CodecConfigurer {

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/CodecConfigurer$CustomCodecs.class */
    public interface CustomCodecs {
        void register(Object codec);

        void registerWithDefaultConfig(Object codec);

        void registerWithDefaultConfig(Object codec, Consumer<DefaultCodecConfig> configConsumer);

        @Deprecated
        void decoder(Decoder<?> decoder);

        @Deprecated
        void encoder(Encoder<?> encoder);

        @Deprecated
        void reader(HttpMessageReader<?> reader);

        @Deprecated
        void writer(HttpMessageWriter<?> writer);

        @Deprecated
        void withDefaultCodecConfig(Consumer<DefaultCodecConfig> codecsConfigConsumer);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/CodecConfigurer$DefaultCodecConfig.class */
    public interface DefaultCodecConfig {
        @Nullable
        Integer maxInMemorySize();

        @Nullable
        Boolean isEnableLoggingRequestDetails();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/CodecConfigurer$DefaultCodecs.class */
    public interface DefaultCodecs {
        void jackson2JsonDecoder(Decoder<?> decoder);

        void jackson2JsonEncoder(Encoder<?> encoder);

        void jackson2SmileDecoder(Decoder<?> decoder);

        void jackson2SmileEncoder(Encoder<?> encoder);

        void protobufDecoder(Decoder<?> decoder);

        void protobufEncoder(Encoder<?> encoder);

        void jaxb2Decoder(Decoder<?> decoder);

        void jaxb2Encoder(Encoder<?> encoder);

        void kotlinSerializationCborDecoder(Decoder<?> decoder);

        void kotlinSerializationCborEncoder(Encoder<?> encoder);

        void kotlinSerializationJsonDecoder(Decoder<?> decoder);

        void kotlinSerializationJsonEncoder(Encoder<?> encoder);

        void kotlinSerializationProtobufDecoder(Decoder<?> decoder);

        void kotlinSerializationProtobufEncoder(Encoder<?> encoder);

        void configureDefaultCodec(Consumer<Object> codecConsumer);

        void maxInMemorySize(int byteCount);

        void enableLoggingRequestDetails(boolean enable);

        MultipartCodecs multipartCodecs();

        void multipartReader(HttpMessageReader<?> reader);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/CodecConfigurer$MultipartCodecs.class */
    public interface MultipartCodecs {
        MultipartCodecs encoder(Encoder<?> encoder);

        MultipartCodecs writer(HttpMessageWriter<?> writer);
    }

    DefaultCodecs defaultCodecs();

    CustomCodecs customCodecs();

    void registerDefaults(boolean registerDefaults);

    List<HttpMessageReader<?>> getReaders();

    List<HttpMessageWriter<?>> getWriters();

    CodecConfigurer clone();
}
