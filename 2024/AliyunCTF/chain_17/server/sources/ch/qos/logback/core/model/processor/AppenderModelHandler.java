package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.JoranConstants;
import ch.qos.logback.core.model.AppenderModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/AppenderModelHandler.class */
public class AppenderModelHandler<E> extends ModelHandlerBase {
    Appender<E> appender;
    private boolean inError;
    private boolean skipped;
    AppenderAttachable<E> appenderAttachable;

    public AppenderModelHandler(Context context) {
        super(context);
        this.inError = false;
        this.skipped = false;
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext mic) {
        return new AppenderModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        this.appender = null;
        this.inError = false;
        AppenderModel appenderModel = (AppenderModel) model;
        String appenderName = mic.subst(appenderModel.getName());
        if (!mic.hasDependers(appenderName)) {
            addWarn("Appender named [" + appenderName + "] not referenced. Skipping further processing.");
            this.skipped = true;
            appenderModel.markAsSkipped();
            return;
        }
        addInfo("Processing appender named [" + appenderName + "]");
        String originalClassName = appenderModel.getClassName();
        String className = mic.getImport(originalClassName);
        try {
            addInfo("About to instantiate appender of type [" + className + "]");
            this.appender = (Appender) OptionHelper.instantiateByClassName(className, (Class<?>) Appender.class, this.context);
            this.appender.setContext(this.context);
            this.appender.setName(appenderName);
            mic.pushObject(this.appender);
        } catch (Exception oops) {
            this.inError = true;
            addError("Could not create an Appender of type [" + className + "].", oops);
            throw new ModelHandlerException(oops);
        }
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void postHandle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        if (this.inError || this.skipped) {
            return;
        }
        if (this.appender instanceof LifeCycle) {
            this.appender.start();
        }
        mic.markStartOfNamedDependee(this.appender.getName());
        Object o = mic.peekObject();
        Map<String, Appender<E>> appenderBag = (Map) mic.getObjectMap().get(JoranConstants.APPENDER_BAG);
        appenderBag.put(this.appender.getName(), this.appender);
        if (o != this.appender) {
            addWarn("The object at the of the stack is not the appender named [" + this.appender.getName() + "] pushed earlier.");
        } else {
            mic.popObject();
        }
    }
}
