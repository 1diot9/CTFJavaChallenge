package org.springframework.format;

import java.util.Locale;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/Printer.class */
public interface Printer<T> {
    String print(T object, Locale locale);
}
