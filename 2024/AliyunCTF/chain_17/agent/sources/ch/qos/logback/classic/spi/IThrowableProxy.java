package ch.qos.logback.classic.spi;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/spi/IThrowableProxy.class */
public interface IThrowableProxy {
    String getMessage();

    String getClassName();

    StackTraceElementProxy[] getStackTraceElementProxyArray();

    int getCommonFrames();

    IThrowableProxy getCause();

    IThrowableProxy[] getSuppressed();

    boolean isCyclic();
}
