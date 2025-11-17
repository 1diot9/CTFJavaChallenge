package org.h2.result;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/LazyResult.class */
public abstract class LazyResult extends FetchedResult {
    private final SessionLocal session;
    private final Expression[] expressions;
    private boolean closed;
    private long limit;

    protected abstract Value[] fetchNextRow();

    public LazyResult(SessionLocal sessionLocal, Expression[] expressionArr) {
        this.session = sessionLocal;
        this.expressions = expressionArr;
    }

    public void setLimit(long j) {
        this.limit = j;
    }

    @Override // org.h2.result.ResultInterface
    public boolean isLazy() {
        return true;
    }

    @Override // org.h2.result.ResultInterface
    public void reset() {
        if (this.closed) {
            throw DbException.getInternalError();
        }
        this.rowId = -1L;
        this.afterLast = false;
        this.currentRow = null;
        this.nextRow = null;
    }

    public boolean skip() {
        if (this.closed || this.afterLast) {
            return false;
        }
        this.currentRow = null;
        if (this.nextRow != null) {
            this.nextRow = null;
            return true;
        }
        if (skipNextRow()) {
            return true;
        }
        this.afterLast = true;
        return false;
    }

    @Override // org.h2.result.ResultInterface
    public boolean hasNext() {
        if (this.closed || this.afterLast) {
            return false;
        }
        if (this.nextRow == null && (this.limit <= 0 || this.rowId + 1 < this.limit)) {
            this.nextRow = fetchNextRow();
        }
        return this.nextRow != null;
    }

    protected boolean skipNextRow() {
        return fetchNextRow() != null;
    }

    @Override // org.h2.result.ResultInterface
    public long getRowCount() {
        throw DbException.getUnsupportedException("Row count is unknown for lazy result.");
    }

    @Override // org.h2.result.ResultInterface
    public boolean isClosed() {
        return this.closed;
    }

    @Override // org.h2.result.ResultInterface, java.lang.AutoCloseable
    public void close() {
        this.closed = true;
    }

    @Override // org.h2.result.ResultInterface
    public String getAlias(int i) {
        return this.expressions[i].getAlias(this.session, i);
    }

    @Override // org.h2.result.ResultInterface
    public String getSchemaName(int i) {
        return this.expressions[i].getSchemaName();
    }

    @Override // org.h2.result.ResultInterface
    public String getTableName(int i) {
        return this.expressions[i].getTableName();
    }

    @Override // org.h2.result.ResultInterface
    public String getColumnName(int i) {
        return this.expressions[i].getColumnName(this.session, i);
    }

    @Override // org.h2.result.ResultInterface
    public TypeInfo getColumnType(int i) {
        return this.expressions[i].getType();
    }

    @Override // org.h2.result.ResultInterface
    public boolean isIdentity(int i) {
        return this.expressions[i].isIdentity();
    }

    @Override // org.h2.result.ResultInterface
    public int getNullable(int i) {
        return this.expressions[i].getNullable();
    }

    @Override // org.h2.result.ResultInterface
    public void setFetchSize(int i) {
    }

    @Override // org.h2.result.ResultInterface
    public int getFetchSize() {
        return 1;
    }
}
