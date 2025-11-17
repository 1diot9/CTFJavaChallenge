package org.springframework.aot.nativex;

import ch.qos.logback.core.joran.conditional.IfAction;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.aot.hint.ConditionalHint;
import org.springframework.aot.hint.JavaSerializationHint;
import org.springframework.aot.hint.SerializationHints;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/nativex/SerializationHintsWriter.class */
public class SerializationHintsWriter {
    public static final SerializationHintsWriter INSTANCE = new SerializationHintsWriter();
    private static final Comparator<JavaSerializationHint> JAVA_SERIALIZATION_HINT_COMPARATOR = Comparator.comparing((v0) -> {
        return v0.getType();
    });

    SerializationHintsWriter() {
    }

    public void write(BasicJsonWriter writer, SerializationHints hints) {
        writer.writeArray(hints.javaSerializationHints().sorted(JAVA_SERIALIZATION_HINT_COMPARATOR).map(this::toAttributes).toList());
    }

    private Map<String, Object> toAttributes(JavaSerializationHint serializationHint) {
        LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();
        handleCondition(attributes, serializationHint);
        attributes.put("name", serializationHint.getType());
        return attributes;
    }

    private void handleCondition(Map<String, Object> attributes, ConditionalHint hint) {
        if (hint.getReachableType() != null) {
            Map<String, Object> conditionAttributes = new LinkedHashMap<>();
            conditionAttributes.put("typeReachable", hint.getReachableType());
            attributes.put(IfAction.CONDITION_ATTRIBUTE, conditionAttributes);
        }
    }
}
