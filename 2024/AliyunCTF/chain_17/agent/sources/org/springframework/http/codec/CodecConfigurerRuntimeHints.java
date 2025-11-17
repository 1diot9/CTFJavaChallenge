package org.springframework.http.codec;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.http.codec.support.DefaultClientCodecConfigurer;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/CodecConfigurerRuntimeHints.class */
class CodecConfigurerRuntimeHints implements RuntimeHintsRegistrar {
    CodecConfigurerRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        hints.resources().registerPattern("org/springframework/http/codec/CodecConfigurer.properties");
        hints.reflection().registerTypes(TypeReference.listOf(DefaultClientCodecConfigurer.class, DefaultServerCodecConfigurer.class), typeHint -> {
            typeHint.onReachableType(CodecConfigurerFactory.class).withMembers(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        });
    }
}
