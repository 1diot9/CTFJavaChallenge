package jakarta.websocket;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/RemoteEndpoint.class */
public interface RemoteEndpoint {

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/RemoteEndpoint$Async.class */
    public interface Async extends RemoteEndpoint {
        long getSendTimeout();

        void setSendTimeout(long j);

        void sendText(String str, SendHandler sendHandler);

        Future<Void> sendText(String str);

        Future<Void> sendBinary(ByteBuffer byteBuffer);

        void sendBinary(ByteBuffer byteBuffer, SendHandler sendHandler);

        Future<Void> sendObject(Object obj);

        void sendObject(Object obj, SendHandler sendHandler);
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/RemoteEndpoint$Basic.class */
    public interface Basic extends RemoteEndpoint {
        void sendText(String str) throws IOException;

        void sendBinary(ByteBuffer byteBuffer) throws IOException;

        void sendText(String str, boolean z) throws IOException;

        void sendBinary(ByteBuffer byteBuffer, boolean z) throws IOException;

        OutputStream getSendStream() throws IOException;

        Writer getSendWriter() throws IOException;

        void sendObject(Object obj) throws IOException, EncodeException;
    }

    void setBatchingAllowed(boolean z) throws IOException;

    boolean getBatchingAllowed();

    void flushBatch() throws IOException;

    void sendPing(ByteBuffer byteBuffer) throws IOException, IllegalArgumentException;

    void sendPong(ByteBuffer byteBuffer) throws IOException, IllegalArgumentException;
}
