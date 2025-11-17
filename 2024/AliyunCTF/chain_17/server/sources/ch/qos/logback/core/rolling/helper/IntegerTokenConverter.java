package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.pattern.DynamicConverter;
import ch.qos.logback.core.pattern.FormatInfo;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/helper/IntegerTokenConverter.class */
public class IntegerTokenConverter extends DynamicConverter<Object> implements MonoTypedConverter {
    public static final String CONVERTER_KEY = "i";

    public String convert(int i) {
        String s = Integer.toString(i);
        FormatInfo formattingInfo = getFormattingInfo();
        if (formattingInfo == null) {
            return s;
        }
        int min = formattingInfo.getMin();
        StringBuilder sbuf = new StringBuilder();
        for (int j = s.length(); j < min; j++) {
            sbuf.append('0');
        }
        return sbuf.append(s).toString();
    }

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Null argument forbidden");
        }
        if (o instanceof Integer) {
            Integer i = (Integer) o;
            return convert(i.intValue());
        }
        throw new IllegalArgumentException("Cannot convert " + String.valueOf(o) + " of type" + o.getClass().getName());
    }

    @Override // ch.qos.logback.core.rolling.helper.MonoTypedConverter
    public boolean isApplicable(Object o) {
        return o instanceof Integer;
    }
}
