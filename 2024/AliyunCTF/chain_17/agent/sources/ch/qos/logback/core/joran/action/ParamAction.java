package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ParamModel;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/ParamAction.class */
public class ParamAction extends BaseModelAction {
    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected boolean validPreconditions(SaxEventInterpretationContext intercon, String name, Attributes attributes) {
        PreconditionValidator pv = new PreconditionValidator(this, intercon, name, attributes);
        pv.validateNameAttribute();
        pv.validateValueAttribute();
        addWarn("<param> element is deprecated in favor of a more direct syntax." + atLine(intercon));
        addWarn("For details see http://logback.qos.ch/codes.html#param");
        return pv.isValid();
    }

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        ParamModel paramModel = new ParamModel();
        paramModel.setName(attributes.getValue("name"));
        paramModel.setValue(attributes.getValue("value"));
        return paramModel;
    }
}
