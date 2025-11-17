package io.micrometer.common;

import io.micrometer.common.lang.Nullable;
import java.util.Objects;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/ImmutableKeyValue.class */
public class ImmutableKeyValue implements KeyValue {
    private final String key;
    private final String value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ImmutableKeyValue(String key, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        this.key = key;
        this.value = value;
    }

    @Override // io.micrometer.common.KeyValue
    public String getKey() {
        return this.key;
    }

    @Override // io.micrometer.common.KeyValue
    public String getValue() {
        return this.value;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KeyValue that = (KeyValue) o;
        return Objects.equals(this.key, that.getKey()) && Objects.equals(this.value, that.getValue());
    }

    public int hashCode() {
        int result = this.key.hashCode();
        return (31 * result) + this.value.hashCode();
    }

    public String toString() {
        return "keyValue(" + this.key + "=" + this.value + ")";
    }
}
