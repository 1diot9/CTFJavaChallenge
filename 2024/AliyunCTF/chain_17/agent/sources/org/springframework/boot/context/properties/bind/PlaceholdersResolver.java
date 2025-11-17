package org.springframework.boot.context.properties.bind;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/PlaceholdersResolver.class */
public interface PlaceholdersResolver {
    public static final PlaceholdersResolver NONE = value -> {
        return value;
    };

    Object resolvePlaceholders(Object value);
}
