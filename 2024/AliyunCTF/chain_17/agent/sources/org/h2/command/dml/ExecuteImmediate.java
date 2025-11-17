package org.h2.command.dml;

import org.h2.api.ErrorCode;
import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/ExecuteImmediate.class */
public class ExecuteImmediate extends Prepared {
    private Expression statement;

    public ExecuteImmediate(SessionLocal sessionLocal, Expression expression) {
        super(sessionLocal);
        this.statement = expression.optimize(sessionLocal);
    }

    @Override // org.h2.command.Prepared
    public long update() {
        String string = this.statement.getValue(this.session).getString();
        if (string == null) {
            throw DbException.getInvalidValueException("SQL command", null);
        }
        Prepared prepare = this.session.prepare(string);
        if (prepare.isQuery()) {
            throw DbException.get(ErrorCode.SYNTAX_ERROR_2, string, "<not a query>");
        }
        return prepare.update();
    }

    @Override // org.h2.command.Prepared
    public boolean isTransactional() {
        return true;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 91;
    }

    @Override // org.h2.command.Prepared
    public ResultInterface queryMeta() {
        return null;
    }
}
