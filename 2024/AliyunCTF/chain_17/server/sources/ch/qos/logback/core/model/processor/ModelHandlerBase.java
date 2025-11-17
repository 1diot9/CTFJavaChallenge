package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.ContextAwareBase;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/ModelHandlerBase.class */
public abstract class ModelHandlerBase extends ContextAwareBase {
    public abstract void handle(ModelInterpretationContext modelInterpretationContext, Model model) throws ModelHandlerException;

    public ModelHandlerBase(Context context) {
        setContext(context);
    }

    protected Class<? extends Model> getSupportedModelClass() {
        return Model.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isSupportedModelType(Model model) {
        Class<? extends Model> modelClass = getSupportedModelClass();
        if (modelClass.isInstance(model)) {
            return true;
        }
        addError("This handler can only handle models of type [" + String.valueOf(modelClass) + "]");
        return false;
    }

    public void postHandle(ModelInterpretationContext intercon, Model model) throws ModelHandlerException {
    }

    public String toString() {
        return getClass().getName();
    }
}
