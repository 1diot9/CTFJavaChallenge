package org.jooq.impl;

import java.io.Serializable;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteListenerProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultExecuteListenerProvider.class */
public class DefaultExecuteListenerProvider implements ExecuteListenerProvider, Serializable {
    private final ExecuteListener listener;

    public static ExecuteListenerProvider[] providers(ExecuteListener... listeners) {
        return (ExecuteListenerProvider[]) Tools.map(listeners, DefaultExecuteListenerProvider::new, x$0 -> {
            return new ExecuteListenerProvider[x$0];
        });
    }

    public DefaultExecuteListenerProvider(ExecuteListener listener) {
        this.listener = listener;
    }

    @Override // org.jooq.ExecuteListenerProvider
    public final ExecuteListener provide() {
        return this.listener;
    }

    public String toString() {
        return this.listener.toString();
    }
}
