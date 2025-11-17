package org.apache.coyote;

import java.util.concurrent.TimeUnit;
import javax.management.ObjectName;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/RequestInfo.class */
public class RequestInfo {
    private final Request req;
    private String workerThreadName;
    private ObjectName rpName;
    private long bytesSent;
    private long bytesReceived;
    private long processingTime;
    private long maxTime;
    private String maxRequestUri;
    private int requestCount;
    private int errorCount;
    private RequestGroupInfo global = null;
    private int stage = 0;
    private long lastRequestProcessingTime = 0;

    public RequestInfo(Request req) {
        this.req = req;
    }

    public RequestGroupInfo getGlobalProcessor() {
        return this.global;
    }

    public void setGlobalProcessor(RequestGroupInfo global) {
        if (global != null) {
            this.global = global;
            global.addRequestProcessor(this);
        } else if (this.global != null) {
            this.global.removeRequestProcessor(this);
            this.global = null;
        }
    }

    public String getMethod() {
        return this.req.method().toString();
    }

    public String getCurrentUri() {
        return this.req.requestURI().toString();
    }

    public String getCurrentQueryString() {
        return this.req.queryString().toString();
    }

    public String getProtocol() {
        return this.req.protocol().toString();
    }

    public String getVirtualHost() {
        return this.req.serverName().toString();
    }

    public int getServerPort() {
        return this.req.getServerPort();
    }

    public String getRemoteAddr() {
        this.req.action(ActionCode.REQ_HOST_ADDR_ATTRIBUTE, null);
        return this.req.remoteAddr().toString();
    }

    public String getPeerAddr() {
        this.req.action(ActionCode.REQ_PEER_ADDR_ATTRIBUTE, null);
        return this.req.peerAddr().toString();
    }

    public String getRemoteAddrForwarded() {
        String remoteAddrProxy = (String) this.req.getAttribute("org.apache.tomcat.remoteAddr");
        if (remoteAddrProxy == null) {
            return getRemoteAddr();
        }
        return remoteAddrProxy;
    }

    public int getContentLength() {
        return this.req.getContentLength();
    }

    public long getRequestBytesReceived() {
        return this.req.getBytesRead();
    }

    public long getRequestBytesSent() {
        return this.req.getResponse().getContentWritten();
    }

    public long getRequestProcessingTime() {
        long startTime = this.req.getStartTimeNanos();
        if (getStage() == 7 || startTime < 0) {
            return 0L;
        }
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateCounters() {
        this.bytesReceived += this.req.getBytesRead();
        this.bytesSent += this.req.getResponse().getContentWritten();
        this.requestCount++;
        if (this.req.getResponse().getStatus() >= 400) {
            this.errorCount++;
        }
        long time = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.req.getStartTimeNanos());
        this.lastRequestProcessingTime = time;
        this.processingTime += time;
        if (this.maxTime < time) {
            this.maxTime = time;
            this.maxRequestUri = this.req.requestURI().toString();
        }
    }

    public int getStage() {
        return this.stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public long getBytesSent() {
        return this.bytesSent;
    }

    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    public long getBytesReceived() {
        return this.bytesReceived;
    }

    public void setBytesReceived(long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    public long getProcessingTime() {
        return this.processingTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }

    public long getMaxTime() {
        return this.maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public String getMaxRequestUri() {
        return this.maxRequestUri;
    }

    public void setMaxRequestUri(String maxRequestUri) {
        this.maxRequestUri = maxRequestUri;
    }

    public int getRequestCount() {
        return this.requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public int getErrorCount() {
        return this.errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public String getWorkerThreadName() {
        return this.workerThreadName;
    }

    public ObjectName getRpName() {
        return this.rpName;
    }

    public long getLastRequestProcessingTime() {
        return this.lastRequestProcessingTime;
    }

    public void setWorkerThreadName(String workerThreadName) {
        this.workerThreadName = workerThreadName;
    }

    public void setRpName(ObjectName rpName) {
        this.rpName = rpName;
    }

    public void setLastRequestProcessingTime(long lastRequestProcessingTime) {
        this.lastRequestProcessingTime = lastRequestProcessingTime;
    }
}
