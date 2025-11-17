package org.jooq.impl;

import io.r2dbc.spi.ConnectionFactory;
import org.jooq.Configuration;
import org.jooq.Record;
import org.jooq.RowCountQuery;
import org.jooq.impl.R2DBC;
import org.reactivestreams.Subscriber;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractRowCountQuery.class */
public abstract class AbstractRowCountQuery extends AbstractQuery<Record> implements RowCountQuery {
    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractRowCountQuery(Configuration configuration) {
        super(configuration);
    }

    @Override // org.reactivestreams.Publisher
    public final void subscribe(Subscriber<? super Integer> subscriber) {
        ConnectionFactory cf = configuration().connectionFactory();
        if (!(cf instanceof NoConnectionFactory)) {
            subscriber.onSubscribe(new R2DBC.QuerySubscription(this, subscriber, (t, u) -> {
                return new R2DBC.RowCountSubscriber(u);
            }));
        } else {
            subscriber.onSubscribe(new R2DBC.BlockingRowCountSubscription(this, subscriber));
        }
    }
}
