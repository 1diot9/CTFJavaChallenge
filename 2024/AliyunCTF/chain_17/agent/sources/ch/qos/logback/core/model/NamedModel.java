package ch.qos.logback.core.model;

import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/NamedModel.class */
public class NamedModel extends Model implements INamedModel {
    private static final long serialVersionUID = 3549881638769570183L;
    String name;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public NamedModel makeNewInstance() {
        return new NamedModel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public void mirror(Model that) {
        NamedModel actual = (NamedModel) that;
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

    @Override // ch.qos.logback.core.model.Model
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + Objects.hash(this.name);
    }

    @Override // ch.qos.logback.core.model.Model
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        NamedModel other = (NamedModel) obj;
        return Objects.equals(this.name, other.name);
    }
}
