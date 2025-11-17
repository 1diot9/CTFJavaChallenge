package ch.qos.logback.classic.model;

import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.PhaseIndicator;
import ch.qos.logback.core.model.processor.ProcessingPhase;
import java.util.Objects;

@PhaseIndicator(phase = ProcessingPhase.SECOND)
/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/model/LoggerModel.class */
public class LoggerModel extends Model {
    private static final long serialVersionUID = 5326913660697375316L;
    String name;
    String level;
    String additivity;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public LoggerModel makeNewInstance() {
        return new LoggerModel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public void mirror(Model that) {
        LoggerModel actual = (LoggerModel) that;
        super.mirror(actual);
        this.name = actual.name;
        this.level = actual.level;
        this.additivity = actual.additivity;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAdditivity() {
        return this.additivity;
    }

    public void setAdditivity(String additivity) {
        this.additivity = additivity;
    }

    @Override // ch.qos.logback.core.model.Model
    public String toString() {
        return getClass().getSimpleName() + " name=" + this.name + "]";
    }

    @Override // ch.qos.logback.core.model.Model
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + Objects.hash(this.additivity, this.level, this.name);
    }

    @Override // ch.qos.logback.core.model.Model
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        LoggerModel other = (LoggerModel) obj;
        return Objects.equals(this.additivity, other.additivity) && Objects.equals(this.level, other.level) && Objects.equals(this.name, other.name);
    }
}
