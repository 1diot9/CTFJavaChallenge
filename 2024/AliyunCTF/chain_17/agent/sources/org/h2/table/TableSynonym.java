package org.h2.table;

import org.h2.command.ddl.CreateSynonymData;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.util.ParserUtil;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/TableSynonym.class */
public class TableSynonym extends SchemaObject {
    private CreateSynonymData data;
    private Table synonymFor;

    public TableSynonym(CreateSynonymData createSynonymData) {
        super(createSynonymData.schema, createSynonymData.id, createSynonymData.synonymName, 11);
        this.data = createSynonymData;
    }

    public Table getSynonymFor() {
        return this.synonymFor;
    }

    public void updateData(CreateSynonymData createSynonymData) {
        this.data = createSynonymData;
    }

    @Override // org.h2.engine.DbObject
    public int getType() {
        return 15;
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQLForCopy(Table table, String str) {
        return this.synonymFor.getCreateSQLForCopy(table, str);
    }

    @Override // org.h2.engine.DbObject
    public void rename(String str) {
        throw DbException.getUnsupportedException("SYNONYM");
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        this.synonymFor.removeSynonym(this);
        this.database.removeMeta(sessionLocal, getId());
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        StringBuilder sb = new StringBuilder("CREATE SYNONYM ");
        getSQL(sb, 0).append(" FOR ");
        ParserUtil.quoteIdentifier(sb, this.data.synonymForSchema.getName(), 0).append('.');
        ParserUtil.quoteIdentifier(sb, this.data.synonymFor, 0);
        return sb.toString();
    }

    @Override // org.h2.engine.DbObject
    public String getDropSQL() {
        return getSQL(new StringBuilder("DROP SYNONYM "), 0).toString();
    }

    @Override // org.h2.engine.DbObject
    public void checkRename() {
        throw DbException.getUnsupportedException("SYNONYM");
    }

    public String getSynonymForName() {
        return this.data.synonymFor;
    }

    public Schema getSynonymForSchema() {
        return this.data.synonymForSchema;
    }

    public boolean isInvalid() {
        return this.synonymFor.isValid();
    }

    public void updateSynonymFor() {
        if (this.synonymFor != null) {
            this.synonymFor.removeSynonym(this);
        }
        this.synonymFor = this.data.synonymForSchema.getTableOrView(this.data.session, this.data.synonymFor);
        this.synonymFor.addSynonym(this);
    }
}
