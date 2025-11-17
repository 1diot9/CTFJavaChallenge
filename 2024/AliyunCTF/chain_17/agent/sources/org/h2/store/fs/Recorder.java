package org.h2.store.fs;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/Recorder.class */
public interface Recorder {
    public static final int CREATE_NEW_FILE = 2;
    public static final int CREATE_TEMP_FILE = 3;
    public static final int DELETE = 4;
    public static final int OPEN_OUTPUT_STREAM = 5;
    public static final int RENAME = 6;
    public static final int TRUNCATE = 7;
    public static final int WRITE = 8;

    void log(int i, String str, byte[] bArr, long j);
}
