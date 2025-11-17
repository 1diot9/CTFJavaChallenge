package org.springframework.boot.web.embedded.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.api.DeploymentManager;
import jakarta.servlet.ServletException;
import java.io.Closeable;
import java.io.IOException;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/DeploymentManagerHttpHandlerFactory.class */
class DeploymentManagerHttpHandlerFactory implements HttpHandlerFactory {
    private final DeploymentManager deploymentManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DeploymentManagerHttpHandlerFactory(DeploymentManager deploymentManager) {
        this.deploymentManager = deploymentManager;
    }

    @Override // org.springframework.boot.web.embedded.undertow.HttpHandlerFactory
    public HttpHandler getHandler(HttpHandler next) {
        Assert.state(next == null, "DeploymentManagerHttpHandlerFactory must be first");
        return new DeploymentManagerHandler(this.deploymentManager);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DeploymentManager getDeploymentManager() {
        return this.deploymentManager;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/DeploymentManagerHttpHandlerFactory$DeploymentManagerHandler.class */
    static class DeploymentManagerHandler implements HttpHandler, Closeable {
        private final DeploymentManager deploymentManager;
        private final HttpHandler handler;

        DeploymentManagerHandler(DeploymentManager deploymentManager) {
            this.deploymentManager = deploymentManager;
            try {
                this.handler = deploymentManager.start();
            } catch (ServletException ex) {
                throw new RuntimeException(ex);
            }
        }

        public void handleRequest(HttpServerExchange exchange) throws Exception {
            this.handler.handleRequest(exchange);
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            try {
                this.deploymentManager.stop();
                this.deploymentManager.undeploy();
            } catch (ServletException ex) {
                throw new RuntimeException(ex);
            }
        }

        DeploymentManager getDeploymentManager() {
            return this.deploymentManager;
        }
    }
}
