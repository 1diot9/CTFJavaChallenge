package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/CreateSequence.class */
public class CreateSequence extends SchemaOwnerCommand {
    private String sequenceName;
    private boolean ifNotExists;
    private SequenceOptions options;
    private boolean belongsToTable;

    public CreateSequence(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
        this.transactional = true;
    }

    public void setSequenceName(String str) {
        this.sequenceName = str;
    }

    public void setIfNotExists(boolean z) {
        this.ifNotExists = z;
    }

    public void setOptions(SequenceOptions sequenceOptions) {
        this.options = sequenceOptions;
    }

    @Override // org.h2.command.ddl.SchemaOwnerCommand
    long update(Schema schema) {
        Database database = getDatabase();
        if (schema.findSequence(this.sequenceName) != null) {
            if (this.ifNotExists) {
                return 0L;
            }
            throw DbException.get(ErrorCode.SEQUENCE_ALREADY_EXISTS_1, this.sequenceName);
        }
        database.addSchemaObject(this.session, new Sequence(this.session, schema, getObjectId(), this.sequenceName, this.options, this.belongsToTable));
        return 0L;
    }

    public void setBelongsToTable(boolean z) {
        this.belongsToTable = z;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 29;
    }
}
