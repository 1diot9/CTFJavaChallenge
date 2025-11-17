package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/NOPModelHandler.class */
public class NOPModelHandler extends ModelHandlerBase {
    public NOPModelHandler(Context context) {
        super(context);
    }

    public static NOPModelHandler makeInstance(Context context, ModelInterpretationContext ic) {
        return new NOPModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext interpretationContext, Model model) {
    }
}
