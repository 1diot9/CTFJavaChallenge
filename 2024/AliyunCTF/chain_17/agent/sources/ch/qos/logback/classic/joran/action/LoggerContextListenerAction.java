package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.model.LoggerContextListenerModel;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.joran.action.BaseModelAction;
import ch.qos.logback.core.joran.action.PreconditionValidator;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/joran/action/LoggerContextListenerAction.class */
public class LoggerContextListenerAction extends BaseModelAction {
    boolean inError = false;
    LoggerContextListener lcl;

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected boolean validPreconditions(SaxEventInterpretationContext ic, String name, Attributes attributes) {
        PreconditionValidator pv = new PreconditionValidator(this, ic, name, attributes);
        pv.validateClassAttribute();
        return pv.isValid();
    }

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        LoggerContextListenerModel loggerContextListenerModel = new LoggerContextListenerModel();
        loggerContextListenerModel.setClassName(attributes.getValue("class"));
        return loggerContextListenerModel;
    }
}
