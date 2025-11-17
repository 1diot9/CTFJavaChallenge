package org.jooq.impl;

import java.util.Arrays;
import org.jooq.Configuration;
import org.jooq.MigrationContext;
import org.jooq.MigrationListener;
import org.jooq.conf.InvocationOrder;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/MigrationListeners.class */
public class MigrationListeners implements MigrationListener {
    private final MigrationListener[] listeners;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MigrationListeners(Configuration configuration) {
        this.listeners = (MigrationListener[]) Tools.map(configuration.migrationListenerProviders(), p -> {
            return p.provide();
        }, x$0 -> {
            return new MigrationListener[x$0];
        });
    }

    @Override // org.jooq.MigrationListener
    public final void migrationStart(MigrationContext ctx) {
        Iterable<MigrationListener> reverseIterable;
        if (ctx.settings().getMigrationListenerStartInvocationOrder() != InvocationOrder.REVERSE) {
            reverseIterable = Arrays.asList(this.listeners);
        } else {
            reverseIterable = Tools.reverseIterable(this.listeners);
        }
        for (MigrationListener listener : reverseIterable) {
            listener.migrationStart(ctx);
        }
    }

    @Override // org.jooq.MigrationListener
    public final void migrationEnd(MigrationContext ctx) {
        Iterable<MigrationListener> reverseIterable;
        if (ctx.settings().getMigrationListenerEndInvocationOrder() != InvocationOrder.REVERSE) {
            reverseIterable = Arrays.asList(this.listeners);
        } else {
            reverseIterable = Tools.reverseIterable(this.listeners);
        }
        for (MigrationListener listener : reverseIterable) {
            listener.migrationEnd(ctx);
        }
    }

    @Override // org.jooq.MigrationListener
    public final void queriesStart(MigrationContext ctx) {
        Iterable<MigrationListener> reverseIterable;
        if (ctx.settings().getMigrationListenerStartInvocationOrder() != InvocationOrder.REVERSE) {
            reverseIterable = Arrays.asList(this.listeners);
        } else {
            reverseIterable = Tools.reverseIterable(this.listeners);
        }
        for (MigrationListener listener : reverseIterable) {
            listener.queriesStart(ctx);
        }
    }

    @Override // org.jooq.MigrationListener
    public final void queriesEnd(MigrationContext ctx) {
        Iterable<MigrationListener> reverseIterable;
        if (ctx.settings().getMigrationListenerEndInvocationOrder() != InvocationOrder.REVERSE) {
            reverseIterable = Arrays.asList(this.listeners);
        } else {
            reverseIterable = Tools.reverseIterable(this.listeners);
        }
        for (MigrationListener listener : reverseIterable) {
            listener.queriesEnd(ctx);
        }
    }

    @Override // org.jooq.MigrationListener
    public final void queryStart(MigrationContext ctx) {
        Iterable<MigrationListener> reverseIterable;
        if (ctx.settings().getMigrationListenerStartInvocationOrder() != InvocationOrder.REVERSE) {
            reverseIterable = Arrays.asList(this.listeners);
        } else {
            reverseIterable = Tools.reverseIterable(this.listeners);
        }
        for (MigrationListener listener : reverseIterable) {
            listener.queryStart(ctx);
        }
    }

    @Override // org.jooq.MigrationListener
    public final void queryEnd(MigrationContext ctx) {
        Iterable<MigrationListener> reverseIterable;
        if (ctx.settings().getMigrationListenerEndInvocationOrder() != InvocationOrder.REVERSE) {
            reverseIterable = Arrays.asList(this.listeners);
        } else {
            reverseIterable = Tools.reverseIterable(this.listeners);
        }
        for (MigrationListener listener : reverseIterable) {
            listener.queryEnd(ctx);
        }
    }
}
