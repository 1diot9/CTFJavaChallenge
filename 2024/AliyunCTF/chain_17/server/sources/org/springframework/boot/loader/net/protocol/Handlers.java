package org.springframework.boot.loader.net.protocol;

import java.net.URL;

/* loaded from: server.jar:org/springframework/boot/loader/net/protocol/Handlers.class */
public final class Handlers {
    private static final String PROTOCOL_HANDLER_PACKAGES = "java.protocol.handler.pkgs";
    private static final String PACKAGE = Handlers.class.getPackageName();

    private Handlers() {
    }

    public static void register() {
        String packages = System.getProperty(PROTOCOL_HANDLER_PACKAGES, "");
        System.setProperty(PROTOCOL_HANDLER_PACKAGES, (packages.isEmpty() || packages.contains(PACKAGE)) ? PACKAGE : packages + "|" + PACKAGE);
        resetCachedUrlHandlers();
    }

    private static void resetCachedUrlHandlers() {
        try {
            URL.setURLStreamHandlerFactory(null);
        } catch (Error e) {
        }
    }
}
