package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.TableSynonym;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/CreateSynonym.class */
public class CreateSynonym extends SchemaOwnerCommand {
    private final CreateSynonymData data;
    private boolean ifNotExists;
    private boolean orReplace;
    private String comment;

    public CreateSynonym(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
        this.data = new CreateSynonymData();
    }

    public void setName(String str) {
        this.data.synonymName = str;
    }

    public void setSynonymFor(String str) {
        this.data.synonymFor = str;
    }

    public void setSynonymForSchema(Schema schema) {
        this.data.synonymForSchema = schema;
    }

    public void setIfNotExists(boolean z) {
        this.ifNotExists = z;
    }

    public void setOrReplace(boolean z) {
        this.orReplace = z;
    }

    @Override // org.h2.command.ddl.SchemaOwnerCommand
    long update(Schema schema) {
        Database database = getDatabase();
        this.data.session = this.session;
        database.lockMeta(this.session);
        if (schema.findTableOrView(this.session, this.data.synonymName) != null) {
            throw DbException.get(ErrorCode.TABLE_OR_VIEW_ALREADY_EXISTS_1, this.data.synonymName);
        }
        if (this.data.synonymForSchema.findTableOrView(this.session, this.data.synonymFor) != null) {
            return createTableSynonym(database);
        }
        throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, this.data.synonymForSchema.getName() + "." + this.data.synonymFor);
    }

    private int createTableSynonym(Database database) {
        TableSynonym createSynonym;
        TableSynonym synonym = getSchema().getSynonym(this.data.synonymName);
        if (synonym != null && !this.orReplace) {
            if (this.ifNotExists) {
                return 0;
            }
            throw DbException.get(ErrorCode.TABLE_OR_VIEW_ALREADY_EXISTS_1, this.data.synonymName);
        }
        if (synonym != null) {
            createSynonym = synonym;
            this.data.schema = createSynonym.getSchema();
            createSynonym.updateData(this.data);
            createSynonym.setComment(this.comment);
            createSynonym.setModified();
            database.updateMeta(this.session, createSynonym);
        } else {
            this.data.id = getObjectId();
            createSynonym = getSchema().createSynonym(this.data);
            createSynonym.setComment(this.comment);
            database.addSchemaObject(this.session, createSynonym);
        }
        createSynonym.updateSynonymFor();
        return 0;
    }

    public void setComment(String str) {
        this.comment = str;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 88;
    }
}
