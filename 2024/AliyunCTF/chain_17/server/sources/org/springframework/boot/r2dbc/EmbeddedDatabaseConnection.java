package org.springframework.boot.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/r2dbc/EmbeddedDatabaseConnection.class */
public enum EmbeddedDatabaseConnection {
    NONE(null, null, options -> {
        return false;
    }),
    H2("io.r2dbc.h2.H2ConnectionFactoryProvider", "r2dbc:h2:mem:///%s?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", options2 -> {
        return options2.getValue(ConnectionFactoryOptions.DRIVER).equals("h2") && options2.getValue(ConnectionFactoryOptions.PROTOCOL).equals("mem");
    });

    private final String driverClassName;
    private final String url;
    private final Predicate<ConnectionFactoryOptions> embedded;

    EmbeddedDatabaseConnection(String driverClassName, String url, Predicate embedded) {
        this.driverClassName = driverClassName;
        this.url = url;
        this.embedded = embedded;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public String getUrl(String databaseName) {
        Assert.hasText(databaseName, "DatabaseName must not be empty");
        if (this.url != null) {
            return String.format(this.url, databaseName);
        }
        return null;
    }

    public static EmbeddedDatabaseConnection get(ClassLoader classLoader) {
        for (EmbeddedDatabaseConnection candidate : values()) {
            if (candidate != NONE && ClassUtils.isPresent(candidate.getDriverClassName(), classLoader)) {
                return candidate;
            }
        }
        return NONE;
    }

    public static boolean isEmbedded(ConnectionFactory connectionFactory) {
        OptionsCapableConnectionFactory optionsCapable = OptionsCapableConnectionFactory.unwrapFrom(connectionFactory);
        Assert.notNull(optionsCapable, (Supplier<String>) () -> {
            return "Cannot determine database's type as ConnectionFactory is not options-capable. To be options-capable, a ConnectionFactory should be created with " + ConnectionFactoryBuilder.class.getName();
        });
        ConnectionFactoryOptions options = optionsCapable.getOptions();
        for (EmbeddedDatabaseConnection candidate : values()) {
            if (candidate.embedded.test(options)) {
                return true;
            }
        }
        return false;
    }
}
