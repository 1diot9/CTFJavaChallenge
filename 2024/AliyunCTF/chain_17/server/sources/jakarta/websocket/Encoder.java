package jakarta.websocket;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/Encoder.class */
public interface Encoder {

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/Encoder$Binary.class */
    public interface Binary<T> extends Encoder {
        ByteBuffer encode(T t) throws EncodeException;
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/Encoder$BinaryStream.class */
    public interface BinaryStream<T> extends Encoder {
        void encode(T t, OutputStream outputStream) throws EncodeException, IOException;
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/Encoder$Text.class */
    public interface Text<T> extends Encoder {
        String encode(T t) throws EncodeException;
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/Encoder$TextStream.class */
    public interface TextStream<T> extends Encoder {
        void encode(T t, Writer writer) throws EncodeException, IOException;
    }

    default void init(EndpointConfig endpointConfig) {
    }

    default void destroy() {
    }
}
