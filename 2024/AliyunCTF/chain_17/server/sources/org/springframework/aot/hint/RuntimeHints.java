package org.springframework.aot.hint;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/RuntimeHints.class */
public class RuntimeHints {
    private final ReflectionHints reflection = new ReflectionHints();
    private final ResourceHints resources = new ResourceHints();
    private final SerializationHints serialization = new SerializationHints();
    private final ProxyHints proxies = new ProxyHints();
    private final ReflectionHints jni = new ReflectionHints();

    public ReflectionHints reflection() {
        return this.reflection;
    }

    public ResourceHints resources() {
        return this.resources;
    }

    public SerializationHints serialization() {
        return this.serialization;
    }

    public ProxyHints proxies() {
        return this.proxies;
    }

    public ReflectionHints jni() {
        return this.jni;
    }
}
