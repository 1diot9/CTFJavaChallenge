package ch.qos.logback.classic.joran;

import ch.qos.logback.classic.joran.action.ConfigurationAction;
import ch.qos.logback.classic.joran.action.ConsolePluginAction;
import ch.qos.logback.classic.joran.action.ContextNameAction;
import ch.qos.logback.classic.joran.action.InsertFromJNDIAction;
import ch.qos.logback.classic.joran.action.LevelAction;
import ch.qos.logback.classic.joran.action.LoggerAction;
import ch.qos.logback.classic.joran.action.LoggerContextListenerAction;
import ch.qos.logback.classic.joran.action.ReceiverAction;
import ch.qos.logback.classic.joran.action.RootLoggerAction;
import ch.qos.logback.classic.joran.sanity.IfNestedWithinSecondPhaseElementSC;
import ch.qos.logback.classic.model.processor.ConfigurationModelHandlerFull;
import ch.qos.logback.classic.model.processor.LogbackClassicDefaultNestedComponentRules;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.JoranConfiguratorBase;
import ch.qos.logback.core.joran.action.AppenderRefAction;
import ch.qos.logback.core.joran.action.IncludeAction;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.DefaultProcessor;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/joran/JoranConfigurator.class */
public class JoranConfigurator extends JoranConfiguratorBase<ILoggingEvent> {
    @Override // ch.qos.logback.core.joran.JoranConfiguratorBase, ch.qos.logback.core.joran.GenericXMLConfigurator
    public void addElementSelectorAndActionAssociations(RuleStore rs) {
        super.addElementSelectorAndActionAssociations(rs);
        rs.addRule(new ElementSelector("configuration"), () -> {
            return new ConfigurationAction();
        });
        rs.addRule(new ElementSelector("configuration/contextName"), () -> {
            return new ContextNameAction();
        });
        rs.addRule(new ElementSelector("configuration/contextListener"), () -> {
            return new LoggerContextListenerAction();
        });
        rs.addRule(new ElementSelector("configuration/insertFromJNDI"), () -> {
            return new InsertFromJNDIAction();
        });
        rs.addRule(new ElementSelector("configuration/logger"), () -> {
            return new LoggerAction();
        });
        rs.addRule(new ElementSelector("configuration/logger/level"), () -> {
            return new LevelAction();
        });
        rs.addRule(new ElementSelector("configuration/root"), () -> {
            return new RootLoggerAction();
        });
        rs.addRule(new ElementSelector("configuration/root/level"), () -> {
            return new LevelAction();
        });
        rs.addRule(new ElementSelector("configuration/logger/appender-ref"), () -> {
            return new AppenderRefAction();
        });
        rs.addRule(new ElementSelector("configuration/root/appender-ref"), () -> {
            return new AppenderRefAction();
        });
        rs.addRule(new ElementSelector("configuration/include"), () -> {
            return new IncludeAction();
        });
        rs.addRule(new ElementSelector("configuration/consolePlugin"), () -> {
            return new ConsolePluginAction();
        });
        rs.addRule(new ElementSelector("configuration/receiver"), () -> {
            return new ReceiverAction();
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.joran.JoranConfiguratorBase, ch.qos.logback.core.joran.GenericXMLConfigurator
    public void sanityCheck(Model topModel) {
        super.sanityCheck(topModel);
        performCheck(new IfNestedWithinSecondPhaseElementSC(), topModel);
    }

    @Override // ch.qos.logback.core.joran.GenericXMLConfigurator
    protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
        LogbackClassicDefaultNestedComponentRules.addDefaultNestedComponentRegistryRules(registry);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.joran.JoranConfiguratorBase, ch.qos.logback.core.joran.GenericXMLConfigurator
    public void addModelHandlerAssociations(DefaultProcessor defaultProcessor) {
        ModelClassToModelHandlerLinker m = new ModelClassToModelHandlerLinker(this.context);
        m.setConfigurationModelHandlerFactoryMethod(ConfigurationModelHandlerFull::makeInstance2);
        m.link(defaultProcessor);
    }

    private void sealModelFilters(DefaultProcessor defaultProcessor) {
        defaultProcessor.getPhaseOneFilter().denyAll();
        defaultProcessor.getPhaseTwoFilter().allowAll();
    }
}
