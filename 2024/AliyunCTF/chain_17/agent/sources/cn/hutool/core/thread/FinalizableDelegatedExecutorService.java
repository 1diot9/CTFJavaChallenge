package cn.hutool.core.thread;

import java.util.concurrent.ExecutorService;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/thread/FinalizableDelegatedExecutorService.class */
public class FinalizableDelegatedExecutorService extends DelegatedExecutorService {
    /* JADX INFO: Access modifiers changed from: package-private */
    public FinalizableDelegatedExecutorService(ExecutorService executor) {
        super(executor);
    }

    protected void finalize() {
        super.shutdown();
    }
}
