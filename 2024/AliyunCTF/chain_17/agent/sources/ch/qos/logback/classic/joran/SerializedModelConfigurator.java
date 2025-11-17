package ch.qos.logback.classic.joran;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.serializedModel.HardenedModelInputStream;
import ch.qos.logback.classic.model.processor.LogbackClassicDefaultNestedComponentRules;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ConfiguratorRank;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ModelUtil;
import ch.qos.logback.core.model.processor.DefaultProcessor;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@ConfiguratorRank(10)
/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/joran/SerializedModelConfigurator.class */
public class SerializedModelConfigurator extends ContextAwareBase implements Configurator {
    public static final String AUTOCONFIG_MODEL_FILE = "logback.scmo";
    public static final String TEST_AUTOCONFIG_MODEL_FILE = "logback-test.scmo";
    protected ModelInterpretationContext modelInterpretationContext;

    @Override // ch.qos.logback.classic.spi.Configurator
    public Configurator.ExecutionStatus configure(LoggerContext loggerContext) {
        URL url = performMultiStepModelFileSearch(true);
        if (url != null) {
            configureByResource(url);
            return Configurator.ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY;
        }
        return Configurator.ExecutionStatus.INVOKE_NEXT_IF_ANY;
    }

    private void configureByResource(URL url) {
        String urlString = url.toString();
        if (urlString.endsWith(CoreConstants.MODEL_CONFIG_FILE_EXTENSION)) {
            Model model = retrieveModel(url);
            if (model == null) {
                addWarn("Empty model. Abandoning.");
                return;
            }
            ModelUtil.resetForReuse(model);
            buildModelInterpretationContext(model);
            DefaultProcessor defaultProcessor = new DefaultProcessor(this.context, this.modelInterpretationContext);
            ModelClassToModelHandlerLinker mc2mhl = new ModelClassToModelHandlerLinker(this.context);
            mc2mhl.link(defaultProcessor);
            synchronized (this.context.getConfigurationLock()) {
                defaultProcessor.process(model);
            }
            return;
        }
        throw new LogbackException("Unexpected filename extension of file [" + url.toString() + "]. Should be .scmo");
    }

    private void buildModelInterpretationContext(Model topModel) {
        this.modelInterpretationContext = new ModelInterpretationContext(this.context, this);
        this.modelInterpretationContext.setTopModel(topModel);
        LogbackClassicDefaultNestedComponentRules.addDefaultNestedComponentRegistryRules(this.modelInterpretationContext.getDefaultNestedComponentRegistry());
        this.modelInterpretationContext.createAppenderBags();
    }

    private Model retrieveModel(URL url) {
        long start = System.currentTimeMillis();
        try {
            InputStream is = url.openStream();
            try {
                HardenedModelInputStream hmis = new HardenedModelInputStream(is);
                Model model = (Model) hmis.readObject();
                long diff = System.currentTimeMillis() - start;
                addInfo("Model at [" + String.valueOf(url) + "] read in " + diff + " milliseconds");
                if (is != null) {
                    is.close();
                }
                return model;
            } catch (Throwable th) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (IOException e) {
            addError("Failed to open " + String.valueOf(url), e);
            return null;
        } catch (ClassNotFoundException e2) {
            addError("Failed read model object in " + String.valueOf(url), e2);
            return null;
        }
    }

    private URL performMultiStepModelFileSearch(boolean updateState) {
        ClassLoader myClassLoader = Loader.getClassLoaderOfObject(this);
        URL url = findModelConfigFileURLFromSystemProperties(myClassLoader);
        if (url != null) {
            return url;
        }
        URL url2 = getResource(TEST_AUTOCONFIG_MODEL_FILE, myClassLoader, updateState);
        if (url2 != null) {
            return url2;
        }
        return getResource(AUTOCONFIG_MODEL_FILE, myClassLoader, updateState);
    }

    URL findModelConfigFileURLFromSystemProperties(ClassLoader classLoader) {
        String logbackModelFile = OptionHelper.getSystemProperty(ClassicConstants.MODEL_CONFIG_FILE_PROPERTY);
        if (logbackModelFile == null) {
            return null;
        }
        URL result = null;
        try {
            try {
                result = new URL(logbackModelFile);
                statusOnResourceSearch(logbackModelFile, result);
                return result;
            } catch (MalformedURLException e) {
                URL result2 = Loader.getResource(logbackModelFile, classLoader);
                if (result2 != null) {
                    statusOnResourceSearch(logbackModelFile, result2);
                    return result2;
                }
                File f = new File(logbackModelFile);
                if (f.exists() && f.isFile()) {
                    try {
                        result2 = f.toURI().toURL();
                        statusOnResourceSearch(logbackModelFile, result2);
                        return result2;
                    } catch (MalformedURLException e2) {
                        statusOnResourceSearch(logbackModelFile, result2);
                        return null;
                    }
                }
                statusOnResourceSearch(logbackModelFile, result2);
                return null;
            }
        } catch (Throwable th) {
            statusOnResourceSearch(logbackModelFile, result);
            throw th;
        }
    }

    private URL getResource(String filename, ClassLoader classLoader, boolean updateStatus) {
        URL url = Loader.getResource(filename, classLoader);
        if (updateStatus) {
            statusOnResourceSearch(filename, url);
        }
        return url;
    }

    private void statusOnResourceSearch(String resourceName, URL url) {
        StatusManager sm = this.context.getStatusManager();
        if (url == null) {
            sm.add(new InfoStatus("Could NOT find resource [" + resourceName + "]", this.context));
        } else {
            sm.add(new InfoStatus("Found resource [" + resourceName + "] at [" + url.toString() + "]", this.context));
        }
    }
}
