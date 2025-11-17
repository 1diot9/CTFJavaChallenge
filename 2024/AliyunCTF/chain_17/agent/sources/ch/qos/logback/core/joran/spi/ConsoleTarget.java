package ch.qos.logback.core.joran.spi;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/spi/ConsoleTarget.class */
public enum ConsoleTarget {
    SystemOut("System.out", new OutputStream() { // from class: ch.qos.logback.core.joran.spi.ConsoleTarget.1
        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            System.out.write(b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b) throws IOException {
            System.out.write(b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            System.out.write(b, off, len);
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
            System.out.flush();
        }
    }),
    SystemErr("System.err", new OutputStream() { // from class: ch.qos.logback.core.joran.spi.ConsoleTarget.2
        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            System.err.write(b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b) throws IOException {
            System.err.write(b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            System.err.write(b, off, len);
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
            System.err.flush();
        }
    });

    private final String name;
    private final OutputStream stream;

    public static ConsoleTarget findByName(String name) {
        for (ConsoleTarget target : values()) {
            if (target.name.equalsIgnoreCase(name)) {
                return target;
            }
        }
        return null;
    }

    ConsoleTarget(String name, OutputStream stream) {
        this.name = name;
        this.stream = stream;
    }

    public String getName() {
        return this.name;
    }

    public OutputStream getStream() {
        return this.stream;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.name;
    }
}
