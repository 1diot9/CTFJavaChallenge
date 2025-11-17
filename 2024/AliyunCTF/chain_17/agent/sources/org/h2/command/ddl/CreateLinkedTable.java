package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.TableLink;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/CreateLinkedTable.class */
public class CreateLinkedTable extends SchemaCommand {
    private String tableName;
    private String driver;
    private String url;
    private String user;
    private String password;
    private String originalSchema;
    private String originalTable;
    private boolean ifNotExists;
    private String comment;
    private boolean emitUpdates;
    private boolean force;
    private boolean temporary;
    private boolean globalTemporary;
    private boolean readOnly;
    private int fetchSize;
    private boolean autocommit;

    public CreateLinkedTable(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
        this.autocommit = true;
    }

    public void setTableName(String str) {
        this.tableName = str;
    }

    public void setDriver(String str) {
        this.driver = str;
    }

    public void setOriginalTable(String str) {
        this.originalTable = str;
    }

    public void setPassword(String str) {
        this.password = str;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public void setUser(String str) {
        this.user = str;
    }

    public void setIfNotExists(boolean z) {
        this.ifNotExists = z;
    }

    public void setFetchSize(int i) {
        this.fetchSize = i;
    }

    public void setAutoCommit(boolean z) {
        this.autocommit = z;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        this.session.getUser().checkAdmin();
        Database database = getDatabase();
        if (getSchema().resolveTableOrView(this.session, this.tableName) != null) {
            if (this.ifNotExists) {
                return 0L;
            }
            throw DbException.get(ErrorCode.TABLE_OR_VIEW_ALREADY_EXISTS_1, this.tableName);
        }
        TableLink createTableLink = getSchema().createTableLink(getObjectId(), this.tableName, this.driver, this.url, this.user, this.password, this.originalSchema, this.originalTable, this.emitUpdates, this.force);
        createTableLink.setTemporary(this.temporary);
        createTableLink.setGlobalTemporary(this.globalTemporary);
        createTableLink.setComment(this.comment);
        createTableLink.setReadOnly(this.readOnly);
        if (this.fetchSize > 0) {
            createTableLink.setFetchSize(this.fetchSize);
        }
        createTableLink.setAutoCommit(this.autocommit);
        if (this.temporary && !this.globalTemporary) {
            this.session.addLocalTempTable(createTableLink);
            return 0L;
        }
        database.addSchemaObject(this.session, createTableLink);
        return 0L;
    }

    public void setEmitUpdates(boolean z) {
        this.emitUpdates = z;
    }

    public void setComment(String str) {
        this.comment = str;
    }

    public void setForce(boolean z) {
        this.force = z;
    }

    public void setTemporary(boolean z) {
        this.temporary = z;
    }

    public void setGlobalTemporary(boolean z) {
        this.globalTemporary = z;
    }

    public void setReadOnly(boolean z) {
        this.readOnly = z;
    }

    public void setOriginalSchema(String str) {
        this.originalSchema = str;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 26;
    }
}
