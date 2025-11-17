package org.apache.logging.log4j.util;

import aQute.bnd.annotation.spi.ServiceConsumer;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Stream;
import org.apache.logging.log4j.util.PropertySource;

@ServiceConsumer(value = PropertySource.class, resolution = "optional", cardinality = "multiple")
/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/PropertiesUtil.class */
public final class PropertiesUtil {
    private static final String LOG4J_SYSTEM_PROPERTIES_FILE_NAME = "log4j2.system.properties";
    private final Environment environment;
    private static final String LOG4J_PROPERTIES_FILE_NAME = "log4j2.component.properties";
    private static final PropertiesUtil LOG4J_PROPERTIES = new PropertiesUtil(LOG4J_PROPERTIES_FILE_NAME, false);

    public PropertiesUtil(final Properties props) {
        this(new PropertiesPropertySource(props));
    }

    public PropertiesUtil(final String propertiesFileName) {
        this(propertiesFileName, true);
    }

    private PropertiesUtil(final String propertiesFileName, final boolean useTccl) {
        this(new PropertyFilePropertySource(propertiesFileName, useTccl));
    }

    PropertiesUtil(final PropertySource source) {
        this.environment = new Environment(source);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Properties loadClose(final InputStream in, final Object source) {
        Properties props = new Properties();
        if (null != in) {
            try {
                try {
                    props.load(in);
                    try {
                        in.close();
                    } catch (IOException e) {
                        LowLevelLogUtil.logException("Unable to close " + source, e);
                    }
                } catch (IOException e2) {
                    LowLevelLogUtil.logException("Unable to read " + source, e2);
                    try {
                        in.close();
                    } catch (IOException e3) {
                        LowLevelLogUtil.logException("Unable to close " + source, e3);
                    }
                }
            } catch (Throwable th) {
                try {
                    in.close();
                } catch (IOException e4) {
                    LowLevelLogUtil.logException("Unable to close " + source, e4);
                }
                throw th;
            }
        }
        return props;
    }

    public static PropertiesUtil getProperties() {
        return LOG4J_PROPERTIES;
    }

    public void addPropertySource(final PropertySource propertySource) {
        if (this.environment != null) {
            this.environment.addPropertySource(propertySource);
        }
    }

    public boolean hasProperty(final String name) {
        return this.environment.containsKey(name);
    }

    public boolean getBooleanProperty(final String name) {
        return getBooleanProperty(name, false);
    }

    public boolean getBooleanProperty(final String name, final boolean defaultValue) {
        String prop = getStringProperty(name);
        return prop == null ? defaultValue : "true".equalsIgnoreCase(prop);
    }

    public boolean getBooleanProperty(final String name, final boolean defaultValueIfAbsent, final boolean defaultValueIfPresent) {
        String prop = getStringProperty(name);
        return prop == null ? defaultValueIfAbsent : prop.isEmpty() ? defaultValueIfPresent : "true".equalsIgnoreCase(prop);
    }

    public Boolean getBooleanProperty(final String[] prefixes, final String key, final Supplier<Boolean> supplier) {
        for (String prefix : prefixes) {
            if (hasProperty(prefix + key)) {
                return Boolean.valueOf(getBooleanProperty(prefix + key));
            }
        }
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }

    public Charset getCharsetProperty(final String name) {
        return getCharsetProperty(name, Charset.defaultCharset());
    }

    public Charset getCharsetProperty(final String name, final Charset defaultValue) {
        String charsetName = getStringProperty(name);
        if (charsetName == null) {
            return defaultValue;
        }
        if (Charset.isSupported(charsetName)) {
            return Charset.forName(charsetName);
        }
        ResourceBundle bundle = getCharsetsResourceBundle();
        if (bundle.containsKey(name)) {
            String mapped = bundle.getString(name);
            if (Charset.isSupported(mapped)) {
                return Charset.forName(mapped);
            }
        }
        LowLevelLogUtil.log("Unable to get Charset '" + charsetName + "' for property '" + name + "', using default " + defaultValue + " and continuing.");
        return defaultValue;
    }

    public double getDoubleProperty(final String name, final double defaultValue) {
        String prop = getStringProperty(name);
        if (prop != null) {
            try {
                return Double.parseDouble(prop);
            } catch (Exception e) {
            }
        }
        return defaultValue;
    }

    public int getIntegerProperty(final String name, final int defaultValue) {
        String prop = getStringProperty(name);
        if (prop != null) {
            try {
                return Integer.parseInt(prop.trim());
            } catch (Exception e) {
            }
        }
        return defaultValue;
    }

    public Integer getIntegerProperty(final String[] prefixes, final String key, final Supplier<Integer> supplier) {
        for (String prefix : prefixes) {
            if (hasProperty(prefix + key)) {
                return Integer.valueOf(getIntegerProperty(prefix + key, 0));
            }
        }
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }

    public long getLongProperty(final String name, final long defaultValue) {
        String prop = getStringProperty(name);
        if (prop != null) {
            try {
                return Long.parseLong(prop);
            } catch (Exception e) {
            }
        }
        return defaultValue;
    }

    public Long getLongProperty(final String[] prefixes, final String key, final Supplier<Long> supplier) {
        for (String prefix : prefixes) {
            if (hasProperty(prefix + key)) {
                return Long.valueOf(getLongProperty(prefix + key, 0L));
            }
        }
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }

    public Duration getDurationProperty(final String name, final Duration defaultValue) {
        String prop = getStringProperty(name);
        if (prop != null) {
            return TimeUnit.getDuration(prop);
        }
        return defaultValue;
    }

    public Duration getDurationProperty(final String[] prefixes, final String key, final Supplier<Duration> supplier) {
        for (String prefix : prefixes) {
            if (hasProperty(prefix + key)) {
                return getDurationProperty(prefix + key, null);
            }
        }
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }

    public String getStringProperty(final String[] prefixes, final String key, final Supplier<String> supplier) {
        for (String prefix : prefixes) {
            String result = getStringProperty(prefix + key);
            if (result != null) {
                return result;
            }
        }
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }

    public String getStringProperty(final String name) {
        return this.environment.get(name);
    }

    public String getStringProperty(final String name, final String defaultValue) {
        String prop = getStringProperty(name);
        return prop == null ? defaultValue : prop;
    }

    public static Properties getSystemProperties() {
        try {
            return new Properties(System.getProperties());
        } catch (SecurityException ex) {
            LowLevelLogUtil.logException("Unable to access system properties.", ex);
            return new Properties();
        }
    }

    public void reload() {
        this.environment.reload();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/PropertiesUtil$Environment.class */
    public static final class Environment {
        private final Set<PropertySource> sources;
        private final Map<String, String> literal;
        private final Map<List<CharSequence>, String> tokenized;

        private Environment(final PropertySource propertySource) {
            this.sources = new ConcurrentSkipListSet(new PropertySource.Comparator());
            this.literal = new ConcurrentHashMap();
            this.tokenized = new ConcurrentHashMap();
            PropertyFilePropertySource sysProps = new PropertyFilePropertySource(PropertiesUtil.LOG4J_SYSTEM_PROPERTIES_FILE_NAME, false);
            try {
                sysProps.forEach((key, value) -> {
                    if (System.getProperty(key) == null) {
                        System.setProperty(key, value);
                    }
                });
            } catch (SecurityException e) {
            }
            this.sources.add(propertySource);
            Stream loadServices = ServiceLoaderUtil.loadServices(PropertySource.class, MethodHandles.lookup(), false, false);
            Set<PropertySource> set = this.sources;
            Objects.requireNonNull(set);
            loadServices.forEach((v1) -> {
                r1.add(v1);
            });
            reload();
        }

        public void addPropertySource(final PropertySource propertySource) {
            this.sources.add(propertySource);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void reload() {
            this.literal.clear();
            this.tokenized.clear();
            Set<String> keys = new HashSet<>();
            Stream<R> map = this.sources.stream().map((v0) -> {
                return v0.getPropertyNames();
            });
            Objects.requireNonNull(keys);
            map.forEach(keys::addAll);
            keys.stream().filter((v0) -> {
                return Objects.nonNull(v0);
            }).forEach(key -> {
                List<CharSequence> tokens = PropertySource.Util.tokenize(key);
                boolean hasTokens = !tokens.isEmpty();
                this.sources.forEach(source -> {
                    if (source.containsProperty(key)) {
                        String value = source.getProperty(key);
                        if (hasTokens) {
                            this.tokenized.putIfAbsent(tokens, value);
                        }
                    }
                    if (hasTokens) {
                        String normalKey = Objects.toString(source.getNormalForm(tokens), null);
                        if (normalKey != null && source.containsProperty(normalKey)) {
                            this.literal.putIfAbsent(key, source.getProperty(normalKey));
                        } else if (source.containsProperty(key)) {
                            this.literal.putIfAbsent(key, source.getProperty(key));
                        }
                    }
                });
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String get(final String key) {
            String normalKey;
            if (this.literal.containsKey(key)) {
                return this.literal.get(key);
            }
            List<CharSequence> tokens = PropertySource.Util.tokenize(key);
            boolean hasTokens = !tokens.isEmpty();
            for (PropertySource source : this.sources) {
                if (hasTokens && (normalKey = Objects.toString(source.getNormalForm(tokens), null)) != null && source.containsProperty(normalKey)) {
                    return source.getProperty(normalKey);
                }
                if (source.containsProperty(key)) {
                    return source.getProperty(key);
                }
            }
            return this.tokenized.get(tokens);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean containsKey(final String key) {
            List<CharSequence> tokens = PropertySource.Util.tokenize(key);
            return this.literal.containsKey(key) || this.tokenized.containsKey(tokens) || this.sources.stream().anyMatch(s -> {
                CharSequence normalizedKey = s.getNormalForm(tokens);
                return s.containsProperty(key) || (normalizedKey != null && s.containsProperty(normalizedKey.toString()));
            });
        }
    }

    public static Properties extractSubset(final Properties properties, final String prefix) {
        Properties subset = new Properties();
        if (prefix == null || prefix.length() == 0) {
            return subset;
        }
        String prefixToMatch = prefix.charAt(prefix.length() - 1) != '.' ? prefix + '.' : prefix;
        List<String> keys = new ArrayList<>();
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(prefixToMatch)) {
                subset.setProperty(key.substring(prefixToMatch.length()), properties.getProperty(key));
                keys.add(key);
            }
        }
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            properties.remove(it.next());
        }
        return subset;
    }

    static ResourceBundle getCharsetsResourceBundle() {
        return ResourceBundle.getBundle("Log4j-charsets");
    }

    public static Map<String, Properties> partitionOnCommonPrefixes(final Properties properties) {
        return partitionOnCommonPrefixes(properties, false);
    }

    public static Map<String, Properties> partitionOnCommonPrefixes(final Properties properties, final boolean includeBaseKey) {
        Map<String, Properties> parts = new ConcurrentHashMap<>();
        for (String key : properties.stringPropertyNames()) {
            int idx = key.indexOf(46);
            if (idx < 0) {
                if (includeBaseKey) {
                    if (!parts.containsKey(key)) {
                        parts.put(key, new Properties());
                    }
                    parts.get(key).setProperty("", properties.getProperty(key));
                }
            } else {
                String prefix = key.substring(0, idx);
                if (!parts.containsKey(prefix)) {
                    parts.put(prefix, new Properties());
                }
                parts.get(prefix).setProperty(key.substring(idx + 1), properties.getProperty(key));
            }
        }
        return parts;
    }

    public boolean isOsWindows() {
        return getStringProperty("os.name", "").startsWith("Windows");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/PropertiesUtil$TimeUnit.class */
    public enum TimeUnit {
        NANOS("ns,nano,nanos,nanosecond,nanoseconds", ChronoUnit.NANOS),
        MICROS("us,micro,micros,microsecond,microseconds", ChronoUnit.MICROS),
        MILLIS("ms,milli,millis,millsecond,milliseconds", ChronoUnit.MILLIS),
        SECONDS("s,second,seconds", ChronoUnit.SECONDS),
        MINUTES("m,minute,minutes", ChronoUnit.MINUTES),
        HOURS("h,hour,hours", ChronoUnit.HOURS),
        DAYS("d,day,days", ChronoUnit.DAYS);

        private final String[] descriptions;
        private final ChronoUnit timeUnit;

        TimeUnit(final String descriptions, final ChronoUnit timeUnit) {
            this.descriptions = descriptions.split(",");
            this.timeUnit = timeUnit;
        }

        ChronoUnit getTimeUnit() {
            return this.timeUnit;
        }

        static Duration getDuration(final String time) {
            String value = time.trim();
            TemporalUnit temporalUnit = ChronoUnit.MILLIS;
            long timeVal = 0;
            for (TimeUnit timeUnit : values()) {
                for (String suffix : timeUnit.descriptions) {
                    if (value.endsWith(suffix)) {
                        temporalUnit = timeUnit.timeUnit;
                        timeVal = Long.parseLong(value.substring(0, value.length() - suffix.length()));
                    }
                }
            }
            return Duration.of(timeVal, temporalUnit);
        }
    }
}
