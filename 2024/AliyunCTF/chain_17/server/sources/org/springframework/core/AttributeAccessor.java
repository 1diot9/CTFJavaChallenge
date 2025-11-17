package org.springframework.core;

import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/AttributeAccessor.class */
public interface AttributeAccessor {
    void setAttribute(String name, @Nullable Object value);

    @Nullable
    Object getAttribute(String name);

    @Nullable
    Object removeAttribute(String name);

    boolean hasAttribute(String name);

    String[] attributeNames();

    default <T> T computeAttribute(String str, Function<String, T> function) {
        Assert.notNull(str, "Name must not be null");
        Assert.notNull(function, "Compute function must not be null");
        Object attribute = getAttribute(str);
        if (attribute == null) {
            attribute = function.apply(str);
            Assert.state(attribute != null, (Supplier<String>) () -> {
                return String.format("Compute function must not return null for attribute named '%s'", str);
            });
            setAttribute(str, attribute);
        }
        return (T) attribute;
    }
}
