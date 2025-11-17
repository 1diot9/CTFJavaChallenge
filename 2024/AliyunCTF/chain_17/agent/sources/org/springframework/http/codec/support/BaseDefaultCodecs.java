package org.springframework.http.codec.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.core.codec.AbstractDataBufferDecoder;
import org.springframework.core.codec.ByteArrayDecoder;
import org.springframework.core.codec.ByteArrayEncoder;
import org.springframework.core.codec.ByteBufferDecoder;
import org.springframework.core.codec.ByteBufferEncoder;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.codec.DataBufferDecoder;
import org.springframework.core.codec.DataBufferEncoder;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.core.codec.Netty5BufferDecoder;
import org.springframework.core.codec.Netty5BufferEncoder;
import org.springframework.core.codec.NettyByteBufDecoder;
import org.springframework.core.codec.NettyByteBufEncoder;
import org.springframework.core.codec.ResourceDecoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.FormHttpMessageReader;
import org.springframework.http.codec.FormHttpMessageWriter;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ResourceHttpMessageReader;
import org.springframework.http.codec.ResourceHttpMessageWriter;
import org.springframework.http.codec.ServerSentEventHttpMessageReader;
import org.springframework.http.codec.ServerSentEventHttpMessageWriter;
import org.springframework.http.codec.cbor.KotlinSerializationCborDecoder;
import org.springframework.http.codec.cbor.KotlinSerializationCborEncoder;
import org.springframework.http.codec.json.AbstractJackson2Decoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.codec.json.Jackson2SmileDecoder;
import org.springframework.http.codec.json.Jackson2SmileEncoder;
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder;
import org.springframework.http.codec.json.KotlinSerializationJsonEncoder;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageWriter;
import org.springframework.http.codec.multipart.PartEventHttpMessageReader;
import org.springframework.http.codec.multipart.PartEventHttpMessageWriter;
import org.springframework.http.codec.multipart.PartHttpMessageWriter;
import org.springframework.http.codec.protobuf.KotlinSerializationProtobufDecoder;
import org.springframework.http.codec.protobuf.KotlinSerializationProtobufEncoder;
import org.springframework.http.codec.protobuf.ProtobufDecoder;
import org.springframework.http.codec.protobuf.ProtobufEncoder;
import org.springframework.http.codec.protobuf.ProtobufHttpMessageWriter;
import org.springframework.http.codec.support.BaseCodecConfigurer;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/support/BaseDefaultCodecs.class */
public class BaseDefaultCodecs implements CodecConfigurer.DefaultCodecs, CodecConfigurer.DefaultCodecConfig {
    static final boolean jackson2Present;
    private static final boolean jackson2SmilePresent;
    private static final boolean jaxb2Present;
    private static final boolean protobufPresent;
    static final boolean synchronossMultipartPresent;
    static final boolean nettyByteBufPresent;
    static final boolean netty5BufferPresent;
    static final boolean kotlinSerializationCborPresent;
    static final boolean kotlinSerializationJsonPresent;
    static final boolean kotlinSerializationProtobufPresent;

    @Nullable
    private Decoder<?> jackson2JsonDecoder;

    @Nullable
    private Encoder<?> jackson2JsonEncoder;

    @Nullable
    private Encoder<?> jackson2SmileEncoder;

    @Nullable
    private Decoder<?> jackson2SmileDecoder;

    @Nullable
    private Decoder<?> protobufDecoder;

    @Nullable
    private Encoder<?> protobufEncoder;

    @Nullable
    private Decoder<?> jaxb2Decoder;

    @Nullable
    private Encoder<?> jaxb2Encoder;

    @Nullable
    private Decoder<?> kotlinSerializationCborDecoder;

    @Nullable
    private Encoder<?> kotlinSerializationCborEncoder;

    @Nullable
    private Decoder<?> kotlinSerializationJsonDecoder;

    @Nullable
    private Encoder<?> kotlinSerializationJsonEncoder;

