package cn.hutool.core.swing.clipboard;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.io.Closeable;
import java.util.LinkedHashSet;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/swing/clipboard/ClipboardMonitor.class */
public enum ClipboardMonitor implements ClipboardOwner, Runnable, Closeable {
    INSTANCE;

    public static final int DEFAULT_TRY_COUNT = 10;
    public static final long DEFAULT_DELAY = 100;
    private int tryCount;
    private long delay;
    private final Clipboard clipboard;
    private final Set<ClipboardListener> listenerSet;
    private boolean isRunning;

    ClipboardMonitor() {
        this(10, 100L);
    }

    ClipboardMonitor(int tryCount, long delay) {
        this(tryCount, delay, ClipboardUtil.getClipboard());
    }

    ClipboardMonitor(int tryCount, long delay, Clipboard clipboard) {
        this.listenerSet = new LinkedHashSet();
        this.tryCount = tryCount;
        this.delay = delay;
        this.clipboard = clipboard;
    }

    public ClipboardMonitor setTryCount(int tryCount) {
        this.tryCount = tryCount;
        return this;
    }

    public ClipboardMonitor setDelay(long delay) {
        this.delay = delay;
        return this;
    }

    public ClipboardMonitor addListener(ClipboardListener listener) {
        this.listenerSet.add(listener);
        return this;
    }

    public ClipboardMonitor removeListener(ClipboardListener listener) {
        this.listenerSet.remove(listener);
        return this;
    }

    public ClipboardMonitor clearListener() {
        this.listenerSet.clear();
        return this;
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        try {
            Transferable newContents = tryGetContent(clipboard);
            Transferable transferable = null;
            for (ClipboardListener listener : this.listenerSet) {
                try {
                    transferable = listener.onChange(clipboard, (Transferable) ObjectUtil.defaultIfNull(transferable, newContents));
                } catch (Throwable th) {
                }
            }
            if (this.isRunning) {
                clipboard.setContents((Transferable) ObjectUtil.defaultIfNull(transferable, ObjectUtil.defaultIfNull(newContents, contents)), this);
            }
        } catch (InterruptedException e) {
        }
    }

    @Override // java.lang.Runnable
    public synchronized void run() {
        if (false == this.isRunning) {
            Clipboard clipboard = this.clipboard;
            clipboard.setContents(clipboard.getContents((Object) null), this);
            this.isRunning = true;
        }
    }

    public void listen(boolean sync) {
        run();
        if (sync) {
            ThreadUtil.sync(this);
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.isRunning = false;
    }

    private Transferable tryGetContent(Clipboard clipboard) throws InterruptedException {
        Transferable newContents = null;
        for (int i = 0; i < this.tryCount; i++) {
            if (this.delay > 0 && i > 0) {
                Thread.sleep(this.delay);
            }
            try {
                newContents = clipboard.getContents((Object) null);
            } catch (IllegalStateException e) {
            }
            if (null != newContents) {
                return newContents;
            }
        }
        return null;
    }
}
