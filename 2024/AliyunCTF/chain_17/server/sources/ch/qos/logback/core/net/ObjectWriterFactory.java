package ch.qos.logback.core.net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/ObjectWriterFactory.class */
public class ObjectWriterFactory {
    public AutoFlushingObjectWriter newAutoFlushingObjectWriter(OutputStream outputStream) throws IOException {
        return new AutoFlushingObjectWriter(new ObjectOutputStream(outputStream), 70);
    }
}
