package org.h2.command.ddl;

import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintActionType;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterTableDropConstraint.class */
public class AlterTableDropConstraint extends AlterTable {
    private String constraintName;
    private final boolean ifExists;
    private ConstraintActionType dropAction;

    public AlterTableDropConstraint(SessionLocal sessionLocal, Schema schema, boolean z) {
        super(sessionLocal, schema);
        this.ifExists = z;
        this.dropAction = getDatabase().getSettings().dropRestrict ? ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
    }

    public void setConstraintName(String str) {
        this.constraintName = str;
    }

    public void setDropAction(ConstraintActionType constraintActionType) {
        this.dropAction = constraintActionType;
    }

    @Override // org.h2.command.ddl.AlterTable
    public long update(Table table) {
        Constraint.Type constraintType;
        Constraint findConstraint = getSchema().findConstraint(this.session, this.constraintName);
        if (findConstraint == null || (constraintType = findConstraint.getConstraintType()) == Constraint.Type.DOMAIN || findConstraint.getTable() != table) {
            if (!this.ifExists) {
                throw DbException.get(ErrorCode.CONSTRAINT_NOT_FOUND_1, this.constraintName);
            }
            return 0L;
        }
        Table refTable = findConstraint.getRefTable();
        if (refTable != table) {
            this.session.getUser().checkTableRight(refTable, 32);
        }
        if (constraintType == Constraint.Type.PRIMARY_KEY || constraintType == Constraint.Type.UNIQUE) {
            Iterator<Constraint> it = findConstraint.getTable().getConstraints().iterator();
            while (it.hasNext()) {
                Constraint next = it.next();
                if (next.getReferencedConstraint() == findConstraint) {
                    if (this.dropAction == ConstraintActionType.RESTRICT) {
                        throw DbException.get(ErrorCode.CONSTRAINT_IS_USED_BY_CONSTRAINT_2, findConstraint.getTraceSQL(), next.getTraceSQL());
                    }
                    Table table2 = next.getTable();
                    if (table2 != table && table2 != refTable) {
                        this.session.getUser().checkTableRight(table2, 32);
                    }
                }
            }
        }
        getDatabase().removeSchemaObject(this.session, findConstraint);
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 14;
    }
}
