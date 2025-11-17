package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.AppenderRefModel;
import ch.qos.logback.core.model.Model;
import org.xml.sax.Attributes;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/AppenderRefAction.class */
public class AppenderRefAction extends BaseModelAction {
    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected boolean validPreconditions(SaxEventInterpretationContext intercon, String name, Attributes attributes) {
        PreconditionValidator pv = new PreconditionValidator(this, intercon, name, attributes);
        pv.validateRefAttribute();
        return pv.isValid();
    }

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        AppenderRefModel arm = new AppenderRefModel();
        String ref = attributes.getValue("ref");
        arm.setRef(ref);
        return arm;
    }
}
