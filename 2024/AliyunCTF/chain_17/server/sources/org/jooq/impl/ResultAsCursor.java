package org.jooq.impl;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import org.jooq.Attachable;
import org.jooq.Record;
import org.jooq.Result;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ResultAsCursor.class */
public final class ResultAsCursor<R extends Record> extends AbstractCursor<R> {
    private final Result<R> result;
    private int index;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResultAsCursor(Result<R> result) {
        super(result.configuration(), (AbstractRow) result.fieldsRow());
        this.result = result;
    }

    @Override // org.jooq.impl.AbstractFormattable
    final List<? extends Attachable> getAttachables() {
        return this.result;
    }

    @Override // java.lang.Iterable
    public final Iterator<R> iterator() {
        return (Iterator<R>) this.result.iterator();
    }

    @Override // org.jooq.Cursor
    public final Result<R> fetchNext(int number) {
        Result<R> r = new ResultImpl<>(this.configuration, this.fields);
        for (int i = 0; i < number && i + this.index < this.result.size(); i++) {
            r.add((Record) this.result.get(i + this.index));
        }
        this.index += number;
        return r;
    }

    @Override // org.jooq.Cursor, java.lang.AutoCloseable
    public void close() {
    }

    @Override // org.jooq.Cursor
    public boolean isClosed() {
        return false;
    }

    @Override // org.jooq.Cursor
    public final ResultSet resultSet() {
        return this.result.intoResultSet();
    }
}
