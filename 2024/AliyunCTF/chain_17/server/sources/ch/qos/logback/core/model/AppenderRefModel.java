package ch.qos.logback.core.model;

import ch.qos.logback.core.model.processor.PhaseIndicator;
import ch.qos.logback.core.model.processor.ProcessingPhase;
import java.util.Objects;

@PhaseIndicator(phase = ProcessingPhase.SECOND)
/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/AppenderRefModel.class */
public class AppenderRefModel extends Model {
    private static final long serialVersionUID = 5238705468395447547L;
    String ref;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public AppenderRefModel makeNewInstance() {
        return new AppenderRefModel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public void mirror(Model that) {
        AppenderRefModel actual = (AppenderRefModel) that;
        super.mirror(actual);
        this.ref = actual.ref;
    }

    public String getRef() {
        return this.ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    @Override // ch.qos.logback.core.model.Model
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + Objects.hash(this.ref);
    }

    @Override // ch.qos.logback.core.model.Model
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        AppenderRefModel other = (AppenderRefModel) obj;
        return Objects.equals(this.ref, other.ref);
    }
}
