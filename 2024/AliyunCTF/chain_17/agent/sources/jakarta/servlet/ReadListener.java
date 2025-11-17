package jakarta.servlet;

import java.io.IOException;
import java.util.EventListener;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ReadListener.class */
public interface ReadListener extends EventListener {
    void onDataAvailable() throws IOException;

    void onAllDataRead() throws IOException;

    void onError(Throwable th);
}
