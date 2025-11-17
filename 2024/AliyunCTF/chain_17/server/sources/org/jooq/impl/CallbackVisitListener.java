package org.jooq.impl;

import java.util.function.Consumer;
import org.jooq.VisitContext;
import org.jooq.VisitListener;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CallbackVisitListener.class */
public final class CallbackVisitListener implements VisitListener {
    private final Consumer<? super VisitContext> onClauseStart;
    private final Consumer<? super VisitContext> onClauseEnd;
    private final Consumer<? super VisitContext> onVisitStart;
    private final Consumer<? super VisitContext> onVisitEnd;

    public CallbackVisitListener() {
        this(null, null, null, null);
    }

    private CallbackVisitListener(Consumer<? super VisitContext> onClauseStart, Consumer<? super VisitContext> onClauseEnd, Consumer<? super VisitContext> onVisitStart, Consumer<? super VisitContext> onVisitEnd) {
        this.onClauseStart = onClauseStart;
        this.onClauseEnd = onClauseEnd;
        this.onVisitStart = onVisitStart;
        this.onVisitEnd = onVisitEnd;
    }

    @Override // org.jooq.VisitListener
    public final void clauseStart(VisitContext context) {
        if (this.onClauseStart != null) {
            this.onClauseStart.accept(context);
        }
    }

    @Override // org.jooq.VisitListener
    public final void clauseEnd(VisitContext context) {
        if (this.onClauseEnd != null) {
            this.onClauseEnd.accept(context);
        }
    }

    @Override // org.jooq.VisitListener
    public final void visitStart(VisitContext context) {
        if (this.onVisitStart != null) {
            this.onVisitStart.accept(context);
        }
    }

    @Override // org.jooq.VisitListener
    public final void visitEnd(VisitContext context) {
        if (this.onVisitEnd != null) {
            this.onVisitEnd.accept(context);
        }
    }

    public final CallbackVisitListener onClauseStart(Consumer<? super VisitContext> newOnClauseStart) {
        return new CallbackVisitListener(newOnClauseStart, this.onClauseEnd, this.onVisitStart, this.onVisitEnd);
    }

    public final CallbackVisitListener onClauseEnd(Consumer<? super VisitContext> newOnClauseEnd) {
        return new CallbackVisitListener(this.onClauseStart, newOnClauseEnd, this.onVisitStart, this.onVisitEnd);
    }

    public final CallbackVisitListener onVisitStart(Consumer<? super VisitContext> newOnVisitStart) {
        return new CallbackVisitListener(this.onClauseStart, this.onClauseEnd, newOnVisitStart, this.onVisitEnd);
    }

    public final CallbackVisitListener onVisitEnd(Consumer<? super VisitContext> newOnVisitEnd) {
        return new CallbackVisitListener(this.onClauseStart, this.onClauseEnd, this.onVisitStart, newOnVisitEnd);
    }
}
