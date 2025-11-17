package org.apache.coyote.http2;

import org.apache.tomcat.util.net.SocketEvent;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/StreamRunnable.class */
public class StreamRunnable implements Runnable {
    private final StreamProcessor processor;
    private final SocketEvent event;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StreamRunnable(StreamProcessor processor, SocketEvent event) {
        this.processor = processor;
        this.event = event;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.processor.process(this.event);
    }
}
