package jakarta.websocket;

import java.nio.ByteBuffer;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/DecodeException.class */
public class DecodeException extends Exception {
    private static final long serialVersionUID = 1;
    private ByteBuffer bb;
    private String encodedString;

    public DecodeException(ByteBuffer bb, String message, Throwable cause) {
        super(message, cause);
        this.bb = bb;
    }

    public DecodeException(String encodedString, String message, Throwable cause) {
        super(message, cause);
        this.encodedString = encodedString;
    }

    public DecodeException(ByteBuffer bb, String message) {
        super(message);
        this.bb = bb;
    }

    public DecodeException(String encodedString, String message) {
        super(message);
        this.encodedString = encodedString;
    }

    public ByteBuffer getBytes() {
        return this.bb;
    }

    public String getText() {
        return this.encodedString;
    }
}
