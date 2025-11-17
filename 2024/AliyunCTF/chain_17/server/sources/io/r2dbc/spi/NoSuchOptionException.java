package io.r2dbc.spi;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/NoSuchOptionException.class */
public class NoSuchOptionException extends IllegalStateException {
    private final Option<?> option;

    public NoSuchOptionException(String reason, Option<?> option) {
        super(reason);
        this.option = option;
    }

    public Option<?> getOption() {
        return this.option;
    }
}
