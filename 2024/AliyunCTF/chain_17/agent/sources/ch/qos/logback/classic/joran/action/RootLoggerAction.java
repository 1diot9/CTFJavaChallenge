package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.model.RootLoggerModel;
import ch.qos.logback.core.joran.JoranConstants;
import ch.qos.logback.core.joran.action.BaseModelAction;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.ErrorCodes;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/joran/action/RootLoggerAction.class */
public class RootLoggerAction extends BaseModelAction {
    Logger root;
    boolean inError = false;

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected boolean validPreconditions(SaxEventInterpretationContext interpcont, String name, Attributes attributes) {
        String levelStr = attributes.getValue("level");
        if (JoranConstants.NULL.equalsIgnoreCase(levelStr) || JoranConstants.INHERITED.equalsIgnoreCase(levelStr)) {
            addError(ErrorCodes.ROOT_LEVEL_CANNOT_BE_SET_TO_NULL);
            return false;
        }
        return true;
    }

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        RootLoggerModel rootLoggerModel = new RootLoggerModel();
        String levelStr = attributes.getValue("level");
        rootLoggerModel.setLevel(levelStr);
        return rootLoggerModel;
    }
}
