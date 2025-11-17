package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DropSequence.class */
public class DropSequence extends SchemaOwnerCommand {
    private String sequenceName;
    private boolean ifExists;

    public DropSequence(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    public void setSequenceName(String str) {
        this.sequenceName = str;
    }

    @Override // org.h2.command.ddl.SchemaOwnerCommand
    long update(Schema schema) {
        Sequence findSequence = schema.findSequence(this.sequenceName);
        if (findSequence == null) {
            if (!this.ifExists) {
                throw DbException.get(ErrorCode.SEQUENCE_NOT_FOUND_1, this.sequenceName);
            }
            return 0L;
        }
        if (findSequence.getBelongsToTable()) {
            throw DbException.get(ErrorCode.SEQUENCE_BELONGS_TO_A_TABLE_1, this.sequenceName);
        }
        getDatabase().removeSchemaObject(this.session, findSequence);
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 43;
    }
}
