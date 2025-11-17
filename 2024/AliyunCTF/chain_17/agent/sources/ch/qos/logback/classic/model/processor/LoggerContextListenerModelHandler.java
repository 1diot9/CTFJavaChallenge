package ch.qos.logback.classic.model.processor;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.model.LoggerContextListenerModel;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.OptionHelper;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/model/processor/LoggerContextListenerModelHandler.class */
public class LoggerContextListenerModelHandler extends ModelHandlerBase {
    boolean inError;
    LoggerContextListener lcl;

    public LoggerContextListenerModelHandler(Context context) {
        super(context);
        this.inError = false;
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new LoggerContextListenerModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<LoggerContextListenerModel> getSupportedModelClass() {
        return LoggerContextListenerModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        LoggerContextListenerModel lclModel = (LoggerContextListenerModel) model;
        String className = lclModel.getClassName();
        if (OptionHelper.isNullOrEmpty(className)) {
            addError("Empty class name for LoggerContextListener");
            this.inError = true;
        } else {
            className = mic.getImport(className);
        }
        try {
            this.lcl = (LoggerContextListener) OptionHelper.instantiateByClassName(className, (Class<?>) LoggerContextListener.class, this.context);
            if (this.lcl instanceof ContextAware) {
                ((ContextAware) this.lcl).setContext(this.context);
            }
            mic.pushObject(this.lcl);
            addInfo("Adding LoggerContextListener of type [" + className + "] to the object stack");
        } catch (Exception oops) {
            this.inError = true;
            addError("Could not create LoggerContextListener of type " + className + "].", oops);
        }
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void postHandle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        if (this.inError) {
            return;
        }
        Object o = mic.peekObject();
        if (o != this.lcl) {
            addWarn("The object on the top the of the stack is not the LoggerContextListener pushed earlier.");
            return;
        }
        if (this.lcl instanceof LifeCycle) {
            ((LifeCycle) this.lcl).start();
            addInfo("Starting LoggerContextListener");
        }
        ((LoggerContext) this.context).addListener(this.lcl);
        mic.popObject();
    }
}
