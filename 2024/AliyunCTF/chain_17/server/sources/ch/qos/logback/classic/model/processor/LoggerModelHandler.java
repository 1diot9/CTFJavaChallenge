package ch.qos.logback.classic.model.processor;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.model.LoggerModel;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.JoranConstants;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.spi.ErrorCodes;
import ch.qos.logback.core.util.OptionHelper;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/model/processor/LoggerModelHandler.class */
public class LoggerModelHandler extends ModelHandlerBase {
    Logger logger;
    boolean inError;

    public LoggerModelHandler(Context context) {
        super(context);
        this.inError = false;
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext mic) {
        return new LoggerModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<LoggerModel> getSupportedModelClass() {
        return LoggerModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        this.inError = false;
        LoggerModel loggerModel = (LoggerModel) model;
        String finalLoggerName = mic.subst(loggerModel.getName());
        LoggerContext loggerContext = (LoggerContext) this.context;
        this.logger = loggerContext.getLogger(finalLoggerName);
        String levelStr = mic.subst(loggerModel.getLevel());
        if (!OptionHelper.isNullOrEmpty(levelStr)) {
            if (JoranConstants.INHERITED.equalsIgnoreCase(levelStr) || JoranConstants.NULL.equalsIgnoreCase(levelStr)) {
                if ("ROOT".equalsIgnoreCase(finalLoggerName)) {
                    addError(ErrorCodes.ROOT_LEVEL_CANNOT_BE_SET_TO_NULL);
                } else {
                    addInfo("Setting level of logger [" + finalLoggerName + "] to null, i.e. INHERITED");
                    this.logger.setLevel(null);
                }
            } else {
                Level level = Level.toLevel(levelStr);
                addInfo("Setting level of logger [" + finalLoggerName + "] to " + String.valueOf(level));
                this.logger.setLevel(level);
            }
        }
        String additivityStr = mic.subst(loggerModel.getAdditivity());
        if (!OptionHelper.isNullOrEmpty(additivityStr)) {
            boolean additive = OptionHelper.toBoolean(additivityStr, true);
            addInfo("Setting additivity of logger [" + finalLoggerName + "] to " + additive);
            this.logger.setAdditive(additive);
        }
        mic.pushObject(this.logger);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void postHandle(ModelInterpretationContext mic, Model model) {
        if (this.inError) {
            return;
        }
        Object o = mic.peekObject();
        if (o != this.logger) {
            LoggerModel loggerModel = (LoggerModel) model;
            addWarn("The object [" + String.valueOf(o) + "] on the top the of the stack is not the expected logger named " + loggerModel.getName());
        } else {
            mic.popObject();
        }
    }
}
