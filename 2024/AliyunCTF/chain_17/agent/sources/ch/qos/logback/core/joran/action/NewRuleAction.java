package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/NewRuleAction.class */
public class NewRuleAction extends Action {
    boolean inError = false;

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(SaxEventInterpretationContext ec, String localName, Attributes attributes) {
        this.inError = false;
        String pattern = attributes.getValue("pattern");
        String actionClass = attributes.getValue("actionClass");
        if (OptionHelper.isNullOrEmpty(pattern)) {
            this.inError = true;
            addError("No 'pattern' attribute in <newRule>");
        } else {
            if (OptionHelper.isNullOrEmpty(actionClass)) {
                this.inError = true;
                addError("No 'actionClass' attribute in <newRule>");
                return;
            }
            try {
                addInfo("About to add new Joran parsing rule [" + pattern + "," + actionClass + "].");
                ec.getSaxEventInterpreter().getRuleStore().addRule(new ElementSelector(pattern), actionClass);
            } catch (Exception e) {
                this.inError = true;
                String errorMsg = "Could not add new Joran parsing rule [" + pattern + "," + actionClass + "]";
                addError(errorMsg);
            }
        }
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(SaxEventInterpretationContext ec, String n) {
    }

    public void finish(SaxEventInterpretationContext ec) {
    }
}
