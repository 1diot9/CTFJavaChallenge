package ch.qos.logback.classic.model.processor;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.model.RootLoggerModel;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.util.OptionHelper;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/model/processor/RootLoggerModelHandler.class */
public class RootLoggerModelHandler extends ModelHandlerBase {
    Logger root;
    boolean inError;

    public RootLoggerModelHandler(Context context) {
        super(context);
        this.inError = false;
    }

    public static RootLoggerModelHandler makeInstance(Context context, ModelInterpretationContext ic) {
        return new RootLoggerModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<RootLoggerModel> getSupportedModelClass() {
        return RootLoggerModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        this.inError = false;
        RootLoggerModel rootLoggerModel = (RootLoggerModel) model;
        LoggerContext loggerContext = (LoggerContext) this.context;
        this.root = loggerContext.getLogger("ROOT");
        String levelStr = mic.subst(rootLoggerModel.getLevel());
        if (!OptionHelper.isNullOrEmpty(levelStr)) {
            Level level = Level.toLevel(levelStr);
            addInfo("Setting level of ROOT logger to " + String.valueOf(level));
            this.root.setLevel(level);
        }
        mic.pushObject(this.root);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void postHandle(ModelInterpretationContext mic, Model model) {
        if (this.inError) {
            return;
        }
        Object o = mic.peekObject();
        if (o != this.root) {
            addWarn("The object [" + String.valueOf(o) + "] on the top the of the stack is not the root logger");
        } else {
            mic.popObject();
        }
    }
}
