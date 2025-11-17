package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.joran.spi.SaxEventInterpreter;
import ch.qos.logback.core.spi.ContextAwareBase;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/Action.class */
public abstract class Action extends ContextAwareBase {
    public static final String NAME_ATTRIBUTE = "name";
    public static final String KEY_ATTRIBUTE = "key";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String FILE_ATTRIBUTE = "file";
    public static final String CLASS_ATTRIBUTE = "class";
    public static final String PATTERN_ATTRIBUTE = "pattern";
    public static final String SCOPE_ATTRIBUTE = "scope";
    public static final String ACTION_CLASS_ATTRIBUTE = "actionClass";

    public abstract void begin(SaxEventInterpretationContext saxEventInterpretationContext, String str, Attributes attributes) throws ActionException;

    public abstract void end(SaxEventInterpretationContext saxEventInterpretationContext, String str) throws ActionException;

    public void body(SaxEventInterpretationContext intercon, String body) throws ActionException {
    }

    public String toString() {
        return getClass().getName();
    }

    protected int getColumnNumber(SaxEventInterpretationContext intercon) {
        Locator locator;
        SaxEventInterpreter interpreter = intercon.getSaxEventInterpreter();
        if (interpreter != null && (locator = interpreter.getLocator()) != null) {
            return locator.getColumnNumber();
        }
        return -1;
    }

    public static int getLineNumber(SaxEventInterpretationContext intercon) {
        Locator locator;
        SaxEventInterpreter interpreter = intercon.getSaxEventInterpreter();
        if (interpreter != null && (locator = interpreter.getLocator()) != null) {
            return locator.getLineNumber();
        }
        return -1;
    }

    protected String getLineColStr(SaxEventInterpretationContext intercon) {
        return "line: " + getLineNumber(intercon) + ", column: " + getColumnNumber(intercon);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String atLine(SaxEventInterpretationContext intercon) {
        return "At line " + getLineNumber(intercon);
    }

    protected String nearLine(SaxEventInterpretationContext intercon) {
        return "Near line " + getLineNumber(intercon);
    }
}
