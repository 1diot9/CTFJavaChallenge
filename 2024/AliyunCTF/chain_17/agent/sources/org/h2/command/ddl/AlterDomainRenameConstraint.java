package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintDomain;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterDomainRenameConstraint.class */
public class AlterDomainRenameConstraint extends AlterDomain {
    private String constraintName;
    private String newConstraintName;

    public AlterDomainRenameConstraint(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public void setConstraintName(String str) {
        this.constraintName = str;
    }

    public void setNewConstraintName(String str) {
        this.newConstraintName = str;
    }

    @Override // org.h2.command.ddl.AlterDomain
    long update(Schema schema, Domain domain) {
        Constraint findConstraint = getSchema().findConstraint(this.session, this.constraintName);
        if (findConstraint == null || findConstraint.getConstraintType() != Constraint.Type.DOMAIN || ((ConstraintDomain) findConstraint).getDomain() != domain) {
            throw DbException.get(ErrorCode.CONSTRAINT_NOT_FOUND_1, this.constraintName);
        }
        if (getSchema().findConstraint(this.session, this.newConstraintName) != null || this.newConstraintName.equals(this.constraintName)) {
            throw DbException.get(ErrorCode.CONSTRAINT_ALREADY_EXISTS_1, this.newConstraintName);
        }
        getDatabase().renameSchemaObject(this.session, findConstraint, this.newConstraintName);
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 101;
    }
}
