package org.jooq.impl;

import java.sql.Connection;
import org.jetbrains.annotations.Nullable;
import org.jooq.ConnectionProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/NoConnectionProvider.class */
public class NoConnectionProvider implements ConnectionProvider {
    @Override // org.jooq.ConnectionProvider
    @Nullable
    public final Connection acquire() {
        return null;
    }

    @Override // org.jooq.ConnectionProvider
    public final void release(Connection connection) {
    }
}
