package cn.hutool.core.io.watch.watchers;

import cn.hutool.core.io.watch.Watcher;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/watch/watchers/IgnoreWatcher.class */
public class IgnoreWatcher implements Watcher {
    @Override // cn.hutool.core.io.watch.Watcher
    public void onCreate(WatchEvent<?> event, Path currentPath) {
    }

    @Override // cn.hutool.core.io.watch.Watcher
    public void onModify(WatchEvent<?> event, Path currentPath) {
    }

    @Override // cn.hutool.core.io.watch.Watcher
    public void onDelete(WatchEvent<?> event, Path currentPath) {
    }

    @Override // cn.hutool.core.io.watch.Watcher
    public void onOverflow(WatchEvent<?> event, Path currentPath) {
    }
}
