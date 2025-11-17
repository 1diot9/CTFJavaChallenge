package org.springframework.boot.r2dbc;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Wrapped;
import org.reactivestreams.Publisher;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/r2dbc/OptionsCapableConnectionFactory.class */
public class OptionsCapableConnectionFactory implements Wrapped<ConnectionFactory>, ConnectionFactory {
    private final ConnectionFactoryOptions options;
    private final ConnectionFactory delegate;

    public OptionsCapableConnectionFactory(ConnectionFactoryOptions options, ConnectionFactory delegate) {
        this.options = options;
        this.delegate = delegate;
    }

    public ConnectionFactoryOptions getOptions() {
        return this.options;
    }

    @Override // io.r2dbc.spi.ConnectionFactory
    public Publisher<? extends Connection> create() {
        return this.delegate.create();
    }

    @Override // io.r2dbc.spi.ConnectionFactory
    public ConnectionFactoryMetadata getMetadata() {
        return this.delegate.getMetadata();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // io.r2dbc.spi.Wrapped
    public ConnectionFactory unwrap() {
        return this.delegate;
    }

    public static OptionsCapableConnectionFactory unwrapFrom(ConnectionFactory connectionFactory) {
        if (connectionFactory instanceof OptionsCapableConnectionFactory) {
            return (OptionsCapableConnectionFactory) connectionFactory;
        }
        if (connectionFactory instanceof Wrapped) {
            Object unwrapped = ((Wrapped) connectionFactory).unwrap();
            if (unwrapped instanceof ConnectionFactory) {
                return unwrapFrom((ConnectionFactory) unwrapped);
            }
            return null;
        }
        return null;
    }
}
