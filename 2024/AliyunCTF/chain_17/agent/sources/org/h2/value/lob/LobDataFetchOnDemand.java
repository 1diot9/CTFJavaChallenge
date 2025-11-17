package org.h2.value.lob;

import java.io.BufferedInputStream;
import java.io.InputStream;
import org.h2.engine.SessionRemote;
import org.h2.store.DataHandler;
import org.h2.store.LobStorageRemoteInputStream;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/lob/LobDataFetchOnDemand.class */
public final class LobDataFetchOnDemand extends LobData {
    private SessionRemote handler;
    private final int tableId;
    private final long lobId;
    private final byte[] hmac;

    public LobDataFetchOnDemand(DataHandler dataHandler, int i, long j, byte[] bArr) {
        this.hmac = bArr;
        this.handler = (SessionRemote) dataHandler;
        this.tableId = i;
        this.lobId = j;
    }

    @Override // org.h2.value.lob.LobData
    public boolean isLinkedToTable() {
        throw new IllegalStateException();
    }

    public int getTableId() {
        return this.tableId;
    }

    public long getLobId() {
        return this.lobId;
    }

    @Override // org.h2.value.lob.LobData
    public InputStream getInputStream(long j) {
        return new BufferedInputStream(new LobStorageRemoteInputStream(this.handler, this.lobId, this.hmac));
    }

    @Override // org.h2.value.lob.LobData
    public DataHandler getDataHandler() {
        return this.handler;
    }

    public String toString() {
        return "lob-table: table: " + this.tableId + " id: " + this.lobId;
    }
}
