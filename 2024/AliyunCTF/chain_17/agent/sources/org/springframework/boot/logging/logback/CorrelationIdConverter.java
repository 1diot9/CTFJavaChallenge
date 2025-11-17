package org.springframework.boot.logging.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.DynamicConverter;
import java.util.Map;
import java.util.Objects;
import org.springframework.boot.logging.CorrelationIdFormatter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/CorrelationIdConverter.class */
public class CorrelationIdConverter extends DynamicConverter<ILoggingEvent> {
    private CorrelationIdFormatter formatter;

    @Override // ch.qos.logback.core.pattern.DynamicConverter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        this.formatter = CorrelationIdFormatter.of(getOptionList());
        super.start();
    }

    @Override // ch.qos.logback.core.pattern.DynamicConverter, ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        this.formatter = null;
        super.stop();
    }

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent event) {
        if (this.formatter == null) {
            return "";
        }
        Map<String, String> mdc = event.getMDCPropertyMap();
        CorrelationIdFormatter correlationIdFormatter = this.formatter;
        Objects.requireNonNull(mdc);
        return correlationIdFormatter.format((v1) -> {
            return r1.get(v1);
        });
    }
}
