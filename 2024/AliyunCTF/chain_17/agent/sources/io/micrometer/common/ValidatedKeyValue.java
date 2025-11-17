package io.micrometer.common;

import java.util.function.Predicate;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/ValidatedKeyValue.class */
public class ValidatedKeyValue<T> implements KeyValue {
    private final String key;
    private final String value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValidatedKeyValue(String key, T value, Predicate<? super T> validator) {
        this.key = key;
        this.value = String.valueOf(assertValue(value, validator));
    }

    @Override // io.micrometer.common.KeyValue
    public String getKey() {
        return this.key;
    }

    @Override // io.micrometer.common.KeyValue
    public String getValue() {
        return this.value;
    }

    private T assertValue(T value, Predicate<? super T> validator) {
        if (!validator.test(value)) {
            throw new IllegalArgumentException("Argument [" + value + "] does not follow required format for key [" + this.key + "]");
        }
        return value;
    }

    public String toString() {
        return "keyValue(" + this.key + "=" + this.value + ")";
    }
}