    @Nullable
    private Decoder<?> kotlinSerializationProtobufDecoder;

    @Nullable
    private Encoder<?> kotlinSerializationProtobufEncoder;

    @Nullable
    private DefaultMultipartCodecs multipartCodecs;

    @Nullable
    private Supplier<List<HttpMessageWriter<?>>> partWritersSupplier;

    @Nullable
    private HttpMessageReader<?> multipartReader;

    @Nullable
    private Consumer<Object> codecConsumer;

    @Nullable
    private Integer maxInMemorySize;

    @Nullable
    private Boolean enableLoggingRequestDetails;
    private boolean registerDefaults;
    private final List<HttpMessageReader<?>> typedReaders;
    private final List<HttpMessageReader<?>> objectReaders;
    private final List<HttpMessageWriter<?>> typedWriters;
    private final List<HttpMessageWriter<?>> objectWriters;

    static {
        ClassLoader classLoader = BaseCodecConfigurer.class.getClassLoader();
        jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader);
        jackson2SmilePresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.smile.SmileFactory", classLoader);
        jaxb2Present = ClassUtils.isPresent("jakarta.xml.bind.Binder", classLoader);
        protobufPresent = ClassUtils.isPresent("com.google.protobuf.Message", classLoader);
        synchronossMultipartPresent = ClassUtils.isPresent("org.synchronoss.cloud.nio.multipart.NioMultipartParser", classLoader);
        nettyByteBufPresent = ClassUtils.isPresent("io.netty.buffer.ByteBuf", classLoader);
        netty5BufferPresent = ClassUtils.isPresent("io.netty5.buffer.Buffer", classLoader);
        kotlinSerializationCborPresent = ClassUtils.isPresent("kotlinx.serialization.cbor.Cbor", classLoader);
        kotlinSerializationJsonPresent = ClassUtils.isPresent("kotlinx.serialization.json.Json", classLoader);
        kotlinSerializationProtobufPresent = ClassUtils.isPresent("kotlinx.serialization.protobuf.ProtoBuf", classLoader);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseDefaultCodecs() {
        this.registerDefaults = true;
        this.typedReaders = new ArrayList();
        this.objectReaders = new ArrayList();
        this.typedWriters = new ArrayList();
        this.objectWriters = new ArrayList();
        initReaders();
        initWriters();
    }

    protected void initReaders() {
        initTypedReaders();
        initObjectReaders();
    }

