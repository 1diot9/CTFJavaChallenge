package jakarta.servlet;

import java.io.IOException;
import java.util.EventListener;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/AsyncListener.class */
public interface AsyncListener extends EventListener {
    void onComplete(AsyncEvent asyncEvent) throws IOException;

    void onTimeout(AsyncEvent asyncEvent) throws IOException;

    void onError(AsyncEvent asyncEvent) throws IOException;

    void onStartAsync(AsyncEvent asyncEvent) throws IOException;
}
