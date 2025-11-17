package jakarta.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/Decoder.class */
public interface Decoder {

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/Decoder$Binary.class */
    public interface Binary<T> extends Decoder {
        T decode(ByteBuffer byteBuffer) throws DecodeException;

        boolean willDecode(ByteBuffer byteBuffer);
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/Decoder$BinaryStream.class */
    public interface BinaryStream<T> extends Decoder {
        T decode(InputStream inputStream) throws DecodeException, IOException;
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/Decoder$Text.class */
    public interface Text<T> extends Decoder {
        T decode(String str) throws DecodeException;

        boolean willDecode(String str);
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/Decoder$TextStream.class */
    public interface TextStream<T> extends Decoder {
        T decode(Reader reader) throws DecodeException, IOException;
    }

    default void init(EndpointConfig endpointConfig) {
    }

    default void destroy() {
    }
}
