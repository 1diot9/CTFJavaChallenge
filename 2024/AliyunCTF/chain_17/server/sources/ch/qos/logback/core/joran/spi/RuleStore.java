package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.joran.action.Action;
import java.util.function.Supplier;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/spi/RuleStore.class */
public interface RuleStore {
    void addRule(ElementSelector elementSelector, String str) throws ClassNotFoundException;

    void addRule(ElementSelector elementSelector, Supplier<Action> supplier);

    Supplier<Action> matchActions(ElementPath elementPath);

    void addTransparentPathPart(String str);
}
