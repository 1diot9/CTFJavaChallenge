package ch.qos.logback.core.joran.spi;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/spi/ActionException.class */
public class ActionException extends Exception {
    private static final long serialVersionUID = 2743349809995319806L;

    public ActionException() {
    }

    public ActionException(String msg) {
        super(msg);
    }

    public ActionException(Throwable rootCause) {
        super(rootCause);
    }
}
