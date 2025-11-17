package org.springframework.boot.context.properties.source;

import java.lang.ref.SoftReference;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/SoftReferenceConfigurationPropertyCache.class */
class SoftReferenceConfigurationPropertyCache<T> implements ConfigurationPropertyCaching {
    private static final Duration UNLIMITED = Duration.ZERO;
    private final boolean neverExpire;
    private volatile Duration timeToLive;
    private volatile SoftReference<T> value = new SoftReference<>(null);
    private volatile Instant lastAccessed = now();

    /* JADX INFO: Access modifiers changed from: package-private */
    public SoftReferenceConfigurationPropertyCache(boolean neverExpire) {
        this.neverExpire = neverExpire;
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertyCaching
    public void enable() {
        this.timeToLive = UNLIMITED;
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertyCaching
    public void disable() {
        this.timeToLive = null;
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertyCaching
    public void setTimeToLive(Duration timeToLive) {
        this.timeToLive = (timeToLive == null || timeToLive.isZero()) ? null : timeToLive;
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertyCaching
    public void clear() {
        this.lastAccessed = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public T get(Supplier<T> factory, UnaryOperator<T> refreshAction) {
        T value = getValue();
        if (value == null) {
            value = refreshAction.apply(factory.get());
            setValue(value);
        } else if (hasExpired()) {
            value = refreshAction.apply(value);
            setValue(value);
        }
        if (!this.neverExpire) {
            this.lastAccessed = now();
        }
        return value;
    }

    private boolean hasExpired() {
        if (this.neverExpire) {
            return false;
        }
        Duration timeToLive = this.timeToLive;
        Instant lastAccessed = this.lastAccessed;
        if (timeToLive == null || lastAccessed == null) {
            return true;
        }
        return !UNLIMITED.equals(timeToLive) && now().isAfter(lastAccessed.plus((TemporalAmount) timeToLive));
    }

    protected Instant now() {
        return Instant.now();
    }

    protected T getValue() {
        return this.value.get();
    }

    protected void setValue(T value) {
        this.value = new SoftReference<>(value);
    }
}
