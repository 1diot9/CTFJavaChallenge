package org.jooq.impl;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.QueryPartInternal;
import org.jooq.Record;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SQLResultQuery.class */
final class SQLResultQuery extends AbstractResultQuery<Record> implements QOM.UEmptyQuery {
    private final SQL delegate;

    /* renamed from: org.jooq.impl.SQLResultQuery$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SQLResultQuery$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SQLResultQuery(Configuration configuration, SQL delegate) {
        super(configuration);
        this.delegate = delegate;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(this.delegate);
                return;
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        SQL sql = this.delegate;
        if (sql instanceof QueryPartInternal) {
            QueryPartInternal q = (QueryPartInternal) sql;
            return q.clauses(ctx);
        }
        return null;
    }

    @Override // org.jooq.impl.AbstractResultQuery
    public final Class<? extends Record> getRecordType0() {
        return RecordImplN.class;
    }

    @Override // org.jooq.impl.ResultQueryTrait
    public final Field<?>[] getFields(ThrowingSupplier<? extends ResultSetMetaData, SQLException> rs) throws SQLException {
        Field<?>[] result = getFields();
        if (!Tools.isEmpty(result)) {
            return result;
        }
        return new MetaDataFieldProvider(configuration(), rs.get()).getFields();
    }

    @Override // org.jooq.impl.ResultQueryTrait
    public final Field<?>[] getFields() {
        Collection<? extends Field<?>> coerce = coerce();
        if (!Tools.isEmpty((Collection<?>) coerce)) {
            return (Field[]) coerce.toArray(Tools.EMPTY_FIELD);
        }
        return Tools.EMPTY_FIELD;
    }
}
