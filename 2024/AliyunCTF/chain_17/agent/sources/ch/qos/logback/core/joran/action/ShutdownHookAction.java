package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ShutdownHookModel;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/ShutdownHookAction.class */
public class ShutdownHookAction extends BaseModelAction {
    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected boolean validPreconditions(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        return true;
    }

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        ShutdownHookModel shutdownHookModel = new ShutdownHookModel();
        String className = attributes.getValue("class");
        shutdownHookModel.setClassName(className);
        return shutdownHookModel;
    }
}
