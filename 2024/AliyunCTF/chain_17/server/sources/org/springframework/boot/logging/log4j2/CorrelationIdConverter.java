package org.springframework.boot.logging.log4j2;

import ch.qos.logback.classic.encoder.JsonEncoder;
import java.util.Objects;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.springframework.boot.logging.CorrelationIdFormatter;
import org.springframework.util.ObjectUtils;

@ConverterKeys({"correlationId"})
@Plugin(name = "CorrelationIdConverter", category = "Converter")
@PerformanceSensitive({"allocation"})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/log4j2/CorrelationIdConverter.class */
public final class CorrelationIdConverter extends LogEventPatternConverter {
    private final CorrelationIdFormatter formatter;

    private CorrelationIdConverter(CorrelationIdFormatter formatter) {
        super("correlationId{%s}".formatted(formatter), JsonEncoder.MDC_ATTR_NAME);
        this.formatter = formatter;
    }

    public void format(LogEvent event, StringBuilder toAppendTo) {
        ReadOnlyStringMap contextData = event.getContextData();
        CorrelationIdFormatter correlationIdFormatter = this.formatter;
        Objects.requireNonNull(contextData);
        correlationIdFormatter.formatTo(contextData::getValue, toAppendTo);
    }

    public static CorrelationIdConverter newInstance(String[] options) {
        String pattern = !ObjectUtils.isEmpty((Object[]) options) ? options[0] : null;
        return new CorrelationIdConverter(CorrelationIdFormatter.of(pattern));
    }
}
