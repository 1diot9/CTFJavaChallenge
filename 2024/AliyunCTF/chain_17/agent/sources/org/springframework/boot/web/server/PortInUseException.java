package org.springframework.boot.web.server;

import java.net.BindException;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/PortInUseException.class */
public class PortInUseException extends WebServerException {
    private final int port;

    public PortInUseException(int port) {
        this(port, null);
    }

    public PortInUseException(int port, Throwable cause) {
        super("Port " + port + " is already in use", cause);
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

    public static void throwIfPortBindingException(Exception ex, IntSupplier port) {
        ifPortBindingException(ex, bindException -> {
            throw new PortInUseException(port.getAsInt(), ex);
        });
    }

    public static void ifPortBindingException(Exception ex, Consumer<BindException> action) {
        ifCausedBy(ex, BindException.class, bindException -> {
            if (bindException.getMessage().toLowerCase().contains("in use")) {
                action.accept(bindException);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <E extends Exception> void ifCausedBy(Exception ex, Class<E> causedBy, Consumer<E> action) {
        Throwable th = ex;
        while (true) {
            Throwable candidate = th;
            if (candidate != null) {
                if (causedBy.isInstance(candidate)) {
                    action.accept((Exception) candidate);
                    return;
                }
                th = candidate.getCause();
            } else {
                return;
            }
        }
    }
}
