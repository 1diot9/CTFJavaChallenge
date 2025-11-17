package org.h2.store;

import java.io.IOException;
import java.io.InputStream;
import org.h2.engine.SessionRemote;
import org.h2.message.DbException;
import org.h2.mvstore.DataUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/LobStorageRemoteInputStream.class */
public class LobStorageRemoteInputStream extends InputStream {
    private final SessionRemote sessionRemote;
    private final long lobId;
    private final byte[] hmac;
    private long pos;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !LobStorageRemoteInputStream.class.desiredAssertionStatus();
    }

    public LobStorageRemoteInputStream(SessionRemote sessionRemote, long j, byte[] bArr) {
        this.sessionRemote = sessionRemote;
        this.lobId = j;
        this.hmac = bArr;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        byte[] bArr = new byte[1];
        int read = read(bArr, 0, 1);
        return read < 0 ? read : bArr[0] & 255;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (!$assertionsDisabled && i2 < 0) {
            throw new AssertionError();
        }
        if (i2 == 0) {
            return 0;
        }
        try {
            int readLob = this.sessionRemote.readLob(this.lobId, this.hmac, this.pos, bArr, i, i2);
            if (readLob == 0) {
                return -1;
            }
            this.pos += readLob;
            return readLob;
        } catch (DbException e) {
            throw DataUtils.convertToIOException(e);
        }
    }
}
