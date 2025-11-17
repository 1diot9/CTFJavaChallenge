package org.springframework.boot.env;

import java.util.HexFormat;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.log.LogMessage;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/env/RandomValuePropertySource.class */
public class RandomValuePropertySource extends PropertySource<Random> {
    public static final String RANDOM_PROPERTY_SOURCE_NAME = "random";
    private static final String PREFIX = "random.";
    private static final Log logger = LogFactory.getLog((Class<?>) RandomValuePropertySource.class);

    public RandomValuePropertySource() {
        this(RANDOM_PROPERTY_SOURCE_NAME);
    }

    public RandomValuePropertySource(String name) {
        super(name, new Random());
    }

    @Override // org.springframework.core.env.PropertySource
    public Object getProperty(String name) {
        if (!name.startsWith(PREFIX)) {
            return null;
        }
        logger.trace(LogMessage.format("Generating random property for '%s'", name));
        return getRandomValue(name.substring(PREFIX.length()));
    }

    private Object getRandomValue(String type) {
        if (type.equals("int")) {
            return Integer.valueOf(getSource().nextInt());
        }
        if (type.equals("long")) {
            return Long.valueOf(getSource().nextLong());
        }
        String range = getRange(type, "int");
        if (range != null) {
            return Integer.valueOf(getNextIntInRange(Range.of(range, Integer::parseInt)));
        }
        String range2 = getRange(type, "long");
        if (range2 != null) {
            return Long.valueOf(getNextLongInRange(Range.of(range2, Long::parseLong)));
        }
        if (type.equals("uuid")) {
            return UUID.randomUUID().toString();
        }
        return getRandomBytes();
    }

    private String getRange(String type, String prefix) {
        int startIndex;
        if (type.startsWith(prefix) && type.length() > (startIndex = prefix.length() + 1)) {
            return type.substring(startIndex, type.length() - 1);
        }
        return null;
    }

    private int getNextIntInRange(Range<Integer> range) {
        OptionalInt first = getSource().ints(1L, range.getMin().intValue(), range.getMax().intValue()).findFirst();
        assertPresent(first.isPresent(), range);
        return first.getAsInt();
    }

    private long getNextLongInRange(Range<Long> range) {
        OptionalLong first = getSource().longs(1L, range.getMin().longValue(), range.getMax().longValue()).findFirst();
        assertPresent(first.isPresent(), range);
        return first.getAsLong();
    }

    private void assertPresent(boolean present, Range<?> range) {
        Assert.state(present, (Supplier<String>) () -> {
            return "Could not get random number for range '" + range + "'";
        });
    }

    private Object getRandomBytes() {
        byte[] bytes = new byte[16];
        getSource().nextBytes(bytes);
        return HexFormat.of().withLowerCase().formatHex(bytes);
    }

    public static void addToEnvironment(ConfigurableEnvironment environment) {
        addToEnvironment(environment, logger);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void addToEnvironment(ConfigurableEnvironment environment, Log logger2) {
        MutablePropertySources sources = environment.getPropertySources();
        PropertySource<?> existing = sources.get(RANDOM_PROPERTY_SOURCE_NAME);
        if (existing != null) {
            logger2.trace("RandomValuePropertySource already present");
            return;
        }
        RandomValuePropertySource randomSource = new RandomValuePropertySource(RANDOM_PROPERTY_SOURCE_NAME);
        if (sources.get("systemEnvironment") != null) {
            sources.addAfter("systemEnvironment", randomSource);
        } else {
            sources.addLast(randomSource);
        }
        logger2.trace("RandomValuePropertySource add to Environment");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/env/RandomValuePropertySource$Range.class */
    public static final class Range<T extends Number> {
        private final String value;
        private final T min;
        private final T max;

        private Range(String value, T min, T max) {
            this.value = value;
            this.min = min;
            this.max = max;
        }

        T getMin() {
            return this.min;
        }

        T getMax() {
            return this.max;
        }

        public String toString() {
            return this.value;
        }

        static <T extends Number & Comparable<T>> Range<T> of(String value, Function<String, T> parse) {
            T zero = parse.apply(CustomBooleanEditor.VALUE_0);
            String[] tokens = StringUtils.commaDelimitedListToStringArray(value);
            T min = parse.apply(tokens[0]);
            if (tokens.length == 1) {
                Assert.isTrue(((Comparable) min).compareTo(zero) > 0, "Bound must be positive.");
                return new Range<>(value, zero, min);
            }
            T max = parse.apply(tokens[1]);
            Assert.isTrue(((Comparable) min).compareTo(max) < 0, "Lower bound must be less than upper bound.");
            return new Range<>(value, min, max);
        }
    }
}
