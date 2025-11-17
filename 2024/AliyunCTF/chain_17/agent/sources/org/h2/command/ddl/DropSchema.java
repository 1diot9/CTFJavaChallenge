package org.h2.command.ddl;

import java.util.ArrayList;
import org.h2.api.ErrorCode;
import org.h2.constraint.ConstraintActionType;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DropSchema.class */
public class DropSchema extends DefineCommand {
    private String schemaName;
    private boolean ifExists;
    private ConstraintActionType dropAction;

    public DropSchema(SessionLocal sessionLocal) {
        super(sessionLocal);
        this.dropAction = getDatabase().getSettings().dropRestrict ? ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
    }

    public void setSchemaName(String str) {
        this.schemaName = str;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        ArrayList<SchemaObject> all;
        int size;
        Database database = getDatabase();
        Schema findSchema = database.findSchema(this.schemaName);
        if (findSchema == null) {
            if (!this.ifExists) {
                throw DbException.get(ErrorCode.SCHEMA_NOT_FOUND_1, this.schemaName);
            }
            return 0L;
        }
        this.session.getUser().checkSchemaOwner(findSchema);
        if (!findSchema.canDrop()) {
            throw DbException.get(ErrorCode.SCHEMA_CAN_NOT_BE_DROPPED_1, this.schemaName);
        }
        if (this.dropAction == ConstraintActionType.RESTRICT && !findSchema.isEmpty() && (size = (all = findSchema.getAll(null)).size()) > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(all.get(i).getName());
            }
            throw DbException.get(ErrorCode.CANNOT_DROP_2, this.schemaName, sb.toString());
        }
        database.removeDatabaseObject(this.session, findSchema);
        return 0L;
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    public void setDropAction(ConstraintActionType constraintActionType) {
        this.dropAction = constraintActionType;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 42;
    }
}
