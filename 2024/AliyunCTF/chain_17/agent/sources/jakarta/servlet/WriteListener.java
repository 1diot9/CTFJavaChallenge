package jakarta.servlet;

import java.io.IOException;
import java.util.EventListener;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/WriteListener.class */
public interface WriteListener extends EventListener {
    void onWritePossible() throws IOException;

    void onError(Throwable th);
}
