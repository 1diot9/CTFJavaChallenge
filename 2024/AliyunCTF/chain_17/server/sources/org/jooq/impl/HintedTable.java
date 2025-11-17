package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Keyword;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/HintedTable.class */
public final class HintedTable<R extends Record> extends AbstractDelegatingTable<R> implements QOM.HintedTable<R> {
    private final Keyword keywords;
    private final QueryPartList<Name> arguments;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HintedTable(AbstractTable<R> delegate, String keywords, String... arguments) {
        this(delegate, keywords, (QueryPartList<Name>) new QueryPartList(Tools.names(arguments)));
    }

    HintedTable(AbstractTable<R> delegate, String keywords, QueryPartList<Name> arguments) {
        this(delegate, DSL.keyword(keywords), arguments);
    }

    HintedTable(AbstractTable<R> delegate, Keyword keywords, String... arguments) {
        this(delegate, keywords, (QueryPartList<Name>) new QueryPartList(Tools.names(arguments)));
    }

    HintedTable(AbstractTable<R> delegate, Keyword keywords, QueryPartList<Name> arguments) {
        super(delegate);
        this.keywords = keywords;
        this.arguments = arguments;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDelegatingTable
    public final <O extends Record> HintedTable<O> construct(AbstractTable<O> newDelegate) {
        return new HintedTable<>(newDelegate, this.keywords, this.arguments);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(this.delegate).sql(' ').visit(this.keywords).sql(" (").visit(this.arguments).sql(')');
    }

    @Override // org.jooq.impl.QOM.HintedTable
    public final Table<R> $table() {
        return this.delegate;
    }

    @Override // org.jooq.impl.QOM.HintedTable
    public final <O extends Record> HintedTable<O> $table(Table<O> newTable) {
        return construct((AbstractTable) newTable);
    }
}
