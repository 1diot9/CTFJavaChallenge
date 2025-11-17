package org.apache.coyote.http2;

import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/ConnectionSettingsLocal.class */
class ConnectionSettingsLocal extends ConnectionSettingsBase<IllegalArgumentException> {
    private static final String ENDPOINT_NAME = "Local(client->server)";
    private boolean sendInProgress;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConnectionSettingsLocal(String connectionId) {
        super(connectionId);
        this.sendInProgress = false;
    }

    @Override // org.apache.coyote.http2.ConnectionSettingsBase
    final synchronized void set(Setting setting, Long value) {
        checkSend();
        if (this.current.get(setting).longValue() == value.longValue()) {
            this.pending.remove(setting);
        } else {
            this.pending.put(setting, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized byte[] getSettingsFrameForPending() {
        checkSend();
        int payloadSize = this.pending.size() * 6;
        byte[] result = new byte[9 + payloadSize];
        ByteUtil.setThreeBytes(result, 0, payloadSize);
        result[3] = FrameType.SETTINGS.getIdByte();
        int pos = 9;
        for (Map.Entry<Setting, Long> setting : this.pending.entrySet()) {
            ByteUtil.setTwoBytes(result, pos, setting.getKey().getId());
            int pos2 = pos + 2;
            ByteUtil.setFourBytes(result, pos2, setting.getValue().longValue());
            pos = pos2 + 4;
        }
        this.sendInProgress = true;
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized boolean ack() {
        if (this.sendInProgress) {
            this.sendInProgress = false;
            this.current.putAll(this.pending);
            this.pending.clear();
            return true;
        }
        return false;
    }

    private void checkSend() {
        if (this.sendInProgress) {
            throw new IllegalStateException();
        }
    }

    @Override // org.apache.coyote.http2.ConnectionSettingsBase
    final void throwException(String msg, Http2Error error) throws IllegalArgumentException {
        throw new IllegalArgumentException(msg);
    }

    @Override // org.apache.coyote.http2.ConnectionSettingsBase
    final String getEndpointName() {
        return ENDPOINT_NAME;
    }
}
