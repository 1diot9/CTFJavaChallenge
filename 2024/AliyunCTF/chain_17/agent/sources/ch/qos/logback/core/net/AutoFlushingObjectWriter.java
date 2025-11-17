package ch.qos.logback.core.net;

import java.io.IOException;
import java.io.ObjectOutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/AutoFlushingObjectWriter.class */
public class AutoFlushingObjectWriter implements ObjectWriter {
    private final ObjectOutputStream objectOutputStream;
    private final int resetFrequency;
    private int writeCounter = 0;

    public AutoFlushingObjectWriter(ObjectOutputStream objectOutputStream, int resetFrequency) {
        this.objectOutputStream = objectOutputStream;
        this.resetFrequency = resetFrequency;
    }

    @Override // ch.qos.logback.core.net.ObjectWriter
    public void write(Object object) throws IOException {
        this.objectOutputStream.writeObject(object);
        this.objectOutputStream.flush();
        preventMemoryLeak();
    }

    private void preventMemoryLeak() throws IOException {
        int i = this.writeCounter + 1;
        this.writeCounter = i;
        if (i >= this.resetFrequency) {
            this.objectOutputStream.reset();
            this.writeCounter = 0;
        }
    }
}
