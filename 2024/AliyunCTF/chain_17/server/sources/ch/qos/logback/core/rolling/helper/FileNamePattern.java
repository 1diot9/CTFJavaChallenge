package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.ConverterUtil;
import ch.qos.logback.core.pattern.LiteralConverter;
import ch.qos.logback.core.pattern.parser.Node;
import ch.qos.logback.core.pattern.parser.Parser;
import ch.qos.logback.core.pattern.util.AlmostAsIsEscapeUtil;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.ScanException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/helper/FileNamePattern.class */
public class FileNamePattern extends ContextAwareBase {
    static final Map<String, String> CONVERTER_MAP = new HashMap();
    String pattern;
    Converter<Object> headTokenConverter;

    static {
        CONVERTER_MAP.put(IntegerTokenConverter.CONVERTER_KEY, IntegerTokenConverter.class.getName());
        CONVERTER_MAP.put(DateTokenConverter.CONVERTER_KEY, DateTokenConverter.class.getName());
    }

    public FileNamePattern(String patternArg, Context contextArg) {
        setPattern(FileFilterUtil.slashify(patternArg));
        setContext(contextArg);
        parse();
        ConverterUtil.startConverters(this.headTokenConverter);
    }

    void parse() {
        try {
            String patternForParsing = escapeRightParantesis(this.pattern);
            Parser<Object> p = new Parser<>(patternForParsing, new AlmostAsIsEscapeUtil());
            p.setContext(this.context);
            Node t = p.parse();
            this.headTokenConverter = p.compile(t, CONVERTER_MAP);
        } catch (ScanException sce) {
            addError("Failed to parse pattern \"" + this.pattern + "\".", sce);
        }
    }

    String escapeRightParantesis(String in) {
        return this.pattern.replace(")", "\\)");
    }

    public String toString() {
        return this.pattern;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.pattern == null ? 0 : this.pattern.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FileNamePattern other = (FileNamePattern) obj;
        if (this.pattern == null) {
            if (other.pattern != null) {
                return false;
            }
            return true;
        }
        if (!this.pattern.equals(other.pattern)) {
            return false;
        }
        return true;
    }

    public DateTokenConverter<Object> getPrimaryDateTokenConverter() {
        Converter<Object> converter = this.headTokenConverter;
        while (true) {
            Converter<Object> p = converter;
            if (p != null) {
                if (p instanceof DateTokenConverter) {
                    DateTokenConverter<Object> dtc = (DateTokenConverter) p;
                    if (dtc.isPrimary()) {
                        return dtc;
                    }
                }
                converter = p.getNext();
            } else {
                return null;
            }
        }
    }

    public IntegerTokenConverter getIntegerTokenConverter() {
        Converter<Object> converter = this.headTokenConverter;
        while (true) {
            Converter<Object> p = converter;
            if (p != null) {
                if (p instanceof IntegerTokenConverter) {
                    return (IntegerTokenConverter) p;
                }
                converter = p.getNext();
            } else {
                return null;
            }
        }
    }

    public boolean hasIntegerTokenCOnverter() {
        IntegerTokenConverter itc = getIntegerTokenConverter();
        return itc != null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public String convertMultipleArguments(Object... objectList) {
        StringBuilder buf = new StringBuilder();
        Converter<Object> converter = this.headTokenConverter;
        while (true) {
            Converter converter2 = converter;
            if (converter2 != 0) {
                if (converter2 instanceof MonoTypedConverter) {
                    MonoTypedConverter monoTyped = (MonoTypedConverter) converter2;
                    for (Object o : objectList) {
                        if (monoTyped.isApplicable(o)) {
                            buf.append(converter2.convert(o));
                        }
                    }
                } else {
                    buf.append(converter2.convert(objectList));
                }
                converter = converter2.getNext();
            } else {
                return buf.toString();
            }
        }
    }

    public String convert(Object o) {
        StringBuilder buf = new StringBuilder();
        Converter<Object> converter = this.headTokenConverter;
        while (true) {
            Converter<Object> p = converter;
            if (p != null) {
                buf.append(p.convert(o));
                converter = p.getNext();
            } else {
                return buf.toString();
            }
        }
    }

    public String convertInt(int i) {
        return convert(Integer.valueOf(i));
    }

    public void setPattern(String pattern) {
        if (pattern != null) {
            this.pattern = pattern.trim();
        }
    }

    public String getPattern() {
        return this.pattern;
    }

    public String toRegexForFixedDate(Date date) {
        StringBuilder buf = new StringBuilder();
        Converter<Object> converter = this.headTokenConverter;
        while (true) {
            Converter<Object> p = converter;
            if (p != null) {
                if (p instanceof LiteralConverter) {
                    buf.append(p.convert(null));
                } else if (p instanceof IntegerTokenConverter) {
                    buf.append("(\\d+)");
                } else if (p instanceof DateTokenConverter) {
                    buf.append(p.convert(date));
                }
                converter = p.getNext();
            } else {
                return buf.toString();
            }
        }
    }

    public String toRegexForFixedDate(Instant instant) {
        StringBuilder buf = new StringBuilder();
        Converter<Object> converter = this.headTokenConverter;
        while (true) {
            Converter<Object> p = converter;
            if (p != null) {
                if (p instanceof LiteralConverter) {
                    buf.append(p.convert(null));
                } else if (p instanceof IntegerTokenConverter) {
                    buf.append("(\\d+)");
                } else if (p instanceof DateTokenConverter) {
                    buf.append(p.convert(instant));
                }
                converter = p.getNext();
            } else {
                return buf.toString();
            }
        }
    }

    public String toRegex() {
        StringBuilder buf = new StringBuilder();
        Converter<Object> converter = this.headTokenConverter;
        while (true) {
            Converter<Object> p = converter;
            if (p != null) {
                if (p instanceof LiteralConverter) {
                    buf.append(p.convert(null));
                } else if (p instanceof IntegerTokenConverter) {
                    buf.append("\\d+");
                } else if (p instanceof DateTokenConverter) {
                    DateTokenConverter<Object> dtc = (DateTokenConverter) p;
                    buf.append(dtc.toRegex());
                }
                converter = p.getNext();
            } else {
                return buf.toString();
            }
        }
    }
}
