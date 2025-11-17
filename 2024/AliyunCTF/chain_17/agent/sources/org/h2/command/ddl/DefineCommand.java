package org.h2.command.ddl;

import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.result.ResultInterface;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DefineCommand.class */
public abstract class DefineCommand extends Prepared {
    protected boolean transactional;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefineCommand(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    @Override // org.h2.command.Prepared
    public boolean isReadOnly() {
        return false;
    }

    @Override // org.h2.command.Prepared
    public ResultInterface queryMeta() {
        return null;
    }

    public void setTransactional(boolean z) {
        this.transactional = z;
    }

    @Override // org.h2.command.Prepared
    public boolean isTransactional() {
        return this.transactional;
    }

    @Override // org.h2.command.Prepared
    public boolean isRetryable() {
        return false;
    }
}
