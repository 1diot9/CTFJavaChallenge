package org.h2.store.fs.rec;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FilePathWrapper;
import org.h2.store.fs.Recorder;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/rec/FilePathRec.class */
public class FilePathRec extends FilePathWrapper {
    private static final FilePathRec INSTANCE = new FilePathRec();
    private static Recorder recorder;
    private boolean trace;

    public static void register() {
        FilePath.register(INSTANCE);
    }

    public static void setRecorder(Recorder recorder2) {
        recorder = recorder2;
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public boolean createFile() {
        log(2, this.name);
        return super.createFile();
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public FilePath createTempFile(String str, boolean z) throws IOException {
        log(3, unwrap(this.name) + ":" + str + ":" + z);
        return super.createTempFile(str, z);
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public void delete() {
        log(4, this.name);
        super.delete();
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public FileChannel open(String str) throws IOException {
        return new FileRec(this, super.open(str), this.name);
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public OutputStream newOutputStream(boolean z) throws IOException {
        log(5, this.name);
        return super.newOutputStream(z);
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public void moveTo(FilePath filePath, boolean z) {
        log(6, unwrap(this.name) + ":" + unwrap(filePath.name));
        super.moveTo(filePath, z);
    }

    public boolean isTrace() {
        return this.trace;
    }

    public void setTrace(boolean z) {
        this.trace = z;
    }

    void log(int i, String str) {
        log(i, str, null, 0L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void log(int i, String str, byte[] bArr, long j) {
        if (recorder != null) {
            recorder.log(i, str, bArr, j);
        }
    }

    @Override // org.h2.store.fs.FilePath
    public String getScheme() {
        return "rec";
    }
}
