package org.springframework.boot.logging.log4j2;

import ch.qos.logback.classic.encoder.JsonEncoder;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.ThrowablePatternConverter;

@ConverterKeys({"wEx", "wThrowable", "wException"})
@Plugin(name = "WhitespaceThrowablePatternConverter", category = "Converter")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/log4j2/WhitespaceThrowablePatternConverter.class */
public final class WhitespaceThrowablePatternConverter extends ThrowablePatternConverter {
    private WhitespaceThrowablePatternConverter(Configuration configuration, String[] options) {
        super("WhitespaceThrowable", JsonEncoder.THROWABLE_ATTR_NAME, options, configuration);
    }

    public void format(LogEvent event, StringBuilder buffer) {
        if (event.getThrown() != null) {
            buffer.append(this.options.getSeparator());
            super.format(event, buffer);
            buffer.append(this.options.getSeparator());
        }
    }

    public static WhitespaceThrowablePatternConverter newInstance(Configuration configuration, String[] options) {
        return new WhitespaceThrowablePatternConverter(configuration, options);
    }
}
