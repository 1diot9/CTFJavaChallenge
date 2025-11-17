package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.UserAggregate;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/CreateAggregate.class */
public class CreateAggregate extends SchemaCommand {
    private String name;
    private String javaClassMethod;
    private boolean ifNotExists;
    private boolean force;

    public CreateAggregate(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    @Override // org.h2.command.Prepared
    public long update() {
        this.session.getUser().checkAdmin();
        Database database = getDatabase();
        Schema schema = getSchema();
        if (schema.findFunctionOrAggregate(this.name) != null) {
            if (!this.ifNotExists) {
                throw DbException.get(ErrorCode.FUNCTION_ALIAS_ALREADY_EXISTS_1, this.name);
            }
            return 0L;
        }
        database.addSchemaObject(this.session, new UserAggregate(schema, getObjectId(), this.name, this.javaClassMethod, this.force));
        return 0L;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setJavaClassMethod(String str) {
        this.javaClassMethod = str;
    }

    public void setIfNotExists(boolean z) {
        this.ifNotExists = z;
    }

    public void setForce(boolean z) {
        this.force = z;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 22;
    }
}
