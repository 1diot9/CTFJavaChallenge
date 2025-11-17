package ch.qos.logback.core.model;

import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/ParamModel.class */
public class ParamModel extends NamedModel {
    private static final long serialVersionUID = -3697627721759508667L;
    String value;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.NamedModel, ch.qos.logback.core.model.Model
    public ParamModel makeNewInstance() {
        return new ParamModel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.NamedModel, ch.qos.logback.core.model.Model
    public void mirror(Model that) {
        ParamModel actual = (ParamModel) that;
        super.mirror(actual);
        this.value = actual.value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override // ch.qos.logback.core.model.NamedModel, ch.qos.logback.core.model.Model
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + Objects.hash(this.value);
    }

    @Override // ch.qos.logback.core.model.NamedModel, ch.qos.logback.core.model.Model
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        ParamModel other = (ParamModel) obj;
        return Objects.equals(this.value, other.value);
    }
}
