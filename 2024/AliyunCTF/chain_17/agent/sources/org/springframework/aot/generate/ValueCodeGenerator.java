package org.springframework.aot.generate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.javapoet.CodeBlock;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/ValueCodeGenerator.class */
public final class ValueCodeGenerator {
    private static final ValueCodeGenerator INSTANCE = new ValueCodeGenerator(ValueCodeGeneratorDelegates.INSTANCES, null);
    private static final CodeBlock NULL_VALUE_CODE_BLOCK = CodeBlock.of("null", new Object[0]);
    private final List<Delegate> delegates;

    @Nullable
    private final GeneratedMethods generatedMethods;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/ValueCodeGenerator$Delegate.class */
    public interface Delegate {
        @Nullable
        CodeBlock generateCode(ValueCodeGenerator valueCodeGenerator, Object value);
    }

    private ValueCodeGenerator(List<Delegate> delegates, @Nullable GeneratedMethods generatedMethods) {
        this.delegates = delegates;
        this.generatedMethods = generatedMethods;
    }

    public static ValueCodeGenerator withDefaults() {
        return INSTANCE;
    }

    public static ValueCodeGenerator with(Delegate... delegates) {
        return with((List<Delegate>) Arrays.asList(delegates));
    }

    public static ValueCodeGenerator with(List<Delegate> delegates) {
        Assert.notEmpty(delegates, "Delegates must not be empty");
        return new ValueCodeGenerator(new ArrayList(delegates), null);
    }

    public ValueCodeGenerator add(List<Delegate> additionalDelegates) {
        Assert.notEmpty(additionalDelegates, "AdditionalDelegates must not be empty");
        List<Delegate> allDelegates = new ArrayList<>(this.delegates);
        allDelegates.addAll(additionalDelegates);
        return new ValueCodeGenerator(allDelegates, this.generatedMethods);
    }

    public ValueCodeGenerator scoped(GeneratedMethods generatedMethods) {
        return new ValueCodeGenerator(this.delegates, generatedMethods);
    }

    public CodeBlock generateCode(@Nullable Object value) {
        if (value == null) {
            return NULL_VALUE_CODE_BLOCK;
        }
        try {
            for (Delegate delegate : this.delegates) {
                CodeBlock code = delegate.generateCode(this, value);
                if (code != null) {
                    return code;
                }
            }
            throw new UnsupportedTypeValueCodeGenerationException(value);
        } catch (Exception ex) {
            throw new ValueCodeGenerationException(value, ex);
        }
    }

    @Nullable
    public GeneratedMethods getGeneratedMethods() {
        return this.generatedMethods;
    }
}
