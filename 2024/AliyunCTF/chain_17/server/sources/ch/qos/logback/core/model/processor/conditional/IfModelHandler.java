package ch.qos.logback.core.model.processor.conditional;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.conditional.Condition;
import ch.qos.logback.core.joran.conditional.PropertyEvalScriptBuilder;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.conditional.IfModel;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.util.EnvUtil;
import ch.qos.logback.core.util.OptionHelper;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/conditional/IfModelHandler.class */
public class IfModelHandler extends ModelHandlerBase {
    public static final String MISSING_JANINO_MSG = "Could not find Janino library on the class path. Skipping conditional processing.";
    public static final String MISSING_JANINO_SEE = "See also http://logback.qos.ch/codes.html#ifJanino";
    IfModel ifModel;

    /* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/conditional/IfModelHandler$Branch.class */
    enum Branch {
        IF_BRANCH,
        ELSE_BRANCH
    }

    public IfModelHandler(Context context) {
        super(context);
        this.ifModel = null;
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new IfModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<IfModel> getSupportedModelClass() {
        return IfModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        this.ifModel = (IfModel) model;
        if (!EnvUtil.isJaninoAvailable()) {
            addError(MISSING_JANINO_MSG);
            addError(MISSING_JANINO_SEE);
            return;
        }
        mic.pushModel(this.ifModel);
        int lineNum = model.getLineNumber();
        String conditionStr = this.ifModel.getCondition();
        if (!OptionHelper.isNullOrEmpty(conditionStr)) {
            try {
                conditionStr = OptionHelper.substVars(conditionStr, mic, this.context);
                try {
                    PropertyEvalScriptBuilder pesb = new PropertyEvalScriptBuilder(mic);
                    pesb.setContext(this.context);
                    Condition condition = pesb.build(conditionStr);
                    if (condition != null) {
                        boolean boolResult = condition.evaluate();
                        addInfo("Condition [" + conditionStr + "] evaluated to " + boolResult + " on line " + lineNum);
                        this.ifModel.setBranchState(boolResult);
                    } else {
                        addError("The condition variable is null. This should not occur.");
                        this.ifModel.setBranchState(IfModel.BranchState.IN_ERROR);
                    }
                } catch (Exception | NoClassDefFoundError e) {
                    this.ifModel.setBranchState(IfModel.BranchState.IN_ERROR);
                    addError("Failed to parse condition [" + conditionStr + "] on line " + lineNum, e);
                }
            } catch (ScanException e2) {
                addError("Failed to parse input [" + conditionStr + "] on line " + lineNum, e2);
                this.ifModel.setBranchState(IfModel.BranchState.IN_ERROR);
            }
        }
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void postHandle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        if (mic.isModelStackEmpty()) {
            addError("Unexpected unexpected empty model stack.");
            return;
        }
        Object o = mic.peekModel();
        if (o != this.ifModel) {
            addWarn("The object [" + String.valueOf(o) + "] on the top the of the stack is not the expected [" + String.valueOf(this.ifModel));
        } else {
            mic.popModel();
        }
    }
}
