package org.jooq.impl;

import java.util.function.Predicate;
import org.jooq.Configuration;
import org.jooq.DiagnosticsContext;
import org.jooq.DiagnosticsListener;
import org.jooq.DiagnosticsListenerProvider;
import org.jooq.conf.Settings;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DiagnosticsListeners.class */
public final class DiagnosticsListeners implements DiagnosticsListener {
    final DiagnosticsListener[] listeners;

    DiagnosticsListeners(DiagnosticsListenerProvider[] providers) {
        this.listeners = (DiagnosticsListener[]) Tools.map(providers, p -> {
            return p.provide();
        }, x$0 -> {
            return new DiagnosticsListener[x$0];
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final DiagnosticsListeners get(Configuration configuration) {
        DiagnosticsListenerProvider[] p = configuration.diagnosticsListenerProviders();
        if (!Boolean.FALSE.equals(configuration.settings().isDiagnosticsLogging())) {
            p = (DiagnosticsListenerProvider[]) Tools.combine((Object[]) DefaultDiagnosticsListenerProvider.providers(new LoggingDiagnosticsListener()), (Object[]) p);
        }
        return new DiagnosticsListeners(p);
    }

    private static final boolean check(DiagnosticsContext ctx, Predicate<? super Settings> test) {
        return check(ctx.settings(), test);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean check(Settings settings, Predicate<? super Settings> test) {
        return !Boolean.FALSE.equals(Boolean.valueOf(test.test(settings)));
    }

    private static final boolean checkPattern(DiagnosticsContext ctx, Predicate<? super Settings> test) {
        return checkPattern(ctx.settings(), test);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean checkPattern(Settings settings, Predicate<? super Settings> test) {
        return !Boolean.FALSE.equals(settings.isDiagnosticsPatterns()) && check(settings, test);
    }

    @Override // org.jooq.DiagnosticsListener
    public final void tooManyRowsFetched(DiagnosticsContext ctx) {
        if (check(ctx, (Predicate<? super Settings>) (v0) -> {
            return v0.isDiagnosticsTooManyRowsFetched();
        })) {
            for (DiagnosticsListener listener : this.listeners) {
                listener.tooManyRowsFetched(ctx);
            }
        }
    }

    @Override // org.jooq.DiagnosticsListener
    public final void tooManyColumnsFetched(DiagnosticsContext ctx) {
        if (check(ctx, (Predicate<? super Settings>) (v0) -> {
            return v0.isDiagnosticsTooManyColumnsFetched();
        })) {
            for (DiagnosticsListener listener : this.listeners) {
                listener.tooManyColumnsFetched(ctx);
            }
        }
    }

    @Override // org.jooq.DiagnosticsListener
    public final void unnecessaryWasNullCall(DiagnosticsContext ctx) {
        if (check(ctx, (Predicate<? super Settings>) (v0) -> {
            return v0.isDiagnosticsUnnecessaryWasNullCall();
        })) {
            for (DiagnosticsListener listener : this.listeners) {
                listener.unnecessaryWasNullCall(ctx);
            }
        }
    }

    @Override // org.jooq.DiagnosticsListener
    public final void missingWasNullCall(DiagnosticsContext ctx) {
        if (check(ctx, (Predicate<? super Settings>) (v0) -> {
            return v0.isDiagnosticsMissingWasNullCall();
        })) {
            for (DiagnosticsListener listener : this.listeners) {
                listener.missingWasNullCall(ctx);
            }
        }
    }

    @Override // org.jooq.DiagnosticsListener
    public final void duplicateStatements(DiagnosticsContext ctx) {
        if (check(ctx, (Predicate<? super Settings>) (v0) -> {
            return v0.isDiagnosticsDuplicateStatements();
        })) {
            for (DiagnosticsListener listener : this.listeners) {
                listener.duplicateStatements(ctx);
            }
        }
    }

    @Override // org.jooq.DiagnosticsListener
    public final void repeatedStatements(DiagnosticsContext ctx) {
        if (check(ctx, (Predicate<? super Settings>) (v0) -> {
            return v0.isDiagnosticsRepeatedStatements();
        })) {
            for (DiagnosticsListener listener : this.listeners) {
                listener.repeatedStatements(ctx);
            }
        }
    }

    @Override // org.jooq.DiagnosticsListener
    public final void exception(DiagnosticsContext ctx) {
        for (DiagnosticsListener listener : this.listeners) {
            listener.exception(ctx);
        }
    }
}
