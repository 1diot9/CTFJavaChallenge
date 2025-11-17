package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import org.xml.sax.Attributes;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/ContextPropertyAction.class */
public class ContextPropertyAction extends Action {
    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(SaxEventInterpretationContext ec, String name, Attributes attributes) throws ActionException {
        addError("The [contextProperty] element has been removed. Please use [property] element instead");
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(SaxEventInterpretationContext ec, String name) throws ActionException {
    }
}
