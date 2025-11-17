package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.AppenderRefModel;
import ch.qos.logback.core.model.Model;

@PhaseIndicator(phase = ProcessingPhase.DEPENDENCY_ANALYSIS)
/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/AppenderRefDependencyAnalyser.class */
public class AppenderRefDependencyAnalyser extends ModelHandlerBase {
    public AppenderRefDependencyAnalyser(Context context) {
        super(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<AppenderRefModel> getSupportedModelClass() {
        return AppenderRefModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        Model depender;
        AppenderRefModel appenderRefModel = (AppenderRefModel) model;
        String ref = mic.subst(appenderRefModel.getRef());
        if (mic.isModelStackEmpty()) {
            depender = appenderRefModel;
        } else {
            Model parentModel = mic.peekModel();
            depender = parentModel;
        }
        DependencyDefinition dd = new DependencyDefinition(depender, ref);
        mic.addDependencyDefinition(dd);
    }
}
