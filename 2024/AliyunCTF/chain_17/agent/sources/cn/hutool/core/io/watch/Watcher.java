package cn.hutool.core.io.watch;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/watch/Watcher.class */
public interface Watcher {
    void onCreate(WatchEvent<?> watchEvent, Path path);

    void onModify(WatchEvent<?> watchEvent, Path path);

    void onDelete(WatchEvent<?> watchEvent, Path path);

    void onOverflow(WatchEvent<?> watchEvent, Path path);
}
