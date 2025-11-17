package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.constraint.Constraint;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DropIndex.class */
public class DropIndex extends SchemaCommand {
    private String indexName;
    private boolean ifExists;

    public DropIndex(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    public void setIndexName(String str) {
        this.indexName = str;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        Database database = getDatabase();
        Index findIndex = getSchema().findIndex(this.session, this.indexName);
        if (findIndex == null) {
            if (!this.ifExists) {
                throw DbException.get(ErrorCode.INDEX_NOT_FOUND_1, this.indexName);
            }
            return 0L;
        }
        Table table = findIndex.getTable();
        this.session.getUser().checkTableRight(findIndex.getTable(), 32);
        Constraint constraint = null;
        ArrayList<Constraint> constraints = table.getConstraints();
        for (int i = 0; constraints != null && i < constraints.size(); i++) {
            Constraint constraint2 = constraints.get(i);
            if (constraint2.usesIndex(findIndex)) {
                if (Constraint.Type.PRIMARY_KEY == constraint2.getConstraintType()) {
                    Iterator<Constraint> it = constraints.iterator();
                    while (it.hasNext()) {
                        if (it.next().getReferencedConstraint() == constraint2) {
                            throw DbException.get(ErrorCode.INDEX_BELONGS_TO_CONSTRAINT_2, this.indexName, constraint2.getName());
                        }
                    }
                    constraint = constraint2;
                } else {
                    throw DbException.get(ErrorCode.INDEX_BELONGS_TO_CONSTRAINT_2, this.indexName, constraint2.getName());
                }
            }
        }
        findIndex.getTable().setModified();
        if (constraint != null) {
            database.removeSchemaObject(this.session, constraint);
            return 0L;
        }
        database.removeSchemaObject(this.session, findIndex);
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 40;
    }
}
