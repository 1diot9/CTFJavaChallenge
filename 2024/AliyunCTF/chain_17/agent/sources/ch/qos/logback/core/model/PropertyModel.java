package ch.qos.logback.core.model;

import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/PropertyModel.class */
public class PropertyModel extends NamedModel {
    private static final long serialVersionUID = 1494176979175092052L;
    String value;
    String scopeStr;
    String file;
    String resource;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.NamedModel, ch.qos.logback.core.model.Model
    public PropertyModel makeNewInstance() {
        return new PropertyModel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.NamedModel, ch.qos.logback.core.model.Model
    public void mirror(Model that) {
        PropertyModel actual = (PropertyModel) that;
        super.mirror(actual);
        this.value = actual.value;
        this.scopeStr = actual.scopeStr;
        this.file = actual.file;
        this.resource = actual.resource;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getScopeStr() {
        return this.scopeStr;
    }

    public void setScopeStr(String scopeStr) {
        this.scopeStr = scopeStr;
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getResource() {
        return this.resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override // ch.qos.logback.core.model.NamedModel, ch.qos.logback.core.model.Model
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + Objects.hash(this.file, this.resource, this.scopeStr, this.value);
    }

    @Override // ch.qos.logback.core.model.NamedModel, ch.qos.logback.core.model.Model
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        PropertyModel other = (PropertyModel) obj;
        return Objects.equals(this.file, other.file) && Objects.equals(this.resource, other.resource) && Objects.equals(this.scopeStr, other.scopeStr) && Objects.equals(this.value, other.value);
    }
}
