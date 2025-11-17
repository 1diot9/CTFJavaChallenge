package cn.hutool.core.io.watch;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/watch/WatchAction.class */
public interface WatchAction {
    void doAction(WatchEvent<?> watchEvent, Path path);
}
