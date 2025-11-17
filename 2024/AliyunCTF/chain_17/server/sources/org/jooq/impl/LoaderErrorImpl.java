package org.jooq.impl;

import org.jooq.LoaderError;
import org.jooq.Query;
import org.jooq.exception.DataAccessException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/LoaderErrorImpl.class */
final class LoaderErrorImpl implements LoaderError {
    private final DataAccessException exception;
    private final int rowIndex;
    private final String[] row;
    private final Query query;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LoaderErrorImpl(DataAccessException exception, Object[] row, int rowIndex, Query query) {
        this.exception = exception;
        this.row = strings(row);
        this.rowIndex = rowIndex;
        this.query = query;
    }

    private static String[] strings(Object[] row) {
        if (row == null) {
            return null;
        }
        return (String[]) Tools.map(row, o -> {
            if (o == null) {
                return null;
            }
            return o.toString();
        }, x$0 -> {
            return new String[x$0];
        });
    }

    @Override // org.jooq.LoaderError
    public DataAccessException exception() {
        return this.exception;
    }

    @Override // org.jooq.LoaderError
    public int rowIndex() {
        return this.rowIndex;
    }

    @Override // org.jooq.LoaderError
    public String[] row() {
        return this.row;
    }

    @Override // org.jooq.LoaderError
    public Query query() {
        return this.query;
    }
}
