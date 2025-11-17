package org.jooq.impl;

import java.util.concurrent.Flow;
import org.jooq.Record;
import org.jooq.RowCountQuery;
import org.jooq.impl.AbstractDMLQuery;
import org.reactivestreams.Subscriber;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractDelegatingDMLQuery.class */
public abstract class AbstractDelegatingDMLQuery<R extends Record, Q extends AbstractDMLQuery<R>> extends AbstractDelegatingQuery<R, Q> implements RowCountQuery {
    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractDelegatingDMLQuery(Q delegate) {
        super(delegate);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Publisher, java.util.concurrent.Flow.Publisher
    public final void subscribe(Flow.Subscriber<? super Integer> subscriber) {
        ((AbstractDMLQuery) getDelegate()).subscribe(subscriber);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.reactivestreams.Publisher
    public final void subscribe(Subscriber<? super Integer> subscriber) {
        ((AbstractDMLQuery) getDelegate()).subscribe(subscriber);
    }
}
