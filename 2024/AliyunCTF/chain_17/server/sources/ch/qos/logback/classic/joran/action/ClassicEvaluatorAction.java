package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.boolex.JaninoEventEvaluator;
import ch.qos.logback.core.joran.action.EventEvaluatorAction;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/joran/action/ClassicEvaluatorAction.class */
public class ClassicEvaluatorAction extends EventEvaluatorAction {
    protected String defaultClassName() {
        return JaninoEventEvaluator.class.getName();
    }
}
