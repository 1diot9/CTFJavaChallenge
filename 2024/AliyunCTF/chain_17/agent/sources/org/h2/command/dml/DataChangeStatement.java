package org.h2.command.dml;

import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.result.ResultInterface;
import org.h2.result.ResultTarget;
import org.h2.table.DataChangeDeltaTable;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/DataChangeStatement.class */
public abstract class DataChangeStatement extends Prepared {
    private boolean isPrepared;

    abstract void doPrepare();

    public abstract String getStatementName();

    public abstract Table getTable();

    public abstract long update(ResultTarget resultTarget, DataChangeDeltaTable.ResultOption resultOption);

    /* JADX INFO: Access modifiers changed from: protected */
    public DataChangeStatement(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    @Override // org.h2.command.Prepared
    public final void prepare() {
        if (this.isPrepared) {
            return;
        }
        doPrepare();
        this.isPrepared = true;
    }

    @Override // org.h2.command.Prepared
    public final boolean isTransactional() {
        return true;
    }

    @Override // org.h2.command.Prepared
    public final ResultInterface queryMeta() {
        return null;
    }

    @Override // org.h2.command.Prepared
    public boolean isCacheable() {
        return true;
    }

    @Override // org.h2.command.Prepared
    public final long update() {
        return update(null, null);
    }
}
