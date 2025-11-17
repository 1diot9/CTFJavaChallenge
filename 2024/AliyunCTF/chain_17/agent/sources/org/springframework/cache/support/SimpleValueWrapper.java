package org.springframework.cache.support;

import java.util.Objects;
import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/support/SimpleValueWrapper.class */
public class SimpleValueWrapper implements Cache.ValueWrapper {

    @Nullable
    private final Object value;

    public SimpleValueWrapper(@Nullable Object value) {
        this.value = value;
    }

    @Override // org.springframework.cache.Cache.ValueWrapper
    @Nullable
    public Object get() {
        return this.value;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof Cache.ValueWrapper) {
                Cache.ValueWrapper wrapper = (Cache.ValueWrapper) other;
                if (Objects.equals(get(), wrapper.get())) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    public String toString() {
        return "ValueWrapper for [" + this.value + "]";
    }
}
