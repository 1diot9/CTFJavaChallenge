package org.apache.coyote;

import java.util.ArrayList;
import java.util.List;
import org.apache.tomcat.util.modeler.BaseModelMBean;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/RequestGroupInfo.class */
public class RequestGroupInfo extends BaseModelMBean {
    private final List<RequestInfo> processors = new ArrayList();
    private long deadMaxTime = 0;
    private long deadProcessingTime = 0;
    private int deadRequestCount = 0;
    private int deadErrorCount = 0;
    private long deadBytesReceived = 0;
    private long deadBytesSent = 0;

    public synchronized void addRequestProcessor(RequestInfo rp) {
        this.processors.add(rp);
    }

    public synchronized void removeRequestProcessor(RequestInfo rp) {
        if (rp != null) {
            if (this.deadMaxTime < rp.getMaxTime()) {
                this.deadMaxTime = rp.getMaxTime();
            }
            this.deadProcessingTime += rp.getProcessingTime();
            this.deadRequestCount += rp.getRequestCount();
            this.deadErrorCount += rp.getErrorCount();
            this.deadBytesReceived += rp.getBytesReceived();
            this.deadBytesSent += rp.getBytesSent();
            this.processors.remove(rp);
        }
    }

    public synchronized long getMaxTime() {
        long maxTime = this.deadMaxTime;
        for (RequestInfo rp : this.processors) {
            if (maxTime < rp.getMaxTime()) {
                maxTime = rp.getMaxTime();
            }
        }
        return maxTime;
    }

    public synchronized void setMaxTime(long maxTime) {
        this.deadMaxTime = maxTime;
        for (RequestInfo rp : this.processors) {
            rp.setMaxTime(maxTime);
        }
    }

    public synchronized long getProcessingTime() {
        long time = this.deadProcessingTime;
        for (RequestInfo rp : this.processors) {
            time += rp.getProcessingTime();
        }
        return time;
    }

    public synchronized void setProcessingTime(long totalTime) {
        this.deadProcessingTime = totalTime;
        for (RequestInfo rp : this.processors) {
            rp.setProcessingTime(totalTime);
        }
    }

    public synchronized int getRequestCount() {
        int requestCount = this.deadRequestCount;
        for (RequestInfo rp : this.processors) {
            requestCount += rp.getRequestCount();
        }
        return requestCount;
    }

    public synchronized void setRequestCount(int requestCount) {
        this.deadRequestCount = requestCount;
        for (RequestInfo rp : this.processors) {
            rp.setRequestCount(requestCount);
        }
    }

    public synchronized int getErrorCount() {
        int requestCount = this.deadErrorCount;
        for (RequestInfo rp : this.processors) {
            requestCount += rp.getErrorCount();
        }
        return requestCount;
    }

    public synchronized void setErrorCount(int errorCount) {
        this.deadErrorCount = errorCount;
        for (RequestInfo rp : this.processors) {
            rp.setErrorCount(errorCount);
        }
    }

    public synchronized long getBytesReceived() {
        long bytes = this.deadBytesReceived;
        for (RequestInfo rp : this.processors) {
            bytes += rp.getBytesReceived();
        }
        return bytes;
    }

    public synchronized void setBytesReceived(long bytesReceived) {
        this.deadBytesReceived = bytesReceived;
        for (RequestInfo rp : this.processors) {
            rp.setBytesReceived(bytesReceived);
        }
    }

    public synchronized long getBytesSent() {
        long bytes = this.deadBytesSent;
        for (RequestInfo rp : this.processors) {
            bytes += rp.getBytesSent();
        }
        return bytes;
    }

    public synchronized void setBytesSent(long bytesSent) {
        this.deadBytesSent = bytesSent;
        for (RequestInfo rp : this.processors) {
            rp.setBytesSent(bytesSent);
        }
    }

    public void resetCounters() {
        setBytesReceived(0L);
        setBytesSent(0L);
        setRequestCount(0);
        setProcessingTime(0L);
        setMaxTime(0L);
        setErrorCount(0);
    }
}
