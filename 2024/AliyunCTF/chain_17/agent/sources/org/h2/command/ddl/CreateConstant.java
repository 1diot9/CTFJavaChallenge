package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.schema.Constant;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/CreateConstant.class */
public class CreateConstant extends SchemaOwnerCommand {
    private String constantName;
    private Expression expression;
    private boolean ifNotExists;

    public CreateConstant(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public void setIfNotExists(boolean z) {
        this.ifNotExists = z;
    }

    @Override // org.h2.command.ddl.SchemaOwnerCommand
    long update(Schema schema) {
        Database database = getDatabase();
        if (schema.findConstant(this.constantName) != null) {
            if (this.ifNotExists) {
                return 0L;
            }
            throw DbException.get(ErrorCode.CONSTANT_ALREADY_EXISTS_1, this.constantName);
        }
        Constant constant = new Constant(schema, getObjectId(), this.constantName);
        this.expression = this.expression.optimize(this.session);
        constant.setValue(this.expression.getValue(this.session));
        database.addSchemaObject(this.session, constant);
        return 0L;
    }

    public void setConstantName(String str) {
        this.constantName = str;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 23;
    }
}
