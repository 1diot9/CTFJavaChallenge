package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintDomain;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterDomainDropConstraint.class */
public class AlterDomainDropConstraint extends AlterDomain {
    private String constraintName;
    private final boolean ifConstraintExists;

    public AlterDomainDropConstraint(SessionLocal sessionLocal, Schema schema, boolean z) {
        super(sessionLocal, schema);
        this.ifConstraintExists = z;
    }

    public void setConstraintName(String str) {
        this.constraintName = str;
    }

    @Override // org.h2.command.ddl.AlterDomain
    long update(Schema schema, Domain domain) {
        Constraint findConstraint = schema.findConstraint(this.session, this.constraintName);
        if (findConstraint == null || findConstraint.getConstraintType() != Constraint.Type.DOMAIN || ((ConstraintDomain) findConstraint).getDomain() != domain) {
            if (!this.ifConstraintExists) {
                throw DbException.get(ErrorCode.CONSTRAINT_NOT_FOUND_1, this.constraintName);
            }
            return 0L;
        }
        getDatabase().removeSchemaObject(this.session, findConstraint);
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 93;
    }
}
