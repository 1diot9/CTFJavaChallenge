package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.List;
import org.slf4j.event.KeyValuePair;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/KeyValuePairConverter.class */
public class KeyValuePairConverter extends ClassicConverter {
    static final String DOUBLE_OPTION_STR = "DOUBLE";
    static final String SINGLE_OPTION_STR = "SINGLE";
    static final String NONE_OPTION_STR = "NONE";
    ValueQuoteSpecification valueQuoteSpec = ValueQuoteSpecification.DOUBLE;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/KeyValuePairConverter$ValueQuoteSpecification.class */
    public enum ValueQuoteSpecification {
        NONE,
        SINGLE,
        DOUBLE;

        Character asChar() {
            switch (this) {
                case NONE:
                    return null;
                case SINGLE:
                    return '\'';
                case DOUBLE:
                    return '\"';
                default:
                    throw new IllegalStateException();
            }
        }
    }

    @Override // ch.qos.logback.core.pattern.DynamicConverter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        String optStr = getFirstOption();
        this.valueQuoteSpec = optionStrToSpec(optStr);
        super.start();
    }

    private ValueQuoteSpecification optionStrToSpec(String optStr) {
        if (optStr == null) {
            return ValueQuoteSpecification.DOUBLE;
        }
        if (DOUBLE_OPTION_STR.equalsIgnoreCase(optStr)) {
            return ValueQuoteSpecification.DOUBLE;
        }
        if (SINGLE_OPTION_STR.equalsIgnoreCase(optStr)) {
            return ValueQuoteSpecification.SINGLE;
        }
        if (NONE_OPTION_STR.equalsIgnoreCase(optStr)) {
            return ValueQuoteSpecification.NONE;
        }
        return ValueQuoteSpecification.DOUBLE;
    }

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent event) {
        List<KeyValuePair> kvpList = event.getKeyValuePairs();
        if (kvpList == null || kvpList.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < kvpList.size(); i++) {
            KeyValuePair kvp = kvpList.get(i);
            if (i != 0) {
                sb.append(' ');
            }
            sb.append(String.valueOf(kvp.key));
            sb.append('=');
            Character quoteChar = this.valueQuoteSpec.asChar();
            if (quoteChar != null) {
                sb.append(quoteChar);
            }
            sb.append(String.valueOf(kvp.value));
            if (quoteChar != null) {
                sb.append(quoteChar);
            }
        }
        return sb.toString();
    }
}
