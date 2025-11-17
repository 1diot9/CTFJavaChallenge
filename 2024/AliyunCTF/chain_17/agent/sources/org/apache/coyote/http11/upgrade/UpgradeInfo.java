package org.apache.coyote.http11.upgrade;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/upgrade/UpgradeInfo.class */
public class UpgradeInfo {
    private UpgradeGroupInfo groupInfo = null;
    private volatile long bytesSent = 0;
    private volatile long bytesReceived = 0;
    private volatile long msgsSent = 0;
    private volatile long msgsReceived = 0;

    public UpgradeGroupInfo getGlobalProcessor() {
        return this.groupInfo;
    }

    public void setGroupInfo(UpgradeGroupInfo groupInfo) {
        if (groupInfo == null) {
            if (this.groupInfo != null) {
                this.groupInfo.removeUpgradeInfo(this);
                this.groupInfo = null;
                return;
            }
            return;
        }
        this.groupInfo = groupInfo;
        groupInfo.addUpgradeInfo(this);
    }

    public long getBytesSent() {
        return this.bytesSent;
    }

    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    public void addBytesSent(long bytesSent) {
        this.bytesSent += bytesSent;
    }

    public long getBytesReceived() {
        return this.bytesReceived;
    }

    public void setBytesReceived(long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    public void addBytesReceived(long bytesReceived) {
        this.bytesReceived += bytesReceived;
    }

    public long getMsgsSent() {
        return this.msgsSent;
    }

    public void setMsgsSent(long msgsSent) {
        this.msgsSent = msgsSent;
    }

    public void addMsgsSent(long msgsSent) {
        this.msgsSent += msgsSent;
    }

    public long getMsgsReceived() {
        return this.msgsReceived;
    }

    public void setMsgsReceived(long msgsReceived) {
        this.msgsReceived = msgsReceived;
    }

    public void addMsgsReceived(long msgsReceived) {
        this.msgsReceived += msgsReceived;
    }
}
