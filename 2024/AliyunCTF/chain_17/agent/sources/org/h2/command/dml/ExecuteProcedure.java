package org.h2.command.dml;

import java.util.ArrayList;
import org.h2.command.Prepared;
import org.h2.engine.Procedure;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.Parameter;
import org.h2.result.ResultInterface;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/ExecuteProcedure.class */
public class ExecuteProcedure extends Prepared {
    private final ArrayList<Expression> expressions;
    private Procedure procedure;

    public ExecuteProcedure(SessionLocal sessionLocal) {
        super(sessionLocal);
        this.expressions = Utils.newSmallArrayList();
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public void setExpression(int i, Expression expression) {
        this.expressions.add(i, expression);
    }

    private void setParameters() {
        ArrayList<Parameter> parameters = this.procedure.getPrepared().getParameters();
        for (int i = 0; parameters != null && i < parameters.size() && i < this.expressions.size(); i++) {
            parameters.get(i).setValue(this.expressions.get(i).getValue(this.session));
        }
    }

    @Override // org.h2.command.Prepared
    public boolean isQuery() {
        return this.procedure.getPrepared().isQuery();
    }

    @Override // org.h2.command.Prepared
    public long update() {
        setParameters();
        return this.procedure.getPrepared().update();
    }

    @Override // org.h2.command.Prepared
    public ResultInterface query(long j) {
        setParameters();
        return this.procedure.getPrepared().query(j);
    }

    @Override // org.h2.command.Prepared
    public boolean isTransactional() {
        return true;
    }

    @Override // org.h2.command.Prepared
    public ResultInterface queryMeta() {
        return this.procedure.getPrepared().queryMeta();
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 59;
    }
}
