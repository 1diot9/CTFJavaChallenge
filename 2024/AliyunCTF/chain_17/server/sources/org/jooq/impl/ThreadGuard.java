package org.jooq.impl;

import java.util.function.Supplier;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ThreadGuard.class */
final class ThreadGuard {
    static final Guard RECORD_TOSTRING = new Guard();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ThreadGuard$Guard.class */
    public static final class Guard {
        final ThreadLocal<Object> tl = new ThreadLocal<>();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ThreadGuard$GuardedOperation.class */
    interface GuardedOperation<V> {
        V unguarded();

        V guarded();
    }

    ThreadGuard() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void run(Guard guard, Runnable unguardedOperation, Runnable guardedOperation) {
        run(guard, () -> {
            unguardedOperation.run();
            return null;
        }, () -> {
            guardedOperation.run();
            return null;
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <V> V run(Guard guard, Supplier<V> unguardedOperation, Supplier<V> guardedOperation) {
        boolean unguarded = guard.tl.get() == null;
        if (unguarded) {
            guard.tl.set(Guard.class);
        }
        try {
            if (unguarded) {
                V v = unguardedOperation.get();
                if (unguarded) {
                    guard.tl.remove();
                }
                return v;
            }
            V v2 = guardedOperation.get();
            if (unguarded) {
                guard.tl.remove();
            }
            return v2;
        } catch (Throwable th) {
            if (unguarded) {
                guard.tl.remove();
            }
            throw th;
        }
    }
}
