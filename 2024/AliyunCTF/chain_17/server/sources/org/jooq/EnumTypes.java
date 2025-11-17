package org.jooq;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/EnumTypes.class */
final class EnumTypes {
    private static final Map<Class<?>, Map<String, ? extends EnumType>> LOOKUP = new ConcurrentHashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <E extends EnumType> E lookupLiteral(Class<E> enumType, String literal) {
        return (E) LOOKUP.computeIfAbsent(enumType, t -> {
            return (Map) Arrays.stream((EnumType[]) enumType.getEnumConstants()).collect(Collectors.toMap((v0) -> {
                return v0.getLiteral();
            }, Function.identity()));
        }).get(literal);
    }

    private EnumTypes() {
    }
}
