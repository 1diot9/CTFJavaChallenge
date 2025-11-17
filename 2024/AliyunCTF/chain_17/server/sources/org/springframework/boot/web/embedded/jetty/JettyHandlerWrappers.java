package org.springframework.boot.web.embedded.jetty;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.Callback;
import org.springframework.boot.web.server.Compression;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/JettyHandlerWrappers.class */
final class JettyHandlerWrappers {
    private JettyHandlerWrappers() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Handler.Wrapper createGzipHandlerWrapper(Compression compression) {
        GzipHandler handler = new GzipHandler();
        handler.setMinGzipSize((int) compression.getMinResponseSize().toBytes());
        handler.setIncludedMimeTypes(compression.getMimeTypes());
        for (HttpMethod httpMethod : HttpMethod.values()) {
            handler.addIncludedMethods(new String[]{httpMethod.name()});
        }
        return handler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Handler.Wrapper createServerHeaderHandlerWrapper(String header) {
        return new ServerHeaderHandler(header);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/JettyHandlerWrappers$ServerHeaderHandler.class */
    private static class ServerHeaderHandler extends Handler.Wrapper {
        private static final String SERVER_HEADER = "server";
        private final String value;

        ServerHeaderHandler(String value) {
            this.value = value;
        }

        public boolean handle(Request request, Response response, Callback callback) throws Exception {
            HttpFields.Mutable headers = response.getHeaders();
            if (!headers.contains(SERVER_HEADER)) {
                headers.add(SERVER_HEADER, this.value);
            }
            return super.handle(request, response, callback);
        }
    }
}
