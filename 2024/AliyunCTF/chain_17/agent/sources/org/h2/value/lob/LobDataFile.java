package org.h2.value.lob;

import java.io.BufferedInputStream;
import java.io.InputStream;
import org.h2.engine.SysProperties;
import org.h2.store.DataHandler;
import org.h2.store.FileStore;
import org.h2.store.FileStoreInputStream;
import org.h2.store.fs.FileUtils;
import org.h2.value.ValueLob;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/lob/LobDataFile.class */
public final class LobDataFile extends LobData {
    private DataHandler handler;
    private final String fileName;
    private final FileStore tempFile;

    public LobDataFile(DataHandler dataHandler, String str, FileStore fileStore) {
        this.handler = dataHandler;
        this.fileName = str;
        this.tempFile = fileStore;
    }

    @Override // org.h2.value.lob.LobData
    public void remove(ValueLob valueLob) {
        if (this.fileName != null) {
            if (this.tempFile != null) {
                this.tempFile.stopAutoDelete();
            }
            synchronized (this.handler.getLobSyncObject()) {
                FileUtils.delete(this.fileName);
            }
        }
    }

    @Override // org.h2.value.lob.LobData
    public InputStream getInputStream(long j) {
        return new BufferedInputStream(new FileStoreInputStream(this.handler.openFile(this.fileName, "r", true), false, SysProperties.lobCloseBetweenReads), 4096);
    }

    @Override // org.h2.value.lob.LobData
    public DataHandler getDataHandler() {
        return this.handler;
    }

    public String toString() {
        return "lob-file: " + this.fileName;
    }
}
