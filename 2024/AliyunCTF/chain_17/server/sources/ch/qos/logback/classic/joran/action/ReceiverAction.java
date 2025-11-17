package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.model.ReceiverModel;
import ch.qos.logback.core.joran.action.BaseModelAction;
import ch.qos.logback.core.joran.action.PreconditionValidator;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import org.xml.sax.Attributes;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/joran/action/ReceiverAction.class */
public class ReceiverAction extends BaseModelAction {
    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        ReceiverModel rm = new ReceiverModel();
        rm.setClassName(attributes.getValue("class"));
        return rm;
    }

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected boolean validPreconditions(SaxEventInterpretationContext seic, String name, Attributes attributes) {
        PreconditionValidator validator = new PreconditionValidator(this, seic, name, attributes);
        validator.validateClassAttribute();
        return validator.isValid();
    }
}
