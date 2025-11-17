package org.springframework.http.codec.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.codec.Encoder;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.HttpMessageEncoder;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/protobuf/ProtobufHttpMessageWriter.class */
public class ProtobufHttpMessageWriter extends EncoderHttpMessageWriter<Message> {
    private static final String X_PROTOBUF_SCHEMA_HEADER = "X-Protobuf-Schema";
    private static final String X_PROTOBUF_MESSAGE_HEADER = "X-Protobuf-Message";
    private static final ConcurrentMap<Class<?>, Method> methodCache = new ConcurrentReferenceHashMap();

    public ProtobufHttpMessageWriter() {
        super(new ProtobufEncoder());
    }

    public ProtobufHttpMessageWriter(Encoder<Message> encoder) {
        super(encoder);
    }

    @Override // org.springframework.http.codec.EncoderHttpMessageWriter, org.springframework.http.codec.HttpMessageWriter
    public Mono<Void> write(Publisher<? extends Message> inputStream, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        try {
            Message.Builder builder = getMessageBuilder(elementType.toClass());
            Descriptors.Descriptor descriptor = builder.getDescriptorForType();
            message.getHeaders().add("X-Protobuf-Schema", descriptor.getFile().getName());
            message.getHeaders().add("X-Protobuf-Message", descriptor.getFullName());
            if (inputStream instanceof Flux) {
                if (mediaType == null) {
                    message.getHeaders().setContentType(((HttpMessageEncoder) getEncoder()).getStreamingMediaTypes().get(0));
                } else if (!"true".equals(mediaType.getParameters().get("delimited"))) {
                    Map<String, String> parameters = new HashMap<>(mediaType.getParameters());
                    parameters.put("delimited", "true");
                    message.getHeaders().setContentType(new MediaType(mediaType.getType(), mediaType.getSubtype(), parameters));
                }
            }
            return super.write(inputStream, elementType, mediaType, message, hints);
        } catch (Exception ex) {
            return Mono.error(new DecodingException("Could not read Protobuf message: " + ex.getMessage(), ex));
        }
    }

    private static Message.Builder getMessageBuilder(Class<?> clazz) throws Exception {
        Method method = methodCache.get(clazz);
        if (method == null) {
            method = clazz.getMethod("newBuilder", new Class[0]);
            methodCache.put(clazz, method);
        }
        return (Message.Builder) method.invoke(clazz, new Object[0]);
    }
}
