package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.TableSynonym;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DropSynonym.class */
public class DropSynonym extends SchemaOwnerCommand {
    private String synonymName;
    private boolean ifExists;

    public DropSynonym(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public void setSynonymName(String str) {
        this.synonymName = str;
    }

    @Override // org.h2.command.ddl.SchemaOwnerCommand
    long update(Schema schema) {
        TableSynonym synonym = schema.getSynonym(this.synonymName);
        if (synonym == null) {
            if (!this.ifExists) {
                throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, this.synonymName);
            }
            return 0L;
        }
        getDatabase().removeSchemaObject(this.session, synonym);
        return 0L;
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 89;
    }
}
