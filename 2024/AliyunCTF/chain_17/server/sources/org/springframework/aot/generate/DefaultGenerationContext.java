package org.springframework.aot.generate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/DefaultGenerationContext.class */
public class DefaultGenerationContext implements GenerationContext {
    private final Map<String, AtomicInteger> sequenceGenerator;
    private final GeneratedClasses generatedClasses;
    private final GeneratedFiles generatedFiles;
    private final RuntimeHints runtimeHints;

    public DefaultGenerationContext(ClassNameGenerator classNameGenerator, GeneratedFiles generatedFiles) {
        this(classNameGenerator, generatedFiles, new RuntimeHints());
    }

    public DefaultGenerationContext(ClassNameGenerator classNameGenerator, GeneratedFiles generatedFiles, RuntimeHints runtimeHints) {
        this(new GeneratedClasses(classNameGenerator), generatedFiles, runtimeHints);
    }

    DefaultGenerationContext(GeneratedClasses generatedClasses, GeneratedFiles generatedFiles, RuntimeHints runtimeHints) {
        Assert.notNull(generatedClasses, "'generatedClasses' must not be null");
        Assert.notNull(generatedFiles, "'generatedFiles' must not be null");
        Assert.notNull(runtimeHints, "'runtimeHints' must not be null");
        this.sequenceGenerator = new ConcurrentHashMap();
        this.generatedClasses = generatedClasses;
        this.generatedFiles = generatedFiles;
        this.runtimeHints = runtimeHints;
    }

    protected DefaultGenerationContext(DefaultGenerationContext existing, String featureName) {
        int sequence = existing.sequenceGenerator.computeIfAbsent(featureName, key -> {
            return new AtomicInteger();
        }).getAndIncrement();
        featureName = sequence > 0 ? featureName + sequence : featureName;
        this.sequenceGenerator = existing.sequenceGenerator;
        this.generatedClasses = existing.generatedClasses.withFeatureNamePrefix(featureName);
        this.generatedFiles = existing.generatedFiles;
        this.runtimeHints = existing.runtimeHints;
    }

    @Override // org.springframework.aot.generate.GenerationContext
    public GeneratedClasses getGeneratedClasses() {
        return this.generatedClasses;
    }

    @Override // org.springframework.aot.generate.GenerationContext
    public GeneratedFiles getGeneratedFiles() {
        return this.generatedFiles;
    }

    @Override // org.springframework.aot.generate.GenerationContext
    public RuntimeHints getRuntimeHints() {
        return this.runtimeHints;
    }

    @Override // org.springframework.aot.generate.GenerationContext
    public DefaultGenerationContext withName(String name) {
        return new DefaultGenerationContext(this, name);
    }

    public void writeGeneratedContent() {
        this.generatedClasses.writeTo(this.generatedFiles);
    }
}
