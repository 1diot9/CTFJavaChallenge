package ch.qos.logback.classic.joran;

import ch.qos.logback.classic.model.ConfigurationModel;
import ch.qos.logback.classic.model.ContextNameModel;
import ch.qos.logback.classic.model.LevelModel;
import ch.qos.logback.classic.model.LoggerContextListenerModel;
import ch.qos.logback.classic.model.LoggerModel;
import ch.qos.logback.classic.model.RootLoggerModel;
import ch.qos.logback.classic.model.processor.ConfigurationModelHandler;
import ch.qos.logback.classic.model.processor.ContextNameModelHandler;
import ch.qos.logback.classic.model.processor.LevelModelHandler;
import ch.qos.logback.classic.model.processor.LoggerContextListenerModelHandler;
import ch.qos.logback.classic.model.processor.LoggerModelHandler;
import ch.qos.logback.classic.model.processor.RootLoggerModelHandler;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.ModelClassToModelHandlerLinkerBase;
import ch.qos.logback.core.model.AppenderModel;
import ch.qos.logback.core.model.AppenderRefModel;
import ch.qos.logback.core.model.InsertFromJNDIModel;
import ch.qos.logback.core.model.ModelHandlerFactoryMethod;
import ch.qos.logback.core.model.processor.AppenderModelHandler;
import ch.qos.logback.core.model.processor.AppenderRefDependencyAnalyser;
import ch.qos.logback.core.model.processor.AppenderRefModelHandler;
import ch.qos.logback.core.model.processor.DefaultProcessor;
import ch.qos.logback.core.model.processor.InsertFromJNDIModelHandler;
import ch.qos.logback.core.model.processor.RefContainerDependencyAnalyser;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/joran/ModelClassToModelHandlerLinker.class */
public class ModelClassToModelHandlerLinker extends ModelClassToModelHandlerLinkerBase {
    ModelHandlerFactoryMethod configurationModelHandlerFactoryMethod;

    public ModelClassToModelHandlerLinker(Context context) {
        super(context);
    }

    @Override // ch.qos.logback.core.joran.ModelClassToModelHandlerLinkerBase
    public void link(DefaultProcessor defaultProcessor) {
        super.link(defaultProcessor);
        defaultProcessor.addHandler(ConfigurationModel.class, getConfigurationModelHandlerFactoryMethod());
        defaultProcessor.addHandler(ContextNameModel.class, ContextNameModelHandler::makeInstance);
        defaultProcessor.addHandler(LoggerContextListenerModel.class, LoggerContextListenerModelHandler::makeInstance);
        defaultProcessor.addHandler(InsertFromJNDIModel.class, InsertFromJNDIModelHandler::makeInstance);
        defaultProcessor.addHandler(AppenderModel.class, AppenderModelHandler::makeInstance);
        defaultProcessor.addHandler(AppenderRefModel.class, AppenderRefModelHandler::makeInstance);
        defaultProcessor.addHandler(RootLoggerModel.class, RootLoggerModelHandler::makeInstance);
        defaultProcessor.addHandler(LoggerModel.class, LoggerModelHandler::makeInstance);
        defaultProcessor.addHandler(LevelModel.class, LevelModelHandler::makeInstance);
        defaultProcessor.addAnalyser(LoggerModel.class, () -> {
            return new RefContainerDependencyAnalyser(this.context, LoggerModel.class);
        });
        defaultProcessor.addAnalyser(RootLoggerModel.class, () -> {
            return new RefContainerDependencyAnalyser(this.context, RootLoggerModel.class);
        });
        defaultProcessor.addAnalyser(AppenderModel.class, () -> {
            return new RefContainerDependencyAnalyser(this.context, AppenderModel.class);
        });
        defaultProcessor.addAnalyser(AppenderRefModel.class, () -> {
            return new AppenderRefDependencyAnalyser(this.context);
        });
        sealModelFilters(defaultProcessor);
    }

    public ModelHandlerFactoryMethod getConfigurationModelHandlerFactoryMethod() {
        if (this.configurationModelHandlerFactoryMethod == null) {
            return ConfigurationModelHandler::makeInstance;
        }
        return this.configurationModelHandlerFactoryMethod;
    }

    public void setConfigurationModelHandlerFactoryMethod(ModelHandlerFactoryMethod cmhfm) {
        this.configurationModelHandlerFactoryMethod = cmhfm;
    }
}
