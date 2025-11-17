package org.h2.command.dml;

import org.h2.api.ErrorCode;
import org.h2.command.ddl.SchemaCommand;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/AlterTableSet.class */
public class AlterTableSet extends SchemaCommand {
    private boolean ifTableExists;
    private String tableName;
    private final int type;
    private final boolean value;
    private boolean checkExisting;

    public AlterTableSet(SessionLocal sessionLocal, Schema schema, int i, boolean z) {
        super(sessionLocal, schema);
        this.type = i;
        this.value = z;
    }

    public void setCheckExisting(boolean z) {
        this.checkExisting = z;
    }

    @Override // org.h2.command.ddl.DefineCommand, org.h2.command.Prepared
    public boolean isTransactional() {
        return true;
    }

    public void setIfTableExists(boolean z) {
        this.ifTableExists = z;
    }

    public void setTableName(String str) {
        this.tableName = str;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        Table resolveTableOrView = getSchema().resolveTableOrView(this.session, this.tableName);
        if (resolveTableOrView == null) {
            if (this.ifTableExists) {
                return 0L;
            }
            throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, this.tableName);
        }
        this.session.getUser().checkTableRight(resolveTableOrView, 32);
        resolveTableOrView.lock(this.session, 2);
        switch (this.type) {
            case 55:
                resolveTableOrView.setCheckForeignKeyConstraints(this.session, this.value, this.value ? this.checkExisting : false);
                return 0L;
            default:
                throw DbException.getInternalError("type=" + this.type);
        }
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return this.type;
    }
}
