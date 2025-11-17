package ch.qos.logback.classic.model.processor;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.model.ConfigurationModel;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ModelConstants;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.util.ContextUtil;
import ch.qos.logback.core.util.Duration;
import ch.qos.logback.core.util.OptionHelper;
import ch.qos.logback.core.util.StatusListenerConfigHelper;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/model/processor/ConfigurationModelHandler.class */
public class ConfigurationModelHandler extends ModelHandlerBase {
    static final Duration SCAN_PERIOD_DEFAULT = Duration.buildByMinutes(1.0d);

    public ConfigurationModelHandler(Context context) {
        super(context);
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext mic) {
        return new ConfigurationModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<ConfigurationModel> getSupportedModelClass() {
        return ConfigurationModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) {
        ConfigurationModel configurationModel = (ConfigurationModel) model;
        String debugAttrib = OptionHelper.getSystemProperty(ModelConstants.DEBUG_SYSTEM_PROPERTY_KEY, null);
        if (debugAttrib == null) {
            debugAttrib = mic.subst(configurationModel.getDebugStr());
        }
        if (!OptionHelper.isNullOrEmpty(debugAttrib) && !debugAttrib.equalsIgnoreCase(Boolean.FALSE.toString()) && !debugAttrib.equalsIgnoreCase("null")) {
            StatusListenerConfigHelper.addOnConsoleListenerInstance(this.context, new OnConsoleStatusListener());
        }
        processScanAttrib(mic, configurationModel);
        LoggerContext lc = (LoggerContext) this.context;
        boolean packagingData = OptionHelper.toBoolean(mic.subst(configurationModel.getPackagingDataStr()), false);
        lc.setPackagingDataEnabled(packagingData);
        ContextUtil contextUtil = new ContextUtil(this.context);
        contextUtil.addGroovyPackages(lc.getFrameworkPackages());
    }

    protected void processScanAttrib(ModelInterpretationContext mic, ConfigurationModel configurationModel) {
        String scanStr = mic.subst(configurationModel.getScanStr());
        if (!OptionHelper.isNullOrEmpty(scanStr) && !"false".equalsIgnoreCase(scanStr)) {
            addInfo("Skipping ReconfigureOnChangeTask registration");
        }
    }
}
