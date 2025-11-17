package org.apache.tomcat.websocket;

import jakarta.websocket.Extension;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/Transformation.class */
public interface Transformation {
    void setNext(Transformation transformation);

    boolean validateRsvBits(int i);

    Extension getExtensionResponse();

    TransformationResult getMoreData(byte b, boolean z, int i, ByteBuffer byteBuffer) throws IOException;

    boolean validateRsv(int i, byte b);

    List<MessagePart> sendMessagePart(List<MessagePart> list) throws IOException;

    void close();
}
