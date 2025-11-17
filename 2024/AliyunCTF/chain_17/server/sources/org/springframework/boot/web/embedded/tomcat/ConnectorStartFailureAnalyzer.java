package org.springframework.boot.web.embedded.tomcat;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/tomcat/ConnectorStartFailureAnalyzer.class */
class ConnectorStartFailureAnalyzer extends AbstractFailureAnalyzer<ConnectorStartFailedException> {
    ConnectorStartFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, ConnectorStartFailedException cause) {
        return new FailureAnalysis("The Tomcat connector configured to listen on port " + cause.getPort() + " failed to start. The port may already be in use or the connector may be misconfigured.", "Verify the connector's configuration, identify and stop any process that's listening on port " + cause.getPort() + ", or configure this application to listen on another port.", cause);
    }
}
