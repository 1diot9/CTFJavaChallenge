package cn.hutool.core.io;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/NullOutputStream.class */
public class NullOutputStream extends OutputStream {
    public static final NullOutputStream NULL_OUTPUT_STREAM = new NullOutputStream();

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) {
    }

    @Override // java.io.OutputStream
    public void write(int b) {
    }

    @Override // java.io.OutputStream
    public void write(byte[] b) throws IOException {
    }
}
