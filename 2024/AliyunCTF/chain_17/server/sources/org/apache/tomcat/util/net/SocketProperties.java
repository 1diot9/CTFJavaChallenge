package org.apache.tomcat.util.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import javax.management.ObjectName;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SocketProperties.class */
public class SocketProperties {
    protected int processorCache = 0;
    protected int eventCache = 0;
    protected boolean directBuffer = false;
    protected boolean directSslBuffer = false;
    protected Integer rxBufSize = null;
    protected Integer txBufSize = null;
    protected int appReadBufSize = 8192;
    protected int appWriteBufSize = 8192;
    protected int bufferPool = -2;
    protected int bufferPoolSize = -2;
    protected Boolean tcpNoDelay = Boolean.TRUE;
    protected Boolean soKeepAlive = null;
    protected Boolean ooBInline = null;
    protected Boolean soReuseAddress = null;
    protected Boolean soLingerOn = null;
    protected Integer soLingerTime = null;
    protected Integer soTimeout = 20000;
    protected Integer performanceConnectionTime = null;
    protected Integer performanceLatency = null;
    protected Integer performanceBandwidth = null;
    protected long timeoutInterval = 1000;
    protected int unlockTimeout = 250;
    private ObjectName oname = null;

    public void setProperties(Socket socket) throws SocketException {
        if (this.rxBufSize != null) {
            socket.setReceiveBufferSize(this.rxBufSize.intValue());
        }
        if (this.txBufSize != null) {
            socket.setSendBufferSize(this.txBufSize.intValue());
        }
        if (this.ooBInline != null) {
            socket.setOOBInline(this.ooBInline.booleanValue());
        }
        if (this.soKeepAlive != null) {
            socket.setKeepAlive(this.soKeepAlive.booleanValue());
        }
        if (this.performanceConnectionTime != null && this.performanceLatency != null && this.performanceBandwidth != null) {
            socket.setPerformancePreferences(this.performanceConnectionTime.intValue(), this.performanceLatency.intValue(), this.performanceBandwidth.intValue());
        }
        if (this.soReuseAddress != null) {
            socket.setReuseAddress(this.soReuseAddress.booleanValue());
        }
        if (this.soLingerOn != null && this.soLingerTime != null) {
            socket.setSoLinger(this.soLingerOn.booleanValue(), this.soLingerTime.intValue());
        }
        if (this.soTimeout != null && this.soTimeout.intValue() >= 0) {
            socket.setSoTimeout(this.soTimeout.intValue());
        }
        if (this.tcpNoDelay != null) {
            try {
                socket.setTcpNoDelay(this.tcpNoDelay.booleanValue());
            } catch (SocketException e) {
            }
        }
    }

    public void setProperties(ServerSocket socket) throws SocketException {
        if (this.rxBufSize != null) {
            socket.setReceiveBufferSize(this.rxBufSize.intValue());
        }
        if (this.performanceConnectionTime != null && this.performanceLatency != null && this.performanceBandwidth != null) {
            socket.setPerformancePreferences(this.performanceConnectionTime.intValue(), this.performanceLatency.intValue(), this.performanceBandwidth.intValue());
        }
        if (this.soReuseAddress != null) {
            socket.setReuseAddress(this.soReuseAddress.booleanValue());
        }
        if (this.soTimeout != null && this.soTimeout.intValue() >= 0) {
            socket.setSoTimeout(this.soTimeout.intValue());
        }
    }

    public void setProperties(AsynchronousSocketChannel socket) throws IOException {
        if (this.rxBufSize != null) {
            socket.setOption((SocketOption<SocketOption>) StandardSocketOptions.SO_RCVBUF, (SocketOption) this.rxBufSize);
        }
        if (this.txBufSize != null) {
            socket.setOption((SocketOption<SocketOption>) StandardSocketOptions.SO_SNDBUF, (SocketOption) this.txBufSize);
        }
        if (this.soKeepAlive != null) {
            socket.setOption((SocketOption<SocketOption>) StandardSocketOptions.SO_KEEPALIVE, (SocketOption) this.soKeepAlive);
        }
        if (this.soReuseAddress != null) {
            socket.setOption((SocketOption<SocketOption>) StandardSocketOptions.SO_REUSEADDR, (SocketOption) this.soReuseAddress);
        }
        if (this.soLingerOn != null && this.soLingerOn.booleanValue() && this.soLingerTime != null) {
            socket.setOption((SocketOption<SocketOption>) StandardSocketOptions.SO_LINGER, (SocketOption) this.soLingerTime);
        }
        if (this.tcpNoDelay != null) {
            socket.setOption((SocketOption<SocketOption>) StandardSocketOptions.TCP_NODELAY, (SocketOption) this.tcpNoDelay);
        }
    }

    public void setProperties(AsynchronousServerSocketChannel socket) throws IOException {
        if (this.rxBufSize != null) {
            socket.setOption((SocketOption<SocketOption>) StandardSocketOptions.SO_RCVBUF, (SocketOption) this.rxBufSize);
        }
        if (this.soReuseAddress != null) {
            socket.setOption((SocketOption<SocketOption>) StandardSocketOptions.SO_REUSEADDR, (SocketOption) this.soReuseAddress);
        }
    }

    public boolean getDirectBuffer() {
        return this.directBuffer;
    }

    public boolean getDirectSslBuffer() {
        return this.directSslBuffer;
    }

