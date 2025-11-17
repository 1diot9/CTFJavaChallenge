package io.r2dbc.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/ConnectionFactories.class */
public final class ConnectionFactories {
    private ConnectionFactories() {
    }

    @Nullable
    public static ConnectionFactory find(ConnectionFactoryOptions connectionFactoryOptions) {
        Assert.requireNonNull(connectionFactoryOptions, "connectionFactoryOptions must not be null");
        Iterator<ConnectionFactoryProvider> it = loadProviders().iterator();
        while (it.hasNext()) {
            ConnectionFactoryProvider provider = it.next();
            if (provider.supports(connectionFactoryOptions)) {
                return provider.create(connectionFactoryOptions);
            }
        }
        return null;
    }

    public static ConnectionFactory get(String url) {
        return get(ConnectionFactoryOptions.parse((CharSequence) Assert.requireNonNull(url, "R2DBC Connection URL must not be null")));
    }

    public static ConnectionFactory get(ConnectionFactoryOptions connectionFactoryOptions) {
        ConnectionFactory connectionFactory = find(connectionFactoryOptions);
        if (connectionFactory == null) {
            throw new IllegalStateException(String.format("Unable to create a ConnectionFactory for '%s'. Available drivers: [ %s ]", connectionFactoryOptions, getAvailableDrivers()));
        }
        return connectionFactory;
    }

    public static boolean supports(ConnectionFactoryOptions connectionFactoryOptions) {
        Assert.requireNonNull(connectionFactoryOptions, "connectionFactoryOptions must not be null");
        Iterator<ConnectionFactoryProvider> it = loadProviders().iterator();
        while (it.hasNext()) {
            ConnectionFactoryProvider provider = it.next();
            if (provider.supports(connectionFactoryOptions)) {
                return true;
            }
        }
        return false;
    }

    private static String getAvailableDrivers() {
        StringBuilder availableDrivers = new StringBuilder();
        Iterator<ConnectionFactoryProvider> it = loadProviders().iterator();
        while (it.hasNext()) {
            ConnectionFactoryProvider provider = it.next();
            if (availableDrivers.length() != 0) {
                availableDrivers.append(", ");
            }
            availableDrivers.append(provider.getDriver());
        }
        if (availableDrivers.length() == 0) {
            availableDrivers.append("None");
        }
        return availableDrivers.toString();
    }

    private static ServiceLoader<ConnectionFactoryProvider> loadProviders() {
        return ServiceLoader.load(ConnectionFactoryProvider.class, ConnectionFactoryProvider.class.getClassLoader());
    }
}
