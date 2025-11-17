package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.constraint.ConstraintActionType;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;
import org.h2.table.TableType;
import org.h2.table.TableView;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DropView.class */
public class DropView extends SchemaCommand {
    private String viewName;
    private boolean ifExists;
    private ConstraintActionType dropAction;

    public DropView(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
        this.dropAction = getDatabase().getSettings().dropRestrict ? ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    public void setDropAction(ConstraintActionType constraintActionType) {
        this.dropAction = constraintActionType;
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
        if (TableType.VIEW != findTableOrView.getTableType()) {
            throw DbException.get(ErrorCode.VIEW_NOT_FOUND_1, this.viewName);
        }
        this.session.getUser().checkSchemaOwner(findTableOrView.getSchema());
        if (this.dropAction == ConstraintActionType.RESTRICT) {
            Iterator<DbObject> it = findTableOrView.getChildren().iterator();
            while (it.hasNext()) {
                DbObject next = it.next();
                if (next instanceof TableView) {
                    throw DbException.get(ErrorCode.CANNOT_DROP_2, this.viewName, next.getName());
                }
            }
        }
        ArrayList arrayList = new ArrayList(((TableView) findTableOrView).getTables());
        findTableOrView.lock(this.session, 2);
        Database database = getDatabase();
        database.removeSchemaObject(this.session, findTableOrView);
        Iterator it2 = arrayList.iterator();
        while (it2.hasNext()) {
            Table table = (Table) it2.next();
            if (TableType.VIEW == table.getTableType()) {
                TableView tableView = (TableView) table;
                if (tableView.isTableExpression() && tableView.getName() != null) {
                    database.removeSchemaObject(this.session, tableView);
                }
            }
        }
        database.unlockMeta(this.session);
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 48;
    }
}
