package org.apache.coyote.http11.upgrade;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import org.apache.tomcat.util.modeler.BaseModelMBean;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/upgrade/UpgradeGroupInfo.class */
public class UpgradeGroupInfo extends BaseModelMBean {
    private final Set<UpgradeInfo> upgradeInfos = new ConcurrentHashMap().keySet(Boolean.TRUE);
    private LongAdder deadBytesReceived = new LongAdder();
    private LongAdder deadBytesSent = new LongAdder();
    private LongAdder deadMsgsReceived = new LongAdder();
    private LongAdder deadMsgsSent = new LongAdder();

    public void addUpgradeInfo(UpgradeInfo ui) {
        this.upgradeInfos.add(ui);
    }

    public void removeUpgradeInfo(UpgradeInfo ui) {
        if (ui != null) {
            this.deadBytesReceived.add(ui.getBytesReceived());
            this.deadBytesSent.add(ui.getBytesSent());
            this.deadMsgsReceived.add(ui.getMsgsReceived());
            this.deadMsgsSent.add(ui.getMsgsSent());
            this.upgradeInfos.remove(ui);
        }
    }

    public long getBytesReceived() {
        long bytes = this.deadBytesReceived.longValue();
        for (UpgradeInfo ui : this.upgradeInfos) {
            bytes += ui.getBytesReceived();
        }
        return bytes;
    }

    public void setBytesReceived(long bytesReceived) {
        this.deadBytesReceived.reset();
        this.deadBytesReceived.add(bytesReceived);
        for (UpgradeInfo ui : this.upgradeInfos) {
            ui.setBytesReceived(bytesReceived);
        }
    }

    public long getBytesSent() {
        long bytes = this.deadBytesSent.longValue();
        for (UpgradeInfo ui : this.upgradeInfos) {
            bytes += ui.getBytesSent();
        }
        return bytes;
    }

    public void setBytesSent(long bytesSent) {
        this.deadBytesSent.reset();
        this.deadBytesSent.add(bytesSent);
        for (UpgradeInfo ui : this.upgradeInfos) {
            ui.setBytesSent(bytesSent);
        }
    }

    public long getMsgsReceived() {
        long msgs = this.deadMsgsReceived.longValue();
        for (UpgradeInfo ui : this.upgradeInfos) {
            msgs += ui.getMsgsReceived();
        }
        return msgs;
    }

    public void setMsgsReceived(long msgsReceived) {
        this.deadMsgsReceived.reset();
        this.deadMsgsReceived.add(msgsReceived);
        for (UpgradeInfo ui : this.upgradeInfos) {
            ui.setMsgsReceived(msgsReceived);
        }
    }

    public long getMsgsSent() {
        long msgs = this.deadMsgsSent.longValue();
        for (UpgradeInfo ui : this.upgradeInfos) {
            msgs += ui.getMsgsSent();
        }
        return msgs;
    }

    public void setMsgsSent(long msgsSent) {
        this.deadMsgsSent.reset();
        this.deadMsgsSent.add(msgsSent);
        for (UpgradeInfo ui : this.upgradeInfos) {
            ui.setMsgsSent(msgsSent);
        }
    }

    public void resetCounters() {
        setBytesReceived(0L);
        setBytesSent(0L);
        setMsgsReceived(0L);
        setMsgsSent(0L);
    }
}
