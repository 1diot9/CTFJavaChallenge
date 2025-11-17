package ch.qos.logback.core.model;

import java.util.Objects;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/TimestampModel.class */
public class TimestampModel extends NamedModel {
    private static final long serialVersionUID = 2096655273673863306L;
    public static final String CONTEXT_BIRTH = "contextBirth";
    String datePattern;
    String timeReference;
    String scopeStr;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.NamedModel, ch.qos.logback.core.model.Model
    public TimestampModel makeNewInstance() {
        return new TimestampModel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.NamedModel, ch.qos.logback.core.model.Model
    public void mirror(Model that) {
        TimestampModel actual = (TimestampModel) that;
        super.mirror(actual);
        this.datePattern = actual.datePattern;
        this.timeReference = actual.timeReference;
        this.scopeStr = actual.scopeStr;
    }

    public String getKey() {
        return getName();
    }

    public void setKey(String key) {
        setName(key);
    }

    public String getDatePattern() {
        return this.datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public String getTimeReference() {
        return this.timeReference;
    }

    public void setTimeReference(String timeReference) {
        this.timeReference = timeReference;
    }

    public String getScopeStr() {
        return this.scopeStr;
    }

    public void setScopeStr(String scopeStr) {
        this.scopeStr = scopeStr;
    }

    @Override // ch.qos.logback.core.model.NamedModel, ch.qos.logback.core.model.Model
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + Objects.hash(this.datePattern, this.scopeStr, this.timeReference);
    }

    @Override // ch.qos.logback.core.model.NamedModel, ch.qos.logback.core.model.Model
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        TimestampModel other = (TimestampModel) obj;
        return Objects.equals(this.datePattern, other.datePattern) && Objects.equals(this.scopeStr, other.scopeStr) && Objects.equals(this.timeReference, other.timeReference);
    }
}
