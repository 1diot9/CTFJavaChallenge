package cn.hutool.core.thread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/thread/RejectPolicy.class */
public enum RejectPolicy {
    ABORT(new ThreadPoolExecutor.AbortPolicy()),
    DISCARD(new ThreadPoolExecutor.DiscardPolicy()),
    DISCARD_OLDEST(new ThreadPoolExecutor.DiscardOldestPolicy()),
    CALLER_RUNS(new ThreadPoolExecutor.CallerRunsPolicy()),
    BLOCK(new BlockPolicy());

    private final RejectedExecutionHandler value;

    RejectPolicy(RejectedExecutionHandler handler) {
        this.value = handler;
    }

    public RejectedExecutionHandler getValue() {
        return this.value;
    }
}
