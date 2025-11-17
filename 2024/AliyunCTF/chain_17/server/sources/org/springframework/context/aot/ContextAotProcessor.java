package org.springframework.context.aot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.aot.generate.ClassNameGenerator;
import org.springframework.aot.generate.DefaultGenerationContext;
import org.springframework.aot.generate.FileSystemGeneratedFiles;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.TypeReference;
import org.springframework.context.aot.AbstractAotProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.javapoet.ClassName;
import org.springframework.util.CollectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/aot/ContextAotProcessor.class */
public abstract class ContextAotProcessor extends AbstractAotProcessor<ClassName> {
    private final Class<?> applicationClass;

    protected abstract GenericApplicationContext prepareApplicationContext(Class<?> applicationClass);

    /* JADX INFO: Access modifiers changed from: protected */
    public ContextAotProcessor(Class<?> applicationClass, AbstractAotProcessor.Settings settings) {
        super(settings);
        this.applicationClass = applicationClass;
    }

    protected Class<?> getApplicationClass() {
        return this.applicationClass;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.context.aot.AbstractAotProcessor
    public ClassName doProcess() {
        deleteExistingOutput();
        GenericApplicationContext applicationContext = prepareApplicationContext(getApplicationClass());
        return performAotProcessing(applicationContext);
    }

    protected ClassName performAotProcessing(GenericApplicationContext applicationContext) {
        FileSystemGeneratedFiles generatedFiles = createFileSystemGeneratedFiles();
        DefaultGenerationContext generationContext = new DefaultGenerationContext(createClassNameGenerator(), generatedFiles);
        ApplicationContextAotGenerator generator = new ApplicationContextAotGenerator();
        ClassName generatedInitializerClassName = generator.processAheadOfTime(applicationContext, generationContext);
        registerEntryPointHint(generationContext, generatedInitializerClassName);
        generationContext.writeGeneratedContent();
        writeHints(generationContext.getRuntimeHints());
        writeNativeImageProperties(getDefaultNativeImageArguments(getApplicationClass().getName()));
        return generatedInitializerClassName;
    }

    protected ClassNameGenerator createClassNameGenerator() {
        return new ClassNameGenerator(ClassName.get(getApplicationClass()));
    }

    protected List<String> getDefaultNativeImageArguments(String applicationClassName) {
        List<String> args = new ArrayList<>();
        args.add("-H:Class=" + applicationClassName);
        args.add("--report-unsupported-elements-at-runtime");
        args.add("--no-fallback");
        args.add("--install-exit-handlers");
        return args;
    }

    private void registerEntryPointHint(DefaultGenerationContext generationContext, ClassName generatedInitializerClassName) {
        TypeReference generatedType = TypeReference.of(generatedInitializerClassName.canonicalName());
        TypeReference applicationType = TypeReference.of(getApplicationClass());
        ReflectionHints reflection = generationContext.getRuntimeHints().reflection();
        reflection.registerType(applicationType, new MemberCategory[0]);
        reflection.registerType(generatedType, typeHint -> {
            typeHint.onReachableType(applicationType).withConstructor(Collections.emptyList(), ExecutableMode.INVOKE);
        });
    }

    private void writeNativeImageProperties(List<String> args) {
        if (CollectionUtils.isEmpty(args)) {
            return;
        }
        Path file = getSettings().getResourceOutput().resolve("META-INF/native-image/" + getSettings().getGroupId() + "/" + getSettings().getArtifactId() + "/native-image.properties");
        try {
            if (!Files.exists(file, new LinkOption[0])) {
                Files.createDirectories(file.getParent(), new FileAttribute[0]);
                Files.createFile(file, new FileAttribute[0]);
            }
            Files.writeString(file, "Args = " + String.join(String.format(" \\%n", new Object[0]), args), new OpenOption[0]);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write native-image.properties", ex);
        }
    }
}
