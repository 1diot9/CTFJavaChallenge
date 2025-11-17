package org.h2.command.ddl;

import java.util.Iterator;
import org.h2.constraint.ConstraintReferential;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterTableRenameColumn.class */
public class AlterTableRenameColumn extends AlterTable {
    private boolean ifExists;
    private String oldName;
    private String newName;

    public AlterTableRenameColumn(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    public void setOldColumnName(String str) {
        this.oldName = str;
    }

    public void setNewColumnName(String str) {
        this.newName = str;
    }

    @Override // org.h2.command.ddl.AlterTable
    public long update(Table table) {
        Column column = table.getColumn(this.oldName, this.ifExists);
        if (column == null) {
            return 0L;
        }
        table.checkSupportAlter();
        table.renameColumn(column, this.newName);
        table.setModified();
        Database database = getDatabase();
        database.updateMeta(this.session, table);
        Iterator<DbObject> it = table.getChildren().iterator();
        while (it.hasNext()) {
            DbObject next = it.next();
            if (next instanceof ConstraintReferential) {
                ((ConstraintReferential) next).updateOnTableColumnRename();
            }
        }
        Iterator<DbObject> it2 = table.getChildren().iterator();
        while (it2.hasNext()) {
            DbObject next2 = it2.next();
            if (next2.getCreateSQL() != null) {
                database.updateMeta(this.session, next2);
            }
        }
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 16;
    }
}
