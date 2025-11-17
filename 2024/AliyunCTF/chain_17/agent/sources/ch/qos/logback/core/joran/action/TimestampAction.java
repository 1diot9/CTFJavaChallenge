package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.TimestampModel;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/TimestampAction.class */
public class TimestampAction extends BaseModelAction {
    public static final String DATE_PATTERN_ATTRIBUTE = "datePattern";
    public static final String TIME_REFERENCE_ATTRIBUTE = "timeReference";

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected boolean validPreconditions(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        boolean valid = true;
        String keyStr = attributes.getValue("key");
        if (OptionHelper.isNullOrEmpty(keyStr)) {
            addError("Attribute named [key] cannot be empty");
            valid = false;
        }
        String datePatternStr = attributes.getValue(DATE_PATTERN_ATTRIBUTE);
        if (OptionHelper.isNullOrEmpty(datePatternStr)) {
            addError("Attribute named [datePattern] cannot be empty");
            valid = false;
        }
        return valid;
    }

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        TimestampModel timestampModel = new TimestampModel();
        timestampModel.setKey(attributes.getValue("key"));
        timestampModel.setDatePattern(attributes.getValue(DATE_PATTERN_ATTRIBUTE));
        timestampModel.setTimeReference(attributes.getValue(TIME_REFERENCE_ATTRIBUTE));
        timestampModel.setScopeStr(attributes.getValue("scope"));
        return timestampModel;
    }
}
