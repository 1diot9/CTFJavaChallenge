package org.jooq.impl;

import java.io.Serializable;
import org.jooq.MigrationListener;
import org.jooq.MigrationListenerProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultMigrationListenerProvider.class */
public class DefaultMigrationListenerProvider implements MigrationListenerProvider, Serializable {
    private final MigrationListener listener;

    public static MigrationListenerProvider[] providers(MigrationListener... listeners) {
        return (MigrationListenerProvider[]) Tools.map(listeners, DefaultMigrationListenerProvider::new, x$0 -> {
            return new MigrationListenerProvider[x$0];
        });
    }

    public DefaultMigrationListenerProvider(MigrationListener listener) {
        this.listener = listener;
    }

    @Override // org.jooq.MigrationListenerProvider
    public final MigrationListener provide() {
        return this.listener;
    }

    public String toString() {
        return this.listener.toString();
    }
}
