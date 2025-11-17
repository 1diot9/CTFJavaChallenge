package org.h2.mvstore;

import java.nio.ByteBuffer;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/MFChunk.class */
final class MFChunk extends Chunk<MFChunk> {
    private static final String ATTR_VOLUME = "vol";
    public volatile int volumeId;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MFChunk(int i) {
        super(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MFChunk(String str) {
        super(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MFChunk(Map<String, String> map) {
        super(map, false);
        this.volumeId = DataUtils.readHexInt(map, ATTR_VOLUME, 0);
    }

    @Override // org.h2.mvstore.Chunk
    protected ByteBuffer readFully(FileStore<MFChunk> fileStore, long j, int i) {
        return fileStore.readFully((FileStore<MFChunk>) this, j, i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.mvstore.Chunk
    public void dump(StringBuilder sb) {
        super.dump(sb);
        if (this.volumeId != 0) {
            DataUtils.appendMap(sb, ATTR_VOLUME, this.volumeId);
        }
    }
}
