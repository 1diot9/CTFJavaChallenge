package org.jooq.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Wrapper;
import org.jooq.Unwrapper;
import org.jooq.UnwrapperProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultUnwrapperProvider.class */
final class DefaultUnwrapperProvider implements UnwrapperProvider {
    static final Unwrapper INSTANCE = new DefaultUnwrapper();

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultUnwrapperProvider$DefaultUnwrapper.class */
    static final class DefaultUnwrapper implements Unwrapper {
        private static int maxUnwrappedConnections = 256;
        private static int maxUnwrappedStatements = 256;

        DefaultUnwrapper() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static final boolean isWrapperFor(Wrapper w, Class<?> iface) {
            try {
                return w.isWrapperFor(iface);
            } catch (SQLException e) {
                return false;
            }
        }

        @Override // org.jooq.Unwrapper
        public <T> T unwrap(Wrapper wrapper, Class<T> cls) {
            if (wrapper instanceof Connection) {
                return (T) unwrap((Connection) wrapper, (Class) cls);
            }
            if (wrapper instanceof Statement) {
                return (T) unwrap((Statement) wrapper, (Class) cls);
            }
            throw new IllegalArgumentException("Cannot unwrap: " + String.valueOf(wrapper));
        }

        /* JADX WARN: Multi-variable type inference failed */
        private <T> T unwrap(Connection connection, Class<T> iface) {
            return connection;
        }

        /* JADX WARN: Multi-variable type inference failed */
        private <T> T unwrap(Statement statement, Class<T> iface) {
            return statement;
        }
    }

    @Override // org.jooq.UnwrapperProvider
    public Unwrapper provide() {
        return INSTANCE;
    }
}
