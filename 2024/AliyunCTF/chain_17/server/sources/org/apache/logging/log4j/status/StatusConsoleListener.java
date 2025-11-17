package org.apache.logging.log4j.status;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedNoReferenceMessageFactory;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/status/StatusConsoleListener.class */
public class StatusConsoleListener implements StatusListener {
    private Level level;
    private String[] filters;
    private final PrintStream stream;
    private final Logger logger;

    public StatusConsoleListener(final Level level) {
        this(level, System.out);
    }

    public StatusConsoleListener(final Level level, final PrintStream stream) {
        this(level, stream, SimpleLoggerFactory.getInstance());
    }

    StatusConsoleListener(final Level level, final PrintStream stream, final SimpleLoggerFactory loggerFactory) {
        this.level = (Level) Objects.requireNonNull(level, "level");
        this.stream = (PrintStream) Objects.requireNonNull(stream, "stream");
        this.logger = ((SimpleLoggerFactory) Objects.requireNonNull(loggerFactory, "loggerFactory")).createSimpleLogger("StatusConsoleListener", level, ParameterizedNoReferenceMessageFactory.INSTANCE, stream);
    }

    public void setLevel(final Level level) {
        this.level = level;
    }

    @Override // org.apache.logging.log4j.status.StatusListener
    public Level getStatusLevel() {
        return this.level;
    }

    @Override // org.apache.logging.log4j.status.StatusListener
    public void log(final StatusData data) {
        boolean filtered = filtered(data);
        if (!filtered) {
            this.logger.atLevel(data.getLevel()).withThrowable(data.getThrowable()).withLocation(data.getStackTraceElement()).log(data.getMessage());
        }
    }

    public void setFilters(final String... filters) {
        this.filters = filters;
    }

    private boolean filtered(final StatusData data) {
        if (this.filters == null) {
            return false;
        }
        String caller = data.getStackTraceElement().getClassName();
        for (String filter : this.filters) {
            if (caller.startsWith(filter)) {
                return true;
            }
        }
        return false;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.stream != System.out && this.stream != System.err) {
            this.stream.close();
        }
    }
}
