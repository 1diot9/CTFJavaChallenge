package org.h2.command.ddl;

import java.util.ArrayList;
import org.h2.api.ErrorCode;
import org.h2.command.query.Query;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Parameter;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.table.TableType;
import org.h2.table.TableView;
import org.h2.value.TypeInfo;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/CreateView.class */
public class CreateView extends SchemaOwnerCommand {
    private Query select;
    private String viewName;
    private boolean ifNotExists;
    private String selectSQL;
    private String[] columnNames;
    private String comment;
    private boolean orReplace;
    private boolean force;
    private boolean isTableExpression;

    public CreateView(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public void setViewName(String str) {
        this.viewName = str;
    }

    public void setSelect(Query query) {
        this.select = query;
    }

    public void setIfNotExists(boolean z) {
        this.ifNotExists = z;
    }

    public void setSelectSQL(String str) {
        this.selectSQL = str;
    }

    public void setColumnNames(String[] strArr) {
        this.columnNames = strArr;
    }

    public void setComment(String str) {
        this.comment = str;
    }

    public void setOrReplace(boolean z) {
        this.orReplace = z;
    }

    public void setForce(boolean z) {
        this.force = z;
    }

    public void setTableExpression(boolean z) {
        this.isTableExpression = z;
    }

    @Override // org.h2.command.ddl.SchemaOwnerCommand
    long update(Schema schema) {
        String planSQL;
        Database database = getDatabase();
        TableView tableView = null;
        Table findTableOrView = schema.findTableOrView(this.session, this.viewName);
        if (findTableOrView != null) {
            if (this.ifNotExists) {
                return 0L;
            }
            if (!this.orReplace || TableType.VIEW != findTableOrView.getTableType()) {
                throw DbException.get(ErrorCode.VIEW_ALREADY_EXISTS_1, this.viewName);
            }
            tableView = (TableView) findTableOrView;
        }
        int objectId = getObjectId();
        if (this.select == null) {
            planSQL = this.selectSQL;
        } else {
            ArrayList<Parameter> parameters = this.select.getParameters();
            if (parameters != null && !parameters.isEmpty()) {
                throw DbException.getUnsupportedException("parameters in views");
            }
            planSQL = this.select.getPlanSQL(0);
        }
        Column[] columnArr = null;
        Column[] columnArr2 = null;
        if (this.columnNames != null) {
            columnArr = new Column[this.columnNames.length];
            columnArr2 = new Column[this.columnNames.length];
            for (int i = 0; i < this.columnNames.length; i++) {
                columnArr[i] = new Column(this.columnNames[i], TypeInfo.TYPE_UNKNOWN);
                columnArr2[i] = new Column(this.columnNames[i], TypeInfo.TYPE_VARCHAR);
            }
        }
        if (tableView == null) {
            if (this.isTableExpression) {
                tableView = TableView.createTableViewMaybeRecursive(schema, objectId, this.viewName, planSQL, null, columnArr2, this.session, false, this.isTableExpression, false, database);
            } else {
                tableView = new TableView(schema, objectId, this.viewName, planSQL, null, columnArr, this.session, false, false, this.isTableExpression, false);
            }
        } else {
            tableView.replace(planSQL, columnArr, this.session, false, this.force, false);
            tableView.setModified();
        }
        if (this.comment != null) {
            tableView.setComment(this.comment);
        }
        if (findTableOrView == null) {
            database.addSchemaObject(this.session, tableView);
            database.unlockMeta(this.session);
            return 0L;
        }
        database.updateMeta(this.session, tableView);
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 34;
    }
}
