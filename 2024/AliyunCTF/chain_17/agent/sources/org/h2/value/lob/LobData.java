package org.h2.value.lob;

import java.io.InputStream;
import org.h2.store.DataHandler;
import org.h2.value.ValueLob;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/lob/LobData.class */
public abstract class LobData {
    public abstract InputStream getInputStream(long j);

    public DataHandler getDataHandler() {
        return null;
    }

    public boolean isLinkedToTable() {
        return false;
    }

    public void remove(ValueLob valueLob) {
    }

    public int getMemory() {
        return Opcodes.F2L;
    }
}
