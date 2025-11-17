package org.h2.command.ddl;

import java.util.ArrayList;
import org.h2.command.Prepared;
import org.h2.engine.Procedure;
import org.h2.engine.SessionLocal;
import org.h2.expression.Parameter;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/PrepareProcedure.class */
public class PrepareProcedure extends DefineCommand {
    private String procedureName;
    private Prepared prepared;

    public PrepareProcedure(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    @Override // org.h2.command.Prepared
    public void checkParameters() {
    }

    @Override // org.h2.command.Prepared
    public long update() {
        Procedure procedure = new Procedure(this.procedureName, this.prepared);
        this.prepared.setParameterList(this.parameters);
        this.prepared.setPrepareAlways(this.prepareAlways);
        this.prepared.prepare();
        this.session.addProcedure(procedure);
        return 0L;
    }

    public void setProcedureName(String str) {
        this.procedureName = str;
    }

    public void setPrepared(Prepared prepared) {
        this.prepared = prepared;
    }

    @Override // org.h2.command.Prepared
    public ArrayList<Parameter> getParameters() {
        return new ArrayList<>(0);
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 51;
    }
}
