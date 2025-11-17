package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.TimestampModel;
import ch.qos.logback.core.util.CachingDateFormatter;
import ch.qos.logback.core.util.OptionHelper;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/TimestampModelHandler.class */
public class TimestampModelHandler extends ModelHandlerBase {
    boolean inError;

    public TimestampModelHandler(Context context) {
        super(context);
        this.inError = false;
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new TimestampModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<TimestampModel> getSupportedModelClass() {
        return TimestampModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext interpretationContext, Model model) {
        long timeReference;
        TimestampModel timestampModel = (TimestampModel) model;
        String keyStr = timestampModel.getKey();
        if (OptionHelper.isNullOrEmpty(keyStr)) {
            addError("Attribute named [key] cannot be empty");
            this.inError = true;
        }
        String datePatternStr = timestampModel.getDatePattern();
        if (OptionHelper.isNullOrEmpty(datePatternStr)) {
            addError("Attribute named [datePattern] cannot be empty");
            this.inError = true;
        }
        String timeReferenceStr = timestampModel.getTimeReference();
        if (TimestampModel.CONTEXT_BIRTH.equalsIgnoreCase(timeReferenceStr)) {
            addInfo("Using context birth as time reference.");
            timeReference = this.context.getBirthTime();
        } else {
            timeReference = System.currentTimeMillis();
            addInfo("Using current interpretation time, i.e. now, as time reference.");
        }
        if (this.inError) {
            return;
        }
        String scopeStr = timestampModel.getScopeStr();
        ActionUtil.Scope scope = ActionUtil.stringToScope(scopeStr);
        CachingDateFormatter sdf = new CachingDateFormatter(datePatternStr);
        String val = sdf.format(timeReference);
        addInfo("Adding property to the context with key=\"" + keyStr + "\" and value=\"" + val + "\" to the " + String.valueOf(scope) + " scope");
        ActionUtil.setProperty(interpretationContext, keyStr, val, scope);
    }
}
