package org.springframework.core.convert.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.springframework.core.convert.converter.Converter;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/PropertiesToStringConverter.class */
final class PropertiesToStringConverter implements Converter<Properties, String> {
    @Override // org.springframework.core.convert.converter.Converter
    public String convert(Properties source) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream(256);
            source.store(os, (String) null);
            return os.toString(StandardCharsets.ISO_8859_1);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to store [" + source + "] into String", ex);
        }
    }
}
