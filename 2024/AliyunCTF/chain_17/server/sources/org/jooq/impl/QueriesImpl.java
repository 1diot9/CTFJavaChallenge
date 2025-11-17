package org.jooq.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.jooq.Batch;
import org.jooq.Block;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DSLContext;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.ResultQuery;
import org.jooq.Results;
import org.jooq.impl.DefaultParseContext;
import org.jooq.impl.QOM;
import org.jooq.impl.ResultsImpl;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QueriesImpl.class */
public final class QueriesImpl extends AbstractAttachableQueryPart implements Queries {
    private final QueryPartList<Query> queries;
    private static final Pattern P_IGNORE_FORMATTED = Pattern.compile("^(?sm:\\n? *(.*?) *\\n?)$");
    private static final Pattern P_IGNORE_UNFORMATTED = Pattern.compile("^(?sm: *(.*?) *)$");

    /* JADX INFO: Access modifiers changed from: package-private */
    public QueriesImpl(Configuration configuration, Collection<? extends Query> queries) {
        super(configuration);
        this.queries = new QueryPartList<>(queries);
    }

    @Override // org.jooq.Queries
    public final Queries concat(Queries other) {
        Query[] array = other.queries();
        List<Query> list = new ArrayList<>(this.queries.size() + array.length);
        list.addAll(this.queries);
        list.addAll(Arrays.asList(array));
        return new QueriesImpl(configuration(), list);
    }

    @Override // org.jooq.Queries
    public final Query[] queries() {
        return (Query[]) this.queries.toArray(Tools.EMPTY_QUERY);
    }

    @Override // org.jooq.Queries
    public final Block block() {
        return configurationOrDefault().dsl().begin(this.queries);
    }

    @Override // org.jooq.Queries
    public final Batch batch() {
        return configurationOrDefault().dsl().batch(this.queries);
    }

    @Override // java.lang.Iterable
    public final Iterator<Query> iterator() {
        return this.queries.iterator();
    }

    @Override // org.jooq.Queries
    public final Stream<Query> stream() {
        return queryStream();
    }

    @Override // org.jooq.Queries
    public final Stream<Query> queryStream() {
        return this.queries.stream();
    }

    @Override // org.jooq.Queries
    public final Results fetchMany() {
        Configuration c = configurationOrThrow();
        ResultsImpl results = new ResultsImpl(c);
        DSLContext ctx = c.dsl();
        Iterator<Query> it = iterator();
        while (it.hasNext()) {
            Query query = it.next();
            if (query instanceof ResultQuery) {
                ResultQuery<?> q = (ResultQuery) query;
                results.resultsOrRows.addAll(ctx.fetchMany(q).resultsOrRows());
            } else {
                results.resultsOrRows.add(new ResultsImpl.ResultOrRowsImpl(ctx.execute(query)));
            }
        }
        return results;
    }

    @Override // org.jooq.Queries
    public final int[] executeBatch() {
        return configurationOrThrow().dsl().batch(this).execute();
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        boolean first = true;
        boolean separatorRequired = false;
        Iterator<Query> it = iterator();
        while (it.hasNext()) {
            Query query = it.next();
            boolean i = query instanceof DefaultParseContext.IgnoreQuery;
            if (first) {
                first = false;
            } else if (separatorRequired) {
                ctx.formatSeparator();
            }
            if (i) {
                if (ctx.format()) {
                    query = new DefaultParseContext.IgnoreQuery(P_IGNORE_FORMATTED.matcher(((DefaultParseContext.IgnoreQuery) query).sql).replaceFirst("$1"));
                } else {
                    query = new DefaultParseContext.IgnoreQuery(P_IGNORE_UNFORMATTED.matcher(((DefaultParseContext.IgnoreQuery) query).sql).replaceFirst("$1"));
                }
            }
            ctx.visit(query);
            if (!i) {
                ctx.sql(';');
            }
            separatorRequired = (i && ((DefaultParseContext.IgnoreQuery) query).sql.endsWith("\n")) ? false : true;
        }
    }

    @Override // org.jooq.Queries
    public final QOM.UnmodifiableList<? extends Query> $queries() {
        return QOM.unmodifiable((List) this.queries);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        return this.queries.hashCode();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof QueriesImpl)) {
            return false;
        }
        return this.queries.equals(((QueriesImpl) obj).queries);
    }
}
