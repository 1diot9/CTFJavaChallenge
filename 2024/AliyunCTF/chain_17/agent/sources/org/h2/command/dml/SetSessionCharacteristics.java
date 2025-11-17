package org.h2.command.dml;

import org.h2.command.Prepared;
import org.h2.engine.IsolationLevel;
import org.h2.engine.SessionLocal;
import org.h2.result.ResultInterface;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/SetSessionCharacteristics.class */
public class SetSessionCharacteristics extends Prepared {
    private final IsolationLevel isolationLevel;

    public SetSessionCharacteristics(SessionLocal sessionLocal, IsolationLevel isolationLevel) {
        super(sessionLocal);
        this.isolationLevel = isolationLevel;
    }

    @Override // org.h2.command.Prepared
    public boolean isTransactional() {
        return false;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        this.session.setIsolationLevel(this.isolationLevel);
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public boolean needRecompile() {
        return false;
    }

    @Override // org.h2.command.Prepared
    public ResultInterface queryMeta() {
        return null;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 67;
    }
}
