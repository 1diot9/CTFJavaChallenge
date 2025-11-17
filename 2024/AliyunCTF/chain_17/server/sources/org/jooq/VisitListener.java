package org.jooq;

import java.util.EventListener;
import java.util.function.Consumer;
import org.jooq.impl.CallbackVisitListener;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/VisitListener.class */
public interface VisitListener extends EventListener {
    default void clauseStart(VisitContext context) {
    }

    default void clauseEnd(VisitContext context) {
    }

    default void visitStart(VisitContext context) {
    }

    default void visitEnd(VisitContext context) {
    }

    static CallbackVisitListener onClauseStart(Consumer<? super VisitContext> onClauseStart) {
        return new CallbackVisitListener().onClauseStart(onClauseStart);
    }

    static CallbackVisitListener onClauseEnd(Consumer<? super VisitContext> onClauseEnd) {
        return new CallbackVisitListener().onClauseEnd(onClauseEnd);
    }

    static CallbackVisitListener onVisitStart(Consumer<? super VisitContext> onVisitStart) {
        return new CallbackVisitListener().onVisitStart(onVisitStart);
    }

    static CallbackVisitListener onVisitEnd(Consumer<? super VisitContext> onVisitEnd) {
        return new CallbackVisitListener().onVisitEnd(onVisitEnd);
    }
}
