package org.apache.coyote.http2;

import java.util.function.IntPredicate;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/FrameType.class */
enum FrameType {
    DATA(0, false, true, null, false),
    HEADERS(1, false, true, null, true),
    PRIORITY(2, false, true, x -> {
        return x == 5;
    }, false),
    RST(3, false, true, x2 -> {
        return x2 == 4;
    }, false),
    SETTINGS(4, true, false, x3 -> {
        return x3 % 6 == 0;
    }, true),
    PUSH_PROMISE(5, false, true, x4 -> {
        return x4 >= 4;
    }, true),
    PING(6, true, false, x5 -> {
        return x5 == 8;
    }, false),
    GOAWAY(7, true, false, x6 -> {
        return x6 >= 8;
    }, false),
    WINDOW_UPDATE(8, true, true, x7 -> {
        return x7 == 4;
    }, true),
    CONTINUATION(9, false, true, null, true),
    PRIORITY_UPDATE(16, true, false, x8 -> {
        return x8 >= 4;
    }, true),
    UNKNOWN(256, true, true, null, false);

    private static final StringManager sm = StringManager.getManager((Class<?>) FrameType.class);
    private final int id;
    private final boolean streamZero;
    private final boolean streamNonZero;
    private final IntPredicate payloadSizeValidator;
    private final boolean payloadErrorFatal;

    FrameType(int id, boolean streamZero, boolean streamNonZero, IntPredicate payloadSizeValidator, boolean payloadErrorFatal) {
        this.id = id;
        this.streamZero = streamZero;
        this.streamNonZero = streamNonZero;
        this.payloadSizeValidator = payloadSizeValidator;
        this.payloadErrorFatal = payloadErrorFatal;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getId() {
        return this.id;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte getIdByte() {
        return (byte) this.id;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void check(int streamId, int payloadSize) throws Http2Exception {
        if ((streamId == 0 && !this.streamZero) || (streamId != 0 && !this.streamNonZero)) {
            throw new ConnectionException(sm.getString("frameType.checkStream", this), Http2Error.PROTOCOL_ERROR);
        }
        if (this.payloadSizeValidator != null && !this.payloadSizeValidator.test(payloadSize)) {
            if (this.payloadErrorFatal || streamId == 0) {
                throw new ConnectionException(sm.getString("frameType.checkPayloadSize", Integer.toString(payloadSize), this), Http2Error.FRAME_SIZE_ERROR);
            }
            throw new StreamException(sm.getString("frameType.checkPayloadSize", Integer.toString(payloadSize), this), Http2Error.FRAME_SIZE_ERROR, streamId);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static FrameType valueOf(int i) {
        switch (i) {
            case 0:
                return DATA;
            case 1:
                return HEADERS;
            case 2:
                return PRIORITY;
            case 3:
                return RST;
            case 4:
                return SETTINGS;
            case 5:
                return PUSH_PROMISE;
            case 6:
                return PING;
            case 7:
                return GOAWAY;
            case 8:
                return WINDOW_UPDATE;
            case 9:
                return CONTINUATION;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            default:
                return UNKNOWN;
            case 16:
                return PRIORITY_UPDATE;
        }
    }
}
