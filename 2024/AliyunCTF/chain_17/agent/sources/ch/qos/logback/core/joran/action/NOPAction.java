package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/NOPAction.class */
public class NOPAction extends Action {
    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(SaxEventInterpretationContext ec, String name, Attributes attributes) {
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(SaxEventInterpretationContext ec, String name) {
    }
}
