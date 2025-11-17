package jakarta.servlet.http;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/WebConnection.class */
public interface WebConnection extends AutoCloseable {
    ServletInputStream getInputStream() throws IOException;

    ServletOutputStream getOutputStream() throws IOException;
}