    public boolean getOoBInline() {
        return this.ooBInline.booleanValue();
    }

    public int getPerformanceBandwidth() {
        return this.performanceBandwidth.intValue();
    }

    public int getPerformanceConnectionTime() {
        return this.performanceConnectionTime.intValue();
    }

    public int getPerformanceLatency() {
        return this.performanceLatency.intValue();
    }

    public int getRxBufSize() {
        return this.rxBufSize.intValue();
    }

    public boolean getSoKeepAlive() {
        return this.soKeepAlive.booleanValue();
    }

    public boolean getSoLingerOn() {
        return this.soLingerOn.booleanValue();
    }

    public int getSoLingerTime() {
        return this.soLingerTime.intValue();
    }

    public boolean getSoReuseAddress() {
        return this.soReuseAddress.booleanValue();
    }

    public int getSoTimeout() {
        return this.soTimeout.intValue();
    }

    public boolean getTcpNoDelay() {
        return this.tcpNoDelay.booleanValue();
    }

    public int getTxBufSize() {
        return this.txBufSize.intValue();
    }

    public int getBufferPool() {
        return this.bufferPool;
    }

    public int getBufferPoolSize() {
        return this.bufferPoolSize;
    }

    public int getEventCache() {
        return this.eventCache;
    }

    public int getAppReadBufSize() {
        return this.appReadBufSize;
    }

    public int getAppWriteBufSize() {
        return this.appWriteBufSize;
    }

    public int getProcessorCache() {
        return this.processorCache;
    }

    public long getTimeoutInterval() {
        return this.timeoutInterval;
    }

    public int getDirectBufferPool() {
        return this.bufferPool;
    }

    public void setPerformanceConnectionTime(int performanceConnectionTime) {
        this.performanceConnectionTime = Integer.valueOf(performanceConnectionTime);
    }

    public void setTxBufSize(int txBufSize) {
        this.txBufSize = Integer.valueOf(txBufSize);
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = Boolean.valueOf(tcpNoDelay);
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = Integer.valueOf(soTimeout);
    }

    public void setSoReuseAddress(boolean soReuseAddress) {
        this.soReuseAddress = Boolean.valueOf(soReuseAddress);
    }

    public void setSoLingerTime(int soLingerTime) {
        this.soLingerTime = Integer.valueOf(soLingerTime);
    }

    public void setSoKeepAlive(boolean soKeepAlive) {
        this.soKeepAlive = Boolean.valueOf(soKeepAlive);
    }

    public void setRxBufSize(int rxBufSize) {
        this.rxBufSize = Integer.valueOf(rxBufSize);
    }

    public void setPerformanceLatency(int performanceLatency) {
        this.performanceLatency = Integer.valueOf(performanceLatency);
    }

    public void setPerformanceBandwidth(int performanceBandwidth) {
        this.performanceBandwidth = Integer.valueOf(performanceBandwidth);
    }

    public void setOoBInline(boolean ooBInline) {
        this.ooBInline = Boolean.valueOf(ooBInline);
    }

    public void setDirectBuffer(boolean directBuffer) {
        this.directBuffer = directBuffer;
    }

    public void setDirectSslBuffer(boolean directSslBuffer) {
        this.directSslBuffer = directSslBuffer;
    }

    public void setSoLingerOn(boolean soLingerOn) {
        this.soLingerOn = Boolean.valueOf(soLingerOn);
    }

    public void setBufferPool(int bufferPool) {
        this.bufferPool = bufferPool;
    }

    public void setBufferPoolSize(int bufferPoolSize) {
        this.bufferPoolSize = bufferPoolSize;
    }

    public void setEventCache(int eventCache) {
        this.eventCache = eventCache;
    }

    public void setAppReadBufSize(int appReadBufSize) {
        this.appReadBufSize = appReadBufSize;
    }

    public void setAppWriteBufSize(int appWriteBufSize) {
        this.appWriteBufSize = appWriteBufSize;
    }

    public void setProcessorCache(int processorCache) {
        this.processorCache = processorCache;
    }

    public void setTimeoutInterval(long timeoutInterval) {
        this.timeoutInterval = timeoutInterval;
    }

    public void setDirectBufferPool(int directBufferPool) {
        this.bufferPool = directBufferPool;
    }

    public int getUnlockTimeout() {
        return this.unlockTimeout;
    }

    public void setUnlockTimeout(int unlockTimeout) {
        this.unlockTimeout = unlockTimeout;
    }

    public int getActualBufferPool(int bufferOverhead) {
        if (this.bufferPool != -2) {
            return this.bufferPool;
        }
        if (this.bufferPoolSize == -1) {
            return -1;
        }
        if (this.bufferPoolSize == 0) {
            return 0;
        }
        long actualBufferPoolSize = this.bufferPoolSize;
        if (actualBufferPoolSize == -2) {
            long maxMemory = Runtime.getRuntime().maxMemory();
            if (maxMemory > 2147483647L) {
                actualBufferPoolSize = maxMemory / 32;
            } else {
                return 0;
            }
        }
        int bufSize = this.appReadBufSize + this.appWriteBufSize + bufferOverhead;
        if (bufSize == 0) {
            return 0;
        }
        long poolSize = actualBufferPoolSize / bufSize;
        if (poolSize > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) poolSize;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setObjectName(ObjectName oname) {
        this.oname = oname;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ObjectName getObjectName() {
        return this.oname;
    }
}
