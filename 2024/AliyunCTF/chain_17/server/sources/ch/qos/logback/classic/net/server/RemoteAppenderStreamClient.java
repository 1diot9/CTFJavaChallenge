package ch.qos.logback.classic.net.server;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.net.HardenedObjectInputStream;
import ch.qos.logback.core.util.CloseUtil;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/net/server/RemoteAppenderStreamClient.class */
class RemoteAppenderStreamClient implements RemoteAppenderClient {
    private final String id;
    private final Socket socket;
    private final InputStream inputStream;
    private LoggerContext lc;
    private Logger logger;

    public RemoteAppenderStreamClient(String id, Socket socket) {
        this.id = id;
        this.socket = socket;
        this.inputStream = null;
    }

    public RemoteAppenderStreamClient(String id, InputStream inputStream) {
        this.id = id;
        this.socket = null;
        this.inputStream = inputStream;
    }

    @Override // ch.qos.logback.classic.net.server.RemoteAppenderClient
    public void setLoggerContext(LoggerContext lc) {
        this.lc = lc;
        this.logger = lc.getLogger(getClass().getPackage().getName());
    }

    @Override // ch.qos.logback.core.net.server.Client, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (this.socket == null) {
            return;
        }
        CloseUtil.closeQuietly(this.socket);
    }

    @Override // java.lang.Runnable
    public void run() {
        this.logger.info(String.valueOf(this) + ": connected");
        HardenedObjectInputStream ois = null;
        try {
            try {
                try {
                    ois = createObjectInputStream();
                    while (true) {
                        ILoggingEvent event = (ILoggingEvent) ois.readObject();
                        Logger remoteLogger = this.lc.getLogger(event.getLoggerName());
                        if (remoteLogger.isEnabledFor(event.getLevel())) {
                            remoteLogger.callAppenders(event);
                        }
                    }
                } catch (EOFException e) {
                    if (ois != null) {
                        CloseUtil.closeQuietly(ois);
                    }
                    close();
                    this.logger.info(String.valueOf(this) + ": connection closed");
                } catch (IOException ex) {
                    this.logger.info(String.valueOf(this) + ": " + String.valueOf(ex));
                    if (ois != null) {
                        CloseUtil.closeQuietly(ois);
                    }
                    close();
                    this.logger.info(String.valueOf(this) + ": connection closed");
                }
            } catch (ClassNotFoundException e2) {
                this.logger.error(String.valueOf(this) + ": unknown event class");
                if (ois != null) {
                    CloseUtil.closeQuietly(ois);
                }
                close();
                this.logger.info(String.valueOf(this) + ": connection closed");
            } catch (RuntimeException ex2) {
                this.logger.error(String.valueOf(this) + ": " + String.valueOf(ex2));
                if (ois != null) {
                    CloseUtil.closeQuietly(ois);
                }
                close();
                this.logger.info(String.valueOf(this) + ": connection closed");
            }
        } catch (Throwable th) {
            if (ois != null) {
                CloseUtil.closeQuietly(ois);
            }
            close();
            this.logger.info(String.valueOf(this) + ": connection closed");
            throw th;
        }
    }

    private HardenedObjectInputStream createObjectInputStream() throws IOException {
        if (this.inputStream != null) {
            return new HardenedLoggingEventInputStream(this.inputStream);
        }
        return new HardenedLoggingEventInputStream(this.socket.getInputStream());
    }

    public String toString() {
        return "client " + this.id;
    }
}
