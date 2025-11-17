package org.jooq.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import org.jooq.CloseableQuery;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Param;
import org.jooq.Record;
import org.jooq.conf.ParamType;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractDelegatingQuery.class */
public abstract class AbstractDelegatingQuery<R extends Record, Q extends CloseableQuery> extends AbstractQueryPart implements CloseableQuery, QOM.UProxy<Q> {
    private final Q delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractDelegatingQuery(Q delegate) {
        this.delegate = delegate;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.Attachable
    public final Configuration configuration() {
        return this.delegate.configuration();
    }

    @Override // org.jooq.AttachableQueryPart
    public final List<Object> getBindValues() {
        return this.delegate.getBindValues();
    }

    @Override // org.jooq.AttachableQueryPart
    public final Map<String, Param<?>> getParams() {
        return this.delegate.getParams();
    }

    @Override // org.jooq.AttachableQueryPart
    public final Param<?> getParam(String name) {
        return this.delegate.getParam(name);
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> context) {
        context.visit(this.delegate);
    }

    @Override // org.jooq.AttachableQueryPart
    public final String getSQL() {
        return this.delegate.getSQL();
    }

    @Override // org.jooq.AttachableQueryPart
    public final String getSQL(ParamType paramType) {
        return this.delegate.getSQL(paramType);
    }

    @Override // org.jooq.Attachable
    public final void attach(Configuration configuration) {
        this.delegate.attach(configuration);
    }

    @Override // org.jooq.Attachable
    public final void detach() {
        this.delegate.detach();
    }

    @Override // org.jooq.Query
    public final int execute() {
        return this.delegate.execute();
    }

    @Override // org.jooq.Query
    public final CompletionStage<Integer> executeAsync() {
        return this.delegate.executeAsync();
    }

    @Override // org.jooq.Query
    public final CompletionStage<Integer> executeAsync(Executor executor) {
        return this.delegate.executeAsync(executor);
    }

    @Override // org.jooq.Query
    public final boolean isExecutable() {
        return this.delegate.isExecutable();
    }

    @Override // org.jooq.CloseableQuery, org.jooq.Query
    public final Q bind(String str, Object obj) {
        return (Q) this.delegate.bind(str, obj);
    }

    @Override // org.jooq.CloseableQuery, org.jooq.Query
    public final Q bind(int i, Object obj) {
        return (Q) this.delegate.bind(i, obj);
    }

    @Override // org.jooq.CloseableQuery, org.jooq.Query
    public final Q poolable(boolean z) {
        return (Q) this.delegate.poolable(z);
    }

    @Override // org.jooq.CloseableQuery, org.jooq.Query
    public final Q queryTimeout(int i) {
        return (Q) this.delegate.queryTimeout(i);
    }

    @Override // org.jooq.CloseableQuery, org.jooq.Query
    public final Q keepStatement(boolean z) {
        return (Q) this.delegate.keepStatement(z);
    }

    @Override // org.jooq.CloseableQuery, java.lang.AutoCloseable
    public final void close() {
        this.delegate.close();
    }

    @Override // org.jooq.Query
    public final void cancel() {
        this.delegate.cancel();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Q getDelegate() {
        return this.delegate;
    }

    @Override // org.jooq.impl.QOM.UProxy
    public final Q $delegate() {
        return getDelegate();
    }
}
