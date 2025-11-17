package org.springframework.boot.convert;

import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/DurationStyle.class */
public enum DurationStyle {
    SIMPLE("^([+-]?\\d+)([a-zA-Z]{0,2})$") { // from class: org.springframework.boot.convert.DurationStyle.1
        @Override // org.springframework.boot.convert.DurationStyle
        public Duration parse(String value, ChronoUnit unit) {
            try {
                Matcher matcher = matcher(value);
                Assert.state(matcher.matches(), "Does not match simple duration pattern");
                String suffix = matcher.group(2);
                return (StringUtils.hasLength(suffix) ? Unit.fromSuffix(suffix) : Unit.fromChronoUnit(unit)).parse(matcher.group(1));
            } catch (Exception ex) {
                throw new IllegalArgumentException("'" + value + "' is not a valid simple duration", ex);
            }
        }

        @Override // org.springframework.boot.convert.DurationStyle
        public String print(Duration value, ChronoUnit unit) {
            return Unit.fromChronoUnit(unit).print(value);
        }
    },
    ISO8601("^[+-]?[pP].*$") { // from class: org.springframework.boot.convert.DurationStyle.2
        @Override // org.springframework.boot.convert.DurationStyle
        public Duration parse(String value, ChronoUnit unit) {
            try {
                return Duration.parse(value);
            } catch (Exception ex) {
                throw new IllegalArgumentException("'" + value + "' is not a valid ISO-8601 duration", ex);
            }
        }

        @Override // org.springframework.boot.convert.DurationStyle
        public String print(Duration value, ChronoUnit unit) {
            return value.toString();
        }
    };

    private final Pattern pattern;

    public abstract Duration parse(String value, ChronoUnit unit);

    public abstract String print(Duration value, ChronoUnit unit);

    DurationStyle(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    protected final boolean matches(String value) {
        return this.pattern.matcher(value).matches();
    }

    protected final Matcher matcher(String value) {
        return this.pattern.matcher(value);
    }

    public Duration parse(String value) {
        return parse(value, null);
    }

    public String print(Duration value) {
        return print(value, null);
    }

    public static Duration detectAndParse(String value) {
        return detectAndParse(value, null);
    }

    public static Duration detectAndParse(String value, ChronoUnit unit) {
        return detect(value).parse(value, unit);
    }

    public static DurationStyle detect(String value) {
        Assert.notNull(value, "Value must not be null");
        for (DurationStyle candidate : values()) {
            if (candidate.matches(value)) {
                return candidate;
            }
        }
        throw new IllegalArgumentException("'" + value + "' is not a valid duration");
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/DurationStyle$Unit.class */
    enum Unit {
        NANOS(ChronoUnit.NANOS, "ns", (v0) -> {
            return v0.toNanos();
        }),
        MICROS(ChronoUnit.MICROS, "us", duration -> {
            return Long.valueOf(duration.toNanos() / 1000);
        }),
        MILLIS(ChronoUnit.MILLIS, "ms", (v0) -> {
            return v0.toMillis();
        }),
        SECONDS(ChronoUnit.SECONDS, "s", (v0) -> {
            return v0.getSeconds();
        }),
        MINUTES(ChronoUnit.MINUTES, ANSIConstants.ESC_END, (v0) -> {
            return v0.toMinutes();
        }),
        HOURS(ChronoUnit.HOURS, "h", (v0) -> {
            return v0.toHours();
        }),
        DAYS(ChronoUnit.DAYS, DateTokenConverter.CONVERTER_KEY, (v0) -> {
            return v0.toDays();
        });

        private final ChronoUnit chronoUnit;
        private final String suffix;
        private final Function<Duration, Long> longValue;

        Unit(ChronoUnit chronoUnit, String suffix, Function toUnit) {
            this.chronoUnit = chronoUnit;
            this.suffix = suffix;
            this.longValue = toUnit;
        }

        public Duration parse(String value) {
            return Duration.of(Long.parseLong(value), this.chronoUnit);
        }

        public String print(Duration value) {
            long longValue = longValue(value);
            String str = this.suffix;
            return longValue + longValue;
        }

        public long longValue(Duration value) {
            return this.longValue.apply(value).longValue();
        }

        public static Unit fromChronoUnit(ChronoUnit chronoUnit) {
            if (chronoUnit == null) {
                return MILLIS;
            }
            for (Unit candidate : values()) {
                if (candidate.chronoUnit == chronoUnit) {
                    return candidate;
                }
            }
            throw new IllegalArgumentException("Unknown unit " + chronoUnit);
        }

        public static Unit fromSuffix(String suffix) {
            for (Unit candidate : values()) {
                if (candidate.suffix.equalsIgnoreCase(suffix)) {
                    return candidate;
                }
            }
            throw new IllegalArgumentException("Unknown unit '" + suffix + "'");
        }
    }
}
