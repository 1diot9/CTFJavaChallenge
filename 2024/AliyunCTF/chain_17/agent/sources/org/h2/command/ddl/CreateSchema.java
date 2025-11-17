package org.h2.command.ddl;

import java.util.ArrayList;
import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.RightOwner;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/CreateSchema.class */
public class CreateSchema extends DefineCommand {
    private String schemaName;
    private String authorization;
    private boolean ifNotExists;
    private ArrayList<String> tableEngineParams;

    public CreateSchema(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    public void setIfNotExists(boolean z) {
        this.ifNotExists = z;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        this.session.getUser().checkSchemaAdmin();
        Database database = getDatabase();
        RightOwner findUserOrRole = database.findUserOrRole(this.authorization);
        if (findUserOrRole == null) {
            throw DbException.get(ErrorCode.USER_OR_ROLE_NOT_FOUND_1, this.authorization);
        }
        if (database.findSchema(this.schemaName) != null) {
            if (this.ifNotExists) {
                return 0L;
            }
            throw DbException.get(ErrorCode.SCHEMA_ALREADY_EXISTS_1, this.schemaName);
        }
        Schema schema = new Schema(database, getObjectId(), this.schemaName, findUserOrRole, false);
        schema.setTableEngineParams(this.tableEngineParams);
        database.addDatabaseObject(this.session, schema);
        return 0L;
    }

    public void setSchemaName(String str) {
        this.schemaName = str;
    }

    public void setAuthorization(String str) {
        this.authorization = str;
    }

    public void setTableEngineParams(ArrayList<String> arrayList) {
        this.tableEngineParams = arrayList;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 28;
    }
}
