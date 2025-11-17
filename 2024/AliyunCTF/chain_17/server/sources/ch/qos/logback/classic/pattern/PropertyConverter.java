package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextVO;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/PropertyConverter.class */
public final class PropertyConverter extends ClassicConverter {
    String key;

    @Override // ch.qos.logback.core.pattern.DynamicConverter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        String optStr = getFirstOption();
        if (optStr != null) {
            this.key = optStr;
            super.start();
        }
    }

    public String getKey() {
        return this.key;
    }

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent event) {
        if (this.key == null) {
            return "Property_HAS_NO_KEY";
        }
        LoggerContextVO lcvo = event.getLoggerContextVO();
        Map<String, String> map = lcvo.getPropertyMap();
        String val = map.get(this.key);
        if (val != null) {
            return val;
        }
        return System.getProperty(this.key);
    }
}
