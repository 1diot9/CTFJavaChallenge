package cn.hutool.socket;

import cn.hutool.core.util.RuntimeUtil;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/socket/SocketConfig.class */
public class SocketConfig implements Serializable {
    private static final long serialVersionUID = 1;
    private static final int CPU_COUNT = RuntimeUtil.getProcessorCount();
    private long readTimeout;
    private long writeTimeout;
    private int threadPoolSize = CPU_COUNT;
    private int readBufferSize = 8192;
    private int writeBufferSize = 8192;

    public int getThreadPoolSize() {
        return this.threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public long getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public long getWriteTimeout() {
        return this.writeTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public int getReadBufferSize() {
        return this.readBufferSize;
    }

    public void setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
    }

    public int getWriteBufferSize() {
        return this.writeBufferSize;
    }

    public void setWriteBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
    }
}
