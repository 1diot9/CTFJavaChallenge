package cn.hutool.extra.compress.archiver;

import cn.hutool.core.lang.Filter;
import java.io.Closeable;
import java.io.File;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/compress/archiver/Archiver.class */
public interface Archiver extends Closeable {
    Archiver add(File file, String str, Filter<File> filter);

    Archiver finish();

    @Override // java.io.Closeable, java.lang.AutoCloseable
    void close();

    default Archiver add(File file) {
        return add(file, null);
    }

    default Archiver add(File file, Filter<File> filter) {
        return add(file, "/", filter);
    }
}
