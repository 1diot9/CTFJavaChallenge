package org.springframework.boot.convert;

import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/PeriodStyle.class */
public enum PeriodStyle {
    SIMPLE("^(?:([-+]?[0-9]+)Y)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)W)?(?:([-+]?[0-9]+)D)?$", 2) { // from class: org.springframework.boot.convert.PeriodStyle.1
        @Override // org.springframework.boot.convert.PeriodStyle
        public Period parse(String value, ChronoUnit unit) {
            try {
                if (PeriodStyle.NUMERIC.matcher(value).matches()) {
                    return Unit.fromChronoUnit(unit).parse(value);
                }
                Matcher matcher = matcher(value);
                Assert.state(matcher.matches(), "Does not match simple period pattern");
                Assert.isTrue(hasAtLeastOneGroupValue(matcher), (Supplier<String>) () -> {
                    return "'" + value + "' is not a valid simple period";
                });
                int years = parseInt(matcher, 1);
                int months = parseInt(matcher, 2);
                int weeks = parseInt(matcher, 3);
                int days = parseInt(matcher, 4);
                return Period.of(years, months, Math.addExact(Math.multiplyExact(weeks, 7), days));
            } catch (Exception ex) {
                throw new IllegalArgumentException("'" + value + "' is not a valid simple period", ex);
            }
        }

        boolean hasAtLeastOneGroupValue(Matcher matcher) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                if (matcher.group(i + 1) != null) {
                    return true;
                }
            }
            return false;
        }

        private int parseInt(Matcher matcher, int group) {
            String value = matcher.group(group);
            if (value != null) {
                return Integer.parseInt(value);
            }
            return 0;
        }

        @Override // org.springframework.boot.convert.PeriodStyle
        protected boolean matches(String value) {
            return PeriodStyle.NUMERIC.matcher(value).matches() || matcher(value).matches();
        }

        @Override // org.springframework.boot.convert.PeriodStyle
        public String print(Period value, ChronoUnit unit) {
            if (value.isZero()) {
                return Unit.fromChronoUnit(unit).print(value);
            }
            StringBuilder result = new StringBuilder();
            append(result, value, Unit.YEARS);
            append(result, value, Unit.MONTHS);
            append(result, value, Unit.DAYS);
            return result.toString();
        }

        private void append(StringBuilder result, Period value, Unit unit) {
            if (!unit.isZero(value)) {
                result.append(unit.print(value));
            }
        }
    },
    ISO8601("^[+-]?P.*$", 2) { // from class: org.springframework.boot.convert.PeriodStyle.2
        @Override // org.springframework.boot.convert.PeriodStyle
        public Period parse(String value, ChronoUnit unit) {
            try {
                return Period.parse(value);
            } catch (Exception ex) {
                throw new IllegalArgumentException("'" + value + "' is not a valid ISO-8601 period", ex);
            }
        }

        @Override // org.springframework.boot.convert.PeriodStyle
        public String print(Period value, ChronoUnit unit) {
            return value.toString();
        }
    };

    private static final Pattern NUMERIC = Pattern.compile("^[-+]?[0-9]+$");
    private final Pattern pattern;

    public abstract Period parse(String value, ChronoUnit unit);

    public abstract String print(Period value, ChronoUnit unit);

    PeriodStyle(String pattern, int flags) {
        this.pattern = Pattern.compile(pattern, flags);
    }

    protected boolean matches(String value) {
        return this.pattern.matcher(value).matches();
    }

    protected final Matcher matcher(String value) {
        return this.pattern.matcher(value);
    }

    public Period parse(String value) {
        return parse(value, null);
    }

    public String print(Period value) {
        return print(value, null);
    }

    public static Period detectAndParse(String value) {
        return detectAndParse(value, null);
    }

    public static Period detectAndParse(String value, ChronoUnit unit) {
        return detect(value).parse(value, unit);
    }

    public static PeriodStyle detect(String value) {
        Assert.notNull(value, "Value must not be null");
        for (PeriodStyle candidate : values()) {
            if (candidate.matches(value)) {
                return candidate;
            }
        }
        throw new IllegalArgumentException("'" + value + "' is not a valid period");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/PeriodStyle$Unit.class */
    public enum Unit {
        DAYS(ChronoUnit.DAYS, DateTokenConverter.CONVERTER_KEY, (v0) -> {
            return v0.getDays();
        }, (v0) -> {
            return Period.ofDays(v0);
        }),
        WEEKS(ChronoUnit.WEEKS, "w", null, (v0) -> {
            return Period.ofWeeks(v0);
        }),
        MONTHS(ChronoUnit.MONTHS, ANSIConstants.ESC_END, (v0) -> {
            return v0.getMonths();
        }, (v0) -> {
            return Period.ofMonths(v0);
        }),
        YEARS(ChronoUnit.YEARS, "y", (v0) -> {
            return v0.getYears();
        }, (v0) -> {
            return Period.ofYears(v0);
        });

        private final ChronoUnit chronoUnit;
        private final String suffix;
        private final Function<Period, Integer> intValue;
        private final Function<Integer, Period> factory;

        Unit(ChronoUnit chronoUnit, String suffix, Function intValue, Function factory) {
            this.chronoUnit = chronoUnit;
            this.suffix = suffix;
            this.intValue = intValue;
            this.factory = factory;
        }

        private Period parse(String value) {
            return this.factory.apply(Integer.valueOf(Integer.parseInt(value)));
        }

        private String print(Period value) {
            return intValue(value) + this.suffix;
        }

        private boolean isZero(Period value) {
            return intValue(value) == 0;
        }

        private int intValue(Period value) {
            Assert.notNull(this.intValue, (Supplier<String>) () -> {
                return "intValue cannot be extracted from " + name();
            });
            return this.intValue.apply(value).intValue();
        }

        private static Unit fromChronoUnit(ChronoUnit chronoUnit) {
            if (chronoUnit == null) {
                return DAYS;
            }
            for (Unit candidate : values()) {
                if (candidate.chronoUnit == chronoUnit) {
                    return candidate;
                }
            }
            throw new IllegalArgumentException("Unsupported unit " + chronoUnit);
        }
    }
}
