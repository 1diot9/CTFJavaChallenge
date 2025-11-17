package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.action.BaseModelAction;
import ch.qos.logback.core.joran.action.PreconditionValidator;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.conditional.IfModel;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/conditional/IfAction.class */
public class IfAction extends BaseModelAction {
    public static final String CONDITION_ATTRIBUTE = "condition";

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected boolean validPreconditions(SaxEventInterpretationContext interpcont, String name, Attributes attributes) {
        PreconditionValidator pv = new PreconditionValidator(this, interpcont, name, attributes);
        pv.generic(CONDITION_ATTRIBUTE);
        return pv.isValid();
    }

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        IfModel ifModel = new IfModel();
        String condition = attributes.getValue(CONDITION_ATTRIBUTE);
        ifModel.setCondition(condition);
        return ifModel;
    }
}
