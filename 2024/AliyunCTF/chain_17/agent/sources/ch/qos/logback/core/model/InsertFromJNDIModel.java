package ch.qos.logback.core.model;

import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/InsertFromJNDIModel.class */
public class InsertFromJNDIModel extends Model {
    private static final long serialVersionUID = -7803377963650426197L;
    public static final String ENV_ENTRY_NAME_ATTR = "env-entry-name";
    public static final String AS_ATTR = "as";
    String as;
    String envEntryName;
    String scopeStr;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public InsertFromJNDIModel makeNewInstance() {
        return new InsertFromJNDIModel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public void mirror(Model that) {
        InsertFromJNDIModel actual = (InsertFromJNDIModel) that;
        super.mirror(actual);
        this.as = actual.as;
        this.envEntryName = actual.envEntryName;
        this.scopeStr = actual.scopeStr;
    }

    public String getScopeStr() {
        return this.scopeStr;
    }

    public void setScopeStr(String scopeStr) {
        this.scopeStr = scopeStr;
    }

    public String getAs() {
        return this.as;
    }

    public void setAs(String as) {
        this.as = as;
    }

    public String getEnvEntryName() {
        return this.envEntryName;
    }

    public void setEnvEntryName(String envEntryName) {
        this.envEntryName = envEntryName;
    }

    @Override // ch.qos.logback.core.model.Model
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + Objects.hash(this.as, this.envEntryName, this.scopeStr);
    }

    @Override // ch.qos.logback.core.model.Model
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        InsertFromJNDIModel other = (InsertFromJNDIModel) obj;
        return Objects.equals(this.as, other.as) && Objects.equals(this.envEntryName, other.envEntryName) && Objects.equals(this.scopeStr, other.scopeStr);
    }
}
