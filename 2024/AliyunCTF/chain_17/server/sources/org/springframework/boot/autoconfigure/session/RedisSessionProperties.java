package org.springframework.boot.autoconfigure.session;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.session.FlushMode;
import org.springframework.session.SaveMode;

@ConfigurationProperties(prefix = "spring.session.redis")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/RedisSessionProperties.class */
public class RedisSessionProperties {
    private String cleanupCron;
    private String namespace = "spring:session";
    private FlushMode flushMode = FlushMode.ON_SAVE;
    private SaveMode saveMode = SaveMode.ON_SET_ATTRIBUTE;
    private ConfigureAction configureAction = ConfigureAction.NOTIFY_KEYSPACE_EVENTS;
    private RepositoryType repositoryType = RepositoryType.DEFAULT;

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/RedisSessionProperties$ConfigureAction.class */
    public enum ConfigureAction {
        NOTIFY_KEYSPACE_EVENTS,
        NONE
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/RedisSessionProperties$RepositoryType.class */
    public enum RepositoryType {
        DEFAULT,
        INDEXED
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public FlushMode getFlushMode() {
        return this.flushMode;
    }

    public void setFlushMode(FlushMode flushMode) {
        this.flushMode = flushMode;
    }

    public SaveMode getSaveMode() {
        return this.saveMode;
    }

    public void setSaveMode(SaveMode saveMode) {
        this.saveMode = saveMode;
    }

    public String getCleanupCron() {
        return this.cleanupCron;
    }

    public void setCleanupCron(String cleanupCron) {
        this.cleanupCron = cleanupCron;
    }

    public ConfigureAction getConfigureAction() {
        return this.configureAction;
    }

    public void setConfigureAction(ConfigureAction configureAction) {
        this.configureAction = configureAction;
    }

    public RepositoryType getRepositoryType() {
        return this.repositoryType;
    }

    public void setRepositoryType(RepositoryType repositoryType) {
        this.repositoryType = repositoryType;
    }
}
