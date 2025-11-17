package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;
import ch.qos.logback.core.pattern.Converter;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/PrefixCompositeConverter.class */
public class PrefixCompositeConverter extends CompositeConverter<ILoggingEvent> {
    @Override // ch.qos.logback.core.pattern.CompositeConverter, ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent event) {
        StringBuilder buf = new StringBuilder();
        Converter<ILoggingEvent> childConverter = getChildConverter();
        Converter<ILoggingEvent> converter = childConverter;
        while (true) {
            Converter<ILoggingEvent> c = converter;
            if (c != null) {
                if (c instanceof MDCConverter) {
                    MDCConverter mdcConverter = (MDCConverter) c;
                    String key = mdcConverter.getKey();
                    if (key != null) {
                        buf.append(key).append("=");
                    }
                } else if (c instanceof PropertyConverter) {
                    PropertyConverter pc = (PropertyConverter) c;
                    String key2 = pc.getKey();
                    if (key2 != null) {
                        buf.append(key2).append("=");
                    }
                } else {
                    String classOfConverter = c.getClass().getName();
                    String key3 = PatternLayout.CONVERTER_CLASS_TO_KEY_MAP.get(classOfConverter);
                    if (key3 != null) {
                        buf.append(key3).append("=");
                    }
                }
                buf.append(c.convert(event));
                converter = c.getNext();
            } else {
                return buf.toString();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.pattern.CompositeConverter
    public String transform(ILoggingEvent event, String in) {
        throw new UnsupportedOperationException();
    }
}
