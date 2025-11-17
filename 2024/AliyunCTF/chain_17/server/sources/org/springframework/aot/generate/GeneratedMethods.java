package org.springframework.aot.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.MethodSpec;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/GeneratedMethods.class */
public class GeneratedMethods {
    private final ClassName className;
    private final Function<MethodName, String> methodNameGenerator;
    private final MethodName prefix;
    private final List<GeneratedMethod> generatedMethods;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GeneratedMethods(ClassName className, Function<MethodName, String> methodNameGenerator) {
        Assert.notNull(className, "'className' must not be null");
        Assert.notNull(methodNameGenerator, "'methodNameGenerator' must not be null");
        this.className = className;
        this.methodNameGenerator = methodNameGenerator;
        this.prefix = MethodName.NONE;
        this.generatedMethods = new ArrayList();
    }

    private GeneratedMethods(ClassName className, Function<MethodName, String> methodNameGenerator, MethodName prefix, List<GeneratedMethod> generatedMethods) {
        this.className = className;
        this.methodNameGenerator = methodNameGenerator;
        this.prefix = prefix;
        this.generatedMethods = generatedMethods;
    }

    public GeneratedMethod add(String suggestedName, Consumer<MethodSpec.Builder> method) {
        Assert.notNull(suggestedName, "'suggestedName' must not be null");
        return add(new String[]{suggestedName}, method);
    }

    public GeneratedMethod add(String[] suggestedNameParts, Consumer<MethodSpec.Builder> method) {
        Assert.notNull(suggestedNameParts, "'suggestedNameParts' must not be null");
        Assert.notNull(method, "'method' must not be null");
        String generatedName = this.methodNameGenerator.apply(this.prefix.and(suggestedNameParts));
        GeneratedMethod generatedMethod = new GeneratedMethod(this.className, generatedName, method);
        this.generatedMethods.add(generatedMethod);
        return generatedMethod;
    }

    public GeneratedMethods withPrefix(String prefix) {
        Assert.notNull(prefix, "'prefix' must not be null");
        return new GeneratedMethods(this.className, this.methodNameGenerator, this.prefix.and(prefix), this.generatedMethods);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doWithMethodSpecs(Consumer<MethodSpec> action) {
        stream().map((v0) -> {
            return v0.getMethodSpec();
        }).forEach(action);
    }

    Stream<GeneratedMethod> stream() {
        return this.generatedMethods.stream();
    }
}
