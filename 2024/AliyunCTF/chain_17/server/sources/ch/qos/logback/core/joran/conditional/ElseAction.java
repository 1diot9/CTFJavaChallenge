package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.action.BaseModelAction;
import ch.qos.logback.core.joran.action.PreconditionValidator;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.conditional.ElseModel;
import org.xml.sax.Attributes;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/conditional/ElseAction.class */
public class ElseAction extends BaseModelAction {
    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected boolean validPreconditions(SaxEventInterpretationContext interpcont, String name, Attributes attributes) {
        PreconditionValidator pv = new PreconditionValidator(this, interpcont, name, attributes);
        pv.validateZeroAttributes();
        return pv.isValid();
    }

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        ElseModel elseModel = new ElseModel();
        return elseModel;
    }
}
