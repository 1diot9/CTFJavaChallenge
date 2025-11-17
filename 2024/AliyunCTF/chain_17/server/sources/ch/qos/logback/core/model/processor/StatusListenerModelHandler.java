package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.StatusListenerModel;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.StatusListener;
import ch.qos.logback.core.util.OptionHelper;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/StatusListenerModelHandler.class */
public class StatusListenerModelHandler extends ModelHandlerBase {
    boolean inError;
    Boolean effectivelyAdded;
    StatusListener statusListener;

    public StatusListenerModelHandler(Context context) {
        super(context);
        this.inError = false;
        this.effectivelyAdded = null;
        this.statusListener = null;
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new StatusListenerModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<StatusListenerModel> getSupportedModelClass() {
        return StatusListenerModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext ic, Model model) throws ModelHandlerException {
        StatusListenerModel slModel = (StatusListenerModel) model;
        String className = slModel.getClassName();
        if (OptionHelper.isNullOrEmpty(className)) {
            addError("Empty class name for StatusListener");
            this.inError = true;
            return;
        }
        try {
            this.statusListener = (StatusListener) OptionHelper.instantiateByClassName(ic.getImport(className), (Class<?>) StatusListener.class, this.context);
            this.effectivelyAdded = Boolean.valueOf(ic.getContext().getStatusManager().add(this.statusListener));
            if (this.statusListener instanceof ContextAware) {
                ((ContextAware) this.statusListener).setContext(this.context);
            }
            addInfo("Added status listener of type [" + slModel.getClassName() + "]");
            ic.pushObject(this.statusListener);
        } catch (Exception e) {
            this.inError = true;
            addError("Could not create an StatusListener of type [" + slModel.getClassName() + "].", e);
            throw new ModelHandlerException(e);
        }
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void postHandle(ModelInterpretationContext mic, Model m) {
        if (this.inError) {
            return;
        }
        if (isEffectivelyAdded() && (this.statusListener instanceof LifeCycle)) {
            ((LifeCycle) this.statusListener).start();
        }
        Object o = mic.peekObject();
        if (o != this.statusListener) {
            addWarn("The object at the of the stack is not the statusListener pushed earlier.");
        } else {
            mic.popObject();
        }
    }

    private boolean isEffectivelyAdded() {
        if (this.effectivelyAdded == null) {
            return false;
        }
        return this.effectivelyAdded.booleanValue();
    }
}
