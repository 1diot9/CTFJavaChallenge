package org.springframework.http.codec.protobuf;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import org.springframework.asm.Opcodes;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/protobuf/ProtobufDecoder.class */
public class ProtobufDecoder extends ProtobufCodecSupport implements Decoder<Message> {
    protected static final int DEFAULT_MESSAGE_MAX_SIZE = 262144;
    private static final ConcurrentMap<Class<?>, Method> methodCache = new ConcurrentReferenceHashMap();
    private final ExtensionRegistry extensionRegistry;
    private int maxMessageSize;

    @Override // org.springframework.core.codec.Decoder
    public /* bridge */ /* synthetic */ Message decode(DataBuffer dataBuffer, ResolvableType targetType, @Nullable MimeType mimeType, @Nullable Map hints) throws DecodingException {
        return decode(dataBuffer, targetType, mimeType, (Map<String, Object>) hints);
    }

    public ProtobufDecoder() {
        this(ExtensionRegistry.newInstance());
    }

    public ProtobufDecoder(ExtensionRegistry extensionRegistry) {
        this.maxMessageSize = 262144;
        Assert.notNull(extensionRegistry, "ExtensionRegistry must not be null");
        this.extensionRegistry = extensionRegistry;
    }

    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    public int getMaxMessageSize() {
        return this.maxMessageSize;
    }

    @Override // org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return Message.class.isAssignableFrom(elementType.toClass()) && supportsMimeType(mimeType);
    }

    @Override // org.springframework.core.codec.Decoder
    public Flux<Message> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        MessageDecoderFunction decoderFunction = new MessageDecoderFunction(elementType, this.maxMessageSize);
        Flux flatMapIterable = Flux.from(inputStream).flatMapIterable(decoderFunction);
        Objects.requireNonNull(decoderFunction);
        return flatMapIterable.doOnTerminate(decoderFunction::discard);
    }

    @Override // org.springframework.core.codec.Decoder
    public Mono<Message> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return DataBufferUtils.join(inputStream, this.maxMessageSize).map(dataBuffer -> {
            return decode(dataBuffer, elementType, mimeType, (Map<String, Object>) hints);
        });
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.core.codec.Decoder
    public Message decode(DataBuffer dataBuffer, ResolvableType targetType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) throws DecodingException {
        try {
            try {
                try {
                    Message.Builder builder = getMessageBuilder(targetType.toClass());
                    ByteBuffer byteBuffer = ByteBuffer.allocate(dataBuffer.readableByteCount());
                    dataBuffer.toByteBuffer(byteBuffer);
                    builder.mergeFrom(CodedInputStream.newInstance(byteBuffer), this.extensionRegistry);
                    Message build = builder.build();
                    DataBufferUtils.release(dataBuffer);
                    return build;
                } catch (IOException ex) {
                    throw new DecodingException("I/O error while parsing input stream", ex);
                }
            } catch (Exception ex2) {
                throw new DecodingException("Could not read Protobuf message: " + ex2.getMessage(), ex2);
            }
        } catch (Throwable th) {
            DataBufferUtils.release(dataBuffer);
            throw th;
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

    @Override // org.springframework.core.codec.Decoder
    public List<MimeType> getDecodableMimeTypes() {
        return getMimeTypes();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/protobuf/ProtobufDecoder$MessageDecoderFunction.class */
    private class MessageDecoderFunction implements Function<DataBuffer, Iterable<? extends Message>> {
        private final ResolvableType elementType;
        private final int maxMessageSize;

        @Nullable
        private DataBuffer output;
        private int messageBytesToRead;
        private int offset;

        public MessageDecoderFunction(ResolvableType elementType, int maxMessageSize) {
            this.elementType = elementType;
            this.maxMessageSize = maxMessageSize;
        }

        @Override // java.util.function.Function
        public Iterable<? extends Message> apply(DataBuffer input) {
            int remainingBytesToRead;
            try {
                try {
                    try {
                        List<Message> messages = new ArrayList<>();
                        do {
                            if (this.output == null) {
                                if (!readMessageSize(input)) {
                                    return messages;
                                }
                                if (this.maxMessageSize > 0 && this.messageBytesToRead > this.maxMessageSize) {
                                    throw new DataBufferLimitException("The number of bytes to read for message (" + this.messageBytesToRead + ") exceeds the configured limit (" + this.maxMessageSize + ")");
                                }
                                this.output = input.factory().allocateBuffer(this.messageBytesToRead);
                            }
                            int chunkBytesToRead = Math.min(this.messageBytesToRead, input.readableByteCount());
                            remainingBytesToRead = input.readableByteCount() - chunkBytesToRead;
                            byte[] bytesToWrite = new byte[chunkBytesToRead];
                            input.read(bytesToWrite, 0, chunkBytesToRead);
                            this.output.write(bytesToWrite);
                            this.messageBytesToRead -= chunkBytesToRead;
                            if (this.messageBytesToRead == 0) {
                                ByteBuffer byteBuffer = ByteBuffer.allocate(this.output.readableByteCount());
                                this.output.toByteBuffer(byteBuffer);
                                CodedInputStream stream = CodedInputStream.newInstance(byteBuffer);
                                DataBufferUtils.release(this.output);
                                this.output = null;
                                Message message = ProtobufDecoder.getMessageBuilder(this.elementType.toClass()).mergeFrom(stream, ProtobufDecoder.this.extensionRegistry).build();
                                messages.add(message);
                            }
                        } while (remainingBytesToRead > 0);
                        DataBufferUtils.release(input);
                        return messages;
                    } catch (IOException ex) {
                        throw new DecodingException("I/O error while parsing input stream", ex);
                    } catch (Exception ex2) {
                        throw new DecodingException("Could not read Protobuf message: " + ex2.getMessage(), ex2);
                    }
                } catch (DecodingException ex3) {
                    throw ex3;
                }
            } finally {
                DataBufferUtils.release(input);
            }
        }

        private boolean readMessageSize(DataBuffer input) {
            if (this.offset == 0) {
                if (input.readableByteCount() == 0) {
                    return false;
                }
                int firstByte = input.read();
                if ((firstByte & 128) == 0) {
                    this.messageBytesToRead = firstByte;
                    return true;
                }
                this.messageBytesToRead = firstByte & Opcodes.LAND;
                this.offset = 7;
            }
            if (this.offset < 32) {
                while (this.offset < 32) {
                    if (input.readableByteCount() == 0) {
                        return false;
                    }
                    int b = input.read();
                    this.messageBytesToRead |= (b & Opcodes.LAND) << this.offset;
                    if ((b & 128) != 0) {
                        this.offset += 7;
                    } else {
                        this.offset = 0;
                        return true;
                    }
                }
            }
            while (this.offset < 64) {
                if (input.readableByteCount() == 0) {
                    return false;
                }
                if ((input.read() & 128) != 0) {
                    this.offset += 7;
                } else {
                    this.offset = 0;
                    return true;
                }
            }
            this.offset = 0;
            throw new DecodingException("Cannot parse message size: malformed varint");
        }

        public void discard() {
            if (this.output != null) {
                DataBufferUtils.release(this.output);
            }
        }
    }
}
