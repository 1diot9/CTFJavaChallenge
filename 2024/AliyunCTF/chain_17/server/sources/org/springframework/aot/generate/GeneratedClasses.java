package org.springframework.aot.generate;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.TypeSpec;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/GeneratedClasses.class */
public class GeneratedClasses {
    private final ClassNameGenerator classNameGenerator;
    private final List<GeneratedClass> classes;
    private final Map<Owner, GeneratedClass> classesByOwner;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GeneratedClasses(ClassNameGenerator classNameGenerator) {
        this(classNameGenerator, new ArrayList(), new ConcurrentHashMap());
    }

    private GeneratedClasses(ClassNameGenerator classNameGenerator, List<GeneratedClass> classes, Map<Owner, GeneratedClass> classesByOwner) {
        Assert.notNull(classNameGenerator, "'classNameGenerator' must not be null");
        this.classNameGenerator = classNameGenerator;
        this.classes = classes;
        this.classesByOwner = classesByOwner;
    }

    public GeneratedClass getOrAddForFeature(String featureName, Consumer<TypeSpec.Builder> type) {
        Assert.hasLength(featureName, "'featureName' must not be empty");
        Assert.notNull(type, "'type' must not be null");
        Owner owner = new Owner(this.classNameGenerator.getFeatureNamePrefix(), featureName, null);
        GeneratedClass generatedClass = this.classesByOwner.computeIfAbsent(owner, key -> {
            return createAndAddGeneratedClass(featureName, null, type);
        });
        generatedClass.assertSameType(type);
        return generatedClass;
    }

    public GeneratedClass getOrAddForFeatureComponent(String featureName, ClassName targetComponent, Consumer<TypeSpec.Builder> type) {
        Assert.hasLength(featureName, "'featureName' must not be empty");
        Assert.notNull(targetComponent, "'targetComponent' must not be null");
        Assert.notNull(type, "'type' must not be null");
        Owner owner = new Owner(this.classNameGenerator.getFeatureNamePrefix(), featureName, targetComponent);
        GeneratedClass generatedClass = this.classesByOwner.computeIfAbsent(owner, key -> {
            return createAndAddGeneratedClass(featureName, targetComponent, type);
        });
        generatedClass.assertSameType(type);
        return generatedClass;
    }

    public GeneratedClass getOrAddForFeatureComponent(String featureName, Class<?> targetComponent, Consumer<TypeSpec.Builder> type) {
        return getOrAddForFeatureComponent(featureName, ClassName.get(targetComponent), type);
    }

    public GeneratedClass addForFeature(String featureName, Consumer<TypeSpec.Builder> type) {
        Assert.hasLength(featureName, "'featureName' must not be empty");
        Assert.notNull(type, "'type' must not be null");
        return createAndAddGeneratedClass(featureName, null, type);
    }

    public GeneratedClass addForFeatureComponent(String featureName, ClassName targetComponent, Consumer<TypeSpec.Builder> type) {
        Assert.hasLength(featureName, "'featureName' must not be empty");
        Assert.notNull(targetComponent, "'targetComponent' must not be null");
        Assert.notNull(type, "'type' must not be null");
        return createAndAddGeneratedClass(featureName, targetComponent, type);
    }

    public GeneratedClass addForFeatureComponent(String featureName, Class<?> targetComponent, Consumer<TypeSpec.Builder> type) {
        return addForFeatureComponent(featureName, ClassName.get(targetComponent), type);
    }

    private GeneratedClass createAndAddGeneratedClass(String featureName, @Nullable ClassName targetComponent, Consumer<TypeSpec.Builder> type) {
        ClassName className = this.classNameGenerator.generateClassName(featureName, targetComponent);
        GeneratedClass generatedClass = new GeneratedClass(className, type);
        this.classes.add(generatedClass);
        return generatedClass;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeTo(GeneratedFiles generatedFiles) {
        Assert.notNull(generatedFiles, "'generatedFiles' must not be null");
        List<GeneratedClass> generatedClasses = new ArrayList<>(this.classes);
        generatedClasses.sort(Comparator.comparing((v0) -> {
            return v0.getName();
        }));
        for (GeneratedClass generatedClass : generatedClasses) {
            generatedFiles.addSourceFile(generatedClass.generateJavaFile());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GeneratedClasses withFeatureNamePrefix(String featureNamePrefix) {
        return new GeneratedClasses(this.classNameGenerator.withFeatureNamePrefix(featureNamePrefix), this.classes, this.classesByOwner);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/GeneratedClasses$Owner.class */
    public static final class Owner extends Record {
        private final String featureNamePrefix;
        private final String featureName;

        @Nullable
        private final ClassName target;

        private Owner(String featureNamePrefix, String featureName, @Nullable ClassName target) {
            this.featureNamePrefix = featureNamePrefix;
            this.featureName = featureName;
            this.target = target;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Owner.class), Owner.class, "featureNamePrefix;featureName;target", "FIELD:Lorg/springframework/aot/generate/GeneratedClasses$Owner;->featureNamePrefix:Ljava/lang/String;", "FIELD:Lorg/springframework/aot/generate/GeneratedClasses$Owner;->featureName:Ljava/lang/String;", "FIELD:Lorg/springframework/aot/generate/GeneratedClasses$Owner;->target:Lorg/springframework/javapoet/ClassName;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Owner.class), Owner.class, "featureNamePrefix;featureName;target", "FIELD:Lorg/springframework/aot/generate/GeneratedClasses$Owner;->featureNamePrefix:Ljava/lang/String;", "FIELD:Lorg/springframework/aot/generate/GeneratedClasses$Owner;->featureName:Ljava/lang/String;", "FIELD:Lorg/springframework/aot/generate/GeneratedClasses$Owner;->target:Lorg/springframework/javapoet/ClassName;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Owner.class, Object.class), Owner.class, "featureNamePrefix;featureName;target", "FIELD:Lorg/springframework/aot/generate/GeneratedClasses$Owner;->featureNamePrefix:Ljava/lang/String;", "FIELD:Lorg/springframework/aot/generate/GeneratedClasses$Owner;->featureName:Ljava/lang/String;", "FIELD:Lorg/springframework/aot/generate/GeneratedClasses$Owner;->target:Lorg/springframework/javapoet/ClassName;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String featureNamePrefix() {
            return this.featureNamePrefix;
        }

        public String featureName() {
            return this.featureName;
        }

        @Nullable
        public ClassName target() {
            return this.target;
        }
    }
}
