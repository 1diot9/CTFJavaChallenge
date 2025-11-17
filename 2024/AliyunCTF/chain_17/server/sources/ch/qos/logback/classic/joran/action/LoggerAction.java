package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.model.LoggerModel;
import ch.qos.logback.core.joran.JoranConstants;
import ch.qos.logback.core.joran.action.BaseModelAction;
import ch.qos.logback.core.joran.action.PreconditionValidator;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import org.xml.sax.Attributes;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/joran/action/LoggerAction.class */
public class LoggerAction extends BaseModelAction {
    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected boolean validPreconditions(SaxEventInterpretationContext ic, String name, Attributes attributes) {
        PreconditionValidator validator = new PreconditionValidator(this, ic, name, attributes);
        validator.validateNameAttribute();
        return validator.isValid();
    }

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        LoggerModel loggerModel = new LoggerModel();
        String nameStr = attributes.getValue("name");
        loggerModel.setName(nameStr);
        String levelStr = attributes.getValue("level");
        loggerModel.setLevel(levelStr);
        String additivityStr = attributes.getValue(JoranConstants.ADDITIVITY_ATTRIBUTE);
        loggerModel.setAdditivity(additivityStr);
        return loggerModel;
    }
}
