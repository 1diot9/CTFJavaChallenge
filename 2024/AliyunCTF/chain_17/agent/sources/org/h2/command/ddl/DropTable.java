package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import org.h2.api.ErrorCode;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintActionType;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.MaterializedView;
import org.h2.table.Table;
import org.h2.table.TableView;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DropTable.class */
public class DropTable extends DefineCommand {
    private boolean ifExists;
    private ConstraintActionType dropAction;
    private final ArrayList<SchemaAndTable> tables;

    public DropTable(SessionLocal sessionLocal) {
        super(sessionLocal);
        this.tables = Utils.newSmallArrayList();
        this.dropAction = getDatabase().getSettings().dropRestrict ? ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    public void addTable(Schema schema, String str) {
        this.tables.add(new SchemaAndTable(schema, str));
    }

    private boolean prepareDrop() {
        HashSet hashSet = new HashSet();
        Iterator<SchemaAndTable> it = this.tables.iterator();
        while (it.hasNext()) {
            SchemaAndTable next = it.next();
            String str = next.tableName;
            Table findTableOrView = next.schema.findTableOrView(this.session, str);
            if (findTableOrView == null) {
                if (!this.ifExists) {
                    throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, str);
                }
            } else {
                this.session.getUser().checkTableRight(findTableOrView, 32);
                if (!findTableOrView.canDrop()) {
                    throw DbException.get(ErrorCode.CANNOT_DROP_TABLE_1, str);
                }
                hashSet.add(findTableOrView);
            }
        }
        if (hashSet.isEmpty()) {
            return false;
        }
        Iterator it2 = hashSet.iterator();
        while (it2.hasNext()) {
            Table table = (Table) it2.next();
            ArrayList arrayList = new ArrayList();
            if (this.dropAction == ConstraintActionType.RESTRICT) {
                CopyOnWriteArrayList<TableView> dependentViews = table.getDependentViews();
                if (dependentViews != null && !dependentViews.isEmpty()) {
                    Iterator<TableView> it3 = dependentViews.iterator();
                    while (it3.hasNext()) {
                        TableView next2 = it3.next();
                        if (!hashSet.contains(next2)) {
                            arrayList.add(next2.getName());
                        }
                    }
                }
                CopyOnWriteArrayList<MaterializedView> dependentMaterializedViews = table.getDependentMaterializedViews();
                if (dependentMaterializedViews != null && !dependentMaterializedViews.isEmpty()) {
                    Iterator<MaterializedView> it4 = dependentMaterializedViews.iterator();
                    while (it4.hasNext()) {
                        MaterializedView next3 = it4.next();
                        if (!hashSet.contains(next3)) {
                            arrayList.add(next3.getName());
                        }
                    }
                }
                ArrayList<Constraint> constraints = table.getConstraints();
                if (constraints != null && !constraints.isEmpty()) {
                    for (Constraint constraint : constraints) {
                        if (!hashSet.contains(constraint.getTable())) {
                            arrayList.add(constraint.getName());
                        }
                    }
                }
                if (!arrayList.isEmpty()) {
                    throw DbException.get(ErrorCode.CANNOT_DROP_2, table.getName(), String.join(", ", new HashSet(arrayList)));
                }
            }
            table.lock(this.session, 2);
        }
        return true;
    }

    private void executeDrop() {
        Iterator<SchemaAndTable> it = this.tables.iterator();
        while (it.hasNext()) {
            SchemaAndTable next = it.next();
            Table findTableOrView = next.schema.findTableOrView(this.session, next.tableName);
            if (findTableOrView != null) {
                findTableOrView.setModified();
                Database database = getDatabase();
                database.lockMeta(this.session);
                database.removeSchemaObject(this.session, findTableOrView);
            }
        }
    }

    @Override // org.h2.command.Prepared
    public long update() {
        if (prepareDrop()) {
            executeDrop();
            return 0L;
        }
        return 0L;
    }

    public void setDropAction(ConstraintActionType constraintActionType) {
        this.dropAction = constraintActionType;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 44;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DropTable$SchemaAndTable.class */
    public static final class SchemaAndTable {
        final Schema schema;
        final String tableName;

        SchemaAndTable(Schema schema, String str) {
            this.schema = schema;
            this.tableName = str;
        }
    }
}
