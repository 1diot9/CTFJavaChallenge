package org.springframework.format;

import java.text.ParseException;
import java.util.Locale;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/Parser.class */
public interface Parser<T> {
    T parse(String text, Locale locale) throws ParseException;
}
