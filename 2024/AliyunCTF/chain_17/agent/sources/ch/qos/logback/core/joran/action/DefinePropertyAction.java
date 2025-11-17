package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.DefineModel;
import ch.qos.logback.core.model.Model;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/DefinePropertyAction.class */
public class DefinePropertyAction extends BaseModelAction {
    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected boolean validPreconditions(SaxEventInterpretationContext ic, String name, Attributes attributes) {
        PreconditionValidator validator = new PreconditionValidator(this, ic, name, attributes);
        validator.validateClassAttribute();
        validator.validateNameAttribute();
        return validator.isValid();
    }

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        DefineModel defineModel = new DefineModel();
        defineModel.setClassName(attributes.getValue("class"));
        defineModel.setName(attributes.getValue("name"));
        defineModel.setScopeStr(attributes.getValue("scope"));
        return defineModel;
    }
}
