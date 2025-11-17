package cn.hutool.core.net;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/net/LocalPortGenerater.class */
public class LocalPortGenerater implements Serializable {
    private static final long serialVersionUID = 1;
    private final AtomicInteger alternativePort;

    public LocalPortGenerater(int beginPort) {
        this.alternativePort = new AtomicInteger(beginPort);
    }

    public int generate() {
        int i = this.alternativePort.get();
        while (true) {
            int validPort = i;
            if (false == NetUtil.isUsableLocalPort(validPort)) {
                i = this.alternativePort.incrementAndGet();
            } else {
                return validPort;
            }
        }
    }
}
