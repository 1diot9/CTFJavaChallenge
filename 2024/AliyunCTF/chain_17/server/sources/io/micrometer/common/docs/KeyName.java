package io.micrometer.common.docs;

import io.micrometer.common.KeyValue;
import java.util.Arrays;
import java.util.function.Predicate;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/docs/KeyName.class */
public interface KeyName {
    String asString();

    static KeyName[] merge(KeyName[]... keyNames) {
        return (KeyName[]) Arrays.stream(keyNames).flatMap((v0) -> {
            return Arrays.stream(v0);
        }).toArray(x$0 -> {
            return new KeyName[x$0];
        });
    }

    default KeyValue withValue(String value) {
        return KeyValue.of(this, value);
    }

    default KeyValue withValue(String value, Predicate<Object> validator) {
        return KeyValue.of(this, value, (Predicate<? super String>) validator);
    }

    default boolean isRequired() {
        return true;
    }
}
