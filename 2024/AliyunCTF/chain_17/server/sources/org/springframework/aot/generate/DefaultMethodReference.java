package org.springframework.aot.generate;

import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Modifier;
import org.springframework.aot.generate.MethodReference;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.CodeBlock;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.TypeName;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/DefaultMethodReference.class */
public class DefaultMethodReference implements MethodReference {
    private final MethodSpec method;

    @Nullable
    private final ClassName declaringClass;

    public DefaultMethodReference(MethodSpec method, @Nullable ClassName declaringClass) {
        this.method = method;
        this.declaringClass = declaringClass;
    }

    @Override // org.springframework.aot.generate.MethodReference
    public CodeBlock toCodeBlock() {
        String methodName = this.method.name;
        if (isStatic()) {
            Assert.state(this.declaringClass != null, "static method reference must define a declaring class");
            return CodeBlock.of("$T::$L", this.declaringClass, methodName);
        }
        return CodeBlock.of("this::$L", methodName);
    }

    @Override // org.springframework.aot.generate.MethodReference
    public CodeBlock toInvokeCodeBlock(MethodReference.ArgumentCodeGenerator argumentCodeGenerator, @Nullable ClassName targetClassName) {
        String methodName = this.method.name;
        CodeBlock.Builder code = CodeBlock.builder();
        if (isStatic()) {
            Assert.state(this.declaringClass != null, "static method reference must define a declaring class");
            if (isSameDeclaringClass(targetClassName)) {
                code.add("$L", methodName);
            } else {
                code.add("$T.$L", this.declaringClass, methodName);
            }
        } else {
            if (!isSameDeclaringClass(targetClassName)) {
                code.add(instantiateDeclaringClass(this.declaringClass));
            }
            code.add("$L", methodName);
        }
        code.add("(", new Object[0]);
        addArguments(code, argumentCodeGenerator);
        code.add(")", new Object[0]);
        return code.build();
    }

    protected void addArguments(CodeBlock.Builder code, MethodReference.ArgumentCodeGenerator argumentCodeGenerator) {
        List<CodeBlock> arguments = new ArrayList<>();
        TypeName[] argumentTypes = (TypeName[]) this.method.parameters.stream().map(parameter -> {
            return parameter.type;
        }).toArray(x$0 -> {
            return new TypeName[x$0];
        });
        for (int i = 0; i < argumentTypes.length; i++) {
            TypeName argumentType = argumentTypes[i];
            CodeBlock argumentCode = argumentCodeGenerator.generateCode(argumentType);
            if (argumentCode == null) {
                throw new IllegalArgumentException("Could not generate code for " + this + ": parameter " + i + " of type " + argumentType + " is not supported");
            }
            arguments.add(argumentCode);
        }
        code.add(CodeBlock.join(arguments, ", "));
    }

    protected CodeBlock instantiateDeclaringClass(ClassName declaringClass) {
        return CodeBlock.of("new $T().", declaringClass);
    }

    private boolean isStatic() {
        return this.method.modifiers.contains(Modifier.STATIC);
    }

    private boolean isSameDeclaringClass(ClassName declaringClass) {
        return this.declaringClass == null || this.declaringClass.equals(declaringClass);
    }

    public String toString() {
        String methodName = this.method.name;
        if (isStatic()) {
            return this.declaringClass + "::" + methodName;
        }
        return (this.declaringClass != null ? "<" + this.declaringClass + ">" : "<instance>") + "::" + methodName;
    }
}
