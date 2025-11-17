package ch.qos.logback.core.model;

import ch.qos.logback.core.joran.spi.ElementPath;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/IncludeModel.class */
public class IncludeModel extends Model {
    private static final long serialVersionUID = -7613821942841993495L;
    String optional;
    String file;
    String url;
    String resource;
    ElementPath elementPath;

    public String getOptional() {
        return this.optional;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResource() {
        return this.resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
