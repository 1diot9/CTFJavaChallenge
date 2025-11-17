package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.net.SocketAppender;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/joran/action/ConsolePluginAction.class */
public class ConsolePluginAction extends Action {
    private static final String PORT_ATTR = "port";
    private static final Integer DEFAULT_PORT = 4321;

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(SaxEventInterpretationContext ec, String name, Attributes attributes) throws ActionException {
        Integer port;
        String portStr = attributes.getValue(PORT_ATTR);
        if (portStr == null) {
            port = DEFAULT_PORT;
        } else {
            try {
                port = Integer.valueOf(portStr);
            } catch (NumberFormatException e) {
                addError("Port " + portStr + " in ConsolePlugin config is not a correct number");
                addError("Abandoning configuration of ConsolePlugin.");
                return;
            }
        }
        LoggerContext lc = (LoggerContext) ec.getContext();
        SocketAppender appender = new SocketAppender();
        appender.setContext(lc);
        appender.setIncludeCallerData(true);
        appender.setRemoteHost("localhost");
        appender.setPort(port.intValue());
        appender.start();
        Logger root = lc.getLogger("ROOT");
        root.addAppender(appender);
        addInfo("Sending LoggingEvents to the plugin using port " + port);
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(SaxEventInterpretationContext ec, String name) throws ActionException {
    }
}
