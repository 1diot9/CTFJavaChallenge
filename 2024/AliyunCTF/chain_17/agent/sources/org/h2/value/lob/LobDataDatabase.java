package org.h2.value.lob;

import java.io.IOException;
import java.io.InputStream;
import org.h2.message.DbException;
import org.h2.store.DataHandler;
import org.h2.value.ValueLob;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/lob/LobDataDatabase.class */
public final class LobDataDatabase extends LobData {
    private final DataHandler handler;
    private final int tableId;
    private final long lobId;

    public LobDataDatabase(DataHandler dataHandler, int i, long j) {
        this.handler = dataHandler;
        this.tableId = i;
        this.lobId = j;
    }

    @Override // org.h2.value.lob.LobData
    public void remove(ValueLob valueLob) {
        if (this.handler != null) {
            this.handler.getLobStorage().removeLob(valueLob);
        }
    }

    @Override // org.h2.value.lob.LobData
    public boolean isLinkedToTable() {
        return this.tableId >= 0;
    }

    public int getTableId() {
        return this.tableId;
    }

    public long getLobId() {
        return this.lobId;
    }

    @Override // org.h2.value.lob.LobData
    public InputStream getInputStream(long j) {
        try {
            return this.handler.getLobStorage().getInputStream(this.lobId, this.tableId, j);
        } catch (IOException e) {
            throw DbException.convertIOException(e, toString());
        }
    }

    @Override // org.h2.value.lob.LobData
    public DataHandler getDataHandler() {
        return this.handler;
    }

    public String toString() {
        return "lob-table: table: " + this.tableId + " id: " + this.lobId;
    }
}
