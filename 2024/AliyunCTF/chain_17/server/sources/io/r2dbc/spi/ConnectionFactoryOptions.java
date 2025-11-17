package io.r2dbc.spi;

import ch.qos.logback.classic.ClassicConstants;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/ConnectionFactoryOptions.class */
public final class ConnectionFactoryOptions {
    public static final Option<Duration> CONNECT_TIMEOUT = Option.valueOf("connectTimeout");
    public static final Option<String> DATABASE = Option.valueOf("database");
    public static final Option<String> DRIVER = Option.valueOf("driver");
    public static final Option<String> HOST = Option.valueOf("host");
    public static final Option<Duration> LOCK_WAIT_TIMEOUT = Option.valueOf("lockWaitTimeout");
    public static final Option<CharSequence> PASSWORD = Option.sensitiveValueOf("password");
    public static final Option<Integer> PORT = Option.valueOf("port");
    public static final Option<String> PROTOCOL = Option.valueOf("protocol");
    public static final Option<Boolean> SSL = Option.valueOf("ssl");
    public static final Option<Duration> STATEMENT_TIMEOUT = Option.valueOf("statementTimeout");
    public static final Option<String> USER = Option.valueOf(ClassicConstants.USER_MDC_KEY);
    private final Map<Option<?>, Object> options;

    private ConnectionFactoryOptions(Map<Option<?>, Object> options) {
        this.options = (Map) Assert.requireNonNull(options, "options must not be null");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ConnectionFactoryOptions parse(CharSequence url) {
        return ConnectionUrlParser.parseQuery((CharSequence) Assert.requireNonNull(url, "R2DBC Connection URL must not be null"));
    }

    public Builder mutate() {
        return new Builder().from(this);
    }

    public Object getRequiredValue(Option<?> option) {
        Object value = getValue(option);
        if (value != null) {
            return value;
        }
        throw new NoSuchOptionException(String.format("No value found for %s", option.name()), option);
    }

    @Nullable
    public Object getValue(Option<?> option) {
        Assert.requireNonNull(option, "option must not be null");
        return this.options.get(option);
    }

    public boolean hasOption(Option<?> option) {
        Assert.requireNonNull(option, "option must not be null");
        return this.options.containsKey(option);
    }

    public String toString() {
        return "ConnectionFactoryOptions{options=" + toString(this.options) + '}';
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String toString(Map<Option<?>, Object> options) {
        List<String> o = new ArrayList<>(options.size());
        for (Map.Entry<Option<?>, Object> entry : options.entrySet()) {
            String key = entry.getKey().name();
            Object value = entry.getKey().sensitive() ? "REDACTED" : entry.getValue();
            o.add(String.format("%s=%s", key, value));
        }
        return String.format("{%s}", String.join(", ", o));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConnectionFactoryOptions that = (ConnectionFactoryOptions) o;
        return this.options.equals(that.options);
    }

    public int hashCode() {
        return Objects.hash(this.options);
    }

    /* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/ConnectionFactoryOptions$Builder.class */
    public static final class Builder {
        private final Map<Option<?>, Object> options;

        private Builder() {
            this.options = new HashMap();
        }

        public ConnectionFactoryOptions build() {
            return new ConnectionFactoryOptions(new HashMap(this.options));
        }

        public Builder from(ConnectionFactoryOptions connectionFactoryOptions) {
            return from(connectionFactoryOptions, it -> {
                return true;
            });
        }

        public Builder from(ConnectionFactoryOptions connectionFactoryOptions, Predicate<Option<?>> filter) {
            Assert.requireNonNull(connectionFactoryOptions, "connectionFactoryOptions must not be null");
            Assert.requireNonNull(filter, "filter must not be null");
            connectionFactoryOptions.options.forEach((k, v) -> {
                if (filter.test(k)) {
                    this.options.put(k, v);
                }
            });
            return this;
        }

        public <T> Builder option(Option<T> option, T value) {
            Assert.requireNonNull(option, "option must not be null");
            Assert.requireNonNull(value, "value must not be null");
            this.options.put(option, value);
            return this;
        }

        public String toString() {
            return "Builder{options=" + ConnectionFactoryOptions.toString(this.options) + '}';
        }
    }
}
