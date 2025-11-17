package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.StatusListenerModel;
import ch.qos.logback.core.status.StatusListener;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/StatusListenerAction.class */
public class StatusListenerAction extends BaseModelAction {
    boolean inError = false;
    Boolean effectivelyAdded = null;
    StatusListener statusListener = null;

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected boolean validPreconditions(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        String className = attributes.getValue("class");
        if (OptionHelper.isNullOrEmpty(className)) {
            addError("Missing class name for statusListener. Near [" + name + "] line " + getLineNumber(interpretationContext));
            return false;
        }
        return true;
    }

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        StatusListenerModel statusListenerModel = new StatusListenerModel();
        statusListenerModel.setClassName(attributes.getValue("class"));
        return statusListenerModel;
    }
}
