package org.springframework.boot.web.embedded.undertow;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.server.handlers.accesslog.DefaultAccessLogReceiver;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.valves.Constants;
import org.springframework.util.Assert;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Xnio;
import org.xnio.XnioWorker;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/AccessLogHttpHandlerFactory.class */
class AccessLogHttpHandlerFactory implements HttpHandlerFactory {
    private final File directory;
    private final String pattern;
    private final String prefix;
    private final String suffix;
    private final boolean rotate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AccessLogHttpHandlerFactory(File directory, String pattern, String prefix, String suffix, boolean rotate) {
        this.directory = directory;
        this.pattern = pattern;
        this.prefix = prefix;
        this.suffix = suffix;
        this.rotate = rotate;
    }

    @Override // org.springframework.boot.web.embedded.undertow.HttpHandlerFactory
    public HttpHandler getHandler(HttpHandler next) {
        try {
            createAccessLogDirectoryIfNecessary();
            XnioWorker worker = createWorker();
            String baseName = this.prefix != null ? this.prefix : "access_log.";
            String formatString = this.pattern != null ? this.pattern : Constants.AccessLog.COMMON_ALIAS;
            return new ClosableAccessLogHandler(next, worker, new DefaultAccessLogReceiver(worker, this.directory, baseName, this.suffix, this.rotate), formatString);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to create AccessLogHandler", ex);
        }
    }

    private void createAccessLogDirectoryIfNecessary() {
        Assert.state(this.directory != null, "Access log directory is not set");
        if (!this.directory.isDirectory() && !this.directory.mkdirs()) {
            throw new IllegalStateException("Failed to create access log directory '" + this.directory + "'");
        }
    }

    private XnioWorker createWorker() throws IOException {
        Xnio xnio = Xnio.getInstance(Undertow.class.getClassLoader());
        return xnio.createWorker(OptionMap.builder().set(Options.THREAD_DAEMON, true).getMap());
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/AccessLogHttpHandlerFactory$ClosableAccessLogHandler.class */
    private static class ClosableAccessLogHandler extends AccessLogHandler implements Closeable {
        private final DefaultAccessLogReceiver accessLogReceiver;
        private final XnioWorker worker;

        ClosableAccessLogHandler(HttpHandler next, XnioWorker worker, DefaultAccessLogReceiver accessLogReceiver, String formatString) {
            super(next, accessLogReceiver, formatString, Undertow.class.getClassLoader());
            this.worker = worker;
            this.accessLogReceiver = accessLogReceiver;
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            try {
                this.accessLogReceiver.close();
                this.worker.shutdown();
                this.worker.awaitTermination(30L, TimeUnit.SECONDS);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
