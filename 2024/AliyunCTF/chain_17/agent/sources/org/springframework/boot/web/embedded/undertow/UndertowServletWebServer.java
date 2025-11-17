package org.springframework.boot.web.embedded.undertow;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.api.DeploymentManager;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/UndertowServletWebServer.class */
public class UndertowServletWebServer extends UndertowWebServer {
    private final String contextPath;
    private final DeploymentManager manager;

    public UndertowServletWebServer(Undertow.Builder builder, Iterable<HttpHandlerFactory> httpHandlerFactories, String contextPath, boolean autoStart) {
        super(builder, httpHandlerFactories, autoStart);
        this.contextPath = contextPath;
        this.manager = findManager(httpHandlerFactories);
    }

    private DeploymentManager findManager(Iterable<HttpHandlerFactory> httpHandlerFactories) {
        for (HttpHandlerFactory httpHandlerFactory : httpHandlerFactories) {
            if (httpHandlerFactory instanceof DeploymentManagerHttpHandlerFactory) {
                DeploymentManagerHttpHandlerFactory deploymentManagerFactory = (DeploymentManagerHttpHandlerFactory) httpHandlerFactory;
                return deploymentManagerFactory.getDeploymentManager();
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.web.embedded.undertow.UndertowWebServer
    public HttpHandler createHttpHandler() {
        HttpHandler handler = super.createHttpHandler();
        if (StringUtils.hasLength(this.contextPath)) {
            handler = Handlers.path().addPrefixPath(this.contextPath, handler);
        }
        return handler;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.web.embedded.undertow.UndertowWebServer
    public String getStartLogMessage() {
        if (!StringUtils.hasText(this.contextPath)) {
            return super.getStartLogMessage();
        }
        return super.getStartLogMessage() + " with context path '" + this.contextPath + "'";
    }

    public DeploymentManager getDeploymentManager() {
        return this.manager;
    }
}
