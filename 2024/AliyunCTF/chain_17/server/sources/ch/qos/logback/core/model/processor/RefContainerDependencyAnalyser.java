package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;

@PhaseIndicator(phase = ProcessingPhase.DEPENDENCY_ANALYSIS)
/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/RefContainerDependencyAnalyser.class */
public class RefContainerDependencyAnalyser extends ModelHandlerBase {
    final Class<?> modelClass;

    public RefContainerDependencyAnalyser(Context context, Class<?> modelClass) {
        super(context);
        this.modelClass = modelClass;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public boolean isSupportedModelType(Model model) {
        if (this.modelClass.isInstance(model)) {
            return true;
        }
        addError("This handler can only handle models of type " + this.modelClass.getName());
        return false;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        mic.pushModel(model);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void postHandle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        Model poppedModel = mic.popModel();
        if (model != poppedModel) {
            addError("Popped model [" + String.valueOf(poppedModel) + "] different than expected [" + String.valueOf(model) + "]");
        }
    }
}
