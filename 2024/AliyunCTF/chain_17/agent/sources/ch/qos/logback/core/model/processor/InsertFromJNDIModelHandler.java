package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.model.InsertFromJNDIModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ModelUtil;
import ch.qos.logback.core.util.JNDIUtil;
import ch.qos.logback.core.util.OptionHelper;
import javax.naming.NamingException;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/InsertFromJNDIModelHandler.class */
public class InsertFromJNDIModelHandler extends ModelHandlerBase {
    public InsertFromJNDIModelHandler(Context context) {
        super(context);
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new InsertFromJNDIModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<InsertFromJNDIModel> getSupportedModelClass() {
        return InsertFromJNDIModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        int errorCount = 0;
        InsertFromJNDIModel ifjm = (InsertFromJNDIModel) model;
        String envEntryName = mic.subst(ifjm.getEnvEntryName());
        String asKey = mic.subst(ifjm.getAs());
        String scopeStr = mic.subst(ifjm.getScopeStr());
        ActionUtil.Scope scope = ActionUtil.stringToScope(scopeStr);
        if (OptionHelper.isNullOrEmpty(envEntryName)) {
            addError("[env-entry-name] missing");
            errorCount = 0 + 1;
        }
        if (OptionHelper.isNullOrEmpty(asKey)) {
            addError("[as] missing");
            errorCount++;
        }
        if (errorCount != 0) {
            return;
        }
        try {
            javax.naming.Context ctx = JNDIUtil.getInitialContext();
            String envEntryValue = JNDIUtil.lookupString(ctx, envEntryName);
            if (OptionHelper.isNullOrEmpty(envEntryValue)) {
                addError("[" + envEntryName + "] has null or empty value");
            } else {
                addInfo("Setting variable [" + asKey + "] to [" + envEntryValue + "] in [" + String.valueOf(scope) + "] scope");
                ModelUtil.setProperty(mic, asKey, envEntryValue, scope);
            }
        } catch (NamingException e) {
            addError("Failed to lookup JNDI env-entry [" + envEntryName + "]");
        }
    }
}
