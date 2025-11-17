package org.springframework.aot.hint.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.function.Supplier;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/annotation/RegisterReflectionForBindingProcessor.class */
public class RegisterReflectionForBindingProcessor implements ReflectiveProcessor {
    private final BindingReflectionHintsRegistrar bindingRegistrar = new BindingReflectionHintsRegistrar();

    @Override // org.springframework.aot.hint.annotation.ReflectiveProcessor
    public void registerReflectionHints(ReflectionHints hints, AnnotatedElement element) {
        RegisterReflectionForBinding registerReflection = (RegisterReflectionForBinding) AnnotationUtils.getAnnotation(element, RegisterReflectionForBinding.class);
        if (registerReflection != null) {
            Class<?>[] classes = registerReflection.classes();
            Assert.state(classes.length != 0, (Supplier<String>) () -> {
                return "A least one class should be specified in @RegisterReflectionForBinding attributes, and none was provided on " + element;
            });
            for (Class<?> type : classes) {
                this.bindingRegistrar.registerReflectionHints(hints, type);
            }
        }
    }
}
