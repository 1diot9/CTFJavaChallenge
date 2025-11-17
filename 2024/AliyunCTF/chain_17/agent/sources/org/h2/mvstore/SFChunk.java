package org.h2.mvstore;

import java.nio.ByteBuffer;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/SFChunk.class */
final class SFChunk extends Chunk<SFChunk> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SFChunk(int i) {
        super(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SFChunk(String str) {
        super(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SFChunk(Map<String, String> map) {
        super(map, false);
    }

    @Override // org.h2.mvstore.Chunk
    protected ByteBuffer readFully(FileStore<SFChunk> fileStore, long j, int i) {
        return fileStore.readFully((FileStore<SFChunk>) this, j, i);
    }
}
