package org.springframework.boot.logging.logback;

import ch.qos.logback.core.model.NamedModel;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/SpringPropertyModel.class */
class SpringPropertyModel extends NamedModel {
    private String scope;
    private String defaultValue;
    private String source;

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getScope() {
        return this.scope;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getSource() {
        return this.source;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSource(String source) {
        this.source = source;
    }
}
