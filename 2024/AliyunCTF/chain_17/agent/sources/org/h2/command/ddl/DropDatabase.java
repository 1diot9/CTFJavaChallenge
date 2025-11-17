package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.h2.engine.Database;
import org.h2.engine.Right;
import org.h2.engine.RightOwner;
import org.h2.engine.Role;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.schema.Sequence;
import org.h2.table.Table;
import org.h2.table.TableType;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DropDatabase.class */
public class DropDatabase extends DefineCommand {
    private boolean dropAllObjects;
    private boolean deleteFiles;

    public DropDatabase(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    @Override // org.h2.command.Prepared
    public long update() {
        if (this.dropAllObjects) {
            dropAllObjects();
        }
        if (this.deleteFiles) {
            getDatabase().setDeleteFilesOnDisconnect(true);
            return 0L;
        }
        return 0L;
    }

    private void dropAllObjects() {
        boolean z;
        User user = this.session.getUser();
        user.checkAdmin();
        Database database = getDatabase();
        database.lockMeta(this.session);
        do {
            ArrayList<Table> allTablesAndViews = database.getAllTablesAndViews();
            ArrayList arrayList = new ArrayList(allTablesAndViews.size());
            Iterator<Table> it = allTablesAndViews.iterator();
            while (it.hasNext()) {
                Table next = it.next();
                if (next.getName() != null && TableType.VIEW == next.getTableType()) {
                    arrayList.add(next);
                }
            }
            Iterator<Table> it2 = allTablesAndViews.iterator();
            while (it2.hasNext()) {
                Table next2 = it2.next();
                if (next2.getName() != null && TableType.TABLE_LINK == next2.getTableType()) {
                    arrayList.add(next2);
                }
            }
            Iterator<Table> it3 = allTablesAndViews.iterator();
            while (it3.hasNext()) {
                Table next3 = it3.next();
                if (next3.getName() != null && TableType.TABLE == next3.getTableType() && !next3.isHidden()) {
                    arrayList.add(next3);
                }
            }
            Iterator<Table> it4 = allTablesAndViews.iterator();
            while (it4.hasNext()) {
                Table next4 = it4.next();
                if (next4.getName() != null && TableType.EXTERNAL_TABLE_ENGINE == next4.getTableType() && !next4.isHidden()) {
                    arrayList.add(next4);
                }
            }
            z = false;
            Iterator it5 = arrayList.iterator();
            while (it5.hasNext()) {
                Table table = (Table) it5.next();
                if (table.getName() != null) {
                    if (database.getDependentTable(table, table) == null) {
                        database.removeSchemaObject(this.session, table);
                    } else {
                        z = true;
                    }
                }
            }
        } while (z);
        Collection<Schema> allSchemasNoMeta = database.getAllSchemasNoMeta();
        for (Schema schema : allSchemasNoMeta) {
            if (schema.canDrop()) {
                database.removeDatabaseObject(this.session, schema);
            }
        }
        ArrayList arrayList2 = new ArrayList();
        Iterator<Schema> it6 = allSchemasNoMeta.iterator();
        while (it6.hasNext()) {
            for (Sequence sequence : it6.next().getAllSequences()) {
                if (!sequence.getBelongsToTable()) {
                    arrayList2.add(sequence);
                }
            }
        }
        addAll(allSchemasNoMeta, 5, arrayList2);
        addAll(allSchemasNoMeta, 4, arrayList2);
        addAll(allSchemasNoMeta, 11, arrayList2);
        addAll(allSchemasNoMeta, 9, arrayList2);
        addAll(allSchemasNoMeta, 12, arrayList2);
        Iterator it7 = arrayList2.iterator();
        while (it7.hasNext()) {
            SchemaObject schemaObject = (SchemaObject) it7.next();
            if (schemaObject.getSchema().isValid() && !schemaObject.isHidden()) {
                database.removeSchemaObject(this.session, schemaObject);
            }
        }
        Role publicRole = database.getPublicRole();
        for (RightOwner rightOwner : database.getAllUsersAndRoles()) {
            if (rightOwner != user && rightOwner != publicRole) {
                database.removeDatabaseObject(this.session, rightOwner);
            }
        }
        Iterator<Right> it8 = database.getAllRights().iterator();
        while (it8.hasNext()) {
            database.removeDatabaseObject(this.session, it8.next());
        }
        for (SessionLocal sessionLocal : database.getSessions(false)) {
            sessionLocal.setLastIdentity(ValueNull.INSTANCE);
        }
    }

    private static void addAll(Collection<Schema> collection, int i, ArrayList<SchemaObject> arrayList) {
        Iterator<Schema> it = collection.iterator();
        while (it.hasNext()) {
            it.next().getAll(i, arrayList);
        }
    }

    public void setDropAllObjects(boolean z) {
        this.dropAllObjects = z;
    }

    public void setDeleteFiles(boolean z) {
        this.deleteFiles = z;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 38;
    }
}
