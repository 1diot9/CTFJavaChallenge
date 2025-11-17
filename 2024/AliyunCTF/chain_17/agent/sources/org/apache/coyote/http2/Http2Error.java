package org.apache.coyote.http2;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2Error.class */
enum Http2Error {
    NO_ERROR(0),
    PROTOCOL_ERROR(1),
    INTERNAL_ERROR(2),
    FLOW_CONTROL_ERROR(3),
    SETTINGS_TIMEOUT(4),
    STREAM_CLOSED(5),
    FRAME_SIZE_ERROR(6),
    REFUSED_STREAM(7),
    CANCEL(8),
    COMPRESSION_ERROR(9),
    CONNECT_ERROR(10),
    ENHANCE_YOUR_CALM(11),
    INADEQUATE_SECURITY(12),
    HTTP_1_1_REQUIRED(13);

    private final long code;

    Http2Error(long code) {
        this.code = code;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getCode() {
        return this.code;
    }

    byte[] getCodeBytes() {
        byte[] codeByte = new byte[4];
        ByteUtil.setFourBytes(codeByte, 0, this.code);
        return codeByte;
    }
}
