package org.springframework.boot.web.embedded.undertow;

import io.undertow.servlet.api.DeploymentInfo;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/UndertowDeploymentInfoCustomizer.class */
public interface UndertowDeploymentInfoCustomizer {
    void customize(DeploymentInfo deploymentInfo);
}
