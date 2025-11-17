package org.yaml.snakeyaml.internal;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/internal/Logger.class */
public class Logger {
    private final java.util.logging.Logger logger;

    /* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/internal/Logger$Level.class */
    public enum Level {
        WARNING(java.util.logging.Level.FINE);

        private final java.util.logging.Level level;

        Level(java.util.logging.Level level) {
            this.level = level;
        }
    }

    private Logger(String name) {
        this.logger = java.util.logging.Logger.getLogger(name);
    }

    public static Logger getLogger(String name) {
        return new Logger(name);
    }

    public boolean isLoggable(Level level) {
        return this.logger.isLoggable(level.level);
    }

    public void warn(String msg) {
        this.logger.log(Level.WARNING.level, msg);
    }
}
