package ch.qos.logback.core.model.conditional;

import ch.qos.logback.core.model.Model;
import java.util.Objects;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/conditional/IfModel.class */
public class IfModel extends Model {
    private static final long serialVersionUID = 1516046821762377019L;
    String condition;
    BranchState branchState = null;

    /* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/conditional/IfModel$BranchState.class */
    public enum BranchState {
        IN_ERROR,
        IF_BRANCH,
        ELSE_BRANCH
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public IfModel makeNewInstance() {
        return new IfModel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public void mirror(Model that) {
        IfModel actual = (IfModel) that;
        super.mirror(actual);
        this.condition = actual.condition;
        this.branchState = actual.branchState;
    }

    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public BranchState getBranchState() {
        return this.branchState;
    }

    public void setBranchState(BranchState state) {
        this.branchState = state;
    }

    public void setBranchState(boolean booleanProxy) {
        if (booleanProxy) {
            setBranchState(BranchState.IF_BRANCH);
        } else {
            setBranchState(BranchState.ELSE_BRANCH);
        }
    }

    public void resetBranchState() {
        setBranchState((BranchState) null);
    }

    @Override // ch.qos.logback.core.model.Model
    public String toString() {
        return getClass().getSimpleName() + " [condition=\"" + this.condition + "\"]";
    }

    @Override // ch.qos.logback.core.model.Model
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + Objects.hash(this.branchState, this.condition);
    }

    @Override // ch.qos.logback.core.model.Model
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        IfModel other = (IfModel) obj;
        return this.branchState == other.branchState && Objects.equals(this.condition, other.condition);
    }
}
