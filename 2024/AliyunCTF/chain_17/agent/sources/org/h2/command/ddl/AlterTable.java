package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterTable.class */
public abstract class AlterTable extends SchemaCommand {
    String tableName;
    boolean ifTableExists;

    abstract long update(Table table);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlterTable(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public final void setTableName(String str) {
        this.tableName = str;
    }

    public final void setIfTableExists(boolean z) {
        this.ifTableExists = z;
    }

    @Override // org.h2.command.Prepared
    public final long update() {
        Table findTableOrView = getSchema().findTableOrView(this.session, this.tableName);
        if (findTableOrView == null) {
            if (this.ifTableExists) {
                return 0L;
            }
            throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, this.tableName);
        }
        this.session.getUser().checkTableRight(findTableOrView, 32);
        return update(findTableOrView);
    }
}
