package cn.hutool.core.io.watch.watchers;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.lang.Chain;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Iterator;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/watch/watchers/WatcherChain.class */
public class WatcherChain implements Watcher, Chain<Watcher, WatcherChain> {
    private final List<Watcher> chain;

    public static WatcherChain create(Watcher... watchers) {
        return new WatcherChain(watchers);
    }

    public WatcherChain(Watcher... watchers) {
        this.chain = CollUtil.newArrayList(watchers);
    }

    @Override // cn.hutool.core.io.watch.Watcher
    public void onCreate(WatchEvent<?> event, Path currentPath) {
        for (Watcher watcher : this.chain) {
            watcher.onCreate(event, currentPath);
        }
    }

    @Override // cn.hutool.core.io.watch.Watcher
    public void onModify(WatchEvent<?> event, Path currentPath) {
        for (Watcher watcher : this.chain) {
            watcher.onModify(event, currentPath);
        }
    }

    @Override // cn.hutool.core.io.watch.Watcher
    public void onDelete(WatchEvent<?> event, Path currentPath) {
        for (Watcher watcher : this.chain) {
            watcher.onDelete(event, currentPath);
        }
    }

    @Override // cn.hutool.core.io.watch.Watcher
    public void onOverflow(WatchEvent<?> event, Path currentPath) {
        for (Watcher watcher : this.chain) {
            watcher.onOverflow(event, currentPath);
        }
    }

    @Override // java.lang.Iterable
    public Iterator<Watcher> iterator() {
        return this.chain.iterator();
    }

    @Override // cn.hutool.core.lang.Chain
    public WatcherChain addChain(Watcher element) {
        this.chain.add(element);
        return this;
    }
}
