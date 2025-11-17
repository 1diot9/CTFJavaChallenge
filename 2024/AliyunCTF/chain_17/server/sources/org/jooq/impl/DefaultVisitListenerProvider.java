package org.jooq.impl;

import java.io.Serializable;
import org.jooq.VisitListener;
import org.jooq.VisitListenerProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultVisitListenerProvider.class */
public class DefaultVisitListenerProvider implements VisitListenerProvider, Serializable {
    private final VisitListener listener;

    public static VisitListenerProvider[] providers(VisitListener... listeners) {
        return (VisitListenerProvider[]) Tools.map(listeners, DefaultVisitListenerProvider::new, x$0 -> {
            return new VisitListenerProvider[x$0];
        });
    }

    public DefaultVisitListenerProvider(VisitListener listener) {
        this.listener = listener;
    }

    @Override // org.jooq.VisitListenerProvider
    public final VisitListener provide() {
        return this.listener;
    }

    public String toString() {
        return this.listener.toString();
    }
}
