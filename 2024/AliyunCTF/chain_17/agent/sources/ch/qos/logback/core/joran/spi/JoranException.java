package ch.qos.logback.core.joran.spi;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/spi/JoranException.class */
public class JoranException extends Exception {
    private static final long serialVersionUID = 1112493363728774021L;

    public JoranException(String msg) {
        super(msg);
    }

    public JoranException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
