package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.EventEvaluatorModel;
import ch.qos.logback.core.model.Model;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/EventEvaluatorAction.class */
public class EventEvaluatorAction extends BaseModelAction {
    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected boolean validPreconditions(SaxEventInterpretationContext intercon, String name, Attributes attributes) {
        PreconditionValidator pv = new PreconditionValidator(this, intercon, name, attributes);
        pv.validateNameAttribute();
        return pv.isValid();
    }

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        EventEvaluatorModel eem = new EventEvaluatorModel();
        eem.setClassName(attributes.getValue("class"));
        eem.setName(attributes.getValue("name"));
        return eem;
    }
}
