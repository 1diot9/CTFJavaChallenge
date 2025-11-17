package org.springframework.aot.hint.predicate;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/predicate/RuntimeHintsPredicates.class */
public abstract class RuntimeHintsPredicates {
    private static final ReflectionHintsPredicates reflection = new ReflectionHintsPredicates();
    private static final ResourceHintsPredicates resource = new ResourceHintsPredicates();
    private static final SerializationHintsPredicates serialization = new SerializationHintsPredicates();
    private static final ProxyHintsPredicates proxies = new ProxyHintsPredicates();

    private RuntimeHintsPredicates() {
    }

    public static ReflectionHintsPredicates reflection() {
        return reflection;
    }

    public static ResourceHintsPredicates resource() {
        return resource;
    }

    public static SerializationHintsPredicates serialization() {
        return serialization;
    }

    public static ProxyHintsPredicates proxies() {
        return proxies;
    }
}
