package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.constraint.Constraint;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterTableRenameConstraint.class */
public class AlterTableRenameConstraint extends AlterTable {
    private String constraintName;
    private String newConstraintName;

    public AlterTableRenameConstraint(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public void setConstraintName(String str) {
        this.constraintName = str;
    }

    public void setNewConstraintName(String str) {
        this.newConstraintName = str;
    }

    @Override // org.h2.command.ddl.AlterTable
    public long update(Table table) {
        Constraint findConstraint = getSchema().findConstraint(this.session, this.constraintName);
        Database database = getDatabase();
        if (findConstraint == null || findConstraint.getConstraintType() == Constraint.Type.DOMAIN || findConstraint.getTable() != table) {
            throw DbException.get(ErrorCode.CONSTRAINT_NOT_FOUND_1, this.constraintName);
        }
        if (getSchema().findConstraint(this.session, this.newConstraintName) != null || this.newConstraintName.equals(this.constraintName)) {
            throw DbException.get(ErrorCode.CONSTRAINT_ALREADY_EXISTS_1, this.newConstraintName);
        }
        User user = this.session.getUser();
        Table refTable = findConstraint.getRefTable();
        if (refTable != table) {
            user.checkTableRight(refTable, 32);
        }
        database.renameSchemaObject(this.session, findConstraint, this.newConstraintName);
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 85;
    }
}
