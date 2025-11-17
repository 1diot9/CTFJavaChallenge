package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.TriggerObject;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/CreateTrigger.class */
public class CreateTrigger extends SchemaCommand {
    private String triggerName;
    private boolean ifNotExists;
    private boolean insteadOf;
    private boolean before;
    private int typeMask;
    private boolean rowBased;
    private int queueSize;
    private boolean noWait;
    private String tableName;
    private String triggerClassName;
    private String triggerSource;
    private boolean force;
    private boolean onRollback;

    public CreateTrigger(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
        this.queueSize = 1024;
    }

    public void setInsteadOf(boolean z) {
        this.insteadOf = z;
    }

    public void setBefore(boolean z) {
        this.before = z;
    }

    public void setTriggerClassName(String str) {
        this.triggerClassName = str;
    }

    public void setTriggerSource(String str) {
        this.triggerSource = str;
    }

    public void setTypeMask(int i) {
        this.typeMask = i;
    }

    public void setRowBased(boolean z) {
        this.rowBased = z;
    }

    public void setQueueSize(int i) {
        this.queueSize = i;
    }

    public void setNoWait(boolean z) {
        this.noWait = z;
    }

    public void setTableName(String str) {
        this.tableName = str;
    }

    public void setTriggerName(String str) {
        this.triggerName = str;
    }

    public void setIfNotExists(boolean z) {
        this.ifNotExists = z;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        this.session.getUser().checkAdmin();
        Database database = getDatabase();
        if (getSchema().findTrigger(this.triggerName) != null) {
            if (this.ifNotExists) {
                return 0L;
            }
            throw DbException.get(ErrorCode.TRIGGER_ALREADY_EXISTS_1, this.triggerName);
        }
        if ((this.typeMask & 8) != 0) {
            if (this.rowBased) {
                throw DbException.get(ErrorCode.INVALID_TRIGGER_FLAGS_1, "SELECT + FOR EACH ROW");
            }
            if (this.onRollback) {
                throw DbException.get(ErrorCode.INVALID_TRIGGER_FLAGS_1, "SELECT + ROLLBACK");
            }
        } else if ((this.typeMask & 7) == 0) {
            if (this.onRollback) {
                throw DbException.get(ErrorCode.INVALID_TRIGGER_FLAGS_1, "(!INSERT & !UPDATE & !DELETE) + ROLLBACK");
            }
            throw DbException.getInternalError();
        }
        int objectId = getObjectId();
        Table tableOrView = getSchema().getTableOrView(this.session, this.tableName);
        TriggerObject triggerObject = new TriggerObject(getSchema(), objectId, this.triggerName, tableOrView);
        triggerObject.setInsteadOf(this.insteadOf);
        triggerObject.setBefore(this.before);
        triggerObject.setNoWait(this.noWait);
        triggerObject.setQueueSize(this.queueSize);
        triggerObject.setRowBased(this.rowBased);
        triggerObject.setTypeMask(this.typeMask);
        triggerObject.setOnRollback(this.onRollback);
        if (this.triggerClassName != null) {
            triggerObject.setTriggerClassName(this.triggerClassName, this.force);
        } else {
            triggerObject.setTriggerSource(this.triggerSource, this.force);
        }
        database.addSchemaObject(this.session, triggerObject);
        tableOrView.addTrigger(triggerObject);
        return 0L;
    }

    public void setForce(boolean z) {
        this.force = z;
    }

    public void setOnRollback(boolean z) {
        this.onRollback = z;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 31;
    }
}
