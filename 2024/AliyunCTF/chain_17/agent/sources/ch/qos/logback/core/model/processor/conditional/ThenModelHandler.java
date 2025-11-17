package ch.qos.logback.core.model.processor.conditional;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.conditional.IfModel;
import ch.qos.logback.core.model.conditional.ThenModel;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.spi.ErrorCodes;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/conditional/ThenModelHandler.class */
public class ThenModelHandler extends ModelHandlerBase {
    public ThenModelHandler(Context context) {
        super(context);
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new ThenModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<ThenModel> getSupportedModelClass() {
        return ThenModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        ThenModel thenModel = (ThenModel) model;
        if (mic.isModelStackEmpty()) {
            addError(ErrorCodes.MISSING_IF_EMPTY_MODEL_STACK);
            thenModel.markAsSkipped();
            return;
        }
        Model parent = mic.peekModel();
        if (!(parent instanceof IfModel)) {
            addError("Unexpected type for parent model [" + String.valueOf(parent) + "]");
            thenModel.markAsSkipped();
        } else {
            IfModel ifModel = (IfModel) parent;
            if (ifModel.getBranchState() != IfModel.BranchState.IF_BRANCH) {
                thenModel.deepMarkAsSkipped();
            }
        }
    }
}
