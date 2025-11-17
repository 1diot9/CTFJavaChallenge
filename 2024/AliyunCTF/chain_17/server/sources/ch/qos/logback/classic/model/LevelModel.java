package ch.qos.logback.classic.model;

import ch.qos.logback.core.model.Model;
import java.util.Objects;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/model/LevelModel.class */
public class LevelModel extends Model {
    private static final long serialVersionUID = -7287549849308062148L;
    String value;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public LevelModel makeNewInstance() {
        return new LevelModel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public void mirror(Model that) {
        LevelModel actual = (LevelModel) that;
        super.mirror(actual);
        this.value = actual.value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override // ch.qos.logback.core.model.Model
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + Objects.hash(this.value);
    }

    @Override // ch.qos.logback.core.model.Model
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        LevelModel other = (LevelModel) obj;
        return Objects.equals(this.value, other.value);
    }
}
