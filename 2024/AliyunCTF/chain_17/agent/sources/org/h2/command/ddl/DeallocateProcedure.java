package org.h2.command.ddl;

import org.h2.engine.SessionLocal;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DeallocateProcedure.class */
public class DeallocateProcedure extends DefineCommand {
    private String procedureName;

    public DeallocateProcedure(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    @Override // org.h2.command.Prepared
    public long update() {
        this.session.removeProcedure(this.procedureName);
        return 0L;
    }

    public void setProcedureName(String str) {
        this.procedureName = str;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 35;
    }
}
