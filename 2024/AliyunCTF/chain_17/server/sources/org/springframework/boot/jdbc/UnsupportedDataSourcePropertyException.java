package org.springframework.boot.jdbc;

import java.util.function.Supplier;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/UnsupportedDataSourcePropertyException.class */
public class UnsupportedDataSourcePropertyException extends RuntimeException {
    UnsupportedDataSourcePropertyException(String message) {
        super(message);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void throwIf(boolean test, Supplier<String> message) {
        if (test) {
            throw new UnsupportedDataSourcePropertyException(message.get());
        }
    }
}
