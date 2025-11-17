package org.springframework.aot.generate;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.TypeSpec;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/GeneratedClass.class */
public final class GeneratedClass {

    @Nullable
    private final GeneratedClass enclosingClass;
    private final ClassName name;
    private final GeneratedMethods methods;
    private final Consumer<TypeSpec.Builder> type;
    private final Map<ClassName, GeneratedClass> declaredClasses;
    private final Map<MethodName, AtomicInteger> methodNameSequenceGenerator;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GeneratedClass(ClassName name, Consumer<TypeSpec.Builder> type) {
        this(null, name, type);
    }

    private GeneratedClass(@Nullable GeneratedClass enclosingClass, ClassName name, Consumer<TypeSpec.Builder> type) {
        this.enclosingClass = enclosingClass;
        this.name = name;
        this.type = type;
        this.methods = new GeneratedMethods(name, this::generateSequencedMethodName);
        this.declaredClasses = new ConcurrentHashMap();
        this.methodNameSequenceGenerator = new ConcurrentHashMap();
    }

    public void reserveMethodNames(String... reservedMethodNames) {
        for (String reservedMethodName : reservedMethodNames) {
            String generatedName = generateSequencedMethodName(MethodName.of(reservedMethodNames));
            Assert.state(generatedName.equals(reservedMethodName), (Supplier<String>) () -> {
                return String.format("Unable to reserve method name '%s'", reservedMethodName);
            });
        }
    }

    private String generateSequencedMethodName(MethodName name) {
        int sequence = this.methodNameSequenceGenerator.computeIfAbsent(name, key -> {
            return new AtomicInteger();
        }).getAndIncrement();
        return sequence > 0 ? name.toString() + sequence : name.toString();
    }

    @Nullable
    public GeneratedClass getEnclosingClass() {
        return this.enclosingClass;
    }

    public ClassName getName() {
        return this.name;
    }

    public GeneratedMethods getMethods() {
        return this.methods;
    }

    public GeneratedClass getOrAdd(String name, Consumer<TypeSpec.Builder> type) {
        ClassName className = this.name.nestedClass(name);
        return this.declaredClasses.computeIfAbsent(className, key -> {
            return new GeneratedClass(this, className, type);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JavaFile generateJavaFile() {
        Assert.state(getEnclosingClass() == null, "Java file cannot be generated for an inner class");
        TypeSpec.Builder type = apply();
        return JavaFile.builder(this.name.packageName(), type.build()).build();
    }

    private TypeSpec.Builder apply() {
        TypeSpec.Builder type = getBuilder(this.type);
        type.addAnnotation(Generated.class);
        GeneratedMethods generatedMethods = this.methods;
        Objects.requireNonNull(type);
        generatedMethods.doWithMethodSpecs(type::addMethod);
        this.declaredClasses.values().forEach(declaredClass -> {
            type.addType(declaredClass.apply().build());
        });
        return type;
    }

    private TypeSpec.Builder getBuilder(Consumer<TypeSpec.Builder> type) {
        TypeSpec.Builder builder = TypeSpec.classBuilder(this.name);
        type.accept(builder);
        return builder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void assertSameType(Consumer<TypeSpec.Builder> type) {
        Assert.state(type == this.type || getBuilder(this.type).build().equals(getBuilder(type).build()), "'type' consumer generated different result");
    }
}
