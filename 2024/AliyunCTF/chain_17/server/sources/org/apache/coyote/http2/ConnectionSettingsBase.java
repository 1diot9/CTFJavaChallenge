package org.apache.coyote.http2;

import java.lang.Throwable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/ConnectionSettingsBase.class */
abstract class ConnectionSettingsBase<T extends Throwable> {
    private final String connectionId;
    static final int MAX_WINDOW_SIZE = Integer.MAX_VALUE;
    static final int MIN_MAX_FRAME_SIZE = 16384;
    static final int MAX_MAX_FRAME_SIZE = 16777215;
    static final long UNLIMITED = 4294967296L;
    static final int MAX_HEADER_TABLE_SIZE = 65536;
    static final int DEFAULT_HEADER_TABLE_SIZE = 4096;
    static final boolean DEFAULT_ENABLE_PUSH = true;
    static final long DEFAULT_MAX_CONCURRENT_STREAMS = 4294967296L;
    static final int DEFAULT_INITIAL_WINDOW_SIZE = 65535;
    static final int DEFAULT_MAX_FRAME_SIZE = 16384;
    static final long DEFAULT_MAX_HEADER_LIST_SIZE = 32768;
    static final long DEFAULT_NO_RFC7540_PRIORITIES = 1;
    private final Log log = LogFactory.getLog((Class<?>) ConnectionSettingsBase.class);
    private final StringManager sm = StringManager.getManager((Class<?>) ConnectionSettingsBase.class);
    Map<Setting, Long> current = new ConcurrentHashMap();
    Map<Setting, Long> pending = new ConcurrentHashMap();

    abstract void throwException(String str, Http2Error http2Error) throws Throwable;

    abstract String getEndpointName();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConnectionSettingsBase(String connectionId) {
        this.connectionId = connectionId;
        this.current.put(Setting.HEADER_TABLE_SIZE, 4096L);
        this.current.put(Setting.ENABLE_PUSH, Long.valueOf(DEFAULT_NO_RFC7540_PRIORITIES));
        this.current.put(Setting.MAX_CONCURRENT_STREAMS, 4294967296L);
        this.current.put(Setting.INITIAL_WINDOW_SIZE, 65535L);
        this.current.put(Setting.MAX_FRAME_SIZE, 16384L);
        this.current.put(Setting.MAX_HEADER_LIST_SIZE, Long.valueOf(DEFAULT_MAX_HEADER_LIST_SIZE));
        this.current.put(Setting.NO_RFC7540_PRIORITIES, Long.valueOf(DEFAULT_NO_RFC7540_PRIORITIES));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void set(Setting setting, long value) throws Throwable {
        if (this.log.isDebugEnabled()) {
            this.log.debug(this.sm.getString("connectionSettings.debug", this.connectionId, getEndpointName(), setting, Long.toString(value)));
        }
        switch (setting) {
            case HEADER_TABLE_SIZE:
                validateHeaderTableSize(value);
                break;
            case ENABLE_PUSH:
                validateEnablePush(value);
                break;
            case INITIAL_WINDOW_SIZE:
                validateInitialWindowSize(value);
                break;
            case MAX_FRAME_SIZE:
                validateMaxFrameSize(value);
                break;
            case NO_RFC7540_PRIORITIES:
                validateNoRfc7540Priorities(value);
                break;
            case UNKNOWN:
                return;
        }
        set(setting, Long.valueOf(value));
    }

    synchronized void set(Setting setting, Long value) {
        this.current.put(setting, value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getHeaderTableSize() {
        return getMinInt(Setting.HEADER_TABLE_SIZE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean getEnablePush() {
        long result = getMin(Setting.ENABLE_PUSH);
        return result != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final long getMaxConcurrentStreams() {
        return getMax(Setting.MAX_CONCURRENT_STREAMS);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getInitialWindowSize() {
        return getMaxInt(Setting.INITIAL_WINDOW_SIZE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getMaxFrameSize() {
        return getMaxInt(Setting.MAX_FRAME_SIZE);
    }

    final long getMaxHeaderListSize() {
        return getMax(Setting.MAX_HEADER_LIST_SIZE);
    }

    private synchronized long getMin(Setting setting) {
        Long pendingValue = this.pending.get(setting);
        long currentValue = this.current.get(setting).longValue();
        if (pendingValue == null) {
            return currentValue;
        }
        return Long.min(pendingValue.longValue(), currentValue);
    }

    private synchronized int getMinInt(Setting setting) {
        long result = getMin(setting);
        if (result > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) result;
    }

    private synchronized long getMax(Setting setting) {
        Long pendingValue = this.pending.get(setting);
        long currentValue = this.current.get(setting).longValue();
        if (pendingValue == null) {
            return currentValue;
        }
        return Long.max(pendingValue.longValue(), currentValue);
    }

    private synchronized int getMaxInt(Setting setting) {
        long result = getMax(setting);
        if (result > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) result;
    }

    private void validateHeaderTableSize(long headerTableSize) throws Throwable {
        if (headerTableSize > 65536) {
            String msg = this.sm.getString("connectionSettings.headerTableSizeLimit", this.connectionId, Long.toString(headerTableSize));
            throwException(msg, Http2Error.PROTOCOL_ERROR);
        }
    }

    private void validateEnablePush(long enablePush) throws Throwable {
        if (enablePush > DEFAULT_NO_RFC7540_PRIORITIES) {
            String msg = this.sm.getString("connectionSettings.enablePushInvalid", this.connectionId, Long.toString(enablePush));
            throwException(msg, Http2Error.PROTOCOL_ERROR);
        }
    }

    private void validateInitialWindowSize(long initialWindowSize) throws Throwable {
        if (initialWindowSize > 2147483647L) {
            String msg = this.sm.getString("connectionSettings.windowSizeTooBig", this.connectionId, Long.toString(initialWindowSize), Long.toString(2147483647L));
            throwException(msg, Http2Error.FLOW_CONTROL_ERROR);
        }
    }

    private void validateMaxFrameSize(long maxFrameSize) throws Throwable {
        if (maxFrameSize < 16384 || maxFrameSize > 16777215) {
            String msg = this.sm.getString("connectionSettings.maxFrameSizeInvalid", this.connectionId, Long.toString(maxFrameSize), Integer.toString(16384), Integer.toString(MAX_MAX_FRAME_SIZE));
            throwException(msg, Http2Error.PROTOCOL_ERROR);
        }
    }

    private void validateNoRfc7540Priorities(long noRfc7540Priorities) throws Throwable {
        if (noRfc7540Priorities < 0 || noRfc7540Priorities > DEFAULT_NO_RFC7540_PRIORITIES) {
            String msg = this.sm.getString("connectionSettings.noRfc7540PrioritiesInvalid", this.connectionId, Long.toString(noRfc7540Priorities));
            throwException(msg, Http2Error.PROTOCOL_ERROR);
        }
    }
}
