package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/MDCConverter.class */
public class MDCConverter extends ClassicConverter {
    private String key;
    private String defaultValue = "";

    @Override // ch.qos.logback.core.pattern.DynamicConverter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        String[] keyInfo = OptionHelper.extractDefaultReplacement(getFirstOption());
        this.key = keyInfo[0];
        if (keyInfo[1] != null) {
            this.defaultValue = keyInfo[1];
        }
        super.start();
    }

    @Override // ch.qos.logback.core.pattern.DynamicConverter, ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        this.key = null;
        super.stop();
    }

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent event) {
        Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
        if (mdcPropertyMap == null) {
            return this.defaultValue;
        }
        if (this.key == null) {
            return outputMDCForAllKeys(mdcPropertyMap);
        }
        String value = mdcPropertyMap.get(this.key);
        if (value != null) {
            return value;
        }
        return this.defaultValue;
    }

    private String outputMDCForAllKeys(Map<String, String> mdcPropertyMap) {
        StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : mdcPropertyMap.entrySet()) {
            if (first) {
                first = false;
            } else {
                buf.append(", ");
            }
            buf.append(entry.getKey()).append('=').append(entry.getValue());
        }
        return buf.toString();
    }

    public String getKey() {
        return this.key;
    }
}
