package org.h2.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.h2.engine.SessionRemote;
import org.h2.value.ValueBlob;
import org.h2.value.ValueClob;
import org.h2.value.ValueLob;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/LobStorageFrontend.class */
public class LobStorageFrontend implements LobStorageInterface {
    public static final int TABLE_ID_SESSION_VARIABLE = -1;
    public static final int TABLE_TEMP = -2;
    public static final int TABLE_RESULT = -3;
    private final SessionRemote sessionRemote;

    public LobStorageFrontend(SessionRemote sessionRemote) {
        this.sessionRemote = sessionRemote;
    }

    @Override // org.h2.store.LobStorageInterface
    public void removeLob(ValueLob valueLob) {
    }

    @Override // org.h2.store.LobStorageInterface
    public InputStream getInputStream(long j, long j2) throws IOException {
        throw new IllegalStateException();
    }

    @Override // org.h2.store.LobStorageInterface
    public InputStream getInputStream(long j, int i, long j2) throws IOException {
        throw new IllegalStateException();
    }

    @Override // org.h2.store.LobStorageInterface
    public boolean isReadOnly() {
        return false;
    }

    @Override // org.h2.store.LobStorageInterface
    public ValueLob copyLob(ValueLob valueLob, int i) {
        throw new UnsupportedOperationException();
    }

    @Override // org.h2.store.LobStorageInterface
    public void removeAllForTable(int i) {
        throw new UnsupportedOperationException();
    }

    @Override // org.h2.store.LobStorageInterface
    public ValueBlob createBlob(InputStream inputStream, long j) {
        return ValueBlob.createTempBlob(inputStream, j, this.sessionRemote);
    }

    @Override // org.h2.store.LobStorageInterface
    public ValueClob createClob(Reader reader, long j) {
        return ValueClob.createTempClob(reader, j, this.sessionRemote);
    }
}