    protected void initWriters() {
        initTypedWriters();
        initObjectWriters();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseDefaultCodecs(BaseDefaultCodecs other) {
        this.registerDefaults = true;
        this.typedReaders = new ArrayList();
        this.objectReaders = new ArrayList();
        this.typedWriters = new ArrayList();
        this.objectWriters = new ArrayList();
        this.jackson2JsonDecoder = other.jackson2JsonDecoder;
        this.jackson2JsonEncoder = other.jackson2JsonEncoder;
        this.jackson2SmileDecoder = other.jackson2SmileDecoder;
        this.jackson2SmileEncoder = other.jackson2SmileEncoder;
        this.protobufDecoder = other.protobufDecoder;
        this.protobufEncoder = other.protobufEncoder;
        this.jaxb2Decoder = other.jaxb2Decoder;
        this.jaxb2Encoder = other.jaxb2Encoder;
        this.kotlinSerializationCborDecoder = other.kotlinSerializationCborDecoder;
        this.kotlinSerializationCborEncoder = other.kotlinSerializationCborEncoder;
        this.kotlinSerializationJsonDecoder = other.kotlinSerializationJsonDecoder;
        this.kotlinSerializationJsonEncoder = other.kotlinSerializationJsonEncoder;
        this.kotlinSerializationProtobufDecoder = other.kotlinSerializationProtobufDecoder;
        this.kotlinSerializationProtobufEncoder = other.kotlinSerializationProtobufEncoder;
        this.multipartCodecs = other.multipartCodecs != null ? new DefaultMultipartCodecs(other.multipartCodecs) : null;
        this.multipartReader = other.multipartReader;
        this.codecConsumer = other.codecConsumer;
        this.maxInMemorySize = other.maxInMemorySize;
        this.enableLoggingRequestDetails = other.enableLoggingRequestDetails;
        this.registerDefaults = other.registerDefaults;
        this.typedReaders.addAll(other.typedReaders);
        this.objectReaders.addAll(other.objectReaders);
        this.typedWriters.addAll(other.typedWriters);
        this.objectWriters.addAll(other.objectWriters);
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void jackson2JsonDecoder(Decoder<?> decoder) {
        this.jackson2JsonDecoder = decoder;
        initObjectReaders();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void jackson2JsonEncoder(Encoder<?> encoder) {
        this.jackson2JsonEncoder = encoder;
        initObjectWriters();
        initTypedWriters();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void jackson2SmileDecoder(Decoder<?> decoder) {
        this.jackson2SmileDecoder = decoder;
        initObjectReaders();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void jackson2SmileEncoder(Encoder<?> encoder) {
        this.jackson2SmileEncoder = encoder;
        initObjectWriters();
        initTypedWriters();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void protobufDecoder(Decoder<?> decoder) {
        this.protobufDecoder = decoder;
        initTypedReaders();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void protobufEncoder(Encoder<?> encoder) {
        this.protobufEncoder = encoder;
        initTypedWriters();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void jaxb2Decoder(Decoder<?> decoder) {
        this.jaxb2Decoder = decoder;
        initObjectReaders();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void jaxb2Encoder(Encoder<?> encoder) {
        this.jaxb2Encoder = encoder;
        initObjectWriters();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void kotlinSerializationCborDecoder(Decoder<?> decoder) {
        this.kotlinSerializationCborDecoder = decoder;
        initObjectReaders();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void kotlinSerializationCborEncoder(Encoder<?> encoder) {
        this.kotlinSerializationCborEncoder = encoder;
        initObjectWriters();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void kotlinSerializationJsonDecoder(Decoder<?> decoder) {
        this.kotlinSerializationJsonDecoder = decoder;
        initObjectReaders();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void kotlinSerializationJsonEncoder(Encoder<?> encoder) {
        this.kotlinSerializationJsonEncoder = encoder;
        initObjectWriters();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void kotlinSerializationProtobufDecoder(Decoder<?> decoder) {
        this.kotlinSerializationProtobufDecoder = decoder;
        initObjectReaders();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void kotlinSerializationProtobufEncoder(Encoder<?> encoder) {
        this.kotlinSerializationProtobufEncoder = encoder;
        initObjectWriters();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void configureDefaultCodec(Consumer<Object> codecConsumer) {
        this.codecConsumer = this.codecConsumer != null ? this.codecConsumer.andThen(codecConsumer) : codecConsumer;
        initReaders();
        initWriters();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void maxInMemorySize(int byteCount) {
        if (!ObjectUtils.nullSafeEquals(this.maxInMemorySize, Integer.valueOf(byteCount))) {
            this.maxInMemorySize = Integer.valueOf(byteCount);
            initReaders();
        }
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecConfig
    @Nullable
    public Integer maxInMemorySize() {
        return this.maxInMemorySize;
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void enableLoggingRequestDetails(boolean enable) {
        if (!ObjectUtils.nullSafeEquals(this.enableLoggingRequestDetails, Boolean.valueOf(enable))) {
            this.enableLoggingRequestDetails = Boolean.valueOf(enable);
            initReaders();
            initWriters();
        }
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public CodecConfigurer.MultipartCodecs multipartCodecs() {
        if (this.multipartCodecs == null) {
            this.multipartCodecs = new DefaultMultipartCodecs();
        }
        return this.multipartCodecs;
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void multipartReader(HttpMessageReader<?> multipartReader) {
        this.multipartReader = multipartReader;
        initTypedReaders();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPartWritersSupplier(Supplier<List<HttpMessageWriter<?>>> supplier) {
        this.partWritersSupplier = supplier;
        initTypedWriters();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecConfig
    @Nullable
    public Boolean isEnableLoggingRequestDetails() {
        return this.enableLoggingRequestDetails;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void registerDefaults(boolean registerDefaults) {
        if (this.registerDefaults != registerDefaults) {
            this.registerDefaults = registerDefaults;
            initReaders();
            initWriters();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final List<HttpMessageReader<?>> getTypedReaders() {
        return this.typedReaders;
    }

    protected void initTypedReaders() {
        this.typedReaders.clear();
        if (!this.registerDefaults) {
            return;
        }
        addCodec(this.typedReaders, new DecoderHttpMessageReader(new ByteArrayDecoder()));
        addCodec(this.typedReaders, new DecoderHttpMessageReader(new ByteBufferDecoder()));
        addCodec(this.typedReaders, new DecoderHttpMessageReader(new DataBufferDecoder()));
        if (nettyByteBufPresent) {
            addCodec(this.typedReaders, new DecoderHttpMessageReader(new NettyByteBufDecoder()));
        }
        if (netty5BufferPresent) {
            addCodec(this.typedReaders, new DecoderHttpMessageReader(new Netty5BufferDecoder()));
        }
        addCodec(this.typedReaders, new ResourceHttpMessageReader(new ResourceDecoder()));
        addCodec(this.typedReaders, new DecoderHttpMessageReader(StringDecoder.textPlainOnly()));
        if (protobufPresent) {
            addCodec(this.typedReaders, new DecoderHttpMessageReader(this.protobufDecoder != null ? (ProtobufDecoder) this.protobufDecoder : new ProtobufDecoder()));
        } else if (kotlinSerializationProtobufPresent) {
            addCodec(this.typedReaders, new DecoderHttpMessageReader(this.kotlinSerializationProtobufDecoder != null ? (KotlinSerializationProtobufDecoder) this.kotlinSerializationProtobufDecoder : new KotlinSerializationProtobufDecoder()));
        }
        addCodec(this.typedReaders, new FormHttpMessageReader());
        if (this.multipartReader != null) {
            addCodec(this.typedReaders, this.multipartReader);
        } else {
            DefaultPartHttpMessageReader partReader = new DefaultPartHttpMessageReader();
            addCodec(this.typedReaders, partReader);
            addCodec(this.typedReaders, new MultipartHttpMessageReader(partReader));
        }
        addCodec(this.typedReaders, new PartEventHttpMessageReader());
        extendTypedReaders(this.typedReaders);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <T> void addCodec(List<T> codecs, T codec) {
        initCodec(codec);
        codecs.add(codec);
    }

    private void initCodec(@Nullable Object codec) {
        if (codec instanceof DecoderHttpMessageReader) {
            DecoderHttpMessageReader<?> decoderHttpMessageReader = (DecoderHttpMessageReader) codec;
            codec = decoderHttpMessageReader.getDecoder();
        } else if (codec instanceof EncoderHttpMessageWriter) {
            EncoderHttpMessageWriter<?> encoderHttpMessageWriter = (EncoderHttpMessageWriter) codec;
            codec = encoderHttpMessageWriter.getEncoder();
        }
        if (codec == null) {
            return;
        }
        Integer size = this.maxInMemorySize;
        if (size != null) {
            if (codec instanceof AbstractDataBufferDecoder) {
                AbstractDataBufferDecoder<?> abstractDataBufferDecoder = (AbstractDataBufferDecoder) codec;
                abstractDataBufferDecoder.setMaxInMemorySize(size.intValue());
            }
            if (protobufPresent && (codec instanceof ProtobufDecoder)) {
                ProtobufDecoder protobufDec = (ProtobufDecoder) codec;
                protobufDec.setMaxMessageSize(size.intValue());
            }
            if (kotlinSerializationCborPresent && (codec instanceof KotlinSerializationCborDecoder)) {
                KotlinSerializationCborDecoder kotlinSerializationCborDec = (KotlinSerializationCborDecoder) codec;
                kotlinSerializationCborDec.setMaxInMemorySize(size.intValue());
            }
            if (kotlinSerializationJsonPresent && (codec instanceof KotlinSerializationJsonDecoder)) {
                KotlinSerializationJsonDecoder kotlinSerializationJsonDec = (KotlinSerializationJsonDecoder) codec;
                kotlinSerializationJsonDec.setMaxInMemorySize(size.intValue());
            }
            if (kotlinSerializationProtobufPresent && (codec instanceof KotlinSerializationProtobufDecoder)) {
                KotlinSerializationProtobufDecoder kotlinSerializationProtobufDec = (KotlinSerializationProtobufDecoder) codec;
                kotlinSerializationProtobufDec.setMaxInMemorySize(size.intValue());
            }
            if (jackson2Present && (codec instanceof AbstractJackson2Decoder)) {
                AbstractJackson2Decoder abstractJackson2Decoder = (AbstractJackson2Decoder) codec;
                abstractJackson2Decoder.setMaxInMemorySize(size.intValue());
            }
            if (jaxb2Present && (codec instanceof Jaxb2XmlDecoder)) {
                Jaxb2XmlDecoder jaxb2XmlDecoder = (Jaxb2XmlDecoder) codec;
                jaxb2XmlDecoder.setMaxInMemorySize(size.intValue());
            }
            if (codec instanceof FormHttpMessageReader) {
                FormHttpMessageReader formHttpMessageReader = (FormHttpMessageReader) codec;
                formHttpMessageReader.setMaxInMemorySize(size.intValue());
            }
            if (codec instanceof ServerSentEventHttpMessageReader) {
                ServerSentEventHttpMessageReader serverSentEventHttpMessageReader = (ServerSentEventHttpMessageReader) codec;
                serverSentEventHttpMessageReader.setMaxInMemorySize(size.intValue());
            }
            if (codec instanceof DefaultPartHttpMessageReader) {
                DefaultPartHttpMessageReader defaultPartHttpMessageReader = (DefaultPartHttpMessageReader) codec;
                defaultPartHttpMessageReader.setMaxInMemorySize(size.intValue());
            }
            if (codec instanceof PartEventHttpMessageReader) {
                PartEventHttpMessageReader partEventHttpMessageReader = (PartEventHttpMessageReader) codec;
                partEventHttpMessageReader.setMaxInMemorySize(size.intValue());
            }
        }
        Boolean enable = this.enableLoggingRequestDetails;
        if (enable != null) {
            if (codec instanceof FormHttpMessageReader) {
                FormHttpMessageReader formHttpMessageReader2 = (FormHttpMessageReader) codec;
                formHttpMessageReader2.setEnableLoggingRequestDetails(enable.booleanValue());
            }
            if (codec instanceof MultipartHttpMessageReader) {
                MultipartHttpMessageReader multipartHttpMessageReader = (MultipartHttpMessageReader) codec;
                multipartHttpMessageReader.setEnableLoggingRequestDetails(enable.booleanValue());
            }
            if (codec instanceof DefaultPartHttpMessageReader) {
                DefaultPartHttpMessageReader defaultPartHttpMessageReader2 = (DefaultPartHttpMessageReader) codec;
                defaultPartHttpMessageReader2.setEnableLoggingRequestDetails(enable.booleanValue());
            }
            if (codec instanceof PartEventHttpMessageReader) {
                PartEventHttpMessageReader partEventHttpMessageReader2 = (PartEventHttpMessageReader) codec;
                partEventHttpMessageReader2.setEnableLoggingRequestDetails(enable.booleanValue());
            }
            if (codec instanceof FormHttpMessageWriter) {
                FormHttpMessageWriter formHttpMessageWriter = (FormHttpMessageWriter) codec;
                formHttpMessageWriter.setEnableLoggingRequestDetails(enable.booleanValue());
            }
            if (codec instanceof MultipartHttpMessageWriter) {
                MultipartHttpMessageWriter multipartHttpMessageWriter = (MultipartHttpMessageWriter) codec;
                multipartHttpMessageWriter.setEnableLoggingRequestDetails(enable.booleanValue());
            }
        }
        if (this.codecConsumer != null) {
            this.codecConsumer.accept(codec);
        }
        if (codec instanceof MultipartHttpMessageReader) {
            MultipartHttpMessageReader multipartHttpMessageReader2 = (MultipartHttpMessageReader) codec;
            initCodec(multipartHttpMessageReader2.getPartReader());
            return;
        }
        if (codec instanceof MultipartHttpMessageWriter) {
            MultipartHttpMessageWriter multipartHttpMessageWriter2 = (MultipartHttpMessageWriter) codec;
            initCodec(multipartHttpMessageWriter2.getFormWriter());
        } else if (codec instanceof ServerSentEventHttpMessageReader) {
            ServerSentEventHttpMessageReader serverSentEventHttpMessageReader2 = (ServerSentEventHttpMessageReader) codec;
            initCodec(serverSentEventHttpMessageReader2.getDecoder());
        } else if (codec instanceof ServerSentEventHttpMessageWriter) {
            ServerSentEventHttpMessageWriter serverSentEventHttpMessageWriter = (ServerSentEventHttpMessageWriter) codec;
            initCodec(serverSentEventHttpMessageWriter.getEncoder());
        }
    }

    protected void extendTypedReaders(List<HttpMessageReader<?>> typedReaders) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final List<HttpMessageReader<?>> getObjectReaders() {
        return this.objectReaders;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initObjectReaders() {
        KotlinSerializationProtobufDecoder kotlinSerializationProtobufDecoder;
        KotlinSerializationCborDecoder kotlinSerializationCborDecoder;
        this.objectReaders.clear();
        if (!this.registerDefaults) {
            return;
        }
        if (kotlinSerializationCborPresent) {
            List<HttpMessageReader<?>> list = this.objectReaders;
            if (this.kotlinSerializationCborDecoder != null) {
                kotlinSerializationCborDecoder = (KotlinSerializationCborDecoder) this.kotlinSerializationCborDecoder;
            } else {
                kotlinSerializationCborDecoder = new KotlinSerializationCborDecoder();
            }
            addCodec(list, new DecoderHttpMessageReader(kotlinSerializationCborDecoder));
        }
        if (kotlinSerializationJsonPresent) {
            addCodec(this.objectReaders, new DecoderHttpMessageReader(getKotlinSerializationJsonDecoder()));
        }
        if (kotlinSerializationProtobufPresent) {
            List<HttpMessageReader<?>> list2 = this.objectReaders;
            if (this.kotlinSerializationProtobufDecoder != null) {
                kotlinSerializationProtobufDecoder = (KotlinSerializationProtobufDecoder) this.kotlinSerializationProtobufDecoder;
            } else {
                kotlinSerializationProtobufDecoder = new KotlinSerializationProtobufDecoder();
            }
            addCodec(list2, new DecoderHttpMessageReader(kotlinSerializationProtobufDecoder));
        }
        if (jackson2Present) {
            addCodec(this.objectReaders, new DecoderHttpMessageReader(getJackson2JsonDecoder()));
        }
        if (jackson2SmilePresent) {
            addCodec(this.objectReaders, new DecoderHttpMessageReader(this.jackson2SmileDecoder != null ? (Jackson2SmileDecoder) this.jackson2SmileDecoder : new Jackson2SmileDecoder()));
        }
        if (jaxb2Present) {
            addCodec(this.objectReaders, new DecoderHttpMessageReader(this.jaxb2Decoder != null ? (Jaxb2XmlDecoder) this.jaxb2Decoder : new Jaxb2XmlDecoder()));
        }
        extendObjectReaders(this.objectReaders);
    }

    protected void extendObjectReaders(List<HttpMessageReader<?>> objectReaders) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final List<HttpMessageReader<?>> getCatchAllReaders() {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        addCodec(arrayList, new DecoderHttpMessageReader(StringDecoder.allMimeTypes()));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final List<HttpMessageWriter<?>> getTypedWriters() {
        return this.typedWriters;
    }

    protected void initTypedWriters() {
        this.typedWriters.clear();
        if (!this.registerDefaults) {
            return;
        }
        this.typedWriters.addAll(getBaseTypedWriters());
        extendTypedWriters(this.typedWriters);
    }

    final List<HttpMessageWriter<?>> getBaseTypedWriters() {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        addCodec(arrayList, new EncoderHttpMessageWriter(new ByteArrayEncoder()));
        addCodec(arrayList, new EncoderHttpMessageWriter(new ByteBufferEncoder()));
        addCodec(arrayList, new EncoderHttpMessageWriter(new DataBufferEncoder()));
        if (nettyByteBufPresent) {
            addCodec(arrayList, new EncoderHttpMessageWriter(new NettyByteBufEncoder()));
        }
        if (netty5BufferPresent) {
            addCodec(arrayList, new EncoderHttpMessageWriter(new Netty5BufferEncoder()));
        }
        addCodec(arrayList, new ResourceHttpMessageWriter());
        addCodec(arrayList, new EncoderHttpMessageWriter(CharSequenceEncoder.textPlainOnly()));
        if (protobufPresent) {
            addCodec(arrayList, new ProtobufHttpMessageWriter(this.protobufEncoder != null ? (ProtobufEncoder) this.protobufEncoder : new ProtobufEncoder()));
        }
        addCodec(arrayList, new MultipartHttpMessageWriter((Supplier<List<HttpMessageWriter<?>>>) this::getPartWriters, new FormHttpMessageWriter()));
        addCodec(arrayList, new PartEventHttpMessageWriter());
        addCodec(arrayList, new PartHttpMessageWriter());
        return arrayList;
    }

    private List<HttpMessageWriter<?>> getPartWriters() {
        if (this.multipartCodecs != null) {
            return this.multipartCodecs.getWriters();
        }
        if (this.partWritersSupplier != null) {
            return this.partWritersSupplier.get();
        }
        return Collections.emptyList();
    }

    protected void extendTypedWriters(List<HttpMessageWriter<?>> typedWriters) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final List<HttpMessageWriter<?>> getObjectWriters() {
        return this.objectWriters;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initObjectWriters() {
        this.objectWriters.clear();
        if (!this.registerDefaults) {
            return;
        }
        this.objectWriters.addAll(getBaseObjectWriters());
        extendObjectWriters(this.objectWriters);
    }

    final List<HttpMessageWriter<?>> getBaseObjectWriters() {
        KotlinSerializationProtobufEncoder kotlinSerializationProtobufEncoder;
        KotlinSerializationCborEncoder kotlinSerializationCborEncoder;
        ArrayList arrayList = new ArrayList();
        if (kotlinSerializationCborPresent) {
            if (this.kotlinSerializationCborEncoder != null) {
                kotlinSerializationCborEncoder = (KotlinSerializationCborEncoder) this.kotlinSerializationCborEncoder;
            } else {
                kotlinSerializationCborEncoder = new KotlinSerializationCborEncoder();
            }
            addCodec(arrayList, new EncoderHttpMessageWriter(kotlinSerializationCborEncoder));
        }
        if (kotlinSerializationJsonPresent) {
            addCodec(arrayList, new EncoderHttpMessageWriter(getKotlinSerializationJsonEncoder()));
        }
        if (kotlinSerializationProtobufPresent) {
            if (this.kotlinSerializationProtobufEncoder != null) {
                kotlinSerializationProtobufEncoder = (KotlinSerializationProtobufEncoder) this.kotlinSerializationProtobufEncoder;
            } else {
                kotlinSerializationProtobufEncoder = new KotlinSerializationProtobufEncoder();
            }
            addCodec(arrayList, new EncoderHttpMessageWriter(kotlinSerializationProtobufEncoder));
        }
        if (jackson2Present) {
            addCodec(arrayList, new EncoderHttpMessageWriter(getJackson2JsonEncoder()));
        }
        if (jackson2SmilePresent) {
            addCodec(arrayList, new EncoderHttpMessageWriter(this.jackson2SmileEncoder != null ? (Jackson2SmileEncoder) this.jackson2SmileEncoder : new Jackson2SmileEncoder()));
        }
        if (jaxb2Present) {
            addCodec(arrayList, new EncoderHttpMessageWriter(this.jaxb2Encoder != null ? (Jaxb2XmlEncoder) this.jaxb2Encoder : new Jaxb2XmlEncoder()));
        }
        return arrayList;
    }

    protected void extendObjectWriters(List<HttpMessageWriter<?>> objectWriters) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<HttpMessageWriter<?>> getCatchAllWriters() {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        List<HttpMessageWriter<?>> result = new ArrayList<>();
        result.add(new EncoderHttpMessageWriter<>(CharSequenceEncoder.allMimeTypes()));
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyDefaultConfig(BaseCodecConfigurer.DefaultCustomCodecs customCodecs) {
        applyDefaultConfig(customCodecs.getTypedReaders());
        applyDefaultConfig(customCodecs.getObjectReaders());
        applyDefaultConfig(customCodecs.getTypedWriters());
        applyDefaultConfig(customCodecs.getObjectWriters());
        customCodecs.getDefaultConfigConsumers().forEach(consumer -> {
            consumer.accept(this);
        });
    }

    private void applyDefaultConfig(Map<?, Boolean> readers) {
        readers.entrySet().stream().filter((v0) -> {
            return v0.getValue();
        }).map((v0) -> {
            return v0.getKey();
        }).forEach(this::initCodec);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Decoder<?> getJackson2JsonDecoder() {
        if (this.jackson2JsonDecoder == null) {
            this.jackson2JsonDecoder = new Jackson2JsonDecoder();
        }
        return this.jackson2JsonDecoder;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Encoder<?> getJackson2JsonEncoder() {
        if (this.jackson2JsonEncoder == null) {
            this.jackson2JsonEncoder = new Jackson2JsonEncoder();
        }
        return this.jackson2JsonEncoder;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Decoder<?> getKotlinSerializationJsonDecoder() {
        if (this.kotlinSerializationJsonDecoder == null) {
            this.kotlinSerializationJsonDecoder = new KotlinSerializationJsonDecoder();
        }
        return this.kotlinSerializationJsonDecoder;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Encoder<?> getKotlinSerializationJsonEncoder() {
        if (this.kotlinSerializationJsonEncoder == null) {
            this.kotlinSerializationJsonEncoder = new KotlinSerializationJsonEncoder();
        }
        return this.kotlinSerializationJsonEncoder;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/support/BaseDefaultCodecs$DefaultMultipartCodecs.class */
    protected class DefaultMultipartCodecs implements CodecConfigurer.MultipartCodecs {
        private final List<HttpMessageWriter<?>> writers = new ArrayList();

        DefaultMultipartCodecs() {
        }

        DefaultMultipartCodecs(DefaultMultipartCodecs other) {
            this.writers.addAll(other.writers);
        }

        @Override // org.springframework.http.codec.CodecConfigurer.MultipartCodecs
        public CodecConfigurer.MultipartCodecs encoder(Encoder<?> encoder) {
            writer(new EncoderHttpMessageWriter(encoder));
            BaseDefaultCodecs.this.initTypedWriters();
            return this;
        }

        @Override // org.springframework.http.codec.CodecConfigurer.MultipartCodecs
        public CodecConfigurer.MultipartCodecs writer(HttpMessageWriter<?> writer) {
            this.writers.add(writer);
            BaseDefaultCodecs.this.initTypedWriters();
            return this;
        }

        List<HttpMessageWriter<?>> getWriters() {
            return this.writers;
        }
    }
}
