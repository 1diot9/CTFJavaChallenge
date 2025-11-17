package ch.qos.logback.classic.model;

import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.PhaseIndicator;
import ch.qos.logback.core.model.processor.ProcessingPhase;
import java.util.Objects;

@PhaseIndicator(phase = ProcessingPhase.SECOND)
/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/model/RootLoggerModel.class */
public class RootLoggerModel extends Model {
    private static final long serialVersionUID = -2811453129653502831L;
    String level;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public RootLoggerModel makeNewInstance() {
        return new RootLoggerModel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public void mirror(Model that) {
        RootLoggerModel actual = (RootLoggerModel) that;
        super.mirror(actual);
        this.level = actual.level;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override // ch.qos.logback.core.model.Model
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + Objects.hash(this.level);
    }

    @Override // ch.qos.logback.core.model.Model
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        RootLoggerModel other = (RootLoggerModel) obj;
        return Objects.equals(this.level, other.level);
    }
}
