package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.JoranConstants;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/ConversionRuleAction.class */
public class ConversionRuleAction extends Action {
    boolean inError = false;

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(SaxEventInterpretationContext ec, String localName, Attributes attributes) {
        this.inError = false;
        String conversionWord = attributes.getValue(JoranConstants.CONVERSION_WORD_ATTRIBUTE);
        String converterClass = attributes.getValue(JoranConstants.CONVERTER_CLASS_ATTRIBUTE);
        if (OptionHelper.isNullOrEmpty(conversionWord)) {
            this.inError = true;
            addError("No 'conversionWord' attribute in <conversionRule>");
            return;
        }
        if (OptionHelper.isNullOrEmpty(converterClass)) {
            this.inError = true;
            ec.addError("No 'converterClass' attribute in <conversionRule>");
            return;
        }
        try {
            Map<String, String> ruleRegistry = (Map) this.context.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
            if (ruleRegistry == null) {
                ruleRegistry = new HashMap<>();
                this.context.putObject(CoreConstants.PATTERN_RULE_REGISTRY, ruleRegistry);
            }
            addInfo("registering conversion word " + conversionWord + " with class [" + converterClass + "]");
            ruleRegistry.put(conversionWord, converterClass);
        } catch (Exception e) {
            this.inError = true;
            addError("Could not add conversion rule to PatternLayout.");
        }
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(SaxEventInterpretationContext ec, String n) {
    }

    public void finish(SaxEventInterpretationContext ec) {
    }
}
