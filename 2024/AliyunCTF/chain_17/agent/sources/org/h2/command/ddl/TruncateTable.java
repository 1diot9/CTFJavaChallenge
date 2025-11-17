package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Sequence;
import org.h2.table.Column;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/TruncateTable.class */
public class TruncateTable extends DefineCommand {
    private Table table;
    private boolean restart;

    public TruncateTable(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public void setRestart(boolean z) {
        this.restart = z;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        if (!this.table.canTruncate()) {
            throw DbException.get(ErrorCode.CANNOT_TRUNCATE_1, this.table.getTraceSQL());
        }
        this.session.getUser().checkTableRight(this.table, 2);
        this.table.lock(this.session, 2);
        long truncate = this.table.truncate(this.session);
        if (this.restart) {
            for (Column column : this.table.getColumns()) {
                Sequence sequence = column.getSequence();
                if (sequence != null) {
                    sequence.modify(Long.valueOf(sequence.getStartValue()), null, null, null, null, null, null);
                    getDatabase().updateMeta(this.session, sequence);
                }
            }
        }
        return truncate;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 53;
    }
}
