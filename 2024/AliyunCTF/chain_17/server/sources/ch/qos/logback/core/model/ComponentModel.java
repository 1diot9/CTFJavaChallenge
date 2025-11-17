package ch.qos.logback.core.model;

import java.util.Objects;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/ComponentModel.class */
public class ComponentModel extends Model {
    private static final long serialVersionUID = -7117814935763453139L;
    String className;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public ComponentModel makeNewInstance() {
        return new ComponentModel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public void mirror(Model that) {
        ComponentModel actual = (ComponentModel) that;
        super.mirror(actual);
        this.className = actual.className;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override // ch.qos.logback.core.model.Model
    public String toString() {
        return getClass().getSimpleName() + " [tag=" + this.tag + ", className=" + this.className + ", bodyText=" + this.bodyText + "]";
    }

    @Override // ch.qos.logback.core.model.Model
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + Objects.hash(this.className);
    }

    @Override // ch.qos.logback.core.model.Model
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        ComponentModel other = (ComponentModel) obj;
        return Objects.equals(this.className, other.className);
    }
}
