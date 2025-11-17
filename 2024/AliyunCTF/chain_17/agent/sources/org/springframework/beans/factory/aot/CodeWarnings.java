package org.springframework.beans.factory.aot;

import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Stream;
import org.springframework.javapoet.AnnotationSpec;
import org.springframework.javapoet.CodeBlock;
import org.springframework.javapoet.MethodSpec;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/CodeWarnings.class */
class CodeWarnings {
    private final Set<String> warnings = new LinkedHashSet();

    public void register(String warning) {
        this.warnings.add(warning);
    }

    public CodeWarnings detectDeprecation(AnnotatedElement... elements) {
        for (AnnotatedElement element : elements) {
            register((Deprecated) element.getAnnotation(Deprecated.class));
        }
        return this;
    }

    public CodeWarnings detectDeprecation(Stream<AnnotatedElement> elements) {
        elements.forEach(element -> {
            register((Deprecated) element.getAnnotation(Deprecated.class));
        });
        return this;
    }

    public void suppress(MethodSpec.Builder method) {
        if (this.warnings.isEmpty()) {
            return;
        }
        method.addAnnotation(buildAnnotationSpec());
    }

    protected Set<String> getWarnings() {
        return Collections.unmodifiableSet(this.warnings);
    }

    private void register(@Nullable Deprecated annotation) {
        if (annotation != null) {
            if (annotation.forRemoval()) {
                register("removal");
            } else {
                register("deprecation");
            }
        }
    }

    private AnnotationSpec buildAnnotationSpec() {
        return AnnotationSpec.builder((Class<?>) SuppressWarnings.class).addMember("value", generateValueCode()).build();
    }

    private CodeBlock generateValueCode() {
        if (this.warnings.size() == 1) {
            return CodeBlock.of("$S", this.warnings.iterator().next());
        }
        CodeBlock values = CodeBlock.join(this.warnings.stream().map(warning -> {
            return CodeBlock.of("$S", warning);
        }).toList(), ", ");
        return CodeBlock.of("{ $L }", values);
    }

    public String toString() {
        return new StringJoiner(", ", CodeWarnings.class.getSimpleName(), "").add(this.warnings.toString()).toString();
    }
}
