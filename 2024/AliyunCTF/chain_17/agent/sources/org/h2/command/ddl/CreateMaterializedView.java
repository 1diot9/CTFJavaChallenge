package org.h2.command.ddl;

import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.command.query.Query;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.MaterializedView;
import org.h2.table.Table;
import org.h2.table.TableType;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/CreateMaterializedView.class */
public class CreateMaterializedView extends SchemaOwnerCommand {
    private final CreateTable createTable;
    private boolean orReplace;
    private boolean ifNotExists;
    private String viewName;
    private String comment;
    private Query select;
    private String selectSQL;

    public CreateMaterializedView(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
        this.createTable = new CreateTable(sessionLocal, schema);
    }

    public void setViewName(String str) {
        this.viewName = str;
        this.createTable.setTableName(str + "$1");
    }

    public void setComment(String str) {
        this.comment = str;
    }

    public void setSelectSQL(String str) {
        this.selectSQL = str;
    }

    public void setIfNotExists(boolean z) {
        this.ifNotExists = z;
        this.createTable.setIfNotExists(z);
    }

    public void setSelect(Query query) {
        this.select = query;
        this.createTable.setQuery(query);
    }

    public void setOrReplace(boolean z) {
        this.orReplace = z;
    }

    @Override // org.h2.command.ddl.SchemaOwnerCommand
    long update(Schema schema) {
        Database database = getDatabase();
        Table findTableOrView = schema.findTableOrView(this.session, this.viewName);
        MaterializedView materializedView = null;
        if (findTableOrView != null) {
            if (this.ifNotExists) {
                return 0L;
            }
            if (!this.orReplace || TableType.MATERIALIZED_VIEW != findTableOrView.getTableType()) {
                throw DbException.get(ErrorCode.VIEW_ALREADY_EXISTS_1, this.viewName);
            }
            materializedView = (MaterializedView) findTableOrView;
        }
        int objectId = getObjectId();
        this.createTable.update();
        Table tableOrView = schema.getTableOrView(this.session, this.viewName + "$1");
        if (materializedView == null) {
            materializedView = new MaterializedView(schema, objectId, this.viewName, tableOrView, this.select, this.selectSQL);
        } else {
            materializedView.replace(tableOrView, this.select, this.selectSQL);
            materializedView.setModified();
        }
        if (this.comment != null) {
            materializedView.setComment(this.comment);
        }
        Iterator<Table> it = this.select.getTables().iterator();
        while (it.hasNext()) {
            it.next().addDependentMaterializedView(materializedView);
        }
        if (findTableOrView == null) {
            database.addSchemaObject(this.session, materializedView);
            database.unlockMeta(this.session);
            return 0L;
        }
        database.updateMeta(this.session, materializedView);
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 102;
    }
}
