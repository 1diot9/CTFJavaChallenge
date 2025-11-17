package org.apache.tomcat.util.net;

import java.nio.ByteBuffer;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/ApplicationBufferHandler.class */
public interface ApplicationBufferHandler {
    public static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
    public static final ApplicationBufferHandler EMPTY = new ApplicationBufferHandler() { // from class: org.apache.tomcat.util.net.ApplicationBufferHandler.1
        @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
        public void expand(int newSize) {
        }

        @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
        public void setByteBuffer(ByteBuffer buffer) {
        }

        @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
        public ByteBuffer getByteBuffer() {
            return EMPTY_BUFFER;
        }
    };

    void setByteBuffer(ByteBuffer byteBuffer);

    ByteBuffer getByteBuffer();

    void expand(int i);
}
