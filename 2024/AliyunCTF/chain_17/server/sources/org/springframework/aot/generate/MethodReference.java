package org.springframework.aot.generate;

import java.util.Objects;
import java.util.function.Function;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.CodeBlock;
import org.springframework.javapoet.TypeName;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/MethodReference.class */
public interface MethodReference {
    CodeBlock toCodeBlock();

    CodeBlock toInvokeCodeBlock(ArgumentCodeGenerator argumentCodeGenerator, @Nullable ClassName targetClassName);

    default CodeBlock toInvokeCodeBlock(ArgumentCodeGenerator argumentCodeGenerator) {
        return toInvokeCodeBlock(argumentCodeGenerator, null);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/MethodReference$ArgumentCodeGenerator.class */
    public interface ArgumentCodeGenerator {
        @Nullable
        CodeBlock generateCode(TypeName argumentType);

        static ArgumentCodeGenerator none() {
            return from(type -> {
                return null;
            });
        }

        static ArgumentCodeGenerator of(Class<?> argumentType, String argumentCode) {
            return from(candidateType -> {
                if (candidateType.equals(ClassName.get((Class<?>) argumentType))) {
                    return CodeBlock.of(argumentCode, new Object[0]);
                }
                return null;
            });
        }

        static ArgumentCodeGenerator from(Function<TypeName, CodeBlock> function) {
            Objects.requireNonNull(function);
            return (v1) -> {
                return r0.apply(v1);
            };
        }

        default ArgumentCodeGenerator and(Class<?> argumentType, String argumentCode) {
            return and(of(argumentType, argumentCode));
        }

        default ArgumentCodeGenerator and(ArgumentCodeGenerator argumentCodeGenerator) {
            return from(type -> {
                CodeBlock code = generateCode(type);
                return code != null ? code : argumentCodeGenerator.generateCode(type);
            });
        }
    }
}
