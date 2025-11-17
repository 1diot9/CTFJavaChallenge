package org.h2.store;

import org.h2.message.DbException;
import org.h2.util.SmallLRUCache;
import org.h2.util.TempFileDeleter;
import org.h2.value.CompareMode;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/DataHandler.class */
public interface DataHandler {
    String getDatabasePath();

    FileStore openFile(String str, String str2, boolean z);

    void checkPowerOff() throws DbException;

    void checkWritingAllowed() throws DbException;

    int getMaxLengthInplaceLob();

    TempFileDeleter getTempFileDeleter();

    Object getLobSyncObject();

    SmallLRUCache<String, String[]> getLobFileListCache();

    LobStorageInterface getLobStorage();

    int readLob(long j, byte[] bArr, long j2, byte[] bArr2, int i, int i2);

    CompareMode getCompareMode();
}
