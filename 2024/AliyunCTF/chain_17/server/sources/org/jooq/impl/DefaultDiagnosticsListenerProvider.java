package org.jooq.impl;

import java.io.Serializable;
import org.jooq.DiagnosticsListener;
import org.jooq.DiagnosticsListenerProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultDiagnosticsListenerProvider.class */
public class DefaultDiagnosticsListenerProvider implements DiagnosticsListenerProvider, Serializable {
    private final DiagnosticsListener listener;

    public static DiagnosticsListenerProvider[] providers(DiagnosticsListener... listeners) {
        return (DiagnosticsListenerProvider[]) Tools.map(listeners, DefaultDiagnosticsListenerProvider::new, x$0 -> {
            return new DiagnosticsListenerProvider[x$0];
        });
    }

    public DefaultDiagnosticsListenerProvider(DiagnosticsListener listener) {
        this.listener = listener;
    }

    @Override // org.jooq.DiagnosticsListenerProvider
    public final DiagnosticsListener provide() {
        return this.listener;
    }

    public String toString() {
        return this.listener.toString();
    }
}
