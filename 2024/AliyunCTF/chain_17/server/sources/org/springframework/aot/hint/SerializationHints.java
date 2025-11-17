package org.springframework.aot.hint;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.springframework.aot.hint.JavaSerializationHint;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/SerializationHints.class */
public class SerializationHints {
    private final Set<JavaSerializationHint> javaSerializationHints = new LinkedHashSet();

    public Stream<JavaSerializationHint> javaSerializationHints() {
        return this.javaSerializationHints.stream();
    }

    public SerializationHints registerType(TypeReference type, @Nullable Consumer<JavaSerializationHint.Builder> serializationHint) {
        JavaSerializationHint.Builder builder = new JavaSerializationHint.Builder(type);
        if (serializationHint != null) {
            serializationHint.accept(builder);
        }
        this.javaSerializationHints.add(builder.build());
        return this;
    }

    public SerializationHints registerType(TypeReference type) {
        return registerType(type, (Consumer<JavaSerializationHint.Builder>) null);
    }

    public SerializationHints registerType(Class<? extends Serializable> type, @Nullable Consumer<JavaSerializationHint.Builder> serializationHint) {
        return registerType(TypeReference.of(type), serializationHint);
    }

    public SerializationHints registerType(Class<? extends Serializable> type) {
        return registerType(type, (Consumer<JavaSerializationHint.Builder>) null);
    }
}
