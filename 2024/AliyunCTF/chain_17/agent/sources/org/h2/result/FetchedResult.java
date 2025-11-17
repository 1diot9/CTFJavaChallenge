package org.h2.result;

import org.h2.engine.Session;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/FetchedResult.class */
public abstract class FetchedResult implements ResultInterface {
    long rowId = -1;
    Value[] currentRow;
    Value[] nextRow;
    boolean afterLast;

    @Override // org.h2.result.ResultInterface
    public final Value[] currentRow() {
        return this.currentRow;
    }

    @Override // org.h2.result.ResultInterface
    public final boolean next() {
        if (hasNext()) {
            this.rowId++;
            this.currentRow = this.nextRow;
            this.nextRow = null;
            return true;
        }
        if (!this.afterLast) {
            this.rowId++;
            this.currentRow = null;
            this.afterLast = true;
            return false;
        }
        return false;
    }

    @Override // org.h2.result.ResultInterface
    public final boolean isAfterLast() {
        return this.afterLast;
    }

    @Override // org.h2.result.ResultInterface
    public final long getRowId() {
        return this.rowId;
    }

    @Override // org.h2.result.ResultInterface
    public final boolean needToClose() {
        return true;
    }

    @Override // org.h2.result.ResultInterface
    public final ResultInterface createShallowCopy(Session session) {
        return null;
    }
}
