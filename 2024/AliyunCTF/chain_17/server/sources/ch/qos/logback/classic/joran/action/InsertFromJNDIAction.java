package ch.qos.logback.classic.joran.action;

import ch.qos.logback.core.joran.action.BaseModelAction;
import ch.qos.logback.core.joran.action.PreconditionValidator;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.InsertFromJNDIModel;
import ch.qos.logback.core.model.Model;
import org.xml.sax.Attributes;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/joran/action/InsertFromJNDIAction.class */
public class InsertFromJNDIAction extends BaseModelAction {
    public static final String ENV_ENTRY_NAME_ATTR = "env-entry-name";
    public static final String AS_ATTR = "as";

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        InsertFromJNDIModel ifjm = new InsertFromJNDIModel();
        ifjm.setEnvEntryName(attributes.getValue("env-entry-name"));
        ifjm.setAs(attributes.getValue("as"));
        ifjm.setScopeStr(attributes.getValue("scope"));
        return ifjm;
    }

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected boolean validPreconditions(SaxEventInterpretationContext seic, String name, Attributes attributes) {
        PreconditionValidator validator = new PreconditionValidator(this, seic, name, attributes);
        validator.generic("env-entry-name");
        validator.generic("as");
        return validator.isValid();
    }
}
