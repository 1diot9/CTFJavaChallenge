package ch.qos.logback.core.joran;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.DefineModel;
import ch.qos.logback.core.model.EventEvaluatorModel;
import ch.qos.logback.core.model.ImplicitModel;
import ch.qos.logback.core.model.ImportModel;
import ch.qos.logback.core.model.IncludeModel;
import ch.qos.logback.core.model.ParamModel;
import ch.qos.logback.core.model.PropertyModel;
import ch.qos.logback.core.model.SequenceNumberGeneratorModel;
import ch.qos.logback.core.model.SerializeModelModel;
import ch.qos.logback.core.model.ShutdownHookModel;
import ch.qos.logback.core.model.SiftModel;
import ch.qos.logback.core.model.StatusListenerModel;
import ch.qos.logback.core.model.TimestampModel;
import ch.qos.logback.core.model.conditional.ElseModel;
import ch.qos.logback.core.model.conditional.IfModel;
import ch.qos.logback.core.model.conditional.ThenModel;
import ch.qos.logback.core.model.processor.DefaultProcessor;
import ch.qos.logback.core.model.processor.DefineModelHandler;
import ch.qos.logback.core.model.processor.EventEvaluatorModelHandler;
import ch.qos.logback.core.model.processor.ImplicitModelHandler;
import ch.qos.logback.core.model.processor.ImportModelHandler;
import ch.qos.logback.core.model.processor.NOPModelHandler;
import ch.qos.logback.core.model.processor.PropertyModelHandler;
import ch.qos.logback.core.model.processor.SequenceNumberGeneratorModelHandler;
import ch.qos.logback.core.model.processor.SerializeModelModelHandler;
import ch.qos.logback.core.model.processor.ShutdownHookModelHandler;
import ch.qos.logback.core.model.processor.StatusListenerModelHandler;
import ch.qos.logback.core.model.processor.TimestampModelHandler;
import ch.qos.logback.core.model.processor.conditional.ElseModelHandler;
import ch.qos.logback.core.model.processor.conditional.IfModelHandler;
import ch.qos.logback.core.model.processor.conditional.ThenModelHandler;
import ch.qos.logback.core.sift.SiftModelHandler;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/ModelClassToModelHandlerLinkerBase.class */
public class ModelClassToModelHandlerLinkerBase {
    protected Context context;

    public ModelClassToModelHandlerLinkerBase(Context context) {
        this.context = context;
    }

    public void link(DefaultProcessor defaultProcessor) {
        defaultProcessor.addHandler(ImportModel.class, ImportModelHandler::makeInstance);
        defaultProcessor.addHandler(ShutdownHookModel.class, ShutdownHookModelHandler::makeInstance);
        defaultProcessor.addHandler(SequenceNumberGeneratorModel.class, SequenceNumberGeneratorModelHandler::makeInstance);
        defaultProcessor.addHandler(SerializeModelModel.class, SerializeModelModelHandler::makeInstance);
        defaultProcessor.addHandler(EventEvaluatorModel.class, EventEvaluatorModelHandler::makeInstance);
        defaultProcessor.addHandler(DefineModel.class, DefineModelHandler::makeInstance);
        defaultProcessor.addHandler(IncludeModel.class, NOPModelHandler::makeInstance);
        defaultProcessor.addHandler(ParamModel.class, ParamModelHandler::makeInstance);
        defaultProcessor.addHandler(PropertyModel.class, PropertyModelHandler::makeInstance);
        defaultProcessor.addHandler(TimestampModel.class, TimestampModelHandler::makeInstance);
        defaultProcessor.addHandler(StatusListenerModel.class, StatusListenerModelHandler::makeInstance);
        defaultProcessor.addHandler(ImplicitModel.class, ImplicitModelHandler::makeInstance);
        defaultProcessor.addHandler(IfModel.class, IfModelHandler::makeInstance);
        defaultProcessor.addHandler(ThenModel.class, ThenModelHandler::makeInstance);
        defaultProcessor.addHandler(ElseModel.class, ElseModelHandler::makeInstance);
        defaultProcessor.addHandler(SiftModel.class, SiftModelHandler::makeInstance);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void sealModelFilters(DefaultProcessor defaultProcessor) {
        defaultProcessor.getPhaseOneFilter().denyAll();
        defaultProcessor.getPhaseTwoFilter().allowAll();
    }
}
