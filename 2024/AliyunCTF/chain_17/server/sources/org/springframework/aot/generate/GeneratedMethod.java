package org.springframework.aot.generate;

import java.util.function.Consumer;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.MethodSpec;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/GeneratedMethod.class */
public final class GeneratedMethod {
    private final ClassName className;
    private final String name;
    private final MethodSpec methodSpec;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GeneratedMethod(ClassName className, String name, Consumer<MethodSpec.Builder> method) {
        this.className = className;
        this.name = name;
        MethodSpec.Builder builder = MethodSpec.methodBuilder(this.name);
        method.accept(builder);
        this.methodSpec = builder.build();
        Assert.state(this.name.equals(this.methodSpec.name), "'method' consumer must not change the generated method name");
    }

    public String getName() {
        return this.name;
    }

    public MethodReference toMethodReference() {
        return new DefaultMethodReference(this.methodSpec, this.className);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MethodSpec getMethodSpec() {
        return this.methodSpec;
    }

    public String toString() {
        return this.name;
    }
}
