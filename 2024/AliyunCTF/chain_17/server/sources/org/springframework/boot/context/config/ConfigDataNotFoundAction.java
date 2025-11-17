package org.springframework.boot.context.config;

import org.apache.commons.logging.Log;
import org.springframework.core.log.LogMessage;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataNotFoundAction.class */
public enum ConfigDataNotFoundAction {
    FAIL { // from class: org.springframework.boot.context.config.ConfigDataNotFoundAction.1
        @Override // org.springframework.boot.context.config.ConfigDataNotFoundAction
        void handle(Log logger, ConfigDataNotFoundException ex) {
            throw ex;
        }
    },
    IGNORE { // from class: org.springframework.boot.context.config.ConfigDataNotFoundAction.2
        @Override // org.springframework.boot.context.config.ConfigDataNotFoundAction
        void handle(Log logger, ConfigDataNotFoundException ex) {
            logger.trace(LogMessage.format("Ignoring missing config data %s", ex.getReferenceDescription()));
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void handle(Log logger, ConfigDataNotFoundException ex);
}
