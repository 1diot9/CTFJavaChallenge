package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterSchemaRename.class */
public class AlterSchemaRename extends DefineCommand {
    private Schema oldSchema;
    private String newSchemaName;

    public AlterSchemaRename(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    public void setOldSchema(Schema schema) {
        this.oldSchema = schema;
    }

    public void setNewName(String str) {
        this.newSchemaName = str;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        this.session.getUser().checkSchemaAdmin();
        Database database = getDatabase();
        if (!this.oldSchema.canDrop()) {
            throw DbException.get(ErrorCode.SCHEMA_CAN_NOT_BE_DROPPED_1, this.oldSchema.getName());
        }
        if (database.findSchema(this.newSchemaName) != null || this.newSchemaName.equals(this.oldSchema.getName())) {
            throw DbException.get(ErrorCode.SCHEMA_ALREADY_EXISTS_1, this.newSchemaName);
        }
        database.renameDatabaseObject(this.session, this.oldSchema, this.newSchemaName);
        ArrayList<SchemaObject> arrayList = new ArrayList<>();
        Iterator<Schema> it = database.getAllSchemas().iterator();
        while (it.hasNext()) {
            it.next().getAll(arrayList);
            Iterator<SchemaObject> it2 = arrayList.iterator();
            while (it2.hasNext()) {
                database.updateMeta(this.session, it2.next());
            }
            arrayList.clear();
        }
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 2;
    }
}
