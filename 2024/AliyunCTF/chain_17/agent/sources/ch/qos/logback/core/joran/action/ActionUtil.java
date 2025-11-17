package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.util.OptionHelper;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/ActionUtil.class */
public class ActionUtil {

    /* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/ActionUtil$Scope.class */
    public enum Scope {
        LOCAL,
        CONTEXT,
        SYSTEM
    }

    public static Scope stringToScope(String scopeStr) {
        if (Scope.SYSTEM.toString().equalsIgnoreCase(scopeStr)) {
            return Scope.SYSTEM;
        }
        if (Scope.CONTEXT.toString().equalsIgnoreCase(scopeStr)) {
            return Scope.CONTEXT;
        }
        return Scope.LOCAL;
    }

    public static void setProperty(ModelInterpretationContext ic, String key, String value, Scope scope) {
        switch (scope) {
            case LOCAL:
                ic.addSubstitutionProperty(key, value);
                return;
            case CONTEXT:
                ic.getContext().putProperty(key, value);
                return;
            case SYSTEM:
                OptionHelper.setSystemProperty(ic, key, value);
                return;
            default:
                return;
        }
    }
}
