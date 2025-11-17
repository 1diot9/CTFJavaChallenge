package ch.qos.logback.core.joran;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.event.SaxEventRecorder;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.joran.spi.SaxEventInterpreter;
import ch.qos.logback.core.joran.spi.SimpleRuleStore;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.DefaultProcessor;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.spi.ConfigurationEvent;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.ErrorCodes;
import ch.qos.logback.core.status.StatusUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import org.xml.sax.InputSource;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/GenericXMLConfigurator.class */
public abstract class GenericXMLConfigurator extends ContextAwareBase {
    protected SaxEventInterpreter saxEventInterpreter;
    protected ModelInterpretationContext modelInterpretationContext;

    protected abstract void addElementSelectorAndActionAssociations(RuleStore ruleStore);

    protected abstract void setImplicitRuleSupplier(SaxEventInterpreter saxEventInterpreter);

    public ModelInterpretationContext getModelInterpretationContext() {
        return this.modelInterpretationContext;
    }

    public final void doConfigure(URL url) throws JoranException {
        InputStream in = null;
        try {
            try {
                informContextOfURLUsedForConfiguration(getContext(), url);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setUseCaches(false);
                in = urlConnection.getInputStream();
                doConfigure(in, url.toExternalForm());
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ioe) {
                        addError("Could not close input stream", ioe);
                        throw new JoranException("Could not close input stream", ioe);
                    }
                }
            } catch (IOException ioe2) {
                String errMsg = "Could not open URL [" + String.valueOf(url) + "].";
                addError(errMsg, ioe2);
                throw new JoranException(errMsg, ioe2);
            }
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe3) {
                    addError("Could not close input stream", ioe3);
                    throw new JoranException("Could not close input stream", ioe3);
                }
            }
            throw th;
        }
    }

    public final void doConfigure(String filename) throws JoranException {
        doConfigure(new File(filename));
    }

    public final void doConfigure(File file) throws JoranException {
        FileInputStream fis = null;
        try {
            try {
                URL url = file.toURI().toURL();
                informContextOfURLUsedForConfiguration(getContext(), url);
                fis = new FileInputStream(file);
                doConfigure(fis, url.toExternalForm());
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException ioe) {
                        String errMsg = "Could not close [" + file.getName() + "].";
                        addError(errMsg, ioe);
                        throw new JoranException(errMsg, ioe);
                    }
                }
            } catch (IOException ioe2) {
                String errMsg2 = "Could not open [" + file.getPath() + "].";
                addError(errMsg2, ioe2);
                throw new JoranException(errMsg2, ioe2);
            }
        } catch (Throwable th) {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ioe3) {
                    String errMsg3 = "Could not close [" + file.getName() + "].";
                    addError(errMsg3, ioe3);
                    throw new JoranException(errMsg3, ioe3);
                }
            }
            throw th;
        }
    }

    public static void informContextOfURLUsedForConfiguration(Context context, URL url) {
        ConfigurationWatchListUtil.setMainWatchURL(context, url);
    }

    public final void doConfigure(InputStream inputStream) throws JoranException {
        doConfigure(new InputSource(inputStream));
    }

    public final void doConfigure(InputStream inputStream, String systemId) throws JoranException {
        InputSource inputSource = new InputSource(inputStream);
        inputSource.setSystemId(systemId);
        doConfigure(inputSource);
    }

    protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
    }

    protected ElementPath initialElementPath() {
        return new ElementPath();
    }

    protected void buildSaxEventInterpreter(List<SaxEvent> saxEvents) {
        RuleStore rs = new SimpleRuleStore(this.context);
        addElementSelectorAndActionAssociations(rs);
        this.saxEventInterpreter = new SaxEventInterpreter(this.context, rs, initialElementPath(), saxEvents);
        SaxEventInterpretationContext interpretationContext = this.saxEventInterpreter.getSaxEventInterpretationContext();
        interpretationContext.setContext(this.context);
        setImplicitRuleSupplier(this.saxEventInterpreter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void buildModelInterpretationContext() {
        this.modelInterpretationContext = new ModelInterpretationContext(this.context);
        addDefaultNestedComponentRegistryRules(this.modelInterpretationContext.getDefaultNestedComponentRegistry());
    }

    public final void doConfigure(InputSource inputSource) throws JoranException {
        this.context.fireConfigurationEvent(ConfigurationEvent.newConfigurationStartedEvent(this));
        long threshold = System.currentTimeMillis();
        SaxEventRecorder recorder = populateSaxEventRecorder(inputSource);
        List<SaxEvent> saxEvents = recorder.getSaxEventList();
        if (saxEvents.isEmpty()) {
            addWarn("Empty sax event list");
            return;
        }
        Model top = buildModelFromSaxEventList(recorder.getSaxEventList());
        if (top == null) {
            addError(ErrorCodes.EMPTY_MODEL_STACK);
            return;
        }
        sanityCheck(top);
        processModel(top);
        StatusUtil statusUtil = new StatusUtil(this.context);
        if (statusUtil.noXMLParsingErrorsOccurred(threshold)) {
            addInfo("Registering current configuration as safe fallback point");
            registerSafeConfiguration(top);
        }
        this.context.fireConfigurationEvent(ConfigurationEvent.newConfigurationEndedEvent(this));
    }

    public SaxEventRecorder populateSaxEventRecorder(InputSource inputSource) throws JoranException {
        SaxEventRecorder recorder = new SaxEventRecorder(this.context);
        recorder.recordEvents(inputSource);
        return recorder;
    }

    public Model buildModelFromSaxEventList(List<SaxEvent> saxEvents) throws JoranException {
        buildSaxEventInterpreter(saxEvents);
        playSaxEvents();
        Model top = this.saxEventInterpreter.getSaxEventInterpretationContext().peekModel();
        return top;
    }

    private void playSaxEvents() throws JoranException {
        this.saxEventInterpreter.getEventPlayer().play();
    }

    public void processModel(Model model) {
        buildModelInterpretationContext();
        this.modelInterpretationContext.setTopModel(model);
        this.modelInterpretationContext.setConfiguratorHint(this);
        DefaultProcessor defaultProcessor = new DefaultProcessor(this.context, this.modelInterpretationContext);
        addModelHandlerAssociations(defaultProcessor);
        synchronized (this.context.getConfigurationLock()) {
            defaultProcessor.process(model);
        }
    }

    protected void sanityCheck(Model topModel) {
    }

    protected void addModelHandlerAssociations(DefaultProcessor defaultProcessor) {
    }

    public void registerSafeConfiguration(Model top) {
        this.context.putObject(CoreConstants.SAFE_JORAN_CONFIGURATION, top);
    }

    public Model recallSafeConfiguration() {
        return (Model) this.context.getObject(CoreConstants.SAFE_JORAN_CONFIGURATION);
    }
}
