package org.jooq.impl;

import java.io.Serializable;
import org.jooq.RecordListener;
import org.jooq.RecordListenerProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordListenerProvider.class */
public class DefaultRecordListenerProvider implements RecordListenerProvider, Serializable {
    private final RecordListener listener;

    public static RecordListenerProvider[] providers(RecordListener... listeners) {
        return (RecordListenerProvider[]) Tools.map(listeners, DefaultRecordListenerProvider::new, x$0 -> {
            return new RecordListenerProvider[x$0];
        });
    }

    public DefaultRecordListenerProvider(RecordListener listener) {
        this.listener = listener;
    }

    @Override // org.jooq.RecordListenerProvider
    public final RecordListener provide() {
        return this.listener;
    }

    public String toString() {
        return this.listener.toString();
    }
}
