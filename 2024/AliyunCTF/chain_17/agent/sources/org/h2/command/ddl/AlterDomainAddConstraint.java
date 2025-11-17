package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.constraint.ConstraintDomain;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterDomainAddConstraint.class */
public class AlterDomainAddConstraint extends AlterDomain {
    private String constraintName;
    private Expression checkExpression;
    private String comment;
    private boolean checkExisting;
    private final boolean ifNotExists;

    public AlterDomainAddConstraint(SessionLocal sessionLocal, Schema schema, boolean z) {
        super(sessionLocal, schema);
        this.ifNotExists = z;
    }

    private String generateConstraintName(Domain domain) {
        if (this.constraintName == null) {
            this.constraintName = getSchema().getUniqueDomainConstraintName(this.session, domain);
        }
        return this.constraintName;
    }

    @Override // org.h2.command.ddl.AlterDomain
    long update(Schema schema, Domain domain) {
        try {
            long tryUpdate = tryUpdate(schema, domain);
            getSchema().freeUniqueName(this.constraintName);
            return tryUpdate;
        } catch (Throwable th) {
            getSchema().freeUniqueName(this.constraintName);
            throw th;
        }
    }

    private int tryUpdate(Schema schema, Domain domain) {
        if (this.constraintName != null && schema.findConstraint(this.session, this.constraintName) != null) {
            if (this.ifNotExists) {
                return 0;
            }
            throw DbException.get(ErrorCode.CONSTRAINT_ALREADY_EXISTS_1, this.constraintName);
        }
        Database database = getDatabase();
        database.lockMeta(this.session);
        ConstraintDomain constraintDomain = new ConstraintDomain(schema, getObjectId(), generateConstraintName(domain), domain);
        constraintDomain.setExpression(this.session, this.checkExpression);
        if (this.checkExisting) {
            constraintDomain.checkExistingData(this.session);
        }
        constraintDomain.setComment(this.comment);
        database.addSchemaObject(this.session, constraintDomain);
        domain.addConstraint(constraintDomain);
        return 0;
    }

    public void setConstraintName(String str) {
        this.constraintName = str;
    }

    public String getConstraintName() {
        return this.constraintName;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 92;
    }

    public void setCheckExpression(Expression expression) {
        this.checkExpression = expression;
    }

    public void setComment(String str) {
        this.comment = str;
    }

    public void setCheckExisting(boolean z) {
        this.checkExisting = z;
    }
}
