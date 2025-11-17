package org.springframework.aot.hint.annotation;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/annotation/ReflectiveRuntimeHintsRegistrar.class */
public class ReflectiveRuntimeHintsRegistrar {
    private final Map<Class<? extends ReflectiveProcessor>, ReflectiveProcessor> processors = new HashMap();

    public void registerRuntimeHints(RuntimeHints runtimeHints, Class<?>... types) {
        Set<Entry> entries = new HashSet<>();
        for (Class<?> type : types) {
            processType(entries, type);
            for (Class<?> implementedInterface : ClassUtils.getAllInterfacesForClass(type)) {
                processType(entries, implementedInterface);
            }
        }
        entries.forEach(entry -> {
            AnnotatedElement element = entry.element();
            entry.processor().registerReflectionHints(runtimeHints.reflection(), element);
        });
    }

    private void processType(Set<Entry> entries, Class<?> typeToProcess) {
        if (isReflective(typeToProcess)) {
            entries.add(createEntry(typeToProcess));
        }
        doWithReflectiveConstructors(typeToProcess, constructor -> {
            entries.add(createEntry(constructor));
        });
        ReflectionUtils.doWithFields(typeToProcess, field -> {
            entries.add(createEntry(field));
        }, (v1) -> {
            return isReflective(v1);
        });
        ReflectionUtils.doWithMethods(typeToProcess, method -> {
            entries.add(createEntry(method));
        }, (v1) -> {
            return isReflective(v1);
        });
    }

    private void doWithReflectiveConstructors(Class<?> typeToProcess, Consumer<Constructor<?>> consumer) {
        for (Constructor<?> constructor : typeToProcess.getDeclaredConstructors()) {
            if (isReflective(constructor)) {
                consumer.accept(constructor);
            }
        }
    }

    private boolean isReflective(AnnotatedElement element) {
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).isPresent(Reflective.class);
    }

    private Entry createEntry(AnnotatedElement element) {
        List<ReflectiveProcessor> processors = MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).stream(Reflective.class).map(annotation -> {
            return annotation.getClassArray("value");
        }).flatMap((v0) -> {
            return Arrays.stream(v0);
        }).distinct().map(type -> {
            return type;
        }).map(processorClass -> {
            return this.processors.computeIfAbsent(processorClass, this::instantiateClass);
        }).toList();
        ReflectiveProcessor processorToUse = processors.size() == 1 ? processors.get(0) : new DelegatingReflectiveProcessor(processors);
        return new Entry(element, processorToUse);
    }

    private ReflectiveProcessor instantiateClass(Class<? extends ReflectiveProcessor> type) {
        try {
            Constructor<? extends ReflectiveProcessor> constructor = type.getDeclaredConstructor(new Class[0]);
            ReflectionUtils.makeAccessible(constructor);
            return constructor.newInstance(new Object[0]);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to instantiate " + type, ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/annotation/ReflectiveRuntimeHintsRegistrar$DelegatingReflectiveProcessor.class */
    public static class DelegatingReflectiveProcessor implements ReflectiveProcessor {
        private final Iterable<ReflectiveProcessor> processors;

        DelegatingReflectiveProcessor(Iterable<ReflectiveProcessor> processors) {
            this.processors = processors;
        }

        @Override // org.springframework.aot.hint.annotation.ReflectiveProcessor
        public void registerReflectionHints(ReflectionHints hints, AnnotatedElement element) {
            this.processors.forEach(processor -> {
                processor.registerReflectionHints(hints, element);
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/annotation/ReflectiveRuntimeHintsRegistrar$Entry.class */
    public static final class Entry extends Record {
        private final AnnotatedElement element;
        private final ReflectiveProcessor processor;

        private Entry(AnnotatedElement element, ReflectiveProcessor processor) {
            this.element = element;
            this.processor = processor;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Entry.class), Entry.class, "element;processor", "FIELD:Lorg/springframework/aot/hint/annotation/ReflectiveRuntimeHintsRegistrar$Entry;->element:Ljava/lang/reflect/AnnotatedElement;", "FIELD:Lorg/springframework/aot/hint/annotation/ReflectiveRuntimeHintsRegistrar$Entry;->processor:Lorg/springframework/aot/hint/annotation/ReflectiveProcessor;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Entry.class), Entry.class, "element;processor", "FIELD:Lorg/springframework/aot/hint/annotation/ReflectiveRuntimeHintsRegistrar$Entry;->element:Ljava/lang/reflect/AnnotatedElement;", "FIELD:Lorg/springframework/aot/hint/annotation/ReflectiveRuntimeHintsRegistrar$Entry;->processor:Lorg/springframework/aot/hint/annotation/ReflectiveProcessor;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Entry.class, Object.class), Entry.class, "element;processor", "FIELD:Lorg/springframework/aot/hint/annotation/ReflectiveRuntimeHintsRegistrar$Entry;->element:Ljava/lang/reflect/AnnotatedElement;", "FIELD:Lorg/springframework/aot/hint/annotation/ReflectiveRuntimeHintsRegistrar$Entry;->processor:Lorg/springframework/aot/hint/annotation/ReflectiveProcessor;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public AnnotatedElement element() {
            return this.element;
        }

        public ReflectiveProcessor processor() {
            return this.processor;
        }
    }
}
