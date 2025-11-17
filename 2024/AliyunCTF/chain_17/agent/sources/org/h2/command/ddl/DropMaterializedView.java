package org.h2.command.ddl;

import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.MaterializedView;
import org.h2.table.Table;
import org.h2.table.TableType;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DropMaterializedView.class */
public class DropMaterializedView extends SchemaCommand {
    private String viewName;
    private boolean ifExists;

    public DropMaterializedView(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    public void setViewName(String str) {
        this.viewName = str;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        Table findTableOrView = getSchema().findTableOrView(this.session, this.viewName);
        if (findTableOrView == null) {
            if (!this.ifExists) {
                throw DbException.get(ErrorCode.VIEW_NOT_FOUND_1, this.viewName);
            }
            return 0L;
        }
        if (TableType.MATERIALIZED_VIEW != findTableOrView.getTableType()) {
            throw DbException.get(ErrorCode.VIEW_NOT_FOUND_1, this.viewName);
        }
        this.session.getUser().checkSchemaOwner(findTableOrView.getSchema());
        MaterializedView materializedView = (MaterializedView) findTableOrView;
        Iterator<Table> it = materializedView.getSelect().getTables().iterator();
        while (it.hasNext()) {
            it.next().removeDependentMaterializedView(materializedView);
        }
        Database database = getDatabase();
        database.lockMeta(this.session);
        database.removeSchemaObject(this.session, findTableOrView);
        database.unlockMeta(this.session);
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 104;
    }
}
