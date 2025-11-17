package org.springframework.context.aot;

import java.util.function.Consumer;
import org.springframework.aot.generate.GeneratedFiles;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeHint;
import org.springframework.aot.hint.TypeReference;
import org.springframework.core.io.ByteArrayResource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/aot/CglibClassHandler.class */
class CglibClassHandler {
    private static final Consumer<TypeHint.Builder> instantiateCglibProxy = hint -> {
        hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
    };
    private final RuntimeHints runtimeHints;
    private final GeneratedFiles generatedFiles;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CglibClassHandler(GenerationContext generationContext) {
        this.runtimeHints = generationContext.getRuntimeHints();
        this.generatedFiles = generationContext.getGeneratedFiles();
    }

    public void handleGeneratedClass(String cglibClassName, byte[] content) {
        registerHints(TypeReference.of(cglibClassName));
        String path = cglibClassName.replace(".", "/") + ".class";
        this.generatedFiles.addFile(GeneratedFiles.Kind.CLASS, path, new ByteArrayResource(content));
    }

    public void handleLoadedClass(Class<?> cglibClass) {
        registerHints(TypeReference.of(cglibClass));
    }

    private void registerHints(TypeReference cglibTypeReference) {
        this.runtimeHints.reflection().registerType(cglibTypeReference, instantiateCglibProxy);
    }
}
