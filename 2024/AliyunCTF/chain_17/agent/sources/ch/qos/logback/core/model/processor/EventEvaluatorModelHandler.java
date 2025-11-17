package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.model.EventEvaluatorModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/EventEvaluatorModelHandler.class */
public class EventEvaluatorModelHandler extends ModelHandlerBase {
    EventEvaluator<?> evaluator;
    boolean inError;

    public EventEvaluatorModelHandler(Context context) {
        super(context);
        this.inError = false;
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new EventEvaluatorModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<EventEvaluatorModel> getSupportedModelClass() {
        return EventEvaluatorModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext intercon, Model model) throws ModelHandlerException {
        String className;
        EventEvaluatorModel eem = (EventEvaluatorModel) model;
        String className2 = eem.getClassName();
        if (OptionHelper.isNullOrEmpty(className2)) {
            String defaultClassName = defaultClassName(intercon, eem);
            if (OptionHelper.isNullOrEmpty(defaultClassName)) {
                this.inError = true;
                addError("Mandatory \"class\" attribute missing for <evaluator>");
                addError("No default classname could be found.");
                return;
            }
            addInfo("Assuming default evaluator class [" + defaultClassName + "]");
            className = defaultClassName;
        } else {
            className = intercon.getImport(className2);
        }
        String evaluatorName = intercon.subst(eem.getName());
        try {
            this.evaluator = (EventEvaluator) OptionHelper.instantiateByClassName(className, (Class<?>) EventEvaluator.class, this.context);
            this.evaluator.setContext(this.context);
            this.evaluator.setName(evaluatorName);
            intercon.pushObject(this.evaluator);
        } catch (Exception oops) {
            this.inError = true;
            addError("Could not create evaluator of type " + className + "].", oops);
        }
    }

    private String defaultClassName(ModelInterpretationContext mic, EventEvaluatorModel model) {
        DefaultNestedComponentRegistry registry = mic.getDefaultNestedComponentRegistry();
        return registry.findDefaultComponentTypeByTag(model.getTag());
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void postHandle(ModelInterpretationContext intercon, Model model) throws ModelHandlerException {
        if (this.inError) {
            return;
        }
        if (this.evaluator instanceof LifeCycle) {
            this.evaluator.start();
            addInfo("Starting evaluator named [" + this.evaluator.getName() + "]");
        }
        Object o = intercon.peekObject();
        if (o != this.evaluator) {
            addWarn("The object on the top the of the stack is not the evaluator pushed earlier.");
            return;
        }
        intercon.popObject();
        try {
            Map<String, EventEvaluator<?>> evaluatorMap = (Map) this.context.getObject(CoreConstants.EVALUATOR_MAP);
            if (evaluatorMap == null) {
                addError("Could not find EvaluatorMap");
            } else {
                evaluatorMap.put(this.evaluator.getName(), this.evaluator);
            }
        } catch (Exception ex) {
            addError("Could not set evaluator named [" + String.valueOf(this.evaluator) + "].", ex);
        }
    }
}
