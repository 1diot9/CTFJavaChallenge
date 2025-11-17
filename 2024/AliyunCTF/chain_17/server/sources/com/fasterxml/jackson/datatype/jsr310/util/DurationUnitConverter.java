package com.fasterxml.jackson.datatype.jsr310.util;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/util/DurationUnitConverter.class */
public class DurationUnitConverter {
    private static final Map<String, DurationSerialization> UNITS;
    final DurationSerialization serialization;

    /* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/util/DurationUnitConverter$DurationSerialization.class */
    protected static class DurationSerialization {
        final Function<Duration, Long> serializer;
        final Function<Long, Duration> deserializer;

        DurationSerialization(Function<Duration, Long> serializer, Function<Long, Duration> deserializer) {
            this.serializer = serializer;
            this.deserializer = deserializer;
        }

        static Function<Long, Duration> deserializer(TemporalUnit unit) {
            return v -> {
                return Duration.of(v.longValue(), unit);
            };
        }
    }

    static {
        Map<String, DurationSerialization> units = new LinkedHashMap<>();
        units.put(ChronoUnit.NANOS.name(), new DurationSerialization((v0) -> {
            return v0.toNanos();
        }, DurationSerialization.deserializer(ChronoUnit.NANOS)));
        units.put(ChronoUnit.MICROS.name(), new DurationSerialization(d -> {
            return Long.valueOf(d.toNanos() / 1000);
        }, DurationSerialization.deserializer(ChronoUnit.MICROS)));
        units.put(ChronoUnit.MILLIS.name(), new DurationSerialization((v0) -> {
            return v0.toMillis();
        }, DurationSerialization.deserializer(ChronoUnit.MILLIS)));
        units.put(ChronoUnit.SECONDS.name(), new DurationSerialization((v0) -> {
            return v0.getSeconds();
        }, DurationSerialization.deserializer(ChronoUnit.SECONDS)));
        units.put(ChronoUnit.MINUTES.name(), new DurationSerialization((v0) -> {
            return v0.toMinutes();
        }, DurationSerialization.deserializer(ChronoUnit.MINUTES)));
        units.put(ChronoUnit.HOURS.name(), new DurationSerialization((v0) -> {
            return v0.toHours();
        }, DurationSerialization.deserializer(ChronoUnit.HOURS)));
        units.put(ChronoUnit.HALF_DAYS.name(), new DurationSerialization(d2 -> {
            return Long.valueOf(d2.toHours() / 12);
        }, DurationSerialization.deserializer(ChronoUnit.HALF_DAYS)));
        units.put(ChronoUnit.DAYS.name(), new DurationSerialization((v0) -> {
            return v0.toDays();
        }, DurationSerialization.deserializer(ChronoUnit.DAYS)));
        UNITS = units;
    }

    DurationUnitConverter(DurationSerialization serialization) {
        this.serialization = serialization;
    }

    public Duration convert(long value) {
        return this.serialization.deserializer.apply(Long.valueOf(value));
    }

    public long convert(Duration duration) {
        return this.serialization.serializer.apply(duration).longValue();
    }

    public static String descForAllowed() {
        return "\"" + ((String) UNITS.keySet().stream().collect(Collectors.joining("\", \""))) + "\"";
    }

    public static DurationUnitConverter from(String unit) {
        DurationSerialization def = UNITS.get(unit);
        if (def == null) {
            return null;
        }
        return new DurationUnitConverter(def);
    }
}
