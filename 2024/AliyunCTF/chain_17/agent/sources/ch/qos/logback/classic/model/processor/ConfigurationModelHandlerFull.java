package ch.qos.logback.classic.model.processor;

import ch.qos.logback.classic.joran.ReconfigureOnChangeTask;
import ch.qos.logback.classic.model.ConfigurationModel;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.spi.ConfigurationEvent;
import ch.qos.logback.core.util.Duration;
import ch.qos.logback.core.util.OptionHelper;
import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/model/processor/ConfigurationModelHandlerFull.class */
public class ConfigurationModelHandlerFull extends ConfigurationModelHandler {
    public ConfigurationModelHandlerFull(Context context) {
        super(context);
    }

    public static ModelHandlerBase makeInstance2(Context context, ModelInterpretationContext mic) {
        return new ConfigurationModelHandlerFull(context);
    }

    @Override // ch.qos.logback.classic.model.processor.ConfigurationModelHandler
    protected void processScanAttrib(ModelInterpretationContext mic, ConfigurationModel configurationModel) {
        String scanStr = mic.subst(configurationModel.getScanStr());
        if (!OptionHelper.isNullOrEmpty(scanStr) && !"false".equalsIgnoreCase(scanStr)) {
            ScheduledExecutorService scheduledExecutorService = this.context.getScheduledExecutorService();
            URL mainURL = ConfigurationWatchListUtil.getMainWatchURL(this.context);
            if (mainURL == null) {
                addWarn("Due to missing top level configuration file, reconfiguration on change (configuration file scanning) cannot be done.");
                return;
            }
            ReconfigureOnChangeTask rocTask = new ReconfigureOnChangeTask();
            rocTask.setContext(this.context);
            addInfo("Registering a new ReconfigureOnChangeTask " + String.valueOf(rocTask));
            this.context.fireConfigurationEvent(ConfigurationEvent.newConfigurationChangeDetectorRegisteredEvent(rocTask));
            String scanPeriodStr = mic.subst(configurationModel.getScanPeriodStr());
            Duration duration = getDurationOfScanPeriodAttribute(scanPeriodStr, SCAN_PERIOD_DEFAULT);
            addInfo("Will scan for changes in [" + String.valueOf(mainURL) + "] ");
            addInfo("Setting ReconfigureOnChangeTask scanning period to " + String.valueOf(duration));
            ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(rocTask, duration.getMilliseconds(), duration.getMilliseconds(), TimeUnit.MILLISECONDS);
            rocTask.setScheduredFuture(scheduledFuture);
            this.context.addScheduledFuture(scheduledFuture);
        }
    }

    private Duration getDurationOfScanPeriodAttribute(String scanPeriodAttrib, Duration defaultDuration) {
        Duration duration = null;
        if (!OptionHelper.isNullOrEmpty(scanPeriodAttrib)) {
            try {
                duration = Duration.valueOf(scanPeriodAttrib);
            } catch (IllegalArgumentException | IllegalStateException e) {
                addWarn("Failed to parse 'scanPeriod' attribute [" + scanPeriodAttrib + "]", e);
            }
        }
        if (duration == null) {
            addInfo("No 'scanPeriod' specified. Defaulting to " + defaultDuration.toString());
            duration = defaultDuration;
        }
        return duration;
    }
}
