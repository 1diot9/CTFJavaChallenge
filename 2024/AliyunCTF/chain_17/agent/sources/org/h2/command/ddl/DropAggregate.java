package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.UserAggregate;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DropAggregate.class */
public class DropAggregate extends SchemaOwnerCommand {
    private String name;
    private boolean ifExists;

    public DropAggregate(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    @Override // org.h2.command.ddl.SchemaOwnerCommand
    long update(Schema schema) {
        Database database = getDatabase();
        UserAggregate findAggregate = schema.findAggregate(this.name);
        if (findAggregate == null) {
            if (!this.ifExists) {
                throw DbException.get(ErrorCode.AGGREGATE_NOT_FOUND_1, this.name);
            }
            return 0L;
        }
        database.removeSchemaObject(this.session, findAggregate);
        return 0L;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 36;
    }
}
