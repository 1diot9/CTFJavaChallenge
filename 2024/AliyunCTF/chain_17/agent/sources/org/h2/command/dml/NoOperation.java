package org.h2.command.dml;

import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.result.ResultInterface;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/NoOperation.class */
public class NoOperation extends Prepared {
    public NoOperation(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    @Override // org.h2.command.Prepared
    public long update() {
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public boolean isTransactional() {
        return true;
    }

    @Override // org.h2.command.Prepared
    public boolean needRecompile() {
        return false;
    }

    @Override // org.h2.command.Prepared
    public boolean isReadOnly() {
        return true;
    }

    @Override // org.h2.command.Prepared
    public ResultInterface queryMeta() {
        return null;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 63;
    }
}
