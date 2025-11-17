package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterIndexRename.class */
public class AlterIndexRename extends DefineCommand {
    private boolean ifExists;
    private Schema oldSchema;
    private String oldIndexName;
    private String newIndexName;

    public AlterIndexRename(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    public void setOldSchema(Schema schema) {
        this.oldSchema = schema;
    }

    public void setOldName(String str) {
        this.oldIndexName = str;
    }

    public void setNewName(String str) {
        this.newIndexName = str;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        Database database = getDatabase();
        Index findIndex = this.oldSchema.findIndex(this.session, this.oldIndexName);
        if (findIndex == null) {
            if (!this.ifExists) {
                throw DbException.get(ErrorCode.INDEX_NOT_FOUND_1, this.newIndexName);
            }
            return 0L;
        }
        if (this.oldSchema.findIndex(this.session, this.newIndexName) != null || this.newIndexName.equals(this.oldIndexName)) {
            throw DbException.get(ErrorCode.INDEX_ALREADY_EXISTS_1, this.newIndexName);
        }
        this.session.getUser().checkTableRight(findIndex.getTable(), 32);
        database.renameSchemaObject(this.session, findIndex, this.newIndexName);
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 1;
    }
}
