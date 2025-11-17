package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.model.ContextNameModel;
import ch.qos.logback.core.joran.action.BaseModelAction;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import org.xml.sax.Attributes;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/joran/action/ContextNameAction.class */
public class ContextNameAction extends BaseModelAction {
    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        ContextNameModel contextNameModel = new ContextNameModel();
        return contextNameModel;
    }
}
