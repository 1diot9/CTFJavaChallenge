package org.springframework.http.codec.support;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/support/BaseCodecConfigurer.class */
abstract class BaseCodecConfigurer implements CodecConfigurer {
    protected final BaseDefaultCodecs defaultCodecs;
    protected final DefaultCustomCodecs customCodecs;

    protected abstract BaseDefaultCodecs cloneDefaultCodecs();

    @Override // 
    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public abstract CodecConfigurer mo2608clone();

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseCodecConfigurer(BaseDefaultCodecs defaultCodecs) {
        Assert.notNull(defaultCodecs, "'defaultCodecs' is required");
        this.defaultCodecs = defaultCodecs;
        this.customCodecs = new DefaultCustomCodecs();
        this.defaultCodecs.setPartWritersSupplier(this::getWriters);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseCodecConfigurer(BaseCodecConfigurer other) {
        this.defaultCodecs = other.cloneDefaultCodecs();
        this.customCodecs = new DefaultCustomCodecs(other.customCodecs);
        this.defaultCodecs.setPartWritersSupplier(this::getWriters);
    }

    @Override // org.springframework.http.codec.CodecConfigurer
    public CodecConfigurer.DefaultCodecs defaultCodecs() {
        return this.defaultCodecs;
    }

    @Override // org.springframework.http.codec.CodecConfigurer
    public void registerDefaults(boolean shouldRegister) {
        this.defaultCodecs.registerDefaults(shouldRegister);
    }

    @Override // org.springframework.http.codec.CodecConfigurer
    public CodecConfigurer.CustomCodecs customCodecs() {
        return this.customCodecs;
    }

    @Override // org.springframework.http.codec.CodecConfigurer
    public List<HttpMessageReader<?>> getReaders() {
        this.defaultCodecs.applyDefaultConfig(this.customCodecs);
        List<HttpMessageReader<?>> result = new ArrayList<>();
        result.addAll(this.customCodecs.getTypedReaders().keySet());
        result.addAll(this.defaultCodecs.getTypedReaders());
        result.addAll(this.customCodecs.getObjectReaders().keySet());
        result.addAll(this.defaultCodecs.getObjectReaders());
        result.addAll(this.defaultCodecs.getCatchAllReaders());
        return result;
    }

    @Override // org.springframework.http.codec.CodecConfigurer
    public List<HttpMessageWriter<?>> getWriters() {
        this.defaultCodecs.applyDefaultConfig(this.customCodecs);
        List<HttpMessageWriter<?>> result = new ArrayList<>();
        result.addAll(this.customCodecs.getTypedWriters().keySet());
        result.addAll(this.defaultCodecs.getTypedWriters());
        result.addAll(this.customCodecs.getObjectWriters().keySet());
        result.addAll(this.defaultCodecs.getObjectWriters());
        result.addAll(this.defaultCodecs.getCatchAllWriters());
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/support/BaseCodecConfigurer$DefaultCustomCodecs.class */
    public static final class DefaultCustomCodecs implements CodecConfigurer.CustomCodecs {
        private final Map<HttpMessageReader<?>, Boolean> typedReaders = new LinkedHashMap(4);
        private final Map<HttpMessageWriter<?>, Boolean> typedWriters = new LinkedHashMap(4);
        private final Map<HttpMessageReader<?>, Boolean> objectReaders = new LinkedHashMap(4);
        private final Map<HttpMessageWriter<?>, Boolean> objectWriters = new LinkedHashMap(4);
        private final List<Consumer<CodecConfigurer.DefaultCodecConfig>> defaultConfigConsumers = new ArrayList(4);

        DefaultCustomCodecs() {
        }

        DefaultCustomCodecs(DefaultCustomCodecs other) {
            this.typedReaders.putAll(other.typedReaders);
            this.typedWriters.putAll(other.typedWriters);
            this.objectReaders.putAll(other.objectReaders);
            this.objectWriters.putAll(other.objectWriters);
        }

        @Override // org.springframework.http.codec.CodecConfigurer.CustomCodecs
        public void register(Object codec) {
            addCodec(codec, false);
        }

        @Override // org.springframework.http.codec.CodecConfigurer.CustomCodecs
        public void registerWithDefaultConfig(Object codec) {
            addCodec(codec, true);
        }

        @Override // org.springframework.http.codec.CodecConfigurer.CustomCodecs
        public void registerWithDefaultConfig(Object codec, Consumer<CodecConfigurer.DefaultCodecConfig> configConsumer) {
            addCodec(codec, false);
            this.defaultConfigConsumers.add(configConsumer);
        }

        @Override // org.springframework.http.codec.CodecConfigurer.CustomCodecs
        public void decoder(Decoder<?> decoder) {
            addCodec(decoder, false);
        }

        @Override // org.springframework.http.codec.CodecConfigurer.CustomCodecs
        public void encoder(Encoder<?> encoder) {
            addCodec(encoder, false);
        }

        @Override // org.springframework.http.codec.CodecConfigurer.CustomCodecs
        public void reader(HttpMessageReader<?> reader) {
            addCodec(reader, false);
        }

        @Override // org.springframework.http.codec.CodecConfigurer.CustomCodecs
        public void writer(HttpMessageWriter<?> writer) {
            addCodec(writer, false);
        }

        @Override // org.springframework.http.codec.CodecConfigurer.CustomCodecs
        public void withDefaultCodecConfig(Consumer<CodecConfigurer.DefaultCodecConfig> codecsConfigConsumer) {
            this.defaultConfigConsumers.add(codecsConfigConsumer);
        }

        private void addCodec(Object codec, boolean applyDefaultConfig) {
            if (codec instanceof Decoder) {
                Decoder<?> decoder = (Decoder) codec;
                codec = new DecoderHttpMessageReader(decoder);
            } else if (codec instanceof Encoder) {
                Encoder<?> encoder = (Encoder) codec;
                codec = new EncoderHttpMessageWriter(encoder);
            }
            if (codec instanceof HttpMessageReader) {
                HttpMessageReader<?> reader = (HttpMessageReader) codec;
                boolean canReadToObject = reader.canRead(ResolvableType.forClass(Object.class), null);
                (canReadToObject ? this.objectReaders : this.typedReaders).put(reader, Boolean.valueOf(applyDefaultConfig));
            } else {
                if (codec instanceof HttpMessageWriter) {
                    HttpMessageWriter<?> writer = (HttpMessageWriter) codec;
                    boolean canWriteObject = writer.canWrite(ResolvableType.forClass(Object.class), null);
                    (canWriteObject ? this.objectWriters : this.typedWriters).put(writer, Boolean.valueOf(applyDefaultConfig));
                    return;
                }
                throw new IllegalArgumentException("Unexpected codec type: " + codec.getClass().getName());
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Map<HttpMessageReader<?>, Boolean> getTypedReaders() {
            return this.typedReaders;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Map<HttpMessageWriter<?>, Boolean> getTypedWriters() {
            return this.typedWriters;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Map<HttpMessageReader<?>, Boolean> getObjectReaders() {
            return this.objectReaders;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Map<HttpMessageWriter<?>, Boolean> getObjectWriters() {
            return this.objectWriters;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public List<Consumer<CodecConfigurer.DefaultCodecConfig>> getDefaultConfigConsumers() {
            return this.defaultConfigConsumers;
        }
    }
}
