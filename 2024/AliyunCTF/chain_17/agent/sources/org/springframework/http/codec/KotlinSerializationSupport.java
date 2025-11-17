package org.springframework.http.codec;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.SerialFormat;
import kotlinx.serialization.SerializersKt;
import kotlinx.serialization.descriptors.PolymorphicKind;
import kotlinx.serialization.descriptors.SerialDescriptor;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.MimeType;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/KotlinSerializationSupport.class */
public abstract class KotlinSerializationSupport<T extends SerialFormat> {
    private final Map<Type, KSerializer<Object>> serializerCache = new ConcurrentReferenceHashMap();
    private final T format;
    private final List<MimeType> supportedMimeTypes;

    /* JADX INFO: Access modifiers changed from: protected */
    public KotlinSerializationSupport(T format, MimeType... supportedMimeTypes) {
        this.format = format;
        this.supportedMimeTypes = Arrays.asList(supportedMimeTypes);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final T format() {
        return this.format;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final List<MimeType> supportedMimeTypes() {
        return this.supportedMimeTypes;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean canSerialize(ResolvableType type, @Nullable MimeType mimeType) {
        KSerializer<Object> serializer = serializer(type);
        return (serializer == null || !supports(mimeType) || String.class.isAssignableFrom(type.toClass()) || ServerSentEvent.class.isAssignableFrom(type.toClass())) ? false : true;
    }

    private boolean supports(@Nullable MimeType mimeType) {
        if (mimeType == null) {
            return true;
        }
        for (MimeType candidate : this.supportedMimeTypes) {
            if (candidate.isCompatibleWith(mimeType)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public final KSerializer<Object> serializer(ResolvableType resolvableType) {
        Type type = resolvableType.getType();
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

    private static boolean hasPolymorphism(SerialDescriptor descriptor, Set<String> alreadyProcessed) {
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
}
