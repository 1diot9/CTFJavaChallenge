package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.model.Model;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/DependencyDefinition.class */
public class DependencyDefinition {
    Model depender;
    String dependee;

    public DependencyDefinition(Model depender, String dependee) {
        this.depender = depender;
        this.dependee = dependee;
    }

    public String getDependee() {
        return this.dependee;
    }

    public Model getDepender() {
        return this.depender;
    }
}
