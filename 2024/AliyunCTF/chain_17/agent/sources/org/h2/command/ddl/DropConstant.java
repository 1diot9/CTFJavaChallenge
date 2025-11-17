package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Constant;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DropConstant.class */
public class DropConstant extends SchemaOwnerCommand {
    private String constantName;
    private boolean ifExists;

    public DropConstant(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    public void setConstantName(String str) {
        this.constantName = str;
    }

    @Override // org.h2.command.ddl.SchemaOwnerCommand
    long update(Schema schema) {
        Database database = getDatabase();
        Constant findConstant = schema.findConstant(this.constantName);
        if (findConstant == null) {
            if (!this.ifExists) {
                throw DbException.get(ErrorCode.CONSTANT_NOT_FOUND_1, this.constantName);
            }
            return 0L;
        }
        database.removeSchemaObject(this.session, findConstant);
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 37;
    }
}
