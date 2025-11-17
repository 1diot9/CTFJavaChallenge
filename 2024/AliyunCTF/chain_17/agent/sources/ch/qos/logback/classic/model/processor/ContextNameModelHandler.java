package ch.qos.logback.classic.model.processor;

import ch.qos.logback.classic.model.ContextNameModel;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/model/processor/ContextNameModelHandler.class */
public class ContextNameModelHandler extends ModelHandlerBase {
    public ContextNameModelHandler(Context context) {
        super(context);
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new ContextNameModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<ContextNameModel> getSupportedModelClass() {
        return ContextNameModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        ContextNameModel contextNameModel = (ContextNameModel) model;
        String finalBody = mic.subst(contextNameModel.getBodyText());
        addInfo("Setting logger context name as [" + finalBody + "]");
        try {
            this.context.setName(finalBody);
        } catch (IllegalStateException e) {
            addError("Failed to rename context [" + this.context.getName() + "] as [" + finalBody + "]", e);
        }
    }
}
