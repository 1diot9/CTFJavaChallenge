package ch.qos.logback.classic.model.processor;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.model.LevelModel;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.JoranConstants;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.spi.ErrorCodes;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/model/processor/LevelModelHandler.class */
public class LevelModelHandler extends ModelHandlerBase {
    boolean inError;

    public LevelModelHandler(Context context) {
        super(context);
        this.inError = false;
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new LevelModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<? extends LevelModel> getSupportedModelClass() {
        return LevelModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        Object o = mic.peekObject();
        if (!(o instanceof Logger)) {
            this.inError = true;
            addError("For element <level>, could not find a logger at the top of execution stack.");
            return;
        }
        Logger l = (Logger) o;
        String loggerName = l.getName();
        LevelModel levelModel = (LevelModel) model;
        String levelStr = mic.subst(levelModel.getValue());
        if (JoranConstants.INHERITED.equalsIgnoreCase(levelStr) || JoranConstants.NULL.equalsIgnoreCase(levelStr)) {
            if ("ROOT".equalsIgnoreCase(loggerName)) {
                addError(ErrorCodes.ROOT_LEVEL_CANNOT_BE_SET_TO_NULL);
            } else {
                l.setLevel(null);
            }
        } else {
            l.setLevel(Level.toLevel(levelStr, Level.DEBUG));
        }
        addInfo(loggerName + " level set to " + String.valueOf(l.getLevel()));
    }
}
