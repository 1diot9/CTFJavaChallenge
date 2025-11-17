package org.springframework.boot.json;

import com.fasterxml.jackson.databind.ser.std.ClassSerializer;
import com.fasterxml.jackson.databind.ser.std.FileSerializer;
import com.fasterxml.jackson.databind.ser.std.StdJdkSerializers;
import com.fasterxml.jackson.databind.ser.std.TokenBufferSerializer;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeHint;
import org.springframework.aot.hint.TypeReference;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/json/JacksonRuntimeHints.class */
class JacksonRuntimeHints implements RuntimeHintsRegistrar {
    JacksonRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        if (!ClassUtils.isPresent("com.fasterxml.jackson.databind.ser.BasicSerializerFactory", classLoader)) {
            return;
        }
        registerSerializers(hints.reflection());
    }

    private void registerSerializers(ReflectionHints hints) {
        hints.registerTypes(TypeReference.listOf(StdJdkSerializers.AtomicBooleanSerializer.class, StdJdkSerializers.AtomicIntegerSerializer.class, StdJdkSerializers.AtomicLongSerializer.class, FileSerializer.class, ClassSerializer.class, TokenBufferSerializer.class), TypeHint.builtWith(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS));
    }
}
