package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.JoranConstants;
import ch.qos.logback.core.model.AppenderRefModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.AppenderAttachable;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/AppenderRefModelHandler.class */
public class AppenderRefModelHandler extends ModelHandlerBase {
    boolean inError;

    public AppenderRefModelHandler(Context context) {
        super(context);
        this.inError = false;
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new AppenderRefModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<? extends AppenderRefModel> getSupportedModelClass() {
        return AppenderRefModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext interpContext, Model model) throws ModelHandlerException {
        Object o = interpContext.peekObject();
        if (!(o instanceof AppenderAttachable)) {
            this.inError = true;
            String errMsg = "Could not find an AppenderAttachable at the top of execution stack. Near " + model.idString();
            addError(errMsg);
        } else {
            AppenderRefModel appenderRefModel = (AppenderRefModel) model;
            AppenderAttachable<?> appenderAttachable = (AppenderAttachable) o;
            attachRefencedAppenders(interpContext, appenderRefModel, appenderAttachable);
        }
    }

    void attachRefencedAppenders(ModelInterpretationContext mic, AppenderRefModel appenderRefModel, AppenderAttachable<?> appenderAttachable) {
        String appenderName = mic.subst(appenderRefModel.getRef());
        Map<String, Appender> appenderBag = (Map) mic.getObjectMap().get(JoranConstants.APPENDER_BAG);
        Appender appender = appenderBag.get(appenderName);
        if (appender == null) {
            addError("Failed to find appender named [" + appenderName + "]");
        } else {
            addInfo("Attaching appender named [" + appenderName + "] to " + String.valueOf(appenderAttachable));
            appenderAttachable.addAppender(appender);
        }
    }
}
