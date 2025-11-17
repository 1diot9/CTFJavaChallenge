package org.h2.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.h2.value.ValueBlob;
import org.h2.value.ValueClob;
import org.h2.value.ValueLob;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/LobStorageInterface.class */
public interface LobStorageInterface {
    ValueClob createClob(Reader reader, long j);

    ValueBlob createBlob(InputStream inputStream, long j);

    ValueLob copyLob(ValueLob valueLob, int i);

    InputStream getInputStream(long j, long j2) throws IOException;

    InputStream getInputStream(long j, int i, long j2) throws IOException;

    void removeLob(ValueLob valueLob);

    void removeAllForTable(int i);

    boolean isReadOnly();

    default void close() {
    }
}
