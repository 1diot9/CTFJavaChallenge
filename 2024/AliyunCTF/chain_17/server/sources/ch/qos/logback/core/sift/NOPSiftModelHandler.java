package ch.qos.logback.core.sift;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.SiftModel;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/sift/NOPSiftModelHandler.class */
public class NOPSiftModelHandler extends ModelHandlerBase {
    public NOPSiftModelHandler(Context context) {
        super(context);
    }

    public static NOPSiftModelHandler makeInstance(Context context, ModelInterpretationContext ic) {
        return new NOPSiftModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<SiftModel> getSupportedModelClass() {
        return SiftModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
    }
}
