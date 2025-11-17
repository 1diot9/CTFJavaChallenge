package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.TriggerObject;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DropTrigger.class */
public class DropTrigger extends SchemaCommand {
    private String triggerName;
    private boolean ifExists;

    public DropTrigger(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    public void setTriggerName(String str) {
        this.triggerName = str;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        Database database = getDatabase();
        TriggerObject findTrigger = getSchema().findTrigger(this.triggerName);
        if (findTrigger == null) {
            if (!this.ifExists) {
                throw DbException.get(ErrorCode.TRIGGER_NOT_FOUND_1, this.triggerName);
            }
            return 0L;
        }
        this.session.getUser().checkTableRight(findTrigger.getTable(), 32);
        database.removeSchemaObject(this.session, findTrigger);
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 45;
    }
}
