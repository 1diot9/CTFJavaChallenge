package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.FunctionAlias;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DropFunctionAlias.class */
public class DropFunctionAlias extends SchemaOwnerCommand {
    private String aliasName;
    private boolean ifExists;

    public DropFunctionAlias(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    @Override // org.h2.command.ddl.SchemaOwnerCommand
    long update(Schema schema) {
        Database database = getDatabase();
        FunctionAlias findFunction = schema.findFunction(this.aliasName);
        if (findFunction == null) {
            if (!this.ifExists) {
                throw DbException.get(ErrorCode.FUNCTION_ALIAS_NOT_FOUND_1, this.aliasName);
            }
            return 0L;
        }
        database.removeSchemaObject(this.session, findFunction);
        return 0L;
    }

    public void setAliasName(String str) {
        this.aliasName = str;
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 39;
    }
}
