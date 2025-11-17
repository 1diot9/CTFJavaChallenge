package ch.qos.logback.core.model;

import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/NamedComponentModel.class */
public class NamedComponentModel extends ComponentModel implements INamedModel {
    private static final long serialVersionUID = -6388316680413871442L;
    String name;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.ComponentModel, ch.qos.logback.core.model.Model
    public NamedComponentModel makeNewInstance() {
        return new NamedComponentModel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.ComponentModel, ch.qos.logback.core.model.Model
    public void mirror(Model that) {
        NamedComponentModel actual = (NamedComponentModel) that;
        super.mirror(actual);
        this.name = actual.name;
    }

    @Override // ch.qos.logback.core.model.INamedModel
    public String getName() {
        return this.name;
    }

    @Override // ch.qos.logback.core.model.INamedModel
    public void setName(String name) {
        this.name = name;
    }

    @Override // ch.qos.logback.core.model.ComponentModel, ch.qos.logback.core.model.Model
    public String toString() {
        return "NamedComponentModel [name=" + this.name + ", className=" + this.className + ", tag=" + this.tag + ", bodyText=" + this.bodyText + "]";
    }

    @Override // ch.qos.logback.core.model.ComponentModel, ch.qos.logback.core.model.Model
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + Objects.hash(this.name);
    }

    @Override // ch.qos.logback.core.model.ComponentModel, ch.qos.logback.core.model.Model
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        NamedComponentModel other = (NamedComponentModel) obj;
        return Objects.equals(this.name, other.name);
    }
}
