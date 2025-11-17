package org.jooq.impl;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import org.reactivestreams.Publisher;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/NoConnectionFactory.class */
final class NoConnectionFactory implements ConnectionFactory {
    @Override // io.r2dbc.spi.ConnectionFactory
    public Publisher<? extends Connection> create() {
        throw new UnsupportedOperationException("No ConnectionFactory configured");
    }

    @Override // io.r2dbc.spi.ConnectionFactory
    public ConnectionFactoryMetadata getMetadata() {
        return () -> {
            return "jOOQ NoConnectionFactory";
        };
    }
}
