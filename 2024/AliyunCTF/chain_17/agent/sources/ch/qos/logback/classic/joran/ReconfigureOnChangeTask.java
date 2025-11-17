package ch.qos.logback.classic.joran;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.spi.ConfigurationWatchList;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ModelUtil;
import ch.qos.logback.core.spi.ConfigurationEvent;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.StatusUtil;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/joran/ReconfigureOnChangeTask.class */
public class ReconfigureOnChangeTask extends ContextAwareBase implements Runnable {
    public static final String DETECTED_CHANGE_IN_CONFIGURATION_FILES = "Detected change in configuration files.";
    static final String RE_REGISTERING_PREVIOUS_SAFE_CONFIGURATION = "Re-registering previous fallback configuration once more as a fallback configuration point";
    static final String FALLING_BACK_TO_SAFE_CONFIGURATION = "Given previous errors, falling back to previously registered safe configuration.";
    long birthdate = System.currentTimeMillis();
    List<ReconfigureOnChangeTaskListener> listeners = null;
    ScheduledFuture<?> scheduledFuture;

    @Override // java.lang.Runnable
    public void run() {
        this.context.fireConfigurationEvent(ConfigurationEvent.newConfigurationChangeDetectorRunningEvent(this));
        ConfigurationWatchList configurationWatchList = ConfigurationWatchListUtil.getConfigurationWatchList(this.context);
        if (configurationWatchList == null) {
            addWarn("Empty ConfigurationWatchList in context");
            return;
        }
        List<File> filesToWatch = configurationWatchList.getCopyOfFileWatchList();
        if (filesToWatch == null || filesToWatch.isEmpty()) {
            addInfo("Empty watch file list. Disabling ");
            return;
        }
        if (!configurationWatchList.changeDetected()) {
            return;
        }
        this.context.fireConfigurationEvent(ConfigurationEvent.newConfigurationChangeDetectedEvent(this));
        cancelFutureInvocationsOfThisTaskInstance();
        URL mainConfigurationURL = configurationWatchList.getMainURL();
        addInfo(DETECTED_CHANGE_IN_CONFIGURATION_FILES);
        addInfo("Will reset and reconfigure context named [" + this.context.getName() + "]");
        LoggerContext lc = (LoggerContext) this.context;
        if (mainConfigurationURL.toString().endsWith("xml")) {
            performXMLConfiguration(lc, mainConfigurationURL);
        } else if (mainConfigurationURL.toString().endsWith("groovy")) {
            addError("Groovy configuration disabled due to Java 9 compilation issues.");
        }
    }

    private void cancelFutureInvocationsOfThisTaskInstance() {
        boolean result = this.scheduledFuture.cancel(false);
        if (!result) {
            addWarn("could not cancel " + toString());
        }
    }

    private void performXMLConfiguration(LoggerContext lc, URL mainConfigurationURL) {
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(this.context);
        StatusUtil statusUtil = new StatusUtil(this.context);
        Model failsafeTop = jc.recallSafeConfiguration();
        URL mainURL = ConfigurationWatchListUtil.getMainWatchURL(this.context);
        lc.reset();
        long threshold = System.currentTimeMillis();
        try {
            jc.doConfigure(mainConfigurationURL);
            if (statusUtil.hasXMLParsingErrors(threshold)) {
                fallbackConfiguration(lc, failsafeTop, mainURL);
            }
        } catch (JoranException e) {
            addWarn("Exception occurred during reconfiguration", e);
            fallbackConfiguration(lc, failsafeTop, mainURL);
        }
    }

    private void fallbackConfiguration(LoggerContext lc, Model failsafeTop, URL mainURL) {
        JoranConfigurator joranConfigurator = new JoranConfigurator();
        joranConfigurator.setContext(this.context);
        ConfigurationWatchList oldCWL = ConfigurationWatchListUtil.getConfigurationWatchList(this.context);
        ConfigurationWatchList newCWL = oldCWL.buildClone();
        if (failsafeTop == null) {
            addWarn("No previous configuration to fall back on.");
            return;
        }
        addWarn(FALLING_BACK_TO_SAFE_CONFIGURATION);
        addInfo("Safe model " + String.valueOf(failsafeTop));
        try {
            lc.reset();
            ConfigurationWatchListUtil.registerConfigurationWatchList(this.context, newCWL);
            ModelUtil.resetForReuse(failsafeTop);
            joranConfigurator.processModel(failsafeTop);
            addInfo(RE_REGISTERING_PREVIOUS_SAFE_CONFIGURATION);
            joranConfigurator.registerSafeConfiguration(failsafeTop);
            this.context.fireConfigurationEvent(ConfigurationEvent.newConfigurationEndedEvent(this));
            addInfo("after registerSafeConfiguration");
        } catch (Exception e) {
            addError("Unexpected exception thrown by a configuration considered safe.", e);
        }
    }

    public String toString() {
        return "ReconfigureOnChangeTask(born:" + this.birthdate + ")";
    }

    public void setScheduredFuture(ScheduledFuture<?> aScheduledFuture) {
        this.scheduledFuture = aScheduledFuture;
    }
}
