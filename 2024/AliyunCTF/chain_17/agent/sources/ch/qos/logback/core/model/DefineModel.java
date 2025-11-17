package ch.qos.logback.core.model;

import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/DefineModel.class */
public class DefineModel extends NamedComponentModel {
    private static final long serialVersionUID = 6209642548924431065L;
    String scopeStr;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.NamedComponentModel, ch.qos.logback.core.model.ComponentModel, ch.qos.logback.core.model.Model
    public DefineModel makeNewInstance() {
        return new DefineModel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.NamedComponentModel, ch.qos.logback.core.model.ComponentModel, ch.qos.logback.core.model.Model
    public void mirror(Model that) {
        DefineModel actual = (DefineModel) that;
        super.mirror(actual);
        this.scopeStr = actual.scopeStr;
    }

    public String getScopeStr() {
        return this.scopeStr;
    }

    public void setScopeStr(String scopeStr) {
        this.scopeStr = scopeStr;
    }

    @Override // ch.qos.logback.core.model.NamedComponentModel, ch.qos.logback.core.model.ComponentModel, ch.qos.logback.core.model.Model
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + Objects.hash(this.scopeStr);
    }

    @Override // ch.qos.logback.core.model.NamedComponentModel, ch.qos.logback.core.model.ComponentModel, ch.qos.logback.core.model.Model
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        DefineModel other = (DefineModel) obj;
        return Objects.equals(this.scopeStr, other.scopeStr);
    }
}
