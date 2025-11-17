package org.h2.value.lob;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/lob/LobDataInMemory.class */
public final class LobDataInMemory extends LobData {
    private final byte[] small;

    public LobDataInMemory(byte[] bArr) {
        if (bArr == null) {
            throw new IllegalStateException();
        }
        this.small = bArr;
    }

    @Override // org.h2.value.lob.LobData
    public InputStream getInputStream(long j) {
        return new ByteArrayInputStream(this.small);
    }

    public byte[] getSmall() {
        return this.small;
    }

    @Override // org.h2.value.lob.LobData
    public int getMemory() {
        return this.small.length + 127;
    }
}
