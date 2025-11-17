package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.model.processor.DefaultProcessor;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/spi/NewRuleProvider.class */
public interface NewRuleProvider {
    void addPathActionAssociations(RuleStore ruleStore);

    void addModelHandlerAssociations(DefaultProcessor defaultProcessor);

    void addModelAnalyserAssociations(DefaultProcessor defaultProcessor);
}
