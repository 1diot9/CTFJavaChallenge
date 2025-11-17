package ch.qos.logback.classic.model;

import ch.qos.logback.core.model.Model;
import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/model/ConfigurationModel.class */
public class ConfigurationModel extends Model {
    private static final long serialVersionUID = 1286156598561818515L;
    String debugStr;
    String scanStr;
    String scanPeriodStr;
    String packagingDataStr;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public ConfigurationModel makeNewInstance() {
        return new ConfigurationModel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public void mirror(Model that) {
        ConfigurationModel actual = (ConfigurationModel) that;
        super.mirror(that);
        this.debugStr = actual.debugStr;
        this.scanStr = actual.scanStr;
        this.scanPeriodStr = actual.scanPeriodStr;
        this.packagingDataStr = actual.packagingDataStr;
    }

    public String getDebugStr() {
        return this.debugStr;
    }

    public void setDebugStr(String debugStr) {
        this.debugStr = debugStr;
    }

    public String getScanStr() {
        return this.scanStr;
    }

    public void setScanStr(String scanStr) {
        this.scanStr = scanStr;
    }

    public String getScanPeriodStr() {
        return this.scanPeriodStr;
    }

    public void setScanPeriodStr(String scanPeriodStr) {
        this.scanPeriodStr = scanPeriodStr;
    }

    public String getPackagingDataStr() {
        return this.packagingDataStr;
    }

    public void setPackagingDataStr(String packagingDataStr) {
        this.packagingDataStr = packagingDataStr;
    }

    @Override // ch.qos.logback.core.model.Model
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + Objects.hash(this.debugStr, this.packagingDataStr, this.scanPeriodStr, this.scanStr);
    }

    @Override // ch.qos.logback.core.model.Model
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        ConfigurationModel other = (ConfigurationModel) obj;
        return Objects.equals(this.debugStr, other.debugStr) && Objects.equals(this.packagingDataStr, other.packagingDataStr) && Objects.equals(this.scanPeriodStr, other.scanPeriodStr) && Objects.equals(this.scanStr, other.scanStr);
    }
}
