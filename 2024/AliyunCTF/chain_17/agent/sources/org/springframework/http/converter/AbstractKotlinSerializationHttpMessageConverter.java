package org.springframework.http.converter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.SerialFormat;
import kotlinx.serialization.SerializersKt;
import kotlinx.serialization.descriptors.PolymorphicKind;
import kotlinx.serialization.descriptors.SerialDescriptor;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/AbstractKotlinSerializationHttpMessageConverter.class */
public abstract class AbstractKotlinSerializationHttpMessageConverter<T extends SerialFormat> extends AbstractGenericHttpMessageConverter<Object> {
    private final Map<Type, KSerializer<Object>> serializerCache;
    private final T format;

    protected abstract Object readInternal(KSerializer<Object> serializer, T format, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException;

    protected abstract void writeInternal(Object object, KSerializer<Object> serializer, T format, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractKotlinSerializationHttpMessageConverter(T format, MediaType... supportedMediaTypes) {
        super(supportedMediaTypes);
        this.serializerCache = new ConcurrentReferenceHashMap();
        this.format = format;
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter, org.springframework.http.converter.AbstractHttpMessageConverter
    protected boolean supports(Class<?> clazz) {
        return serializer(clazz) != null;
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter, org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        if (serializer(GenericTypeResolver.resolveType(type, contextClass)) != null) {
            return canRead(mediaType);
        }
        return false;
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter, org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canWrite(@Nullable Type type, Class<?> clazz, @Nullable MediaType mediaType) {
        if (serializer(type != null ? GenericTypeResolver.resolveType(type, clazz) : clazz) != null) {
            return canWrite(mediaType);
        }
        return false;
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public final Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Type resolvedType = GenericTypeResolver.resolveType(type, contextClass);
        KSerializer<Object> serializer = serializer(resolvedType);
        if (serializer == null) {
            throw new HttpMessageNotReadableException("Could not find KSerializer for " + resolvedType, inputMessage);
        }
        return readInternal(serializer, this.format, inputMessage);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected final Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        KSerializer<Object> serializer = serializer(clazz);
        if (serializer == null) {
            throw new HttpMessageNotReadableException("Could not find KSerializer for " + clazz, inputMessage);
        }
        return readInternal(serializer, this.format, inputMessage);
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter
    protected final void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        Type resolvedType = type != null ? type : object.getClass();
        KSerializer<Object> serializer = serializer(resolvedType);
        if (serializer == null) {
            throw new HttpMessageNotWritableException("Could not find KSerializer for " + resolvedType);
        }
        writeInternal(object, serializer, this.format, outputMessage);
    }

    @Nullable
    private KSerializer<Object> serializer(Type type) {
        KSerializer<Object> serializer = this.serializerCache.get(type);
        if (serializer == null) {
            try {
                serializer = SerializersKt.serializerOrNull(this.format.getSerializersModule(), type);
            } catch (IllegalArgumentException e) {
            }
            if (serializer != null) {
                if (hasPolymorphism(serializer.getDescriptor(), new HashSet())) {
                    return null;
                }
                this.serializerCache.put(type, serializer);
            }
        }
        return serializer;
    }

    private boolean hasPolymorphism(SerialDescriptor descriptor, Set<String> alreadyProcessed) {
        alreadyProcessed.add(descriptor.getSerialName());
        if (descriptor.getKind().equals(PolymorphicKind.OPEN.INSTANCE)) {
            return true;
        }
        for (int i = 0; i < descriptor.getElementsCount(); i++) {
            SerialDescriptor elementDescriptor = descriptor.getElementDescriptor(i);
            if (!alreadyProcessed.contains(elementDescriptor.getSerialName()) && hasPolymorphism(elementDescriptor, alreadyProcessed)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean supportsRepeatableWrites(Object object) {
        return true;
    }
}
