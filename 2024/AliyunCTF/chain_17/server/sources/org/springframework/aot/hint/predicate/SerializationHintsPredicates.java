package org.springframework.aot.hint.predicate;

import java.util.function.Predicate;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeReference;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/predicate/SerializationHintsPredicates.class */
public class SerializationHintsPredicates {
    public Predicate<RuntimeHints> onType(Class<?> type) {
        Assert.notNull(type, "'type' must not be null");
        return onType(TypeReference.of(type));
    }

    public Predicate<RuntimeHints> onType(TypeReference typeReference) {
        Assert.notNull(typeReference, "'typeReference' must not be null");
        return hints -> {
            return hints.serialization().javaSerializationHints().anyMatch(hint -> {
                return hint.getType().equals(typeReference);
            });
        };
    }
}
