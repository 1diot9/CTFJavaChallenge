package org.springframework.boot.origin;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/origin/OriginLookup.class */
public interface OriginLookup<K> {
    Origin getOrigin(K key);

    default boolean isImmutable() {
        return false;
    }

    default String getPrefix() {
        return null;
    }

    static <K> Origin getOrigin(Object source, K key) {
        if (!(source instanceof OriginLookup)) {
            return null;
        }
        try {
            return ((OriginLookup) source).getOrigin(key);
        } catch (Throwable th) {
            return null;
        }
    }
}
