package cn.hutool.extra.compress.extractor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.StrUtil;
import java.io.Closeable;
import java.io.File;
import java.util.List;
import org.apache.commons.compress.archivers.ArchiveEntry;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/compress/extractor/Extractor.class */
public interface Extractor extends Closeable {
    void extract(File file, int i, Filter<ArchiveEntry> filter);

    @Override // java.io.Closeable, java.lang.AutoCloseable
    void close();

    default void extract(File targetDir) {
        extract(targetDir, (Filter<ArchiveEntry>) null);
    }

    default void extract(File targetDir, Filter<ArchiveEntry> filter) {
        extract(targetDir, 0, filter);
    }

    default void extract(File targetDir, int stripComponents) {
        extract(targetDir, stripComponents, null);
    }

    default String stripName(String name, int stripComponents) {
        if (stripComponents <= 0) {
            return name;
        }
        List<String> nameList = StrUtil.splitTrim(name, "/");
        int size = nameList.size();
        if (size > stripComponents) {
            return CollUtil.join(CollUtil.sub((List) nameList, stripComponents, size), "/");
        }
        return null;
    }
}
