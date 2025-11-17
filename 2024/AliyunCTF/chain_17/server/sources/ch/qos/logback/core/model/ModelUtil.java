package ch.qos.logback.core.model;

import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.util.ContextUtil;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Properties;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/ModelUtil.class */
public class ModelUtil {
    public static void resetForReuse(Model model) {
        if (model == null) {
            return;
        }
        model.resetForReuse();
    }

    public static void setProperty(ModelInterpretationContext mic, String key, String value, ActionUtil.Scope scope) {
        switch (scope) {
            case LOCAL:
                mic.addSubstitutionProperty(key, value);
                return;
            case CONTEXT:
                mic.getContext().putProperty(key, value);
                return;
            case SYSTEM:
                OptionHelper.setSystemProperty(mic, key, value);
                return;
            default:
                return;
        }
    }

    public static void setProperties(ModelInterpretationContext ic, Properties props, ActionUtil.Scope scope) {
        switch (scope) {
            case LOCAL:
                ic.addSubstitutionProperties(props);
                return;
            case CONTEXT:
                ContextUtil cu = new ContextUtil(ic.getContext());
                cu.addProperties(props);
                return;
            case SYSTEM:
                OptionHelper.setSystemProperties(ic, props);
                return;
            default:
                return;
        }
    }
}
